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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.security.*;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.jboss.seam.security.Identity;

/**
 * Handles security checks.
 */
@ApplicationScoped
public class ServiceSecurity {
    
    @Inject
    private Identity identity;

    protected void checkSecurityIsAdmin() {
        identity.checkPermission(new AdminType(),
                RoleType.ADMIN.getName());
    }

    protected boolean isSecurityIsAnalystReadWithTargetObject(final Object target) {
        return identity.hasPermission(target,
                RoleType.ANALYST_READ.getName());
    }

    protected void checkPermissionAnalystReadWithCategoryPathType(final String categoryPath) {
        identity.checkPermission(new CategoryPathType(categoryPath),
                RoleType.ANALYST_READ.getName());
    }

    protected boolean hasPermissionAnalystReadWithCategoryPathType(final String categoryPath) {
        return identity.hasPermission(new CategoryPathType(categoryPath),
                RoleType.ANALYST_READ.getName());
    }

    protected void checkSecurityIsPackageAdminWithPackageName(String packageName) {
        identity.checkPermission(new PackageNameType(packageName),
                RoleType.PACKAGE_ADMIN.getName());
    }

    protected void checkSecurityIsPackageAdminWithAdminType() {
        identity.checkPermission(new AdminType(), RoleType.PACKAGE_ADMIN.getName());
    }

    protected void checkSecurityIsPackageAdminWithPackageUuid(String uuid) {
        identity.checkPermission(new PackageUUIDType(uuid),
                RoleType.PACKAGE_ADMIN.getName());
    }

    protected void checkSecurityIsPackageDeveloperWithPackageUuid(String uuid) {
        identity.checkPermission(new PackageUUIDType(uuid),
                RoleType.PACKAGE_DEVELOPER.getName());
    }

    protected void checkSecurityIsPackageDeveloperWithPackageName(String packageName) {
        identity.checkPermission(new PackageNameType(packageName),
                RoleType.PACKAGE_DEVELOPER.getName());
    }

    protected void checkSecurityIsPackageReadOnlyWithPackageName(String packageName) {
        identity.checkPermission(new PackageNameType(packageName),
                RoleType.PACKAGE_READONLY.getName());
    }

    protected void checkSecurityPackageReadOnlyWithPackageUuid(final String uuid) {
        identity.checkPermission(new PackageUUIDType(uuid),
                RoleType.PACKAGE_READONLY.getName());
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
    protected void checkIsPackageDeveloperOrAnalyst(final RuleAsset asset) {
        boolean passed = false;

        try {
            identity.checkPermission(new PackageNameType(asset.getMetaData().getPackageName()),
                    RoleType.PACKAGE_DEVELOPER.getName());
        } catch (RuntimeException e) {
            if (asset.getMetaData().getCategories().length == 0) {
                identity.checkPermission(new CategoryPathType(null),
                        RoleType.ANALYST.getName());
            } else {
                RuntimeException exception = null;

                for (String cat : asset.getMetaData().getCategories()) {
                    try {
                        identity.checkPermission(new CategoryPathType(cat),
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
    protected void checkIsPackageDeveloperOrAnalyst(final AssetItem asset) {
        boolean passed = false;

        try {
            identity.checkPermission(new PackageUUIDType(asset.getPackage().getUUID()),
                    RoleType.PACKAGE_DEVELOPER.getName());
        } catch (RuntimeException e) {
            if (asset.getCategories().size() == 0) {
                identity.checkPermission(new CategoryPathType(null),
                        RoleType.ANALYST.getName());
            } else {
                RuntimeException exception = null;

                for (CategoryItem cat : asset.getCategories()) {
                    try {
                        identity.checkPermission(new CategoryPathType(cat.getFullPath()),
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

    protected void checkIsPackageReadOnlyOrAnalystReadOnly(final RuleAsset asset) {
        boolean passed = false;

        try {
            identity.checkPermission(new PackageNameType(asset.getMetaData().getPackageName()),
                    RoleType.PACKAGE_READONLY.getName());
        } catch (RuntimeException e) {
            if (asset.getMetaData().getCategories().length == 0) {
                identity.checkPermission(new CategoryPathType(null),
                        RoleType.ANALYST_READ.getName());
            } else {
                RuntimeException exception = null;

                for (String cat : asset.getMetaData().getCategories()) {
                    try {
                        identity.checkPermission(new CategoryPathType(cat),
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
