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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.inbox.backend.server.InboxEntry;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class InboxEntrySecurity {

    private AuthorizationManager authorizationManager;

    private OrganizationalUnitService organizationalUnitService;

    private ProjectService<? extends Project> projectService;

    private ConfiguredRepositories configuredRepositories;

    public InboxEntrySecurity() {
    }

    @Inject
    public InboxEntrySecurity(final AuthorizationManager authorizationManager,
                              final OrganizationalUnitService organizationalUnitService,
                              final ProjectService<? extends Project> projectService,
                              final ConfiguredRepositories configuredRepositories) {
        this.authorizationManager = authorizationManager;
        this.organizationalUnitService = organizationalUnitService;
        this.projectService = projectService;
        this.configuredRepositories = configuredRepositories;
    }

    public List<InboxEntry> secure(List<InboxEntry> inboxEntries,
                                   User user) {
        List<InboxEntry> secureInboxEntries = new ArrayList<InboxEntry>();
        final Set<Repository> authorizedRepositories = getAuthorizedRepositories(user);
        for (InboxEntry inboxEntry : inboxEntries) {
            if (canAccess(inboxEntry,
                          authorizedRepositories,
                          user)) {
                secureInboxEntries.add(inboxEntry);
            }
        }
        return secureInboxEntries;
    }

    private boolean canAccess(InboxEntry inboxEntry,
                              Set<Repository> authorizedRepositories,
                              User user) {

        final Repository inboxEntryRepository = getInboxEntryRepository(inboxEntry);

        if (thereIsNoRepositoryAssociated(inboxEntryRepository)) {
            return true;
        } else if (canAccessRepository(authorizedRepositories,
                                       inboxEntryRepository)) {
            return canAccessProject(inboxEntry,
                                    user);
        }
        return false;
    }

    private boolean canAccessProject(InboxEntry inboxEntry,
                                     User user) {
        Project project = getInboxEntryProject(inboxEntry);
        if (thereIsNoProject(project)) {
            return true;
        } else {
            return authorizationManager.authorize(project,
                                                  user);
        }
    }

    private boolean thereIsNoProject(Project project) {
        return project == null;
    }

    private boolean canAccessRepository(Set<Repository> authorizedRepositories,
                                        Repository inboxEntryRepository) {
        return authorizedRepositories.contains(inboxEntryRepository);
    }

    Project getInboxEntryProject(final InboxEntry inboxEntry) {
        final Path path = Paths.get(inboxEntry.getItemPath());
        final org.uberfire.backend.vfs.Path vfsPath = org.uberfire.backend.server.util.Paths.convert(path);
        return projectService.resolveProject(vfsPath);
    }

    private boolean thereIsNoRepositoryAssociated(Repository inboxEntryRepository) {
        return inboxEntryRepository == null;
    }

    Repository getInboxEntryRepository(InboxEntry inboxEntry) {
        try {

            final Path path = Paths.get(inboxEntry.getItemPath());
            final FileSystem fileSystem = path.getFileSystem();
            return configuredRepositories.getRepositoryByRepositoryFileSystem(fileSystem);
        } catch (FileSystemNotFoundException exception) {
            return null;
        }
    }

    private Set<Repository> getAuthorizedRepositories(User user) {
        final Set<Repository> authorizedRepos = new HashSet<Repository>();
        for (OrganizationalUnit ou : getAuthorizedOrganizationUnits(user)) {
            final Collection<Repository> repositories = ou.getRepositories();
            for (final Repository repository : repositories) {
                if (authorizationManager.authorize(repository,
                                                   user)) {
                    authorizedRepos.add(repository);
                }
            }
        }
        return authorizedRepos;
    }

    private Collection<OrganizationalUnit> getAuthorizedOrganizationUnits(User user) {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Collection<OrganizationalUnit> authorizedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        for (OrganizationalUnit ou : organizationalUnits) {
            if (authorizationManager.authorize(ou,
                                               user)) {
                authorizedOrganizationalUnits.add(ou);
            }
        }
        return authorizedOrganizationalUnits;
    }
}
