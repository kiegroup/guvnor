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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implements security related services.
 */
@ApplicationScoped
public class SecurityServiceImpl implements SecurityService {

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

    @Inject
    private RoleBasedPermissionManager roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    @Inject
    private Identity identity;

    @Inject
    private Credentials credentials;

    public boolean login(String userName,
                         String password) {

        if ( userName == null || userName.trim().equals( "" ) ) {
            userName = "admin";
        }

        log.info( "Logging in user [" + userName + "]" );
        BeanManagerLocator beanManagerLocator = new BeanManagerLocator();
        if (beanManagerLocator.isBeanManagerAvailable()) {

            // Check for banned characters in user name
            // These will cause the session to jam if you let them go further
            char[] bannedChars = {'\'', '*', '[', ']'};
            for (char bannedChar : bannedChars) {
                if (userName.indexOf(bannedChar) >= 0) {
                    log.error("Not a valid name character " + bannedChar);
                    return false;
                }
            }

            credentials.setUsername(userName);
            credentials.setCredential(new org.picketlink.idm.impl.api.PasswordCredential(password));

            identity.login();
            if ( !identity.isLoggedIn() ) {
                log.error( "Unable to login.");
            }
            return identity.isLoggedIn();
        }
        return true;

    }

    public UserSecurityContext getCurrentUser() {
        if ( !identity.isLoggedIn() ) {
            //check to see if we can autologin
            return new UserSecurityContext( checkAutoLogin() );
        }
        return new UserSecurityContext( credentials.getUsername() );
    }

    /**
     * This will return a auto login user name if it has been configured.
     * Autologin means that its not really logged in, but a generic username will be used.
     * Basically means security is bypassed.
     */
    private String checkAutoLogin() {
        credentials.setUsername(GUEST_LOGIN);
        identity.login();
        if ( identity.isLoggedIn() ) {
            return credentials.getUsername();
        } else {
            return null;
        }
    }

    public List<Capability> getUserCapabilities() {
        if ( identity.hasRole( RoleType.ADMIN.getName(), null, null ) ) {
            return CapabilityCalculator.grantAllCapabilities();
        }

        if ( !roleBasedPermissionResolver.isEnableRoleBasedAuthorization() ) {
            return CapabilityCalculator.grantAllCapabilities();
        }
        
        List<RoleBasedPermission> permissions = roleBasedPermissionManager.getRoleBasedPermission();
        if ( permissions.size() == 0 ) {
            identity.logout();
            throw new AuthorizationException( "This user has no permissions setup." );
        }

        if ( invalidSecuritySerializationSetup() ) {
            identity.logout();
            throw new AuthorizationException( " Configuration error - Please refer to the Administration Guide section on installation. You must configure a key store before proceding.  " );
        }
        return new CapabilityCalculator().calcCapabilities( permissions );
    }

    private boolean invalidSecuritySerializationSetup() {
        String serializationSign = System.getProperty( "drools.serialization.sign" );
        if ( serializationSign != null && serializationSign.equalsIgnoreCase( "true" ) ) {
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
