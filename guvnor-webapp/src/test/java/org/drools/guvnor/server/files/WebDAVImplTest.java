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

import net.sf.webdav.ITransaction;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class WebDAVImplTest extends GuvnorTestBase {

    @Test
    public void testPath() {
        WebDAVImpl imp = new WebDAVImpl( new File( "" ) );
        String[] path = imp.getPath( "http://goober/whee/webdav/packages/packagename/resource.drl",
                                     true );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "packagename",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = imp.getPath( "foo/webdav",
                            true );
        assertEquals( 0,
                      path.length );

        path = imp.getPath( "/" );
        assertEquals( 0,
                      path.length );

        path = imp.getPath( "/packages/packagename/resource.drl" );
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
        WebDAVImpl imp = new WebDAVImpl( new File( "" ) );
        String[] path = imp.getPath( "http://goober/whee/webdav/packages/ssswebdavss/resource.drl",
                                     true );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "ssswebdavss",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = imp.getPath( "foo/webdav",
                            true );
        assertEquals( 0,
                      path.length );

        path = imp.getPath( "/" );
        assertEquals( 0,
                      path.length );

        path = imp.getPath( "/packages/ssswebdavss/resource.drl" );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "ssswebdavss",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = imp.getPath( "http://goober/whee/webdav/packages/webdav/resource.drl",
                            true );
        assertEquals( "packages",
                      path[0] );
        assertEquals( "webdav",
                      path[1] );
        assertEquals( "resource.drl",
                      path[2] );

        path = imp.getPath( "/packages/webdav/resource.drl" );
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
        WebDAVImpl imp = new WebDAVImpl( new File( "" ) );
        try {
            imp.objectExists( "/foo/webdav/packages/foobar/Something.drl copy 42" );
            fail( "should not be allowed" );
        } catch ( IllegalArgumentException e ) {
            assertNotNull( e.getMessage() );
        }

    }

    @Test
    public void testChildrenNames() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            RulesRepository repo = imp.getRepo();
            String[] children = imp.getChildrenNames( new TransactionMock(),
                                                      "/packages" );
            assertTrue( children.length > 0 );
            int packageCount = children.length;

            PackageItem pkg = repo.createPackage( "testWebDavChildNames1",
                                                  "" );
            repo.createPackage( "testWebDavChildNames2",
                                "" );
            repo.save();
            children = imp.getChildrenNames( new TransactionMock(),
                                             "/packages" );
            assertEquals( packageCount + 2,
                          children.length );
            assertContains( "testWebDavChildNames1",
                            children );
            assertContains( "testWebDavChildNames2",
                            children );

            AssetItem asset = pkg.addAsset( "asset1",
                                            "something" );
            asset.updateFormat( "drl" );
            asset.checkin( "" );
            asset = pkg.addAsset( "asset2",
                                  "something" );
            asset.updateFormat( "dsl" );
            asset.checkin( "" );

            children = imp.getChildrenNames( new TransactionMock(),
                                             "/packages/testWebDavChildNames1" );
            assertEquals( 2,
                          children.length );
            assertEquals( "asset1.drl",
                          children[0] );
            assertEquals( "asset2.dsl",
                          children[1] );

            children = imp.getChildrenNames( new TransactionMock(),
                                             "/packages/testWebDavChildNames1/asset1.drl" );
            assertNull( children );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testCreateFolder() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            RulesRepository repo = imp.getRepo();
            String[] children = imp.getChildrenNames( new TransactionMock(),
                                                      "/packages" );
            int packageCount = children.length;

            imp.createFolder( new TransactionMock(),
                              "/packages/testCreateWebDavFolder" );
            children = imp.getChildrenNames( new TransactionMock(),
                                             "/packages" );

            assertEquals( packageCount + 1,
                          children.length );
            assertContains( "testCreateWebDavFolder",
                            children );

            PackageItem pkg = repo.loadPackage( "testCreateWebDavFolder" );
            assertNotNull( pkg );

            pkg.addAsset( "someAsset",
                          "" );

            try {
                imp.createFolder( new TransactionMock(),
                                  "/somethingElse" );
                fail( "this should not work !" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
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
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertNotNull( imp.getCreationDate( uri ) );
            assertNotNull( imp.getLastModified( uri ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testCreateResourceAndCreatedDate() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            RulesRepository repo = imp.getRepo();
            imp.createFolder( new TransactionMock(),
                              "/packages/testCreateResourceDAVFolder" );

            Thread.sleep( 100 );

            imp.createResource( new TransactionMock(),
                                "/packages/testCreateResourceDAVFolder/asset.drl" );

            String[] resources = imp.getChildrenNames( new TransactionMock(),
                                                       "/packages/testCreateResourceDAVFolder" );
            assertEquals( 1,
                          resources.length );
            assertEquals( "asset.drl",
                          resources[0] );

            //should be ignored
            imp.createResource( new TransactionMock(),
                                "/packages/testCreateResourceDAVFolder/._asset.drl" );
            imp.createResource( new TransactionMock(),
                                "/packages/.DS_Store" );

            PackageItem pkg = repo.loadPackage( "testCreateResourceDAVFolder" );
            assertFalse( pkg.containsAsset( "._asset" ) );
            assertTrue( pkg.containsAsset( "asset" ) );

            Iterator<AssetItem> it = pkg.getAssets();
            AssetItem ass = it.next();
            assertEquals( "asset",
                          ass.getName() );
            assertEquals( "drl",
                          ass.getFormat() );

            Date create = imp.getCreationDate( "/packages/testCreateResourceDAVFolder" );
            assertNotNull( create );
            assertTrue( create.after( new Date( "10-Jul-1974" ) ) );

            Date assetCreate = imp.getCreationDate( "/packages/testCreateResourceDAVFolder/asset.drl" );
            assertTrue( assetCreate.after( create ) );

            Date lm = imp.getLastModified( "/packages/testCreateResourceDAVFolder" );
            assertNotNull( lm );
            assertTrue( lm.after( new Date( "10-Jul-1974" ) ) );

            Date alm = imp.getLastModified( "/packages/testCreateResourceDAVFolder/asset.drl" );
            assertTrue( alm.after( lm ) );

            try {
                imp.createResource( new TransactionMock(),
                                    "/hummer.drl" );
                fail( "Shouldn't be able to do this" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testResourceContent() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            RulesRepository repo = imp.getRepo();
            PackageItem pkg = repo.createPackage( "testWebDAVContent",
                                                  "" );

            AssetItem asset = pkg.addAsset( "asset",
                                            "something" );
            asset.updateFormat( "drl" );
            asset.updateContent( "Some content" );
            asset.checkin( "" );
            InputStream data = imp.getResourceContent( new TransactionMock(),
                                                       "/packages/testWebDAVContent/asset.drl" );
            assertEquals( "Some content",
                          IOUtils.toString( data ) );

            asset = pkg.addAsset( "asset2",
                                  "something" );
            asset.updateFormat( "xls" );
            asset.updateBinaryContentAttachment( IOUtils.toInputStream( "This is binary" ) );
            asset.checkin( "" );

            data = imp.getResourceContent( new TransactionMock(),
                                           "/packages/testWebDAVContent/asset2.xls" );
            assertEquals( "This is binary",
                          IOUtils.toString( data ) );

            AssetItem asset_ = pkg.addAsset( "somethingelse",
                                             "" );
            asset_.updateFormat( "drl" );
            asset_.checkin( "" );

            data = imp.getResourceContent( new TransactionMock(),
                                           "/packages/testWebDAVContent/somethingelse.drl" );
            assertEquals( "",
                          IOUtils.toString( data ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testIsFolder() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertTrue( imp.isFolder( "/packages" ) );
            assertTrue( imp.isFolder( "/packages/" ) );
            assertFalse( imp.isFolder( "/packages/somePackage" ) );

            imp.createFolder( new TransactionMock(),
                              "/packages/testDAVIsFolder" );
            assertTrue( imp.isFolder( "/packages/testDAVIsFolder" ) );
            assertFalse( imp.isFolder( "/packages/somePackage/SomeFile.drl" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }
    }

    @Test
    public void testIsResource() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertFalse( imp.isResource( "/packages" ) );
            assertFalse( imp.isResource( "/packages/somePackage" ) );
            assertFalse( imp.isResource( "/packages/somePackage/SomeFile.drl" ) );

            imp.createFolder( new TransactionMock(),
                              "/packages/testDAVIsResource" );
            imp.createResource( new TransactionMock(),
                                "/packages/testDAVIsResource/SomeFile.drl" );

            assertTrue( imp.isResource( "/packages/testDAVIsResource/SomeFile.drl" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testResourceLength() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertEquals( 0,
                          imp.getResourceLength( new TransactionMock(),
                                                 "/webdav/packages" ) );
            imp.createFolder( new TransactionMock(),
                              "/packages/testResourceLengthDAV" );
            imp.createResource( new TransactionMock(),
                                "/packages/testResourceLengthDAV/testResourceLength" );
            assertEquals( 0,
                          imp.getResourceLength( new TransactionMock(),
                                                 "/packages/testResourceLengthDAV/testResourceLength" ) );
            imp.setResourceContent( new TransactionMock(),
                                    "/packages/testResourceLengthDAV/testResourceLength",
                                    IOUtils.toInputStream( "some input" ),
                                    null,
                                    null );
            assertEquals( "some input".getBytes().length,
                          imp.getResourceLength( new TransactionMock(),
                                                 "/packages/testResourceLengthDAV/testResourceLength" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testObjectExists() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertTrue( imp.objectExists( "/packages" ) );

            imp.createFolder( new TransactionMock(),
                              "/packages/testDavObjectExists" );
            assertTrue( imp.objectExists( "/packages/testDavObjectExists" ) );
            assertFalse( imp.objectExists( "/packages/testDavObjectExistsXXXX" ) );
            assertFalse( imp.objectExists( "/packages/testDavObjectExists/foobar.drl" ) );
            assertFalse( imp.objectExists( "/packages/testDavObjectExistsXXXX/foobar.drl" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testRemoveObject() throws Exception {
        WebDAVImpl imp = getWebDAVImpl();
        try {
            assertFalse( imp.objectExists( "/packages/testDavRemoveObjectFolder" ) );
            imp.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertTrue( imp.objectExists( "/packages/testDavRemoveObjectFolder" ) );
            imp.removeObject( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertFalse( imp.objectExists( "/packages/testDavRemoveObjectFolder" ) );

            imp.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectAsset" );
            imp.createResource( new TransactionMock(),
                                "/packages/testDavRemoveObjectAsset/asset.drl" );

            AssetItem as = imp.getRepo().loadPackage( "testDavRemoveObjectAsset" ).loadAsset( "asset" );
            long origVer = as.getVersionNumber();

            assertTrue( imp.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );
            imp.removeObject( new TransactionMock(),
                              "/packages/testDavRemoveObjectAsset/asset.drl" );
            assertFalse( imp.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );
            assertTrue( imp.objectExists( "/packages/testDavRemoveObjectAsset" ) );

            imp.createResource( new TransactionMock(),
                                "/packages/testDavRemoveObjectAsset/asset.drl" );
            assertTrue( imp.objectExists( "/packages/testDavRemoveObjectAsset/asset.drl" ) );

            as = imp.getRepo().loadPackage( "testDavRemoveObjectAsset" ).loadAsset( "asset" );
            assertTrue( as.getVersionNumber() > origVer );
            imp.createFolder( new TransactionMock(),
                              "/packages/testDavRemoveObjectFolder" );
            assertTrue( imp.objectExists( "/packages/testDavRemoveObjectFolder" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
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

        WebDAVImpl imp = getWebDAVImpl();
        try {
            imp.createFolder( new TransactionMock(),
                              "/packages/testSetDavContent" );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            imp.createResource( new TransactionMock(),
                                "/packages/testSetDavContent/Something.drl" );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            imp.setResourceContent( new TransactionMock(),
                                    "/packages/testSetDavContent/Something.drl",
                                    IOUtils.toInputStream( CONTENT1 ),
                                    null,
                                    null );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            imp.getResourceContent( new TransactionMock(),
                                    "/packages/testSetDavContent/Something.drl" );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            AssetItem as = imp.getRepo().loadPackage( "testSetDavContent" ).loadAsset( "Something" );
            assertTrue( as.isBinary() );

            String result = IOUtils.toString( imp.getResourceContent( new TransactionMock(),
                                                                      "/packages/testSetDavContent/Something.drl" ) );
            assertEquals( EXPECTED_CONTENT1,
                          result );

            PackageItem pkg = imp.getRepo().loadPackage( "testSetDavContent" );
            AssetItem asset = pkg.loadAsset( "Something" );
            assertEquals( "drl",
                          asset.getFormat() );
            assertEquals( EXPECTED_CONTENT1,
                          asset.getContent() );
            assertEquals( EXPECTED_CONTENT1,
                          IOUtils.toString( asset.getBinaryContentAttachment() ) );

            imp.setResourceContent( new TransactionMock(),
                                    "/packages/testSetDavContent/Something.drl",
                                    IOUtils.toInputStream( CONTENT2 ),
                                    null,
                                    null );
            result = IOUtils.toString( imp.getResourceContent( new TransactionMock(),
                                                               "/packages/testSetDavContent/Something.drl" ) );
            assertEquals( EXPECTED_CONTENT2,
                          result );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testNewAsset() throws Exception {
        //simulating a full lifecycle of a new asset from webdav
        WebDAVImpl imp = getWebDAVImpl();
        try {
            imp.createFolder( new TransactionMock(),
                              "/packages/testDavNewAsset" );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            assertFalse( imp.objectExists( "/packages/testDavNewAsset/Blah.drl" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            imp.isFolder( "/packages/testDavNewAsset" );
            imp.isFolder( "/packages/testDavNewAsset/Blah.drl" );
            assertFalse( imp.objectExists( "/packages/testDavNewAsset/Blah.drl" ) );
            imp.createResource( new TransactionMock(),
                                "/packages/testDavNewAsset/Blah.drl" );
            imp.setResourceContent( new TransactionMock(),
                                    "/packages/testDavNewAsset/Blah.drl",
                                    IOUtils.toInputStream( "blah blah" ),
                                    null,
                                    null );
            imp.getResourceLength( new TransactionMock(),
                                   "/packages/testDavNewAsset/Blah.drl" );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

        imp = getWebDAVImpl();
        try {
            assertTrue( imp.objectExists( "/packages/testDavNewAsset/Blah.drl" ) );
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }

    }

    @Test
    public void testSnapshot() throws Exception {

        //WebDAVImpl.setResourceContent adds a trailing \n
        final String CONTENT = "some input";
        final String EXPECTED_CONTENT = CONTENT + "\n";

        WebDAVImpl imp = getWebDAVImpl();
        try {
            imp.createFolder( new TransactionMock(),
                              "/packages/testDavSnapshot" );
            imp.createResource( new TransactionMock(),
                                "/packages/testDavSnapshot/Something.drl" );
            imp.setResourceContent( new TransactionMock(),
                                    "/packages/testDavSnapshot/Something.drl",
                                    IOUtils.toInputStream( CONTENT ),
                                    null,
                                    null );

            RulesRepository repo = imp.getRepo();

            repo.createPackageSnapshot( "testDavSnapshot",
                                        "SNAP1" );
            repo.createPackageSnapshot( "testDavSnapshot",
                                        "SNAP2" );

            String[] packages = imp.getChildrenNames( new TransactionMock(),
                                                      "/snapshots" );
            assertTrue( packages.length > 0 );
            assertContains( "testDavSnapshot",
                            packages );

            String[] snaps = imp.getChildrenNames( new TransactionMock(),
                                                   "/snapshots/testDavSnapshot" );
            assertEquals( 2,
                          snaps.length );

            assertEquals( "SNAP1",
                          snaps[0] );
            assertEquals( "SNAP2",
                          snaps[1] );

            String[] list = imp.getChildrenNames( new TransactionMock(),
                                                  "/snapshots/testDavSnapshot/SNAP1" );
            assertEquals( 1,
                          list.length );
            assertEquals( "Something.drl",
                          list[0] );

            list = imp.getChildrenNames( new TransactionMock(),
                                         "/snapshots/testDavSnapshot/SNAP2" );
            assertEquals( 1,
                          list.length );
            assertEquals( "Something.drl",
                          list[0] );

            assertNotNull( imp.getCreationDate( "/snapshots" ) );
            assertNotNull( imp.getCreationDate( "/snapshots/testDavSnapshot" ) );
            assertNotNull( imp.getCreationDate( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertNotNull( imp.getCreationDate( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertNotNull( imp.getLastModified( "/snapshots" ) );
            assertNotNull( imp.getLastModified( "/snapshots/testDavSnapshot" ) );
            assertNotNull( imp.getLastModified( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertNotNull( imp.getLastModified( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            createFolderTry( imp,
                             "/snapshots/randomAss" );
            createFolderTry( imp,
                             "/snapshots/testDavSnapshot/SNAPX" );
            createFolderTry( imp,
                             "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            createFolderTry( imp,
                             "/snapshots/testDavSnapshot/SNAP1/Another.drl" );

            createResourceTry( imp,
                               "/snapshots/randomAss" );
            createResourceTry( imp,
                               "/snapshots/testDavSnapshot/SNAPX" );
            createResourceTry( imp,
                               "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            createResourceTry( imp,
                               "/snapshots/testDavSnapshot/SNAP1/Another.drl" );

            InputStream in = imp.getResourceContent( new TransactionMock(),
                                                     "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
            assertEquals( EXPECTED_CONTENT,
                          IOUtils.toString( in ) );

            assertEquals( 0,
                          imp.getResourceLength( new TransactionMock(),
                                                 "/snapshots/testDavSnapshot/SNAP1" ) );
            assertEquals( EXPECTED_CONTENT.getBytes().length,
                          imp.getResourceLength( new TransactionMock(),
                                                 "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertTrue( imp.isFolder( "/snapshots" ) );
            assertTrue( imp.isFolder( "/snapshots/testDavSnapshot" ) );
            assertTrue( imp.isFolder( "/snapshots/testDavSnapshot/SNAP2" ) );
            assertFalse( imp.isFolder( "/snapshots/testDavSnapshot/SNAP2/Something.drl" ) );

            assertFalse( imp.isResource( "/snapshots" ) );
            assertFalse( imp.isResource( "/snapshots/testDavSnapshot" ) );
            assertFalse( imp.isResource( "/snapshots/testDavSnapshot/SNAP2" ) );
            assertTrue( imp.isResource( "/snapshots/testDavSnapshot/SNAP2/Something.drl" ) );

            assertFalse( imp.isResource( "/snapshots/testDavSnapshot/SNAP2/DoesNotExist.drl" ) );

            assertTrue( imp.objectExists( "/snapshots" ) );
            assertFalse( imp.objectExists( "/snapshots/testDavSnapshotXX" ) );
            assertTrue( imp.objectExists( "/snapshots/testDavSnapshot" ) );
            assertTrue( imp.objectExists( "/snapshots/testDavSnapshot/SNAP1" ) );
            assertFalse( imp.objectExists( "/snapshots/testDavSnapshot/SNAPX" ) );

            assertFalse( imp.objectExists( "/snapshots/testDavSnapshot/SNAP1/Foo.drl" ) );
            assertTrue( imp.objectExists( "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            assertNull( imp.getChildrenNames( new TransactionMock(),
                                              "/snapshots/testDavSnapshot/SNAP1/Something.drl" ) );

            try {
                imp.removeObject( new TransactionMock(),
                                  "/snapshots/testDavSnapshot/SNAP1/Something.drl" );
                fail( "Should not delete files from snapshots" );
            } catch ( Exception e ) {
                assertNotNull( e.getMessage() );
            }

            try {
                imp.setResourceContent( new TransactionMock(),
                                        "/snapshots/testDavSnapshot/SNAP1/Something.drl",
                                        null,
                                        null,
                                        null );
                fail( "should not be allowed to update content in snapshots." );
            } catch ( Exception e ) {
                assertNotNull( e.getMessage() );
            }

            assertFalse( imp.objectExists( "/snapshots/defaultPackage/new file" ) );
            try {
                imp.createResource( new TransactionMock(),
                                    "/snapshots/defaultPackage/new file" );
                fail( "can't touch this" );
            } catch ( UnsupportedOperationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            if ( imp != null ) {
                //This clears the ThreadLocal reference to Repository
                imp.commit( new TransactionMock() );
            }
        }
    }

    private void createResourceTry(WebDAVImpl imp,
                                   String path) {
        try {
            imp.createResource( new TransactionMock(),
                                path );
            fail( "Should not be allowed" );
        } catch ( UnsupportedOperationException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    private void createFolderTry(WebDAVImpl imp,
                                 String path) {
        try {
            imp.createFolder( new TransactionMock(),
                              path );
            fail( "should not be allowed" );
        } catch ( UnsupportedOperationException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    @Test
    public void testThreadLocal() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        final RulesRepository repo = impl.getRulesRepository();

        Thread t = new Thread( new Runnable() {
            public void run() {
                WebDAVImpl i = new WebDAVImpl( repo );
                ITransaction txn = null;
                assertNotNull( i.getRepo() );
                try {
                    txn = i.begin( null );
                } catch ( Exception e ) {
                    e.fillInStackTrace();
                    e.printStackTrace();
                    fail( "should not happen" );
                } finally {
                    i.commit( txn );
                }
                assertNull( i.getRepo() );
            }
        } );
        t.start();
        t.join();
        System.out.println( "h" );
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
