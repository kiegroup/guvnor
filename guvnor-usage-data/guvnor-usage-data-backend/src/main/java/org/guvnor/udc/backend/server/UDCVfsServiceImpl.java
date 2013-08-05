/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.backend.server;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.GfsSummary;
import org.guvnor.udc.model.InboxPageRequest;
import org.guvnor.udc.model.InboxPageRow;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.PageResponseBuilder;
import org.guvnor.udc.service.UDCVfsService;
import org.guvnor.udc.service.UsageEventSummaryBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.Paths;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import com.google.common.collect.Lists;

@Service
@ApplicationScoped
public class UDCVfsServiceImpl extends UDCVfsManager implements UDCVfsService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public Queue<UsageEventSummary> readUsageDataCollector() {
        return readEntriesByPath(super.getPathBySession());
    }

    @Override
    public void removeEventsByFilter(EventTypes eventType, String userName) {
        Path path;
        switch (eventType) {
        case ALL:
            for (EventTypes type : EventTypes.values()) {
                path = getPathByType(type, userName);
                if (type != EventTypes.ALL && ioService.exists(path)) {
                    ioService.delete(path);
                }
            }
        default:
            path = getPathByType(eventType, userName);
            if (ioService.exists(path)) {
                ioService.delete(getPathByType(eventType, userName));
            }
            break;
        }
    }

    @Override
    public Queue<UsageEventSummary> readEventsByFilter(EventTypes eventType, String userName) {
        Queue<UsageEventSummary> usages = null;
        switch (eventType) {
        case USAGE_DATA:
            usages = readUsageDataCollector();
            break;
        case ALL:
            usages = readAllEntries(userName);
            break;
        default:
            // default (RECENT_EDITED_ID, RECENT_VIEWED_ID, INCOMING_ID)
            usages = readEntries(userName, eventType.getInboxName());
            break;
        }
        return usages;
    }

    @Override
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) {
        PortablePreconditions.checkNotNull("request", request);
        if (request.getPageSize() != null && request.getPageSize() < 0) {
            throw new IllegalArgumentException("pageSize cannot be less than zero.");
        }

        Queue<UsageEventSummary> entries = readEntries(identity.getName(), request.getInboxName());
        Iterator<UsageEventSummary> iterator = entries.iterator();
        List<InboxPageRow> rowList = new InboxPageRowBuilder().withPageRequest(request).withIdentity(identity)
                .withContent(iterator).build();

        return new PageResponseBuilder<InboxPageRow>().withStartRowIndex(request.getStartRowIndex())
                .withTotalRowSize(entries.size()).withTotalRowSizeExact().withPageRowList(rowList)
                .withLastPage(!iterator.hasNext()).build();
    }

    @Override
    public void addToIncoming(String itemPath, String note, String userFrom, String userName) {
        addToInbox(INCOMING_ID, itemPath, note, userFrom, userName);
    }

    @Override
    public void addToUsageData(UsageEventSummary usageEvent) {
        Path path = getPathBySession();
        usageEvent.setItemPath(String.valueOf(path));
        usageEvent.setFileName(String.valueOf(path.getFileName()));
        usageEvent.setFileSystem(String.valueOf(path.getFileSystem()));
        Queue<UsageEventSummary> usages = this.readUsageDataCollector();
        checkSizeQueue(usages);
        usages.add(usageEvent);
        writeEntries(usages, path);
    }
    
    @Override
    public GfsSummary getInfoGfs() {
        GfsSummary gfsSummary;
        try {
            gfsSummary = new GfsSummary();
            gfsSummary.setInboxUsers(getUsersVfs());
            gfsSummary.setPath(super.getPathBase().toString());
            gfsSummary.setUriPath(super.getUriPath().toString());
            gfsSummary.setFileSystem(super.getFileSystemPath().toString());
        } catch (Exception e) {
            gfsSummary = null;
        }
        return gfsSummary;
    }
    
    @Override
    public String[] getUsersVfs(){
        List<String> users = Lists.newArrayList();
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(getUriPath()));
        for (Path path : directoryStream) {
            users.add(path.getFileName().toString());
        }
        return users.toArray(new String[users.size()]);
    }
    
    /**
     * This should be called when the user edits or comments on an asset. Simply
     * adds to the list...
     */
    @Override
    public void addToRecentEdited(String itemPath, String note) {
        addToInbox(RECENT_EDITED_ID, itemPath, note, identity.getName(), identity.getName());
    }

    @Override
    public void addToRecentOpened(String itemPath, String note) {
        addToInbox(RECENT_VIEWED_ID, itemPath, note, identity.getName(), identity.getName());
    }

    public void recordOpeningEvent(@Observes final ResourceOpenedEvent event) {
        PortablePreconditions.checkNotNull("event", event);
        final org.uberfire.backend.vfs.Path resourcePath = event.getPath();
        recordOpeningEvent(resourcePath.toURI(), resourcePath.getFileName().toString());
    }

    public void recordUserEditEvent(@Observes final ResourceUpdatedEvent event) {
        PortablePreconditions.checkNotNull("event", event);
        final org.uberfire.backend.vfs.Path resourcePath = event.getPath();
        recordUserEditEvent(resourcePath.toURI(), resourcePath.getFileName().toString());
    }
    
    /**
     * Helper method to note the event. addToRecentEdited and deliver messages
     * to users inboxes (ie., the edited item is the itme that the current
     * logged in user has edited in the past, or commented on)
     */
    private synchronized void recordUserEditEvent(String itemPath, String itemName) {
        addToRecentEdited(itemPath, itemName);
        addToIncoming(itemPath, itemName, identity.getName(), MailboxService.MAIL_MAN);
        super.broadcastEvent();
    }

    private synchronized void recordOpeningEvent(String itemPath, String itemName) {
        addToRecentOpened(itemPath, itemName);
        Queue<UsageEventSummary> unreadIncoming = removeAnyExisting(itemPath, readEntries(identity.getName(), INCOMING_ID));
        writeEntries(unreadIncoming, getPath(INBOX, INCOMING_ID));
    }

    private void addToInbox(String boxName, String itemPath, String note, String userFrom, String toUser) {
        assert boxName.equals(RECENT_EDITED_ID) || boxName.equals(RECENT_VIEWED_ID) || boxName.equals(INCOMING_ID);
        Path path = (!userFrom.equals(toUser)) ? getPathByUser(toUser, INBOX, boxName) : getPath(INBOX, boxName);
        Queue<UsageEventSummary> entries = removeAnyExisting(itemPath, readEntries(toUser, boxName));
        checkSizeQueue(entries);
        entries.add(new UsageEventSummaryBuilder().key(boxName).description("resource: " + itemPath).component(boxName)
                .from(userFrom).toUser(toUser).itemPath(itemPath).fileName(String.valueOf(path.getFileName()))
                .fileSystem(String.valueOf(path.getFileSystem())).build());

        writeEntries(entries, path);
    }

    private Queue<UsageEventSummary> removeAnyExisting(String itemPath, Queue<UsageEventSummary> inboxEntries) {
        Iterator<UsageEventSummary> it = inboxEntries.iterator();
        while (it.hasNext()) {
            UsageEventSummary e = it.next();
            if (e.getItemPath().equals(itemPath)) {
                it.remove();
                return inboxEntries;
            }
        }
        return inboxEntries;
    }

    private void writeEntries(Queue<UsageEventSummary> usagesData, Path path) {
        ioService.write(path, getXStream().toXML(usagesData));
    }

    private Queue<UsageEventSummary> readEntries(String userName, String boxName) {
        return readEntriesByPath(getPathByUser(userName, INBOX, boxName));
    }

    @SuppressWarnings("unchecked")
    private Queue<UsageEventSummary> readEntriesByPath(Path path) {
        Queue<UsageEventSummary> entries = Lists.newLinkedList();
        if (ioService.exists(path)) {
            final String xml = ioService.readAllString(path);
            if (xml != null && !xml.equals("")) {
                entries = (Queue<UsageEventSummary>) getXStream().fromXML(xml);
            }
        }
        return entries;
    }

    private Queue<UsageEventSummary> readAllEntries(String userName) {
        Queue<UsageEventSummary> events = Lists.newLinkedList();
        for (EventTypes type : EventTypes.values()) {
            if (type != EventTypes.ALL) {
                events.addAll(readEventsByFilter(type, userName));
            }
        }
        return events;
    }

}