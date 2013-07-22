package org.guvnor.common.services.project.builder.model;

import org.guvnor.common.services.shared.builder.BuildResults;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DeployResult {

    private String groupId;
    private String artifactId;
    private String version;

    private BuildResults buildResults;
    public DeployResult() {

    }

    public DeployResult(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

	public BuildResults getBuildResults() {
		return buildResults;
	}

	public void setBuildResults(BuildResults buildResults) {
		this.buildResults = buildResults;
	}    
    
}
