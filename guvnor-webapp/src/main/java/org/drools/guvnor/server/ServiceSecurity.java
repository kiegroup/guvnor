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

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.security.*;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
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

    /**
    *
    * Role-based Authorization check: This method can be accessed if user has
    * following permissions:
    * 1. The user has a Analyst role and this role has permission to access the category
    * which the asset belongs to.
    * Or.
    * 2. The user has a package.developer role or higher (i.e., package.admin)
    * and this role has permission to access the package which the asset belongs to.
    */
    //TODO: may need some refactorings after Ge0ffrey's domain object change.
    protected void checkIsPackageDeveloperOrAnalyst(final RuleAsset asset) {
        if (Contexts.isSessionContextActive()) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission(new PackageNameType(asset.getMetaData().getPackageName()),
                        RoleType.PACKAGE_DEVELOPER.getName());
            } catch (RuntimeException e) {
                if (asset.getMetaData().getCategories().length == 0) {
                    Identity.instance().checkPermission(new CategoryPathType(null),
                            RoleType.ANALYST.getName());
                } else {
                    RuntimeException exception = null;

                    for (String cat : asset.getMetaData().getCategories()) {
                        try {
                            Identity.instance().checkPermission(new CategoryPathType(cat),
                                    RoleType.ANALYST.getName());
                            passed = true;
                        } catch (RuntimeException re) {
                            exception = re;
                        }
                    }
                    if (!passed) {
                        throw exception;
                    }
                }
            }
        }
    }
    
    /**
    *
    * Role-based Authorization check: This method can be accessed if user has
    * following permissions:
    * 1. The user has a Analyst role and this role has permission to access the category
    * which the asset belongs to.
    * Or.
    * 2. The user has a package.developer role or higher (i.e., package.admin)
    * and this role has permission to access the package which the asset belongs to.
    */
    //TODO: may need some refactorings after Ge0ffrey's domain object change.
    protected void checkIsPackageDeveloperOrAnalyst(final AssetItem asset) {
        if (Contexts.isSessionContextActive()) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission(new PackageUUIDType(asset.getPackage().getUUID()),
                        RoleType.PACKAGE_DEVELOPER.getName());
            } catch (RuntimeException e) {
                if (asset.getCategories().size() == 0) {
                    Identity.instance().checkPermission(new CategoryPathType(null),
                            RoleType.ANALYST.getName());
                } else {
                    RuntimeException exception = null;

                    for (CategoryItem cat : asset.getCategories()) {
                        try {
                            Identity.instance().checkPermission(new CategoryPathType(cat.getFullPath()),
                                    RoleType.ANALYST.getName());
                            passed = true;
                        } catch (RuntimeException re) {
                            exception = re;
                        }
                    }
                    if (!passed) {
                        throw exception;
                    }
                }
            }
        }
    }   

    //TODO: may need some refactorings after Ge0ffrey's domain object change.
    protected void checkIsPackageReadOnlyOrAnalystReadOnly(final RuleAsset asset) {
        if (Contexts.isSessionContextActive()) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission(new PackageNameType(asset.getMetaData().getPackageName()),
                        RoleType.PACKAGE_READONLY.getName());
            } catch (RuntimeException e) {
                if (asset.getMetaData().getCategories().length == 0) {
                    Identity.instance().checkPermission(new CategoryPathType(null),
                            RoleType.ANALYST_READ.getName());
                } else {
                    RuntimeException exception = null;

                    for (String cat : asset.getMetaData().getCategories()) {
                        try {
                            Identity.instance().checkPermission(new CategoryPathType(cat),
                                    RoleType.ANALYST_READ.getName());
                            passed = true;
                        } catch (RuntimeException re) {
                            exception = re;
                        }
                    }
                    if (!passed) {
                        throw exception;
                    }
                }
            }
        }
    } 
}
