/*
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

import org.drools.guvnor.client.configurations.Capability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.drools.guvnor.client.configurations.Capability.*;

/**
 * Load up the capabilities from a given list of roles.
 */
public class CapabilityCalculator {

    public List<Capability> calcCapabilities(List<RoleBasedPermission> permissions) {
        if (permissions.size() == 0) {
            return grantAllCapabilities();
        }
        List<Capability> capabilities = new ArrayList<Capability>();
        for (RoleBasedPermission roleBasedPermission : permissions) {
            String role = roleBasedPermission.getRole();
            if ( role.equals( RoleType.ADMIN.getName() ) ) {
                return grantAllCapabilities();
            } else if ( role.equals( RoleType.PACKAGE_ADMIN.getName() ) ) {
                capabilities.add( SHOW_KNOWLEDGE_BASES_VIEW );
                capabilities.add( SHOW_CREATE_NEW_ASSET );
                capabilities.add( SHOW_CREATE_NEW_PACKAGE );
                capabilities.add( SHOW_DEPLOYMENT );
                capabilities.add( SHOW_DEPLOYMENT_NEW );
                capabilities.add( SHOW_QA );
            } else if ( role.equals( RoleType.PACKAGE_DEVELOPER.getName() ) ) {
                capabilities.add( SHOW_KNOWLEDGE_BASES_VIEW );
                capabilities.add( SHOW_CREATE_NEW_ASSET );
                capabilities.add( SHOW_QA );
            } else if ( role.equals( RoleType.PACKAGE_READONLY.getName() ) ) {
                capabilities.add( SHOW_KNOWLEDGE_BASES_VIEW );
            }
        }
        return capabilities;
    }

    /**
     * Grants all capabilities.
     * Only used for when there is basically no login.
     */
    public static List<Capability> grantAllCapabilities() {
        return Arrays.asList(Capability.values());
    }

}
