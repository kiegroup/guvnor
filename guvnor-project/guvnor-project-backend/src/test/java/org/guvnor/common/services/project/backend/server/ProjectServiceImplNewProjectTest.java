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

import java.util.Optional;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplNewProjectTest {

    ProjectService projectService;

    @Mock
    RepositoryService repositoryService;
    @Mock
    Instance<ModuleService<? extends Module>> moduleServices;

    @Mock
    ModuleService moduleService;

    @Mock
    private OrganizationalUnit ou;

    @Mock
    private Repository repository;

    @Mock
    private Path repositoryRoot;

    @Mock
    private Branch branch;

    @Mock
    private EventSourceMock<NewProjectEvent> newProjectEvent;

    @Mock
    private Module module;
    private POM pom;

    @Before
    public void setUp() throws Exception {

        doReturn(Optional.of(branch)).when(repository).getDefaultBranch();
        doReturn(repositoryRoot).when(repository).getRoot();

        doReturn(repository).when(repositoryService).createRepository(eq(ou),
                                                                      eq("git"),
                                                                      eq("my project"),
                                                                      any(RepositoryEnvironmentConfigurations.class));

        pom = new POM("my project",
                      "my description",
                      new GAV("groupId",
                              "artifactId",
                              "version"));

        doReturn(moduleService).when(moduleServices).get();

        projectService = new ProjectServiceImpl(mock(OrganizationalUnitService.class),
                                                repositoryService,
                                                newProjectEvent,
                                                moduleServices,
                                                mock(User.class),
                                                mock(AuthorizationManager.class));
    }

    @Test
    public void newProjectDefault() throws Exception {

        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(""),
                                                       eq(DeploymentMode.VALIDATED));

        final Project project = projectService.newProject(ou,
                                                          pom);
        assertProject(project);
        verify(newProjectEvent).fire(any());
    }

    @Test
    public void newProjectValidated() throws Exception {

        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(""),
                                                       eq(DeploymentMode.VALIDATED));

        final Project project = projectService.newProject(ou,
                                                          pom,
                                                          DeploymentMode.VALIDATED);
        assertProject(project);
        verify(newProjectEvent).fire(any());
    }

    @Test
    public void newProjectForced() throws Exception {
        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(""),
                                                       eq(DeploymentMode.FORCED));

        final Project project = projectService.newProject(ou,
                                                          pom,
                                                          DeploymentMode.FORCED);
        assertProject(project);
        verify(newProjectEvent).fire(any());
    }

    private void assertProject(final Project project) {
        assertEquals(ou,
                     project.getOrganizationalUnit());
        assertEquals(repository,
                     project.getRepository());
        assertEquals(branch,
                     project.getBranch());
        assertEquals(module,
                     project.getMainModule());
    }
}