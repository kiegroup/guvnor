/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.inject.Inject;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.model.impl.MavenBinaryImpl;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.registry.BuildRegistry;

import static org.guvnor.ala.build.maven.util.MavenBuildExecutor.*;

public class MavenBuildExecConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, MavenBuildExecConfig, BinaryConfig> {

    private final BuildRegistry buildRegistry;

    @Inject
    public MavenBuildExecConfigExecutor( final BuildRegistry buildRegistry ) {
        this.buildRegistry = buildRegistry;
    }

    @Override
    public Optional<BinaryConfig> apply( final MavenBuild mavenBuild,
            final MavenBuildExecConfig mavenBuildExecConfig ) {

        build( mavenBuild.getProject(), mavenBuild.getGoals(), mavenBuild.getProperties() );
        final MavenBinary binary = new MavenBinaryImpl( mavenBuild.getProject() );
        buildRegistry.registerBinary( binary );
        return Optional.of( binary );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenBuildExecConfig.class;
    }

    @Override
    public String outputId() {
        return "binary";
    }

    @Override
    public String inputId() {
        return "maven-exec-config";
    }

    public void build( final Project project,
            final List<String> goals,
            final Properties properties ) throws BuildException {
        final File pom = new File( project.getTempDir(), "pom.xml" );
        executeMaven( pom, properties, goals.toArray( new String[]{} ) );
    }

}
