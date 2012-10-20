package org.drools.guvnor.shared.security;

import org.uberfire.security.Role;

public enum AppRoles implements Role {

    /**
     * Admin can do everything
     */
    ADMIN,

    /**
     * Analyst only see the "rules" view, and we specify what category paths they
     * can see. They can't create anything, only edit rules, and run tests etc,
     * but only things that are exposed to them via categories
     */
    ANALYST,

    /**
     * Read only for categories (analyst view)
     */
    ANALYST_READ,

    /**
     * package.admin can do everything within this package
     */
    PACKAGE_ADMIN,

    /**
     * package.developer can do anything in that package but not snapshots. This
     * includes creating a new package (in which case they inherit permissions
     * for it).
     */
    PACKAGE_DEVELOPER,

    /**
     * Read only for package.
     */
    PACKAGE_READONLY;

    @Override
    public String getName() {
        return toString();
    }
}
