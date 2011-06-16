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

package org.drools.guvnor.server;

import org.drools.guvnor.server.security.*;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * Handles security checks.
 */
public class ServiceSecurity {

    protected void checkSecurityIsAdmin() {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new AdminType(),
                    RoleType.ADMIN.getName());
        }
    }

    protected boolean isSecurityIsAnalystReadWithTargetObject(final Object target) {
        if (Contexts.isSessionContextActive()) {
            return Identity.instance().hasPermission(target,
                    RoleType.ANALYST_READ.getName());
        }
        return true;
    }

    protected void checkPermissionAnalystReadWithCategoryPathType(final String categoryPath) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new CategoryPathType(categoryPath),
                    RoleType.ANALYST_READ.getName());

        }
    }

    protected boolean hasPermissionAnalystReadWithCategoryPathType(final String categoryPath) {
        if (Contexts.isSessionContextActive()) {
            return Identity.instance().hasPermission(new CategoryPathType(categoryPath),
                    RoleType.ANALYST_READ.getName());

        }
        return true;
    }

    protected void checkSecurityIsPackageAdminWithPackageName(String packageName) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageNameType(packageName),
                    RoleType.PACKAGE_ADMIN.getName());
        }
    }

    protected void checkSecurityIsPackageAdminWithAdminType() {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new AdminType(), RoleType.PACKAGE_ADMIN.getName());
        }
    }

    protected void checkSecurityIsPackageAdminWithPackageUuid(String uuid) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageUUIDType(uuid),
                    RoleType.PACKAGE_ADMIN.getName());
        }
    }

    protected void checkSecurityIsPackageDeveloperWithPackageUuid(String uuid) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageUUIDType(uuid),
                    RoleType.PACKAGE_DEVELOPER.getName());
        }
    }

    protected void checkSecurityIsPackageDeveloperWithPackageName(String packageName) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageNameType(packageName),
                    RoleType.PACKAGE_DEVELOPER.getName());
        }
    }

    protected void checkSecurityIsPackageReadOnlyWithPackageName(String packageName) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageNameType(packageName),
                    RoleType.PACKAGE_READONLY.getName());
        }
    }

    protected void checkSecurityPackageReadOnlyWithPackageUuid(final String uuid) {
        if (Contexts.isSessionContextActive()) {
            Identity.instance().checkPermission(new PackageUUIDType(uuid),
                    RoleType.PACKAGE_READONLY.getName());
        }
    }


}
