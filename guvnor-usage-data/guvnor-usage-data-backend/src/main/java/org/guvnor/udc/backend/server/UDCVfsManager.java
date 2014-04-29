/*
 * Copyright 2013 JBoss by Red Hat.
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

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.UsageEventSummary;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.security.Identity;

import com.thoughtworks.xstream.XStream;

public abstract class UDCVfsManager extends UDCSessionManager {

    @Inject
    private UserServicesImpl userServices;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @SessionScoped
    protected Identity identity;
    
    @Inject
    protected MailboxService mailboxService;
    
    protected static final String RECENT_EDITED_ID = EventTypes.RECENT_EDITED_ID.getInboxName();

    protected static final String RECENT_VIEWED_ID = EventTypes.RECENT_VIEWED_ID.getInboxName();

    protected static final String INCOMING_ID = EventTypes.INCOMING_ID.getInboxName();

    protected static final String INBOX = "inbox";

    protected static final int MAX_SIZE_QUEUE = 100;
    
    private static final String PATH_GFS = "/.metadata/.users/";
    
    private Path bootstrapRoot = null;

    private static final String patternName = "yyyyMMdd_HHmmss-SSS";

    private static final String UDC = "udc";
    

    @PostConstruct
    public void setup() {
        final Iterator<FileSystem> fsIterator = ioService.getFileSystems(BOOTSTRAP_INSTANCE).iterator();
        if (fsIterator.hasNext()) {
            final FileSystem bootstrap = fsIterator.next();
            final Iterator<org.kie.commons.java.nio.file.Path> rootIterator = bootstrap.getRootDirectories().iterator();
            if (rootIterator.hasNext()) {
                this.bootstrapRoot = rootIterator.next();
            }
        }
    }

    /**
     * return unique path by session
     */
    protected Path getPathBySession() {
        return userServices.buildPath(UDC, getUniqueNameByFile());
    }

    protected Path getPath(String inbox, String boxName) {
        return userServices.buildPath(inbox, boxName);
    }

    protected Path getPathByUser(String user, String inbox, String boxName) {
        return userServices.buildPath(user, inbox, boxName);
    }

    protected Path getPathByType(EventTypes eventType, String userName) {
        Path path;
        switch (eventType) {
        case USAGE_DATA:
            path = getPathBySession();
            break;
        default:
            path = getPathByUser(userName, INBOX, eventType.getInboxName());
            break;
        }
        return path;
    }

    protected XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("inbox-entries", Queue.class);
        xs.alias("entry", UsageEventSummary.class);
        return xs;
    }

    protected String getUniqueNameByFile() {
        String userName = identity.getName();
        if (this.getSession().getAttribute(userName) == null) {
            this.getSession().setAttribute(userName, this.getUniqueKeyByUser(userName));
        }
        return String.valueOf(this.getSession().getAttribute(userName));
    }

    private String getUniqueKeyByUser(String userName) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(patternName);
        return new StringBuilder(userName).append("-").append(sdf.format(new Date())).toString();
    }

    protected void checkSizeQueue(Queue<UsageEventSummary> entries) {
        if (entries.size() >= MAX_SIZE_QUEUE) {
            entries.poll();
        }
    }

    protected void broadcastEvent() {
        mailboxService.processOutgoing();
        mailboxService.wakeUp();
    }
    
    protected Path getPathBase(){
        return bootstrapRoot.resolve(PATH_GFS);
    }
    
    protected URI getUriPath(){
        return getPathBase().toUri();
    } 
    
    protected FileSystem getFileSystemPath(){
        return getPathBase().getFileSystem();
    }
    
}