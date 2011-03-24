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

package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.core.util.DateUtils;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PermissionsPageRow;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRow;
import org.drools.guvnor.client.rpc.SnapshotDiff;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.ide.common.server.util.RepositoryUpgradeHelper;
import org.drools.ide.common.server.util.ScenarioXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.repository.UserInfo.InboxEntry;
import org.drools.rule.Package;
import org.drools.type.DateFormatsImpl;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This is really a collection of integration tests.
 */
public class ServiceImplementationTest extends GuvnorTestBase {

    @Test
    @Ignore("this test fail intermittently")
    public void testInboxEvents() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        assertNotNull( impl.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID ) );

        //this should trigger the fact that the original user edited something
        AssetItem as = impl.getRulesRepository().loadDefaultPackage().addAsset( "testLoadInbox",
                                                                                "" );
        as.checkin( "" );
        TableDataResult res = impl.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID );
        boolean found = false;
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertTrue( found );

        //but should not be in "incoming" yet
        found = false;
        res = impl.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertFalse( found );

        //Now, another user comes along, makes a change...
        RulesRepository repo2 = new RulesRepository( TestEnvironmentSessionHelper.getSessionFor( "thirdpartyuser" ) );
        AssetItem as2 = repo2.loadDefaultPackage().loadAsset( "testLoadInbox" );
        as2.updateContent( "hey" );
        as2.checkin( "here we go again !" );

        Thread.sleep( 200 );

        //now check that it is in the first users inbox
        TableDataRow rowMatch = null;
        res = impl.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) {
                rowMatch = row;
                break;
            }
        }
        assertNotNull( rowMatch );
        assertEquals( as.getName(),
                      rowMatch.values[0] );
        assertEquals( "thirdpartyuser",
                      rowMatch.values[2] ); //should be "from" that user name...

        //shouldn't be in thirdpartyusers inbox
        UserInbox ib = new UserInbox( repo2 );
        ib.loadIncoming();
        assertEquals( 0,
                      ib.loadIncoming().size() );
        assertEquals( 1,
                      ib.loadRecentEdited().size() );

        //ok lets create another user...
        RulesRepository repo3 = new RulesRepository( TestEnvironmentSessionHelper.getSessionFor( "fourthuser" ) );
        AssetItem as3 = repo3.loadDefaultPackage().loadAsset( "testLoadInbox" );
        as3.updateContent( "hey22" );
        as3.checkin( "here we go again 22!" );

        Thread.sleep( 250 );

        //so should be in thirdpartyuser inbox
        assertEquals( 1,
                      ib.loadIncoming().size() );

        //and also still in the original user...
        found = false;
        res = impl.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertTrue( found );

        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        //now lets open it with first user, and check that it disappears from the incoming...
        repositoryAssetService.loadRuleAsset( as.getUUID() );
        found = false;
        res = impl.loadInbox( ExplorerNodeConfig.INCOMING_ID );
        for ( TableDataRow row : res.data ) {
            if ( row.id.equals( as.getUUID() ) ) found = true;
        }
        assertFalse( found );

    }

    @Ignore
    @Test
    public void testCategory() throws Exception {
        ServiceImplementation serviceImplementation = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();

        String[] originalCats = repositoryCategoryService.loadChildCategories( "/" );

        Boolean result = repositoryCategoryService.createCategory( "/",
                                                                   "TopLevel1",
                                                                   "a description" );
        assertTrue( result.booleanValue() );

        result = repositoryCategoryService.createCategory( "/",
                                                           "TopLevel2",
                                                           "a description" );
        assertTrue( result.booleanValue() );

        String[] cats = repositoryCategoryService.loadChildCategories( "/" );
        assertTrue( cats.length == originalCats.length + 2 );

        result = repositoryCategoryService.createCategory( "",
                                                           "Top3",
                                                           "description" );
        assertTrue( result.booleanValue() );

        result = repositoryCategoryService.createCategory( null,
                                                           "Top4",
                                                           "description" );
        assertTrue( result.booleanValue() );

    }

    @Ignore
    @Test
    public void testDeleteUnversionedRule() throws Exception {
        getRulesRepository();
        ServiceImplementation impl = getServiceImplementation();

        impl.getRulesRepository().loadDefaultPackage();
        impl.getRulesRepository().createPackage( "anotherPackage",
                                                 "woot" );

        CategoryItem cat = impl.getRulesRepository().loadCategory( "/" );
        cat.addCategory( "testDeleteUnversioned",
                         "yeah" );

        String uuid = impl.createNewRule( "test Delete Unversioned",
                                          "a description",
                                          "testDeleteUnversioned",
                                          "anotherPackage",
                                          "txt" );
        assertNotNull( uuid );
        assertFalse( "".equals( uuid ) );

        AssetItem localItem = impl.getRulesRepository().loadAssetByUUID( uuid );

        // String drl = "package org.drools.repository\n\ndialect 'mvel'\n\n" +
        // "rule Rule1 \n when \n AssetItem(description != null) \n then \n
        // System.out.println(\"yeah\");\nend";
        // RuleBase rb = RuleBaseLoader.getInstance().loadFromReader(new
        // StringReader(drl));
        // rb.newStatelessSession().execute(localItem);

        assertEquals( "test Delete Unversioned",
                      localItem.getName() );

        localItem.remove();
        impl.getRulesRepository().save();

        try {
            localItem = impl.getRulesRepository().loadAssetByUUID( uuid );
            fail();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testAddRuleAndListPackages() throws Exception {
        // ServiceImpl impl = new ServiceImpl(new
        // RulesRepository(SessionHelper.getSession()));

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        impl.getRulesRepository().loadDefaultPackage();
        impl.getRulesRepository().createPackage( "another",
                                                 "woot" );

        CategoryItem cat = impl.getRulesRepository().loadCategory( "/" );
        cat.addCategory( "testAddRule",
                         "yeah" );

        String result = impl.createNewRule( "test AddRule",
                                            "a description",
                                            "testAddRule",
                                            "another",
                                            "txt" );
        assertNotNull( result );
        assertFalse( "".equals( result ) );

        PackageConfigData[] packages = repositoryPackageService.listPackages();
        assertTrue( packages.length > 0 );

        boolean found = false;
        for ( int i = 0; i < packages.length; i++ ) {
            if ( packages[i].name.equals( "another" ) ) {
                found = true;
            }
        }

        assertTrue( found );

        assertFalse( packages[0].uuid == null );
        assertFalse( packages[0].uuid.equals( "" ) );

        // just for performance testing with scaling up numbers of rules
        // for (int i=1; i <= 1000; i++) {
        // impl.createNewRule( "somerule_" + i, "description",
        // "testAddRule", "another", "drl" );
        // }

        result = impl.createNewRule( "testDTSample",
                                     "a description",
                                     "testAddRule",
                                     "another",
                                     AssetFormats.DECISION_SPREADSHEET_XLS );
        AssetItem dtItem = impl.getRulesRepository().loadAssetByUUID( result );
        assertNotNull( dtItem.getBinaryContentAsBytes() );
        assertTrue( dtItem.getBinaryContentAttachmentFileName().endsWith( ".xls" ) );
    }

    @Test
    @Ignore
    public void testAttemptDupeRule() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        CategoryItem cat = impl.getRulesRepository().loadCategory( "/" );
        cat.addCategory( "testAttemptDupeRule",
                         "yeah" );

        impl.getRulesRepository().createPackage( "dupes",
                                                 "yeah" );

        impl.createNewRule( "testAttemptDupeRule",
                            "ya",
                            "testAttemptDupeRule",
                            "dupes",
                            "rule" );

        String uuid = impl.createNewRule( "testAttemptDupeRule",
                                          "ya",
                                          "testAttemptDupeRule",
                                          "dupes",
                                          "rule" );
        assertEquals( "DUPLICATE",
                      uuid );

    }

    @Test
    @Ignore
    public void testCreateNewRule() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testCreateNewRule",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testCreateNewRule",
                                                  "this is a cat" );

        String uuid = impl.createNewRule( "testCreateNewRuleName",
                                          "an initial desc",
                                          "testCreateNewRule",
                                          "testCreateNewRule",
                                          AssetFormats.DSL_TEMPLATE_RULE );
        assertNotNull( uuid );
        assertFalse( "".equals( uuid ) );

        AssetItem dtItem = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( dtItem.getDescription(),
                      "an initial desc" );
    }

    @Test
    @Ignore
    public void testCreateLinkedAssetItem() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        @SuppressWarnings("unused")
        PackageItem testCreateNewRuleAsLinkPackage1 = impl.getRulesRepository().createPackage( "testCreateNewRuleAsLinkPackage1",
                                                                                               "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testCreateNewRuleAsLinkCat1",
                                                  "this is a cat" );
        repositoryCategoryService.createCategory( "",
                                                  "testCreateNewRuleAsLinkCat2",
                                                  "this is a cat" );

        //Create the shared asset.
        String uuid = impl.createNewRule( "testCreateLinkedAssetItemRule",
                                          "an initial desc",
                                          "testCreateNewRuleAsLinkCat1",
                                          "globalArea",
                                          AssetFormats.DSL_TEMPLATE_RULE );
        assertNotNull( uuid );
        assertFalse( "".equals( uuid ) );

        AssetItem dtItem = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( dtItem.getDescription(),
                      "an initial desc" );

        //create an asset which is imported from global area. 
        String uuidLink = impl.createNewImportedRule( "testCreateLinkedAssetItemRule",
                                                      "testCreateNewRuleAsLinkPackage1" );
        assertNotNull( uuidLink );
        assertFalse( "".equals( uuidLink ) );
        assertTrue( uuidLink.equals( uuid ) );

        //now verify the linked asset.
        AssetItem itemLink = impl.getRulesRepository().loadAssetByUUID( uuidLink );
        assertEquals( itemLink.getName(),
                      "testCreateLinkedAssetItemRule" );
        assertEquals( itemLink.getDescription(),
                      "an initial desc" );
        assertEquals( itemLink.getFormat(),
                      AssetFormats.DSL_TEMPLATE_RULE );
        assertEquals( itemLink.getPackage().getName(),
                      "globalArea" );

        assertEquals( itemLink.getPackageName(),
                      "globalArea" );

        assertTrue( itemLink.getCategories().size() == 1 );
        assertTrue( itemLink.getCategorySummary().contains( "testCreateNewRuleAsLinkCat1" ) );

        //now verify the original asset.
        AssetItem referredItem = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( referredItem.getName(),
                      "testCreateLinkedAssetItemRule" );
        assertEquals( referredItem.getDescription(),
                      "an initial desc" );
        assertEquals( referredItem.getFormat(),
                      AssetFormats.DSL_TEMPLATE_RULE );
        assertEquals( referredItem.getPackage().getName(),
                      "globalArea" );

        assertTrue( referredItem.getCategories().size() == 1 );
        assertTrue( referredItem.getCategorySummary().contains( "testCreateNewRuleAsLinkCat1" ) );

        //now verify AssetItemIterator works by calling search
        AssetItemIterator it = impl.getRulesRepository().findAssetsByName( "testCreateLinkedAssetItemRule%",
                                                                           true );
        //NOTE, getSize() may return -1
        /*       assertEquals( 1,
                             it.getSize() );*/
        int size = 0;
        while ( it.hasNext() ) {
            size++;
            AssetItem ai = it.next();
            if ( ai.getUUID().equals( uuid ) ) {
                assertEquals( ai.getPackage().getName(),
                              "globalArea" );
                assertEquals( ai.getDescription(),
                              "an initial desc" );
            } else {
                fail( "unexptected asset found: " + ai.getPackage().getName() );
            }
        }
        assertEquals( 1,
                      size );
    }

    @Test
    @Ignore
    public void testLinkedAssetItemHistoryRelated() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        @SuppressWarnings("unused")
        PackageItem testCreateNewRuleAsLinkPackage1 = impl.getRulesRepository().createPackage( "testLinkedAssetItemHistoryRelatedPack",
                                                                                               "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testLinkedAssetItemHistoryRelatedCat",
                                                  "this is a cat" );

        //Create the shared asset in global area.
        String uuid = impl.createNewRule( "testLinkedAssetItemHistoryRelatedRule",
                                          "an initial desc",
                                          "testLinkedAssetItemHistoryRelatedCat",
                                          "globalArea",
                                          AssetFormats.DSL_TEMPLATE_RULE );

        //create an asset which refers to the shared assets.
        String uuidLink = impl.createNewImportedRule( "testLinkedAssetItemHistoryRelatedRule",
                                                      "testLinkedAssetItemHistoryRelatedPack" );
        assertTrue( uuidLink.equals( uuid ) );

        //create version 1.
        RuleAsset assetWrapper = repositoryAssetService.loadRuleAsset( uuidLink );
        assertEquals( assetWrapper.metaData.description,
                      "an initial desc" );
        assetWrapper.metaData.description = "version 1";
        String uuidLink1 = impl.checkinVersion( assetWrapper );

        //create version 2
        RuleAsset assetWrapper2 = repositoryAssetService.loadRuleAsset( uuidLink );
        assetWrapper2.metaData.description = "version 2";
        String uuidLink2 = impl.checkinVersion( assetWrapper2 );

        //create version head
        RuleAsset assetWrapper3 = repositoryAssetService.loadRuleAsset( uuidLink );
        assetWrapper3.metaData.description = "version head";
        @SuppressWarnings("unused")
        String uuidLink3 = impl.checkinVersion( assetWrapper3 );

        assertEquals( uuidLink,
                      uuidLink1 );
        assertEquals( uuidLink,
                      uuidLink2 );

        //verify the history info of LinkedAssetItem
        TableDataResult result = repositoryAssetService.loadItemHistory( uuidLink );
        assertNotNull( result );
        TableDataRow[] rows = result.data;
        assertEquals( 3,
                      rows.length );
        assertFalse( rows[0].id.equals( uuidLink ) );
        assertFalse( rows[1].id.equals( uuidLink ) );
        assertFalse( rows[2].id.equals( uuidLink ) );

        RuleAsset version1 = repositoryAssetService.loadRuleAsset( rows[0].id );
        RuleAsset version2 = repositoryAssetService.loadRuleAsset( rows[1].id );
        RuleAsset version3 = repositoryAssetService.loadRuleAsset( rows[2].id );
        RuleAsset versionHead = repositoryAssetService.loadRuleAsset( uuidLink );
        assertFalse( version1.metaData.versionNumber == version2.metaData.versionNumber );
        assertFalse( version1.metaData.versionNumber == versionHead.metaData.versionNumber );
        assertEquals( version1.metaData.description,
                      "an initial desc" );
        assertEquals( version2.metaData.description,
                      "version 1" );
        assertEquals( version3.metaData.description,
                      "version 2" );
        assertEquals( versionHead.metaData.description,
                      "version head" );

        //verify the history info of the original AssetItem
        result = repositoryAssetService.loadItemHistory( uuid );
        assertNotNull( result );
        rows = result.data;
        assertEquals( 3,
                      rows.length );
        assertFalse( rows[0].id.equals( uuid ) );
        assertFalse( rows[1].id.equals( uuid ) );

        version1 = repositoryAssetService.loadRuleAsset( rows[0].id );
        version2 = repositoryAssetService.loadRuleAsset( rows[1].id );
        versionHead = repositoryAssetService.loadRuleAsset( uuid );
        assertFalse( version1.metaData.versionNumber == version2.metaData.versionNumber );
        assertFalse( version1.metaData.versionNumber == versionHead.metaData.versionNumber );
        assertTrue( version1.metaData.description.equals( "an initial desc" ) );
        assertTrue( version2.metaData.description.equals( "version 1" ) );
        assertTrue( versionHead.metaData.description.equals( "version head" ) );

        //test restore
        impl.restoreVersion( version1.uuid,
                             versionHead.uuid,
                             "this was cause of a mistake" );

        RuleAsset newHead = repositoryAssetService.loadRuleAsset( uuid );

        assertEquals( "this was cause of a mistake",
                      newHead.metaData.checkinComment );
    }

    //path name contains Apostrophe is no longer a problem with jackrabbit 2.0

    @Test
    @Ignore
    public void testCreateNewRuleContainsApostrophe() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testCreateNewRuleContainsApostrophe",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testCreateNewRuleContainsApostrophe",
                                                  "this is a cat" );

        String uuid = null;
        try {
            uuid = impl.createNewRule( "testCreateNewRuleContains' character",
                                       "an initial desc",
                                       "testCreateNewRuleContainsApostrophe",
                                       "testCreateNewRuleContainsApostrophe",
                                       AssetFormats.DSL_TEMPLATE_RULE );
            //fail( "did not get expected exception" );
        } catch ( SerializationException e ) {
            //assertTrue( e.getMessage().indexOf( "'testCreateNewRuleContains' character' is not a valid path. ''' not a valid name character" ) >= 0 );
        }

        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset assetWrapper = repositoryAssetService.loadRuleAsset( uuid );
        assertEquals( assetWrapper.metaData.description,
                      "an initial desc" );
        assertEquals( assetWrapper.metaData.name,
                      "testCreateNewRuleContains' character" );

    }

    @Test
    @Ignore
    @Deprecated
    public void testRuleTableLoad() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        TableConfig conf = impl.loadTableConfig( ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertNotNull( conf.headers );
        assertNotNull( conf.headerTypes );

        CategoryItem cat = impl.getRulesRepository().loadCategory( "/" );
        cat.addCategory( "testRuleTableLoad",
                         "yeah" );

        impl.getRulesRepository().createPackage( "testRuleTableLoad",
                                                 "yeah" );
        impl.createNewRule( "testRuleTableLoad",
                            "ya",
                            "testRuleTableLoad",
                            "testRuleTableLoad",
                            "rule" );
        impl.createNewRule( "testRuleTableLoad2",
                            "ya",
                            "testRuleTableLoad",
                            "testRuleTableLoad",
                            "rule" );

        TableDataResult result = repositoryCategoryService.loadRuleListForCategories( "testRuleTableLoad",
                                                                                      0,
                                                                                      -1,
                                                                                      ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 2,
                      result.data.length );

        String key = result.data[0].id;
        assertFalse( key.startsWith( "testRule" ) );

        assertEquals( result.data[0].format,
                      "rule" );

        assertTrue( result.data[0].values[0].startsWith( "testRuleTableLoad" ) );
    }

    @Test
    @Ignore
    @Deprecated
    public void testDateFormatting() throws Exception {
        Calendar cal = Calendar.getInstance();
        TableDisplayHandler handler = new TableDisplayHandler( ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        String fmt = handler.formatDate( cal );
        assertNotNull( fmt );

        assertTrue( fmt.length() > 8 );
    }

    @Test
    @Ignore
    @Deprecated
    public void testLoadRuleAsset() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testLoadRuleAsset",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadRuleAsset",
                                                  "this is a cat" );

        impl.createNewRule( "testLoadRuleAsset",
                            "description",
                            "testLoadRuleAsset",
                            "testLoadRuleAsset",
                            AssetFormats.DRL );

        TableDataResult res = repositoryCategoryService.loadRuleListForCategories( "testLoadRuleAsset",
                                                                                   0,
                                                                                   -1,
                                                                                   ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 1,
                      res.data.length );
        assertEquals( -1,
                      res.total );
        assertTrue( res.currentPosition > 0 );
        assertFalse( res.hasNext );

        TableDataRow row = res.data[0];
        String uuid = row.id;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );
        assertNotNull( asset );

        assertEquals( uuid,
                      asset.uuid );

        assertEquals( "description",
                      asset.metaData.description );

        assertNotNull( asset.content );
        assertTrue( asset.content instanceof RuleContentText );
        assertEquals( "testLoadRuleAsset",
                      asset.metaData.name );
        assertEquals( "testLoadRuleAsset",
                      asset.metaData.title );
        assertEquals( "testLoadRuleAsset",
                      asset.metaData.packageName );
        assertEquals( AssetFormats.DRL,
                      asset.metaData.format );
        assertNotNull( asset.metaData.createdDate );

        assertEquals( 1,
                      asset.metaData.categories.length );
        assertEquals( "testLoadRuleAsset",
                      asset.metaData.categories[0] );

        AssetItem rule = impl.getRulesRepository().loadPackage( "testLoadRuleAsset" ).loadAsset( "testLoadRuleAsset" );
        impl.getRulesRepository().createState( "whee" );
        rule.updateState( "whee" );
        rule.checkin( "changed state" );
        asset = repositoryAssetService.loadRuleAsset( uuid );

        assertEquals( "whee",
                      asset.metaData.status );
        assertEquals( "changed state",
                      asset.metaData.checkinComment );

        uuid = impl.createNewRule( "testBRLFormatSugComp",
                                   "description",
                                   "testLoadRuleAsset",
                                   "testLoadRuleAsset",
                                   AssetFormats.BUSINESS_RULE );
        asset = repositoryAssetService.loadRuleAsset( uuid );
        assertTrue( asset.content instanceof RuleModel );

        uuid = impl.createNewRule( "testLoadRuleAssetBRL",
                                   "description",
                                   "testLoadRuleAsset",
                                   "testLoadRuleAsset",
                                   AssetFormats.DSL_TEMPLATE_RULE );
        asset = repositoryAssetService.loadRuleAsset( uuid );
        assertTrue( asset.content instanceof RuleContentText );
    }

    @Test
    @Ignore
    @Deprecated
    public void testListAssets() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        PackageItem pacakgeItem = impl.getRulesRepository().createPackage( "testListAssetsPackage",
                                                                           "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testListAssetsCat",
                                                  "this is a cat" );

        impl.createNewRule( "testLoadArchivedAssets1",
                                           "description",
                                           "testListAssetsCat",
                                           "testListAssetsPackage",
                                           AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testLoadArchivedAssets2",
                                           "description",
                                           "testListAssetsCat",
                                           "testListAssetsPackage",
                                           AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testLoadArchivedAssets3",
                                           "description",
                                           "testListAssetsCat",
                                           "testListAssetsPackage",
                                           AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testLoadArchivedAssets4",
                                           "description",
                                           "testListAssetsCat",
                                           "testListAssetsPackage",
                                           AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testLoadArchivedAssets5",
                                           "description",
                                           "testListAssetsCat",
                                           "testListAssetsPackage",
                                           AssetFormats.BUSINESS_RULE );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pacakgeItem.getUUID(),
                                                                 new String[]{AssetFormats.BUSINESS_RULE},
                                                                 0,
                                                                 2,
                                                                 ExplorerNodeConfig.PACKAGEVIEW_LIST_TABLE_ID );

        assertEquals( 2,
                      res.data.length );
        assertTrue( 5 == res.total );
        assertTrue( res.currentPosition == 2 );
        assertTrue( res.hasNext );

        res = repositoryAssetService.listAssets( pacakgeItem.getUUID(),
                                                 new String[]{AssetFormats.BUSINESS_RULE},
                                                 2,
                                                 2,
                                                 ExplorerNodeConfig.PACKAGEVIEW_LIST_TABLE_ID );
        assertEquals( 2,
                      res.data.length );
        assertTrue( 5 == res.total );
        assertTrue( res.currentPosition == 4 );
        assertTrue( res.hasNext );
    }

    @Test
    @Ignore
    public void testLoadArchivedAssets() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testLoadArchivedAssetsPackage",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadArchivedAssetsCat",
                                                  "this is a cat" );

        String uuid1 = impl.createNewRule( "testLoadArchivedAssets1",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( uuid1 );

        String uuid2 = impl.createNewRule( "testLoadArchivedAssets2",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid2 );

        String uuid3 = impl.createNewRule( "testLoadArchivedAssets3",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid3 );

        String uuid4 = impl.createNewRule( "testLoadArchivedAssets4",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid4 );

        String uuid5 = impl.createNewRule( "testLoadArchivedAssets5",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid5 );

        //We do not know how many archived assets we have in the test repo,
        //but definitely more than 5 (as we just created 5)
        TableDataResult res = repositoryAssetService.loadArchivedAssets( 0,
                                                                         2 );

        assertEquals( 2,
                      res.data.length );
        //may return -1 as per JCR2.0 when precise count is not available due to performance reasons. 
        //assertTrue(-1 != res.total);
        assertTrue( res.currentPosition == 2 );
        assertTrue( res.hasNext );

        res = repositoryAssetService.loadArchivedAssets( 2,
                                                         2 );

        assertEquals( 2,
                      res.data.length );
        //assertTrue(-1 != res.total);
        assertEquals( res.currentPosition,
                      4 );
        assertTrue( res.hasNext );
    }

    @Test
    @Ignore
    public void testTrackRecentOpenedChanged() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        UserInbox ib = new UserInbox( impl.getRulesRepository() );
        ib.clearAll();
        impl.getRulesRepository().createPackage( "testTrackRecentOpenedChanged",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testTrackRecentOpenedChanged",
                                                  "this is a cat" );

        String id = impl.createNewRule( "myrule",
                                        "desc",
                                        "testTrackRecentOpenedChanged",
                                        "testTrackRecentOpenedChanged",
                                        "drl" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset ass = repositoryAssetService.loadRuleAsset( id );

        impl.checkinVersion( ass );

        List<InboxEntry> es = ib.loadRecentEdited();
        assertEquals( 1,
                      es.size() );
        assertEquals( ass.uuid,
                      es.get( 0 ).assetUUID );
        assertEquals( ass.metaData.name,
                      es.get( 0 ).note );

        ib.clearAll();

        repositoryAssetService.loadRuleAsset( ass.uuid );
        es = ib.loadRecentEdited();
        assertEquals( 0,
                      es.size() );

        //now check they have it in their opened list...
        es = ib.loadRecentOpened();
        assertEquals( 1,
                      es.size() );
        assertEquals( ass.uuid,
                      es.get( 0 ).assetUUID );
        assertEquals( ass.metaData.name,
                      es.get( 0 ).note );

        assertEquals( 0,
                      ib.loadRecentEdited().size() );
    }

    @Test
    @Ignore
    public void testLoadAssetHistoryAndRestore() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        long startTime = System.currentTimeMillis();
        impl.getRulesRepository().createPackage( "testLoadAssetHistory",
                                                 "desc" );
        long nowTime1 = System.currentTimeMillis();
        System.out.println( "CreatePackage: " + (nowTime1 - startTime) );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadAssetHistory",
                                                  "this is a cat" );

        long nowTime2 = System.currentTimeMillis();
        System.out.println( "CreateCategory: " + (nowTime2 - nowTime1) );
        String uuid = impl.createNewRule( "testLoadAssetHistory",
                                          "description",
                                          "testLoadAssetHistory",
                                          "testLoadAssetHistory",
                                          AssetFormats.DRL );
        long nowTime3 = System.currentTimeMillis();
        System.out.println( "CreateNewRule: " + (nowTime3 - nowTime2) );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );
        impl.checkinVersion( asset ); // 1
        long nowTime4 = System.currentTimeMillis();
        System.out.println( "Checkin 1: " + (nowTime4 - nowTime3) );
        asset = repositoryAssetService.loadRuleAsset( uuid );
        long nowTime5 = System.currentTimeMillis();
        System.out.println( "load ruleasset: " + (nowTime5 - nowTime4) );
        impl.checkinVersion( asset ); // 2
        long nowTime6 = System.currentTimeMillis();
        System.out.println( "Checkin 2: " + (nowTime6 - nowTime5) );
        asset = repositoryAssetService.loadRuleAsset( uuid );
        impl.checkinVersion( asset ); // HEAD

        TableDataResult result = repositoryAssetService.loadItemHistory( uuid );
        assertNotNull( result );
        TableDataRow[] rows = result.data;
        assertEquals( 2,
                      rows.length );
        assertFalse( rows[0].id.equals( uuid ) );
        assertFalse( rows[1].id.equals( uuid ) );

        RuleAsset old = repositoryAssetService.loadRuleAsset( rows[0].id );
        RuleAsset newer = repositoryAssetService.loadRuleAsset( rows[1].id );
        assertFalse( old.metaData.versionNumber == newer.metaData.versionNumber );

        RuleAsset head = repositoryAssetService.loadRuleAsset( uuid );

        long oldVersion = old.metaData.versionNumber;
        assertFalse( oldVersion == head.metaData.versionNumber );

        impl.restoreVersion( old.uuid,
                             head.uuid,
                             "this was cause of a mistake" );

        RuleAsset newHead = repositoryAssetService.loadRuleAsset( uuid );

        assertEquals( "this was cause of a mistake",
                      newHead.metaData.checkinComment );

    }

    @Test
    @Ignore
    public void testCheckin() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        UserInbox ib = new UserInbox( serv.getRulesRepository() );
        List<InboxEntry> inbox = ib.loadRecentEdited();

        repositoryPackageService.listPackages();

        repositoryCategoryService.createCategory( "/",
                                                  "testCheckinCategory",
                                                  "this is a description" );
        repositoryCategoryService.createCategory( "/",
                                                  "testCheckinCategory2",
                                                  "this is a description" );
        repositoryCategoryService.createCategory( "testCheckinCategory",
                                                  "deeper",
                                                  "description" );

        String uuid = serv.createNewRule( "testChecking",
                                          "this is a description",
                                          "testCheckinCategory",
                                          RulesRepository.DEFAULT_PACKAGE,
                                          AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );

        assertNotNull( asset.metaData.lastModifiedDate );

        asset.metaData.coverage = "boo";
        asset.content = new RuleContentText();
        ((RuleContentText) asset.content).content = "yeah !";

        Date start = new Date();
        Thread.sleep( 100 );

        String uuid2 = serv.checkinVersion( asset );
        assertEquals( uuid,
                      uuid2 );

        assertTrue( ib.loadRecentEdited().size() > inbox.size() );

        RuleAsset asset2 = repositoryAssetService.loadRuleAsset( uuid );
        assertNotNull( asset2.metaData.lastModifiedDate );
        assertTrue( asset2.metaData.lastModifiedDate.after( start ) );

        assertEquals( "boo",
                      asset2.metaData.coverage );
        assertEquals( 1,
                      asset2.metaData.versionNumber );

        assertEquals( "yeah !",
                      ((RuleContentText) asset2.content).content );

        asset2.metaData.coverage = "ya";
        asset2.metaData.checkinComment = "checked in";

        String cat = asset2.metaData.categories[0];
        asset2.metaData.categories = new String[3];
        asset2.metaData.categories[0] = cat;
        asset2.metaData.categories[1] = "testCheckinCategory2";
        asset2.metaData.categories[2] = "testCheckinCategory/deeper";

        serv.checkinVersion( asset2 );

        asset2 = repositoryAssetService.loadRuleAsset( uuid );
        assertEquals( "ya",
                      asset2.metaData.coverage );
        assertEquals( 2,
                      asset2.metaData.versionNumber );
        assertEquals( "checked in",
                      asset2.metaData.checkinComment );
        assertEquals( 3,
                      asset2.metaData.categories.length );
        assertEquals( "testCheckinCategory",
                      asset2.metaData.categories[0] );
        assertEquals( "testCheckinCategory2",
                      asset2.metaData.categories[1] );
        assertEquals( "testCheckinCategory/deeper",
                      asset2.metaData.categories[2] );

        // now lets try a concurrent edit of an asset.
        // asset3 will be loaded and edited, and then asset2 will try to
        // clobber, it, which should fail.
        // as it is optimistically locked.
        RuleAsset asset3 = repositoryAssetService.loadRuleAsset( asset2.uuid );
        asset3.metaData.subject = "new sub";
        serv.checkinVersion( asset3 );

        asset3 = repositoryAssetService.loadRuleAsset( asset2.uuid );
        assertFalse( asset3.metaData.versionNumber == asset2.metaData.versionNumber );

        String result = serv.checkinVersion( asset2 );
        assertTrue( result.startsWith( "ERR" ) );
        System.err.println( result.substring( 5 ) );

    }

    @Test
    @Ignore
    public void testArchivePackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        PackageConfigData[] pkgs = repositoryPackageService.listPackages();

        PackageConfigData[] arch = repositoryPackageService.listArchivedPackages();

        @SuppressWarnings("unused")
        String uuid = repositoryPackageService.createPackage( "testCreateArchivedPackage",
                                                              "this is a new package" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        PackageItem item = impl.getRulesRepository().loadPackage( "testCreateArchivedPackage" );
        TableDataResult td = repositoryAssetService.loadArchivedAssets( 0,
                                                                        1000 );

        item.archiveItem( true );

        TableDataResult td2 = repositoryAssetService.loadArchivedAssets( 0,
                                                                         1000 );
        assertEquals( td2.data.length,
                      td.data.length );

        PackageConfigData[] arch2 = repositoryPackageService.listArchivedPackages();
        assertEquals( arch2.length,
                      arch.length + 1 );

        assertEquals( pkgs.length,
                      repositoryPackageService.listPackages().length );

        item.archiveItem( false );
        arch2 = repositoryPackageService.listArchivedPackages();
        assertEquals( arch2.length,
                      arch.length );
    }

    @Test
    @Ignore
    public void testCreatePackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        PackageConfigData[] pkgs = repositoryPackageService.listPackages();
        String uuid = repositoryPackageService.createPackage( "testCreatePackage",
                                                              "this is a new package" );
        assertNotNull( uuid );

        PackageItem item = impl.getRulesRepository().loadPackage( "testCreatePackage" );
        assertNotNull( item );
        assertEquals( "this is a new package",
                      item.getDescription() );

        assertEquals( pkgs.length + 1,
                      repositoryPackageService.listPackages().length );

        PackageConfigData conf = repositoryPackageService.loadPackageConfig( uuid );
        assertEquals( "this is a new package",
                      conf.description );
        assertNotNull( conf.lastModified );

        pkgs = repositoryPackageService.listPackages();

        repositoryPackageService.copyPackage( "testCreatePackage",
                                              "testCreatePackage_COPY" );

        assertEquals( pkgs.length + 1,
                      repositoryPackageService.listPackages().length );
        try {
            repositoryPackageService.copyPackage( "testCreatePackage",
                                                  "testCreatePackage_COPY" );
        } catch ( RulesRepositoryException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    @Test
    @Ignore
    public void testLoadPackageConfig() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        PackageItem it = impl.getRulesRepository().loadDefaultPackage();
        String uuid = it.getUUID();
        it.updateCoverage( "xyz" );
        it.updateExternalURI( "ext" );
        DroolsHeader.updateDroolsHeader( "header",
                                                  it );
        impl.getRulesRepository().save();

        PackageConfigData data = repositoryPackageService.loadPackageConfig( uuid );
        assertNotNull( data );

        assertEquals( RulesRepository.DEFAULT_PACKAGE,
                      data.name );
        assertEquals( "header",
                      data.header );
        assertEquals( "ext",
                      data.externalURI );

        assertNotNull( data.uuid );
        assertFalse( data.isSnapshot );

        assertNotNull( data.dateCreated );
        Date original = data.lastModified;

        Thread.sleep( 100 );

        repositoryPackageService.createPackageSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                                        "TEST SNAP 2.0",
                                                        false,
                                                        "ya" );
        PackageItem loaded = impl.getRulesRepository().loadPackageSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                                                            "TEST SNAP 2.0" );

        data = repositoryPackageService.loadPackageConfig( loaded.getUUID() );
        assertTrue( data.isSnapshot );
        assertEquals( "TEST SNAP 2.0",
                      data.snapshotName );
        assertFalse( original.equals( data.lastModified ) );
        assertEquals( "ya",
                      data.checkinComment );
    }

    @Test
    @Ignore
    public void testArchiveAndUnarchivePackageAndHeader() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String uuid = repositoryPackageService.createPackage( "testArchiveAndUnarchivePackageAndHeader",
                                                              "a desc" );
        PackageConfigData data = repositoryPackageService.loadPackageConfig( uuid );
        PackageItem it = impl.getRulesRepository().loadPackageByUUID( uuid );
        data.archived = true;

        AssetItem rule1 = it.addAsset( "rule_1",
                                       "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.archiveItem( true );
        rule1.checkin( "" );
        impl.getRulesRepository().save();

        repositoryPackageService.savePackage( data );
        data = repositoryPackageService.loadPackageConfig( uuid );
        it = impl.getRulesRepository().loadPackage( data.name );
        assertTrue( data.archived );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.archived = false;

        repositoryPackageService.savePackage( data );
        data = repositoryPackageService.loadPackageConfig( uuid );
        it = impl.getRulesRepository().loadPackage( data.name );
        assertFalse( data.archived );
        assertFalse( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.archived = true;

        repositoryPackageService.savePackage( data );
        data = repositoryPackageService.loadPackageConfig( uuid );
        it = impl.getRulesRepository().loadPackage( data.name );
        assertTrue( data.archived );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

    }

    @Test
    @Ignore
    public void testPackageConfSave() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String uuid = repositoryPackageService.createPackage( "testPackageConfSave",
                                                              "a desc" );
        PackageConfigData data = repositoryPackageService.loadPackageConfig( uuid );

        data.description = "new desc";
        data.header = "wa";
        data.externalURI = "new URI";

        ValidatedResponse res = repositoryPackageService.validatePackageConfiguration( data );
        assertNotNull( res );
        assertTrue( res.hasErrors );
        assertNotNull( res.errorMessage );

        data = repositoryPackageService.loadPackageConfig( uuid );
        assertEquals( "new desc",
                      data.description );
        assertEquals( "wa",
                      data.header );
        assertEquals( "new URI",
                      data.externalURI );

        data.header = "";
        res = repositoryPackageService.validatePackageConfiguration( data );
        if ( res.hasErrors ) {
            System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
            System.out.println( res.errorMessage );
            System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

        }

        assertFalse( res.hasErrors );
    }

    @Test
    @Ignore
    @Deprecated
    public void testListByFormat() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String cat = "testListByFormat";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "ya" );
        String pkgUUID = repositoryPackageService.createPackage( "testListByFormat",
                                                                 "used for listing by format." );

        String uuid = impl.createNewRule( "testListByFormat",
                                          "x",
                                          cat,
                                          "testListByFormat",
                                          "testListByFormat" );
        @SuppressWarnings("unused")
        String uuid2 = impl.createNewRule( "testListByFormat2",
                                           "x",
                                           cat,
                                           "testListByFormat",
                                           "testListByFormat" );
        String uuid3 = impl.createNewRule( "testListByFormat3",
                                           "x",
                                           cat,
                                           "testListByFormat",
                                           "testListByFormat" );
        @SuppressWarnings("unused")
        String uuid4 = impl.createNewRule( "testListByFormat4",
                                           "x",
                                           cat,
                                           "testListByFormat",
                                           "testListByFormat" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pkgUUID,
                                                                 arr( "testListByFormat" ),
                                                                 0,
                                                                 -1,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );
        assertEquals( uuid,
                      res.data[0].id );
        assertEquals( "testListByFormat",
                      res.data[0].values[0] );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testListByFormat" ),
                                                 0,
                                                 4,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testListByFormat" ),
                                                 0,
                                                 2,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 2,
                      res.data.length );
        assertEquals( uuid,
                      res.data[0].id );
        assertEquals( 4,
                      res.total );
        assertTrue( res.hasNext );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testListByFormat" ),
                                                 2,
                                                 2,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 2,
                      res.data.length );
        assertEquals( uuid3,
                      res.data[0].id );
        assertEquals( 4,
                      res.total );
        assertFalse( res.hasNext );

        uuid = impl.createNewRule( "testListByFormat5",
                                   "x",
                                   cat,
                                   "testListByFormat",
                                   "otherFormat" );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "otherFormat" ),
                                                 0,
                                                 40,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 1,
                      res.data.length );
        assertEquals( uuid,
                      res.data[0].id );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 new String[]{"otherFormat", "testListByFormat"},
                                                 0,
                                                 40,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 5,
                      res.data.length );

        TableDataResult result = repositoryAssetService.quickFindAsset( "testListByForma",
                                                                        false,
                                                                        0,
                                                                        5 );
        assertEquals( 5,
                      result.data.length );

        assertNotNull( result.data[0].id );
        assertTrue( result.data[0].values[0].startsWith( "testListByFormat" ) );

        result = repositoryAssetService.quickFindAsset( "testListByForma",
                                                        false,
                                                        0,
                                                        4 );
        assertEquals( 4,
                      result.data.length );
    }

    @Test
    @Ignore
    @Deprecated
    public void testListUnregisteredAssetFormats() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testListUnregisteredAssetFormats",
                                                                   "" );
        AssetItem as = pkg.addAsset( "whee",
                                     "" );
        as.updateFormat( AssetFormats.DRL );
        as.checkin( "" );

        as = pkg.addAsset( "whee2",
                           "" );
        as.updateFormat( "something_silly" );
        as.checkin( "" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pkg.getUUID(),
                                                                 new String[0],
                                                                 0,
                                                                 40,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 1,
                      res.data.length );
    }

    @Test
    @Ignore
    @Deprecated
    public void testQuickFind() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String cat = "testQuickFind";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQuickFind",
                                                "for testing quick find." );
        String uuid = impl.createNewRule( "testQuickFindmyRule1",
                                          "desc",
                                          cat,
                                          "testQuickFind",
                                          AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.quickFindAsset( "testQuickFindmyRule",
                                                                     false,
                                                                     0,
                                                                     20 );
        assertEquals( 1,
                      res.data.length );

        impl.createNewRule( "testQuickFindmyRule2",
                            "desc",
                            cat,
                            "testQuickFind",
                            AssetFormats.DRL );
        res = repositoryAssetService.quickFindAsset( "testQuickFindmyRule",
                                                     false,
                                                     0,
                                                     20 );
        assertEquals( 2,
                      res.data.length );

        repositoryAssetService.copyAsset( uuid,
                                          "testQuickFind",
                                          "testQuickFindmyRule3" );
        res = repositoryAssetService.quickFindAsset( "testQuickFindmyRule",
                                                     false,
                                                     0,
                                                     20 );
        assertEquals( 3,
                      res.data.length );

        res = repositoryAssetService.quickFindAsset( "testQuickFindm*Rule",
                                                     false,
                                                     0,
                                                     20 );
        assertEquals( 3,
                      res.data.length );

    }

    @Test
    @Ignore
    @Deprecated
    public void testSearchText() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );
        @SuppressWarnings("unused")
        String uuid = impl.createNewRule( "testTextRule1",
                                          "desc",
                                          cat,
                                          "testTextSearch",
                                          AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.queryFullText( "testTextRule1",
                                                                    false,
                                                                    0,
                                                                    -1 );
        assertEquals( 1,
                      res.data.length );
    }

    @Test
    @Ignore
    @Deprecated
    public void testSearchMetaData() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testMetaDataSearch",
                                                                   "" );

        AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset",
                                        "" );
        asset.updateSubject( "testMetaDataSearch" );
        asset.updateExternalSource( "numberwang" );
        asset.checkin( "" );

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "wang, testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "numberwan*";
        TableDataResult res = impl.queryMetaData( qr,
                                                  DateUtils.parseDate( "10-Jul-1974",
                                                                       new DateFormatsImpl() ),
                                                  null,
                                                  null,
                                                  null,
                                                  false,
                                                  0,
                                                  -1 );
        assertEquals( 1,
                      res.data.length );

    }

    public String[] arr(String s) {
        return new String[]{s};
    }

    @Test
    @Ignore
    public void testStatus() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String uuid = impl.createState( "testStatus1" );
        assertNotNull( uuid );

        String[] states = impl.listStates();
        assertTrue( states.length > 0 );

        impl.createState( "testStatus2" );
        String[] states2 = impl.listStates();
        assertEquals( states.length + 1,
                      states2.length );

        int match = 0;
        for ( int i = 0; i < states2.length; i++ ) {
            if ( states2[i].equals( "testStatus2" ) ) {
                match++;
            } else if ( states2[i].equals( "testStatus1" ) ) {
                match++;
            }
        }

        assertEquals( 2,
                      match );

        String packagUUID = repositoryPackageService.createPackage( "testStatus",
                                                                    "description" );
        String ruleUUID = impl.createNewRule( "testStatus",
                                              "desc",
                                              null,
                                              "testStatus",
                                              AssetFormats.DRL );
        String ruleUUID2 = impl.createNewRule( "testStatus2",
                                               "desc",
                                               null,
                                               "testStatus",
                                               AssetFormats.DRL );
        impl.createState( "testState" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( ruleUUID );
        assertEquals( StateItem.DRAFT_STATE_NAME,
                      asset.metaData.status );
        repositoryAssetService.changeState( ruleUUID,
                          "testState",
                          false );
        asset = repositoryAssetService.loadRuleAsset( ruleUUID );
        assertEquals( "testState",
                      asset.metaData.status );
        asset = repositoryAssetService.loadRuleAsset( ruleUUID2 );
        assertEquals( StateItem.DRAFT_STATE_NAME,
                      asset.metaData.status );

        impl.createState( "testState2" );
        repositoryAssetService.changeState( packagUUID,
                          "testState2",
                          true );

        PackageConfigData pkg = repositoryPackageService.loadPackageConfig( packagUUID );
        assertEquals( "testState2",
                      pkg.state );

        asset = repositoryAssetService.loadRuleAsset( ruleUUID2 );
        assertEquals( "testState2",
                      asset.metaData.status );

        impl.checkinVersion( asset );
        asset = repositoryAssetService.loadRuleAsset( asset.uuid );
        assertEquals( "testState2",
                      asset.metaData.status );

    }

    @Test
    @Ignore
    public void testMovePackage() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        String[] cats = repositoryCategoryService.loadChildCategories( "/" );
        if ( cats.length == 0 ) {
            repositoryCategoryService.createCategory( "/",
                                                      "la",
                                                      "d" );
        }
        String sourcePkgId = repositoryPackageService.createPackage( "sourcePackage",
                                                                     "description" );
        String destPkgId = repositoryPackageService.createPackage( "targetPackage",
                                                                   "description" );

        String cat = repositoryCategoryService.loadChildCategories( "/" )[0];

        String uuid = impl.createNewRule( "testMovePackage",
                                          "desc",
                                          cat,
                                          "sourcePackage",
                                          AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
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
    @Ignore
    public void testCopyAsset() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        repositoryCategoryService.createCategory( "/",
                                                  "templates",
                                                  "ya" );
        String uuid = impl.createNewRule( "testCopyAsset",
                                          "",
                                          "templates",
                                          RulesRepository.DEFAULT_PACKAGE,
                                          AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        String uuid2 = repositoryAssetService.copyAsset( uuid,
                                                         RulesRepository.DEFAULT_PACKAGE,
                                                         "testCopyAsset2" );
        assertNotSame( uuid,
                       uuid2 );

        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid2 );
        assertNotNull( asset );
        assertEquals( RulesRepository.DEFAULT_PACKAGE,
                      asset.metaData.packageName );
        assertEquals( "testCopyAsset2",
                      asset.metaData.name );
    }

    @Test
    @Ignore
    public void testSnapshot() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotTesting",
                                                  "y" );
        repositoryPackageService.createPackage( "testSnapshot",
                                                "d" );
        @SuppressWarnings("unused")
        String uuid = impl.createNewRule( "testSnapshotRule",
                                          "",
                                          "snapshotTesting",
                                          "testSnapshot",
                                          AssetFormats.DRL );

        repositoryPackageService.createPackageSnapshot( "testSnapshot",
                                                        "X",
                                                        false,
                                                        "ya" );
        SnapshotInfo[] snaps = repositoryPackageService.listSnapshots( "testSnapshot" );
        assertEquals( 1,
                      snaps.length );
        assertEquals( "X",
                      snaps[0].name );
        assertEquals( "ya",
                      snaps[0].comment );
        assertNotNull( snaps[0].uuid );
        PackageConfigData confSnap = repositoryPackageService.loadPackageConfig( snaps[0].uuid );
        assertEquals( "testSnapshot",
                      confSnap.name );

        repositoryPackageService.createPackageSnapshot( "testSnapshot",
                                                        "Y",
                                                        false,
                                                        "we" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "testSnapshot" ).length );
        repositoryPackageService.createPackageSnapshot( "testSnapshot",
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
    @Ignore
    public void testSnapshotRebuild() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        // get rid of other snapshot crap
        Iterator< ? > pkit = repo.listPackages();
        while ( pkit.hasNext() ) {
            PackageItem pkg = (PackageItem) pkit.next();
            String[] snaps = repo.listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                repo.removePackageSnapshot( pkg.getName(),
                                            snapName );
            }
        }

        final PackageItem pkg = repo.createPackage( "testSnapshotRebuild",
                                                    "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                                  pkg );
        repo.save();

        AssetItem item = pkg.addAsset( "anAsset",
                                       "" );
        item.updateFormat( AssetFormats.DRL );
        item.updateContent( " rule abc \n when \n then \n System.out.println(42); \n end" );
        item.checkin( "" );

        BuilderResult res = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                   true );
        assertNull( res );

        repositoryPackageService.createPackageSnapshot( "testSnapshotRebuild",
                                                        "SNAP",
                                                        false,
                                                        "" );

        PackageItem snap = repo.loadPackageSnapshot( "testSnapshotRebuild",
                                                     "SNAP" );
        long snapTime = snap.getLastModified().getTimeInMillis();

        Thread.sleep( 100 );

        repositoryPackageService.rebuildSnapshots();

        PackageItem snap_ = repo.loadPackageSnapshot( "testSnapshotRebuild",
                                                      "SNAP" );
        long newTime = snap_.getLastModified().getTimeInMillis();

        assertTrue( newTime > snapTime );

        item.updateContent( "garbage" );
        item.checkin( "" );

        repositoryPackageService.createPackageSnapshot( "testSnapshotRebuild",
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
    @Ignore
    public void testPackageRebuild() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        RulesRepository repo = impl.getRulesRepository();

        final PackageItem pkg = repo.createPackage( "testPackageRebuild",
                                                    "" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                                  pkg );
        repo.save();

        AssetItem item = pkg.addAsset( "anAsset",
                                       "" );
        item.updateFormat( AssetFormats.DRL );
        item.updateContent( " rule abc \n when \n then \n System.out.println(42); \n end" );
        item.checkin( "" );

        assertNull( pkg.getCompiledPackageBytes() );

        long last = pkg.getLastModified().getTimeInMillis();
        Thread.sleep( 100 );
        try {
            repositoryPackageService.rebuildPackages();
        } catch ( DetailedSerializationException e ) {
            assertNotNull( e.getMessage() );
            assertNotNull( e.getLongDescription() );
        }

        assertFalse( pkg.getLastModified().getTimeInMillis() == last );
        assertNotNull( pkg.getCompiledPackageBytes() );

    }

    @Test
    @Ignore
    public void testRemoveCategory() throws Exception {

        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String[] children = repositoryCategoryService.loadChildCategories( "/" );
        repositoryCategoryService.createCategory( "/",
                                                  "testRemoveCategory",
                                                  "foo" );

        repositoryCategoryService.removeCategory( "testRemoveCategory" );
        String[] _children = repositoryCategoryService.loadChildCategories( "/" );
        assertEquals( children.length,
                      _children.length );

    }

    @Test
    @Ignore
    public void testRemoveAsset() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testRemoveAsset";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "ya" );
        String pkgUUID = repositoryPackageService.createPackage( "testRemoveAsset",
                                                                 "" );
        @SuppressWarnings("unused")
        String uuid = impl.createNewRule( "testRemoveAsset",
                                          "x",
                                          cat,
                                          "testRemoveAsset",
                                          "testRemoveAsset" );
        @SuppressWarnings("unused")
        String uuid2 = impl.createNewRule( "testRemoveAsset2",
                                           "x",
                                           cat,
                                           "testRemoveAsset",
                                           "testRemoveAsset" );
        @SuppressWarnings("unused")
        String uuid3 = impl.createNewRule( "testRemoveAsset3",
                                           "x",
                                           cat,
                                           "testRemoveAsset",
                                           "testRemoveAsset" );
        String uuid4 = impl.createNewRule( "testRemoveAsset4",
                                           "x",
                                           cat,
                                           "testRemoveAsset",
                                           "testRemoveAsset" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pkgUUID,
                                                                 arr( "testRemoveAsset" ),
                                                                 0,
                                                                 -1,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );

        repositoryAssetService.removeAsset( uuid4 );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testRemoveAsset" ),
                                                 0,
                                                 -1,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 3,
                      res.data.length );
    }

    @Test
    @Ignore
    public void testRemovePackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        int n = repositoryPackageService.listPackages().length;
        PackageItem p = impl.getRulesRepository().createPackage( "testRemovePackage",
                                                                 "" );
        assertNotNull( repositoryPackageService.loadPackageConfig( p.getUUID() ) );

        repositoryPackageService.removePackage( p.getUUID() );
        assertEquals( n,
                      repositoryPackageService.listPackages().length );
    }

    @Test
    @Ignore
    public void testImportPackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        try {
            PackageItem item = impl.getRulesRepository().loadPackage( "testExportPackage" );
            fail();
            assertNull( item );
        } catch ( Exception e ) {
            // expected
        }

        //impl.createCategory( "/", "testExportPackageCat1", "desc" );
        //impl.createCategory( "/", "testExportPackageCat2", "desc" );

        File file = new File( "d:\\testExportPackage.xml" );

        FileInputStream fis = new FileInputStream( file );
        byte[] byteArray = new byte[fis.available()];
        fis.read( byteArray );

        repositoryPackageService.importPackages( byteArray,
                                                 true );

        PackageItem item = impl.getRulesRepository().loadPackage( "testExportPackage" );
        assertNotNull( item );
        assertEquals( "desc",
                      item.getDescription() );
    }

    @Ignore
    @Test
    public void testExportPackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        int n = repositoryPackageService.listPackages().length;
        repositoryCategoryService.createCategory( "/",
                                                  "testExportPackageCat1",
                                                  "desc" );
        repositoryCategoryService.createCategory( "/",
                                                  "testExportPackageCat2",
                                                  "desc" );
        PackageItem p = impl.getRulesRepository().createPackage( "testExportPackage",
                                                                 "" );

        String uuid1 = impl.createNewRule( "testExportPackageAsset1",
                                           "desc",
                                           "testExportPackageCat1",
                                           "testExportPackage",
                                           "dsl" );

        String uuid2 = impl.createNewRule( "testExportPackageAsset2",
                                           "desc",
                                           "testExportPackageCat2",
                                           "testExportPackage",
                                           "dsl" );

        byte[] exportedPackage = repositoryPackageService.exportPackages( "testExportPackage" );

        assertNotNull( exportedPackage );

        File file = new File( "d:\\testExportPackage.xml" );

        FileOutputStream fos = new FileOutputStream( file );

        fos.write( exportedPackage );
        fos.close();
    }

    @Test
    @Ignore
    public void testArchiveAsset() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testArchiveAsset";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "ya" );
        String pkgUUID = repositoryPackageService.createPackage( "testArchiveAsset",
                                                                 "" );
        @SuppressWarnings("unused")
        String uuid = impl.createNewRule( "testArchiveAsset",
                                          "x",
                                          cat,
                                          "testArchiveAsset",
                                          "testArchiveAsset" );
        @SuppressWarnings("unused")
        String uuid2 = impl.createNewRule( "testArchiveAsset2",
                                           "x",
                                           cat,
                                           "testArchiveAsset",
                                           "testArchiveAsset" );
        @SuppressWarnings("unused")
        String uuid3 = impl.createNewRule( "testArchiveAsset3",
                                           "x",
                                           cat,
                                           "testArchiveAsset",
                                           "testArchiveAsset" );
        String uuid4 = impl.createNewRule( "testArchiveAsset4",
                                           "x",
                                           cat,
                                           "testArchiveAsset",
                                           "testArchiveAsset" );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pkgUUID,
                                                                 arr( "testArchiveAsset" ),
                                                                 0,
                                                                 -1,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );
        assertEquals( 4,
                      res.total );
        assertFalse( res.hasNext );

        TableDataResult td = repositoryAssetService.loadArchivedAssets( 0,
                                                                        1000 );
        assertEquals( -1,
                      td.total );
        repositoryAssetService.archiveAsset( uuid4 );

        TableDataResult td2 = repositoryAssetService.loadArchivedAssets( 0,
                                                                         1000 );
        assertTrue( td2.data.length == td.data.length + 1 );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testArchiveAsset" ),
                                                 0,
                                                 -1,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 3,
                      res.data.length );

        repositoryAssetService.unArchiveAsset( uuid4 );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( "testArchiveAsset" ),
                                                 0,
                                                 -1,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );

    }

    @Test
    @Ignore
    public void testArchiveAssetWhenParentPackageArchived() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String packageName = "testArchiveAssetWhenParentPackageArchived";
        String cat = packageName;
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "ya" );
        String pkgUUID = repositoryPackageService.createPackage( packageName,
                                                                 "" );
        @SuppressWarnings("unused")
        String uuid = impl.createNewRule( packageName,
                                          "x",
                                          cat,
                                          packageName,
                                          packageName );
        @SuppressWarnings("unused")
        String uuid2 = impl.createNewRule( "testArchiveAssetWhenParentPackageArchived2",
                                           "x",
                                           cat,
                                           packageName,
                                           packageName );
        @SuppressWarnings("unused")
        String uuid3 = impl.createNewRule( "testArchiveAssetWhenParentPackageArchived3",
                                           "x",
                                           cat,
                                           packageName,
                                           packageName );
        String uuid4 = impl.createNewRule( "testArchiveAssetWhenParentPackageArchived4",
                                           "x",
                                           cat,
                                           packageName,
                                           packageName );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        TableDataResult res = repositoryAssetService.listAssets( pkgUUID,
                                                                 arr( packageName ),
                                                                 0,
                                                                 -1,
                                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 4,
                      res.data.length );
        assertEquals( 4,
                      res.total );
        assertFalse( res.hasNext );

        TableDataResult td = repositoryAssetService.loadArchivedAssets( 0,
                                                                        1000 );
        assertEquals( -1,
                      td.total );
        repositoryAssetService.archiveAsset( uuid4 );
        PackageItem packageItem = impl.getRulesRepository().loadPackage( packageName );
        packageItem.archiveItem( true );

        TableDataResult td2 = repositoryAssetService.loadArchivedAssets( 0,
                                                                         1000 );
        assertTrue( td2.data.length == td.data.length + 1 );

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( packageName ),
                                                 0,
                                                 -1,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 3,
                      res.data.length );

        try {
            repositoryAssetService.unArchiveAsset( uuid4 );
            fail( "Should throw an exception" );
        } catch ( RulesRepositoryException e ) {
            // Works
        }

        res = repositoryAssetService.listAssets( pkgUUID,
                                                 arr( packageName ),
                                                 0,
                                                 -1,
                                                 ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        assertEquals( 3,
                      res.data.length );

    }

    @Test
    @Ignore
    public void testLoadSuggestionCompletionEngine() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testSILoadSCE",
                                              "" );

        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board",
                                                  pkg );

        AssetItem m2 = pkg.addAsset( "MyModel2",
                                     "" );
        m2.updateFormat( AssetFormats.DRL_MODEL );
        m2.updateContent( "declare Whee\n name: String\nend" );
        m2.checkin( "" );

        AssetItem r1 = pkg.addAsset( "garbage",
                                     "" );
        r1.updateFormat( AssetFormats.DRL );
        r1.updateContent( "this will not compile" );
        r1.checkin( "" );

        SuggestionCompletionEngine eng = impl.loadSuggestionCompletionEngine( pkg.getName() );
        assertNotNull( eng );

        //The loader could define extra imports
        assertTrue( eng.getFactTypes().length >= 2 );
        List<String> factTypes = Arrays.asList( eng.getFactTypes() );

        assertTrue( factTypes.contains( "Board" ) );
        assertTrue( factTypes.contains( "Whee" ) );

    }

    @Test
    @Ignore
    public void testDiscussion() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testDiscussionFeature",
                                              "" );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        List<DiscussionRecord> dr = repositoryAssetService.loadDiscussionForAsset( rule1.getUUID() );
        assertEquals( 0,
                      dr.size() );

        List<DiscussionRecord> dr_ = repositoryAssetService.addToDiscussionForAsset( rule1.getUUID(),
                                                                   "This is a note" );
        assertEquals( 1,
                      dr_.size() );
        assertNotNull( dr_.get( 0 ).author );
        assertEquals( "This is a note",
                      dr_.get( 0 ).note );
        Thread.sleep( 100 );
        repositoryAssetService.addToDiscussionForAsset( rule1.getUUID(),
                                      "This is a note2" );

        List<DiscussionRecord> d_ = repositoryAssetService.loadDiscussionForAsset( rule1.getUUID() );
        assertEquals( 2,
                      d_.size() );

        assertEquals( "This is a note",
                      d_.get( 0 ).note );
        assertEquals( "This is a note2",
                      d_.get( 1 ).note );
        assertTrue( d_.get( 1 ).timestamp > d_.get( 0 ).timestamp );

        rule1.updateContent( "some more content" );
        rule1.checkin( "" );

        repositoryAssetService.addToDiscussionForAsset( rule1.getUUID(),
                                      "This is a note2" );
        d_ = repositoryAssetService.loadDiscussionForAsset( rule1.getUUID() );
        assertEquals( 3,
                      d_.size() );

        assertEquals( "This is a note",
                      d_.get( 0 ).note );
        assertEquals( "This is a note2",
                      d_.get( 1 ).note );

        repositoryAssetService.clearAllDiscussionsForAsset( rule1.getUUID() );
        d_ = repositoryAssetService.loadDiscussionForAsset( rule1.getUUID() );
        assertEquals( 0,
                      d_.size() );

        repositoryAssetService.addToDiscussionForAsset( rule1.getUUID(),
                                      "This is a note2" );
        d_ = repositoryAssetService.loadDiscussionForAsset( rule1.getUUID() );
        assertEquals( 1,
                      d_.size() );
    }

    /**
     * This will test creating a package, check it compiles, and can exectute
     * rules, then take a snapshot, and check that it reports errors.
     */

    @Test
    @Ignore
    public void testBinaryPackageCompileAndExecute() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBinaryPackageCompile",
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
        assertNull( result );

        pkg = repo.loadPackage( "testBinaryPackageCompile" );
        byte[] binPackage = pkg.getCompiledPackageBytes();

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

        repositoryPackageService.createPackageSnapshot( "testBinaryPackageCompile",
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

        pkg = repo.loadPackageSnapshot( "testBinaryPackageCompile",
                                        "SNAP1" );
        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertNull( result );

    }

    /**
     * This will test creating a package with a BRL rule, check it compiles, and
     * can exectute rules, then take a snapshot, and check that it reports
     * errors.
     */

    @Test
    @Ignore
    public void testBinaryPackageCompileAndExecuteWithBRXML() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBinaryPackageCompileBRL",
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
        assertNull( result );

        pkg = repo.loadPackage( "testBinaryPackageCompileBRL" );
        byte[] binPackage = pkg.getCompiledPackageBytes();

        // Here is where we write it out is needed... set to true if needed for
        // the binary test below "testLoadAndExecBinary"
        boolean saveBinPackage = false;
        if ( saveBinPackage ) {
            FileOutputStream out = new FileOutputStream( "RepoBinPackage.pkg" );
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

        repositoryPackageService.createPackageSnapshot( "testBinaryPackageCompileBRL",
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

        pkg = repo.loadPackageSnapshot( "testBinaryPackageCompileBRL",
                                        "SNAP1" );
        result = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                        true );
        assertNull( result );

        // check that the rule name in the model is being set
        AssetItem asset2 = pkg.addAsset( "testSetRuleName",
                                         "" );
        asset2.updateFormat( AssetFormats.BUSINESS_RULE );
        asset2.checkin( "" );

        RuleModel model2 = new RuleModel();
        assertNull( model2.name );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( asset2.getUUID() );
        asset.content = (PortableObject) model2;

        impl.checkinVersion( asset );

        asset = repositoryAssetService.loadRuleAsset( asset2.getUUID() );

        model2 = (RuleModel) asset.content;
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
    @Ignore
    public void testLoadAndExecBinary() throws Exception {
        Person p = new Person( "fubar" );
        BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
        loader.addPackage( this.getClass().getResourceAsStream( "/RepoBinPackage.pkg" ) );
        RuleBase rb = loader.getRuleBase();
        StatelessSession sess = rb.newStatelessSession();
        sess.execute( p );
        assertEquals( 42,
                      p.getAge() );
    }

    @Test
    @Ignore
    public void testSuggestionCompletionLoading() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testSISuggestionCompletionLoading",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "model_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL_MODEL );
        rule1.updateContent( "declare Whee\n name: String \nend" );
        rule1.checkin( "" );
        repo.save();

    }

    @Test
    @Ignore
    public void testPackageSource() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        // create our package
        PackageItem pkg = repo.createPackage( "testPackageSource",
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

        String drl = repositoryPackageService.buildPackageSource( pkg.getUUID() );
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

        drl = repositoryPackageService.buildPackageSource( pkg.getUUID() );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "import org.goo.Ber" ) > -1 );
        assertTrue( drl.indexOf( "This is foo" ) == -1 );
        assertTrue( drl.indexOf( "do something" ) == -1 );
        assertTrue( drl.indexOf( "bar()" ) > 0 );
        assertTrue( drl.indexOf( "yeahMan();" ) > 0 );

    }

    @Test
    @Ignore
    public void testAssetSource() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testAssetSource",
                                              "" );
        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'n' \n when Foo() then bar(); \n end" );
        asset.checkin( "" );
        repo.save();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );
        String drl = repositoryAssetService.buildAssetSource( rule );
        assertEquals( "rule 'n' \n when Foo() then bar(); \n end",
                      drl );

        asset = pkg.addAsset( "DT",
                              "" );
        asset.updateFormat( AssetFormats.DECISION_SPREADSHEET_XLS );
        asset.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/SampleDecisionTable.xls" ) );
        asset.checkin( "" );

        rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );
        drl = repositoryAssetService.buildAssetSource( rule );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "rule" ) > -1 );
        assertTrue( drl.indexOf( "policy: Policy" ) > -1 );

        AssetItem dsl = pkg.addAsset( "MyDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[when]This is foo=bar()\n[then]do something=yeahMan();" );
        dsl.checkin( "" );

        asset = pkg.addAsset( "MyDSLRule",
                              "" );
        asset.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        asset.updateContent( "when \n This is foo \n then \n do something" );
        asset.checkin( "" );

        rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );
        drl = repositoryAssetService.buildAssetSource( rule );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "This is foo" ) == -1 );
        assertTrue( drl.indexOf( "do something" ) == -1 );
        assertTrue( drl.indexOf( "bar()" ) > -1 );
        assertTrue( drl.indexOf( "yeahMan();" ) > -1 );

        rule = repositoryAssetService.loadRuleAsset( repo.copyAsset( asset.getUUID(),
                                                                     "testAssetSource",
                                                                     "newRuleName" ) );
        // System.err.println(((RuleContentText)rule.content).content);
        drl = repositoryAssetService.buildAssetSource( rule );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "newRuleName" ) > 0 );

    }

    @Test
    @Ignore
    public void testBuildAssetWithError() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBuildAssetWithError",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Person",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n when Personx() then System.err.println(42); \n end" );
        asset.checkin( "" );
        repo.save();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );

        BuilderResult result = repositoryAssetService.buildAsset( rule );
        assertNotNull( result );
        assertEquals( -1,
                      result.getLines().get( 0 ).getMessage().indexOf( "Check log for" ) );
        assertTrue( result.getLines().get( 0 ).getMessage().indexOf( "Unable to resolve" ) > -1 );

    }

    @Test
    @Ignore
    public void testBuildAsset() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBuildAsset",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Person",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n when Person() then System.err.println(42); \n end" );
        asset.checkin( "" );
        repo.save();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );

        // check its all OK
        BuilderResult result = repositoryAssetService.buildAsset( rule );
        assertNull( result );

        RuleBaseCache.getInstance().clearCache();

        // try it with a bad rule
        RuleContentText text = new RuleContentText();
        text.content = "rule 'MyBadRule' \n when Personx() then System.err.println(42); \n end";
        rule.content = text;

        result = repositoryAssetService.buildAsset( rule );
        assertNotNull( result );
        assertNotNull( result.getLines().get( 0 ).getMessage() );
        assertEquals( AssetFormats.DRL,
                      result.getLines().get( 0 ).getAssetFormat() );

        // now mix in a DSL
        AssetItem dsl = pkg.addAsset( "MyDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[when]There is a person=Person()\n[then]print out 42=System.err.println(42);" );
        dsl.checkin( "" );

        AssetItem dslRule = pkg.addAsset( "dslRule",
                                          "" );
        dslRule.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        dslRule.updateContent( "when \n There is a person \n then \n print out 42" );
        dslRule.checkin( "" );

        rule = repositoryAssetService.loadRuleAsset( dslRule.getUUID() );

        result = repositoryAssetService.buildAsset( rule );
        assertNull( result );

        asset = pkg.addAsset( "someEnumThing",
                              "" );
        asset.updateFormat( AssetFormats.ENUMERATION );
        asset.updateContent( "goober boy" );
        asset.checkin( "" );
        result = repositoryAssetService.buildAsset( repositoryAssetService.loadRuleAsset( asset.getUUID() ) );
        assertFalse( result.getLines().size() == 0 );

    }

    @Test
    @Ignore
    public void testBuildAssetMultipleFunctionsCallingEachOther() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        repositoryPackageService.createPackage( "testBuildAssetMultipleFunctionsCallingEachOther",
                                                "" );
        repositoryCategoryService.createCategory( "/",
                                                  "funkytest",
                                                  "" );

        String uuidt1 = impl.createNewRule( "t1",
                                            "",
                                            "funkytest",
                                            "testBuildAssetMultipleFunctionsCallingEachOther",
                                            AssetFormats.FUNCTION );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset t1 = repositoryAssetService.loadRuleAsset( uuidt1 );
        RuleContentText t1Content = new RuleContentText();
        t1Content.content = "function void t1(){\n";
        t1Content.content += " t2();\n";
        t1Content.content += "}\n";
        t1.content = t1Content;
        impl.checkinVersion( t1 );

        String uuidt2 = impl.createNewRule( "t2",
                                            "",
                                            "funkytest",
                                            "testBuildAssetMultipleFunctionsCallingEachOther",
                                            AssetFormats.FUNCTION );
        RuleAsset t2 = repositoryAssetService.loadRuleAsset( uuidt2 );
        RuleContentText t2Content = new RuleContentText();
        t2Content.content = "function void t2(){\n";
        t2Content.content += " t1();\n";
        t2Content.content += "}\n";
        t2.content = t2Content;
        impl.checkinVersion( t2 );

        BuilderResult result = repositoryAssetService.buildAsset( t1 );

        assertNull( result );

    }

    @Test
    @Ignore
    public void testBuildAssetBRXMLAndCopy() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBuildAssetBRL",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Person",
                                                  pkg );
        repositoryCategoryService.createCategory( "/",
                                                  "brl",
                                                  "" );

        String uuid = impl.createNewRule( "testBRL",
                                          "",
                                          "brl",
                                          "testBuildAssetBRL",
                                          AssetFormats.BUSINESS_RULE );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset rule = repositoryAssetService.loadRuleAsset( uuid );

        RuleModel m = (RuleModel) rule.content;
        assertNotNull( m );
        m.name = "testBRL";

        FactPattern p = new FactPattern( "Person" );
        p.setBoundName( "p" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "name" );
        con.setValue( "mark" );
        con.setOperator( "==" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );

        p.addConstraint( con );

        m.addLhsItem( p );

        ActionSetField set = new ActionSetField( "p" );
        ActionFieldValue f = new ActionFieldValue( "name",
                                                   "42-ngoo",
                                                   SuggestionCompletionEngine.TYPE_STRING );
        set.addFieldValue( f );

        m.addRhsItem( set );

        impl.checkinVersion( rule );

        // check its all OK
        BuilderResult result = repositoryAssetService.buildAsset( rule );
        if ( result != null ) {
            for ( int i = 0; i < result.getLines().size(); i++ ) {
                System.err.println( result.getLines().get( i ).getMessage() );
            }
        }
        assertNull( result );

        List<AssetItem> assets = iteratorToList( pkg.getAssets() );
        assertEquals( 3,
                      assets.size() );
        // now lets copy...
        String newUUID = repositoryAssetService.copyAsset( rule.uuid,
                                                           rule.metaData.packageName,
                                                           "ruleName2" );

        assets = iteratorToList( pkg.getAssets() );
        assertEquals( 4,
                      assets.size() ); //we have 4 due to the drools.package file.
        RuleAsset asset = repositoryAssetService.loadRuleAsset( newUUID );

        String pkgSource = repositoryPackageService.buildPackageSource( pkg.getUUID() );

        assertTrue( pkgSource.indexOf( "ruleName2" ) > 0 );
        assertTrue( repositoryAssetService.buildAssetSource( asset ).indexOf( "ruleName2" ) > 0 );
        assertTrue( repositoryAssetService.buildAssetSource( asset ).indexOf( "testBRL" ) == -1 );

        // RuleModel model2 = (RuleModel) asset.content;
        // assertEquals("ruleName2", model2.name);

    }

    private List<AssetItem> iteratorToList(Iterator<AssetItem> assets) {
        List<AssetItem> result = new ArrayList<AssetItem>();
        while ( assets.hasNext() ) {
            result.add( assets.next() );

        }
        return result;
    }

    @Test
    @Ignore
    public void testBuildAssetWithPackageConfigError() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testBuildAssetWithPackageConfigError",
                                              "" );
        // AssetItem model = pkg.addAsset( "MyModel", "" );
        // model.updateFormat( AssetFormats.MODEL );
        // model.updateBinaryContentAttachment(
        // this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        // model.checkin( "" );

        // pkg.updateHeader( "import com.billasurf.Person" );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n when \n then \n end" );
        asset.checkin( "" );
        repo.save();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset rule = repositoryAssetService.loadRuleAsset( asset.getUUID() );

        // check its all OK
        BuilderResult result = repositoryAssetService.buildAsset( rule );
        if ( !(result == null) ) {
            System.err.println( result.getLines().get( 0 ).getAssetName() + " " + result.getLines().get( 0 ).getMessage() );
        }
        assertNull( result );

        DroolsHeader.updateDroolsHeader( "importxxxx",
                                                  pkg );
        repo.save();
        result = repositoryAssetService.buildAsset( rule );
        assertNotNull( result );

        assertEquals( 1,
                      result.getLines().size() );
        assertEquals( "package",
                      result.getLines().get( 0 ).getAssetFormat() );
        assertNotNull( result.getLines().get( 0 ).getMessage() );

    }

    @Test
    @Ignore
    public void testRuleNameList() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testRuleNameList",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.goo.Ber",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "package wee.wee \nrule 'rule1' \n  when p:Person() \n then p.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        AssetItem rule2 = pkg.addAsset( "rule_2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "rule 'rule2' \n ruleflow-group 'whee' \nwhen p:Person() \n then p.setAge(42); \n end" );
        rule2.checkin( "" );
        repo.save();

        String[] list = repositoryPackageService.listRulesInPackage( pkg.getName() );
        assertEquals( 2,
                      list.length );
        assertEquals( "rule1",
                      list[0] );
        assertEquals( "rule2",
                      list[1] );

        rule2.updateContent( "wang" );
        rule2.checkin( "" );

        list = repositoryPackageService.listRulesInPackage( pkg.getName() );
        assertEquals( 2,
                      list.length );

    }

    /**
     * This idea of this is to not compile packages more then we have to.
     */

    @Test
    @Ignore
    public void testBinaryUpToDate() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RulesRepository repo = impl.getRulesRepository();

        // create our package
        PackageItem pkg = repo.createPackage( "testBinaryPackageUpToDate",
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
        assertFalse( RuleBaseCache.getInstance().contains( pkg.getUUID() ) );
        RuleBaseCache.getInstance().remove( "XXX" );

        BuilderResult results = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                       true );
        assertNull( results );

        pkg = repo.loadPackage( "testBinaryPackageUpToDate" );
        byte[] binPackage = pkg.getCompiledPackageBytes();

        assertNotNull( binPackage );

        assertTrue( pkg.getNode().getProperty( "drools:binaryUpToDate" ).getBoolean() );
        assertTrue( pkg.isBinaryUpToDate() );
        assertFalse( RuleBaseCache.getInstance().contains( pkg.getUUID() ) );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( rule1.getUUID() );
        impl.checkinVersion( asset );

        assertFalse( pkg.getNode().getProperty( "drools:binaryUpToDate" ).getBoolean() );
        assertFalse( RuleBaseCache.getInstance().contains( pkg.getUUID() ) );

        repositoryPackageService.buildPackage( pkg.getUUID(),
                                               false );

        assertTrue( pkg.getNode().getProperty( "drools:binaryUpToDate" ).getBoolean() );
        assertFalse( RuleBaseCache.getInstance().contains( pkg.getUUID() ) );

        PackageConfigData config = repositoryPackageService.loadPackageConfig( pkg.getUUID() );
        repositoryPackageService.savePackage( config );

        assertFalse( pkg.getNode().getProperty( "drools:binaryUpToDate" ).getBoolean() );
        assertFalse( pkg.isBinaryUpToDate() );
        repositoryPackageService.buildPackage( pkg.getUUID(),
                                               false );
        assertTrue( pkg.getNode().getProperty( "drools:binaryUpToDate" ).getBoolean() );
        assertTrue( pkg.isBinaryUpToDate() );

    }

    @Test
    @Ignore
    public void testRunScenario() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        System.out.println( "create package" );
        PackageItem pkg = repo.createPackage( "testScenarioRun",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person\n global org.drools.Cheese cheese\n",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "michael",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        FactData cheese = new FactData();
        cheese.setName( "cheese" );
        cheese.setType( "Cheese" );
        cheese.getFieldData().add( new FieldData( "price",
                                                  "42" ) );
        sc.getGlobals().add( cheese );

        ScenarioRunResult res = repositoryPackageService.runScenario( pkg.getName(),
                                                                      sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();
        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        //BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
        //assertNull(results);

        rule1.updateContent( "Junk" );
        rule1.checkin( "" );

        RuleBaseCache.getInstance().clearCache();
        pkg.updateBinaryUpToDate( false );
        repo.save();
        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNotNull( res.getErrors() );
        assertNull( res.getScenario() );

        assertTrue( res.getErrors().size() > 0 );

        repositoryCategoryService.createCategory( "/",
                                                  "sc",
                                                  "" );

        String scenarioId = impl.createNewRule( "sc1",
                                                "s",
                                                "sc",
                                                pkg.getName(),
                                                AssetFormats.TEST_SCENARIO );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset asset = repositoryAssetService.loadRuleAsset( scenarioId );
        assertNotNull( asset.content );
        assertTrue( asset.content instanceof Scenario );

        Scenario sc_ = (Scenario) asset.content;
        sc_.getFixtures().add( new ExecutionTrace() );
        impl.checkinVersion( asset );
        asset = repositoryAssetService.loadRuleAsset( scenarioId );
        assertNotNull( asset.content );
        assertTrue( asset.content instanceof Scenario );
        sc_ = (Scenario) asset.content;
        assertEquals( 1,
                      sc_.getFixtures().size() );

    }

    @Test
    @Ignore
    public void testRunScenarioWithGeneratedBeans() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        PackageItem pkg = repo.createPackage( "testScenarioRunWithGeneratedBeans",
                                              "" );
        DroolsHeader.updateDroolsHeader( "declare GenBean\n name: String \n age: int \nend\n",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \n p : GenBean(name=='mic') \n then \n p.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "c" );
        person.setType( "GenBean" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "mic" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "c" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "mic",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        SingleScenarioResult res_ = repositoryPackageService.runScenario( pkg.getName(),
                                                                          sc );
        assertTrue( res_.auditLog.size() > 0 );

        String[] logEntry = res_.auditLog.get( 0 );
        assertNotNull( logEntry[0],
                       logEntry[1] );

        ScenarioRunResult res = res_.result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    @Ignore
    public void testRunPackageScenariosWithDeclaredFacts() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        PackageItem pkg = repo.createPackage( "testScenarioRunBulkWithDeclaredFacts",
                                              "" );
        DroolsHeader.updateDroolsHeader( "declare Wang \n age: Integer \n name: String \n end",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Wang() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );

        //this rule will never fire
        AssetItem rule2 = pkg.addAsset( "rule_2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "rule 'rule2' \n when \np : Wang(age == 1000) \n then \np.setAge(46); \n end" );
        rule2.checkin( "" );
        repo.save();

        //first, the green scenario
        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Wang" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "michael",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        AssetItem scenario1 = pkg.addAsset( "scen1",
                                            "" );
        scenario1.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario1.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario1.checkin( "" );

        //now the bad scenario
        sc = new Scenario();
        person = new FactData();
        person.setName( "p" );
        person.setType( "Wang" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        vr = new VerifyRuleFired( "rule2",
                                  1,
                                  null );
        sc.getFixtures().add( vr );

        AssetItem scenario2 = pkg.addAsset( "scen2",
                                            "" );
        scenario2.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario2.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario2.checkin( "" );

        BulkTestRunResult result = repositoryPackageService.runScenariosInPackage( pkg.getUUID() );
        assertNull( result.getResult() );

        assertEquals( 50,
                      result.getPercentCovered() );
        assertEquals( 1,
                      result.getRulesNotCovered().length );
        assertEquals( "rule2",
                      result.getRulesNotCovered()[0] );

        assertEquals( 2,
                      result.getResults().length );

        ScenarioResultSummary s1 = result.getResults()[0];
        assertEquals( 0,
                      s1.getFailures() );
        assertEquals( 3,
                      s1.getTotal() );
        assertEquals( scenario1.getUUID(),
                      s1.getUuid() );
        assertEquals( scenario1.getName(),
                      s1.getScenarioName() );

        ScenarioResultSummary s2 = result.getResults()[1];
        assertEquals( 1,
                      s2.getFailures() );
        assertEquals( 1,
                      s2.getTotal() );
        assertEquals( scenario2.getUUID(),
                      s2.getUuid() );
        assertEquals( scenario2.getName(),
                      s2.getScenarioName() );
    }

    @Test
    @Ignore
    public void testRunScenarioWithJar() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        // create our package
        PackageItem pkg = repo.createPackage( "testRunScenarioWithJar",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n dialect 'mvel' \n when Board() then System.err.println(42); \n end" );
        asset.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Board" );
        person.getFieldData().add( new FieldData( "cost",
                                                  "42" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "MyGoodRule",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );

        vf.getFieldValues().add( new VerifyField( "cost",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        ScenarioRunResult res = repositoryPackageService.runScenario( pkg.getName(),
                                                                      sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );

        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );

        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;
        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vf.wasSuccessful() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    @Ignore
    public void testRunScenarioWithJarThatHasSourceFiles() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        // create our package
        PackageItem pkg = repo.createPackage( "testRunScenarioWithJarThatHasSourceFiles",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/jarWithSourceFiles.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import org.test.Person; \n import org.test.Banana; \n ",
                                                  pkg );

        AssetItem asset = pkg.addAsset( "testRule",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'MyGoodRule' \n dialect 'mvel' \n when \n Person() \n then \n insert( new Banana() ); \n end" );
        asset.checkin( "" );
        repo.save();

        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "MyGoodRule",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        ScenarioRunResult res = null;
        try {
            res = repositoryPackageService.runScenario( pkg.getName(),
                                                        sc ).result;
        } catch ( ClassFormatError e ) {
            fail( "Probably failed when loading a source file instead of class file. " + e );
        }

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

        RuleBaseCache.getInstance().clearCache();

        res = repositoryPackageService.runScenario( pkg.getName(),
                                                    sc ).result;

        assertNull( res.getErrors() );
        assertNotNull( res.getScenario() );
        assertTrue( vr.wasSuccessful() );

    }

    @Test
    @Ignore
    public void testRunPackageScenarios() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();

        PackageItem pkg = repo.createPackage( "testScenarioRunBulk",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );

        //this rule will never fire
        AssetItem rule2 = pkg.addAsset( "rule_2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "rule 'rule2' \n when \np : Person(age == 1000) \n then \np.setAge(46); \n end" );
        rule2.checkin( "" );
        repo.save();

        //first, the green scenario
        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        VerifyRuleFired vr = new VerifyRuleFired( "rule1",
                                                  1,
                                                  null );
        sc.getFixtures().add( vr );

        VerifyFact vf = new VerifyFact();
        vf.setName( "p" );
        vf.getFieldValues().add( new VerifyField( "name",
                                                  "michael",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );
        sc.getFixtures().add( vf );

        AssetItem scenario1 = pkg.addAsset( "scen1",
                                            "" );
        scenario1.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario1.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario1.checkin( "" );

        //now the bad scenario
        sc = new Scenario();
        person = new FactData();
        person.setName( "p" );
        person.setType( "Person" );
        person.getFieldData().add( new FieldData( "age",
                                                  "40" ) );
        person.getFieldData().add( new FieldData( "name",
                                                  "michael" ) );

        sc.getFixtures().add( person );
        sc.getFixtures().add( new ExecutionTrace() );
        vr = new VerifyRuleFired( "rule2",
                                  1,
                                  null );
        sc.getFixtures().add( vr );

        AssetItem scenario2 = pkg.addAsset( "scen2",
                                            "" );
        scenario2.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario2.updateContent( ScenarioXMLPersistence.getInstance().marshal( sc ) );
        scenario2.checkin( "" );

        AssetItem scenario3 = pkg.addAsset( "scenBOGUS",
                                            "" );
        scenario3.updateFormat( AssetFormats.TEST_SCENARIO );
        scenario3.updateContent( "SOME RUBBISH" );
        scenario3.updateDisabled( true );
        scenario3.checkin( "" );

        //love you
        long time = System.currentTimeMillis();
        BulkTestRunResult result = repositoryPackageService.runScenariosInPackage( pkg.getUUID() );
        System.err.println( "Time taken for runScenariosInPackage " + (System.currentTimeMillis() - time) );
        assertNull( result.getResult() );

        assertEquals( 50,
                      result.getPercentCovered() );
        assertEquals( 1,
                      result.getRulesNotCovered().length );
        assertEquals( "rule2",
                      result.getRulesNotCovered()[0] );

        assertEquals( 2,
                      result.getResults().length );

        ScenarioResultSummary s1 = result.getResults()[0];
        assertEquals( 0,
                      s1.getFailures() );
        assertEquals( 3,
                      s1.getTotal() );
        assertEquals( scenario1.getUUID(),
                      s1.getUuid() );
        assertEquals( scenario1.getName(),
                      s1.getScenarioName() );

        ScenarioResultSummary s2 = result.getResults()[1];
        assertEquals( 1,
                      s2.getFailures() );
        assertEquals( 1,
                      s2.getTotal() );
        assertEquals( scenario2.getUUID(),
                      s2.getUuid() );
        assertEquals( scenario2.getName(),
                      s2.getScenarioName() );
    }

    @Test
    @Ignore
    public void testListFactTypesAvailableInPackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RulesRepository repo = impl.getRulesRepository();

        PackageItem pkg = repo.createPackage( "testAvailableTypes",
                                              "" );
        AssetItem model = pkg.addAsset( "MyModel",
                                        "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        repo.save();

        String[] s = repositoryPackageService.listTypesInPackage( pkg.getUUID() );
        assertNotNull( s );
        assertEquals( 2,
                      s.length );
        assertEquals( "com.billasurf.Person",
                      s[0] );
        assertEquals( "com.billasurf.Board",
                      s[1] );

        AssetItem asset = pkg.addAsset( "declaretTypes",
                                        "" );
        asset.updateFormat( AssetFormats.DRL_MODEL );
        asset.updateContent( "declare Whee\n name: String \n end" );
        asset.checkin( "" );

        s = repositoryPackageService.listTypesInPackage( pkg.getUUID() );
        assertEquals( 3,
                      s.length );
        assertEquals( "Whee",
                      s[2] );

    }

    @Test
    @Ignore
    public void testGuidedDTExecute() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RulesRepository repo = impl.getRulesRepository();
        repositoryCategoryService.createCategory( "/",
                                                  "decisiontables",
                                                  "" );

        PackageItem pkg = repo.createPackage( "testGuidedDTCompile",
                                              "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                                  pkg );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end" );
        rule1.checkin( "" );
        repo.save();

        TypeSafeGuidedDecisionTable dt = new TypeSafeGuidedDecisionTable();
        ConditionCol col = new ConditionCol();
        col.setBoundName( "p" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFactField( "hair" );
        col.setFactType( "Person" );
        col.setOperator( "==" );
        dt.getConditionCols().add( col );

        ActionSetFieldCol ac = new ActionSetFieldCol();
        ac.setBoundName( "p" );
        ac.setFactField( "likes" );
        ac.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( ac );

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{new String[]{"1", "descrip", "pink", "cheese"}} ) );

        String uid = impl.createNewRule( "decTable",
                                         "",
                                         "decisiontables",
                                         pkg.getName(),
                                         AssetFormats.DECISION_TABLE_GUIDED );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RuleAsset ass = repositoryAssetService.loadRuleAsset( uid );
        ass.content = dt;
        impl.checkinVersion( ass );

        BuilderResult results = repositoryPackageService.buildPackage( pkg.getUUID(),
                                                                       true );
        assertNull( results );

        pkg = repo.loadPackage( "testGuidedDTCompile" );
        byte[] binPackage = pkg.getCompiledPackageBytes();

        assertNotNull( binPackage );

        Package binPkg = (Package) DroolsStreamUtils.streamIn( binPackage );

        assertEquals( 2,
                      binPkg.getRules().length );

        assertNotNull( binPkg );
        assertTrue( binPkg.isValid() );

        Person p = new Person();

        p.setHair( "pink" );

        BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
        loader.addPackage( new ByteArrayInputStream( binPackage ) );
        RuleBase rb = loader.getRuleBase();

        StatelessSession sess = rb.newStatelessSession();
        sess.execute( p );
        assertEquals( 42,
                      p.getAge() );
        assertEquals( "cheese",
                      p.getLikes() );
    }

    @Test
    @Ignore
    public void testLoadDropDown() throws Exception {
        ServiceImplementation serv = new ServiceImplementation();
        String[] pairs = new String[]{"f1=x", "f2=2"};
        String expression = "['@{f1}', '@{f2}']";
        String[] r = serv.loadDropDownExpression( pairs,
                                                  expression );
        assertEquals( 2,
                      r.length );

        assertEquals( "x",
                      r[0] );
        assertEquals( "2",
                      r[1] );

    }

    @Test
    @Ignore
    public void testLoadDropDownNoValuePairs() throws Exception {
        ServiceImplementation serv = new ServiceImplementation();
        String[] pairs = new String[]{null};
        String expression = "['@{f1}', '@{f2}']";
        String[] r = serv.loadDropDownExpression( pairs,
                                                  expression );

        assertEquals( 0,
                      r.length );

    }

    @Test
    @Ignore
    @Deprecated
    public void testListUserPermisisons() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        Map<String, List<String>> r = serv.listUserPermissions();
        assertNotNull( r );
    }

    @Test
    public void testListUserPermissionsPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        //Setup data
        ServiceImplementation impl = getServiceImplementation();
        impl.createUser( "user1" );
        impl.createUser( "user2" );
        impl.createUser( "user3" );

        PageRequest request = new PageRequest( 0,
                                               PAGE_SIZE );
        PageResponse<PermissionsPageRow> response;
        response = impl.listUserPermissions( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.listUserPermissions( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testListUserPermissionsFullResults() throws Exception {

        //Setup data
        ServiceImplementation impl = getServiceImplementation();
        impl.createUser( "user1" );
        impl.createUser( "user2" );
        impl.createUser( "user3" );

        PageRequest request = new PageRequest( 0,
                                               null );
        PageResponse<PermissionsPageRow> response;
        response = impl.listUserPermissions( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testShowLogPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        //Setup data (createUser makes log entries)
        ServiceImplementation impl = getServiceImplementation();
        impl.cleanLog();
        impl.createUser( "user1" );
        impl.createUser( "user2" );
        impl.createUser( "user3" );

        PageRequest request = new PageRequest( 0,
                                               PAGE_SIZE );
        PageResponse<LogPageRow> response;
        response = impl.showLog( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.showLog( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testShowLogFullResults() throws Exception {

        //Setup data (createUser makes log entries)
        ServiceImplementation impl = getServiceImplementation();
        impl.cleanLog();
        impl.createUser( "user1" );
        impl.createUser( "user2" );
        impl.createUser( "user3" );

        PageRequest request = new PageRequest( 0,
                                               null );
        PageResponse<LogPageRow> response;
        response = impl.showLog( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                                          "desc",
                                          cat,
                                          "testTextSearch",
                                          AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQueryFullTextFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                                          "desc",
                                          cat,
                                          "testTextSearch",
                                          AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQuickFindAssetPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                                          "desc",
                                          cat,
                                          "testTextSearch",
                                          AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQuickFindAssetFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                                          "desc",
                                          cat,
                                          "testTextSearch",
                                          AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQueryMetaDataPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testMetaDataSearch",
                                                                   "" );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "wang, testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList( qr );
        QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                         DateUtils.parseDate( "10-Jul-1974",
                                                                                                       new DateFormatsImpl() ),
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         false,
                                                                         0,
                                                                         PAGE_SIZE );

        PageResponse<QueryPageRow> response;
        response = impl.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testQueryMetaDataFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testMetaDataSearch",
                                                                   "" );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "wang, testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList( qr );
        QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                         DateUtils.parseDate( "10-Jul-1974",
                                                                                                       new DateFormatsImpl() ),
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         false,
                                                                         0,
                                                                         null );

        PageResponse<QueryPageRow> response;
        response = impl.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadRuleListForCategoryPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );

        impl.createNewRule( "testTextRule1",
                            "testCategoryRule1",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule2",
                            "testCategoryRule2",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule3",
                            "testCategoryRule3",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );

        CategoryPageRequest request = new CategoryPageRequest( cat,
                                                               0,
                                                               PAGE_SIZE );
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadRuleListForCategoryFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );

        impl.createNewRule( "testTextRule1",
                            "testCategoryRule1",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule2",
                            "testCategoryRule2",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule3",
                            "testCategoryRule3",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );

        CategoryPageRequest request = new CategoryPageRequest( cat,
                                                               0,
                                                               null );
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadRuleListForStatePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        String cat = "testCategory";
        String status = "testStatus";
        String uuid;
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );
        impl.createState( status );

        uuid = impl.createNewRule( "testTextRule1",
                                   "testCategoryRule1",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        uuid = impl.createNewRule( "testTextRule2",
                                   "testCategoryRule2",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        uuid = impl.createNewRule( "testTextRule3",
                                   "testCategoryRule3",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        StatePageRequest request = new StatePageRequest( status,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<StatePageRow> response;
        response = impl.loadRuleListForState( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.loadRuleListForState( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadRuleListForStateFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        String cat = "testCategory";
        String status = "testStatus";
        String uuid;
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );
        impl.createState( status );

        uuid = impl.createNewRule( "testTextRule1",
                                   "testCategoryRule1",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        uuid = impl.createNewRule( "testTextRule2",
                                   "testCategoryRule2",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        uuid = impl.createNewRule( "testTextRule3",
                                   "testCategoryRule3",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                          status,
                          false );

        StatePageRequest request = new StatePageRequest( status,
                                                         0,
                                                         null );
        PageResponse<StatePageRow> response;
        response = impl.loadRuleListForState( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadArchivedAssetsPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testLoadArchivedAssetsPackage",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadArchivedAssetsCat",
                                                  "this is a cat" );

        String uuid1 = impl.createNewRule( "testLoadArchivedAssets1",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( uuid1 );

        String uuid2 = impl.createNewRule( "testLoadArchivedAssets2",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid2 );

        String uuid3 = impl.createNewRule( "testLoadArchivedAssets3",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid3 );

        PageRequest request = new PageRequest( 0,
                                               PAGE_SIZE );
        PageResponse<AdminArchivedPageRow> response;
        response = repositoryAssetService.loadArchivedAssets( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryAssetService.loadArchivedAssets( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadArchivedAssetsFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testLoadArchivedAssetsPackage",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadArchivedAssetsCat",
                                                  "this is a cat" );

        String uuid1 = impl.createNewRule( "testLoadArchivedAssets1",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( uuid1 );

        String uuid2 = impl.createNewRule( "testLoadArchivedAssets2",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid2 );

        String uuid3 = impl.createNewRule( "testLoadArchivedAssets3",
                                           "description",
                                           "testLoadArchivedAssetsCat",
                                           "testLoadArchivedAssetsPackage",
                                           AssetFormats.DRL );
        repositoryAssetService.archiveAsset( uuid3 );

        PageRequest request = new PageRequest( 0,
                                               null );
        PageResponse<AdminArchivedPageRow> response;
        response = repositoryAssetService.loadArchivedAssets( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadInboxPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        UserInbox ib = new UserInbox( impl.getRulesRepository() );
        ib.clearAll();

        @SuppressWarnings("unused")
        RuleAsset asset;
        String uuid;
        impl.getRulesRepository().createPackage( "testLoadInboxPackage",
                                                 "testLoadInboxDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadInboxCategory",
                                                  "testLoadInboxCategoryDescription" );

        uuid = impl.createNewRule( "rule1",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        asset = repositoryAssetService.loadRuleAsset( uuid );

        uuid = impl.createNewRule( "rule2",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        asset = repositoryAssetService.loadRuleAsset( uuid );

        uuid = impl.createNewRule( "rule3",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        asset = repositoryAssetService.loadRuleAsset( uuid );

        InboxPageRequest request = new InboxPageRequest( ExplorerNodeConfig.RECENT_VIEWED_ID,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<InboxPageRow> response;
        response = impl.loadInbox( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.loadInbox( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testLoadInboxFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        UserInbox ib = new UserInbox( impl.getRulesRepository() );

        ib.clearAll();

        @SuppressWarnings("unused")
        RuleAsset asset;
        String uuid;
        impl.getRulesRepository().createPackage( "testLoadInboxPackage",
                                                 "testLoadInboxDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testLoadInboxCategory",
                                                  "testLoadInboxCategoryDescription" );

        uuid = impl.createNewRule( "rule1",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        asset = repositoryAssetService.loadRuleAsset( uuid );

        uuid = impl.createNewRule( "rule2",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        asset = repositoryAssetService.loadRuleAsset( uuid );

        uuid = impl.createNewRule( "rule3",
                                   "desc",
                                   "testLoadInboxCategory",
                                   "testLoadInboxPackage",
                                   AssetFormats.DRL );
        asset = repositoryAssetService.loadRuleAsset( uuid );

        InboxPageRequest request = new InboxPageRequest( ExplorerNodeConfig.RECENT_VIEWED_ID,
                                                         0,
                                                         null );
        PageResponse<InboxPageRow> response;
        response = impl.loadInbox( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testSnapshotDiffPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();

        // Lets make a package and put a rule into it
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotDiffTestingCategory",
                                                  "snapshotDiffTestingCategoryDescription" );
        String packageUuid = repositoryPackageService.createPackage( "snapshotDiffTestingPackage",
                                                                     "snapshotDiffTestingPackageDescription" );
        assertNotNull( packageUuid );

        // Create some rules
        String archiveRuleUuid = impl.createNewRule( "testRuleArchived",
                                                     "testRuleArchivedDescription",
                                                     "snapshotDiffTestingCategory",
                                                     "snapshotDiffTestingPackage",
                                                     AssetFormats.DRL );
        String modifiedRuleUuid = impl.createNewRule( "testRuleModified",
                                                      "testRuleModifiedDescription",
                                                      "snapshotDiffTestingCategory",
                                                      "snapshotDiffTestingPackage",
                                                      AssetFormats.DRL );
        String deletedRuleUuid = impl.createNewRule( "testRuleDeleted",
                                                     "testRuleDeletedDescription",
                                                     "snapshotDiffTestingCategory",
                                                     "snapshotDiffTestingPackage",
                                                     AssetFormats.DRL );
        String restoredRuleUuid = impl.createNewRule( "testRuleRestored",
                                                      "testRuleRestoredDescription",
                                                      "snapshotDiffTestingCategory",
                                                      "snapshotDiffTestingPackage",
                                                      AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( restoredRuleUuid );

        @SuppressWarnings("unused")
        String noChangesRuleUuid = impl.createNewRule( "testRuleNoChanges",
                                                       "testRuleNoChangesDescription",
                                                       "snapshotDiffTestingCategory",
                                                       "snapshotDiffTestingPackage",
                                                       AssetFormats.DRL );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createPackageSnapshot( "snapshotDiffTestingPackage",
                                                        "FIRST",
                                                        false,
                                                        "First snapshot" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "snapshotDiffTestingPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "snapshotDiffTestingPackage" ).length );

        // Change a rule...
        RuleAsset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = impl.checkinVersion( asset );
        assertNotNull( uuid );

        //...delete one...
        repositoryAssetService.removeAsset( deletedRuleUuid );

        //...archive one...
        repositoryAssetService.archiveAsset( archiveRuleUuid );

        //...create a new one...
        @SuppressWarnings("unused")
        String addedRuleUuid = impl.createNewRule( "testRuleAdded",
                                                   "testRuleAddedDescription",
                                                   "snapshotDiffTestingCategory",
                                                   "snapshotDiffTestingPackage",
                                                   AssetFormats.DRL );

        //...and unarchive one
        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createPackageSnapshot( "snapshotDiffTestingPackage",
                                                        "SECOND",
                                                        false,
                                                        "Second snapshot" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "snapshotDiffTestingPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "snapshotDiffTestingPackage" ).length );

        // Compare the snapshots
        SnapshotComparisonPageRequest request = new SnapshotComparisonPageRequest( "snapshotDiffTestingPackage",
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
    @Ignore("until repository locking issue is resolved")
    public void testSnapshotDiffFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();

        // Lets make a package and put a rule into it
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotDiffTestingCategory",
                                                  "snapshotDiffTestingCategoryDescription" );
        String packageUuid = repositoryPackageService.createPackage( "snapshotDiffTestingPackage",
                                                                     "snapshotDiffTestingPackageDescription" );
        assertNotNull( packageUuid );

        // Create some rules
        String archiveRuleUuid = impl.createNewRule( "testRuleArchived",
                                                     "testRuleArchivedDescription",
                                                     "snapshotDiffTestingCategory",
                                                     "snapshotDiffTestingPackage",
                                                     AssetFormats.DRL );
        String modifiedRuleUuid = impl.createNewRule( "testRuleModified",
                                                      "testRuleModifiedDescription",
                                                      "snapshotDiffTestingCategory",
                                                      "snapshotDiffTestingPackage",
                                                      AssetFormats.DRL );
        String deletedRuleUuid = impl.createNewRule( "testRuleDeleted",
                                                     "testRuleDeletedDescription",
                                                     "snapshotDiffTestingCategory",
                                                     "snapshotDiffTestingPackage",
                                                     AssetFormats.DRL );
        String restoredRuleUuid = impl.createNewRule( "testRuleRestored",
                                                      "testRuleRestoredDescription",
                                                      "snapshotDiffTestingCategory",
                                                      "snapshotDiffTestingPackage",
                                                      AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( restoredRuleUuid );

        @SuppressWarnings("unused")
        String noChangesRuleUuid = impl.createNewRule( "testRuleNoChanges",
                                                       "testRuleNoChangesDescription",
                                                       "snapshotDiffTestingCategory",
                                                       "snapshotDiffTestingPackage",
                                                       AssetFormats.DRL );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createPackageSnapshot( "snapshotDiffTestingPackage",
                                                        "FIRST",
                                                        false,
                                                        "First snapshot" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "snapshotDiffTestingPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "snapshotDiffTestingPackage" ).length );

        // Change a rule...
        RuleAsset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = impl.checkinVersion( asset );
        assertNotNull( uuid );

        //...delete one...
        repositoryAssetService.removeAsset( deletedRuleUuid );

        //...archive one...
        repositoryAssetService.archiveAsset( archiveRuleUuid );

        //...create a new one...
        @SuppressWarnings("unused")
        String addedRuleUuid = impl.createNewRule( "testRuleAdded",
                                                   "testRuleAddedDescription",
                                                   "snapshotDiffTestingCategory",
                                                   "snapshotDiffTestingPackage",
                                                   AssetFormats.DRL );

        //...and unarchive one
        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createPackageSnapshot( "snapshotDiffTestingPackage",
                                                        "SECOND",
                                                        false,
                                                        "Second snapshot" );
        assertEquals( 2,
                      repositoryPackageService.listSnapshots( "snapshotDiffTestingPackage" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "snapshotDiffTestingPackage" ).length );

        // Compare the snapshots
        SnapshotComparisonPageRequest request = new SnapshotComparisonPageRequest( "snapshotDiffTestingPackage",
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

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testFindAssetPagePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPagePackage",
                                                                           "testFindAssetPagePackageDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testFindAssetPageCategory",
                                                  "testFindAssetPageCategoryDescription" );

        impl.createNewRule( "testFindAssetPageAsset1",
                            "testFindAssetPageAsset1Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset2",
                            "testFindAssetPageAsset2Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset3",
                            "testFindAssetPageAsset3Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         PAGE_SIZE );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testFindAssetPageFullResults() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPagePackage",
                                                                           "testFindAssetPagePackageDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testFindAssetPageCategory",
                                                  "testFindAssetPageCategoryDescription" );

        impl.createNewRule( "testFindAssetPageAsset1",
                            "testFindAssetPageAsset1Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset2",
                            "testFindAssetPageAsset2Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset3",
                            "testFindAssetPageAsset3Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         null );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    @Ignore("until repository locking issue is resolved")
    public void testFindAssetPageUnregisteredAssetFormats() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPageUnregisteredAssetFormats",
                                                                           "testFindAssetPageUnregisteredAssetFormatsDescription" );
        AssetItem as;

        as = packageItem.addAsset( "assetWithKnownFormat",
                                   "assetWithKnownFormatDescription" );
        as.updateFormat( AssetFormats.DRL );
        as.checkin( "" );

        as = packageItem.addAsset( "assetWithUnknownFormat",
                                   "assetWithUnknownFormatDescription" );
        as.updateFormat( "something_silly" );
        as.checkin( "" );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.DRL );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         null );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertEquals( 1,
                      response.getPageRowList().size() );
    }

    @Test
    @Ignore
    public void testManageUserPermissions() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        Map<String, List<String>> perms = new HashMap<String, List<String>>();
        serv.updateUserPermissions( "googoo",
                                    perms );

        Map<String, List<String>> perms_ = serv.retrieveUserPermissions( "googoo" );
        assertEquals( 0,
                      perms_.size() );
    }

    @Test
    @Ignore
    public void testImportSampleRepository() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        repositoryPackageService.installSampleRepository();
        PackageConfigData[] cfgs = repositoryPackageService.listPackages();
        assertEquals( 2,
                      cfgs.length );
        assertTrue( cfgs[0].name.equals( "mortgages" ) || cfgs[1].name.equals( "mortgages" ) );
        String puuid = (cfgs[0].name.equals( "mortgages" )) ? cfgs[0].uuid : cfgs[1].uuid;
        BulkTestRunResult res = repositoryPackageService.runScenariosInPackage( puuid );
        assertEquals( null,
                      res.getResult() );
    }

    //GUVNOR-296
    @Test
    @Ignore
    public void testHistoryAfterReImportSampleRepository() throws Exception {

        QueryPageRequest request;
        PageResponse<QueryPageRow> response;
        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        //Import sample, do a sanity check, make sure sample is installed correctly
        repositoryPackageService.installSampleRepository();
        PackageConfigData[] cfgs = repositoryPackageService.listPackages();
        assertEquals( 2,
                      cfgs.length );
        assertTrue( cfgs[0].name.equals( "mortgages" ) || cfgs[1].name.equals( "mortgages" ) );

        request = new QueryPageRequest( "Bankruptcy history",
                                        false,
                                        0,
                                        20 );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        response = repositoryAssetService.quickFindAsset( request );
        assertEquals( 1,
                      response.getPageRowList().size() );
        String uuid = response.getPageRowList().get( 0 ).getUuid();

        // create version 4.
        RuleAsset ai = repositoryAssetService.loadRuleAsset( uuid );
        ai.metaData.checkinComment = "version 4";
        impl.checkinVersion( ai );

        // create version 5.
        ai = repositoryAssetService.loadRuleAsset( uuid );
        ai.metaData.checkinComment = "version 5";
        impl.checkinVersion( ai );

        System.out.println( "old uuid: " + uuid );

        //NOTE: Have not figured out the reason, but if we dont create a random package here, 
        //we will get an InvalidItemStateException during impl.installSampleRepository()
        impl.getRulesRepository().createPackage( "testHistoryAfterReImportSampleRepository",
                                                 "desc" );

        TableDataResult result = repositoryAssetService.loadItemHistory( uuid );
        assertNotNull( result );
        TableDataRow[] rows = result.data;
        assertEquals( 1,
                      rows.length );

        //Import sample again
        repositoryPackageService.installSampleRepository();

        request = new QueryPageRequest( "Bankruptcy history",
                                        false,
                                        0,
                                        20 );
        response = repositoryAssetService.quickFindAsset( request );
        assertEquals( 1,
                      response.getPageRowList().size() );
        String newUuid = response.getPageRowList().get( 0 ).getUuid();

        //Now verify history, should be zero.
        result = repositoryAssetService.loadItemHistory( newUuid );
        System.out.println( "new uuid: " + newUuid );

        assertNotNull( result );
        rows = result.data;
        assertEquals( 1,
                      rows.length );
    }

    @Test
    @Ignore
    public void testAddCategories() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testAddCategoriesPackage",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat1",
                                                  "this is a cat" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat2",
                                                  "this is a cat" );

        String uuid = impl.createNewRule( "testCreateNewRuleName",
                                          "an initial desc",
                                          "testAddCategoriesCat1",
                                          "testAddCategoriesPackage",
                                          AssetFormats.DSL_TEMPLATE_RULE );

        AssetItem dtItem = impl.getRulesRepository().loadAssetByUUID( uuid );
        dtItem.addCategory( "testAddCategoriesCat1" );
        impl.getRulesRepository().save();

        AssetItem dtItem1 = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( 1,
                      dtItem1.getCategories().size() );
        assertTrue( dtItem1.getCategorySummary().contains( "testAddCategoriesCat1" ) );

        AssetItem dtItem2 = impl.getRulesRepository().loadAssetByUUID( uuid );
        dtItem2.addCategory( "testAddCategoriesCat2" );
        impl.getRulesRepository().save();

        AssetItem dtItem3 = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( 2,
                      dtItem3.getCategories().size() );
        assertTrue( dtItem3.getCategorySummary().contains( "testAddCategoriesCat2" ) );
    }

    @Test
    @Ignore
    public void testSnapshotDiff() throws Exception {
        RepositoryService impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        // Lets make a package and a rule into tit.
        repositoryCategoryService.createCategory( "/",
                                                  "snapshotDiffTesting",
                                                  "y" );
        String packageUuid = repositoryPackageService.createPackage( "testSnapshotDiff",
                                                                     "d" );

        assertNotNull( packageUuid );

        // Create two rules
        String archiveRuleUuid = impl.createNewRule( "testRuleArchived",
                                                     "",
                                                     "snapshotDiffTesting",
                                                     "testSnapshotDiff",
                                                     AssetFormats.DRL );
        String modifiedRuleUuid = impl.createNewRule( "testRuleModified",
                                                      "",
                                                      "snapshotDiffTesting",
                                                      "testSnapshotDiff",
                                                      AssetFormats.DRL );
        String deletedRuleUuid = impl.createNewRule( "testRuleDeleted",
                                                     "",
                                                     "snapshotDiffTesting",
                                                     "testSnapshotDiff",
                                                     AssetFormats.DRL );
        String restoredRuleUuid = impl.createNewRule( "testRuleRestored",
                                                      "",
                                                      "snapshotDiffTesting",
                                                      "testSnapshotDiff",
                                                      AssetFormats.DRL );
        @SuppressWarnings("unused")
        String noChangesRuleUuid = impl.createNewRule( "testRuleNoChanges",
                                                       "",
                                                       "snapshotDiffTesting",
                                                       "testSnapshotDiff",
                                                       AssetFormats.DRL );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        repositoryAssetService.archiveAsset( restoredRuleUuid );

        // Create a snapshot called FIRST for the package
        repositoryPackageService.createPackageSnapshot( "testSnapshotDiff",
                                                        "FIRST",
                                                        false,
                                                        "ya" );
        assertEquals( 1,
                      repositoryPackageService.listSnapshots( "testSnapshotDiff" ).length );
        assertEquals( 4,
                      repositoryPackageService.listRulesInPackage( "testSnapshotDiff" ).length );

        // Change the rule, archive one, delete one and create a new one
        RuleAsset asset = repositoryAssetService.loadRuleAsset( modifiedRuleUuid );
        String uuid = impl.checkinVersion( asset );
        assertNotNull( uuid );

        repositoryAssetService.removeAsset( deletedRuleUuid );

        repositoryAssetService.archiveAsset( archiveRuleUuid );

        @SuppressWarnings("unused")
        String addedRuleUuid = impl.createNewRule( "testRuleAdded",
                                                   "",
                                                   "snapshotDiffTesting",
                                                   "testSnapshotDiff",
                                                   AssetFormats.DRL );

        repositoryAssetService.unArchiveAsset( restoredRuleUuid );

        // Create a snapshot called SECOND for the package
        repositoryPackageService.createPackageSnapshot( "testSnapshotDiff",
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
    @Ignore
    public void testWorkspaces() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        impl.createWorkspace( "testWorkspaces1" );
        impl.createWorkspace( "testWorkspaces2" );

        String[] result = impl.listWorkspaces();
        assertEquals( 2,
                      result.length );
    }

    @Test
    public void testGetHistoryPackageSource() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        //Package version 1(Initial version)
        PackageItem pkg = impl.getRulesRepository().createPackage( "testGetHistoryPackageSource",
                                                                   "" );

        //Package version 2	
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer1",
                                         pkg );

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(version 1); }" );
        func.checkin( "version 1" );

        AssetItem dsl = pkg.addAsset( "myDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz1()" );
        dsl.checkin( "version 1" );

        AssetItem rule = pkg.addAsset( "rule1",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo1() then end" );
        rule.checkin( "version 1" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 1" );

        AssetItem rule3 = pkg.addAsset( "model1",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL_MODEL );
        rule3.updateContent( "declare Album1\n genre1: String \n end" );
        rule3.checkin( "version 1" );

        pkg.checkin( "version2" );

        //Package version 3
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer2",
                                         pkg );
        func.updateContent( "function void foo() { System.out.println(version 2); }" );
        func.checkin( "version 2" );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz2()" );
        dsl.checkin( "version 2" );
        rule.updateContent( "rule 'foo' when Goo2() then end" );
        rule.checkin( "version 2" );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 2" );
        rule3.updateContent( "declare Album2\n genre2: String \n end" );
        rule3.checkin( "version 2" );
        //impl.buildPackage(pkg.getUUID(), true);
        pkg.checkin( "version3" );

        //Verify the latest version
        PackageItem item = impl.getRulesRepository().loadPackage( "testGetHistoryPackageSource" );
        ContentPackageAssembler asm = new ContentPackageAssembler( item,
                                                                   false );
        String drl = asm.getDRL();

        System.out.println( drl );

        assertEquals( "version3",
                      item.getCheckinComment() );
        assertTrue( drl.indexOf( "global com.billasurf.Person customer2" ) >= 0 );
        assertTrue( drl.indexOf( "System.out.println(version 2)" ) >= 0 );
        assertTrue( drl.indexOf( "FooBarBaz2()" ) >= 0 );
        assertTrue( drl.indexOf( "rule 'foo' when Goo2() then end" ) >= 0 );
        assertTrue( drl.indexOf( "foo" ) >= 0 );
        assertTrue( drl.indexOf( "declare Album2" ) >= 0 );
        //assertEquals(12, item.getCompiledPackageBytes().length);

        //Verify version 2
        PackageItem item2 = impl.getRulesRepository().loadPackage( "testGetHistoryPackageSource",
                                                                   2 );
        ContentPackageAssembler asm2 = new ContentPackageAssembler( item2,
                                                                    false );
        String drl2 = asm2.getDRL();

        System.out.println( drl2 );

        assertEquals( "version2",
                      item2.getCheckinComment() );
        assertTrue( drl2.indexOf( "global com.billasurf.Person customer1" ) >= 0 );
        assertTrue( drl2.indexOf( "System.out.println(version 1)" ) >= 0 );
        assertTrue( drl2.indexOf( "FooBarBaz1()" ) >= 0 );
        assertTrue( drl2.indexOf( "rule 'foo' when Goo1() then end" ) >= 0 );
        assertTrue( drl2.indexOf( "foo" ) >= 0 );
        assertTrue( drl2.indexOf( "declare Album1" ) >= 0 );
    }

    @Test
    public void testDependencyHistoryPackage() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        //Package version 1
        PackageItem pkg = impl.getRulesRepository().createPackage( "testDependencyHistoryPackage",
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
        PackageItem item = impl.getRulesRepository().loadPackage( "testDependencyHistoryPackage" );
        assertEquals( "package version 4",
                      item.getCheckinComment() );
        assertEquals( "func?version=1",
                      item.getDependencies()[0] );

        //Verify version 2
        item = impl.getRulesRepository().loadPackage( "testDependencyHistoryPackage",
                                                      2 );
        assertEquals( "package version 2",
                      item.getCheckinComment() );
        assertEquals( "func?version=2",
                      item.getDependencies()[0] );

        //Verify version 3
        item = impl.getRulesRepository().loadPackage( "testDependencyHistoryPackage",
                                                      3 );
        assertEquals( "package version 3",
                      item.getCheckinComment() );
        assertEquals( "func?version=1",
                      item.getDependencies()[0] );
    }

}
