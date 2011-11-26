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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * This enhances the BRMS repository for lifecycle management.
 */
@SessionScoped
public class RoleBasedPermissionManager implements Serializable {

    //    @Inject
    private List<RoleBasedPermission> permissions;

    @Inject
    private RoleBasedPermissionStore roleBasedPermissionStore;

    @Inject
    private Credentials credentials;

    //    @Unwrap
    public List<RoleBasedPermission> getRoleBasedPermission() {
        return permissions;
    }

    @PostConstruct
    public void create() {
        permissions = roleBasedPermissionStore.getRoleBasedPermissionsByUserName(credentials.getUsername());
    }

}
