/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.AuthorizationManager;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ProjectServiceImpl
        implements ProjectService {

    private OrganizationalUnitService organizationalUnitService;
    private RepositoryService repositoryService;
    private Event<NewProjectEvent> newProjectEvent;
    private ModuleService<? extends Module> moduleService;
    private User user;
    private AuthorizationManager authorizationManager;

    public ProjectServiceImpl() {
    }

    @Inject
    public ProjectServiceImpl(final OrganizationalUnitService organizationalUnitService,
                              final RepositoryService repositoryService,
                              final Event<NewProjectEvent> newProjectEvent,
                              final Instance<ModuleService<? extends Module>> moduleServices,
                              final User user,
                              final AuthorizationManager authorizationManager) {
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.newProjectEvent = newProjectEvent;
        moduleService = moduleServices.get();
        this.user = user;
        this.authorizationManager = authorizationManager;
    }

    @Override
    public Collection<Project> getAllProjects() {

        final List<Project> result = new ArrayList<>();

        for (final OrganizationalUnit ou : organizationalUnitService.getOrganizationalUnits()) {
            result.addAll(getAllProjects(ou));
        }

        return result;
    }

    @Override
    public Collection<Project> getAllProjects(final OrganizationalUnit organizationalUnit) {
        final List<Project> result = new ArrayList<>();

        for (final Repository repository : repositoryService.getRepositories()) {

            if (containsRepository(organizationalUnit,
                                   repository)) {

                result.add(new Project(organizationalUnit,
                                       repository,
                                       repository.getDefaultBranch().get(),
                                       moduleService.resolveModule(repository.getRoot())));
            }
        }

        return result;
    }

    private boolean containsRepository(final OrganizationalUnit organizationalUnit,
                                       final Repository repository) {
        for (final Repository ouRepository : organizationalUnitService.getOrganizationalUnit(organizationalUnit.getName()).getRepositories()) {
            if (ouRepository.getAlias().equals(repository.getAlias())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Project newProject(final OrganizationalUnit organizationalUnit,
                              final POM pom) {
        return newProject(organizationalUnit,
                          pom,
                          DeploymentMode.VALIDATED);
    }

    @Override
    public Project newProject(final OrganizationalUnit organizationalUnit,
                              final POM pom,
                              final DeploymentMode mode) {

        final Repository repository = repositoryService.createRepository(organizationalUnit,
                                                                         "git",
                                                                         checkNotNull("project name in pom model",
                                                                                                            pom.getName()),
                                                                         new RepositoryEnvironmentConfigurations());
        final Module module = moduleService.newModule(repository.getRoot(),
                                                      pom,
                                                      "",
                                                      mode);

        final Project project = new Project(organizationalUnit,
                                            repository,
                                            repository.getDefaultBranch().get(),
                                            module);

        newProjectEvent.fire(new NewProjectEvent(project));

        return project;
    }

    @Override
    public Project resolveProject(final Repository repository) {
        return resolveProject(repository.getRoot());
    }

    @Override
    public Project resolveProject(final Branch branch) {
        return resolveProject(branch.getPath());
    }

    @Override
    public Project resolveProject(final Module module) {
        return resolveProject(module.getRootPath());
    }

    @Override
    public Project resolveProject(final String name) {

        for (final Project project : getAllProjects()) {
            if (project.getName().equals(name)) {
                return project;
            }
        }

        return null;
    }

    @Override
    public Project resolveProjectByRepositoryAlias(String repositoryAlias) {

        // TODO: Test

        return resolveProject(repositoryService.getRepository(repositoryAlias));
    }

    @Override
    public Project resolveProject(final Path path) {

        final org.uberfire.java.nio.file.Path repositoryRoot = Paths.convert(path).getRoot();

        final Repository repository = repositoryService.getRepository(Paths.convert(repositoryRoot));

        final Branch branch = resolveBranch(repositoryRoot,
                                            repository);

        return new Project(organizationalUnitService.getParentOrganizationalUnit(repository),
                           repository,
                           branch,
                           moduleService.resolveModule(Paths.convert(Paths.convert(branch.getPath()).getRoot())));
    }

    private Branch resolveBranch(final org.uberfire.java.nio.file.Path repositoryRoot,
                                 final Repository repository) {

        final Branch defaultBranch = repository.getDefaultBranch().get();

        if (!Paths.convert(defaultBranch.getPath()).equals(repositoryRoot)) {

            for (final Branch branch : repository.getBranches()) {

                if (Paths.convert(branch.getPath()).equals(repositoryRoot)) {
                    return branch;
                }
            }
        }
        return defaultBranch;
    }
}
