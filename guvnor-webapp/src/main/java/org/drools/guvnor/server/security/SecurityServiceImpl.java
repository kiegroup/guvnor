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

package org.drools.guvnor.server.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.drools.core.util.DateUtils;
import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implements security related services.
 */
public class SecurityServiceImpl
    implements
    SecurityService {

    public static final String GUEST_LOGIN = "guest";
    private static final Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);
    private static final String[] serializationProperties = new String[]{
            KeyStoreHelper.PROP_PVT_KS_URL,
            KeyStoreHelper.PROP_PVT_KS_PWD,
            KeyStoreHelper.PROP_PVT_ALIAS,
            KeyStoreHelper.PROP_PVT_PWD,
            KeyStoreHelper.PROP_PUB_KS_URL,
            KeyStoreHelper.PROP_PUB_KS_PWD
    };

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
            for (char bannedChar : bannedChars) {
                if (userName.indexOf(bannedChar) >= 0) {
                    log.error("Not a valid name character " + bannedChar);
                    return false;
                }
            }

            Identity.instance().getCredentials().setUsername( userName );
            Identity.instance().getCredentials().setPassword( password );

            try {
                Identity.instance().authenticate();
            } catch ( LoginException e ) {
                log.error( "Unable to login.",
                           e );
                return false;
            }
            return Identity.instance().isLoggedIn();
        }
        return true;

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

    public List<Capability> getUserCapabilities() {

        if ( Contexts.isApplicationContextActive() ) {
            if ( Identity.instance().hasRole( RoleType.ADMIN.getName() ) ) { 
                return CapabilityCalculator.grantAllCapabilities();
            }

            if ( !createRoleBasedPermissionResolver().isEnableRoleBasedAuthorization() ) {
                return CapabilityCalculator.grantAllCapabilities();
            }
            
            List<RoleBasedPermission> permissions = createRoleBasedPermissionManager().getRoleBasedPermission();
            if ( permissions.size() == 0 ) {
                Identity.instance().logout();
                throw new AuthorizationException( "This user has no permissions setup." );
            }

            if ( invalidSecuritySerilizationSetup() ) {
                Identity.instance().logout();
                throw new AuthorizationException( " Configuration error - Please refer to the Administration Guide section on installation. You must configure a key store before proceding.  " );
            }
            return new CapabilityCalculator().calcCapabilities( permissions );
        } else {
            if ( invalidSecuritySerilizationSetup() ) {
                throw new AuthorizationException( " Configuration error - Please refer to the Administration Guide section on installation. You must configure a key store before proceding.  " );
            }
            return CapabilityCalculator.grantAllCapabilities();
        }
    }

    private RoleBasedPermissionManager createRoleBasedPermissionManager() {
        return (RoleBasedPermissionManager) Component.getInstance( "roleBasedPermissionManager" );
    }

    private RoleBasedPermissionResolver createRoleBasedPermissionResolver() {
        return (RoleBasedPermissionResolver) Component.getInstance( "org.jboss.seam.security.roleBasedPermissionResolver" );
    }

    private boolean invalidSecuritySerilizationSetup() {
        String ssecurity = System.getProperty( "drools.serialization.sign" );
        if ( ssecurity != null && ssecurity.equalsIgnoreCase( "true" ) ) {
            for ( String nextProp : serializationProperties ) {
                String nextPropVal = System.getProperty( nextProp );
                if ( nextPropVal == null || nextPropVal.trim().equals( "" ) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
