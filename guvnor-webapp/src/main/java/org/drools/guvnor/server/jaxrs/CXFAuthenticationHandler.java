/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.jaxrs;


import javax.security.auth.login.LoginException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

@Provider
public class CXFAuthenticationHandler implements RequestHandler {

    public Response handleRequest(Message m, ClassResourceInfo resourceClass) {
        if (Contexts.isApplicationContextActive()) {
            //If the request is from same session, the user should be logged already.
            Identity ids = Identity.instance();
            if (ids.isLoggedIn()) {
                return null;
            }

            AuthorizationPolicy policy = (AuthorizationPolicy) m
                    .get(AuthorizationPolicy.class);

            // The policy can be null when the user did not specify credentials
            if (policy != null) {
                String username = policy.getUserName();
                String password = policy.getPassword();

                ids.getCredentials().setUsername(username);
                ids.getCredentials().setPassword(password);
            }

            try {
                ids.authenticate();
                return null;
            } catch (LoginException e) {
                e.printStackTrace();
                throw new WebApplicationException(getErrorResponse());
            }
        } else {
            // NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            AuthorizationPolicy policy = (AuthorizationPolicy) m
                    .get(AuthorizationPolicy.class);

            if (policy == null || (("test").equals(policy.getUserName())
                    && ("password").equals(policy.getPassword()))) {
                return null;
            } else {
                throw new WebApplicationException(getErrorResponse());
            }
        }
    }
    
    private Response getErrorResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", "BASIC realm=\"users\"").build();
    }
}
