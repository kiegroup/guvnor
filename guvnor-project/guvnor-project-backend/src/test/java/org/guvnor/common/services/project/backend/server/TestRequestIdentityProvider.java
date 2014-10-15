package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.guvnor.common.services.shared.identity.RequestIdentityProvider;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

public class TestRequestIdentityProvider implements RequestIdentityProvider {

    @Inject
    private Identity identity;
    
    @Override
    public String getName() {
        try {
            return identity.getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();
        
        List<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }
        
        return roles;
    }

}
