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

import org.drools.repository.RulesRepository;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.ServiceImplementation;
import org.apache.util.Base64;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Michael Neale
 */
public class FeedServletTest {

	@Test
    public void testPackageFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testPackageFeed", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.checkin("");
        
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(false);
        midentity.setCheckPermission(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );        
        FileManagerUtils manager = new FileManagerUtils();
        manager.setRepository(repo);
        Contexts.getSessionContext().set( "fileManager", manager );

 
        Map<String, String> headers = new HashMap<String, String>() {
            {
                put("Irrelevant", "garbage");
            }
        };

        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/package?name=...", headers);
        FeedServlet fs = new FeedServlet();
        MockHTTPResponse res = new MockHTTPResponse();
        fs.doGet(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);

        //try again with valid user and password
        midentity.setAllowLogin(true);

        headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("testuser:password".getBytes())));
            }
        };
        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
            }
        });
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);

        String r = res.extractContent();
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);


        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
                put("status", "Foo");
            }
        });
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);

        r = res.extractContent();
        assertNotNull(r);

        assertFalse(r.indexOf("asset1.drl") > -1);

        req = new MockHTTPRequest("/org.foo/feed/package", headers, new HashMap<String, String>() {
            {
                put("name", "testPackageFeed");
                put("viewUrl", "http://foo.bar");
                put("status", "Draft");
            }
        });
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);

        r = res.extractContent();
        assertNotNull(r);
        assertTrue(r.indexOf("asset1") > -1);
        
        Lifecycle.endApplication();
    }

	@Test
    public void testCategoryFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testCategoryFeed", "");
        repo.loadCategory("/").addCategory("testCategoryFeedCat", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.updateCategoryList(new String[] {"testCategoryFeedCat"});
        asset.checkin("");

        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(true);
        midentity.setCheckPermission(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        
        FileManagerUtils manager = new FileManagerUtils();
        manager.setRepository(repo);
        Contexts.getSessionContext().set( "fileManager", manager );


        //try with valid password
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("testuser:password".getBytes())));
            }
        };
        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/category", headers, new HashMap<String, String>() {
            {
                put("name", "testCategoryFeedCat");
                put("viewUrl", "http://foo.bar");
            }
        });
        FeedServlet fs = new FeedServlet();
        MockHTTPResponse res = new MockHTTPResponse();
        fs.doGet(req, res);

        String r = res.extractContent();
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);
        assertTrue(r.indexOf("http://foo.bar") > -1);


        req = new MockHTTPRequest("/org.foo/feed/category", headers, new HashMap<String, String>() {
            {
                put("name", "testCategoryFeedCat");
                put("viewUrl", "http://foo.bar");
                put("status", "*");
            }
        });
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);

        r = res.extractContent();
        assertNotNull(r);

        assertTrue(r.indexOf("asset1") > -1);
        assertTrue(r.indexOf("http://foo.bar") > -1);

        
        midentity.setAllowLogin(false);
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);

        Lifecycle.endApplication();
    }


	@Test
    public void testDiscussionFeed() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
        PackageItem pkg = repo.createPackage("testDiscussionFeed", "");
        AssetItem asset = pkg.addAsset("asset1", "desc");
        asset.updateFormat("drl");
        asset.checkin("");
        
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(false);
        midentity.setCheckPermission(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        
        FileManagerUtils manager = new FileManagerUtils();
        manager.setRepository(repo);
        Contexts.getSessionContext().set( "fileManager", manager );

        ServiceImplementation impl = new ServiceImplementation();
        impl.repository = repo;
        impl.addToDiscussionForAsset(asset.getUUID(), "This is a comment");
        impl.addToDiscussionForAsset(asset.getUUID(), "This is another comment");

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put("Irrelevant", "garbage");
            }
        };

        MockHTTPRequest req = new MockHTTPRequest("/org.foo/feed/discussion?package=...", headers);
        FeedServlet fs = new FeedServlet();
        MockHTTPResponse res = new MockHTTPResponse();
        fs.doGet(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);

        headers = new HashMap<String, String>() {
            {
                put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
            }
        };


        midentity.setAllowLogin(true);
        req = new MockHTTPRequest("/org.foo/feed/discussion", headers, new HashMap<String, String>() {
            {
                put("package", "testDiscussionFeed");
                put("assetName", "asset1");
            }
        });
        fs = new FeedServlet();
        res = new MockHTTPResponse();
        fs.doGet(req, res);

        String r = res.extractContent();
        assertNotNull(r);
        assertTrue(r.indexOf("This is a comment") > -1);
        assertTrue(r.indexOf("This is another comment") > r.indexOf("This is a comment"));
        System.err.println(r);

        Lifecycle.endApplication();
    }
    
    @After
    public void tearDown() throws Exception {
    	TestEnvironmentSessionHelper.shutdown();
    }
    
}
