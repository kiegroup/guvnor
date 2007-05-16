package org.drools.brms.server.security;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

/**
 * This is kind of a nil authenticator, does not validate the user at all.
 * This will accept any user name except the "guest" user name (as that is used for skipping logging in altogether).
 * @author Michael Neale
 */
@Name("defaultAuthenticator")
public class DefaultAuthenticator {
    
    private static final Logger log = Logger.getLogger( DefaultAuthenticator.class );
    
    public boolean authenticate() {
        if (SecurityServiceImpl.GUEST_LOGIN.equals( Identity.instance().getUsername())) {
            return false;
        }
        log.info( "User logged in via default authentication module (no security check).");
        return true;
    }
}
