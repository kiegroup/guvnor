/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.io.FileSystemType.Bootstrap.*;

/**
 * This service the "delivery" of messages to users inboxes for events.
 * Ideally only one instance of this running at a time (at least on a node) to avoid doubling up.
 */
@ApplicationScoped
public class MailboxService {

    private static final Logger log = LoggerFactory.getLogger( MailboxService.class );

    private MailboxProcessOutgoingExecutorManager executorManager = null;
    public static final String MAIL_MAN = "mailman";

    @Inject
    private InboxBackend inboxBackend;

    @Inject
    @Named("configIO")
    private IOService ioService;

    private FileSystem bootstrapFS = null;

    @PostConstruct
    public void setup() {
        final Iterator<FileSystem> fsIterator = ioService.getFileSystems( BOOTSTRAP_INSTANCE ).iterator();
        if ( fsIterator.hasNext() ) {
            bootstrapFS = fsIterator.next();
        }

        log.info( "mailbox service is up" );
        processOutgoing();
    }

    @PreDestroy
    public void destroy() {
        stopExecutor();
    }

    private void stopExecutor() {
        if ( executorManager != null && !isEjb(executorManager, MailboxProcessOutgoingExecutorManager.class)) {
            log.info( "Shutting down mailbox service" );
            executorManager.shutdown();
            log.info( "Mailbox service is shutdown." );
        }
    }

    private boolean isEjb(Object o, Class<?> expected) {
        if (o.getClass() != expected) {
            return true;
        }

        return false;
    }

    /**
     * Call this to note that there has been a change - will then publish to any interested parties.
     * @param item
     */
    /**
     * Process any waiting messages
     */
    void processOutgoing() {
        getExecutor().execute( new AsyncMailboxProcessOutgoing() {
            @Override
            public String getDescription() {
                return "Mailbox Outgoing Processing";
            }

            @Override
            public void execute( InboxBackend inboxBackend ) {
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
        } );
    }

    private synchronized MailboxProcessOutgoingExecutorManager getExecutor() {
        if ( executorManager == null ) {
            MailboxProcessOutgoingExecutorManager _executorManager = null;
            try {
                _executorManager = InitialContext.doLookup( "java:module/MailboxProcessOutgoingExecutorManager" );
            } catch ( final Exception ignored ) {
            }

            if ( _executorManager == null ) {
                executorManager = new MailboxProcessOutgoingExecutorManager();
                executorManager.setInboxBackend( inboxBackend );
            } else {
                executorManager = _executorManager;
            }
        }

        return executorManager;
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
            if ( value.endsWith( "-uf-user" ) ) {
                userList.add( value.substring( 0, value.indexOf( "-uf-user" ) ) );
            }
        }

        return userList.toArray( new String[ userList.size() ] );
    }

}
