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

import java.net.URI;
import java.util.Arrays;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.*;

/**
 * TODO: update me
 */
public class MailboxServiceTest {

    @Test
    public void testCheckBatch() {
        final IOService ioService = mock( IOService.class );
        final FileSystem systemFS = mock( FileSystem.class );
        final SessionInfo sessionInfo = mock( SessionInfo.class );
        final User user = mock( User.class );

        when( sessionInfo.getIdentity() ).thenReturn( user );
        when( user.getIdentifier() ).thenReturn( "user1" );

        final Path resourcePath = mock( Path.class );
        final FileSystem mockedFSId = mock( FileSystem.class, withSettings().extraInterfaces( FileSystemId.class ) );

        when( resourcePath.toURI() ).thenReturn( URI.create( "jgit://repo/my-file.txt" ).toString() );
        when( resourcePath.getFileName() ).thenReturn( "my-file.txt" );

        final org.uberfire.java.nio.file.Path rootPath = mock( org.uberfire.java.nio.file.Path.class );

        when( systemFS.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );
        when( mockedFSId.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );

        when( rootPath.getFileSystem() ).thenReturn( mockedFSId );
        when( ( (FileSystemId) mockedFSId ).id() ).thenReturn( "my-fsid" );

        when( rootPath.toUri() ).thenReturn( URI.create( "jgit://user1-uf-user@sss" ) );

        final MailboxService inboxBackend = new MailboxService( mock( InboxBackend.class ), ioService, systemFS );

        inboxBackend.setup();

        verify( ioService, times( 1 ) ).startBatch( mockedFSId );

        verify( ioService, times( 1 ) ).endBatch();
    }
}
