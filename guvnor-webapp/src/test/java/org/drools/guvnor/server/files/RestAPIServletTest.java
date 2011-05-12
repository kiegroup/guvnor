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

import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.util.codec.Base64;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.junit.Before;
import org.junit.Test;

public class RestAPIServletTest extends GuvnorTestBase {

    @Before
    public void setup() {
        setUpFileManagerUtils();
    }

    @Test
    public void testGet() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testGetRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "" );
        ass.updateFormat( "drl" );
        ass.updateContent( "some content" );
        ass.checkin( "hey ho" );

        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn( false );
        midentity.setAllowLogin( false );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

        RestAPIServlet serv = new RestAPIServlet();
        assertNotNull( serv.getAPI() );
        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Irrelevant",
                     "garbage" );
            }
        };
        String uri = "http://loser/api/packages/testGetRestServlet/asset1.drl";
        MockHTTPRequest req = new MockHTTPRequest( uri,
                                                   headers );

        MockHTTPResponse res = new MockHTTPResponse();

        //try with no password
        serv.doGet( req,
                    res );
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED,
                      res.errorCode );
        assertTrue( res.headers.containsKey( "WWW-Authenticate" ) );

        //try again with bad password
        headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     new String( Base64.encodeBase64( "foo:bar".getBytes() ) ) );
            }
        };
        req = new MockHTTPRequest( uri,
                                   headers );
        res = new MockHTTPResponse();
        serv.doGet( req,
                    res );
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED,
                      res.errorCode );
        assertTrue( res.headers.containsKey( "WWW-Authenticate" ) );

        //finally, making it work
        midentity.setAllowLogin( true );

        headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "testuser:password".getBytes() ) ) );
            }
        };

        req = new MockHTTPRequest( uri,
                                   headers );
        res = new MockHTTPResponse();
        serv.doGet( req,
                    res );

        assertEquals( 0,
                      res.errorCode );
        String data = res.extractContent();
        assertEquals( "some content",
                      data );

        assertEquals( "application/x-download",
                      res.getContentType() );
        assertEquals( true,
                      res.containsHeader( "Content-Disposition" ) );

        //now try getting some version listings
        req = new MockHTTPRequest( uri,
                                   headers );
        req.queryString = "version=all";
        res = new MockHTTPResponse();
        serv.doGet( req,
                    res );

        assertEquals( 0,
                      res.errorCode );
        data = res.extractContent();
        assertFalse( "some content".equals( data ) );
        assertNotNull( data );

        Lifecycle.endApplication();
    }

    @Test
    public void testPost() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testPostRestServlet",
                                              "" );

        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn( false );
        midentity.setAllowLogin( true );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "test:password".getBytes() ) ) );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        RestAPIServlet serv = new RestAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testPostRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPost( req,
                     res );

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
        serv.doPost( req,
                     res );
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

        repo.logout();

        Lifecycle.endApplication();
    }

    @Test
    public void testPut() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testPutRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "abc" );
        ass.updateFormat( "drl" );
        ass.checkin( "" );
        long ver = ass.getVersionNumber();

        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn( false );
        midentity.setAllowLogin( true );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "test:password".getBytes() ) ) );
                put( "Checkin-Comment",
                     "hey ho" );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        RestAPIServlet serv = new RestAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testPutRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        serv.doPut( req,
                    res );

        assertEquals( "OK",
                      res.extractContent() );

        ass = pkg.loadAsset( "asset1" );
        pkg.getNode().refresh( false );
        assertEquals( "some new content",
                      ass.getContent() );
        assertEquals( ver + 1,
                      ass.getVersionNumber() );
        assertEquals( "hey ho",
                      ass.getCheckinComment() );

        repo.logout();

        Lifecycle.endApplication();
    }

    @Test
    public void testDelete() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testDeleteRestServlet",
                                              "" );
        AssetItem ass = pkg.addAsset( "asset1",
                                      "abc" );
        ass.updateFormat( "drl" );
        ass.checkin( "" );

        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn( false );
        midentity.setAllowLogin( true );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        FileManagerUtils manager = new FileManagerUtils();
        manager.setRepository( repo );
        Contexts.getSessionContext().set( "fileManager",
                                          manager );
        Contexts.getSessionContext().set( "repository",
                                          repo );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "test:password".getBytes() ) ) );
            }
        };

        ByteArrayInputStream in = new ByteArrayInputStream( "some new content".getBytes() );
        RestAPIServlet serv = new RestAPIServlet();
        MockHTTPRequest req = new MockHTTPRequest( "http://foo/api/packages/testDeleteRestServlet/asset1.drl",
                                                   headers,
                                                   in );

        MockHTTPResponse res = new MockHTTPResponse();
        serv.doDelete( req,
                       res );

        assertEquals( "OK",
                      res.extractContent() );

        pkg = repo.loadPackage( "testDeleteRestServlet" );
        assertFalse( pkg.listAssetsByFormat( new String[]{"drl"} ).hasNext() );

        repo.logout();

        Lifecycle.endApplication();
    }

}
