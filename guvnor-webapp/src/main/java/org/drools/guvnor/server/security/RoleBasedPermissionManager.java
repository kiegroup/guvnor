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
import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

/**
 * This enhances the BRMS repository for lifecycle management.
 */
@Scope(ScopeType.SESSION)
@AutoCreate
@Name("roleBasedPermissionManager")
public class RoleBasedPermissionManager implements Serializable {

    //    @In
    private List<RoleBasedPermission> permissions;

    //    @Unwrap
    public List<RoleBasedPermission> getRoleBasedPermission() {
        return permissions;
    }

    @Create
    public void create() {
        RoleBasedPermissionStore roleBasedPermissionStore = (RoleBasedPermissionStore) Component
                .getInstance("org.drools.guvnor.server.security.RoleBasedPermissionStore");
        permissions = roleBasedPermissionStore.getRoleBasedPermissionsByUserName(Identity
                .instance().getCredentials().getUsername());

    }

    @Destroy
    public void close() {

    }


}
