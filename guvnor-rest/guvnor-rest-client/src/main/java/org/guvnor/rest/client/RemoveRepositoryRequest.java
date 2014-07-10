package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoveRepositoryRequest extends JobRequest {
    
    private String repositoryName;

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}


}
