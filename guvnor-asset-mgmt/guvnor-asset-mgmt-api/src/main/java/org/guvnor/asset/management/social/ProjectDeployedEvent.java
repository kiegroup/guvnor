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
