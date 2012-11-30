package org.kie.guvnor.backend.server.repositories;

import javax.enterprise.context.ApplicationScoped;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.service.GitRepository;
import org.kie.guvnor.service.Repository;

/**
 * Factory for Repository implementations
 */
@ApplicationScoped
public class RepositoryFactory {

    private static final String SCHEME_GIT = "git";

    public Repository makeRepository( final String alias,
                                      final String scheme ) {
        PortablePreconditions.checkNotNull( "alias",
                                            alias );
        PortablePreconditions.checkNotNull( "scheme",
                                            scheme );
        if ( SCHEME_GIT.equals( scheme ) ) {
            return new GitRepository( alias );
        }
        throw new IllegalArgumentException( "Unrecognized scheme '" + scheme + "'." );
    }

}
