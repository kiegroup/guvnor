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
package org.guvnor.inbox.backend.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class InboxBackendImpl implements InboxBackend {

    static final int MAX_RECENT_EDITED = 200;

    private static final String RECENT_EDITED_ID = "recentEdited";
    private static final String RECENT_VIEWED_ID = "recentViewed";
    private static final String INCOMING_ID = "incoming";
    private static final String INBOX = "inbox";

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private MailboxService mailboxService;

    @Override
    public List<InboxEntry> loadRecentEdited( String userName ) {
        return readEntries( userName, RECENT_EDITED_ID );
    }

    @Override
    public List<InboxEntry> loadIncoming( String userName ) {
        return readEntries( userName, INCOMING_ID );
    }

    @Override
    public List<InboxEntry> readEntries( String userName,
                                         String boxName ) {
        Path path = userServicesBackend.buildPath( userName, INBOX, boxName );

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

    @Override
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

    public void recordOpeningEvent( @Observes final ResourceOpenedEvent event ) {
        checkNotNull( "event", event );
        final org.uberfire.backend.vfs.Path resourcePath = event.getPath();
        recordOpeningEvent( resourcePath.toURI(), resourcePath.getFileName().toString(), event.getSessionInfo().getIdentity().getIdentifier() );
    }

    public void recordUserEditEvent( @Observes final ResourceUpdatedEvent event ) {
        checkNotNull( "event", event );
        recordUserEditEvent( event.getPath().toURI(), event.getPath().getFileName(), event.getSessionInfo().getIdentity().getIdentifier() );
    }

    /**
     * Helper method to log the opening. Will remove any inbox items that have
     * the same id.
     */
    private synchronized void recordOpeningEvent( final String itemPath,
                                                  final String itemName,
                                                  final String userName ) {
        addToRecentOpened( itemPath, itemName, userName );
        List<InboxEntry> unreadIncoming = removeAnyExisting( itemPath,
                                                             loadIncoming( userName ) );
        writeEntries( userName, INCOMING_ID, unreadIncoming );
    }

    /**
     * Helper method to note the event
     */
    //@Override
    private synchronized void recordUserEditEvent( final String itemPath,
                                                   final String itemName,
                                                   final String userName ) {
        addToRecentEdited( itemPath, itemName, userName );

        //deliver messages to users inboxes (ie., the edited item is the itme that the current logged in user has edited in the past, or commented on)
        addToIncoming( itemPath, itemName, userName, MailboxService.MAIL_MAN );
        mailboxService.processOutgoing();
    }

    /**
     * This should be called when the user edits or comments on an asset. Simply
     * adds to the list...
     */
    private void addToRecentEdited( final String itemPath,
                                    final String note,
                                    final String userName ) {
        addToInbox( RECENT_EDITED_ID,
                    itemPath,
                    note,
                    userName,
                    userName );
    }

    private void addToRecentOpened( String itemPath,
                                    String note,
                                    String userName ) {
        addToInbox( RECENT_VIEWED_ID,
                    itemPath,
                    note,
                    userName,
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

        writeEntries( userName, boxName, entries );
    }

    private List<InboxEntry> removeAnyExisting( final String itemPath,
                                                final List<InboxEntry> inboxEntries ) {
        Iterator<InboxEntry> it = inboxEntries.iterator();
        while ( it.hasNext() ) {
            InboxEntry e = it.next();
            if ( e.getItemPath().equals( itemPath ) ) {
                it.remove();
                return inboxEntries;
            }
        }
        return inboxEntries;
    }

    private void writeEntries( final String userName,
                               final String boxName,
                               final List<InboxEntry> entries ) {
        final Path path = userServicesBackend.buildPath( userName, INBOX, boxName );

        String entry = getXStream().toXML( entries );

        ioService.write( path, entry );
    }

    private XStream getXStream() {
        XStream xs = new XStream();
        xs.alias( "inbox-entries", List.class );
        xs.alias( "entry", InboxEntry.class );
        return xs;
    }

}
