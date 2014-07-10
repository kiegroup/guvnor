package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CreateOrCloneRepositoryRequest extends JobRequest {
    
    private RepositoryRequest repository;

    public RepositoryRequest getRepository() {
        return repository;
    }

    public void setRepository(RepositoryRequest repository) {
        this.repository = repository;
    }   

}
