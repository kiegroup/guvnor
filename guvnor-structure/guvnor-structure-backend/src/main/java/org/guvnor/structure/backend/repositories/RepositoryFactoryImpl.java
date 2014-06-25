package org.guvnor.structure.backend.repositories;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;

import static org.guvnor.structure.backend.repositories.SystemRepository.*;
import static org.uberfire.commons.validation.Preconditions.*;

@ApplicationScoped
public class RepositoryFactoryImpl implements RepositoryFactory {

    @Inject
    private PasswordService secureService;

    @Inject
    @Any
    private Instance<RepositoryFactoryHelper> helpers;

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );

        //Find a Helper that can create a repository
        Repository repository = null;
        for ( RepositoryFactoryHelper helper : helpers ) {
            if ( helper.accept( repoConfig ) ) {
                repository = helper.newRepository( repoConfig );
                break;
            }
        }

        //Check one was created
        if ( repository == null ) {
            throw new IllegalArgumentException( "Unrecognized scheme '" + schemeConfigItem.getValue() + "'." );
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> roles = repoConfig.getConfigItem( "security:roles" );
        if ( roles != null ) {
            for ( String role : roles.getValue() ) {
                repository.getRoles().add( role );
            }
        }

        return repository;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SYSTEM_REPO;
    }

}
