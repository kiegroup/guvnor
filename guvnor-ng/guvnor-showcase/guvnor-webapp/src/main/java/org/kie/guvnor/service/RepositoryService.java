package org.kie.guvnor.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service to manage repositories.
 */
@Remote
public interface RepositoryService {

    List<Repository> getRepositories();

    void addRepository( final Repository repository );

    Repository getRepository( final String alias );

    void removeRepository( final String alias );

}
