package org.drools.brms.server.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * This holds the current users info.
 * 
 * @author Michael Neale
 */
@Scope(ScopeType.SESSION)
@Name("currentUser")
@AutoCreate
public class UserIdentity {

    private String userName;

    public String getUserName() {
        if (userName == null) return "default";
        return userName;
    }
    
    public void setUserName(String n) {
        this.userName = n;
    }
    
}
