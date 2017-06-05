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

package org.guvnor.ala.build.maven.model.impl;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBuildResult;
import org.uberfire.java.nio.file.Path;

/**
 * This class model the information for a Binary produced by a Maven Build process by adding the ability of querying
 * the build messages produced the build.
 * @see MavenBuildResult
 */
public class MavenProjectBinaryBuildImpl
        extends MavenProjectBinaryImpl {

    private MavenBuildResult mavenBuildResult;

    public MavenProjectBinaryBuildImpl( final Path path,
                                        final Project sourceProject,
                                        final String groupId,
                                        final String artifactId,
                                        final String version,
                                        final MavenBuildResult mavenBuildResult ) {
        super( path, sourceProject, groupId, artifactId, version );
        this.mavenBuildResult = mavenBuildResult;
    }

    public MavenProjectBinaryBuildImpl( MavenBuildResult mavenBuildResult ) {
        this.mavenBuildResult = mavenBuildResult;
    }

    public MavenBuildResult getMavenBuildResult( ) {
        return mavenBuildResult;
    }

}