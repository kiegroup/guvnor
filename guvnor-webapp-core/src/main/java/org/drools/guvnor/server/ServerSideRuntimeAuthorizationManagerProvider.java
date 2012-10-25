package org.drools.guvnor.server;

import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class ServerSideRuntimeAuthorizationManagerProvider {

    @Produces
    @ApplicationScoped
    public RuntimeAuthorizationManager temporalFixWeNeedToWaitForUberfireToTestThis() {
        return new RuntimeAuthorizationManager();
    }
}
