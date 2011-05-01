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

package org.drools.guvnor.server.files;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.RepositoryPackageService;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.repository.RulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrance point to basic actions API.
 *
 * Fix for GUVNOR-1080
 */
public class ActionsAPIServlet extends RepositoryServlet {

    public static final Logger log = LoggerFactory.getLogger(
                                           ActionsAPIServlet.class );

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse res) throws ServletException,
                                                        IOException {
        final RulesRepository repository = RestAPIServlet.getRepository();
        final RepositoryPackageService service = RepositoryServiceServlet.getPackageService();

        doAuthorizedAction( req,
                            res,
                            new Command() {
                                public void execute() throws Exception {
                                    Map map = req.getParameterMap();
                                    ActionsAPI api = new ActionsAPI();
                                    api.post( service,
                                              repository,
                                              req,
                                              res );
                                }
                            } );
    }
}
