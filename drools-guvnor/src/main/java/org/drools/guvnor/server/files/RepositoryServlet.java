package org.drools.guvnor.server.files;
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



import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.apache.util.Base64;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.contexts.Contexts;

import java.io.IOException;

/**
 * This is a base servlet that all repo servlets inherit behaviour from. 
 * 
 * @author Michael Neale
 */
public class RepositoryServlet extends HttpServlet {

    private static final long  serialVersionUID = 400L;
    //    protected final FileManagerUtils uploadHelper = new FileManagerUtils();
    public static final Logger log              = Logger.getLogger( RepositoryServlet.class );

    //    protected RulesRepository getRepository() {
    //
    //        if ( Contexts.isApplicationContextActive() ) {
    //            return (RulesRepository) Component.getInstance( "repository" );
    //        } else {
    //            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
    //            //THIS IS ALL THAT IS NEEDED FOR THE SERVLETS.
    //            log.debug( "WARNING: RUNNING IN NON SEAM MODE SINGLE USER MODE - ONLY FOR TESTING AND DEBUGGING !!!!!" );
    //
    //            try {
    //                return new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) );
    //            } catch ( Exception e ) {
    //                throw new IllegalStateException( "Unable to launch debug mode..." );
    //            }
    //        }
    //    }   

    public FileManagerUtils getFileManager() {
        if ( Contexts.isApplicationContextActive() ) {
            return (FileManagerUtils) Component.getInstance( "fileManager" );
        } else {
            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            //THIS IS ALL THAT IS NEEDED FOR THE SERVLETS.
            log.debug( "WARNING: RUNNING IN NON SEAM MODE SINGLE USER MODE - ONLY FOR TESTING AND DEBUGGING !!!!!" );
            FileManagerUtils manager = new FileManagerUtils();
            try {
                manager.setRepository(new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) ));
                return manager;
            } catch ( Exception e ) {
                throw new IllegalStateException();
            }
            
        }
    }


    /**
     * Here we perform the action in the appropriate security context.
     */
	void doAuthorizedAction(HttpServletRequest req, HttpServletResponse res, A action) throws IOException {
        String auth = req.getHeader("Authorization");
        if (!allowUser(auth)) {
          res.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
        	try {
        		action.a();
        	} catch (RuntimeException e) {
        		log.error(e);
        		throw e;
        	} catch (Exception e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
	}


    /**
     * Check the users credentials.
     * This takes the Authorization string from the HTTP request header (the whole lot).
     * uses Seam Identity component to set the user up.
     */
    public static boolean allowUser(String auth) {
        if (auth == null) return false;  // no auth
        if (!auth.toUpperCase().startsWith("BASIC "))
          return false;  // we only do BASIC

        String[] a = unpack(auth);
        String usr = a[0];
        String pwd = a[1];
        if ( Contexts.isApplicationContextActive() ) {
           // return (FileManagerUtils) Component.getInstance( "fileManager" );
            Identity ids = Identity.instance();
            ids.getCredentials().setUsername(usr);
            ids.getCredentials().setPassword(pwd);
            try {
                ids.authenticate();
                return true;
            } catch (LoginException e) {
                log.warn("Unable to authenticate for rest api: " + usr);
                return false;
            }
        } else {
            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            return usr.equals("test") && pwd.equals("password");
        }

    }


    /**
     * For closures. Damn you java when will you catch up with the 70s.
     */
    static interface A { public void a() throws Exception; }


    	static String[] unpack(String auth) {

        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(Base64.decode(userpassEncoded.getBytes()));

        String[] a = userpassDecoded.split(":");
        a[0] = a[0].trim();
        a[1] = a[1].trim();
		return a;
	}


}