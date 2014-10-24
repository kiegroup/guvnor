package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CreateProjectRequest extends JobRequest {
    
	private String repositoryName;
	private String projectName;
	private String projectGroupId;
	private String projectVersion;
	private String description;
	
    public String getRepositoryName() {
        return repositoryName;
    }
    public void setRepositoryName( String repositoryName ) {
        this.repositoryName = repositoryName;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName( String projectName ) {
        this.projectName = projectName;
    }
    public String getProjectGroupId() {
        return projectGroupId;
    }
    public void setProjectGroupId( String projectGroupId ) {
        this.projectGroupId = projectGroupId;
    }
    public String getProjectVersion() {
        return projectVersion;
    }
    public void setProjectVersion( String projectVersion ) {
        this.projectVersion = projectVersion;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription( String description ) {
        this.description = description;
    }
    
}