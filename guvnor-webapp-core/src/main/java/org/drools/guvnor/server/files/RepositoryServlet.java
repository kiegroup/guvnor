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

import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.RulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a base servlet that all repo servlets inherit behaviour from.
 */
public class RepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Inject @Preferred
    protected RulesRepository rulesRepository;

    @Inject
    protected AuthorizationHeaderChecker authorizationHeaderChecker;

    /**
     * Here we perform the action in the appropriate security context.
     */
    void doAuthorizedAction(HttpServletRequest req,
                            HttpServletResponse res,
                            Command action) throws IOException {
        String auth = req.getHeader("Authorization");

        if (!authorizationHeaderChecker.loginByHeader(auth)) {
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

    static interface Command {
        public void execute() throws Exception;
    }

}
