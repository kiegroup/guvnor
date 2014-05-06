package org.guvnor.common.services.backend.config;

import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.IdentityImpl;

public class SafeSessionInfo implements SessionInfo {

    private SessionInfo delegate;

    public SafeSessionInfo(SessionInfo delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        try {
            return delegate.getId();
        } catch (Exception e) {
            return "--";
        }
    }

    @Override
    public Identity getIdentity() {
        try {
            return delegate.getIdentity();
        } catch (Exception e) {
            return new IdentityImpl("Anonymous");
        }
    }
}
