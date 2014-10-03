/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.asset.management.social;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectDeployedEvent extends AssetManagementEvent {

    private String projectName;

    private String groupId;

    private String artifactId;

    private String version;

    private String executionServer;

    private DeployType deployType;

    public enum DeployType { MAVEN, RUNTIME };

    public ProjectDeployedEvent() {
    }

    public ProjectDeployedEvent( String processName, String repositoryAlias, String rootURI, String user, Long timestamp ) {
        super( processName, repositoryAlias, rootURI, user, timestamp );
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName( String projectName ) {
        this.projectName = projectName;
    }

    public String getExecutionServer() {
        return executionServer;
    }

    public void setExecutionServer( String executionServer ) {
        this.executionServer = executionServer;
    }

    public DeployType getDeployType() {
        return deployType;
    }

    public void setDeployType( DeployType deployType ) {
        this.deployType = deployType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId( String groupId ) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId( String artifactId ) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }
}
