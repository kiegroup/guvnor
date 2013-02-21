/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.services.backend.inbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.services.inbox.AssetEditedEvent;
import org.kie.guvnor.services.inbox.AssetOpenedEvent;
import org.kie.guvnor.services.inbox.InboxService;
import org.kie.guvnor.services.inbox.model.InboxPageRequest;
import org.kie.guvnor.services.inbox.model.InboxPageRow;
import org.uberfire.client.workbench.services.UserServices;
import org.uberfire.security.Identity;

/**
 *
 */
@Service
@ApplicationScoped
public class InboxServiceImpl
        implements InboxService {

    static final int MAX_RECENT_EDITED = 200;

    public static final  String RECENT_EDITED_ID = "recentEdited";
    public static final  String RECENT_VIEWED_ID = "recentViewed";
    public static final  String INCOMING_ID      = "incoming";
    private static final String INBOX            = "inbox";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private UserServices userServices;

    @Inject
    @SessionScoped
    private Identity identity;

    @Inject
    MailboxService mailboxService;

    public PageResponse<InboxPageRow> loadInbox( InboxPageRequest request ) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        String inboxName = request.getInboxName();
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();

        List<InboxEntry> entries = loadEntries( inboxName );
        Iterator<InboxEntry> iterator = entries.iterator();
        List<InboxPageRow> rowList = new InboxPageRowBuilder()
                .withPageRequest( request )
                .withIdentity( identity )
                .withContent( iterator )
                .build();

        response = new PageResponseBuilder<InboxPageRow>()
                .withStartRowIndex( request.getStartRowIndex() )
                .withTotalRowSize( entries.size() )
                .withTotalRowSizeExact()
                .withPageRowList( rowList )
                .withLastPage( !iterator.hasNext() )
                .build();
/*        
        List<InboxPageRow> inboxRowList = new ArrayList<InboxPageRow>();
        response.setPageRowList(inboxRowList);
        response.setStartRowIndex(request.getStartRowIndex());
        response.setTotalRowSize(0);
        response.setTotalRowSizeExact(true);*/

        return response;
    }

    public List<InboxEntry> loadEntries( final String inboxName ) {
        List<InboxEntry> entries;
        if ( inboxName.equals( RECENT_VIEWED_ID ) ) {
            entries = loadRecentOpened();
        } else if ( inboxName.equals( RECENT_EDITED_ID ) ) {
            entries = loadRecentEdited();
        } else {
            entries = loadIncoming();

        }
        return entries;
    }

    public List<InboxEntry> loadRecentEdited() {
        return readEntries( identity.getName(), RECENT_EDITED_ID );
    }

    public List<InboxEntry> loadRecentEdited( String userName ) {
        return readEntries( userName, RECENT_EDITED_ID );
    }

    public List<InboxEntry> loadRecentOpened() {
        return readEntries( identity.getName(), RECENT_VIEWED_ID );
    }

    public List<InboxEntry> loadIncoming() {
        return readEntries( identity.getName(), INCOMING_ID );
    }

    public List<InboxEntry> loadIncoming( String userName ) {
        return readEntries( userName, INCOMING_ID );
    }

    public List<InboxEntry> readEntries( String userName,
                                         String boxName ) {
        Path path = userServices.buildPath( INBOX, boxName );

        if ( ioService.exists( path ) ) {
            final String xml = ioService.readAllString( path );
            if ( !( xml == null || xml.equals( "" ) ) ) {
                return (List<InboxEntry>) getXStream().fromXML( xml );
            } else {
                return new ArrayList<InboxEntry>();
            }
        }

        return new ArrayList<InboxEntry>();
    }

    public void recordOpeningEvent( @Observes final AssetOpenedEvent event ) {
        PortablePreconditions.checkNotNull( "event", event );
        final org.uberfire.backend.vfs.Path resourcePath = event.getResourcePath();
        recordOpeningEvent( resourcePath.toURI(), resourcePath.getFileName().toString() );
    }

    /**
     * Helper method to log the opening. Will remove any inbox items that have
     * the same id.
     */
    public synchronized void recordOpeningEvent( String itemPath,
                                                 String itemName ) {
        addToRecentOpened( itemPath, itemName );
        List<InboxEntry> unreadIncoming = removeAnyExisting( itemPath,
                                                             loadIncoming() );
        writeEntries( INCOMING_ID, unreadIncoming );
    }

    public void recordUserEditEvent( @Observes final AssetEditedEvent event ) {
        PortablePreconditions.checkNotNull( "event", event );
        final org.uberfire.backend.vfs.Path resourcePath = event.getResourcePath();
        recordUserEditEvent( resourcePath.toURI(), resourcePath.getFileName().toString() );
    }

    /**
     * Helper method to note the event
     */
    @Override
    public synchronized void recordUserEditEvent( String itemPath,
                                                  String itemName ) {
        addToRecentEdited( itemPath, itemName );

        //deliver messages to users inboxes (ie., the edited item is the itme that the current logged in user has edited in the past, or commented on)
        addToIncoming( itemPath, itemName, identity.getName(), MailboxService.MAIL_MAN );
        mailboxService.processOutgoing();
        mailboxService.wakeUp();
    }

    /**
     * This should be called when the user edits or comments on an asset. Simply
     * adds to the list...
     */
    public void addToRecentEdited( String itemPath,
                                   String note ) {
        addToInbox( RECENT_EDITED_ID,
                    itemPath,
                    note,
                    identity.getName(),
                    identity.getName() );
    }

    public void addToRecentOpened( String itemPath,
                                   String note ) {
        addToInbox( RECENT_VIEWED_ID,
                    itemPath,
                    note,
                    identity.getName(),
                    identity.getName() );
    }

    public void addToIncoming( String itemPath,
                               String note,
                               String userFrom,
                               String userName ) {
        addToInbox( INCOMING_ID,
                    itemPath,
                    note,
                    userFrom,
                    userName );
    }

    private void addToInbox( String boxName,
                             String itemPath,
                             String note,
                             String userFrom,
                             String userName ) {
        assert boxName.equals( RECENT_EDITED_ID ) || boxName.equals( RECENT_VIEWED_ID ) || boxName.equals( INCOMING_ID );
        List<InboxEntry> entries = removeAnyExisting( itemPath,
                                                      readEntries( userName, boxName ) );

        if ( entries.size() >= MAX_RECENT_EDITED ) {
            entries.remove( 0 );
            entries.add( new InboxEntry( itemPath,
                                         note,
                                         userFrom ) );
        } else {
            entries.add( new InboxEntry( itemPath,
                                         note,
                                         userFrom ) );
        }

        writeEntries( boxName, entries );
    }

    private List<InboxEntry> removeAnyExisting( String itemPath,
                                                List<InboxEntry> inboxEntries ) {
        Iterator<InboxEntry> it = inboxEntries.iterator();
        while ( it.hasNext() ) {
            InboxEntry e = it.next();
            if ( e.itemPath.equals( itemPath ) ) {
                it.remove();
                return inboxEntries;
            }
        }
        return inboxEntries;
    }

    public void writeEntries( String boxName,
                              List<InboxEntry> entries ) {
        Path path = userServices.buildPath( INBOX, boxName );

        System.out.println( "writeEntries: " + path.toString() );
        String entry = getXStream().toXML( entries );
        System.out.println( "writeEntries: " + entry );

        ioService.write( path, entry );

    }

    private XStream getXStream() {
        XStream xs = new XStream();
        xs.alias( "inbox-entries", List.class );
        xs.alias( "entry", InboxEntry.class );
        return xs;
    }

    /**
     * And entry in an inbox.
     */
    public static class InboxEntry {

        public String from;

        public InboxEntry() {
        }

        public InboxEntry( String itemPath,
                           String note,
                           String userFrom ) {
            this.itemPath = itemPath;
            this.note = note;
            this.timestamp = System.currentTimeMillis();
            this.from = userFrom;
        }

        public String itemPath;
        public String note;
        public long   timestamp;
    }

}
