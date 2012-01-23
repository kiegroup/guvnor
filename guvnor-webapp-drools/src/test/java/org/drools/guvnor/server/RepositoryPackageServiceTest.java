/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRow;
import org.drools.guvnor.client.rpc.SnapshotDiff;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.Package;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryPackageServiceTest extends GuvnorTestBase {

    @Test
    public void testSnapshotDiff() throws Exception {
        // Lets make a package and a rule into tit.
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotDiffTesting",
                                                  "y" );
        String packageUuid = repositoryPackageService.createModule( "testSnapshotDiff",
                                                                    "d",
                                                                    "package" );

        assertNotNull( packageUuid );

        // Create two rules
        String archiveRuleUuid = serviceImplementation.createNewRule( "testRuleArchived",
                                                                      "",
                                                                      "snapshotDiffTesting",
                                                                      "testSnapshotDiff",
                                                                      AssetFormats.DRL );
        String modifiedRuleUuid = serviceImplementation.createNewRule( "testRuleModified",
                                                                       "",
                                                                       "snapshotDiffTesting",
                                                                       "testSnapshotDiff",
                                                                       AssetFormats.DRL );
        String deletedRuleUuid = serviceImplementation.createNewRule( "testRuleDeleted",
                                                                      "",
                                                                      "snapshotDiffTesting",
                                                                      "testSnapshotDiff",
                                                                      AssetFormats.DRL );
        String restoredRuleUuid = serviceImplementation.createNewRule( "testRuleRestored",
                                                                       "",
                                                                       "snapshotDiffTesting",
                                                                       "testSnapshotDiff",
                                                                       AssetFormats.DRL );
        @SuppressWarnings("unused")
        String noChangesRuleUuid = serviceImplementation.createNewRule( "testRuleNoChanges",
                                                                        "",
                                                                        "snapshotDiffTesting",
                                                                        "testSnapshotDiff",
                                                                        AssetFormats.DRL );
        repositoryAssetService.archiveAsset( restoredRuleUuid );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiff",
                                                       "FIRST",
                                                       false,
                                                       "ya" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "testSnapshotDiff" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiff" ).length );

        // Change the rule, archive one, delete one and create a new one
        Asset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = repositoryAssetService.checkinVersion( asset );
        assertNotNull( uuid );

        repositoryAssetService.removeAsset( deletedRuleUuid );

        repositoryAssetService.archiveAsset( archiveRuleUuid );

        @SuppressWarnings("unused")
        String addedRuleUuid = serviceImplementation.createNewRule( "testRuleAdded",
                                                                    "",
                                                                    "snapshotDiffTesting",
                                                                    "testSnapshotDiff",
                                                                    AssetFormats.DRL );

        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiff",
                                                       "SECOND",
                                                       false,
                                                       "we" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshotDiff" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiff" ).length );

        // Compare the snapshots
        SnapshotDiffs diffs = repositoryPackageService.compareSnapshots( "testSnapshotDiff",
                                                                         "FIRST",
                                                                         "SECOND" );
        assertEquals( "FIRST",
                      diffs.leftName );
        assertEquals( "SECOND",
                      diffs.rightName );

        SnapshotDiff[] list = diffs.diffs;
        assertNotNull( list );
        assertEquals( 5,
                      list.length );

        for ( int i = 0; i < list.length; i++ ) {
            SnapshotDiff diff = list[i];
            if ( diff.name.equals( "testRuleArchived" ) ) {
                assertEquals( SnapshotDiff.TYPE_ARCHIVED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleModified" ) ) {
                assertEquals( SnapshotDiff.TYPE_UPDATED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleAdded" ) ) {
                assertEquals( SnapshotDiff.TYPE_ADDED,
                              diff.diffType );
                assertNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleDeleted" ) ) {
                assertEquals( SnapshotDiff.TYPE_DELETED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleRestored" ) ) {
                assertEquals( SnapshotDiff.TYPE_RESTORED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else {
                fail( "Diff not expected." );
            }
        }
    }

    @Test
    public void testPackageBinaryUpdatedResetWhenDeletingAnAsset() throws Exception {

        ModuleItem packageItem = rulesRepository.createModule( "testPackageBinaryUpdatedResetWhenDeletingAnAsset",
                                                               "" );

        AssetItem assetItem = packageItem.addAsset( "temp",
                                                    "" );

        assertNotNull( packageItem.getName() );
        packageItem.updateBinaryUpToDate( true );
        assertTrue( packageItem.isBinaryUpToDate() );

        //Need to commit change to Module for it to be visible to subsequent retrieval
        packageItem.checkin( "" );

        serviceImplementation.deleteUncheckedRule( assetItem.getUUID() );

        //Subsequent retrieval
        ModuleItem reloadedPackage = rulesRepository.loadModule( packageItem.getName() );

        assertEquals( packageItem.getName(),
                      reloadedPackage.getName() );
        assertFalse( reloadedPackage.isBinaryUpToDate() );
    }

    @Test
    public void testGetHistoryPackageBinary() throws Exception {
        RulesRepository repo = rulesRepository;

        // create our package
        ModuleItem pkg = repo.createModule( "testGetHistoryPackageBinary",
                                            "" );
        assertFalse( pkg.isBinaryUpToDate() );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                          pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();
        assertFalse( pkg.isBinaryUpToDate() );

        //Create a history package with no compiled binary stored. 
        pkg.checkout();
        pkg.checkin( "version1" );
        //Verify history package binary.
        ModuleItem p = repo.loadModule( "testGetHistoryPackageBinary",
                                        2 );
        assertEquals( "version1",
                      p.getCheckinComment() );
        assertFalse( p.isBinaryUpToDate() );
        byte[] result = p.getCompiledBinaryBytes();
        assertNull( result );

        //Build package update package node to store compiled binary. This is wont work for a history version
        //The best strategy we can do is to force a package build before checkin. TODO.
        //BuilderResult results = repositoryPackageService.buildPackage(pkg.getUUID(), true);
    }

    @Test
    public void testDependencyHistoryPackage() throws Exception {
        //Package version 1
        ModuleItem pkg = rulesRepository.createModule( "testDependencyHistoryPackage",
                                                       "" );

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(version 1); }" );
        func.checkin( "func version 1" );
        func.updateContent( "function void foo() { System.out.println(version 2); }" );
        func.checkin( "func version 2" );

        //Package version 2     
        pkg.checkout();
        pkg.checkin( "package version 2" );

        //calling updateDependency creates package version 3
        pkg.updateDependency( "func?version=1" );
        pkg.checkin( "package version 3" );

        func.updateContent( "function void foo() { System.out.println(version 2); }" );
        func.checkin( "func version 3" );

        //Package version 4     
        pkg.checkout();
        pkg.checkin( "package version 4" );

        //Verify the latest version
        ModuleItem item = rulesRepository.loadModule( "testDependencyHistoryPackage" );
        assertEquals( "package version 4",
                      item.getCheckinComment() );
        assertEquals( "func?version=1",
                      item.getDependencies()[0] );

        //Verify version 2
        item = rulesRepository.loadModule( "testDependencyHistoryPackage",
                                              2 );
        assertEquals( "package version 2",
                      item.getCheckinComment() );
        assertEquals( "func?version=2",
                      item.getDependencies()[0] );

        //Verify version 3
        item = rulesRepository.loadModule( "testDependencyHistoryPackage",
                                              3 );
        assertEquals( "package version 3",
                      item.getCheckinComment() );
        assertEquals( "func?version=1",
                      item.getDependencies()[0] );
    }

    @Test
    public void testMovePackage() throws Exception {
        String[] cats = repositoryCategoryService.loadChildCategories( "/" );
        if ( cats.length == 0 ) {
            repositoryCategoryService.createCategory( "/",
                                                      "la",
                                                      "d" );
        }
        String sourcePkgId = repositoryPackageService.createModule( "sourcePackage",
                                                                    "description",
                                                                    "package" );
        String destPkgId = repositoryPackageService.createModule( "targetPackage",
                                                                  "description",
                                                                  "package" );

        String cat = repositoryCategoryService.loadChildCategories( "/" )[0];

        String uuid = serviceImplementation.createNewRule( "testMovePackage",
                                                           "desc",
                                                           cat,
                                                           "sourcePackage",
                                                           AssetFormats.DRL );

        TableDataResult res = repositoryAssetService.listAssets( destPkgId,
                                                                 new String[]{"drl"},
                                                                 0,
                                                                 2,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 0,
                      res.data.length );

        repositoryAssetService.changeAssetPackage( uuid,
                                                   "targetPackage",
                                                   "yeah" );
        res = repositoryAssetService.listAssets( destPkgId,
                                                 new String[]{"drl"},
                                                 0,
                                                 2,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );

        assertEquals( 1,
                      res.data.length );

        res = repositoryAssetService.listAssets( sourcePkgId,
                                                 new String[]{"drl"},
                                                 0,
                                                 2,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );

        assertEquals( 0,
                      res.data.length );

    }

    @Test
    public void testSnapshot() throws Exception {
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotTesting",
                                                  "y" );
        repositoryPackageService.createModule( "testSnapshot",
                                               "d",
                                               "package" );
        @SuppressWarnings("unused")
        String uuid = serviceImplementation.createNewRule( "testSnapshotRule",
                                                           "",
                                                           "snapshotTesting",
                                                           "testSnapshot",
                                                           AssetFormats.DRL );

        repositoryPackageService.createModuleSnapshot( "testSnapshot",
                                                       "X",
                                                       false,
                                                       "ya" );
        SnapshotInfo[] snaps = repositoryPackageService.listSnapshots( "testSnapshot" );
        assertEquals( 1,
                      snaps.length );
        assertEquals( "X",
                      snaps[0].getName() );
        assertEquals( "ya",
                      snaps[0].getComment() );
        assertNotNull( snaps[0].getUuid() );
        Module confSnap = repositoryPackageService.loadModule( snaps[0].getUuid() );
        assertEquals( "testSnapshot",
                      confSnap.getName() );

        repositoryPackageService.createModuleSnapshot( "testSnapshot",
                                                       "Y",
                                                       false,
                                                       "we" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshot" ).length );
        repositoryPackageService.createModuleSnapshot( "testSnapshot",
                                                       "X",
                                                       true,
                                                       "we" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshot" ).length );

        repositoryPackageService.copyOrRemoveSnapshot( "testSnapshot",
                                                       "X",
                                                       false,
                                                       "Q" );
        assertEquals( 3,
                      repositoryPackageService.listSnapshots( "testSnapshot" ).length );

        try {
            repositoryPackageService.copyOrRemoveSnapshot( "testSnapshot",
                                                           "X",
                                                           false,
                                                           "" );
            fail( "should not be able to copy snapshot to empty detination" );
        } catch ( SerializationException e ) {
            assertNotNull( e.getMessage() );
        }

        repositoryPackageService.copyOrRemoveSnapshot( "testSnapshot",
                                                       "X",
                                                       true,
                                                       null );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshot" ).length );

    }

    @Test
    public void testSnapshotRebuild() throws Exception {

        RulesRepository repo = rulesRepository;

        // get rid of other snapshot crap
        Iterator< ? > pkit = repo.listModules();
        while ( pkit.hasNext() ) {
            ModuleItem pkg = (ModuleItem) pkit.next();
            String[] snaps = repo.listModuleSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                repo.removeModuleSnapshot( pkg.getName(),
                                           snapName );
            }
        }

        final ModuleItem pkg = repo.createModule( "testSnapshotRebuild",
                                                  "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                          pkg );
        repo.save();

        AssetItem item = pkg.addAsset( "anAsset",
                                       "" );
        item.updateFormat( AssetFormats.DRL );
        item.updateContent( " rule abc \n when \n then \n System.out.println(42); \n end" );
        item.checkin( "" );

        BuilderResult builderResult = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                             true );
        assertFalse( builderResult.hasLines() );

        repositoryPackageService.createModuleSnapshot( "testSnapshotRebuild",
                                                       "SNAP",
                                                       false,
                                                       "" );

        ModuleItem snap = repo.loadModuleSnapshot( "testSnapshotRebuild",
                                                   "SNAP" );
        long snapTime = snap.getLastModified().getTimeInMillis();

        Thread.sleep( 100 );

        repositoryPackageService.rebuildSnapshots();

        ModuleItem snap_ = repo.loadModuleSnapshot( "testSnapshotRebuild",
                                                    "SNAP" );
        long newTime = snap_.getLastModified().getTimeInMillis();

        assertTrue( newTime > snapTime );

        item.updateContent( "garbage" );
        item.checkin( "" );

        repositoryPackageService.createModuleSnapshot( "testSnapshotRebuild",
                                                       "SNAP2",
                                                       false,
                                                       "" );

        try {
            repositoryPackageService.rebuildSnapshots();
        } catch ( DetailedSerializationException e ) {
            assertNotNull( e.getMessage() );
            assertNotNull( e.getLongDescription() );
        }

    }

    @Test
    public void testPackageRebuild() throws Exception {

        RulesRepository repo = rulesRepository;

        final ModuleItem pkg = repo.createModule( "testPackageRebuild",
                                                  "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                          pkg );
        repo.save();

        AssetItem item = pkg.addAsset( "anAsset",
                                       "" );
        item.updateFormat( AssetFormats.DRL );
        item.updateContent( " rule abc \n when \n then \n System.out.println(42); \n end" );
        item.checkin( "" );

        assertNull( pkg.getCompiledBinaryBytes() );

        long last = pkg.getLastModified().getTimeInMillis();
        Thread.sleep( 100 );
        try {
            repositoryPackageService.rebuildPackages();
        } catch ( DetailedSerializationException e ) {
            assertNotNull( e.getMessage() );
            assertNotNull( e.getLongDescription() );
        }

        assertFalse( pkg.getLastModified().getTimeInMillis() == last );
        assertNotNull( pkg.getCompiledBinaryBytes() );

    }

    @Test
    public void testExportPackage() throws Exception {
        int n = repositoryPackageService.listModules().length;
        repositoryCategoryService.createCategory( "/",
                                                  "testExportPackageCat1",
                                                  "desc" );
        repositoryCategoryService.createCategory( "/",
                                                  "testExportPackageCat2",
                                                  "desc" );
        ModuleItem p = rulesRepository.createModule( "testExportPackage",
                                                     "" );

        String uuid1 = serviceImplementation.createNewRule( "testExportPackageAsset1",
                                                            "desc",
                                                            "testExportPackageCat1",
                                                            "testExportPackage",
                                                            "dsl" );

        String uuid2 = serviceImplementation.createNewRule( "testExportPackageAsset2",
                                                            "desc",
                                                            "testExportPackageCat2",
                                                            "testExportPackage",
                                                            "dsl" );

        byte[] exportedPackage = repositoryPackageService.exportModules( "testExportPackage" );

        assertNotNull( exportedPackage );

        File file = new File( "testExportPackage.xml" );

        FileOutputStream fos = new FileOutputStream( file );

        fos.write( exportedPackage );
        fos.close();

        file.delete();
    }

    @Test
    public void testArchivePackage() throws Exception {
        Module[] pkgs = repositoryPackageService.listModules();

        Module[] arch = repositoryPackageService.listArchivedModules();

        repositoryPackageService.createModule( "testCreateArchivedPackage",
                                               "this is a new package",
                                               "package" );

        ModuleItem item = rulesRepository.loadModule( "testCreateArchivedPackage" );
        TableDataResult td = repositoryAssetService.loadArchivedAssets( 0,
                                                                        1000 );

        item.archiveItem( true );
        item.checkin( "" );

        TableDataResult td2 = repositoryAssetService.loadArchivedAssets( 0,
                                                                         1000 );
        assertEquals( td2.data.length,
                      td.data.length );

        Module[] arch2 = repositoryPackageService.listArchivedModules();
        assertEquals( arch.length + 1,
                      arch2.length );

        assertEquals( pkgs.length,
                      repositoryPackageService.listModules().length );

        item.archiveItem( false );
        item.checkin( "" );

        arch2 = repositoryPackageService.listArchivedModules();
        assertEquals( arch2.length,
                      arch.length );
    }

    @Test
    public void testCreatePackage() throws Exception {
        Module[] pkgs = repositoryPackageService.listModules();
        String uuid = repositoryPackageService.createModule( "testCreatePackage",
                                                             "this is a new package",
                                                             "package" );
        assertNotNull( uuid );

        ModuleItem item = rulesRepository.loadModule( "testCreatePackage" );
        assertNotNull( item );
        assertEquals( "this is a new package",
                      item.getDescription() );

        assertEquals( pkgs.length + 1,
                      repositoryPackageService.listModules().length );

        Module conf = repositoryPackageService.loadModule( uuid );
        assertEquals( "this is a new package",
                      conf.getDescription() );
        assertNotNull( conf.getLastModified() );

        pkgs = repositoryPackageService.listModules();

        repositoryPackageService.copyModule( "testCreatePackage",
                                             "testCreatePackage_COPY" );

        assertEquals( pkgs.length + 1,
                      repositoryPackageService.listModules().length );
        try {
            repositoryPackageService.copyModule( "testCreatePackage",
                                                 "testCreatePackage_COPY" );
        } catch ( RulesRepositoryException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    @Test
    public void testLoadPackageConfig() throws Exception {
        ModuleItem it = rulesRepository.loadDefaultModule();
        String uuid = it.getUUID();
        it.updateCoverage( "xyz" );
        it.updateExternalURI( "ext" );
        DroolsHeader.updateDroolsHeader( "header",
                                         it );
        rulesRepository.save();

        Module data = repositoryPackageService.loadModule( uuid );
        assertNotNull( data );

        assertEquals( RulesRepository.DEFAULT_PACKAGE,
                      data.getName() );
        assertEquals( "header",
                      data.getHeader() );
        assertEquals( "ext",
                      data.getExternalURI() );

        assertNotNull( data.getUuid() );
        assertFalse( data.isSnapshot() );

        assertNotNull( data.getDateCreated() );
        Date original = data.getLastModified();

        Thread.sleep( 100 );

        repositoryPackageService.createModuleSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                                       "TEST SNAP 2.0",
                                                       false,
                                                       "ya" );
        ModuleItem loaded = rulesRepository.loadModuleSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                                                "TEST SNAP 2.0" );

        data = repositoryPackageService.loadModule( loaded.getUUID() );
        assertTrue( data.isSnapshot() );
        assertEquals( "TEST SNAP 2.0",
                      data.getSnapshotName() );
        assertFalse( original.equals( data.getLastModified() ) );
        assertEquals( "ya",
                      data.getCheckinComment() );
    }

    @Test
    public void testArchiveAndUnarchivePackageAndHeader() throws Exception {
        String uuid = repositoryPackageService.createModule( "testArchiveAndUnarchivePackageAndHeader",
                                                             "a desc",
                                                             "package" );
        Module data = repositoryPackageService.loadModule( uuid );
        ModuleItem it = rulesRepository.loadModuleByUUID( uuid );
        data.setArchived( true );

        AssetItem rule1 = it.addAsset( "rule_1",
                                       "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.archiveItem( true );
        rule1.checkin( "" );
        rulesRepository.save();

        repositoryPackageService.saveModule( data );
        data = repositoryPackageService.loadModule( uuid );
        it = rulesRepository.loadModule( data.getName() );
        assertTrue( data.isArchived() );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.setArchived( false );

        repositoryPackageService.saveModule( data );
        data = repositoryPackageService.loadModule( uuid );
        it = rulesRepository.loadModule( data.getName() );
        assertFalse( data.isArchived() );
        assertFalse( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.setArchived( true );

        repositoryPackageService.saveModule( data );
        data = repositoryPackageService.loadModule( uuid );
        it = rulesRepository.loadModule( data.getName() );
        assertTrue( data.isArchived() );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

    }

    @Test
    public void testPackageConfSave() throws Exception {
        String uuid = repositoryPackageService.createModule( "testPackageConfSave",
                                                             "a desc",
                                                             "package" );
        Module data = repositoryPackageService.loadModule( uuid );

        data.setDescription( "new desc" );
        data.setHeader( "wa" );
        data.setExternalURI( "new URI" );
        repositoryPackageService.saveModule( data );

        ValidatedResponse res = repositoryPackageService.validateModule( data );
        assertNotNull( res );
        assertTrue( res.hasErrors );
        assertNotNull( res.errorMessage );

        data = repositoryPackageService.loadModule( uuid );
        assertEquals( "new desc",
                      data.getDescription() );
        assertEquals( "wa",
                      data.getHeader() );
        assertEquals( "new URI",
                      data.getExternalURI() );

        data.setHeader( "" );
        res = repositoryPackageService.validateModule( data );
        if ( res.hasErrors ) {
            System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
            System.out.println( res.errorMessage );
            System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

        }

        assertFalse( res.hasErrors );
    }

    @Test
    public void testUpdateModuleFormat() throws Exception {
        String uuid = repositoryPackageService.createModule( "testUpdateModuleFormat",
                                                             "a desc",
                                                             "package" );
        Module data = repositoryPackageService.loadModule( uuid );
        assertEquals( "a desc",
                      data.getDescription() );
        assertEquals( "package",
                      data.getFormat() );

        data.setFormat( "SOAService" );
        repositoryPackageService.saveModule( data );

        data = repositoryPackageService.loadModule( uuid );
        assertEquals( "SOAService",
                      data.getFormat() );
    }

    @Test
    public void testRemovePackage() throws Exception {
        int n = repositoryPackageService.listModules().length;
        ModuleItem p = rulesRepository.createModule( "testRemovePackage",
                                                     "" );
        assertNotNull( repositoryPackageService.loadModule( p.getUUID() ) );

        repositoryPackageService.removeModule( p.getUUID() );
        assertEquals( n,
                      repositoryPackageService.listModules().length );
    }

    @Test
    public void testSnapshotDiffPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        // Lets make a package and put a rule into it
        repositoryCategoryService.createCategory( "/",
                                                  "testSnapshotDiffPagedResultsCategory",
                                                  "testSnapshotDiffPagedResultsCategoryDescription" );
        String packageUuid = repositoryPackageService.createModule( "testSnapshotDiffPagedResultsPackage",
                                                                    "testSnapshotDiffPagedResultsPackageDescription",
                                                                    "package" );
        assertNotNull( packageUuid );

        // Create some rules
        String archiveRuleUuid = serviceImplementation.createNewRule( "testRuleArchived",
                                                                      "testRuleArchivedDescription",
                                                                      "testSnapshotDiffPagedResultsCategory",
                                                                      "testSnapshotDiffPagedResultsPackage",
                                                                      AssetFormats.DRL );
        String modifiedRuleUuid = serviceImplementation.createNewRule( "testRuleModified",
                                                                       "testRuleModifiedDescription",
                                                                       "testSnapshotDiffPagedResultsCategory",
                                                                       "testSnapshotDiffPagedResultsPackage",
                                                                       AssetFormats.DRL );
        String deletedRuleUuid = serviceImplementation.createNewRule( "testRuleDeleted",
                                                                      "testRuleDeletedDescription",
                                                                      "testSnapshotDiffPagedResultsCategory",
                                                                      "testSnapshotDiffPagedResultsPackage",
                                                                      AssetFormats.DRL );
        String restoredRuleUuid = serviceImplementation.createNewRule( "testRuleRestored",
                                                                       "testRuleRestoredDescription",
                                                                       "testSnapshotDiffPagedResultsCategory",
                                                                       "testSnapshotDiffPagedResultsPackage",
                                                                       AssetFormats.DRL );

        repositoryAssetService.archiveAsset( restoredRuleUuid );

        @SuppressWarnings("unused")
        String noChangesRuleUuid = serviceImplementation.createNewRule( "testRuleNoChanges",
                                                                        "testRuleNoChangesDescription",
                                                                        "testSnapshotDiffPagedResultsCategory",
                                                                        "testSnapshotDiffPagedResultsPackage",
                                                                        AssetFormats.DRL );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiffPagedResultsPackage",
                                                       "FIRST",
                                                       false,
                                                       "First snapshot" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "testSnapshotDiffPagedResultsPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiffPagedResultsPackage" ).length );

        // Change a rule...
        Asset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = repositoryAssetService.checkinVersion( asset );
        assertNotNull( uuid );

        //...delete one...
        repositoryAssetService.removeAsset( deletedRuleUuid );

        //...archive one...
        repositoryAssetService.archiveAsset( archiveRuleUuid );

        //...create a new one...
        @SuppressWarnings("unused")
        String addedRuleUuid = serviceImplementation.createNewRule( "testRuleAdded",
                                                                    "testRuleAddedDescription",
                                                                    "testSnapshotDiffPagedResultsCategory",
                                                                    "testSnapshotDiffPagedResultsPackage",
                                                                    AssetFormats.DRL );

        //...and unarchive one
        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiffPagedResultsPackage",
                                                       "SECOND",
                                                       false,
                                                       "Second snapshot" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshotDiffPagedResultsPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiffPagedResultsPackage" ).length );

        // Compare the snapshots
        SnapshotComparisonPageRequest request = new SnapshotComparisonPageRequest( "testSnapshotDiffPagedResultsPackage",
                                                                                   "FIRST",
                                                                                   "SECOND",
                                                                                   0,
                                                                                   PAGE_SIZE );
        SnapshotComparisonPageResponse response;
        response = repositoryPackageService.compareSnapshots( request );

        assertEquals( "FIRST",
                      response.getLeftSnapshotName() );
        assertEquals( "SECOND",
                      response.getRightSnapshotName() );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryPackageService.compareSnapshots( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE * 2 );
        response = repositoryPackageService.compareSnapshots( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE * 2 );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testSnapshotDiffFullResults() throws Exception {

        // Lets make a package and put a rule into it
        repositoryCategoryService.createCategory( "/",
                                                  "testSnapshotDiffFullResultsCategory",
                                                  "testSnapshotDiffFullResultsCategoryDescription" );
        String packageUuid = repositoryPackageService.createModule( "testSnapshotDiffFullResultsPackage",
                                                                    "testSnapshotDiffFullResultsPackageDescription",
                                                                    "package" );
        assertNotNull( packageUuid );

        // Create some rules
        String archiveRuleUuid = serviceImplementation.createNewRule( "testRuleArchived",
                                                                      "testRuleArchivedDescription",
                                                                      "testSnapshotDiffFullResultsCategory",
                                                                      "testSnapshotDiffFullResultsPackage",
                                                                      AssetFormats.DRL );
        String modifiedRuleUuid = serviceImplementation.createNewRule( "testRuleModified",
                                                                       "testRuleModifiedDescription",
                                                                       "testSnapshotDiffFullResultsCategory",
                                                                       "testSnapshotDiffFullResultsPackage",
                                                                       AssetFormats.DRL );
        String deletedRuleUuid = serviceImplementation.createNewRule( "testRuleDeleted",
                                                                      "testRuleDeletedDescription",
                                                                      "testSnapshotDiffFullResultsCategory",
                                                                      "testSnapshotDiffFullResultsPackage",
                                                                      AssetFormats.DRL );
        String restoredRuleUuid = serviceImplementation.createNewRule( "testRuleRestored",
                                                                       "testRuleRestoredDescription",
                                                                       "testSnapshotDiffFullResultsCategory",
                                                                       "testSnapshotDiffFullResultsPackage",
                                                                       AssetFormats.DRL );

        repositoryAssetService.archiveAsset( restoredRuleUuid );

        @SuppressWarnings("unused")
        String noChangesRuleUuid = serviceImplementation.createNewRule( "testRuleNoChanges",
                                                                        "testRuleNoChangesDescription",
                                                                        "testSnapshotDiffFullResultsCategory",
                                                                        "testSnapshotDiffFullResultsPackage",
                                                                        AssetFormats.DRL );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiffFullResultsPackage",
                                                       "FIRST",
                                                       false,
                                                       "First snapshot" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "testSnapshotDiffFullResultsPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiffFullResultsPackage" ).length );

        // Change a rule...
        Asset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = repositoryAssetService.checkinVersion( asset );
        assertNotNull( uuid );

        //...delete one...
        repositoryAssetService.removeAsset( deletedRuleUuid );

        //...archive one...
        repositoryAssetService.archiveAsset( archiveRuleUuid );

        //...create a new one...
        @SuppressWarnings("unused")
        String addedRuleUuid = serviceImplementation.createNewRule( "testRuleAdded",
                                                                    "testRuleAddedDescription",
                                                                    "testSnapshotDiffFullResultsCategory",
                                                                    "testSnapshotDiffFullResultsPackage",
                                                                    AssetFormats.DRL );

        //...and unarchive one
        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createModuleSnapshot( "testSnapshotDiffFullResultsPackage",
                                                       "SECOND",
                                                       false,
                                                       "Second snapshot" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshotDiffFullResultsPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiffFullResultsPackage" ).length );

        // Compare the snapshots
        SnapshotComparisonPageRequest request = new SnapshotComparisonPageRequest( "testSnapshotDiffFullResultsPackage",
                                                                                   "FIRST",
                                                                                   "SECOND",
                                                                                   0,
                                                                                   null );
        SnapshotComparisonPageResponse response;
        response = repositoryPackageService.compareSnapshots( request );

        assertEquals( "FIRST",
                      response.getLeftSnapshotName() );
        assertEquals( "SECOND",
                      response.getRightSnapshotName() );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 5 );
        assertTrue( response.isLastPage() );

        for ( SnapshotComparisonPageRow row : response.getPageRowList() ) {
            SnapshotDiff diff = row.getDiff();
            if ( diff.name.equals( "testRuleArchived" ) ) {
                assertEquals( SnapshotDiff.TYPE_ARCHIVED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleModified" ) ) {
                assertEquals( SnapshotDiff.TYPE_UPDATED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleAdded" ) ) {
                assertEquals( SnapshotDiff.TYPE_ADDED,
                              diff.diffType );
                assertNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleDeleted" ) ) {
                assertEquals( SnapshotDiff.TYPE_DELETED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNull( diff.rightUuid );
            } else if ( diff.name.equals( "testRuleRestored" ) ) {
                assertEquals( SnapshotDiff.TYPE_RESTORED,
                              diff.diffType );
                assertNotNull( diff.leftUuid );
                assertNotNull( diff.rightUuid );
            } else {
                fail( "Diff not expected." );
            }
        }
    }

    /**
     * This will test creating a package, check it compiles, and can exectute
     * rules, then take a snapshot, and check that it reports errors.
     */

    @Test
    public void testBinaryPackageCompileAndExecute() throws Exception {
        RulesRepository repo = rulesRepository;

        // create our package
        ModuleItem pkg = repo.createModule( "testBinaryPackageCompile",
                                            "" );
        DroolsHeader.updateDroolsHeader( "global java.util.List ls \n import org.drools.Person",
                                          pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        BuilderResult result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                      true );
        
        List<BuilderResultLine> lines = result.getLines();
        assertFalse( result.hasLines() );

        pkg = repo.loadModule( "testBinaryPackageCompile" );
        byte[] binPackage = pkg.getCompiledBinaryBytes();

        assertNotNull( binPackage );

        Package binPkg = (Package) DroolsStreamUtils.streamIn( binPackage );

        assertNotNull( binPkg );
        assertTrue( binPkg.isValid() );

        Person p = new Person();

        BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
        loader.addPackage( new ByteArrayInputStream( binPackage ) );
        RuleBase rb = loader.getRuleBase();

        StatelessSession sess = rb.newStatelessSession();
        sess.setGlobal( "ls",
                        new ArrayList<String>() );
        sess.execute( p );

        assertEquals( 42,
                      p.getAge() );

        repositoryPackageService.createModuleSnapshot( "testBinaryPackageCompile",
                                                       "SNAP1",
                                                       false,
                                                       "" );

        rule1.updateContent( "rule 'rule1' \n when p:PersonX() \n then System.err.println(42); \n end" );
        rule1.checkin( "" );

        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertNotNull( result );
        assertEquals( 1,
                      result.getLines().size() );
        assertEquals( rule1.getName(),
                      result.getLines().get( 0 ).getAssetName() );
        assertEquals( AssetFormats.DRL,
                      result.getLines().get( 0 ).getAssetFormat() );
        assertNotNull( result.getLines().get( 0 ).getMessage() );
        assertEquals( rule1.getUUID(),
                      result.getLines().get( 0 ).getUuid() );

        pkg = repo.loadModuleSnapshot( "testBinaryPackageCompile",
                                       "SNAP1" );
        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertFalse( result.hasLines() );

    }

    /**
     * This will test creating a package with a BRL rule, check it compiles, and
     * can exectute rules, then take a snapshot, and check that it reports
     * errors.
     */

    @Test
    public void testBinaryPackageCompileAndExecuteWithBRXML() throws Exception {
        RulesRepository repo = rulesRepository;

        // create our package
        ModuleItem pkg = repo.createModule( "testBinaryPackageCompileBRL",
                                            "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                          pkg );
        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.BUSINESS_RULE );

        RuleModel model = new RuleModel();
        model.name = "rule2";
        FactPattern pattern = new FactPattern( "Person" );

        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con.setValue( "name soundslike 'foobar'" );
        pattern.addConstraint( con );

        pattern.setBoundName( "p" );
        ActionSetField action = new ActionSetField( "p" );
        ActionFieldValue value = new ActionFieldValue( "age",
                                                       "42",
                                                       SuggestionCompletionEngine.TYPE_NUMERIC );
        action.addFieldValue( value );

        model.addLhsItem( pattern );
        model.addRhsItem( action );

        rule2.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        rule2.checkin( "" );
        repo.save();

        BuilderResult result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                      true );
        if ( result != null ) {
            for ( int i = 0; i < result.getLines().size(); i++ ) {
                System.err.println( result.getLines().get( i ).getMessage() );
            }
        }
        assertFalse( result.hasLines() );

        pkg = repo.loadModule( "testBinaryPackageCompileBRL" );
        byte[] binPackage = pkg.getCompiledBinaryBytes();

        // Here is where we write it out is needed... set to true if needed for
        // the binary test below "testLoadAndExecBinary"
        boolean saveBinPackage = false;
        if ( saveBinPackage ) {
            //FileOutputStream out = new FileOutputStream( "RepoBinPackage.pkg" );
            FileOutputStream out = new FileOutputStream( "src/test/resources/org/drools/guvnor/server/RepoBinPackage.pkg" );
            out.write( binPackage );
            out.flush();
            out.close();
        }

        assertNotNull( binPackage );

        Package binPkg = (Package) DroolsStreamUtils.streamIn( binPackage );

        assertNotNull( binPkg );
        assertTrue( binPkg.isValid() );

        // and this shows off the "soundex" thing...
        Person p = new Person( "fubar" );

        BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
        loader.addPackage( new ByteArrayInputStream( binPackage ) );
        RuleBase rb = loader.getRuleBase();

        StatelessSession sess = rb.newStatelessSession();
        sess.execute( p );
        assertEquals( 42,
                      p.getAge() );

        repositoryPackageService.createModuleSnapshot( "testBinaryPackageCompileBRL",
                                                       "SNAP1",
                                                       false,
                                                       "" );

        pattern.setFactType( "PersonX" );
        rule2.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        rule2.checkin( "" );

        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertNotNull( result );
        assertTrue( result.getLines().size() > 0 );
        // assertEquals(2, results.length);
        assertEquals( rule2.getName(),
                      result.getLines().get( 0 ).getAssetName() );
        assertEquals( AssetFormats.BUSINESS_RULE,
                      result.getLines().get( 0 ).getAssetFormat() );
        assertNotNull( result.getLines().get( 0 ).getMessage() );
        assertEquals( rule2.getUUID(),
                      result.getLines().get( 0 ).getUuid() );

        pkg = repo.loadModuleSnapshot( "testBinaryPackageCompileBRL",
                                       "SNAP1" );
        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertFalse( result.hasLines() );

        // check that the rule name in the model is being set
        AssetItem asset2 = pkg.addAsset( "testSetRuleName",
                                         "" );
        asset2.updateFormat( AssetFormats.BUSINESS_RULE );
        asset2.checkin( "" );

        RuleModel model2 = new RuleModel();
        assertNull( model2.name );

        Asset asset = repositoryAssetService.loadRuleAsset( asset2.getUUID() );
        asset.setContent( (PortableObject) model2 );

        repositoryAssetService.checkinVersion( asset );

        asset = repositoryAssetService.loadRuleAsset( asset2.getUUID() );

        model2 = (RuleModel) asset.getContent();
        assertNotNull( model2 );
        assertNotNull( model2.name );
        assertEquals( asset2.getName(),
                      model2.name );

    }

    /**
     * this loads up a precompile binary package. If this fails, then it means
     * it needs to be updated. It gets the package form the BRL example above.
     * Simply set saveBinPackage to true to save a new version of the
     * RepoBinPackage.pkg.
     */
    @Test
    public void testLoadAndExecBinary() throws Exception {
        Person p = new Person( "fubar" );
        BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
        loader.addPackage( this.getClass().getResourceAsStream( "RepoBinPackage.pkg" ) );
        RuleBase rb = loader.getRuleBase();
        StatelessSession sess = rb.newStatelessSession();
        sess.execute( p );
        assertEquals( 42,
                      p.getAge() );
    }

    @Test
    public void testPackageSource() throws Exception {
        RulesRepository repo = rulesRepository;
        // create our package
        ModuleItem pkg = repo.createModule( "testPackageSource",
                                            "" );
        DroolsHeader.updateDroolsHeader( "import org.goo.Ber",
                                          pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when p:Person() \n then p.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        AssetItem func = pkg.addAsset( "funky",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "this is a func" );
        func.checkin( "" );

        String drl = repositoryPackageService.buildModuleSource( pkg.getUUID() );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "import org.goo.Ber" ) > -1 );
        assertTrue( drl.indexOf( "package testPackageSource" ) > -1 );
        assertTrue( drl.indexOf( "rule 'rule1'" ) > -1 );
        assertTrue( drl.indexOf( "this is a func" ) > -1 );
        assertTrue( drl.indexOf( "this is a func" ) < drl.indexOf( "rule 'rule1'" ) );
        assertTrue( drl.indexOf( "package testPackageSource" ) < drl.indexOf( "this is a func" ) );
        assertTrue( drl.indexOf( "package testPackageSource" ) < drl.indexOf( "import org.goo.Ber" ) );

        AssetItem dsl = pkg.addAsset( "MyDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[when]This is foo=bar()\n[then]do something=yeahMan();" );
        dsl.checkin( "" );

        AssetItem asset = pkg.addAsset( "MyDSLRule",
                                        "" );
        asset.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        asset.updateContent( "when \n This is foo \n then \n do something" );
        asset.checkin( "" );

        drl = repositoryPackageService.buildModuleSource( pkg.getUUID() );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "import org.goo.Ber" ) > -1 );
        assertTrue( drl.indexOf( "This is foo" ) == -1 );
        assertTrue( drl.indexOf( "do something" ) == -1 );
        assertTrue( drl.indexOf( "bar()" ) > 0 );
        assertTrue( drl.indexOf( "yeahMan();" ) > 0 );

    }

}
