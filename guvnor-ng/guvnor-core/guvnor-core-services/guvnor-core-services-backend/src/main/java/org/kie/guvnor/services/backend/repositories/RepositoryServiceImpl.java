package org.kie.guvnor.services.backend.repositories;

import org.kie.guvnor.services.config.AppConfigService;
import org.kie.guvnor.services.repositories.Repository;
import org.kie.guvnor.services.repositories.RepositoryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Server-side utilities to manipulate repository information
 */
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    private final String PREFERENCE_REPOSITORY = "repository";
    private final String PREFERENCE_BOOTSTRAP  = "bootstrap";
    private final String PREFERENCE_SCHEME     = "scheme";

    private List<Repository> repositories = null;

    @Inject
    private AppConfigService appConfigService;

    @Inject
    private RepositoryFactory repositoryFactory;

    @Override
    public List<Repository> getRepositories() {
        if ( this.repositories == null ) {
            initialiseRepositories();
        }
        return Collections.unmodifiableList( this.repositories );
    }

    private void initialiseRepositories() {
        this.repositories = new LinkedList<Repository>();
        final Map<String, Map<String, String>> repositoriesParameters = new HashMap<String, Map<String, String>>();
        final Map<String, String> preferences = appConfigService.loadPreferences();

        //Collate repository definitions
        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();
            if ( key.startsWith( PREFERENCE_REPOSITORY ) ) {
                final String repositoryAlias = getRepositoryAlias( key );
                if ( repositoryAlias != null ) {
                    Map<String, String> repositoryParameters = repositoriesParameters.get( repositoryAlias );
                    if ( repositoryParameters == null ) {
                        repositoryParameters = new HashMap<String, String>();
                        repositoriesParameters.put( repositoryAlias,
                                                    repositoryParameters );
                    }
                    final String repositoryParameterName = getRepositoryParameterName( key );
                    if ( repositoryParameterName != null ) {
                        repositoryParameters.put( repositoryParameterName,
                                                  value );
                    }
                }
            }
        }

        //Create Repository object for each repository
        for ( Map.Entry<String, Map<String, String>> repositoryDefinition : repositoriesParameters.entrySet() ) {
            final String alias = repositoryDefinition.getKey();
            final Map<String, String> repositoryParameters = repositoryDefinition.getValue();
            final String scheme = getRepositoryScheme( repositoryParameters );
            if ( scheme != null ) {
                final Repository repository = repositoryFactory.makeRepository( alias,
                                                                                scheme );
                repositories.add( repository );
                for ( Map.Entry<String, String> parameter : repositoryParameters.entrySet() ) {
                    final String key = parameter.getKey();
                    final String value = parameter.getValue();
                    if ( PREFERENCE_BOOTSTRAP.equals( key ) ) {
                        repository.setBootstrap( Boolean.parseBoolean( value ) );
                    } else if ( PREFERENCE_SCHEME.equals( key ) ) {
                        //Do nothing
                    } else if ( value != null ) {
                        repository.addEnvironmentParameter( key,
                                                            value );
                    }
                }
            }
        }
    }

    private String getRepositoryScheme( final Map<String, String> parameters ) {
        for ( Map.Entry<String, String> parameter : parameters.entrySet() ) {
            if ( PREFERENCE_SCHEME.equals( parameter.getKey() ) ) {
                return parameter.getValue();
            }
        }
        return null;
    }

    private String getRepositoryAlias( String value ) {
        int dotIndex = value.indexOf( "." );
        if ( dotIndex < 0 ) {
            return null;
        }
        value = value.substring( dotIndex + 1 );
        dotIndex = value.indexOf( "." );
        if ( dotIndex < 0 ) {
            return value;
        }
        return value.substring( 0, dotIndex );
    }

    private String getRepositoryParameterName( final String value ) {
        int dotIndex = value.lastIndexOf( "." );
        if ( dotIndex < 0 ) {
            return null;
        }
        return value.substring( dotIndex + 1 );
    }

}
