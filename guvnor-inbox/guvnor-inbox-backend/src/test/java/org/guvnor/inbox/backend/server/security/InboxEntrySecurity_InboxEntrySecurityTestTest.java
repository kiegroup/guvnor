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

import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.inbox.backend.server.InboxEntry;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.test.TestFileSystem;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class InboxEntrySecurity_InboxEntrySecurityTestTest {

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private Repository repository;

    private InboxEntrySecurity inbox;
    private TestFileSystem     testFileSystem;

    @Before
    public void setup() {
        testFileSystem = new TestFileSystem();

        when( configuredRepositories.getRepositoryByRepositoryFileSystem( any( FileSystem.class ) ) ).thenReturn( repository );

        inbox = new InboxEntrySecurity( mock( User.class ),
                                        mock( AuthorizationManager.class ),
                                        mock( OrganizationalUnitService.class ),
                                        mock( ProjectService.class ),
                                        configuredRepositories );
    }

    @Test
    public void testWorkingURI() throws Exception {

        final Path tempFile = testFileSystem.createTempFile( "text.txt" );

        final InboxEntry entry = new InboxEntry( tempFile.toURI(),
                                                 "note",
                                                 "userFrom" );

        assertEquals( repository, inbox.getInboxEntryRepository( entry ) );
    }

    @Test
    public void testFileRemoved() throws Exception {

        final Path tempFile = testFileSystem.createTempFile( "text.txt" );

        testFileSystem.deleteFile( tempFile );

        final InboxEntry entry = new InboxEntry( tempFile.toURI(),
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
}