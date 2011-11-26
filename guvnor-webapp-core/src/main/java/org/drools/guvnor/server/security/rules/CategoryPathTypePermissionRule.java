/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.security.rules;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PathHelper;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.LoggingHelper;

@ApplicationScoped
public class CategoryPathTypePermissionRule
    implements
    PermissionRule {

    private static final LoggingHelper log = LoggingHelper.getLogger( CategoryPathTypePermissionRule.class );

    public boolean hasPermission(Object requestedObject,
                                 String requestedPermission,
                                 List<RoleBasedPermission> permissions) {
        String requestedPath = ((CategoryPathType) requestedObject).getCategoryPath();
        String requestedPermType = (requestedPermission == null) ? RoleType.ANALYST.getName() : requestedPermission;
        if ( requestedPermType.equals( "navigate" ) ) {
            for ( RoleBasedPermission roleBasedPermission : permissions ) {
                if ( roleBasedPermission.getCategoryPath() != null ) {
                    if ( isCategoryPathMatched( requestedPath,
                                                roleBasedPermission ) ) {
                        return true;
                    }
                    if ( PathHelper.isSubPath( requestedPath,
                                               roleBasedPermission.getCategoryPath() ) ) {
                        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                        return true;
                    } else if ( PathHelper.isSubPath( roleBasedPermission.getCategoryPath(),
                                                      requestedPath ) ) {
                        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                        return true;
                    }
                }
            }
            log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: No" );
            return false;
        }
        for ( RoleBasedPermission roleBasedPermission : permissions ) {
            if ( isRoleAnalyst( roleBasedPermission ) && isPermissionToCurrentDirectory( requestedPermType,
                                                                                         roleBasedPermission ) && isPermittedCategoryPath( requestedPath,
                                                                                                                                           roleBasedPermission.getCategoryPath() ) ) {
                log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: Yes" );
                return true;
            }
        }
        log.debug( "Requested permission: " + requestedPermType + ", Requested object: " + requestedPath + " , Permission granted: No" );
        return false;
    }

    private boolean isPermissionToCurrentDirectory(String requestedPermType,
                                                   RoleBasedPermission roleBasedPermission) {
        return requestedPermType.equals( roleBasedPermission.getRole() ) || (requestedPermType.equals( RoleType.ANALYST_READ.getName() ) && roleBasedPermission.getRole().equals( RoleType.ANALYST.getName() ));
    }

    private boolean isRoleAnalyst(RoleBasedPermission roleBasedPermission) {
        return roleBasedPermission.getRole().equals( RoleType.ANALYST.getName() ) || roleBasedPermission.getRole().equals( RoleType.ANALYST_READ.getName() );
    }

    private boolean isCategoryPathMatched(String requestedPath,
                                          RoleBasedPermission roleBasedPermission) {
        return roleBasedPermission.getCategoryPath().equals( requestedPath );
    }

    private boolean isPermittedCategoryPath(String requestedPath,
                                            String allowedPath) {
        if ( requestedPath == null && allowedPath == null ) {
            return true;
        } else if ( requestedPath == null || allowedPath == null ) {
            return false;
        }
        return requestedPath.equals( allowedPath ) || PathHelper.isSubPath( allowedPath,
                                                                            requestedPath );
    }

}
