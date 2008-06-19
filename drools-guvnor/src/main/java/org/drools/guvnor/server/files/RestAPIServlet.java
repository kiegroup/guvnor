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



import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.util.Base64;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
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
public class RestAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 500L;
    public static final Logger log              = Logger.getLogger( RestAPIServlet.class );

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse res) throws ServletException,
                                                       IOException {
        doAuthorizedAction(req, res, new A() {
			public void a() throws Exception {
					res.setContentType( "text/html" );
					RestAPI api = getAPI();
					String comment = req.getHeader("Checkin-Comment");
					api.post(req.getRequestURI(), req.getInputStream(), (comment != null)? comment : "");
					res.getWriter().write( "OK" );
			}
        });
    }



    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse res) throws ServletException,
                                                 IOException {
        doAuthorizedAction(req, res, new A() {
			public void a() throws Exception {
					RestAPI api = getAPI();
					Response apiRes = api.get(req.getRequestURI());
			        res.setContentType( "application/x-download" );
			        res.setHeader( "Content-Disposition",
			                       "attachment; filename=data;");
					apiRes.writeData(res.getOutputStream());
					res.getOutputStream().flush();
			}
        });

    }

	@Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse res)
    		throws ServletException, IOException {
        doAuthorizedAction(req, res, new A() {
			public void a() throws Exception {
					res.setContentType( "text/html" );
					RestAPI api = getAPI();
					String comment = req.getHeader("Checkin-Comment");
					Calendar lastMod = getModified(req.getHeader("Last-Modified"));
					api.put(req.getRequestURI(), lastMod, req.getInputStream(),  (comment != null)? comment : "");
					res.getWriter().write( "OK" );
			}
        });
    }

    Calendar getModified(String f) throws ParseException {

		if (f == null) return null;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = RestAPI.getISODateFormat();
		try {
			c.setTime(sdf.parse(f));
		} catch (ParseException e) {
			DateFormat df = DateFormat.getInstance();
			c.setTime(df.parse(f));
		}
		return c;
	}



	@Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse res)
    		throws ServletException, IOException {
        doAuthorizedAction(req, res, new A() {
			public void a() throws Exception {
					res.setContentType( "text/html" );
					RestAPI api = getAPI();
					api.delete(req.getRequestURI());
					res.getWriter().write( "OK" );
			}
        });
    }



    /**
     * Here we perform the action in the appropriate security context.
     */
	private void doAuthorizedAction(HttpServletRequest req, HttpServletResponse res, A action) throws IOException {
        String auth = req.getHeader("Authorization");
        if (!allowUser(auth)) {
          res.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
        	try {
        		action.a();
        	} catch (Exception e) {
        		log.error(e);
        		throw new RuntimeException(e);
        	}
        }
	}



	/**
	 * Get a repository instance.
	 * This will use the Seam identity component, or it will just
	 * return a repo for test puposes if seam is not active.
	 */
	static RulesRepository getRepository() {
		if (Contexts.isApplicationContextActive()) {
			RulesRepository repo = (RulesRepository) Component.getInstance( "repository" );
			return repo;
		} else {
			try {
				RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( false ) );
				return repo;
			} catch (Exception e) {
				throw new IllegalStateException("Unable to get repo to run tests", e);
			}

		}

	}

	RestAPI getAPI()  {
		return new RestAPI(getRepository());
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



	static String[] unpack(String auth) {

        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(Base64.decode(userpassEncoded.getBytes()));

        String[] a = userpassDecoded.split(":");
        a[0] = a[0].trim();
        a[1] = a[1].trim();
		return a;
	}






    /**
     * For closures. Damn you java when will you catch up with the 70s.
     */
    static interface A { public void a() throws Exception; }

}