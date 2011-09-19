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
import org.drools.guvnor.server.security.AdminType;
import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * Handles security checks.
 */
public class ServiceSecurity {

    protected boolean isSecurityIsAnalystRead(final Object target) {
        if ( Contexts.isSessionContextActive() ) {
            return Identity.instance().hasPermission( target,
                                                       RoleTypes.ANALYST_READ );
        }
        return true;
    }

    protected void checkSecurityIsAdmin() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }
    }

    protected void checkSecurityIsPackageNameTypeAdmin(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }
    }

    protected void checkSecurityIsPackageDeveloper(String packageUUID) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUUID ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }

    protected void checkSecurityIsPackageReadOnly(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }

    protected void checkSecurityIsPackageAdmin(String packageUuid) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( packageUuid ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }
    }

    protected void checkSecurityNameTypePackageReadOnly(PackageItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( item.getName() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }

    protected void checkIsPackageDeveloper(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }
    
    protected void checkSecurityIsPackageReadOnly(AssetItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }
    
    protected void checkSecurityIsPackageDeveloper(AssetItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }

    protected void checkSecurityIsPackageDeveloper(RuleAsset asset) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
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
    protected void checkSecurityIsPackageDeveloperOrAnalyst(RuleAsset asset) {
        if ( Contexts.isSessionContextActive() ) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission( new PackageNameType( asset.metaData.packageName ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            } catch ( RuntimeException e ) {
                if ( asset.metaData.categories.length == 0 ) {
                    Identity.instance().checkPermission( new CategoryPathType( null ),
                                                         RoleTypes.ANALYST );
                } else {
                    RuntimeException exception = null;

                    for ( String cat : asset.metaData.categories ) {
                        try {
                            Identity.instance().checkPermission( new CategoryPathType( cat ),
                                                                 RoleTypes.ANALYST );
                            passed = true;
                        } catch ( RuntimeException re ) {
                            exception = re;
                        }
                    }
                    if ( !passed ) {
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
    protected void checkSecurityIsPackageDeveloperOrAnalyst(AssetItem asset) {
        if ( Contexts.isSessionContextActive() ) {
            boolean passed = false;

            try {
                Identity.instance().checkPermission( new PackageNameType( asset.getPackage().getName() ),
                                                     RoleTypes.PACKAGE_DEVELOPER );
            } catch ( RuntimeException e ) {
                if ( asset.getCategories().size() == 0 ) {
                    Identity.instance().checkPermission( new CategoryPathType( null ),
                                                         RoleTypes.ANALYST );
                } else {
                    RuntimeException exception = null;

                    for ( CategoryItem cat : asset.getCategories() ) {
                        try {
                            Identity.instance().checkPermission( new CategoryPathType( cat.getFullPath() ),
                                                                 RoleTypes.ANALYST );
                            passed = true;
                        } catch ( RuntimeException re ) {
                            exception = re;
                        }
                    }
                    if ( !passed ) {
                        throw exception;
                    }
                }
            }
        }
    }
}
