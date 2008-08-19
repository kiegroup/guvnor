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



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This implements security related services.
 * @author Michael Neale
 */
public class SecurityServiceImpl
    implements
    SecurityService {

    public static final String GUEST_LOGIN = "guest";
    private static final Logger log = Logger.getLogger( SecurityServiceImpl.class );

    public boolean login(String userName, String password) {
        log.info( "Logging in user [" + userName + "]" );
        if (Contexts.isApplicationContextActive()) {
            Identity.instance().getCredentials().setUsername(userName);
            Identity.instance().getCredentials().setPassword(password);
            try {
                Identity.instance().authenticate();
            } catch ( LoginException e ) {
                log.error( e );
                return false;
            }
            return Identity.instance().isLoggedIn();
        } else {
            return true;
        }

    }

    public UserSecurityContext getCurrentUser() {
        if (Contexts.isApplicationContextActive()) {
        	HashSet<String> disabled = new HashSet<String>();
        	//disabled.add("QA");
            if (!Identity.instance().isLoggedIn()) {
                //check to see if we can autologin
                return new UserSecurityContext(checkAutoLogin(), disabled);
            }
            return new UserSecurityContext(Identity.instance().getCredentials().getUsername(), disabled);
        } else {
        	HashSet<String> disabled = new HashSet<String>();
        	//disabled.add("QA");
            //return new UserSecurityContext(null, new HashSet());
            return new UserSecurityContext("SINGLE USER MODE (DEBUG) USE ONLY", disabled);
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
        if (id.isLoggedIn()) {
            return id.getCredentials().getUsername();
        } else {
            return null;
        }

    }

	public Capabilities getUserCapabilities() {
		if (Contexts.isSessionContextActive()) {
			if (Identity.instance().hasRole(RoleTypes.ADMIN)) {
				return Capabilities.all();
			}
			CapabilityCalculator c = new CapabilityCalculator();
			RoleBasedPermissionManager permManager = (RoleBasedPermissionManager)
					Component.getInstance("roleBasedPermissionManager");
			List<RoleBasedPermission> permissions = permManager.getRoleBasedPermission();
			return c.calcCapabilities(permissions);
		} else {
			return Capabilities.all();
		}
	}

}
