package org.guvnor.m2repo.security;

import org.uberfire.security.Role;

public enum AppRole implements Role {

    /**
     * Admin can do everything
     */
    ADMIN;

    @Override
    public String getName() {
        return toString().toLowerCase();
    }
}
