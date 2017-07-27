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
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.inbox.backend.server.security.InboxEntrySecurity;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class InboxBackendImpl implements InboxBackend {

    static final int MAX_RECENT_EDITED = 200;

    private static final String RECENT_EDITED_ID = "recentEdited";
    private static final String RECENT_VIEWED_ID = "recentViewed";
    private static final String INCOMING_ID = "incoming";
    private static final String INBOX = "inbox";

    private IOService ioService;
    private FileSystem bootstrapFS;
    private AppConfigService configService;
    private UserServicesBackendImpl userServicesBackend;
    private MailboxService mailboxService;
    private InboxEntrySecurity inboxEntrySecurity;

    //Proxyable
    public InboxBackendImpl() {
    }

    @Inject
    public InboxBackendImpl(@Named("configIO") final IOService ioService,
                            @Named("systemFS") final FileSystem bootstrapFS,
                            final AppConfigService configService,
                            final UserServicesBackendImpl userServicesBackend,
                            final MailboxService mailboxService,
                            final InboxEntrySecurity inboxEntrySecurity) {
        this.ioService = ioService;
        this.bootstrapFS = bootstrapFS;
        this.configService = configService;
        this.userServicesBackend = userServicesBackend;
        this.mailboxService = mailboxService;
        this.inboxEntrySecurity = inboxEntrySecurity;
    }

    @Override
    public List<InboxEntry> loadRecentEdited(User user) {
        return readEntries(user,
                           RECENT_EDITED_ID);
    }

    @Override
    public List<InboxEntry> loadIncoming(User user) {
        return readEntries(user,
                           INCOMING_ID);
    }

    @Override
    public List<InboxEntry> readEntries(User user,
                                        String boxName) {
        final Path path = userServicesBackend.buildPath(user.getIdentifier(),
                                                        INBOX,
                                                        boxName);

        if (ioService.exists(path)) {
            final String xml = ioService.readAllString(path);
            if (!(xml == null || xml.equals(""))) {
                final List<InboxEntry> inboxEntries = getInboxEntries(xml);

                return inboxEntrySecurity.secure(inboxEntries,
                                                 user);
            } else {
                return new ArrayList<InboxEntry>();
            }
        }

        return new ArrayList<InboxEntry>();
    }

    List<InboxEntry> getInboxEntries(String xml) {
        return (List<InboxEntry>) getXStream().fromXML(xml);
    }

    @Override
    public void addToIncoming(String itemPath,
                              String note,
                              User userFrom,
                              User userTo) {
        addToInbox(INCOMING_ID,
                   itemPath,
                   note,
                   userFrom,
                   userTo);
    }

    public void onRecordOpeningEvent(@Observes final ResourceOpenedEvent event) {
        checkNotNull("event",
                     event);

        if (isInboxDisabled()) {
            return;
        }

        final org.uberfire.backend.vfs.Path resourcePath = event.getPath();
        try {
            ioService.startBatch(bootstrapFS.getRootDirectories().iterator().next().getFileSystem());
            onRecordOpeningEvent(resourcePath.toURI(),
                                 resourcePath.getFileName(),
                                 event.getSessionInfo().getIdentity());
        } finally {
            ioService.endBatch();
        }
    }

    public void onRecordUserEditEvent(@Observes final ResourceUpdatedEvent event) {
        checkNotNull("event",
                     event);

        if (isInboxDisabled()) {
            return;
        }

        try {
            ioService.startBatch(bootstrapFS.getRootDirectories().iterator().next().getFileSystem());
            onRecordUserEditEvent(event.getPath().toURI(),
                                  event.getPath().getFileName(),
                                  event.getSessionInfo().getIdentity());
        } finally {
            ioService.endBatch();
        }
    }

    /**
     * Helper method to log the opening. Will remove any inbox items that have
     * the same id.
     */
    private void onRecordOpeningEvent(final String itemPath,
                                      final String itemName,
                                      final User user) {
        addToRecentOpened(itemPath,
                          itemName,
                          user);
        List<InboxEntry> unreadIncoming = removeAnyExisting(itemPath,
                                                            loadIncoming(user));
        writeEntries(user,
                     INCOMING_ID,
                     unreadIncoming);
    }

    /**
     * Helper method to note the event
     */
    //@Override
    private void onRecordUserEditEvent(final String itemPath,
                                       final String itemName,
                                       final User user) {
        addToRecentEdited(itemPath,
                          itemName,
                          user);

        //deliver messages to users inboxes (ie., the edited item is the item that the current logged in user has edited in the past, or commented on)
        addToIncoming(itemPath,
                      itemName,
                      user,
                      MailboxService.MAIL_MAN);
        mailboxService.processOutgoing();
    }

    /**
     * This should be called when the user edits or comments on an asset. Simply
     * adds to the list...
     */
    private void addToRecentEdited(final String itemPath,
                                   final String note,
                                   final User user) {
        addToInbox(RECENT_EDITED_ID,
                   itemPath,
                   note,
                   user,
                   user);
    }

    private void addToRecentOpened(String itemPath,
                                   String note,
                                   User user) {
        addToInbox(RECENT_VIEWED_ID,
                   itemPath,
                   note,
                   user,
                   user);
    }

    private void addToInbox(String boxName,
                            String itemPath,
                            String note,
                            User userFrom,
                            User userTo) {
        assert boxName.equals(RECENT_EDITED_ID) || boxName.equals(RECENT_VIEWED_ID) || boxName
                .equals(INCOMING_ID);
        List<InboxEntry> entries = removeAnyExisting(itemPath,
                                                     readEntries(userTo,
                                                                 boxName));

        if (entries.size() >= MAX_RECENT_EDITED) {
            entries.remove(0);
            entries.add(new InboxEntry(itemPath,
                                       note,
                                       userFrom.getIdentifier()));
        } else {
            entries.add(new InboxEntry(itemPath,
                                       note,
                                       userFrom.getIdentifier()));
        }

        writeEntries(userTo,
                     boxName,
                     entries);
    }

    private List<InboxEntry> removeAnyExisting(final String itemPath,
                                               final List<InboxEntry> inboxEntries) {
        Iterator<InboxEntry> it = inboxEntries.iterator();
        while (it.hasNext()) {
            InboxEntry e = it.next();
            if (e.getItemPath().equals(itemPath)) {
                it.remove();
                return inboxEntries;
            }
        }
        return inboxEntries;
    }

    private void writeEntries(final User userTo,
                              final String boxName,
                              final List<InboxEntry> entries) {
        final Path path = userServicesBackend.buildPath(userTo.getIdentifier(),
                                                        INBOX,
                                                        boxName);

        String entry = getXStream().toXML(entries);

        ioService.write(path,
                        entry);
    }

    private boolean isInboxDisabled() {
        return configService.loadPreferences().containsKey(INBOX_DISABLED)
                && configService.loadPreferences().get(INBOX_DISABLED).toLowerCase().trim().equals("true");
    }

    private XStream getXStream() {
        XStream xs = new XStream();
        String[] voidDeny = {"void.class", "Void.class"};
        xs.denyTypes(voidDeny);
        xs.alias("inbox-entries",
                 List.class);
        xs.alias("entry",
                 InboxEntry.class);
        return xs;
    }
}
