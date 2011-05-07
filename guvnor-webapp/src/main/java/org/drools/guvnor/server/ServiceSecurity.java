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
import org.drools.guvnor.server.security.PackageNameType;
import org.drools.guvnor.server.security.PackageUUIDType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.repository.AssetItem;
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

    protected void checkSecurityIsPackageDeveloper(RuleAsset asset) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( asset.getMetaData().getPackageName() ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }

    protected void checkSecurityIsPackageReadOnly(String packageName) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( packageName ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }

    protected void checkSecurityIsPackageDeveloper(AssetItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }

    protected void checkSecurityIsPackageAdmin(String uuid) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( uuid ),
                                                 RoleTypes.PACKAGE_ADMIN );
        }
    }

    protected void checkSecurityIsAdmin() {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new AdminType(),
                                                 RoleTypes.ADMIN );
        }
    }

    protected void checkSecurityNameTypePackageReadOnly(PackageItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( item.getName() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }

    protected void checkSecurityIsPackageDeveloperForName(String initialPackage) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageNameType( initialPackage ),
                                                 RoleTypes.PACKAGE_DEVELOPER );
        }
    }

    protected void checkSecurityAssetPackagePackageReadOnly(AssetItem item) {
        if ( Contexts.isSessionContextActive() ) {
            Identity.instance().checkPermission( new PackageUUIDType( item.getPackage().getUUID() ),
                                                 RoleTypes.PACKAGE_READONLY );
        }
    }
}
