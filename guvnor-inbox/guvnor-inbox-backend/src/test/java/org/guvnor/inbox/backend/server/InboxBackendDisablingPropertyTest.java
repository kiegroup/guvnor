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

import java.util.Arrays;
import java.util.HashMap;

import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.inbox.backend.server.security.InboxEntrySecurity;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InboxBackendDisablingPropertyTest {

    @Mock
    private FileSystem systemFS;

    private InboxBackendImpl inboxBackend;
    private HashMap<String, String> preferences;

    @Before
    public void setUp() throws Exception {

        final org.uberfire.java.nio.file.Path rootPath = mock(org.uberfire.java.nio.file.Path.class);
        when(systemFS.getRootDirectories()).thenReturn(Arrays.asList(rootPath));

        final AppConfigService configService = mock(AppConfigService.class);

        preferences = new HashMap<>();
        when(configService.loadPreferences()).thenReturn(preferences);

        inboxBackend = new InboxBackendImpl(mock(IOService.class),
                                            systemFS,
                                            configService,
                                            mock(UserServicesBackendImpl.class),
                                            mock(MailboxService.class),
                                            mock(InboxEntrySecurity.class));
    }

    @Test
    public void onRecordUserEditEvent() {

        inboxBackend.onRecordUserEditEvent(new ResourceUpdatedEvent(mock(Path.class),
                                                                    "",
                                                                    getSessionInfo()));
        verify(systemFS).getRootDirectories();
    }

    @Test
    public void onRecordUserEditEventDisablingPropertyTrue() {

        preferences.put(InboxBackend.INBOX_DISABLED,
                        "true");

        inboxBackend.onRecordUserEditEvent(new ResourceUpdatedEvent(mock(Path.class),
                                                                    "",
                                                                    getSessionInfo()));
        verify(systemFS,
               never()).getRootDirectories();
    }

    @Test
    public void onRecordUserEditEventDisablingPropertyFalse() {

        preferences.put(InboxBackend.INBOX_DISABLED,
                        "false");

        inboxBackend.onRecordUserEditEvent(new ResourceUpdatedEvent(mock(Path.class),
                                                                    "",
                                                                    getSessionInfo()));
        verify(systemFS).getRootDirectories();
    }

    @Test
    public void onRecordOpeningEvent() {

        inboxBackend.onRecordOpeningEvent(new ResourceOpenedEvent(mock(Path.class),
                                                                  getSessionInfo()));
        verify(systemFS).getRootDirectories();
    }

    @Test
    public void onRecordOpeningEventDisablingPropertyTrue() {

        preferences.put(InboxBackend.INBOX_DISABLED,
                        "true");

        inboxBackend.onRecordOpeningEvent(new ResourceOpenedEvent(mock(Path.class),
                                                                  getSessionInfo()));
        verify(systemFS,
               never()).getRootDirectories();
    }

    @Test
    public void onRecordOpeningEventDisablingPropertyFalse() {

        preferences.put(InboxBackend.INBOX_DISABLED,
                        "false");

        inboxBackend.onRecordOpeningEvent(new ResourceOpenedEvent(mock(Path.class),
                                                                  getSessionInfo()));
        verify(systemFS).getRootDirectories();
    }

    private SessionInfo getSessionInfo() {
        final SessionInfo sessionInfo = mock(SessionInfo.class);
        when(sessionInfo.getIdentity()).thenReturn(mock(User.class));
        return sessionInfo;
    }
}
