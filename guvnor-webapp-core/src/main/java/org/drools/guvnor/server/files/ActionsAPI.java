/*
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

import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Basic API for executing simple actions against Guvnor:
 * compilation and snapshot creation for packages.
 * <p/>
 * Fix for GUVNOR-1080
 */
public class ActionsAPI {

    public enum Parameters {

        PackageName {
            public String toString() {
                return "package-name";
            }
        },

        SnapshotName {
            public String toString() {
                return "snapshot-name";
            }
        }

    }

    /**
     * Post is for actions.
     * <p/>
     * URL should be:  http://servername:port/action/compile
     * http://servername:port/action/snapshot
     * <p/>
     * parameters:  package-name
     * snapshot-name
     *
     * @throws IOException
     * @throws RulesRepositoryException
     */
    public void post(RepositoryModuleService service,
                     RulesRepository repository,
                     HttpServletRequest request,
                     HttpServletResponse response)
            throws IOException {
        try {
            String packageName = request.getParameter(Parameters.PackageName.toString());
            String[] pathstr = split(request.getPathTranslated());

            if (pathstr[0].equals("compile")) {
                if (repository.containsModule(packageName)) {
                    ModuleIterator iter = repository.listModules();
                    while (iter.hasNext()) {
                        ModuleItem p = iter.next();
                        if (p.getName().equals(packageName)) {
                            String uuid = p.getUUID();
                            service.buildPackage(uuid,
                                    true);
                            break;
                        }
                    }
                }
            } else if (pathstr[0].equals("snapshot")) if (repository.containsModule(packageName)) {
                String snapshotName = request.getParameter(Parameters.SnapshotName.toString());
                repository.createModuleSnapshot(packageName,
                        snapshotName);
            } else {
                throw new RulesRepositoryException("Unknown action request: "
                        + request.getContextPath());
            }

            response.setContentType("text/html");
            response.setStatus(200);
            response.getWriter().write("OK");

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Split from RestAPI
     */
    String[] split(String path) throws UnsupportedEncodingException {
        if (path.contains("action")) {
            path = path.split("action")[1];
        }
        if (path.startsWith("/")) path = path.substring(1);
        String[] bits = path.split("/");
        for (int i = 0; i < bits.length; i++) {
            bits[i] = URLDecoder.decode(bits[i],
                    "UTF-8");
        }
        return bits;
    }
}
