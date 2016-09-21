/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.build.maven.model.impl;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.config.CloneableConfig;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class MavenBinaryImpl implements MavenBinary,
                                        CloneableConfig<MavenBinary> {

    private final String type;
    private final Project sourceProject;

    public MavenBinaryImpl( final Project sourceProject ) {
        this.type = "Maven";
        this.sourceProject = checkNotNull( "sourceProject", sourceProject );
    }

    @Override
    public Project getProject() {
        return sourceProject;
    }

    @Override
    public Path getPath() {
        return sourceProject.getPath();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return sourceProject.getExpectedBinary();
    }

    @Override
    public MavenBinary asNewClone( final MavenBinary source ) {
        return new MavenBinaryImpl( source.getProject() );
    }
}