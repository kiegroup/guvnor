package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Contains methods for authenticating/authorising from the front end.
 * 
 * @author Michael Neale
 */
public interface SecurityService extends RemoteService {

    /**
     * This will do a password authentication, using the configured JAAS provider.
     * This may be a default one (which allows anything in).
     * 
     * @return true if user is logged in successfully.
     */
    public boolean login(String userName, String password);
    
    /**
     * @return This returns the current user's name if they are logged in. If not
     * then null is returned.
     */
    public String getCurrentUser();
    
}
