package org.guvnor.common.services.backend.config;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.rpc.SessionInfo;

public class SafeSessionInfo implements SessionInfo {

    private SessionInfo delegate;

    public SafeSessionInfo( SessionInfo delegate ) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        try {
            return delegate.getId();
        } catch ( Exception e ) {
            return "--";
        }
    }

    @Override
    public User getIdentity() {
        try {
            return delegate.getIdentity();
        } catch ( Exception e ) {
            return new UserImpl( "Anonymous" );
        }
    }
}
