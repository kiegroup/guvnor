/*
 * Copyright 2013 JBoss Inc
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
package org.guvnor.udc.backend.server;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.UDCVfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service the "delivery" of messages to users inboxes for events. Ideally
 * only one instance of this running at a time (at least on a node) to avoid
 * doubling up.
 */
@ApplicationScoped
public class MailboxService {

    private static final Logger log = LoggerFactory.getLogger(MailboxService.class);

    private ExecutorService executor = null;

    public static final String MAIL_MAN = "mailman";

    @Inject
    private UDCVfsService udcVfsService;

    @PostConstruct
    public void setup() {
        executor = Executors.newSingleThreadExecutor();
        wakeUp();
    }

    @PreDestroy
    public void destroy() {
        stopExecutor();
    }

    public void stopExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("MailBox executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void wakeUp() {
        executor.execute(new Runnable() {
            public void run() {
                processOutgoing();
            }
        });
    }

    /**
     * Call this to note that there has been a change - will then publish to any
     * interested parties. Process any waiting messages
     */
    void processOutgoing() {
        executor.execute(new Runnable() {
            public void run() {
                
                final Queue<UsageEventSummary> es = udcVfsService.readEventsByFilter(EventTypes.INCOMING_ID, MAIL_MAN);
                for (String toUser : udcVfsService.getUsersVfs()) {
                    if (!toUser.equals(MAIL_MAN)) {
                        Set<String> recentEdited = makeSetOf(udcVfsService.readEventsByFilter(EventTypes.RECENT_EDITED_ID,
                                toUser));
                        for (UsageEventSummary e : es) {
                            if (!e.getFrom().equals(toUser) && recentEdited.contains(e.getItemPath())) {
                                udcVfsService.addToIncoming(e.getItemPath(), e.getFileName(), e.getFrom(), toUser);
                            }
                        }
                    }
                }

            }
        });

    }

    private Set<String> makeSetOf(Queue<UsageEventSummary> inboxEntries) {
        Set<String> entries = new HashSet<String>();
        for (UsageEventSummary e : inboxEntries) {
            entries.add(e.getItemPath());
        }
        return entries;
    }

}