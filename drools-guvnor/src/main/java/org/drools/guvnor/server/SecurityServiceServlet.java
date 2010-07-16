/**
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.rpc.SecurityService;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.server.security.SecurityServiceImpl;
import org.drools.guvnor.server.util.LoggingHelper;
import org.jboss.seam.security.AuthorizationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Wrapper for GWT RPC.
 * @author michaelneale
 *
 */
public class SecurityServiceServlet extends RemoteServiceServlet implements
		SecurityService {

    private static final LoggingHelper log              = LoggingHelper.getLogger(SecurityServiceServlet.class);
	SecurityService service = new SecurityServiceImpl();

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        if (e.getCause() instanceof AuthorizationException) {
            log.info(e.getMessage(), e);
            HttpServletResponse response = getThreadLocalResponse();
            try {
              response.setContentType("text/plain");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write(e.getCause().getMessage());
            } catch (IOException ex) {
              getServletContext().log(
                  "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                  ex);
            }
        } else {
            log.error(e.getMessage(), e.getCause());
            super.doUnexpectedFailure(e);
        }
    }


	public UserSecurityContext getCurrentUser() {
		return service.getCurrentUser();
	}

	public Capabilities getUserCapabilities() {
		return service.getUserCapabilities();
	}

	public boolean login(String userName, String password) {
		return service.login(userName, password);
	}

}
