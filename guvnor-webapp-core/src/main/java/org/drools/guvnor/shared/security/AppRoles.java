package org.drools.guvnor.shared.security;

import org.uberfire.security.Role;

public enum AppRoles implements Role {

    /**
     * Admin can do everything
     */
    ADMIN,

    /**
     * Can edit repositories
     */
    REPOSITORY_EDITOR,

    /**
     * Can view repositories, but not do changes.
     */
    REPOSITORY_VIEWER;

    @Override
    public String getName() {
        return toString();
    }
}
