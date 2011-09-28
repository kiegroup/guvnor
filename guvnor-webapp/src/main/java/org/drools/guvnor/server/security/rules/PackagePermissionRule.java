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

import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.LoggingHelper;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PackagePermissionRule
        implements
        PermissionRule {

    private static final LoggingHelper log = LoggingHelper.getLogger(PackagePermissionRule.class);

    public boolean hasPermission(Object requestedObject,
                                 String requestedPermission,
                                 List<RoleBasedPermission> permissions) {
        String targetName = (String) requestedObject;
        for (RoleBasedPermission pbp : permissions) {
            if (targetName.equalsIgnoreCase(pbp.getPackageName()) && isPermittedPackage(requestedPermission,
                    pbp.getRole())) {
                log.debug("Requested permission: " + requestedPermission + ", Requested object: " + targetName + " , Permission granted: Yes");
                return true;
            }
        }

        log.debug("Requested permission: " + requestedPermission + ", Requested object: " + targetName + " , Permission granted: No");
        return false;
    }

    private boolean isPermittedPackage(String requestedAction,
                                       String role) {
        if (RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase(role)) {
            return true;
        } else if (RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase(role)) {
            if (RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase(requestedAction)) {
                return false;
            } else if (RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase(requestedAction)) {
                return true;
            } else if (RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase(requestedAction)) {
                return true;
            }
        } else if (RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase(role)) {
            if (RoleType.PACKAGE_ADMIN.getName().equalsIgnoreCase(requestedAction)) {
                return false;
            } else if (RoleType.PACKAGE_DEVELOPER.getName().equalsIgnoreCase(requestedAction)) {
                return false;
            } else if (RoleType.PACKAGE_READONLY.getName().equalsIgnoreCase(requestedAction)) {
                return true;
            }
        }

        return false;
    }

}
