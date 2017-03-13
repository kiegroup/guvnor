/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryFactory;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JarPathTest {

    private GuvnorM2Repository repository;

    @Before
    public void setupGuvnorM2Repository() {
        ArtifactRepositoryPreference pref = mock(ArtifactRepositoryPreference.class);
        when(pref.getGlobalM2RepoDir()).thenReturn("repositories/kie");
        when(pref.isGlobalM2RepoDirEnabled()).thenReturn(true);
        when(pref.isDistributionManagementM2RepoDirEnabled()).thenReturn(true);
        when(pref.isWorkspaceM2RepoDirEnabled()).thenReturn(false);
        WorkspaceNameResolver resolver = mock(WorkspaceNameResolver.class);
        when(resolver.getWorkspaceName()).thenReturn("global");
        ArtifactRepositoryFactory factory = new ArtifactRepositoryFactory(pref,
                                                                          resolver);
        factory.initialize();
        repository = new GuvnorM2Repository(factory);
        repository.init();
    }

    @Test
    public void testLinuxPathSeparators() {
        final M2RepoServiceImpl service = new M2RepoServiceImpl(this.repository);
        final String jarPath = service.getJarPath(repository.getM2RepositoryDir(ArtifactRepositoryFactory.GLOBAL_M2_REPO_NAME) + "/a/b/c",
                                                  "/");
        assertEquals("a/b/c",
                     jarPath);
    }

    @Test
    public void testWindowsPathSeparators() {
        final M2RepoServiceImpl service = new M2RepoServiceImpl(this.repository);
        final String jarPath = service.getJarPath(repository.getM2RepositoryDir(ArtifactRepositoryFactory.GLOBAL_M2_REPO_NAME) + "\\a\\b\\c",
                                                  "\\");
        assertEquals("a/b/c",
                     jarPath);
    }
}
