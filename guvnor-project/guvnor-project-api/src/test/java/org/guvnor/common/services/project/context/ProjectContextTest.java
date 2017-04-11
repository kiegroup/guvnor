/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.context;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContextTest {

    @Spy
    private EventSourceMock<ProjectContextChangeEvent> changeEvent = new EventSourceMock<ProjectContextChangeEvent>();

    private ProjectContext context;

    @Before
    public void setUp() throws Exception {
        context = new ProjectContext(changeEvent);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                context.onProjectContextChanged((ProjectContextChangeEvent) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(changeEvent).fire(any(ProjectContextChangeEvent.class));
    }

    @Test
    public void testGetActiveRepositoryRoot() throws Exception {

        final Path devRoot = mock(Path.class);

        context.setActiveProject(new Project(mock(OrganizationalUnit.class),
                                             mock(Repository.class),
                                             new Branch("dev",
                                                        devRoot),
                                             mock(Module.class)));

        assertEquals(devRoot,
                     context.getActiveRepositoryRoot());
    }

    @Test
    public void testRepositoryDeleted() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repository = mock(Repository.class);

        doReturn("myrepo").when(repository).getAlias();

        context.setActiveOrganizationalUnit(organizationalUnit);
        context.setActiveProject(new Project(organizationalUnit,
                                             repository,
                                             mock(Branch.class),
                                             mock(Module.class)));

        assertNotNull(context.getActiveProject());

        final RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent(repository);
        context.onRepositoryRemoved(repositoryRemovedEvent);

        assertEquals(organizationalUnit,
                     context.getActiveOrganizationalUnit());
        assertNull(context.getActiveProject());
    }

    @Test
    public void testRepositoryDeletedNoActiveProject() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        context.setActiveOrganizationalUnit(organizationalUnit);
        context.setActiveProject(null);

        context.onRepositoryRemoved(new RepositoryRemovedEvent(new GitRepository()));

        assertEquals(organizationalUnit,
                     context.getActiveOrganizationalUnit());
        assertNull(context.getActiveProject());
    }

    @Test
    public void testIgnoreRepositoryDeletedEventIfTheActiveRepositoryWasNotDeleted() throws Exception {

        GitRepository deletedRepository = new GitRepository("deleted repo");

        final Project activeProject = new Project(mock(OrganizationalUnit.class),
                                                  new GitRepository("active repo"),
                                                  mock(Branch.class),
                                                  mock(Module.class));
        context.setActiveProject(activeProject);

        final RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent(deletedRepository);

        context.onRepositoryRemoved(repositoryRemovedEvent);

        assertEquals(activeProject,
                     context.getActiveProject());
    }

    @Test
    public void testContextChanged() throws Exception {
        final OrganizationalUnit oldOrganizationalUnit = mock(OrganizationalUnit.class);
        final Repository oldRepository = mock(Repository.class);
        final Package oldPackage = new Package();
        final Module oldModule = new Module();

        context.setActiveOrganizationalUnit(oldOrganizationalUnit);
        context.setActiveProject(new Project(oldOrganizationalUnit,
                                             oldRepository,
                                             mock(Branch.class),
                                             mock(Module.class)));
        context.setActivePackage(oldPackage);
        context.setActiveModule(oldModule);

        final OrganizationalUnit newOrganizationalUnit = mock(OrganizationalUnit.class);
        final Branch newBranch = new Branch("master",
                                            mock(Path.class));
        final Package newPackage = new Package();
        final Module newModule = new Module();

        final ProjectContextChangeHandler changeHandler = mock(ProjectContextChangeHandler.class);
        context.addChangeHandler(changeHandler);

        final Project newProject = new Project(newOrganizationalUnit,
                                               mock(Repository.class),
                                               newBranch,
                                               mock(Module.class));
        context.onProjectContextChanged(new ProjectContextChangeEvent(newProject,
                                                                      newModule,
                                                                      newPackage));

        assertEquals(newOrganizationalUnit,
                     context.getActiveOrganizationalUnit());
        assertEquals(newProject,
                     context.getActiveProject());
        assertEquals(newModule,
                     context.getActiveModule());
        assertEquals(newPackage,
                     context.getActivePackage());
        verify(changeHandler).onChange();
    }

    @Test
    public void testContextChangeHandlerGetsRemoved() throws Exception {
        ProjectContextChangeHandler changeHandler = mock(ProjectContextChangeHandler.class);
        ProjectContextChangeHandle handle = context.addChangeHandler(changeHandler);

        context.onProjectContextChanged(new ProjectContextChangeEvent());

        verify(changeHandler).onChange();

        context.removeChangeHandler(handle);

        reset(changeHandler);

        context.onProjectContextChanged(new ProjectContextChangeEvent());

        verify(changeHandler,
               never()).onChange();
    }
}