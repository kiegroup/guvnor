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

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.permission.PermissionResolver;

/**
 * PermissionResolvers are chained together to resolve permission check, the check returns true if
 * one of the PermissionResolvers in the chain returns true.
 *
 * This PermissionResolver resolves category-based permissions and package-based permissions.
 *
 * If the input is category-based request, the resolver returns true under following situations:
 * 1. The user is logInAdmin
 * Or
 * 2. The user has at least one analyst role that has access to the requested category path.
 *
 * If the input is package-based request, the resolver returns true under following situations:
 * 1. The user is logInAdmin
 * Or
 * 2. The user has one of the following roles package.logInAdmin|package.developer|package.readonly on the requested
 * package, and requested role requires lower privilege than assigned role(I.e., package.logInAdmin>package.developer>package.readonly)
 *
 *

 */
@Name("org.jboss.seam.security.roleBasedPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence = org.jboss.seam.annotations.Install.APPLICATION)
@Startup
public class RoleBasedPermissionResolver implements PermissionResolver, Serializable {
    private static final LoggingHelper log                          = LoggingHelper.getLogger( RoleBasedPermissionResolver.class );

    private boolean                    enableRoleBasedAuthorization = false;

    @Create
    public void create() {
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
     *            followings: logInAdmin|analyst|package.logInAdmin|package.developer|package.readonly,
     *            otherwise return false;
     * @return true if the permission can be granted on the requested object with the
     * requested role; return false otherwise.
     */
    public boolean hasPermission(Object requestedObject, String requestedPermission) {
        if ( isInvalidInstance( requestedObject ) ) {
            log.debug( "Requested permission is not an instance of CategoryPathType|PackageNameType|WebDavPackageNameType|AdminType|PackageUUIDType" );
            return false;
        }

        if ( !enableRoleBasedAuthorization ) {
            return true;
        }

        List<RoleBasedPermission> permissions = fetchAllRoleBasedPermissionsForCurrentUser();

        if ( hasAdminPermission( permissions ) ) {
            return true;
        } else if ( RoleType.ADMIN.getName().equals( requestedPermission ) ) {
            return hasAdminPermission( permissions );
        }

        if ( requestedObject instanceof CategoryPathType ) {
            return handleCategoryPathPermission( requestedObject, requestedPermission, permissions );
        }
        String targetName = "";

        if ( requestedObject instanceof PackageUUIDType ) {
            String targetUUID = ((PackageUUIDType) requestedObject).getUUID();
            try {
                ServiceImplementation serviceImplementation = (ServiceImplementation) Component.getInstance( "org.drools.guvnor.client.rpc.RepositoryService" );
                targetName = serviceImplementation.getRulesRepository().loadPackageByUUID( targetUUID ).getName();
            } catch ( RulesRepositoryException e ) {
                return false;
            }
        } else if ( requestedObject instanceof PackageNameType ) {
            targetName = ((PackageNameType) requestedObject).getPackageName();
        }

        for ( RoleBasedPermission pbp : permissions ) {
            if ( targetName.equalsIgnoreCase( pbp.getPackageName() ) && isPermittedPackage( requestedPermission, pbp.getRole() ) ) {
                log.debug( "Requested permission: " + requestedPermission + ", Requested object: " + targetName + " , Permission granted: Yes" );
                return true;
            }
        }

        log.debug( "Requested permission: " + requestedPermission + ", Requested object: " + targetName + " , Permission granted: No" );
        return false;

    }

    private List<RoleBasedPermission> fetchAllRoleBasedPermissionsForCurrentUser() {
        return ((RoleBasedPermissionManager) Component.getInstance( "roleBasedPermissionManager" )).getRoleBasedPermission();
    }

    private boolean handleCategoryPathPermission(Object requestedObject, String requestedPermission, List<RoleBasedPermission> permissions) {
        String requestedPath = ((CategoryPathType) requestedObject).getCategoryPath();
        String requestedPermType = (requestedPermission == null) ? RoleType.ANALYST.getName() : requestedPermission;
        if ( requestedPermType.equals( "navigate" ) ) {
            for ( RoleBasedPermission roleBasedPermission : permissions ) {
                if ( roleBasedPermission.getCategoryPath() != null ) {
                    if ( isCategoryPathMatched( requestedPath, roleBasedPermission ) ) {
                        return true;
                    }
                    if ( isSubPath( requestedPath, roleBasedPermission.getCategoryPath() ) ) {
                        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                        return true;
                    } else if ( isSubPath( roleBasedPermission.getCategoryPath(), requestedPath ) ) {
                        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                        return true;
                    }
                }
            }
            log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: No" );
            return false;
        }
        for ( RoleBasedPermission roleBasedPermission : permissions ) {
            if ( isRoleAnalyst( roleBasedPermission ) && isPermissionToCurrentDirectory( requestedPermType, roleBasedPermission ) && isPermittedCategoryPath( requestedPath, roleBasedPermission.getCategoryPath() ) ) {
                log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                return true;
            }
        }
        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: No" );
        return false;
    }

    private boolean isPermissionToCurrentDirectory(String requestedPermType, RoleBasedPermission roleBasedPermission) {
        return requestedPermType.equals( roleBasedPermission.getRole() ) || (requestedPermType.equals( RoleType.ANALYST_READ.getName() ) && roleBasedPermission.getRole().equals( RoleType.ANALYST.getName() ));
    }

    private boolean isRoleAnalyst(RoleBasedPermission roleBasedPermission) {
        return roleBasedPermission.getRole().equals( RoleType.ANALYST.getName() ) || roleBasedPermission.getRole().equals( RoleType.ANALYST_READ.getName() );
    }

    private boolean isCategoryPathMatched(String requestedPath, RoleBasedPermission roleBasedPermission) {
        return roleBasedPermission.getCategoryPath().equals( requestedPath );
    }

    private boolean isInvalidInstance(Object requestedObject) {
        return !((requestedObject instanceof CategoryPathType) || (requestedObject instanceof PackageNameType) || (requestedObject instanceof WebDavPackageNameType) || (requestedObject instanceof AdminType) || (requestedObject instanceof PackageUUIDType));
    }

    private boolean hasAdminPermission(List<RoleBasedPermission> permissions) {
        for ( RoleBasedPermission p : permissions ) {
            if ( RoleType.ADMIN.getName().equalsIgnoreCase( p.getRole() ) ) {
                log.debug( "Requested permission: unknown, Permission granted: Yes" );
                return true;
            }
        }
        log.debug( "Requested permission: logInAdmin, Permission granted: No" );
        return false;
    }

    private boolean isPermittedCategoryPath(String requestedPath, String allowedPath) {
        if ( requestedPath == null && allowedPath == null ) {
            return true;
        } else if ( requestedPath == null || allowedPath == null ) {
            return false;
        }
        return requestedPath.equals( allowedPath ) || isSubPath( allowedPath, requestedPath );
    }

    private boolean isPermittedPackage(String requestedAction, String role) {
        if ( RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase( role ) ) {
            return true;
        } else if ( RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase( role ) ) {
            if ( RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase( requestedAction ) ) {
                return false;
            } else if ( RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase( requestedAction ) ) {
                return true;
            } else if ( RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase( requestedAction ) ) {
                return true;
            }
        } else if ( RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase( role ) ) {
            if ( RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase( requestedAction ) ) {
                return false;
            } else if ( RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase( requestedAction ) ) {
                return false;
            } else if ( RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase( requestedAction ) ) {
                return true;
            }
        }

        return false;
    }

    public boolean isSubPath(String parentPath, String subPath) {
        parentPath = (parentPath.startsWith( "/" )) ? parentPath.substring( 1 ) : parentPath;
        subPath = (subPath.startsWith( "/" )) ? subPath.substring( 1 ) : subPath;
        String[] parentTags = parentPath.split( "/" );
        String[] subTags = subPath.split( "/" );
        if ( parentTags.length > subTags.length ) {
            return false;
        }
        for ( int i = 0; i < parentTags.length; i++ ) {
            if ( !parentTags[i].equals( subTags[i] ) ) {
                return false;
            }
        }

        return true;
    }

    public void filterSetByAction(Set<Object> targets, String action) {
    }

    public boolean isEnableRoleBasedAuthorization() {
        return enableRoleBasedAuthorization;
    }

    public void setEnableRoleBasedAuthorization(boolean enableRoleBasedAuthorization) {
        this.enableRoleBasedAuthorization = enableRoleBasedAuthorization;
    }
}
