/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.ui.model.InternalGitSource;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineInputBuilderTest {

    private static final String RUNTIME = "RUNTIME";

    private static final String PROVIDER = "PROVIDER";

    private static final String OU = "OU";

    private static final String REPO = "REPO";

    private static final String BRANCH = "BRANCH";

    private static final String PROJECT_NAME = "PROJECT_NAME";

    @Mock
    private Project project;

    @Mock
    private ProviderKey providerKey;

    private InternalGitSource gitSource;

    @Before
    public void setUp() {
        when(providerKey.getId()).thenReturn(PROVIDER);
        when(project.getProjectName()).thenReturn(PROJECT_NAME);

        gitSource = new InternalGitSource(OU,
                                          REPO,
                                          BRANCH,
                                          project);
    }

    @Test
    public void testBuild() {
        Input result = PipelineInputBuilder.newInstance()
                .withProvider(providerKey)
                .withRuntimeName(RUNTIME)
                .withSource(gitSource).build();

        assertNotNull(result);
        assertEquals(RUNTIME,
                     result.get(RuntimeConfig.RUNTIME_NAME));
        assertEquals(PROVIDER,
                     result.get(ProviderConfig.PROVIDER_NAME));
        assertEquals(REPO,
                     result.get(GitConfig.REPO_NAME));
        assertEquals(BRANCH,
                     result.get(GitConfig.BRANCH));
        assertEquals(PROJECT_NAME,
                     result.get(MavenProjectConfig.PROJECT_DIR));
    }
}
