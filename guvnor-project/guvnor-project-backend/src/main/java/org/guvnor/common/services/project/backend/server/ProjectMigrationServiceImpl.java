/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.project.ProjectMigrationService;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class ProjectMigrationServiceImpl
        implements ProjectMigrationService {

    private ProjectService projectService;
    private RepositoryService repositoryService;
    private OrganizationalUnitService organizationalUnitService;
    private Event<NewProjectEvent> newProjectEvent;
    private RepositoryCopier repositoryCopier;
    private ModuleService<? extends Module> moduleService;
    private IOService ioService;

    public ProjectMigrationServiceImpl() {
    }

    @Inject
    public ProjectMigrationServiceImpl(final ProjectService projectService,
                                       final RepositoryService repositoryService,
                                       final OrganizationalUnitService organizationalUnitService,
                                       final Event<NewProjectEvent> newProjectEvent,
                                       final RepositoryCopier repositoryCopier,
                                       final ModuleService<? extends Module> moduleService,
                                       final @Named("ioStrategy") IOService ioService) {
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.newProjectEvent = newProjectEvent;
        this.repositoryCopier = repositoryCopier;
        this.moduleService = moduleService;
        this.ioService = ioService;
    }

    @Override
    public void migrate(final Project legacyProject) {

        final Map<String, Repository> newRepositories = new HashMap<>();

        // TODO: What to do if the old repo did not have OU?

        // TODO Migrate all branches. repository.getBranches()

        for (final Branch branch : legacyProject.getRepository().getBranches()) {

            final Set<Module> modules = moduleService.getAllModules(branch);

            for (final Module module : modules) {

                // TODO: Get all the OUs this repository is in.

                if (!newRepositories.containsKey(module.getModuleName())) {

                    final Repository repository = repositoryService.createRepository(legacyProject.getOrganizationalUnit(),
                                                                                     GitRepository.SCHEME,
                                                                                     repositoryCopier.makeSafeRepositoryName(module.getModuleName()),
                                                                                     new RepositoryEnvironmentConfigurations());

                    newRepositories.put(module.getModuleName(),
                                        repository);
                }

                // TODO: If legacy did not contain master. Kill it from the new one.

                final Repository targetRepository = newRepositories.get(module.getModuleName());

                URI uri = URI.create("default://" + branch.getName() + "@" + targetRepository.getAlias());
                final Path targetBranchRoot = ioService.get(uri);

                repositoryCopier.copy(module.getRootPath(),
                                      Paths.convert(targetBranchRoot));
            }
        }

        for (final OrganizationalUnit organizationalUnit : organizationalUnitService.getOrganizationalUnits(legacyProject.getRepository())) {
            if (!legacyProject.getOrganizationalUnit().equals(organizationalUnit)) {
                for (final Repository repository : newRepositories.values()) {
                    organizationalUnitService.addRepository(organizationalUnit,
                                                            repository);
                }
            }
        }

        for (final Repository repository : newRepositories.values()) {
            final Project newProject = projectService.resolveProject(repository);
            newProjectEvent.fire(new NewProjectEvent(newProject));
        }
    }
}
