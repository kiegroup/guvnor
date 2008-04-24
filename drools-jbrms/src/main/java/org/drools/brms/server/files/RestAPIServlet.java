package org.drools.brms.server.files;
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



import java.io.IOException;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.util.Base64;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.drools.repository.remoteapi.Response;
import org.drools.repository.remoteapi.RestAPI;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This servlet is the entry point for the rest API.
 *
 * @author Michael Neale
 */
public class RestAPIServlet extends RepositoryServlet {

    private static final long serialVersionUID = 500L;

    /**
     * This is used for importing legacy DRL.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {



    }



    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res) throws ServletException,
                                                 IOException {
        authAndInit(req, res);
        RestAPI api = null;
        Response apiRes = api.get(req.getRequestURI());
        apiRes.writeData(res.getOutputStream());

    }



    /**
     * Here we perform the action in the appropriate security context.
     * TODO: add in a closure for the action.
     */
	private void authAndInit(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String auth = req.getHeader("Authorization");

        if (!allowUser(auth)) {
          res.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
          res.sendError(res.SC_UNAUTHORIZED);
        }
        else {
          // Allowed, so do it already
        }
	}



	RestAPI getAPI() throws Exception {
		if (Contexts.isApplicationContextActive()) {
			RulesRepository repo = (RulesRepository) Component.getInstance( "repository" );
			return new RestAPI(repo);
		} else {
			RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) );
			return new RestAPI(repo);
		}
	}

    boolean allowUser(String auth) {
		if (auth == null) return false;  // no auth
        if (!auth.toUpperCase().startsWith("BASIC "))
          return false;  // we only do BASIC

        String[] a = unpack(auth);
        String usr = a[0];
        String pwd = a[1];
        if ( Contexts.isApplicationContextActive() ) {
           // return (FileManagerUtils) Component.getInstance( "fileManager" );
        	Identity ids = Identity.instance();
        	ids.setUsername(usr);
        	ids.setPassword(pwd);
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



	String[] unpack(String auth) {

        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(Base64.decode(userpassEncoded.getBytes()));

        String[] a = userpassDecoded.split(":");
        a[0] = a[0].trim();
        a[1] = a[1].trim();
		return a;
	}



	@Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
    		throws ServletException, IOException {
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
    		throws ServletException, IOException {
    }



}