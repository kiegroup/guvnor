/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.security;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.DateUtils;
import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.permission.RoleBasedPermissionResolver;

/**
 * This implements security related services.
 * @author Michael Neale
 */
public class SecurityServiceImpl
    implements
    SecurityService {

    public static final String       GUEST_LOGIN = "guest";
    private static final Logger      log         = LoggerFactory.getLogger( SecurityServiceImpl.class );
    static final Map<String, String> PREFERENCES = loadPrefs();
    private static String[] serializationProperties = new String[] {"drools.serialization.private.keyStoreURL", "drools.serialization.private.keyStorePwd",
    	"drools.serialization.private.keyAlias", "drools.serialization.private.keyPwd", "drools.serialization.public.keyStoreURL", "drools.serialization.public.keyStorePwd"};

    public boolean login(String userName,
                         String password) {  	

	if ( userName == null || userName.trim().equals( "" ) ) {
 		            userName = "admin";
 	}
 		
        log.info( "Logging in user [" + userName + "]" );
        if ( Contexts.isApplicationContextActive() ) {

            // Check for banned characters in user name
            // These will cause the session to jam if you let them go further
            char[] bannedChars = {'\'', '*', '[', ']'};
            for ( int i = 0; i < bannedChars.length; i++ ) {
                char c = bannedChars[i];
                if ( userName.indexOf( c ) >= 0 ) {
                    log.error( "Not a valid name character " + c );
                    return false;
                }
            }

            Identity.instance().getCredentials().setUsername( userName );
            Identity.instance().getCredentials().setPassword( password );

            try {
                Identity.instance().authenticate();
            } catch ( LoginException e ) {
                log.error( "Unable to login.", e );
                return false;
            }
            return Identity.instance().isLoggedIn();
        } else {
            return true;
        }

    }

    public UserSecurityContext getCurrentUser() {
        if ( Contexts.isApplicationContextActive() ) {
            if ( !Identity.instance().isLoggedIn() ) {
                //check to see if we can autologin
                return new UserSecurityContext( checkAutoLogin() );
            }
            return new UserSecurityContext( Identity.instance().getCredentials().getUsername() );
        } else {
//            HashSet<String> disabled = new HashSet<String>();
            //return new UserSecurityContext(null);
            return new UserSecurityContext( "SINGLE USER MODE (DEBUG) USE ONLY" );
        }
    }

    /**
     * This will return a auto login user name if it has been configured.
     * Autologin means that its not really logged in, but a generic username will be used.
     * Basically means security is bypassed.
     *
     */
    private String checkAutoLogin() {
        Identity id = Identity.instance();
        id.getCredentials().setUsername( GUEST_LOGIN );
        try {
            id.authenticate();
        } catch ( LoginException e ) {
            return null;
        }
        if ( id.isLoggedIn() ) {
            return id.getCredentials().getUsername();
        } else {
            return null;
        }

    }

    public Capabilities getUserCapabilities() {

        if ( Contexts.isApplicationContextActive() ) {
            if ( Identity.instance().hasRole( RoleTypes.ADMIN ) ) {
                return Capabilities.all( PREFERENCES );
            }
            
            RoleBasedPermissionResolver resolver = (RoleBasedPermissionResolver) Component.getInstance( "org.jboss.seam.security.roleBasedPermissionResolver" );
            if ( !resolver.isEnableRoleBasedAuthorization() ) {
                return Capabilities.all( PREFERENCES );
            }
            
            CapabilityCalculator c = new CapabilityCalculator();
            RoleBasedPermissionManager permManager = (RoleBasedPermissionManager) Component.getInstance( "roleBasedPermissionManager" );

            List<RoleBasedPermission> permissions = permManager.getRoleBasedPermission();
            if ( permissions.size() == 0 ) {
                    Identity.instance().logout();
                    throw new AuthorizationException( "This user has no permissions setup." );
            }
            
            if(invalidSecuritySerilizationSetup()) {
            	Identity.instance().logout();
                throw new AuthorizationException( " Configuration error - Please refer to the Administration Guide section on installation. You must configure a key store before proceding.  " );
            }
            return c.calcCapabilities( permissions,
                                       PREFERENCES );
        } else {
        	if(invalidSecuritySerilizationSetup()) {
                throw new AuthorizationException( " Configuration error - Please refer to the Administration Guide section on installation. You must configure a key store before proceding.  " );
            }
            return Capabilities.all( PREFERENCES );
        }
    }
    
    private boolean invalidSecuritySerilizationSetup() {
    	String ssecurity = System.getProperty("drools.serialization.sign");
    	if(ssecurity != null && ssecurity.equalsIgnoreCase( "true" )) {
    		for(String nextProp : serializationProperties) {
    			String nextPropVal = System.getProperty(nextProp);
    			if(nextPropVal == null || nextPropVal.trim().equals("")) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    private static Map<String, String> loadPrefs() {
        Properties ps = new Properties();
        try {
            ps.load( SecurityServiceImpl.class.getResourceAsStream( "/preferences.properties" ) );
            Map<String, String> prefs = new HashMap<String, String>();
            for ( Object o : ps.keySet() ) {
                String feature = (String) o;

                prefs.put( feature,
                           ps.getProperty( feature ) );
            }

            setSystemProperties( prefs );

            return prefs;
        } catch ( IOException e ) {
            log.info( "Couldn't find preferences.properties - using defaults" );
            return new HashMap<String, String>();
        }
    }

    /**
     * Set system properties.
     * If the system properties were not set, set them to Preferences so we can access them in client side.
     * @param prefs
     */
    private static void setSystemProperties(Map<String, String> prefs) {
        final String dateFormat = "drools.dateformat";
        final String defaultLanguage = "drools.defaultlanguage";
        final String defaultCountry = "drools.defaultcountry";
        final String serializationSign = "drools.serialization.sign";
        final String privateKeyStoreURL = "drools.serialization.private.keyStoreURL";
        final String privateKeyStorePwd = "drools.serialization.private.keyStorePwd";
        final String privateKeyAlias = "drools.serialization.private.keyAlias";
        final String privateKeyPwd = "drools.serialization.private.keyPwd";
        final String publicKeyStoreURL = "drools.serialization.public.keyStoreURL";
        final String publicKeyStorePwd = "drools.serialization.public.keyStorePwd";

        // Set properties that were specified in the properties file
        if ( prefs.containsKey( dateFormat ) ) {
            System.setProperty( dateFormat,
                                prefs.get( dateFormat ) );
        }
        if ( prefs.containsKey( defaultLanguage ) ) {
            System.setProperty( defaultLanguage,
                                prefs.get( defaultLanguage ) );
        }
        if ( prefs.containsKey( defaultCountry ) ) {
            System.setProperty( defaultCountry,
                                prefs.get( defaultCountry ) );
        }
        
        if ( prefs.containsKey( serializationSign ) ) {
            System.setProperty( serializationSign,
                                prefs.get( serializationSign ) );
        }
        if ( prefs.containsKey( privateKeyStoreURL ) ) {
            System.setProperty( privateKeyStoreURL,
                                prefs.get( privateKeyStoreURL ) );
        }
        if ( prefs.containsKey( privateKeyStorePwd ) ) {
            System.setProperty( privateKeyStorePwd,
                                prefs.get( privateKeyStorePwd ) );
        }
        if ( prefs.containsKey( privateKeyAlias ) ) {
            System.setProperty( privateKeyAlias,
                                prefs.get( privateKeyAlias ) );
        }
        if ( prefs.containsKey( privateKeyPwd ) ) {
            System.setProperty( privateKeyPwd,
                                prefs.get( privateKeyPwd ) );
        }
        if ( prefs.containsKey( publicKeyStoreURL ) ) {
            System.setProperty( publicKeyStoreURL,
                                prefs.get( publicKeyStoreURL ) );
        }
        if ( prefs.containsKey( publicKeyStorePwd ) ) {
            System.setProperty( publicKeyStorePwd,
                                prefs.get( publicKeyStorePwd ) );
        }

        // If properties were not set in the file, use the defaults
        if ( !prefs.containsKey( dateFormat ) ) {
            prefs.put( dateFormat,
                       DateUtils.getDateFormatMask() );
        }
        if ( !prefs.containsKey( defaultLanguage ) ) {
            prefs.put( defaultLanguage,
                       System.getProperty( defaultLanguage ) );
        }
        if ( !prefs.containsKey( defaultCountry ) ) {
            prefs.put( defaultCountry,
                       System.getProperty( defaultCountry ) );
        }
        
        // For security Serialization we DO NOT want to set any default 
        // as those can be set through other means and we don't want 
        // to override or mess with that
    }
}
