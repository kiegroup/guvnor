package org.drools.brms.server.security;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.drools.brms.client.rpc.SecurityService;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.WebRemote;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This implements security related services. 
 * @author Michael Neale
 */
@Name("org.drools.brms.client.rpc.SecurityService")
@AutoCreate
public class SecurityServiceImpl
    implements
    SecurityService {

    public static final String GUEST_LOGIN = "guest";
    private static final Logger log = Logger.getLogger( SecurityServiceImpl.class );
    
    @WebRemote
    public boolean login(String userName, String password) {
        log.info( "Logging in user [" + userName + "]" );
        if (Contexts.isApplicationContextActive()) {
            Identity.instance().setUsername( userName );
            Identity.instance().setPassword( password );
            try {
                Identity.instance().authenticate();
            } catch ( LoginException e ) {
                log.error( e );
                return false;
            }
            return Identity.instance().isLoggedIn();
        } else {
            return true;
        }    

    }

    @WebRemote
    public String getCurrentUser() {
        if (Contexts.isApplicationContextActive()) {
            if (!Identity.instance().isLoggedIn()) {
                //check to see if we can autologin
                return checkAutoLogin();
            }
            return Identity.instance().getUsername();
        } else {
            return "SINGLE USER MODE (DEBUG) USE ONLY";
        }
    }

    /**
     * This will return a auto login user name if it has been configured.
     * Autologin means that its not really logged in, but a generic username will be used.
     * Basically means security is bypassed.
     * 
     */
    private String checkAutoLogin() {
        Identity id = Identity.instance();
        id.setUsername( GUEST_LOGIN );
        try {
            id.authenticate();
        } catch ( LoginException e ) {
            return null;
        }
        if (id.isLoggedIn()) {
            return id.getUsername();
        } else {
            return null;
        }
            
    }

}
