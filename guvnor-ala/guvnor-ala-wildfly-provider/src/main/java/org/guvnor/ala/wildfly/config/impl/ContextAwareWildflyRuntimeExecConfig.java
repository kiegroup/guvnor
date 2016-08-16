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

package org.guvnor.ala.wildfly.config.impl;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;

public class ContextAwareWildflyRuntimeExecConfig implements
                                                  ContextAware,
                                                  WildflyRuntimeExecConfig,
                                                  CloneableConfig<WildflyRuntimeExecConfig> {

    @JsonIgnore
    private Map<String, ?> context;
    private ProviderId providerId;
    private String warPath;
    private final Map<Project, RepositoryVisitor> projectVisitorMap = new HashMap<>();

    public ContextAwareWildflyRuntimeExecConfig() {
    }

    public ContextAwareWildflyRuntimeExecConfig( final ProviderId providerId,
                                                 final String warPath ) {
        this.providerId = providerId;
        this.warPath = warPath;
    }

    @Override
    public void setContext( final Map<String, ?> context ) {
        this.context = context;
        MavenBinary binary = (MavenBinary) context.get( "binary" );
        String binaryPath = binary.getProject().getExpectedBinary();

        String projectPath = getRepositoryVisitor( binary.getProject() ).getProjectFolder().getAbsolutePath();
        WildflyProvider provider = (WildflyProvider) context.get( "wildfly-provider" );
        this.providerId = provider;
        this.warPath = projectPath + "/target/" + binaryPath;

    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public String getWarPath() {
        return warPath;
    }

    private RepositoryVisitor getRepositoryVisitor( final Project project ) {
        final RepositoryVisitor projectVisitor;
        if ( projectVisitorMap.containsKey( project ) ) {
            projectVisitor = projectVisitorMap.get( project );
        } else {
            projectVisitor = new RepositoryVisitor( project );
            projectVisitorMap.put( project, projectVisitor );
        }
        return projectVisitor;
    }

    @Override
    public WildflyRuntimeExecConfig asNewClone( final WildflyRuntimeExecConfig origin ) {
        return new ContextAwareWildflyRuntimeExecConfig( origin.getProviderId(),
                                                         origin.getWarPath() );
    }
}