/*
 *  Copyright 2010, ECOSUR and Andrew Waterman (andrew.waterman@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.guvnor.server.files;

import ch.ethz.ssh2.crypto.Base64;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.UUID;
import junit.framework.TestCase;
import org.drools.guvnor.server.rest.Parameters;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;

/**
 * Some basic unit tests for compilation and snapshot
 * creation in the ActionsAPIServlet.
 *
 * @author andrew.waterman@gmail.com
 */
public class ActionAPIServletTest extends TestCase {

    private final String compilationPath = "http://foo/action/compile";
    private final String snapshotPath = "http://foo/action/snapshot";

    /*
     * Modeled after testPost in RestAPIServletTest.
     * @author andrew.waterman@gmail.com
     */
    public void testCompilation() throws Exception {
        final String dynamicPackage = "test-action" + UUID.randomUUID();
        RulesRepository repo = new RulesRepository(
                TestEnvironmentSessionHelper.getSession());
        repo.createPackage(dynamicPackage, "test-action package for testing");
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode(
                        "test:password".getBytes())));
            }
        };
        HashMap<String,String> parameters = new HashMap<String,String>() {
            {
                put (Parameters.PackageName.toString(), dynamicPackage);
            }
        };
        ActionsAPIServlet serv = new ActionsAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest(compilationPath,
                headers, parameters);
        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPost(req, res);
        assertEquals(200, res.status);
        repo.logout();
    }

    public void testSnapshotCreation () throws Exception {
        final String dynamicPackage = "test-snap" + UUID.randomUUID();
        RulesRepository repo = new RulesRepository(
                TestEnvironmentSessionHelper.getSession());
        repo.createPackage(dynamicPackage, "test-snapshot package for testing");
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
            }
        };
        HashMap<String,String> parameters = new HashMap<String,String>() {
            {
                put (Parameters.PackageName.toString(), dynamicPackage);
                put (Parameters.SnapshotName.toString(), "test-action-snap1");
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream(
                "some content".getBytes());
        ActionsAPIServlet serv = new ActionsAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest(snapshotPath, headers,
                parameters, in);
        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPost(req, res);
        assertEquals(200, res.status);
        repo.logout();        
    }
}
