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

import org.guvnor.inbox.backend.server.security.InboxEntrySecurity;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class InboxBackendImplTest {

    private InboxBackendImpl inboxBackend;
    private Path resourcePath;
    private SessionInfo sessionInfo;
    private FileSystem mockedFSId;
    private IOService ioService;
    private InboxEntrySecurity securitySpy;
    private UserServicesBackendImpl userServicesBackend;
    private MailboxService mailboxService;
    private FileSystem systemFS;

    @Before
    public void setup() {
        ioService = mock( IOService.class );
        systemFS = mock( FileSystem.class );
        final InboxEntrySecurity security = new InboxEntrySecurity() {
            @Override
            public List<InboxEntry> secure( List<InboxEntry> inboxEntries ) {
                return inboxEntries;
            }
        };
        securitySpy = spy( security );
        userServicesBackend = mock( UserServicesBackendImpl.class );
        mailboxService = mock( MailboxService.class );
        sessionInfo = mock( SessionInfo.class );
        final User user = mock( User.class );

        when( sessionInfo.getIdentity() ).thenReturn( user );
        when( user.getIdentifier() ).thenReturn( "user1" );

        resourcePath = mock( Path.class );
        mockedFSId = mock( FileSystem.class, withSettings().extraInterfaces( FileSystemId.class ) );

        when( resourcePath.toURI() ).thenReturn( URI.create( "jgit://repo/my-file.txt" ).toString() );
        when( resourcePath.getFileName() ).thenReturn( "my-file.txt" );

        final org.uberfire.java.nio.file.Path rootPath = mock( org.uberfire.java.nio.file.Path.class );

        when( systemFS.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );
        when( mockedFSId.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );

        when( rootPath.getFileSystem() ).thenReturn( mockedFSId );
        when( ( ( FileSystemId ) mockedFSId ).id() ).thenReturn( "my-fsid" );

    }

    @Test
    public void testCheckBatch() {
        inboxBackend = new InboxBackendImpl( ioService, systemFS, userServicesBackend, mailboxService, securitySpy );

        inboxBackend.recordOpeningEvent( new ResourceOpenedEvent( resourcePath, sessionInfo ) );

        verify( ioService, times( 1 ) ).startBatch( mockedFSId );

        verify( ioService, times( 1 ) ).endBatch();

        inboxBackend.recordUserEditEvent( new ResourceUpdatedEvent( resourcePath, "message", sessionInfo ) );

        verify( ioService, times( 2 ) ).startBatch( mockedFSId );

        verify( ioService, times( 2 ) ).endBatch();

    }

    @Test
    public void readShouldSecureItems() {
        org.uberfire.java.nio.file.Path path = mock( org.uberfire.java.nio.file.Path.class );
        when( userServicesBackend.buildPath( "userName", "inbox", "boxName" ) ).thenReturn( path );
        when( ioService.exists( path ) ).thenReturn( true );
        when( ioService.readAllString( path ) ).thenReturn( " " );
        final List<InboxEntry> entries = new ArrayList<InboxEntry>();
        final InboxEntry inboxEntry1 = new InboxEntry( "path1", "note1", "user1" );
        entries.add( inboxEntry1 );

        inboxBackend = new InboxBackendImpl( ioService, systemFS, userServicesBackend, mailboxService, securitySpy ) {
            @Override
            List<InboxEntry> getInboxEntries( String xml ) {

                return entries;
            }
        };

        inboxBackend.readEntries( "userName", "boxName" );

        verify( securitySpy ).secure( entries );
    }
}
