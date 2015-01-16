package org.guvnor.structure.backend.social;

import org.uberfire.rpc.SessionInfo;

public abstract class SocialEventAdapter {


    protected String getUserInfo( SessionInfo sessionInfo ) {

        try {
            return sessionInfo.getIdentity().getIdentifier();
        } catch ( Exception e) {
            return "system";
        }
    }
}
