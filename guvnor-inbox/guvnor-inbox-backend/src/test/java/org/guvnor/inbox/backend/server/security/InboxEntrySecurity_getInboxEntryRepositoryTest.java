/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.inbox.backend.server.security;

import java.io.File;
import java.io.IOException;

import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.inbox.backend.server.InboxEntry;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class InboxEntrySecurity_getInboxEntryRepositoryTest {

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private Repository repository;

    private InboxEntrySecurity inbox;

    private Path tempPath;
    private File tempFile;

    @Before
    public void setup() throws IOException {

        when( configuredRepositories.getRepositoryByRepositoryFileSystem( any( FileSystem.class ) ) ).thenReturn( repository );

        tempFile = File.createTempFile( "test", "txt" );
        tempPath = createTempPath( tempFile );

        inbox = new InboxEntrySecurity( mock( User.class ),
                                        mock( AuthorizationManager.class ),
                                        mock( OrganizationalUnitService.class ),
                                        mock( ProjectService.class ),
                                        configuredRepositories );
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

    @Test
    public void testWorkingURI() throws Exception {

        final InboxEntry entry = new InboxEntry( tempPath.toURI(),
                                                 "note",
                                                 "userFrom" );

        assertEquals( repository, inbox.getInboxEntryRepository( entry ) );
    }

    @Test
    public void testFileRemoved() throws Exception {

        tempFile.delete();

        final InboxEntry entry = new InboxEntry( tempPath.toURI(),
                                                 "note",
                                                 "userFrom" );

        assertEquals( repository, inbox.getInboxEntryRepository( entry ) );
    }

    @Test
    public void testBrokenURI() throws Exception {

        final InboxEntry entry = new InboxEntry( "git://master@broken",
                                                 "note",
                                                 "userFrom" );

        assertNull( inbox.getInboxEntryRepository( entry ) );
    }

    private Path createTempPath( final File file ) throws IOException {
        SimpleFileSystemProvider simpleFileSystemProvider = new SimpleFileSystemProvider();
        return Paths.convert( simpleFileSystemProvider.getPath( file.toURI() ) );
    }
}