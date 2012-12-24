package org.kie.guvnor.services.backend.repositories;

import javax.enterprise.context.ApplicationScoped;

import org.kie.guvnor.services.repositories.GitRepository;
import org.kie.guvnor.services.repositories.Repository;

import static org.kie.commons.validation.Preconditions.*;

/**
 * Factory for Repository implementations
 */
@ApplicationScoped
public class RepositoryFactory {

    private static final String SCHEME_GIT = "git";

    public Repository makeRepository( final String alias,
                                      final String scheme ) {
        checkNotNull( "alias",
                      alias );
        checkNotNull( "scheme",
                      scheme );
        if ( SCHEME_GIT.equals( scheme ) ) {
            return new GitRepository( alias );
        }
        throw new IllegalArgumentException( "Unrecognized scheme '" + scheme + "'." );
    }

}
