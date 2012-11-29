package org.kie.guvnor.backend.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.guvnor.service.ConfigurationService;

/**
 * Utilities to manipulate repository information
 */
@ApplicationScoped
public class RepositoryUtils {

    private final String PREFERENCE_REPOSITORY = "repository";
    private final String PREFERENCE_ALIAS = "alias";
    private final String PREFERENCE_URL = "url";
    private final String PREFERENCE_USERNAME = "username";
    private final String PREFERENCE_PASSWORD = "password";
    private final String PREFERENCE_BOOTSTRAP = "bootstrap";

    @Inject
    private ConfigurationService configService;

    public Collection<Repository> getRepositories() {
        final Map<String, Repository> repositories = new HashMap<String, Repository>();
        final Map<String, String> preferences = configService.loadPreferences();
        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();
            if ( key.startsWith( PREFERENCE_REPOSITORY ) ) {
                final String preferenceAlias = getPreferenceAlias( key );
                if ( preferenceAlias != null ) {
                    Repository repository = repositories.get( preferenceAlias );
                    if ( repository == null ) {
                        repository = new Repository();
                        repositories.put( preferenceAlias,
                                          repository );
                    }
                    final String preferenceKeyLeaf = getPreferenceKeyLeaf( key );
                    if ( PREFERENCE_ALIAS.equals( preferenceKeyLeaf ) ) {
                        repository.setAlias( value );
                    } else if ( PREFERENCE_URL.equals( preferenceKeyLeaf ) ) {
                        repository.setUrl( value );
                    } else if ( PREFERENCE_USERNAME.equals( preferenceKeyLeaf ) ) {
                        repository.setUserName( value );
                    } else if ( PREFERENCE_PASSWORD.equals( preferenceKeyLeaf ) ) {
                        repository.setPassword( value );
                    } else if ( PREFERENCE_BOOTSTRAP.equals( preferenceKeyLeaf ) ) {
                        repository.setBootstrap( Boolean.valueOf( value ) );
                    }
                }
            }
        }
        return repositories.values();
    }

    private String getPreferenceAlias( String value ) {
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

    private String getPreferenceKeyLeaf( final String value ) {
        int dotIndex = value.lastIndexOf( "." );
        if ( dotIndex < 0 ) {
            return null;
        }
        return value.substring( dotIndex + 1 );
    }

    public boolean isRepositoryDefinitionValid( final Repository repository ) {
        return repository.alias != null &&
                repository.url != null &&
                repository.userName != null &&
                repository.password != null;
    }

    public static class Repository {

        private String alias;
        private String url;
        private String userName;
        private String password;
        private boolean bootstrap;

        public String getAlias() {
            return alias;
        }

        public void setAlias( final String alias ) {
            this.alias = alias;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( final String url ) {
            this.url = url;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName( final String userName ) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword( final String password ) {
            this.password = password;
        }

        public boolean isBootstrap() {
            return bootstrap;
        }

        public void setBootstrap( final boolean bootstrap ) {
            this.bootstrap = bootstrap;
        }
    }

}
