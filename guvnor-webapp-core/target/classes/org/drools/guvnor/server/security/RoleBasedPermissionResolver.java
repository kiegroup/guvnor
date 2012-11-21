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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.server.security.rules.CategoryPathTypePermissionRule;
import org.drools.guvnor.server.security.rules.PackageNameTypeConverter;
import org.drools.guvnor.server.security.rules.PackagePermissionRule;
import org.drools.guvnor.server.security.rules.PackageUUIDTypePermissionRule;
import org.drools.guvnor.server.security.rules.PermissionRule;
import org.drools.guvnor.server.security.rules.PermissionRuleObjectConverter;
import org.drools.guvnor.server.util.LoggingHelper;
import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.security.permission.PermissionResolver;

/**
 * PermissionResolvers are chained together to resolve permission check, the check returns true if
 * one of the PermissionResolvers in the chain returns true.
 *
 * This PermissionResolver resolves category-based permissions and package-based permissions.
 *
 * If the input is category-based request, the resolver returns true under following situations:
 * 1. The user is admin
 * Or
 * 2. The user has at least one analyst role that has access to the requested category path.
 *
 * If the input is package-based request, the resolver returns true under following situations:
 * 1. The user is admin
 * Or
 * 2. The user has one of the following roles package.admin|package.developer|package.readonly on the requested
 * package, and requested role requires lower privilege than assigned role(I.e., package.admin>package.developer>package.readonly)
 *
 */
@ApplicationScoped
//@BypassInterceptors
//@Install(precedence = org.jboss.seam.annotations.Install.APPLICATION)
//@Startup
public class RoleBasedPermissionResolver
        implements PermissionResolver, Serializable {
    private static final LoggingHelper                     log                            = LoggingHelper.getLogger( RoleBasedPermissionResolver.class );

    private boolean                                        enableRoleBasedAuthorization   = false;

    private final Map<Class< ? >, PermissionRule>                permissionRules                = new HashMap<Class< ? >, PermissionRule>();
    private final Map<Class< ? >, PermissionRuleObjectConverter> permissionRuleObjectConverters = new HashMap<Class< ? >, PermissionRuleObjectConverter>();

    @Inject
    private RoleBasedPermissionManager roleBasedPermissionManager;

    @Inject
    private CategoryPathTypePermissionRule categoryPathTypePermissionRule;

    @Inject
    private PackageUUIDTypePermissionRule packageUUIDTypePermissionRule;

    @Inject
    private PackagePermissionRule packagePermissionRule;

    @Inject
    private PackageNameTypeConverter packageNameTypeConverter;

    @PostConstruct
    public void setupPermissionRules() {
        permissionRules.put( CategoryPathType.class,
                             categoryPathTypePermissionRule );
        permissionRules.put( ModuleUUIDType.class,
                             packageUUIDTypePermissionRule );
        permissionRules.put( ModuleNameType.class,
                             packagePermissionRule );
        permissionRules.put( WebDavPackageNameType.class,
                             packagePermissionRule );
        permissionRuleObjectConverters.put( ModuleNameType.class,
                                            packageNameTypeConverter );
        permissionRuleObjectConverters.put( WebDavPackageNameType.class,
                                            packageNameTypeConverter );
    }

    /**
     * check permission
     *
     * @param requestedObject
     *            the requestedObject must be an instance of CategoryPathType,
     *            or PackageNameType or PackageUUIDType.
     *            Otherwise return false;
     * @param requestedPermission
     *            the requestedRole must be an instance of String, its value has to be one of the
     *            followings: admin|analyst|package.admin|package.developer|package.readonly,
     *            otherwise return false;
     * @return true if the permission can be granted on the requested object with the
     * requested role; return false otherwise.
     */
    public boolean hasPermission(Object requestedObject,
                                 String requestedPermission) {
        if ( isInvalidInstance( requestedObject ) ) {
            log.debug( "Requested permission is not an instance of CategoryPathType|PackageNameType|WebDavPackageNameType|AdminType|PackageUUIDType" );
            return false;
        }

        if ( !enableRoleBasedAuthorization ) {
            return true;
        }

        List<RoleBasedPermission> permissions = fetchAllRoleBasedPermissionsForCurrentUser();

        boolean hasAdminPermission = hasAdminPermission( permissions );
        if ( hasAdminPermission || RoleType.ADMIN.getName().equals( requestedPermission ) ) {
            return hasAdminPermission;
        }

        return getPermissionRuleFor( requestedObject ).hasPermission( convertFor( requestedObject ),
                                                                         requestedPermission,
                                                                         permissions );
    }

    private PermissionRule getPermissionRuleFor(Object requestedObject) {
        return permissionRules.get( requestedObject.getClass() );
    }

    private Object convertFor(Object requestedObject) {
        PermissionRuleObjectConverter permissionRuleObjectConverter = permissionRuleObjectConverters.get( requestedObject.getClass() );
        return permissionRuleObjectConverter == null ? requestedObject : permissionRuleObjectConverter.convert( requestedObject );
    }

    private List<RoleBasedPermission> fetchAllRoleBasedPermissionsForCurrentUser() {
        return roleBasedPermissionManager.getRoleBasedPermission();
    }

    private boolean isInvalidInstance(Object requestedObject) {
        return !((requestedObject instanceof CategoryPathType) || (requestedObject instanceof ModuleNameType) || (requestedObject instanceof WebDavPackageNameType) || (requestedObject instanceof AdminType) || (requestedObject instanceof ModuleUUIDType));
    }

    private boolean hasAdminPermission(List<RoleBasedPermission> permissions) {
        for ( RoleBasedPermission p : permissions ) {
            if ( RoleType.ADMIN.getName().equalsIgnoreCase( p.getRole() ) ) {
                log.debug( "Requested permission: unknown, Permission granted: Yes" );
                return true;
            }
        }
        log.debug( "Requested permission: admin, Permission granted: No" );
        return false;
    }

    public void filterSetByAction(Set<Object> targets,
                                  String action) {
    }

    public boolean isEnableRoleBasedAuthorization() {
        return enableRoleBasedAuthorization;
    }

    public void setEnableRoleBasedAuthorization(boolean enableRoleBasedAuthorization) {
        this.enableRoleBasedAuthorization = enableRoleBasedAuthorization;
    }
}
