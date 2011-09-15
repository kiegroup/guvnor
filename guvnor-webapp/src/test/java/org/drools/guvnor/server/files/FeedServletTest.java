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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.util.codec.Base64;
import org.junit.Test;

public class FeedServletTest extends GuvnorTestBase {

    @Inject
    private FeedServlet feedServlet;

    @Test
    public void testPackageFeed() throws Exception {

        PackageItem pkg = rulesRepository.createPackage( "testPackageFeed",
                                              "" );
        AssetItem asset = pkg.addAsset( "asset1",
                                        "desc" );
        asset.updateFormat( "drl" );
        asset.checkin( "" );

        // TODO seam3upgrade
//        //Mock up SEAM contexts
//        MockIdentity midentity = new MockIdentity();
//        midentity.setIsLoggedIn( false );
//        midentity.setAllowLogin( false );
//        midentity.setCheckPermission( true );
//        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
//                                          midentity );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Irrelevant",
                     "garbage" );
            }
        };

        MockHTTPRequest req = new MockHTTPRequest( "/org.foo/feed/package",
                                                   headers,
                                                   new HashMap<String, String>() {
                                                       {
                                                           put( "name",
                                                                "testPackageFeed" );
                                                           put( "viewUrl",
                                                                "http://foo.bar" );
                                                       }
                                                   } );
        MockHTTPResponse res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED,
                      res.errorCode );

        // TODO seam3upgrade
//        //try again with valid user and password
//        midentity.setAllowLogin( true );

        headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "testuser:password".getBytes() ) ) );
            }
        };
        req = new MockHTTPRequest( "/org.foo/feed/package",
                                   headers,
                                   new HashMap<String, String>() {
                                       {
                                           put( "name",
                                                "testPackageFeed" );
                                           put( "viewUrl",
                                                "http://foo.bar" );
                                       }
                                   } );
        res = new MockHTTPResponse();
        feedServlet.doGet( req,
                  res );

        String r = res.extractContent();
        assertNotNull(r);

        assertTrue( r.indexOf( "asset1" ) > -1 );

        req = new MockHTTPRequest( "/org.foo/feed/package",
                                   headers,
                                   new HashMap<String, String>() {
                                       {
                                           put( "name",
                                                "testPackageFeed" );
                                           put( "viewUrl",
                                                "http://foo.bar" );
                                           put( "status",
                                                "Foo" );
                                       }
                                   } );
        res = new MockHTTPResponse();
        feedServlet.doGet( req,
                  res );

        r = res.extractContent();
        assertNotNull( r );

        assertFalse(r.indexOf("asset1.drl") > -1);

        req = new MockHTTPRequest( "/org.foo/feed/package",
                                   headers,
                                   new HashMap<String, String>() {
                                       {
                                           put( "name",
                                                "testPackageFeed" );
                                           put( "viewUrl",
                                                "http://foo.bar" );
                                           put( "status",
                                                "Draft" );
                                       }
                                   } );
        res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);

        r = res.extractContent();
        assertNotNull( r );
        assertTrue( r.indexOf( "asset1" ) > -1 );
    }

    @Test
    public void testCategoryFeed() throws Exception {

        PackageItem pkg = rulesRepository.createPackage( "testCategoryFeed",
                                              "" );
        rulesRepository.loadCategory("/").addCategory( "testCategoryFeedCat",
                                              "" );
        AssetItem asset = pkg.addAsset( "asset1",
                                        "desc" );
        asset.updateFormat( "drl" );
        asset.updateCategoryList( new String[]{"testCategoryFeedCat"} );
        asset.checkin( "" );

        // TODO seam3upgrade
//        //Mock up SEAM contexts
//        MockIdentity midentity = new MockIdentity();
//        midentity.setIsLoggedIn( false );
//        midentity.setAllowLogin( true );
//        midentity.setCheckPermission( true );
//        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
//                                          midentity );

        //try with valid password
        HashMap<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "testuser:password".getBytes() ) ) );
            }
        };
        MockHTTPRequest req = new MockHTTPRequest( "/org.foo/feed/category",
                                                   headers,
                                                   new HashMap<String, String>() {
                                                       {
                                                           put( "name",
                                                                "testCategoryFeedCat" );
                                                           put( "viewUrl",
                                                                "http://foo.bar" );
                                                       }
                                                   } );
        MockHTTPResponse res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);

        String r = res.extractContent();
        assertNotNull( r );

        assertTrue( r.indexOf( "asset1" ) > -1 );
        assertTrue( r.indexOf( "http://foo.bar" ) > -1 );

        req = new MockHTTPRequest( "/org.foo/feed/category",
                                   headers,
                                   new HashMap<String, String>() {
                                       {
                                           put( "name",
                                                "testCategoryFeedCat" );
                                           put( "viewUrl",
                                                "http://foo.bar" );
                                           put( "status",
                                                "*" );
                                       }
                                   } );
        res = new MockHTTPResponse();
        feedServlet.doGet( req,
                  res );

        r = res.extractContent();
        assertNotNull( r );

        assertTrue(r.indexOf("asset1") > -1);
        assertTrue( r.indexOf( "http://foo.bar" ) > -1 );

        // TODO seam3upgrade
//        midentity.setAllowLogin( false );
        res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED,
                      res.errorCode );
    }

    @Test
    public void testDiscussionFeed() throws Exception {

        PackageItem pkg = rulesRepository.createPackage( "testDiscussionFeed",
                                              "" );
        AssetItem asset = pkg.addAsset( "asset1",
                                        "desc" );
        asset.updateFormat( "drl" );
        asset.checkin( "" );

        // TODO seam3upgrade
//        //Mock up SEAM contexts
//        MockIdentity midentity = new MockIdentity();
//        midentity.setIsLoggedIn( false );
//        midentity.setAllowLogin( false );
//        midentity.setCheckPermission( true );
//        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
//                                          midentity );

        repositoryAssetService.setRulesRepository( rulesRepository );
        repositoryAssetService.addToDiscussionForAsset( asset.getUUID(),
                                                        "This is a comment" );
        repositoryAssetService.addToDiscussionForAsset( asset.getUUID(),
                                                        "This is another comment" );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Irrelevant",
                     "garbage" );
            }
        };

        MockHTTPRequest req = new MockHTTPRequest( "/org.foo/feed/discussion",
                                                   headers,
                                                   new HashMap<String, String>() {
                                                       {
                                                           put( "package",
                                                                "testDiscussionFeed" );
                                                           put( "assetName",
                                                                "asset1" );
                                                       }
                                                   } );
        MockHTTPResponse res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);
        assertEquals( HttpServletResponse.SC_UNAUTHORIZED,
                      res.errorCode );

        headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + new String( Base64.encodeBase64( "test:password".getBytes() ) ) );
            }
        };

        // TODO seam3upgrade
//        midentity.setAllowLogin( true );
        req = new MockHTTPRequest( "/org.foo/feed/discussion",
                                   headers,
                                   new HashMap<String, String>() {
                                       {
                                           put( "package",
                                                "testDiscussionFeed" );
                                           put( "assetName",
                                                "asset1" );
                                       }
                                   } );
        res = new MockHTTPResponse();
        feedServlet.doGet(req,
                res);

        String r = res.extractContent();
        assertNotNull( r );
        assertTrue( r.indexOf( "This is a comment" ) > -1 );
        assertTrue( r.indexOf( "This is another comment" ) > r.indexOf( "This is a comment" ) );
        System.err.println( r );
    }

}
