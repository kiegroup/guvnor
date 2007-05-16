package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Contains methods for authenticating/authorising from the front end.
 * 
 * @author Michael Neale
 */
public interface SecurityServiceAsync extends RemoteService {


    public void login(String userName, String password, AsyncCallback cb);
    
    public void getCurrentUser(AsyncCallback cb);
    
}
