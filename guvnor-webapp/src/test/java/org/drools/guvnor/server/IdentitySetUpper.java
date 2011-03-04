package org.drools.guvnor.server;

import org.drools.guvnor.server.security.*;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

public class IdentitySetUpper {

    public void setUpMockIdentity() {
        MockIdentity mockIdentity = getIdentity(false);
        setUpMockIdentity(mockIdentity);
    }

    private MockIdentity getIdentity(boolean enableRoleBasedAuthorization) {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn(true);
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization(enableRoleBasedAuthorization);
        mockIdentity.addPermissionResolver(resolver);
        return mockIdentity;
    }

    public void logInAdmin() {

        MockIdentity admin = getIdentity(false);

        admin.addRole(RoleTypes.ADMIN);

        setUpMockIdentity(admin);
    }

    public void setUpMockIdentity(MockIdentity mockIdentity) {
        mockIdentity.inject();
        mockIdentity.create();
    }

}
