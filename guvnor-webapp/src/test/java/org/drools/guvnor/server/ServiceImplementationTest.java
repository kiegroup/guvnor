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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PermissionsPageRow;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.server.util.RepositoryUpgradeHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
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
    @Ignore("This needs a little re-work as it does not work as expected")
    public void testInboxEvents() throws Exception {
        
        try {
            RepositoryStartupService.registerCheckinListener();
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetServiceImpl = getRepositoryAssetService();
            assertNotNull( impl.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID ) );

            //this should trigger the fact that the original user edited something
            AssetItem as = impl.getRulesRepository().loadDefaultPackage().addAsset( "testLoadInbox",
                                                                                    "" );
            as.checkin( "" );
            RuleAsset ras = assetServiceImpl.loadRuleAsset( as.getUUID() );

            TableDataResult res = impl.loadInbox( ExplorerNodeConfig.RECENT_EDITED_ID );
            boolean found = false;
            for ( TableDataRow row : res.data ) {
                if ( row.id.equals( ras.getUuid() ) ) found = true;
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
        } finally {
            RepositoryStartupService.removeListeners();
        }

    }

    @Test
    public void testDeleteUnversionedRule() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();

        repo.loadDefaultPackage();
        repo.createPackage( "anotherPackage",
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
        repo.save();

        try {
            localItem = impl.getRulesRepository().loadAssetByUUID( uuid );
            fail();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
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
            if ( packages[i].getName().equals( "another" ) ) {
                found = true;
            }
        }

        assertTrue( found );

        assertFalse( packages[0].getUuid() == null );
        assertFalse( packages[0].getUuid().equals( "" ) );

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
    //path name contains Apostrophe is no longer a problem with jackrabbit 2.0
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
        assertEquals( assetWrapper.getDescription(),
                      "an initial desc" );
        assertEquals( assetWrapper.getName(),
                      "testCreateNewRuleContains' character" );

    }

    @Test
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
    @Deprecated
    public void testDateFormatting() throws Exception {
        Calendar cal = Calendar.getInstance();
        TableDisplayHandler handler = new TableDisplayHandler( ExplorerNodeConfig.RULE_LIST_TABLE_ID );
        String fmt = handler.formatDate( cal );
        assertNotNull( fmt );

        assertTrue( fmt.length() > 8 );
    }

    @Test
    public void testTrackRecentOpenedChanged() throws Exception {
        try {
            RepositoryStartupService.registerCheckinListener();
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

            repositoryAssetService.checkinVersion( ass );

            List<InboxEntry> es = ib.loadRecentEdited();
            assertEquals( 1,
                          es.size() );
            assertEquals( ass.getUuid(),
                          es.get( 0 ).assetUUID );
            assertEquals( ass.getName(),
                          es.get( 0 ).note );

            ib.clearAll();

            repositoryAssetService.loadRuleAsset( ass.getUuid() );
            es = ib.loadRecentEdited();
            assertEquals( 0,
                          es.size() );

            //now check they have it in their opened list...
            es = ib.loadRecentOpened();
            assertEquals( 1,
                          es.size() );
            assertEquals( ass.getUuid(),
                          es.get( 0 ).assetUUID );
            assertEquals( ass.getName(),
                          es.get( 0 ).note );

            assertEquals( 0,
                          ib.loadRecentEdited().size() );
        } finally {
            RepositoryStartupService.removeListeners();
        }
    }

    @Test
    public void testCheckin() throws Exception {
        try {
            RepositoryStartupService.registerCheckinListener();
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

            assertNotNull( asset.getLastModified() );

            asset.getMetaData().setCoverage( "boo" );
            asset.setContent( new RuleContentText() );
            ((RuleContentText) asset.getContent()).content = "yeah !";
            asset.setDescription( "Description 1" );

            Date start = new Date();
            Thread.sleep( 100 );

            String uuid2 = repositoryAssetService.checkinVersion( asset );
            assertEquals( uuid,
                          uuid2 );

            assertTrue( ib.loadRecentEdited().size() > inbox.size() );

            RuleAsset asset2 = repositoryAssetService.loadRuleAsset( uuid );
            assertNotNull( asset2.getLastModified() );
            assertTrue( asset2.getLastModified().after( start ) );

            assertEquals( "boo",
                          asset2.getMetaData().getCoverage() );
            assertEquals( 1,
                          asset2.getVersionNumber() );

            assertEquals( "yeah !",
                          ((RuleContentText) asset2.getContent()).content );

            assertEquals( "Description 1",
                          asset2.getDescription() );

            asset2.getMetaData().setCoverage( "ya" );
            asset2.setCheckinComment( "checked in" );

            String cat = asset2.getMetaData().getCategories()[0];
            asset2.getMetaData().setCategories( new String[3] );
            asset2.getMetaData().getCategories()[0] = cat;
            asset2.getMetaData().getCategories()[1] = "testCheckinCategory2";
            asset2.getMetaData().getCategories()[2] = "testCheckinCategory/deeper";
            asset2.setDescription( "Description 2" );

            repositoryAssetService.checkinVersion( asset2 );

            asset2 = repositoryAssetService.loadRuleAsset( uuid );
            assertEquals( "ya",
                          asset2.getMetaData().getCoverage() );
            assertEquals( 2,
                          asset2.getVersionNumber() );
            assertEquals( "checked in",
                          asset2.getCheckinComment() );
            assertEquals( 3,
                          asset2.getMetaData().getCategories().length );
            assertEquals( "testCheckinCategory",
                          asset2.getMetaData().getCategories()[0] );
            assertEquals( "testCheckinCategory2",
                          asset2.getMetaData().getCategories()[1] );
            assertEquals( "testCheckinCategory/deeper",
                          asset2.getMetaData().getCategories()[2] );
            assertEquals( "Description 2",
                          asset2.getDescription() );

            // now lets try a concurrent edit of an asset.
            // asset3 will be loaded and edited, and then asset2 will try to
            // clobber, it, which should fail.
            // as it is optimistically locked.
            RuleAsset asset3 = repositoryAssetService.loadRuleAsset( asset2.getUuid() );
            asset3.getMetaData().setSubject( "new sub" );
            repositoryAssetService.checkinVersion( asset3 );

            asset3 = repositoryAssetService.loadRuleAsset( asset2.getUuid() );
            assertFalse( asset3.getVersionNumber() == asset2.getVersionNumber() );

            String result = repositoryAssetService.checkinVersion( asset2 );
            assertTrue( result.startsWith( "ERR" ) );
            System.err.println( result.substring( 5 ) );
        } finally {
            RepositoryStartupService.removeListeners();
        }

    }

    @Test
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
                      asset.getState() );
        repositoryAssetService.changeState( ruleUUID,
                                            "testState" );
        asset = repositoryAssetService.loadRuleAsset( ruleUUID );
        assertEquals( "testState",
                      asset.getState() );
        asset = repositoryAssetService.loadRuleAsset( ruleUUID2 );
        assertEquals( StateItem.DRAFT_STATE_NAME,
                      asset.getState() );

        impl.createState( "testState2" );
        repositoryAssetService.changePackageState( packagUUID,
                                                   "testState2" );

        PackageConfigData pkg = repositoryPackageService.loadPackageConfig( packagUUID );
        assertEquals( "testState2",
                      pkg.getState() );

        asset = repositoryAssetService.loadRuleAsset( ruleUUID2 );
        assertEquals( "testState2",
                      asset.getState() );

        repositoryAssetService.checkinVersion( asset );
        asset = repositoryAssetService.loadRuleAsset( asset.getUuid() );
        assertEquals( "testState2",
                      asset.getState() );

    }

    @Test
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
     * this loads up a precompile binary package. If this fails, then it means
     * it needs to be updated. It gets the package form the BRL example above.
     * Simply set saveBinPackage to true to save a new version of the
     * RepoBinPackage.pkg.
     */

    @Test
    @Ignore("The binary package needs to be updated")
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
        repositoryAssetService.checkinVersion( asset );

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
        col.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
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
        ass.setContent( dt );
        repositoryAssetService.checkinVersion( ass );

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

        PageRequest requestPage1 = new PageRequest( 0,
                                                    PAGE_SIZE );
        PageResponse<PermissionsPageRow> responsePage1 = impl.listUserPermissions( requestPage1 );

        assertNotNull( responsePage1 );
        assertNotNull( responsePage1.getPageRowList() );

        System.out.println( "ListUserPermissionsFullResults-page1" );
        for ( PermissionsPageRow row : responsePage1.getPageRowList() ) {
            System.out.println( "--> Username = " + row.getUserName() );
        }

        assertEquals( 0,
                      responsePage1.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      responsePage1.getPageRowList().size() );
        assertFalse( responsePage1.isLastPage() );

        PageRequest requestPage2 = new PageRequest( PAGE_SIZE,
                                                    PAGE_SIZE );
        PageResponse<PermissionsPageRow> responsePage2 = impl.listUserPermissions( requestPage2 );

        assertNotNull( responsePage2 );
        assertNotNull( responsePage2.getPageRowList() );

        System.out.println( "ListUserPermissionsFullResults-page2" );
        for ( PermissionsPageRow row : responsePage2.getPageRowList() ) {
            System.out.println( "--> Username = " + row.getUserName() );
        }

        assertEquals( PAGE_SIZE,
                      responsePage2.getStartRowIndex() );
        assertEquals( 1,
                      responsePage2.getPageRowList().size() );
        assertTrue( responsePage2.isLastPage() );

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

        System.out.println( "ListUserPermissionsFullResults" );
        for ( PermissionsPageRow row : response.getPageRowList() ) {
            System.out.println( "--> Username = " + row.getUserName() );
        }

        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );

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
                                            status );

        uuid = impl.createNewRule( "testTextRule2",
                                   "testCategoryRule2",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                                            status );

        uuid = impl.createNewRule( "testTextRule3",
                                   "testCategoryRule3",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                                            status );

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
                                            status );

        uuid = impl.createNewRule( "testTextRule2",
                                   "testCategoryRule2",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                                            status );

        uuid = impl.createNewRule( "testTextRule3",
                                   "testCategoryRule3",
                                   cat,
                                   "testCategoryPackage",
                                   AssetFormats.DRL );
        repositoryAssetService.changeState( uuid,
                                            status );

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
    public void testImportSampleRepository() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        repositoryPackageService.installSampleRepository();
        PackageConfigData[] cfgs = repositoryPackageService.listPackages();
        assertEquals( 2,
                      cfgs.length );
        assertTrue( cfgs[0].getName().equals( "mortgages" ) || cfgs[1].getName().equals( "mortgages" ) );
        String puuid = (cfgs[0].getName().equals( "mortgages" )) ? cfgs[0].getUuid() : cfgs[1].getUuid();
        BulkTestRunResult res = repositoryPackageService.runScenariosInPackage( puuid );
        assertEquals( null,
                      res.getResult() );
    }

    //GUVNOR-296
    @Test
    @Ignore("This test is broken. Suspect changes for new Workspaces and versionable packages could have broken.")
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
        assertTrue( cfgs[0].getName().equals( "mortgages" ) || cfgs[1].getName().equals( "mortgages" ) );

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
        ai.setCheckinComment( "version 4" );
        repositoryAssetService.checkinVersion( ai );

        // create version 5.
        ai = repositoryAssetService.loadRuleAsset( uuid );
        ai.setCheckinComment( "version 5" );
        repositoryAssetService.checkinVersion( ai );

        System.out.println( "old uuid: " + uuid );

        //NOTE: Have not figured out the reason, but if we dont create a random package here, 
        //we will get an InvalidItemStateException during impl.installSampleRepository()
        impl.getRulesRepository().createPackage( "testHistoryAfterReImportSampleRepository",
                                                 "desc" );

        TableDataResult result = repositoryAssetService.loadItemHistory( uuid );
        assertNotNull( result );
        TableDataRow[] rows = result.data;
        assertEquals( 2,
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
        assertEquals( 0,
                      rows.length );
    }

    @Test
    public void testWorkspaces() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        impl.createWorkspace( "testWorkspaces1" );
        impl.createWorkspace( "testWorkspaces2" );

        String[] result = impl.listWorkspaces();
        assertEquals( 2,
                      result.length );
    }

}
