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
package org.guvnor.inbox.backend.server.security;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.inbox.backend.server.InboxEntry;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.security.authz.AuthorizationManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class InboxEntrySecurity {

    @Inject
    private User identity;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private RepositoryServiceImpl repositoryService;

    public InboxEntrySecurity() {
    }

    @Inject
    public InboxEntrySecurity( User identity, AuthorizationManager authorizationManager,
                               OrganizationalUnitService organizationalUnitService, KieProjectService projectService,
                               RepositoryServiceImpl repositoryService) {
        this.identity = identity;
        this.authorizationManager = authorizationManager;
        this.organizationalUnitService = organizationalUnitService;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
    }


    public List<InboxEntry> secure( List<InboxEntry> inboxEntries ) {
        List<InboxEntry> secureInboxEntries = new ArrayList<InboxEntry>();
        final Set<Repository> authorizedRepositories = getAuthorizedRepositories();
        for ( InboxEntry inboxEntry : inboxEntries ) {
            if ( canAccess( inboxEntry, authorizedRepositories ) ) {
                secureInboxEntries.add( inboxEntry );
            }
        }
        return secureInboxEntries;
    }

    private boolean canAccess( InboxEntry inboxEntry, Set<Repository> authorizedRepositories ) {

        final Repository inboxEntryRepository = getInboxEntryRepository( inboxEntry );

        if ( thereIsNoRepositoryAssociated( inboxEntryRepository ) ) {
            return true;
        } else if ( canAccessRepository( authorizedRepositories, inboxEntryRepository ) ) {
            return canAccessProject( inboxEntry );
        }
        return false;
    }

    private boolean canAccessProject( InboxEntry inboxEntry ) {
        Project project = getInboxEntryProject( inboxEntry );
        if ( thereIsNoProject( project ) ) {
            return true;
        } else {
            return authorizationManager.authorize( project, identity );
        }
    }

    private boolean thereIsNoProject( Project project ) {
        return project == null;
    }


    private boolean canAccessRepository( Set<Repository> authorizedRepositories, Repository inboxEntryRepository ) {
        return authorizedRepositories.contains( inboxEntryRepository );
    }

    Project getInboxEntryProject( final InboxEntry inboxEntry ) {
        final Path path = Paths.get( inboxEntry.getItemPath() );
        final org.uberfire.backend.vfs.Path vfsPath = org.uberfire.backend.server.util.Paths.convert( path );
        return projectService.resolveProject( vfsPath );
    }

    private boolean thereIsNoRepositoryAssociated( Repository inboxEntryRepository ) {
        return inboxEntryRepository == null;
    }

    Repository getInboxEntryRepository( InboxEntry inboxEntry ) {
        final Path path = Paths.get( inboxEntry.getItemPath() );
        final FileSystem fileSystem = path.getFileSystem();
        return repositoryService.getRepository( fileSystem );
    }

    private Set<Repository> getAuthorizedRepositories() {
        final Set<Repository> authorizedRepos = new HashSet<Repository>();
        for ( OrganizationalUnit ou : getAuthorizedOrganizationUnits() ) {
            final Collection<Repository> repositories = ou.getRepositories();
            for ( final Repository repository : repositories ) {
                if ( authorizationManager.authorize( repository,
                                                     identity ) ) {
                    authorizedRepos.add( repository );
                }
            }
        }
        return authorizedRepos;
    }

    private Collection<OrganizationalUnit> getAuthorizedOrganizationUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Collection<OrganizationalUnit> authorizedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        for ( OrganizationalUnit ou : organizationalUnits ) {
            if ( authorizationManager.authorize( ou,
                                                 identity ) ) {
                authorizedOrganizationalUnits.add( ou );
            }
        }
        return authorizedOrganizationalUnits;
    }

}
