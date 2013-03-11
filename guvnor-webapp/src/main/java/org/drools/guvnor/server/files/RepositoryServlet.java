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

package org.drools.guvnor.server.files;

import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.drools.util.codec.Base64;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * This is a base servlet that all repo servlets inherit behaviour from.
 */
public class RepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;
    static final Logger log = LoggerFactory.getLogger(RepositoryServlet.class);

    public static FileManagerUtils getFileManager() {
        if (Contexts.isApplicationContextActive()) {
            return (FileManagerUtils) Component.getInstance("fileManager");
        } else {
            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            //THIS IS ALL THAT IS NEEDED FOR THE SERVLETS.
            log.debug("WARNING: RUNNING IN NON SEAM MODE SINGLE USER MODE - ONLY FOR TESTING AND DEBUGGING !!!!!");
            FileManagerUtils manager = new FileManagerUtils();
            try {
                manager.setRepository(new RulesRepository(TestEnvironmentSessionHelper.getSession(false)));
                return manager;
            } catch (Exception e) {
                throw new IllegalStateException();
            }

        }
    }

    /**
     * Here we perform the action in the appropriate security context.
     */
    void doAuthorizedAction(HttpServletRequest req,
                            HttpServletResponse res,
                            Command action) throws IOException {
        String auth = req.getHeader("Authorization");

        if (!allowUser(auth)) {
            res.setHeader("WWW-Authenticate",
                    "BASIC realm=\"users\"");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            try {
                action.execute();
            } catch (RuntimeException e) {
                log.error(e.getMessage(),
                        e);
                throw e;
            } catch (Exception e) {
                log.error(e.getMessage(),
                        e);
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
        String usr = null;
        String pwd = null;

        if (Contexts.isApplicationContextActive()) {
            //If the request is from same session, the user should be logged already.
            if (Identity.instance().isLoggedIn()) {
                return true;
            }

            Identity ids = Identity.instance();
            if (auth != null && auth.toUpperCase(Locale.ENGLISH).startsWith("BASIC ")) {
                String[] a = unpack(auth);
                usr = a[0];
                pwd = a[1];
                ids.getCredentials().setUsername(usr);
                ids.getCredentials().setPassword(pwd);
            }
            try {
                ids.authenticate();
                log.info(usr + " authenticated for rest api");

                return true;
            } catch (LoginException e) {
                log.warn("Unable to authenticate for rest api: " + usr);
                return false;
            }
        } else {
            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            String[] a = unpack(auth);
            usr = a[0];
            pwd = a[1];

            return usr.equals("test") && pwd.equals("password");
        }

    }

    static interface Command {
        public void execute() throws Exception;
    }

    static String[] unpack(String auth) {

        // Get encoded user and password, comes after "BASIC "
        if (Contexts.isApplicationContextActive()) {
            String userpassEncoded = auth.substring(6);
            String userpassDecoded = new String(Base64.decodeBase64(userpassEncoded.getBytes()));

            String[] a = userpassDecoded.split(":", 2);
            for (int i = 0; i < a.length; i++) {
                a[i] = a[i].trim();
            }
            if (a.length == 2) {
                return a;
            } else if (a.length == 1) {
                //pwd is empty
                return new String[]{a[0], ""};
            } else {
                return new String[]{"", ""};
            }
        } else {
            return new String[]{"test", "password"};
        }
    }
}
