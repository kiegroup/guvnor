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

package org.drools.guvnor.server.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.UserInfo;
import org.drools.repository.UserInfo.InboxEntry;
import org.drools.repository.events.CheckinEvent;
import org.drools.repository.events.StorageEventManager;

/**
 * This service the "delivery" of messages to users inboxes for events.
 * Ideally only one instance of this running at a time (at least on a node) to avoid doubling up.
 */
@ApplicationScoped
public class MailboxService {

    private static final LoggingHelper log  = LoggingHelper.getLogger( MailboxService.class );

    private ExecutorService executor = null;
    private String mailmanUsername = null;

    @Inject
    private RepositoryStartupService repositoryStartupService;

    @Inject
    protected GuvnorBootstrapConfiguration guvnorBootstrapConfiguration;

    /**
     * Should be the for the "mailman" user.
     */
    private RulesRepository mailmanRulesRepository;

    @PostConstruct
    public void setup() {
        mailmanUsername = guvnorBootstrapConfiguration.extractMailmanUsername();
        String mailmanPassword = guvnorBootstrapConfiguration.extractMailmanPassword();
        mailmanRulesRepository = new RulesRepository(repositoryStartupService.newSession(mailmanUsername, mailmanPassword));
        executor = Executors.newSingleThreadExecutor();
        log.info("mailbox service is up");
        registerCheckinListener();
        wakeUp();
    }

    /**
     * Listen for changes to the repository - for inbox purposes
     */
    public void registerCheckinListener() {
        StorageEventManager.registerCheckinEvent(new CheckinEvent() {
            public void afterCheckin(AssetItem item) {
                UserInbox.recordUserEditEvent(item);  //to register that she edited...
                recordItemUpdated(item);   //for outgoing...
                wakeUp();
            }
        });
        log.info("CheckinListener registered");
    }

    @PreDestroy
    public void destroy() {
        stopExecutor();
        mailmanRulesRepository.logout();

        log.info( "Removing listeners...." );
        StorageEventManager.removeListeners();
    }
    
    public void stopExecutor() {
        log.info("Shutting down mailbox service");
        executor.shutdown();

        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info( "Mailbox service is shutdown.");
    }

    public void wakeUp() {
        log.debug("Waking up");
        executor.execute(new Runnable() {
            public void run() {
                processOutgoing();
            }
        });
    }

    /** Process any waiting messages */
    void processOutgoing()  {
        if (mailmanRulesRepository != null) {
            UserInbox mailman = new UserInbox(mailmanRulesRepository, mailmanUsername);
            final List<UserInfo.InboxEntry> es  = mailman.loadIncoming();
            log.debug("Outgoing messages size " + es.size());
            //wipe out inbox for mailman here...
            UserInfo.eachUser(this.mailmanRulesRepository, new UserInfo.Command() {
                public void process(final String toUser) {
                    log.debug("Processing any inbound messages for " + toUser);
                    if (toUser.equals(mailmanUsername)) return;
                    UserInbox inbox = new UserInbox(mailmanRulesRepository, toUser);
                    Set<String> recentEdited = makeSetOf(inbox.loadRecentEdited());
                    for (UserInfo.InboxEntry e : es) {
                        //the user who edited the item wont receive a message in inbox.
                        if (!e.from.equals(toUser) && recentEdited.contains(e.assetUUID)) {
                            inbox.addToIncoming(e.assetUUID, e.note, e.from);
                        }
                    }
                }
            });
            mailman.clearIncoming();
            mailmanRulesRepository.save();
        }
    }

    private Set<String> makeSetOf(List<InboxEntry> inboxEntries) {
        Set<String> entries = new HashSet<String>();
        for(InboxEntry e : inboxEntries) {
            entries.add(e.assetUUID);
        }
        return entries;
    }

    /**
     * Call this to note that there has been a change - will then publish to any interested parties.
     * @param item
     */
    public void recordItemUpdated(AssetItem item) {
        final String id = item.getUUID();
        final String name = item.getName();
        final String from = item.getRulesRepository().getSession().getUserID();
        executor.execute(new Runnable() {
            public void run() {
                if (mailmanRulesRepository !=null) {
                    // write the message to the admins outbox
                    UserInbox inbox = new UserInbox(mailmanRulesRepository, mailmanUsername);
                    inbox.addToIncoming(id, name, from);
                    processOutgoing();

                    mailmanRulesRepository.save();
                }
            }
        });
    }


}
