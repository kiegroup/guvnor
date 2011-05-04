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
        AuthorizationPolicy policy = (AuthorizationPolicy) m
                .get(AuthorizationPolicy.class);
        String username = policy.getUserName();
        String password = policy.getPassword();
        if (Contexts.isApplicationContextActive()) {
            System.out.println("-----CXFAuthenticationHandler.isApplicationContextActive");

            // If the request is from same session, the user should be logged
            // already.
            if (Identity.instance().isLoggedIn()) {
                return null;
            }

            Identity ids = Identity.instance();

            ids.getCredentials().setUsername(username);
            ids.getCredentials().setPassword(password);

            try {
                ids.authenticate();
                return null;
            } catch (LoginException e) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } else {           
            //NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            System.out.println("-----CXFAuthenticationHandler.Not isApplicationContextActive");
            if(username.equals( "test" ) && password.equals( "password" )) {
                return null;
            } else{
                Response r =  Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", "BASIC realm=\"users\"").build();

                throw new WebApplicationException(r);
            }
        }
    }
}
