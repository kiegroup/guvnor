/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler.drools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.files.MockHTTPRequest;
import org.drools.guvnor.server.files.MockHTTPResponse;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.util.codec.Base64;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceWarBuilderAndDownloadHandlerTest extends GuvnorTestBase {

    private static final String REST_SERVICE_CONFIG_CONTENT = "polling=70\n" +
            "protocol=REST\n" +
            "resource=pkgRef|a|drl|http://localhost/c/source|uuid1\n" +
            "resource=pkgRef|aa|drl|http://localhost/cc/source|uuid2\n" +
            "resource=pkgRef|ab|change_set|http://localhost/cd/source|uuid3\n";

    @Inject
    private ServiceWarBuilderAndDownloadHandler serviceHandler;

    @Test
    public void testGet() throws IOException, ServletException {

        final ModuleItem pkg = rulesRepository.createModule("testGetServiceConfigServlet",
                "");
        final AssetItem ass = pkg.addAsset("myAsset", "");
        ass.updateFormat("serviceConfig");
        ass.updateContent(REST_SERVICE_CONFIG_CONTENT);
        ass.checkin("hey ho, let's go!");

        assertNotNull(serviceHandler);

        final Map<String, String> headers = new HashMap<String, String>() {{
            put("Authorization", "BASIC " + new String(Base64.encodeBase64("admin:admin".getBytes())));
        }};
        final Map<String, String> parameters = new HashMap<String, String>() {{
            put("uuid", ass.getUUID());
        }};
        final String uri = "http://loser/api/packages/testGetServiceConfigServlet/myAsset.serviceConfig";

        MockHTTPRequest req = new MockHTTPRequest(uri, headers, parameters);
        MockHTTPResponse res = new MockHTTPResponse();

        serviceHandler.doGet(req, res);

        assertEquals("application/x-download", res.getContentType());
        assertEquals(true, res.containsHeader("Content-Disposition"));
    }

}

