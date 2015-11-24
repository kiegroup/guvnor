/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

/**
 * This service the "delivery" of messages to users inboxes for events.
 * Ideally only one instance of this running at a time (at least on a node) to avoid doubling up.
 */
@ApplicationScoped
public class MailboxService {

    private static final Logger log = LoggerFactory.getLogger( MailboxService.class );
    public static final String MAIL_MAN = "mailman";

    private InboxBackend inboxBackend;
    private IOService ioService;
    private FileSystem bootstrapFS;

    //Proxyable
    public MailboxService(){
    }

    @Inject
    public MailboxService( final InboxBackend inboxBackend,
                           @Named("configIO") final IOService ioService,
                           @Named("systemFS") final FileSystem bootstrapFS ) {
        this.inboxBackend = inboxBackend;
        this.ioService = ioService;
        this.bootstrapFS = bootstrapFS;
    }

    @PostConstruct
    public void setup() {
        log.info( "mailbox service is up" );
        try {
            ioService.startBatch( bootstrapFS.getRootDirectories().iterator().next().getFileSystem() );
            processOutgoing();
        } finally {
            ioService.endBatch();
        }
    }

    /**
     * Call this to note that there has been a change - will then publish to any interested parties.
     * @param item
     */
    /**
     * Process any waiting messages
     */
    void processOutgoing() {
        final List<InboxEntry> es = inboxBackend.loadIncoming( MAIL_MAN );
        log.debug( "Outgoing messages size " + es.size() );
        //wipe out inbox for mailman here...

        String[] userList = listUsers();
        log.debug( "userServices:" + userList.length );
        for ( String toUser : userList ) {
            log.debug( "userServices:" + toUser );
            log.debug( "Processing any inbound messages for " + toUser );
            if ( toUser.equals( MAIL_MAN ) ) {
                return;
            }

            final Set<String> recentEdited = makeSetOf( inboxBackend.loadRecentEdited( toUser ) );
            for ( InboxEntry e : es ) {
                //the user who edited the item wont receive a message in inbox.
                if ( !e.getFrom().equals( toUser ) && recentEdited.contains( e.getItemPath() ) ) {
                    inboxBackend.addToIncoming( e.getItemPath(), e.getNote(), e.getFrom(), toUser );
                }
            }
        }
    }

    private Set<String> makeSetOf( List<InboxEntry> inboxEntries ) {
        final Set<String> entries = new HashSet<String>();
        for ( InboxEntry e : inboxEntries ) {
            entries.add( e.getItemPath() );
        }
        return entries;
    }

    public String[] listUsers() {
        //TODO: a temporary hack to retrieve user list. Root dirs are branches and every user has it's own branch
        final List<String> userList = new ArrayList<String>();

        for ( final Path path : bootstrapFS.getRootDirectories() ) {
            final String value = path.toUri().getUserInfo();
            if ( value != null && value.endsWith( "-uf-user" ) ) {
                userList.add( value.substring( 0, value.indexOf( "-uf-user" ) ) );
            }
        }

        return userList.toArray( new String[ userList.size() ] );
    }

}
