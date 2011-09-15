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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import net.sf.webdav.ITransaction;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class WebDAVImplTest extends GuvnorTestBase {

    @Inject
    protected WebDAVImpl webDAV;

    @Test
    public void testPath() {
        String[] path = webDAV.getPath( "http://goober/whee/webdav/packages/packagename/resource.drl",
                                     true );
        assertEquals("packages",
                path[0]);
        assertEquals( "packagename",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = webDAV.getPath( "foo/webdav",
                            true );
        assertEquals( 0,
                      path.length );

        path = webDAV.getPath( "/" );
        assertEquals( 0,
                      path.length );

        path = webDAV.getPath( "/packages/packagename/resource.drl" );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "packagename",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );
    }

    //GUVNOR-669
    @Test
    public void testPathContainsWebdav() {
        String[] path = webDAV.getPath( "http://goober/whee/webdav/packages/ssswebdavss/resource.drl",
                                     true );
        assertEquals("packages",
                path[0]);
        assertEquals( "ssswebdavss",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = webDAV.getPath( "foo/webdav",
                            true );
        assertEquals( 0,
                      path.length );

        path = webDAV.getPath( "/" );
        assertEquals( 0,
                      path.length );

        path = webDAV.getPath( "/packages/ssswebdavss/resource.drl" );
        assertEquals("packages",
                path[0]);
        assertEquals( "ssswebdavss",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = webDAV.getPath( "http://goober/whee/webdav/packages/webdav/resource.drl",
                            true );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "webdav",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = webDAV.getPath( "/packages/webdav/resource.drl" );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "webdav",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );
    }

    @Test
    public void testBadCopy() throws Exception {
        //OSX does stupid shit when copying in the same directory
        //for instance, it creates the copy as foobar.x copy - totally hosing
        //the file extension.
        try {
            webDAV.objectExists("/foo/webdav/packages/foobar/Something.drl copy 42");
            fail( "should not be allowed" );
        } catch ( IllegalArgumentException e ) {
            assertNotNull( e.getMessage() );
        }

    }

    @Test
    public void testChildrenNames() throws Exception {
        try {
            String[] children = webDAV.getChildrenNames( new TransactionMock(),
                                                      "/packages" );
            assertTrue(children.length > 0);
            int packageCount = children.length;

            PackageItem pkg = rulesRepository.createPackage( "testWebDavChildNames1",
                                                  "" );
            rulesRepository.createPackage("testWebDavChildNames2",
                    "");
            rulesRepository.save();
            children = webDAV.getChildrenNames( new TransactionMock(),
                                             "/packages" );
            assertEquals( packageCount + 2,
                          children.length );
            assertContains("testWebDavChildNames1",
                    children);
            assertContains("testWebDavChildNames2",
                    children);

            AssetItem asset = pkg.addAsset( "asset1",
                                            "something" );
            asset.updateFormat("drl");
            asset.checkin("");
            asset = pkg.addAsset( "asset2",
                                  "something" );
            asset.updateFormat("dsl");
            asset.checkin( "" );

            children = webDAV.getChildrenNames( new TransactionMock(),
                                             "/packages/testWebDavChildNames1" );
            assertEquals(2,
                    children.length);
            assertEquals( "asset1.drl",
                          children[0] );
            assertEquals("asset2.dsl",
                    children[1]);

            children = webDAV.getChildrenNames( new TransactionMock(),
                                             "/packages/testWebDavChildNames1/asset1.drl" );
            assertNull( children );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testCreateFolder() throws Exception {
        try {
            String[] children = webDAV.getChildrenNames( new TransactionMock(),
                                                      "/packages" );
            int packageCount = children.length;

            webDAV.createFolder(new TransactionMock(),
                    "/packages/testCreateWebDavFolder");
            children = webDAV.getChildrenNames( new TransactionMock(),
                                             "/packages" );

            assertEquals( packageCount + 1,
                          children.length );
            assertContains("testCreateWebDavFolder",
                    children);

            PackageItem pkg = rulesRepository.loadPackage( "testCreateWebDavFolder" );
            assertNotNull( pkg );

            pkg.addAsset( "someAsset",
                          "" );

            try {
                webDAV.createFolder(new TransactionMock(),
                        "/somethingElse");
                fail( "this should not work !" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testDates() throws Exception {
        /*        String uri = "/foo/webdav";
                WebDAVImpl imp = getImpl();
                assertNotNull( imp.getCreationDate( uri ) );
                assertNotNull( imp.getLastModified( uri ) );*/

        String uri = "/packages";
        try {
            assertNotNull(webDAV.getCreationDate(uri));
            assertNotNull( webDAV.getLastModified( uri ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testCreateResourceAndCreatedDate() throws Exception {
        try {
            webDAV.createFolder( new TransactionMock(),
                              "/packages/testCreateResourceAndCreatedDate" );

            Thread.sleep( 100 );

            webDAV.createResource( new TransactionMock(),
                                "/packages/testCreateResourceAndCreatedDate/asset.drl" );

            String[] resources = webDAV.getChildrenNames( new TransactionMock(),
                                                       "/packages/testCreateResourceAndCreatedDate" );
            assertEquals( 1,
                          resources.length );
            assertEquals( "asset.drl",
                          resources[0] );

            //should be ignored
            webDAV.createResource( new TransactionMock(),
                                "/packages/testCreateResourceAndCreatedDate/._asset.drl" );
            webDAV.createResource( new TransactionMock(),
                                "/packages/.DS_Store" );

            PackageItem pkg = rulesRepository.loadPackage( "testCreateResourceAndCreatedDate" );
            assertFalse( pkg.containsAsset( "._asset" ) );
            assertTrue( pkg.containsAsset( "asset" ) );

            Iterator<AssetItem> it = pkg.getAssets();
            AssetItem ass = it.next();
            assertEquals( "asset",
                          ass.getName() );
            assertEquals( "drl",
                          ass.getFormat() );

            Date create = webDAV.getCreationDate( "/packages/testCreateResourceAndCreatedDate" );
            assertNotNull( create );
            assertTrue( create.after( new Date( "10-Jul-1974" ) ) );

            Date assetCreate = webDAV.getCreationDate( "/packages/testCreateResourceAndCreatedDate/asset.drl" );
            assertTrue( assetCreate.after( create ) );

            Date lm = webDAV.getLastModified( "/packages/testCreateResourceAndCreatedDate" );
            assertNotNull( lm );
            assertTrue( lm.after( new Date( "10-Jul-1974" ) ) );

            Date alm = webDAV.getLastModified( "/packages/testCreateResourceAndCreatedDate/asset.drl" );
            assertTrue( alm.after( lm ) );

            try {
                webDAV.createResource( new TransactionMock(),
                                    "/hummer.drl" );
                fail( "Shouldn't be able to do this" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }
    }

    @Test
    public void testResourceContent() throws Exception {
        try {
            PackageItem pkg = rulesRepository.createPackage( "testWebDAVContent",
                                                  "" );

            AssetItem asset = pkg.addAsset("asset",
                    "something");
            asset.updateFormat( "drl" );
            asset.updateContent("Some content");
            asset.checkin("");
            InputStream data = webDAV.getResourceContent( new TransactionMock(),
                                                       "/packages/testWebDAVContent/asset.drl" );
            assertEquals( "Some content",
                          IOUtils.toString( data ) );

            asset = pkg.addAsset( "asset2",
                                  "something" );
            asset.updateFormat("xls");
            asset.updateBinaryContentAttachment( IOUtils.toInputStream( "This is binary" ) );
            asset.checkin("");

            data = webDAV.getResourceContent( new TransactionMock(),
                                           "/packages/testWebDAVContent/asset2.xls" );
            assertEquals("This is binary",
                    IOUtils.toString(data));

            AssetItem asset_ = pkg.addAsset( "somethingelse",
                                             "" );
            asset_.updateFormat("drl");
            asset_.checkin("");

            data = webDAV.getResourceContent( new TransactionMock(),
                                           "/packages/testWebDAVContent/somethingelse.drl" );
            assertEquals( "",
                          IOUtils.toString( data ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testIsFolder() throws Exception {
        try {
            assertTrue(webDAV.isFolder("/packages"));
            assertTrue( webDAV.isFolder( "/packages/" ) );
            assertFalse(webDAV.isFolder("/packages/somePackage"));

            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDAVIsFolder" );
            assertTrue(webDAV.isFolder("/packages/testDAVIsFolder"));
            assertFalse( webDAV.isFolder( "/packages/somePackage/SomeFile.drl" ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }
    }

    @Test
    public void testIsResource() throws Exception {
        try {
            assertFalse(webDAV.isResource("/packages"));
            assertFalse( webDAV.isResource( "/packages/somePackage" ) );
            assertFalse(webDAV.isResource("/packages/somePackage/SomeFile.drl"));

            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDAVIsResource" );
            webDAV.createResource(new TransactionMock(),
                    "/packages/testDAVIsResource/SomeFile.drl");

            assertTrue( webDAV.isResource( "/packages/testDAVIsResource/SomeFile.drl" ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testResourceLength() throws Exception {
        try {
            assertEquals(0,
                    webDAV.getResourceLength(new TransactionMock(),
                            "/webdav/packages"));
            webDAV.createFolder( new TransactionMock(),
                              "/packages/testResourceLengthDAV" );
            webDAV.createResource(new TransactionMock(),
                    "/packages/testResourceLengthDAV/testResourceLength");
            assertEquals(0,
                    webDAV.getResourceLength(new TransactionMock(),
                            "/packages/testResourceLengthDAV/testResourceLength"));
            webDAV.setResourceContent(new TransactionMock(),
                    "/packages/testResourceLengthDAV/testResourceLength",
                    IOUtils.toInputStream("some input"),
                    null,
                    null);
            assertEquals( "some input".getBytes().length,
                          webDAV.getResourceLength( new TransactionMock(),
                                                 "/packages/testResourceLengthDAV/testResourceLength" ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testObjectExists() throws Exception {
        try {
            assertTrue(webDAV.objectExists("/packages"));

            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDavObjectExists" );
            assertTrue(webDAV.objectExists("/packages/testDavObjectExists"));
            assertFalse(webDAV.objectExists("/packages/testDavObjectExistsXXXX"));
            assertFalse( webDAV.objectExists( "/packages/testDavObjectExists/foobar.drl" ) );
            assertFalse(webDAV.objectExists("/packages/testDavObjectExistsXXXX/foobar.drl"));
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testRemoveObject() throws Exception {
        try {
            assertFalse( webDAV.objectExists( "/packages/testDavRemoveObjectFolder" ) );
            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertTrue( webDAV.objectExists( "/packages/testDavRemoveObjectFolder" ) );
            webDAV.removeObject( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertFalse( webDAV.objectExists( "/packages/testDavRemoveObjectFolder" ) );

            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectAsset" );
            webDAV.createResource( new TransactionMock(),
                                "/packages/testDavRemoveObjectAsset/asset.drl" );

            AssetItem as = rulesRepository.loadPackage( "testDavRemoveObjectAsset" ).loadAsset( "asset" );
            long origVer = as.getVersionNumber();

            assertTrue( webDAV.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );
            webDAV.removeObject( new TransactionMock(),
                              "/packages/testDavRemoveObjectAsset/asset.drl" );
            assertFalse( webDAV.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );
            assertTrue( webDAV.objectExists( "/packages/testDavRemoveObjectAsset" ) );

            webDAV.createResource( new TransactionMock(),
                                "/packages/testDavRemoveObjectAsset/asset.drl" );
            assertTrue( webDAV.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );

            as = rulesRepository.loadPackage( "testDavRemoveObjectAsset" ).loadAsset( "asset" );
            assertTrue( as.getVersionNumber() > origVer );
            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertTrue( webDAV.objectExists( "/packages/testDavRemoveObjectFolder" ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testSetContent() throws Exception {

        //WebDAVImpl.setResourceContent adds a trailing \n
        final String CONTENT1 = "some input";
        final String EXPECTED_CONTENT1 = CONTENT1 + "\n";

        final String CONTENT2 = "some more input";
        final String EXPECTED_CONTENT2 = CONTENT2 + "\n";

        try {
            webDAV.createFolder(new TransactionMock(),
                    "/packages/testSetDavContent");
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            webDAV.createResource( new TransactionMock(),
                                "/packages/testSetDavContent/Something.drl" );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            webDAV.setResourceContent( new TransactionMock(),
                                    "/packages/testSetDavContent/Something.drl",
                                    IOUtils.toInputStream( CONTENT1 ),
                                    null,
                                    null );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            webDAV.getResourceContent(new TransactionMock(),
                    "/packages/testSetDavContent/Something.drl");
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            AssetItem as = rulesRepository.loadPackage("testSetDavContent").loadAsset( "Something" );
            assertTrue(as.isBinary());

            String result = IOUtils.toString( webDAV.getResourceContent( new TransactionMock(),
                                                                      "/packages/testSetDavContent/Something.drl" ) );
            assertEquals( EXPECTED_CONTENT1,
                          result );

            PackageItem pkg = rulesRepository.loadPackage( "testSetDavContent" );
            AssetItem asset = pkg.loadAsset( "Something" );
            assertEquals( "drl",
                          asset.getFormat() );
            assertEquals(EXPECTED_CONTENT1,
                    asset.getContent());
            assertEquals( EXPECTED_CONTENT1,
                          IOUtils.toString( asset.getBinaryContentAttachment() ) );

            webDAV.setResourceContent(new TransactionMock(),
                    "/packages/testSetDavContent/Something.drl",
                    IOUtils.toInputStream(CONTENT2),
                    null,
                    null);
            result = IOUtils.toString( webDAV.getResourceContent( new TransactionMock(),
                                                               "/packages/testSetDavContent/Something.drl" ) );
            assertEquals( EXPECTED_CONTENT2,
                          result );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testNewAsset() throws Exception {
        //simulating a full lifecycle of a new asset from webdav
        try {
            webDAV.createFolder(new TransactionMock(),
                    "/packages/testDavNewAsset");
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            assertFalse( webDAV.objectExists( "/packages/testDavNewAsset/Blah.drl" ) );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            webDAV.isFolder( "/packages/testDavNewAsset" );
            webDAV.isFolder( "/packages/testDavNewAsset/Blah.drl" );
            assertFalse( webDAV.objectExists( "/packages/testDavNewAsset/Blah.drl" ) );
            webDAV.createResource( new TransactionMock(),
                                "/packages/testDavNewAsset/Blah.drl" );
            webDAV.setResourceContent( new TransactionMock(),
                                    "/packages/testDavNewAsset/Blah.drl",
                                    IOUtils.toInputStream( "blah blah" ),
                                    null,
                                    null );
            webDAV.getResourceLength( new TransactionMock(),
                                   "/packages/testDavNewAsset/Blah.drl" );
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

        try {
            assertTrue(webDAV.objectExists("/packages/testDavNewAsset/Blah.drl"));
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testSnapshot() throws Exception {

        //WebDAVImpl.setResourceContent adds a trailing \n
        final String CONTENT = "some input";
        final String EXPECTED_CONTENT = CONTENT + "\n";

        try {
            webDAV.createFolder( new TransactionMock(),
                              "/packages/testDavSnapshot" );
            webDAV.createResource( new TransactionMock(),
                                "/packages/testDavSnapshot/Something.drl" );
            webDAV.setResourceContent( new TransactionMock(),
                                    "/packages/testDavSnapshot/Something.drl",
                                    IOUtils.toInputStream( CONTENT ),
                                    null,
                                    null );


            rulesRepository.createPackageSnapshot("testDavSnapshot",
                    "SNAP1");
            rulesRepository.createPackageSnapshot("testDavSnapshot",
                    "SNAP2");

            String[] packages = webDAV.getChildrenNames( new TransactionMock(),
                                                      "/snapshots" );
            assertTrue( packages.length > 0 );
            assertContains( "testDavSnapshot",
                            packages );

            String[] snaps = webDAV.getChildrenNames( new TransactionMock(),
                                                   "/snapshots/testDavSnapshot" );
            assertEquals( 2,
                          snaps.length );

            assertEquals( "SNAP1",
                          snaps[0] );
            assertEquals( "SNAP2",
                          snaps[1] );

            String[] list = webDAV.getChildrenNames( new TransactionMock(),
                                                  "/snapshots/testDavSnapshot/SNAP1" );
            assertEquals( 1,
                          list.length );
            assertEquals( "Something.drl",
                          list[0] );

            list = webDAV.getChildrenNames( new TransactionMock(),
                                         "/snapshots/testDavSnapshot/SNAP2" );
            assertEquals( 1,
                          list.length );
            assertEquals( "Something.drl",
                          list[0] );

            assertNotNull( webDAV.getCreationDate( "/snapshots" ) );
            assertNotNull( webDAV.getCreationDate( "/snapshots/testDavSnapshot" ) );
            assertNotNull( webDAV.getCreationDate( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertNotNull( webDAV.getCreationDate( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertNotNull( webDAV.getLastModified( "/snapshots" ) );
            assertNotNull( webDAV.getLastModified( "/snapshots/testDavSnapshot" ) );
            assertNotNull( webDAV.getLastModified( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertNotNull( webDAV.getLastModified( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            createFolderTry( webDAV,
                             "/snapshots/randomAss" );
            createFolderTry( webDAV,
                             "/snapshots/testDavSnapshot/SNAPX" );
            createFolderTry( webDAV,
                             "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            createFolderTry( webDAV,
                             "/snapshots/testDavSnapshot/SNAP1/Another.drl" );

            createResourceTry( webDAV,
                               "/snapshots/randomAss" );
            createResourceTry( webDAV,
                               "/snapshots/testDavSnapshot/SNAPX" );
            createResourceTry( webDAV,
                               "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            createResourceTry( webDAV,
                               "/snapshots/testDavSnapshot/SNAP1/Another.drl" );

            InputStream in = webDAV.getResourceContent( new TransactionMock(),
                                                     "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            assertEquals( EXPECTED_CONTENT,
                          IOUtils.toString( in ) );

            assertEquals( 0,
                          webDAV.getResourceLength( new TransactionMock(),
                                                 "/snapshots/testDavSnapshot/SNAP1" ) );
            assertEquals( EXPECTED_CONTENT.getBytes().length,
                          webDAV.getResourceLength( new TransactionMock(),
                                                 "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertTrue( webDAV.isFolder( "/snapshots" ) );
            assertTrue( webDAV.isFolder( "/snapshots/testDavSnapshot" ) );
            assertTrue( webDAV.isFolder( "/snapshots/testDavSnapshot/SNAP2" ) );
            assertFalse( webDAV.isFolder( "/snapshots/testDavSnapshot/SNAP2/Something.drl" ) );

            assertFalse( webDAV.isResource( "/snapshots" ) );
            assertFalse( webDAV.isResource( "/snapshots/testDavSnapshot" ) );
            assertFalse( webDAV.isResource( "/snapshots/testDavSnapshot/SNAP2" ) );
            assertTrue( webDAV.isResource( "/snapshots/testDavSnapshot/SNAP2/Something.drl" ) );

            assertFalse( webDAV.isResource( "/snapshots/testDavSnapshot/SNAP2/DoesNotExist.drl" ) );

            assertTrue( webDAV.objectExists( "/snapshots" ) );
            assertFalse( webDAV.objectExists( "/snapshots/testDavSnapshotXX" ) );
            assertTrue( webDAV.objectExists( "/snapshots/testDavSnapshot" ) );
            assertTrue( webDAV.objectExists( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertFalse( webDAV.objectExists( "/snapshots/testDavSnapshot/SNAPX" ) );

            assertFalse( webDAV.objectExists( "/snapshots/testDavSnapshot/SNAP1/Foo.drl" ) );
            assertTrue( webDAV.objectExists( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertNull( webDAV.getChildrenNames( new TransactionMock(),
                                              "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            try {
                webDAV.removeObject( new TransactionMock(),
                                  "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
                fail( "Should not delete files from snapshots" );
            } catch ( Exception e ) {
                assertNotNull( e.getMessage() );
            }

            try {
                webDAV.setResourceContent( new TransactionMock(),
                                        "/snapshots/testDavSnapshot/SNAP1/Something.drl",
                                        null,
                                        null,
                                        null );
                fail( "should not be allowed to update content in snapshots." );
            } catch ( Exception e ) {
                assertNotNull( e.getMessage() );
            }

            assertFalse( webDAV.objectExists( "/snapshots/defaultPackage/new file" ) );
            try {
                webDAV.createResource( new TransactionMock(),
                                    "/snapshots/defaultPackage/new file" );
                fail( "can't touch this" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( webDAV != null ) {
                //This clears the ThreadLocal reference to Repository
                webDAV.commit( new TransactionMock() );
            }
        }
    }

    private void createResourceTry(WebDAVImpl webDAV,
                                   String path) {
        try {
            webDAV.createResource(new TransactionMock(),
                    path);
            fail( "Should not be allowed" );
        } catch ( UnsupportedOperationException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    private void createFolderTry(WebDAVImpl webDAV,
                                 String path) {
        try {
            webDAV.createFolder(new TransactionMock(),
                    path);
            fail( "should not be allowed" );
        } catch ( UnsupportedOperationException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    private void assertContains(String string,
                                String[] children) {
        for ( int i = 0; i < children.length; i++ ) {
            if ( children[i].equals( string ) ) {
                return;
            }
        }
        fail( "Array did not contain " + string );
    }

    static class TransactionMock
        implements
        ITransaction {

        public Principal getPrincipal() {
            return null;
        }

    }

}
