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


import java.util.List;
import org.apache.log4j.Logger;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * Use this authenticator for role based authentication.
 * @author Jervis Liu
 */
@Name("roleBasedAuthenticator")
public class RoleBasedAuthenticator {

    private static final Logger log = Logger.getLogger(RoleBasedAuthenticator.class);
	
    public boolean authenticate() {
        if (SecurityServiceImpl.GUEST_LOGIN.equals( Identity.instance().getUsername())) {
            return false;
        }
        log.info( "User logged in via RoleBasedAuthenticator.");
        
        RoleBasedPermissionStore pbps = (RoleBasedPermissionStore) Component
		.getInstance("org.drools.guvnor.server.security.RoleBasedPermissionStore");
    	List<RoleBasedPermission> permissions = pbps.getRoleBasedPermissionsByUserName(Identity.instance().getUsername());

    	//The admin role is added into Identity so that we can call Identity.hadRole("admin")
    	//later. Other permissions are stored in session context
    	for(RoleBasedPermission p : permissions) {
    		if(RoleTypes.ADMIN.equalsIgnoreCase(p.getRole())) {
    			Identity.instance().addRole(RoleTypes.ADMIN);
    		}
    	}
    	Contexts.getSessionContext().set("packageBasedPermission", permissions);
        return true;
    }
}