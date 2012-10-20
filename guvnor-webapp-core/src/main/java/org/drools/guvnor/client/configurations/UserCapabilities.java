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

package org.drools.guvnor.client.configurations;

import org.drools.guvnor.shared.security.AppRoles;
import org.uberfire.security.Identity;

/**
 * This is used to turn off GUI functionality. The server decides what should be visible
 * based on roles and permissions granted. This is essentially a security and permissions function.
 * (however the Capabilities do not enforce actions on the server - these are more for GUI convenience so elements are not displayed
 * that are not relevant to a given users role).
 */
public class UserCapabilities {

    public static boolean canCreateNewAsset(Identity identity) {
        return identity.hasRole(AppRoles.ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_DEVELOPER);
    }

    public static boolean canSeeModulesTree(Identity identity) {
        return identity.hasRole(AppRoles.ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_DEVELOPER)
                || identity.hasRole(AppRoles.PACKAGE_READONLY);
    }

    public static boolean canSeeStatuses(Identity identity) {
        return identity.hasRole(AppRoles.ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_DEVELOPER);
    }

    public static boolean canSeeQA(Identity identity) {
        return identity.hasRole(AppRoles.ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_DEVELOPER);
    }

    public static boolean canSeeDeploymentTree(Identity identity) {
        return identity.hasRole(AppRoles.ADMIN)
                || identity.hasRole(AppRoles.PACKAGE_ADMIN);
    }
}
