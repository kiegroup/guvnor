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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.util.codec.Base64;
import org.junit.Test;

public class RestAPIServletTest extends GuvnorTestBase {

    @Inject
    private RestAPIServlet restAPIServlet;

    public RestAPIServletTest() {
        autoLoginAsAdmin = false;
    }

    @Test
    public void testGetRestServletNoLogin() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testGetRestServletNoLogin",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "" );
        ass.updateFormat( "drl" );
        ass.updateContent( "some content" );
        ass.checkin( "hey ho" );

        assertNotNull(restAPIServlet.getAPI());
        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Irrelevant",
                     "garbage" );
            }
        };
        String uri = "http://loser/api/packages/testGetRestServletNoLogin/asset1.drl";
        MockHTTPRequest req = new MockHTTPRequest( uri,
                                                   headers );
        MockHTTPResponse res = new MockHTTPResponse();

        //try with no password
        restAPIServlet.doGet( req,
                    res );
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                res.errorCode);
        assertTrue(res.headers.containsKey("WWW-Authenticate"));
    }

    @Test
    public void testGetRestServletBadLogin() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testGetRestServletBadLogin",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "" );
        ass.updateFormat( "drl" );
        ass.updateContent( "some content" );
        ass.checkin( "hey ho" );

        //try again with bad password
        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     new String( Base64.encodeBase64( "admin:invalidPwd".getBytes() ) ) );
            }
        };
        String uri = "http://loser/api/packages/testGetRestServletBadLogin/asset1.drl";
        MockHTTPRequest req = new MockHTTPRequest( uri,
                                   headers );
        MockHTTPResponse res = new MockHTTPResponse();
        restAPIServlet.doGet( req,
                    res );
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                res.errorCode);
        assertTrue(res.headers.containsKey("WWW-Authenticate"));
    }

    @Test
    public void testGetRestServlet() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testGetRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "" );
        ass.updateFormat( "drl" );
        ass.updateContent( "some content" );
        ass.checkin( "hey ho" );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "admin:admin".getBytes() ) ) );
            }
        };
        String uri = "http://loser/api/packages/testGetRestServlet/asset1.drl";
        MockHTTPRequest req = new MockHTTPRequest( uri,
                                   headers );
        MockHTTPResponse res = new MockHTTPResponse();
        restAPIServlet.doGet( req,
                    res );

        assertEquals(0,
                res.errorCode);
        String data = res.extractContent();
        assertEquals( "some content",
                      data );

        assertEquals( "application/x-download",
                      res.getContentType() );
        assertEquals( true,
                      res.containsHeader("Content-Disposition") );

        //now try getting some version listings
        req = new MockHTTPRequest( uri,
                                   headers );
        req.queryString = "version=all";
        res = new MockHTTPResponse();
        restAPIServlet.doGet(req,
                res);

        assertEquals( 0,
                      res.errorCode );
        data = res.extractContent();
        assertFalse( "some content".equals( data ) );
        assertNotNull( data );
    }

    @Test
    public void testPost() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testPostRestServlet",
                                              "" );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "admin:admin".getBytes() ) ) );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testPostRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        restAPIServlet.doPost(req,
                res);

        assertEquals( "OK",
                      res.extractContent() );

        AssetItemIterator it = pkg.listAssetsByFormat( "drl" );
        AssetItem ass = it.next();
        assertEquals( "asset1",
                      ass.getName() );
        assertEquals( "drl",
                      ass.getFormat() );
        assertFalse( ass.isBinary() );
        assertEquals( "some new content",
                      ass.getContent() );

        in = new ByteArrayInputStream( "more content".getBytes() );
        req = new MockHTTPRequest( "http://foo/api/packages/testPostRestServlet/asset2.xls",
                                   headers,
                                   in );
        res = new MockHTTPResponse();
        restAPIServlet.doPost(req,
                res);
        assertEquals( "OK",
                      res.extractContent() );

        pkg.getNode().refresh( false );
        AssetItem ass2 = pkg.loadAsset( "asset2" );
        assertEquals( "xls",
                      ass2.getFormat() );
        assertTrue( ass2.isBinary() );

        String out = new String( ass2.getBinaryContentAsBytes() );
        assertEquals( "more content",
                      out );
    }

    @Test
    public void testPut() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testPutRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "abc" );
        ass.updateFormat("drl");
        ass.checkin( "" );
        long ver = ass.getVersionNumber();

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "admin:admin".getBytes() ) ) );
                put( "Checkin-Comment",
                     "hey ho" );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testPutRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        restAPIServlet.doPut(req,
                res);

        assertEquals( "OK",
                      res.extractContent() );

        ass = pkg.loadAsset( "asset1" );
        pkg.getNode().refresh(false);
        assertEquals( "some new content",
                      ass.getContent() );
        assertEquals(ver + 1,
                ass.getVersionNumber());
        assertEquals("hey ho",
                ass.getCheckinComment());
    }

    @Test
    public void testDelete() throws Exception {
        PackageItem pkg = rulesRepository.createPackage( "testDeleteRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "abc" );
        ass.updateFormat("drl");
        ass.checkin( "" );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "admin:admin".getBytes() ) ) );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testDeleteRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        restAPIServlet.doDelete(req,
                res);

        assertEquals( "OK",
                      res.extractContent() );

        pkg = rulesRepository.loadPackage( "testDeleteRestServlet" );
        assertFalse( pkg.listAssetsByFormat( new String[]{"drl"} ).hasNext() );
    }

}
