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

package org.guvnor.ala.docker.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.ProvisioningConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.config.SourceConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryBuildRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistry;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.guvnor.ala.pipeline.StageUtil.config;
import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;
import static org.junit.Assert.*;

/**
 * Simple test using the Pipeline API and the docker Provider & Executors
 */
public class DockerExecutorTest {

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory("xxx").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void testAPI() throws InterruptedException {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Stage<Input, SourceConfig> sourceConfig = config("Git Source",
                                                               (s) -> new GitConfigImpl());
        final Stage<SourceConfig, ProjectConfig> projectConfig = config("Maven Project",
                                                                        (s) -> new MavenProjectConfigImpl());
        final Stage<ProjectConfig, BuildConfig> buildConfig = config("Maven Build Config",
                                                                     (s) -> new MavenBuildConfigImpl());
        final Stage<BuildConfig, BuildConfig> dockerBuildConfig = config("Docker Build Config",
                                                                         (s) -> new DockerBuildConfigImpl());
        final Stage<BuildConfig, BinaryConfig> buildExec = config("Maven Build",
                                                                  (s) -> new MavenBuildExecConfigImpl());
        final Stage<BinaryConfig, ProviderConfig> providerConfig = config("Docker Provider Config",
                                                                          (s) -> new DockerProviderConfigImpl());

        final Stage<ProviderConfig, ProvisioningConfig> runtimeConfig = config("Docker Runtime Config",
                                                                               (s) -> new ContextAwareDockerProvisioningConfig());

        final Stage<ProvisioningConfig, RuntimeConfig> runtimeExec = config("Docker Runtime Exec",
                                                                            (s) -> new ContextAwareDockerRuntimeExecConfig());

        final Pipeline pipe = PipelineFactory
                .startFrom(sourceConfig)
                .andThen(projectConfig)
                .andThen(buildConfig)
                .andThen(dockerBuildConfig)
                .andThen(buildExec)
                .andThen(providerConfig)
                .andThen(runtimeConfig)
                .andThen(runtimeExec).buildAs("my pipe");

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor(runtimeRegistry,
                                                                                            dockerAccessInterface);

        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry),
                                                                      new DockerBuildConfigExecutor(),
                                                                      new DockerProviderConfigExecutor(runtimeRegistry),
                                                                      new DockerProvisioningConfigExecutor(),
                                                                      dockerRuntimeExecExecutor));

        executor.execute(new Input() {
                             {
                                 put("repo-name",
                                     "drools-workshop");
                                 put("create-repo",
                                     "true");
                                 put("branch",
                                     "master");
                                 put("out-dir",
                                     tempPath.getAbsolutePath());
                                 put("origin",
                                     "https://github.com/kiegroup/drools-workshop");
                                 put("project-dir",
                                     "drools-webapp-example");
                             }
                         },
                         pipe,
                         (Runtime b) -> System.out.println(b));

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof DockerRuntime);

        DockerRuntime dockerRuntime = (DockerRuntime) runtime;

        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry,
                                                                       dockerAccessInterface);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(RUNNING,
                     dockerRuntime.getState().getState());

        runtimeManager.stop(dockerRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(STOPPED,
                     dockerRuntime.getState().getState());

        dockerRuntimeExecExecutor.destroy(runtime);

        dockerAccessInterface.dispose();
    }

    @Test
    public void testFlexAPI() throws InterruptedException {
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Stage<Input, ProviderConfig> providerConfig = config("Docker Provider Config",
                                                                   (s) -> new DockerProviderConfig() {
                                                                   });

        final Stage<ProviderConfig, ProvisioningConfig> runtimeConfig = config("Docker Runtime Config",
                                                                               (s) -> new ContextAwareDockerProvisioningConfig() {
                                                                               });

        final Stage<ProvisioningConfig, RuntimeConfig> runtimeExec = config("Docker Runtime Exec",
                                                                            (s) -> new ContextAwareDockerRuntimeExecConfig());

        final Pipeline pipe = PipelineFactory
                .startFrom(providerConfig)
                .andThen(runtimeConfig)
                .andThen(runtimeExec).buildAs("my pipe");

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor(runtimeRegistry,
                                                                                            dockerAccessInterface);
        final PipelineExecutor executor = new PipelineExecutor(asList(new DockerProviderConfigExecutor(runtimeRegistry),
                                                                      new DockerProvisioningConfigExecutor(),
                                                                      dockerRuntimeExecExecutor));
        executor.execute(new Input() {
                             {
                                 put("image-name",
                                     "kitematic/hello-world-nginx");
                                 put("port-number",
                                     "8080");
                                 put("docker-pull",
                                     "true");
                             }
                         },
                         pipe,
                         (Runtime b) -> System.out.println(b));

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof DockerRuntime);

        DockerRuntime dockerRuntime = (DockerRuntime) runtime;

        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry,
                                                                       dockerAccessInterface);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(RUNNING,
                     dockerRuntime.getState().getState());

        runtimeManager.stop(dockerRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(STOPPED,
                     dockerRuntime.getState().getState());

        dockerRuntimeExecExecutor.destroy(runtime);

        dockerAccessInterface.dispose();
    }
}
