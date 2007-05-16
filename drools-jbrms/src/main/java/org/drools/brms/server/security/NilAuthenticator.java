package org.drools.brms.server.security;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;

/**
 * This will let any user in, effectively removing any authentication (as the system 
 * will attempt to auto login the first time).
 * @author Michael Neale
 */
@Name("nilAuthenticator")
public class NilAuthenticator {
    
    private static final Logger log = Logger.getLogger( NilAuthenticator.class );
    
    public boolean authenticate() {
        log.info( "All users are guests.");
        return true;
    }
}
