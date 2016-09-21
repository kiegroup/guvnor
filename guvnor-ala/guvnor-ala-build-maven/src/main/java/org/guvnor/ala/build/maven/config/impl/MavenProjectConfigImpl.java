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

package org.guvnor.ala.build.maven.config.impl;

import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.config.CloneableConfig;

public class MavenProjectConfigImpl implements MavenProjectConfig,
                                               CloneableConfig<MavenProjectConfig> {

    private final String projectDir;

    public MavenProjectConfigImpl() {
        this.projectDir = MavenProjectConfig.super.getProjectDir();
    }

    public MavenProjectConfigImpl( final String projectDir ) {
        this.projectDir = projectDir;
    }

    @Override
    public String getProjectDir() {
        return projectDir;
    }

    @Override
    public String toString() {
        return "MavenProjectConfigImpl{" + "projectDir=" + projectDir + '}';
    }

    @Override
    public MavenProjectConfig asNewClone( final MavenProjectConfig source ) {
        return new MavenProjectConfigImpl( source.getProjectDir() );
    }
}