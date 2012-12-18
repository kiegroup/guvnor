/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.fileupload.FileItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.Snapshot;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.guvnor.server.files.FileManagerService;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.junit.Test;

public class FileManagerServiceIntegrationTest extends GuvnorIntegrationTest {

    @Inject
    private FileManagerService fileManagerService;

    @Test
    public void testAttachFile() throws Exception {

        AssetItem item = rulesRepository.loadDefaultModule().addAsset( "testUploadFile",
                                                             "description" );
        item.updateFormat("drl");
        FormData upload = new FormData();

        upload.setFile( new MockFile() );
        upload.setUuid(item.getUUID());

        fileManagerService.attachFile(upload);

        AssetItem item2 = rulesRepository.loadDefaultModule().loadAsset( "testUploadFile" );
        byte[] data = item2.getBinaryContentAsBytes();

        assertNotNull( data );
        assertEquals( "foo bar",
                      new String( data ) );
        assertEquals( "foo.bar",
                      item2.getBinaryContentAttachmentFileName() );
    }

    @Test
    public void testAttachModel() throws Exception {

        ModuleItem pkg = rulesRepository.createModule( "testAttachModelImports",
                                              "heh" );
        AssetItem asset = pkg.addAsset( "MyModel",
                                        "" );
        asset.updateFormat(AssetFormats.MODEL);
        asset.checkin( "" );

        pkg.updateBinaryUpToDate(true);
        rulesRepository.save();

        assertTrue(pkg.isBinaryUpToDate());
        assertEquals("",
                DroolsHeader.getDroolsHeader(pkg));

        fileManagerService.attachFileToAsset(asset.getUUID(),
                this.getClass().getResourceAsStream("/billasurf.jar"),
                "billasurf.jar");

        pkg = rulesRepository.loadModule( "testAttachModelImports" );

        assertFalse(pkg.isBinaryUpToDate());
        assertNotNull( DroolsHeader.getDroolsHeader( pkg ) );
        assertTrue(DroolsHeader.getDroolsHeader(pkg).indexOf("import com.billasurf.Board") > -1);
        assertTrue(DroolsHeader.getDroolsHeader(pkg).indexOf("import com.billasurf.Person") > -1);

        DroolsHeader.updateDroolsHeader("goo wee",
                pkg);
        pkg.checkin("");

        fileManagerService.attachFileToAsset(asset.getUUID(),
                this.getClass().getResourceAsStream("/billasurf.jar"),
                "billasurf.jar");
        pkg = rulesRepository.loadModule( "testAttachModelImports" );
        assertEquals( "goo wee\nimport com.billasurf.Board\nimport com.billasurf.Person\n",
                      DroolsHeader.getDroolsHeader( pkg ) );

    }

    @Test
    public void testGetFilebyUUID() throws Exception {

        AssetItem item = rulesRepository.loadDefaultModule().addAsset( "testGetFilebyUUID",
                                                                       "description" );
        item.updateFormat("drl");
        rulesRepository.save();

        FormData upload = new FormData();
        MockFile file = new MockFile("testGetFilebyUUID.drl");
        upload.setFile( file );
        upload.setUuid(item.getUUID());
        fileManagerService.attachFile( upload );

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String filename = fileManagerService.loadFileAttachmentByUUID( item.getUUID(),
                                                                 out );

        assertNotNull( out.toByteArray() );
        assertEquals( "foo bar",
                      new String( out.toByteArray() ) );
        assertEquals( "testGetFilebyUUID.drl",
                      filename );
    }

    @Test
    public void testGetPackageBinaryAndSource() throws Exception {

        long before = System.currentTimeMillis();
        Thread.sleep( 20 );
        ModuleItem pkg = rulesRepository.createModule( "testGetBinaryPackageServlet",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                                  pkg );
        pkg.updateCompiledBinary( new ByteArrayInputStream( "foo".getBytes() ) );
        pkg.checkin( "" );

        assertTrue( before < fileManagerService.getLastModified( pkg.getName(),
                                                           "LATEST" ) );

        repositoryPackageService.createModuleSnapshot(pkg.getName(),
                "SNAPPY 1",
                false,
                "",false,"","","",false,"","",false,"");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fileName = fileManagerService.loadBinaryPackage( pkg.getName(),
                                                          Snapshot.LATEST_SNAPSHOT,
                                                          true,
                                                          out );
        assertEquals( "testGetBinaryPackageServlet.pkg",
                      fileName );
        byte[] file = out.toByteArray();
        assertNotNull( file );
        assertEquals( "foo",
                      new String( file ) );

        out = new ByteArrayOutputStream();
        String drlName = fileManagerService.loadSourcePackage( pkg.getName(),
                                                         Snapshot.LATEST_SNAPSHOT,
                                                         true,
                                                         out );
        String drl = new String( out.toByteArray() );
        assertEquals( pkg.getName() + ".drl",
                      drlName );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "import java.util.List" ) > -1 );

        out = new ByteArrayOutputStream();
        fileName = fileManagerService.loadBinaryPackage( pkg.getName(),
                                                   "SNAPPY 1",
                                                   false,
                                                   out );
        assertEquals( "testGetBinaryPackageServlet_SNAPPY+1.pkg",
                      fileName );
        file = out.toByteArray();
        assertNotNull( file );
        assertEquals( "foo",
                      new String( file ) );

        out = new ByteArrayOutputStream();
        fileName = fileManagerService.loadSourcePackage( pkg.getName(),
                                                   "SNAPPY 1",
                                                   false,
                                                   out );
        assertEquals( "testGetBinaryPackageServlet_SNAPPY+1.drl",
                      fileName );
        drl = new String( out.toByteArray() );
        assertTrue( drl.indexOf( "import java.util.List" ) > -1 );

        Thread.sleep( 100 );
        repositoryPackageService.createModuleSnapshot(pkg.getName(),
                "SNAPX",
                false,
                "",false,"","","",false,"","",false,"");

        long lastMod = fileManagerService.getLastModified( pkg.getName(),
                                                     "SNAPPY 1" );
        assertTrue( pkg.getLastModified().getTimeInMillis() < lastMod );

        Thread.sleep( 100 );

        repositoryPackageService.createModuleSnapshot(pkg.getName(),
                "SNAPX",
                true,
                "yeah",false,"","","",false,"","",false,"");

        long lastMod2 = fileManagerService.getLastModified( pkg.getName(),
                                                      "SNAPX" );
        assertTrue( lastMod < lastMod2 );

    }

    /**
     * 
     * Tests importing when an archived package with the same name exists.
     */

    @Test
    public void testImportArchivedPackage() throws Exception {
        // Import package
        String drl = "package testImportArchivedPackage\n import blah \n rule 'ola' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        InputStream in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                null);

        ModuleItem pkg = rulesRepository.loadModule("testImportArchivedPackage");
        assertNotNull( pkg );
        assertFalse( pkg.isArchived() );

        // Archive it
        pkg.archiveItem( true );
        rulesRepository.save();

        pkg = rulesRepository.loadModule("testImportArchivedPackage");
        assertNotNull( pkg );
        assertTrue( pkg.isArchived() );

        // Import it again
        InputStream in2 = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in2,
                null);

        pkg = rulesRepository.loadModule("testImportArchivedPackage");
        assertNotNull( pkg );
        assertFalse( pkg.isArchived() );

    }

    @Test
    public void testClassicDRLImport() throws Exception {
        String drl = "package testClassicDRLImport\n import blah \n rule 'ola' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        InputStream in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                null);

        ModuleItem pkg = rulesRepository.loadModule("testClassicDRLImport");
        assertNotNull( pkg );

        List<AssetItem> rules = iteratorToList( pkg.getAssets() );
        assertEquals( 3,
                      rules.size() );

        AssetItem pkgConf = rules.get( 0 );
        assertEquals( "drools",
                      pkgConf.getName() );
        rules.remove( 0 );

        final AssetItem rule1 = rules.get( 0 );
        assertEquals( "ola",
                      rule1.getName() );
        assertNotNull( rule1.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule1.getFormat() );
        assertTrue( rule1.getContent().indexOf( "when" ) > -1 );

        final AssetItem rule2 = rules.get( 1 );
        assertEquals( "hola",
                      rule2.getName() );
        assertNotNull( rule2.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule2.getFormat() );
        assertTrue( rule2.getContent().indexOf( "when" ) > -1 );

        assertNotNull( DroolsHeader.getDroolsHeader( pkg ) );
        assertTrue( DroolsHeader.getDroolsHeader( pkg ).indexOf( "import" ) > -1 );

        // now lets import an existing thing
        drl = "package testClassicDRLImport\n import should not see \n rule 'ola2' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                null);

        pkg = rulesRepository.loadModule("testClassicDRLImport");
        assertNotNull( pkg );

        // it should not overwrite this.
        String hdr = DroolsHeader.getDroolsHeader( pkg );
        assertTrue( hdr.indexOf( "import should not see" ) > -1 );
        assertTrue( hdr.indexOf( "import blah" ) > -1 );
        assertTrue( hdr.indexOf( "import should not see" ) > hdr.indexOf( "import blah" ) );

        rules = iteratorToList( pkg.getAssets() );
        assertEquals( 4,
                      rules.size() );

        // now we will import a change, check that it appears. a change to the
        // "ola" rule
        AssetItem assetOriginal = rulesRepository.loadModule("testClassicDRLImport").loadAsset( "ola" );
        long ver = assetOriginal.getVersionNumber();

        drl = "package testClassicDRLImport\n import blah \n rule 'ola' \n when CHANGED\n then \n end \n rule 'hola' \n when \n then \n end";
        in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                null);
        pkg = rulesRepository.loadModule("testClassicDRLImport");
        AssetItem asset = pkg.loadAsset( "ola" );

        assertTrue( asset.getContent().indexOf( "CHANGED" ) > 0 );
        assertEquals( ver + 1,
                      asset.getVersionNumber() );

    }

    @Test
    public void testDRLImportWithoutPackageName() throws Exception {
        String drl = "import blah \n rule 'ola' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        InputStream in = new ByteArrayInputStream( drl.getBytes() );

        try {
            fileManagerService.importClassicDRL(in,
                    null);
        } catch ( IllegalArgumentException e ) {
            assertEquals( "Missing package name.",
                          e.getMessage() );
        }

        in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                "testDRLImportWithoutPackageName");

        ModuleItem pkg = rulesRepository.loadModule("testDRLImportWithoutPackageName");
        assertNotNull( pkg );

        List<AssetItem> rules = iteratorToList( pkg.getAssets() );
        assertEquals( 3,
                      rules.size() );

        AssetItem pkgConf = rules.get( 0 );
        assertEquals( "drools",
                      pkgConf.getName() );
        rules.remove( 0 );

        final AssetItem rule1 = rules.get( 0 );
        assertEquals( "ola",
                      rule1.getName() );
        assertNotNull( rule1.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule1.getFormat() );
        assertTrue( rule1.getContent().indexOf( "when" ) > -1 );

        final AssetItem rule2 = rules.get( 1 );
        assertEquals( "hola",
                      rule2.getName() );
        assertNotNull( rule2.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule2.getFormat() );
        assertTrue( rule2.getContent().indexOf( "when" ) > -1 );

        assertNotNull( DroolsHeader.getDroolsHeader( pkg ) );
        assertTrue( DroolsHeader.getDroolsHeader( pkg ).indexOf( "import" ) > -1 );

    }

    @Test
    public void testDRLImportOverrideExistingPackageName() throws Exception {
        String drl = "package thisIsNeverUsed \n import blah \n rule 'ola' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        InputStream in = new ByteArrayInputStream( drl.getBytes() );

        in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                "testDRLImportOverrideExistingPackageName");

        ModuleItem pkg = rulesRepository.loadModule("testDRLImportOverrideExistingPackageName");
        assertNotNull( pkg );

        List<AssetItem> rules = iteratorToList( pkg.getAssets() );
        assertEquals( 3,
                      rules.size() );

        AssetItem pkgConf = rules.get( 0 );
        assertEquals( "drools",
                      pkgConf.getName() );
        rules.remove( 0 );

        final AssetItem rule1 = rules.get( 0 );
        assertEquals( "ola",
                      rule1.getName() );
        assertNotNull( rule1.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule1.getFormat() );
        assertTrue( rule1.getContent().indexOf( "when" ) > -1 );

        final AssetItem rule2 = rules.get( 1 );
        assertEquals( "hola",
                      rule2.getName() );
        assertNotNull( rule2.getContent() );
        assertEquals( AssetFormats.DRL,
                      rule2.getFormat() );
        assertTrue( rule2.getContent().indexOf( "when" ) > -1 );

        assertNotNull( DroolsHeader.getDroolsHeader( pkg ) );
        assertTrue( DroolsHeader.getDroolsHeader( pkg ).indexOf( "import" ) > -1 );

    }

    @Test
    public void testClassicDRLImportWithDSL() throws Exception {
        String drl = "package testClassicDRLImportWithDSL\n import blah \n expander goo \n rule 'ola' \n when \n then \n end \n rule 'hola' \n when \n then \n end";
        InputStream in = new ByteArrayInputStream( drl.getBytes() );
        fileManagerService.importClassicDRL(in,
                null);

        ModuleItem pkg = rulesRepository.loadModule("testClassicDRLImportWithDSL");
        assertNotNull( pkg );

        List<AssetItem> rules = iteratorToList( pkg.getAssets() );
        assertEquals( 3,
                      rules.size() ); //its 3 cause there is the drools.package file
        AssetItem pkgConf = rules.get( 0 );
        assertEquals( "drools",
                      pkgConf.getName() );
        assertEquals( "package",
                      pkgConf.getFormat() );
        rules.remove( 0 );//now lets get rid of it

        final AssetItem rule1 = rules.get( 0 );
        assertEquals( "ola",
                      rule1.getName() );
        assertNotNull( rule1.getContent() );
        assertEquals( AssetFormats.DSL_TEMPLATE_RULE,
                      rule1.getFormat() );
        assertTrue( rule1.getContent().indexOf( "when" ) > -1 );

        final AssetItem rule2 = rules.get( 1 );
        assertEquals( "hola",
                      rule2.getName() );
        assertNotNull( rule2.getContent() );
        assertEquals( AssetFormats.DSL_TEMPLATE_RULE,
                      rule2.getFormat() );
        assertTrue( rule2.getContent().indexOf( "when" ) > -1 );

        assertTrue( DroolsHeader.getDroolsHeader( pkg ).indexOf( "import" ) > -1 );

    }

    @Test
    //Calling repository.logout() will close the current JCR session. Commenting out this line makes this 
    //test run. But we probably want to remove this test later as this is not a good designed test anyway (the
    //purpose of this test is to detect memory leak?)
    public void testHeadOOME() throws Exception {

        ModuleItem pkg = rulesRepository.createModule( "testHeadOOME",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                                  pkg );
        pkg.updateCompiledBinary( new ByteArrayInputStream( "foo".getBytes() ) );
        pkg.checkin( "" );
        //rulesRepository.logout(); // Breaks lifecycle of test

        int iterations = 0;

        int maxIteration = 1; //pick a large number to do a stress test
        while ( iterations < maxIteration ) {
            iterations++;

            if ( iterations % 50 == 0 ) {
                ModuleItem pkg1 = rulesRepository.loadModule("testHeadOOME");
                pkg1.updateDescription(System.currentTimeMillis() + "");
                pkg1.checkin("a change");
                //rulesRepository.logout(); // Breaks lifecycle of test
            }

            //fm.setRepository( new RulesRepository(TestEnvironmentSessionHelper.getSession()));
            fileManagerService.getLastModified("testHeadOOME",
                    "LATEST");
            //rulesRepository.logout(); // Breaks lifecycle of test
            System.err.println( "Number " + iterations + " free mem : " + Runtime.getRuntime().freeMemory() );
        }
    }

    private List<AssetItem> iteratorToList(Iterator<AssetItem> assets) {
        List<AssetItem> list = new ArrayList<AssetItem>();
        for ( Iterator<AssetItem> iter = assets; iter.hasNext(); ) {
            AssetItem rule = (AssetItem) iter.next();
            list.add( rule );
        }
        return list;

    }

    private static class MockFile
        implements FileItem {

        private static final long serialVersionUID = 510l;

        private final String      fileName;

        InputStream               stream           = new ByteArrayInputStream( "foo bar".getBytes() );

        public MockFile() {
            this.fileName = "foo.bar";
        }
        
        public MockFile(final String fileName) {
            this.fileName = fileName;
        }
        
        public void setInputStream(InputStream is) throws IOException {
            stream.close();
            stream = is;
        }

        public void delete() {
        }

        public byte[] get() {

            return null;
        }

        public String getContentType() {

            return null;
        }

        public String getFieldName() {

            return null;
        }

        public InputStream getInputStream() throws IOException {
            return stream;
        }

        public String getName() {
            return this.fileName;
        }

        public OutputStream getOutputStream() throws IOException {

            return null;
        }

        public long getSize() {
            return 0;
        }

        public String getString() {
            return null;
        }

        public String getString(String arg0) throws UnsupportedEncodingException {
            return null;
        }

        public boolean isFormField() {
            return false;
        }

        public boolean isInMemory() {
            return false;
        }

        public void setFieldName(String arg0) {

        }

        public void setFormField(boolean arg0) {

        }

        public void write(File arg0) throws Exception {

        }

    }

}
