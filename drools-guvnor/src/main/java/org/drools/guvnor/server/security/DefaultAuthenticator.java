package org.drools.guvnor.server.security;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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