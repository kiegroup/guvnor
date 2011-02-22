/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.files;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.UUID;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.files.ActionsAPI.Parameters;
import org.drools.repository.RulesRepository;
import org.drools.util.codec.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Some basic unit tests for compilation and snapshot
 * creation in the ActionsAPIServlet.
 */
public class ActionAPIServletTest extends GuvnorTestBase {

    private final String compilationPath = "http://foo/action/compile";
    private final String snapshotPath    = "http://foo/action/snapshot";

    @Before
    public void setUp() {
        setUpSeamAndRepository();
        setUpMockIdentity();
    }

    @After
    public void tearDown() {
        tearAllDown();
    }

    /*
     * Modeled after testPost in RestAPIServletTest.
     */
    @Test
    public void testCompilation() throws Exception {
        final String dynamicPackage = "test-action" + UUID.randomUUID();
        RulesRepository repo = getRulesRepository();
        repo.createPackage( dynamicPackage,
                            "test-action package for testing" );
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( new Base64().encode( "test:password".getBytes() ) ) );
            }
        };
        HashMap<String, String> parameters = new HashMap<String, String>() {
            {
                put( Parameters.PackageName.toString(),
                     dynamicPackage );
            }
        };
        ActionsAPIServlet serv = new ActionsAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest( compilationPath,
                                                   headers,
                                                   parameters );
        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPost( req,
                     res );
        assertEquals( 200,
                      res.status );
        repo.logout();
    }

    @Test
    public void testSnapshotCreation() throws Exception {
        final String dynamicPackage = "test-snap" + UUID.randomUUID();
        RulesRepository repo = getRulesRepository();
        repo.createPackage( dynamicPackage,
                            "test-snapshot package for testing" );
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( new Base64().encode( "test:password".getBytes() ) ) );
            }
        };
        HashMap<String, String> parameters = new HashMap<String, String>() {
            {
                put( Parameters.PackageName.toString(),
                     dynamicPackage );
                put( Parameters.SnapshotName.toString(),
                     "test-action-snap1" );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some content".getBytes() );
        ActionsAPIServlet serv = new ActionsAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest( snapshotPath,
                                                   headers,
                                                   parameters,
                                                   in );
        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPost( req,
                     res );
        assertEquals( 200,
                      res.status );
        repo.logout();
    }

}
