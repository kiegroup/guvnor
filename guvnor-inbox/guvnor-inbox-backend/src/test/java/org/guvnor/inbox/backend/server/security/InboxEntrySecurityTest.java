/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.inbox.backend.server.InboxEntry;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class InboxEntrySecurityTest {

    private Repository                        repo1;
    private Repository                        repo2;
    private User                              user;
    private AuthorizationManager              authorizationManager;
    private OrganizationalUnitService         organizationalUnitService;
    private ProjectService<? extends Project> projectService;
    private ConfiguredRepositories            configuredRepositories;
    private Project                           project1;

    @Before
    public void setup() {
        user = mock( User.class );
        authorizationManager = mock( AuthorizationManager.class );
        organizationalUnitService = mock( OrganizationalUnitService.class );
        Collection<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
        final OrganizationalUnit ou = mock( OrganizationalUnit.class );
        ous.add( ou );
        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( ous );
        when( authorizationManager.authorize( ou, user ) ).thenReturn( true );
        Collection<Repository> repositories = new ArrayList<Repository>();
        repo1 = mock( Repository.class );
        repo2 = mock( Repository.class );
        project1 = mock( Project.class );

        repositories.add( repo1 );
        when( authorizationManager.authorize( repo1, user ) ).thenReturn( true );
        when( authorizationManager.authorize( project1, user ) ).thenReturn( true );
        when( ou.getRepositories() ).thenReturn( repositories );

        projectService = null;
        configuredRepositories = null;


    }


    @Test
    public void testSecureNullRepoNullProject() throws Exception {
        InboxEntrySecurity inbox = new InboxEntrySecurity( user, authorizationManager, organizationalUnitService,
                                                           projectService,
                                                           configuredRepositories ) {
            @Override
            Repository getInboxEntryRepository( InboxEntry inboxEntry ) {
                return null;
            }

            @Override
            Project getInboxEntryProject( InboxEntry inboxEntry ) {
                return null;
            }
        };
        List<InboxEntry> entries = new ArrayList<InboxEntry>();
        final InboxEntry inboxEntry1 = new InboxEntry( "path1", "note1", "user1" );
        final InboxEntry inboxEntry2 = new InboxEntry( "path2", "note2", "user2" );
        final InboxEntry inboxEntry3 = new InboxEntry( "path3", "note3", "user3" );
        entries.add( inboxEntry1 );
        entries.add( inboxEntry2 );
        entries.add( inboxEntry3 );


        assertEquals( entries.size(), inbox.secure( entries ).size() );

    }

    @Test
    public void testSecureRepoWithoutProject() throws Exception {
        InboxEntrySecurity inbox = new InboxEntrySecurity( user, authorizationManager, organizationalUnitService,
                                                           projectService,
                                                           configuredRepositories ) {
            @Override
            Repository getInboxEntryRepository( InboxEntry inboxEntry ) {
                if ( inboxEntry.getItemPath().equals( "path1" ) ) {
                    return repo2;
                }
                return repo1;
            }

            @Override
            Project getInboxEntryProject( InboxEntry inboxEntry ) {
                return null;
            }
        };
        List<InboxEntry> entries = new ArrayList<InboxEntry>();
        final InboxEntry inboxEntry1 = new InboxEntry( "path1", "note1", "user1" );
        final InboxEntry inboxEntry2 = new InboxEntry( "path2", "note2", "user2" );
        final InboxEntry inboxEntry3 = new InboxEntry( "path3", "note3", "user3" );
        entries.add( inboxEntry1 );
        entries.add( inboxEntry2 );
        entries.add( inboxEntry3 );


        assertEquals( 2, inbox.secure( entries ).size() );

    }

    @Test
    public void testSecureRepoInsecureProject() throws Exception {
        InboxEntrySecurity inbox = new InboxEntrySecurity( user, authorizationManager, organizationalUnitService,
                                                           projectService,
                                                           configuredRepositories ) {
            @Override
            Repository getInboxEntryRepository( InboxEntry inboxEntry ) {
                return repo1;
            }

            @Override
            Project getInboxEntryProject( InboxEntry inboxEntry ) {
                return mock( Project.class );
            }
        };
        List<InboxEntry> entries = new ArrayList<InboxEntry>();
        final InboxEntry inboxEntry1 = new InboxEntry( "path1", "note1", "user1" );
        final InboxEntry inboxEntry2 = new InboxEntry( "path2", "note2", "user2" );
        final InboxEntry inboxEntry3 = new InboxEntry( "path3", "note3", "user3" );
        entries.add( inboxEntry1 );
        entries.add( inboxEntry2 );
        entries.add( inboxEntry3 );


        assertEquals( 0, inbox.secure( entries ).size() );

    }

    @Test
    public void testSecureRepoSecureProject() throws Exception {
        InboxEntrySecurity inbox = new InboxEntrySecurity( user, authorizationManager, organizationalUnitService,
                                                           projectService,
                                                           configuredRepositories ) {
            @Override
            Repository getInboxEntryRepository( InboxEntry inboxEntry ) {
                return repo1;
            }

            @Override
            Project getInboxEntryProject( InboxEntry inboxEntry ) {
                return project1;
            }
        };
        List<InboxEntry> entries = new ArrayList<InboxEntry>();
        final InboxEntry inboxEntry1 = new InboxEntry( "path1", "note1", "user1" );
        final InboxEntry inboxEntry2 = new InboxEntry( "path2", "note2", "user2" );
        final InboxEntry inboxEntry3 = new InboxEntry( "path3", "note3", "user3" );
        entries.add( inboxEntry1 );
        entries.add( inboxEntry2 );
        entries.add( inboxEntry3 );


        assertEquals( 3, inbox.secure( entries ).size() );

    }
}