package org.drools.guvnor.server;

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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.Inbox;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionSetField;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.testing.ExecutionTrace;
import org.drools.guvnor.client.modeldriven.testing.FactData;
import org.drools.guvnor.client.modeldriven.testing.FieldData;
import org.drools.guvnor.client.modeldriven.testing.Scenario;
import org.drools.guvnor.client.modeldriven.testing.VerifyFact;
import org.drools.guvnor.client.modeldriven.testing.VerifyField;
import org.drools.guvnor.client.modeldriven.testing.VerifyRuleFired;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializableException;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.util.BRXMLPersistence;
import org.drools.guvnor.server.util.IO;
import org.drools.guvnor.server.util.ScenarioXMLPersistence;
import org.drools.guvnor.server.util.TableDisplayHandler;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.repository.RepositoryStartupService;
import org.drools.guvnor.server.repository.UserInbox;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.rule.Package;
import org.drools.util.BinaryRuleBaseLoader;
import org.drools.util.DateUtils;
import org.drools.util.DroolsStreamUtils;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.permission.RoleBasedPermissionResolver;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is really a collection of integration tests.
 * @author Michael Neale
 */
public class ServiceImplementationTest extends TestCase {

    static {

        try {
            MailboxService.getInstance().init(new RulesRepository(TestEnvironmentSessionHelper
            .getSession()));
            RepositoryStartupService.registerCheckinListener();
        } catch (Exception e) {
            fail("unable to init");
        }
    }


    public void testInboxEvents() throws Exception {
        ServiceImplementation impl = getService();
        assertNotNull(impl.loadInbox(Inbox.RECENT_EDITED));

        //this should trigger the fact that the original user edited something
        RulesRepository repo1 = impl.repository;
        AssetItem as = impl.repository.loadDefaultPackage().addAsset("testLoadInbox", "");
        as.checkin("");
        TableDataResult res = impl.loadInbox(Inbox.RECENT_EDITED);
        boolean found = false;
        for (TableDataRow row: res.data) { if (row.id.equals(as.getUUID())) found = true;}
        assertTrue(found);


        //but should not be in "incoming" yet
        found = false;
        res = impl.loadInbox(Inbox.INCOMING);
        for (TableDataRow row: res.data) { if (row.id.equals(as.getUUID())) found = true;}
        assertFalse(found);



        //Now, another user comes along, makes a change...
        RulesRepository repo2 = new RulesRepository(TestEnvironmentSessionHelper.getSessionFor("thirdpartyuser"));
        AssetItem as2 = repo2.loadDefaultPackage().loadAsset("testLoadInbox");
        as2.updateContent("hey");
        as2.checkin("here we go again !");

        Thread.sleep(200);

        //now check that it is in the first users inbox
        TableDataRow rowMatch = null;
        res = impl.loadInbox(Inbox.INCOMING);
        for (TableDataRow row: res.data) {
           if (row.id.equals(as.getUUID())) {
                rowMatch = row;
            }
        }
        assertNotNull(rowMatch);
        assertEquals(as.getName(), rowMatch.values[0]);
        assertEquals("thirdpartyuser", rowMatch.values[2]); //should be "from" that user name...


        //shouldn't be in thirdpartyusers inbox
        UserInbox ib = new UserInbox(repo2);
        ib.loadIncoming();
        assertEquals(0, ib.loadIncoming().size());
        assertEquals(1, ib.loadRecentEdited().size());


        //ok lets create another user...
        RulesRepository repo3 = new RulesRepository(TestEnvironmentSessionHelper.getSessionFor("fourthuser"));
        AssetItem as3 = repo3.loadDefaultPackage().loadAsset("testLoadInbox");
        as3.updateContent("hey22");
        as3.checkin("here we go again 22!");

        Thread.sleep(250);

        //so should be in thirdpartyuser inbox
        assertEquals(1, ib.loadIncoming().size());


        //and also still in the original user...
        found = false;
        res = impl.loadInbox(Inbox.INCOMING);
        for (TableDataRow row: res.data) { if (row.id.equals(as.getUUID())) found = true;}
        assertTrue(found);
        
        //now lets open it with first user, and check that it disappears from the incoming...
        impl.loadRuleAsset(as.getUUID());
        found = false;
        res = impl.loadInbox(Inbox.INCOMING);
        for (TableDataRow row: res.data) { if (row.id.equals(as.getUUID())) found = true;}
        assertFalse(found);


    }

	public void testCategory() throws Exception {

		RepositoryService impl = getService();

		String[] originalCats = impl.loadChildCategories("/");

		Boolean result = impl.createCategory("/", "TopLevel1", "a description");
		assertTrue(result.booleanValue());

		result = impl.createCategory("/", "TopLevel2", "a description");
		assertTrue(result.booleanValue());

		String[] cats = impl.loadChildCategories("/");
		assertTrue(cats.length == originalCats.length + 2);

		result = impl.createCategory("", "Top3", "description");
		assertTrue(result.booleanValue());

		result = impl.createCategory(null, "Top4", "description");
		assertTrue(result.booleanValue());

	}

    public void testCleanHTML() {
        ServiceImplementation impl = new ServiceImplementation();
        assertEquals("&lt;script&gt;", impl.cleanHTML("<script>"));
    }

	public void testDeleteUnversionedRule() throws Exception {
		ServiceImplementation impl = getService();

		impl.repository.loadDefaultPackage();
		impl.repository.createPackage("anotherPackage", "woot");

		CategoryItem cat = impl.repository.loadCategory("/");
		cat.addCategory("testDeleteUnversioned", "yeah");

		String uuid = impl.createNewRule("test Delete Unversioned",
				"a description", "testDeleteUnversioned", "anotherPackage",
				"txt");
		assertNotNull(uuid);
		assertFalse("".equals(uuid));

		AssetItem localItem = impl.repository.loadAssetByUUID(uuid);

		// String drl = "package org.drools.repository\n\ndialect 'mvel'\n\n" +
		// "rule Rule1 \n when \n AssetItem(description != null) \n then \n
		// System.out.println(\"yeah\");\nend";
		// RuleBase rb = RuleBaseLoader.getInstance().loadFromReader(new
		// StringReader(drl));
		// rb.newStatelessSession().execute(localItem);

		assertEquals("test Delete Unversioned", localItem.getName());

		localItem.remove();
		impl.repository.save();

		try {
			localItem = impl.repository.loadAssetByUUID(uuid);
			fail();
		} catch (Exception e) {
		}
	}

	public void testAddRuleAndListPackages() throws Exception {
		// ServiceImpl impl = new ServiceImpl(new
		// RulesRepository(SessionHelper.getSession()));

		ServiceImplementation impl = getService();

		impl.repository.loadDefaultPackage();
		impl.repository.createPackage("another", "woot");

		CategoryItem cat = impl.repository.loadCategory("/");
		cat.addCategory("testAddRule", "yeah");

		String result = impl.createNewRule("test AddRule", "a description",
				"testAddRule", "another", "txt");
		assertNotNull(result);
		assertFalse("".equals(result));

		PackageConfigData[] packages = impl.listPackages();
		assertTrue(packages.length > 0);

		boolean found = false;
		for (int i = 0; i < packages.length; i++) {
			if (packages[i].name.equals("another")) {
				found = true;
			}
		}

		assertTrue(found);

		assertFalse(packages[0].uuid == null);
		assertFalse(packages[0].uuid.equals(""));

		// just for performance testing with scaling up numbers of rules
		// for (int i=1; i <= 1000; i++) {
		// impl.createNewRule( "somerule_" + i, "description",
		// "testAddRule", "another", "drl" );
		// }

		result = impl
				.createNewRule("testDTSample", "a description", "testAddRule",
						"another", AssetFormats.DECISION_SPREADSHEET_XLS);
		AssetItem dtItem = impl.repository.loadAssetByUUID(result);
		assertNotNull(dtItem.getBinaryContentAsBytes());
		assertTrue(dtItem.getBinaryContentAttachmentFileName().endsWith(".xls"));
	}

	public void testAttemptDupeRule() throws Exception {
		ServiceImplementation impl = getService();
		CategoryItem cat = impl.repository.loadCategory("/");
		cat.addCategory("testAttemptDupeRule", "yeah");

		impl.repository.createPackage("dupes", "yeah");

		impl.createNewRule("testAttemptDupeRule", "ya", "testAttemptDupeRule",
				"dupes", "rule");

		String uuid = impl.createNewRule("testAttemptDupeRule", "ya",
				"testAttemptDupeRule", "dupes", "rule");
		assertEquals("DUPLICATE", uuid);

	}

	public void testCreateNewRule() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testCreateNewRule", "desc");
		impl.createCategory("", "testCreateNewRule", "this is a cat");

		String uuid = impl.createNewRule("testCreateNewRuleName",
				"an initial desc", "testCreateNewRule", "testCreateNewRule",
				AssetFormats.DSL_TEMPLATE_RULE);
		assertNotNull(uuid);
		assertFalse("".equals(uuid));

		AssetItem dtItem = impl.repository.loadAssetByUUID(uuid);
		assertEquals(dtItem.getDescription(), "an initial desc");
	}	

	public void testCreateLinkedAssetItem() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem testCreateNewRuleAsLinkPackage1 = impl.repository.createPackage("testCreateNewRuleAsLinkPackage1", "desc");
		impl.createCategory("", "testCreateNewRuleAsLinkCat1", "this is a cat");
		impl.repository.createPackage("testCreateNewRuleAsLinkPackage2", "desc");
		impl.createCategory("", "testCreateNewRuleAsLinkCat2", "this is a cat");

		//Create the original asset.
		String uuid = impl.createNewRule("testCreateLinkedAssetItemRule",
				"an initial desc", "testCreateNewRuleAsLinkCat1", "testCreateNewRuleAsLinkPackage1",
				AssetFormats.DSL_TEMPLATE_RULE);
		assertNotNull(uuid);
		assertFalse("".equals(uuid));

		AssetItem dtItem = impl.repository.loadAssetByUUID(uuid);
		assertEquals(dtItem.getDescription(), "an initial desc");
		
		//create an asset which is linked to an existing asset. Different package, same category.
		String uuidLink = impl.createNewLinkedRule("testCreateLinkedAssetItemRuleLinked",
				uuid, "testCreateNewRuleAsLinkCat1", "testCreateNewRuleAsLinkPackage2");
		assertNotNull(uuidLink);
		assertFalse("".equals(uuidLink));
		assertFalse(uuidLink.equals(uuid));

		//now verify the linked asset.
		AssetItem itemLink = impl.repository.loadAssetByUUID(uuidLink);
		assertEquals(itemLink.getName(), "testCreateLinkedAssetItemRuleLinked");
		assertEquals(itemLink.getDescription(), "an initial desc");
		assertEquals(itemLink.getFormat(), AssetFormats.DSL_TEMPLATE_RULE);
		assertEquals(itemLink.getPackage().getName(), "testCreateNewRuleAsLinkPackage2");

		assertEquals(itemLink.getPackageName(), "testCreateNewRuleAsLinkPackage2");
		
		assertTrue(itemLink.getCategories().size() == 1);
		assertTrue(itemLink.getCategorySummary().contains("testCreateNewRuleAsLinkCat1"));
		
		//now verify the original asset.
		AssetItem referredItem = impl.repository.loadAssetByUUID(uuid);
		assertEquals(referredItem.getName(), "testCreateLinkedAssetItemRule");
		assertEquals(referredItem.getDescription(), "an initial desc");
		assertEquals(referredItem.getFormat(), AssetFormats.DSL_TEMPLATE_RULE);
		assertEquals(referredItem.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");

		assertTrue(referredItem.getCategories().size() == 1);
		assertTrue(referredItem.getCategorySummary().contains("testCreateNewRuleAsLinkCat1"));		

		
		//create an asset which refers to an existing asset. same package, different category.
		String uuidLink1 = impl.createNewLinkedRule("testCreateLinkedAssetItemRuleLinked1",
				uuid, "testCreateNewRuleAsLinkCat2", "testCreateNewRuleAsLinkPackage1");
		assertNotNull(uuidLink1);
		assertFalse("".equals(uuidLink1));
		assertFalse(uuidLink1.equals(uuid));

		
		//now verify the linked asset.
		AssetItem itemLink1 = impl.repository.loadAssetByUUID(uuidLink1);
		assertEquals(itemLink1.getDescription(), "an initial desc");
		assertEquals(itemLink1.getFormat(), AssetFormats.DSL_TEMPLATE_RULE);
		assertEquals(itemLink1.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");
		assertEquals(itemLink1.getPackageName(), "testCreateNewRuleAsLinkPackage1");
		
		assertTrue(itemLink1.getCategories().size() == 2);
		assertTrue(itemLink1.getCategorySummary().contains("testCreateNewRuleAsLinkCat1"));
		assertTrue(itemLink1.getCategorySummary().contains("testCreateNewRuleAsLinkCat2"));
		
		//now verify the linked asset by calling PackageItem.loadAsset()
		AssetItem itemLinkFromPackage1 = testCreateNewRuleAsLinkPackage1.loadAsset("testCreateLinkedAssetItemRuleLinked1");
		assertEquals(itemLinkFromPackage1.getDescription(), "an initial desc");
		assertEquals(itemLinkFromPackage1.getFormat(), AssetFormats.DSL_TEMPLATE_RULE);
		assertEquals(itemLinkFromPackage1.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");
		assertEquals(itemLinkFromPackage1.getPackageName(), "testCreateNewRuleAsLinkPackage1");
		
		assertTrue(itemLinkFromPackage1.getCategories().size() == 2);
		assertTrue(itemLinkFromPackage1.getCategorySummary().contains("testCreateNewRuleAsLinkCat1"));
		assertTrue(itemLinkFromPackage1.getCategorySummary().contains("testCreateNewRuleAsLinkCat2"));
		
		//now verify the original asset.
		AssetItem referredItem1 = impl.repository.loadAssetByUUID(uuid);
		assertEquals(referredItem1.getDescription(), "an initial desc");
		assertEquals(referredItem1.getFormat(), AssetFormats.DSL_TEMPLATE_RULE);
		assertEquals(referredItem1.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");
		
		assertTrue(referredItem1.getCategories().size() == 2);
		assertTrue(referredItem1.getCategorySummary().contains("testCreateNewRuleAsLinkCat1"));		
		assertTrue(referredItem1.getCategorySummary().contains("testCreateNewRuleAsLinkCat2"));
		
		//now verify AssetItemIterator works by calling search
        AssetItemIterator it = impl.repository.findAssetsByName("testCreateLinkedAssetItemRule%",
                true); 
		assertEquals(3, it.getSize());
		while(it.hasNext()) {
			AssetItem ai = it.next();
			if(ai.getUUID().equals(uuid)) {
				assertEquals(ai.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");
				assertEquals(ai.getDescription(), "an initial desc");
			} else if (ai.getUUID().equals(uuidLink)) {
				assertEquals(ai.getPackage().getName(), "testCreateNewRuleAsLinkPackage2");
				assertEquals(ai.getDescription(), "an initial desc");
			} else if (ai.getUUID().equals(uuidLink1)) {
				assertEquals(ai.getPackage().getName(), "testCreateNewRuleAsLinkPackage1");
				assertEquals(ai.getDescription(), "an initial desc");
			} else {
				fail("unexptected asset found: " + ai.getPackage().getName());
			}
		}
	}
	
	public void testLinkedAssetItemHistoryRelated() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem testCreateNewRuleAsLinkPackage1 = impl.repository.createPackage("testLinkedAssetItemHistoryRelatedPack", "desc");
		impl.createCategory("", "testLinkedAssetItemHistoryRelatedCat", "this is a cat");

		//Create the original asset.
		String uuid = impl.createNewRule("testLinkedAssetItemHistoryRelatedRule",
				"an initial desc", "testLinkedAssetItemHistoryRelatedCat", "testLinkedAssetItemHistoryRelatedPack",
				AssetFormats.DSL_TEMPLATE_RULE);
		
		//create an asset which refers to an existing asset.
		String uuidLink = impl.createNewLinkedRule("testLinkedAssetItemHistoryRelatedRuleLinked",
				uuid, "testLinkedAssetItemHistoryRelatedCat", "testLinkedAssetItemHistoryRelatedPack");
		assertFalse(uuidLink.equals(uuid));

		//create version 1.
		RuleAsset assetWrapper = impl.loadRuleAsset(uuidLink);
		assertEquals(assetWrapper.metaData.description, "an initial desc");
		assetWrapper.metaData.description = "version 1";
		String uuidLink1 = impl.checkinVersion(assetWrapper);
		
		//create version 2
		RuleAsset assetWrapper2 = impl.loadRuleAsset(uuidLink);
		assetWrapper2.metaData.description = "version 2";
		String uuidLink2 = impl.checkinVersion(assetWrapper2);
		
		//create version head
		RuleAsset assetWrapper3 = impl.loadRuleAsset(uuidLink);
		assetWrapper3.metaData.description = "version head";
		String uuidLink3 = impl.checkinVersion(assetWrapper3);

		assertEquals(uuidLink, uuidLink1);
		assertEquals(uuidLink, uuidLink2);

		//verify the history info of LinkedAssetItem
		TableDataResult result = impl.loadAssetHistory(uuidLink);
		assertNotNull(result);
		TableDataRow[] rows = result.data;
		assertEquals(2, rows.length);
		assertFalse(rows[0].id.equals(uuidLink));
		assertFalse(rows[1].id.equals(uuidLink));

		RuleAsset version1 = impl.loadRuleAsset(rows[0].id);
		RuleAsset version2 = impl.loadRuleAsset(rows[1].id);
		RuleAsset versionHead = impl.loadRuleAsset(uuidLink);
		assertFalse(version1.metaData.versionNumber == version2.metaData.versionNumber);
		assertFalse(version1.metaData.versionNumber == versionHead.metaData.versionNumber);
		assertTrue(version1.metaData.description.equals("version 1"));
		assertTrue(version2.metaData.description.equals("version 2"));
		assertTrue(versionHead.metaData.description.equals("version head"));

		//verify the history info of the original AssetItem
		result = impl.loadAssetHistory(uuid);
		assertNotNull(result);
		rows = result.data;
		assertEquals(2, rows.length);
		assertFalse(rows[0].id.equals(uuid));
		assertFalse(rows[1].id.equals(uuid));

		version1 = impl.loadRuleAsset(rows[0].id);
		version2 = impl.loadRuleAsset(rows[1].id);
		versionHead = impl.loadRuleAsset(uuid);
		assertFalse(version1.metaData.versionNumber == version2.metaData.versionNumber);
		assertFalse(version1.metaData.versionNumber == versionHead.metaData.versionNumber);
		assertTrue(version1.metaData.description.equals("version 1"));
		assertTrue(version2.metaData.description.equals("version 2"));
		assertTrue(versionHead.metaData.description.equals("version head"));

		//test restore
		impl.restoreVersion(version1.uuid, versionHead.uuid, "this was cause of a mistake");

		RuleAsset newHead = impl.loadRuleAsset(uuid);

		assertEquals("this was cause of a mistake",
				newHead.metaData.checkinComment);
	}

	public void testCreateNewRuleContainsApostrophe() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testCreateNewRuleContainsApostrophe",
				"desc");
		impl.createCategory("", "testCreateNewRuleContainsApostrophe",
				"this is a cat");

		try {
			impl.createNewRule("testCreateNewRuleContains' character",
					"an initial desc", "testCreateNewRuleContainsApostrophe",
					"testCreateNewRuleContainsApostrophe",
					AssetFormats.DSL_TEMPLATE_RULE);
			fail("did not get expected exception");
		} catch (SerializableException e) {
			assertTrue(e
					.getMessage()
					.indexOf(
							"'testCreateNewRuleContains' character' is not a valid path. ''' not a valid name character") >= 0);
		}
	}

	public void testRuleTableLoad() throws Exception {
		ServiceImplementation impl = getService();
		TableConfig conf = impl
				.loadTableConfig(AssetItemGrid.RULE_LIST_TABLE_ID);
		assertNotNull(conf.headers);
		assertNotNull(conf.headerTypes);

		CategoryItem cat = impl.repository.loadCategory("/");
		cat.addCategory("testRuleTableLoad", "yeah");

		impl.repository.createPackage("testRuleTableLoad", "yeah");
		impl.createNewRule("testRuleTableLoad", "ya", "testRuleTableLoad",
				"testRuleTableLoad", "rule");
		impl.createNewRule("testRuleTableLoad2", "ya", "testRuleTableLoad",
				"testRuleTableLoad", "rule");

		TableDataResult result = impl
				.loadRuleListForCategories("testRuleTableLoad", 0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(2, result.data.length);

		String key = result.data[0].id;
		assertFalse(key.startsWith("testRule"));

		assertEquals(result.data[0].format, "rule");


		assertTrue(result.data[0].values[0].startsWith("testRuleTableLoad"));
	}

	public void testDateFormatting() throws Exception {
		Calendar cal = Calendar.getInstance();
		TableDisplayHandler handler = new TableDisplayHandler(
				AssetItemGrid.RULE_LIST_TABLE_ID);
		String fmt = handler.formatDate(cal);
		assertNotNull(fmt);

		assertTrue(fmt.length() > 8);
	}

	public void testLoadRuleAsset() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testLoadRuleAsset", "desc");
		impl.createCategory("", "testLoadRuleAsset", "this is a cat");

		impl.createNewRule("testLoadRuleAsset", "description",
				"testLoadRuleAsset", "testLoadRuleAsset", AssetFormats.DRL);

		TableDataResult res = impl
				.loadRuleListForCategories("testLoadRuleAsset", 0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(1, res.data.length);
		assertEquals(-1, res.total);
		assertTrue(res.currentPosition > 0);
		assertFalse(res.hasNext);

		TableDataRow row = res.data[0];
		String uuid = row.id;

		RuleAsset asset = impl.loadRuleAsset(uuid);
		assertNotNull(asset);

		assertEquals(uuid, asset.uuid);

		assertEquals("description", asset.metaData.description);

		assertNotNull(asset.content);
		assertTrue(asset.content instanceof RuleContentText);
		assertEquals("testLoadRuleAsset", asset.metaData.name);
		assertEquals("testLoadRuleAsset", asset.metaData.title);
		assertEquals("testLoadRuleAsset", asset.metaData.packageName);
		assertEquals(AssetFormats.DRL, asset.metaData.format);
		assertNotNull(asset.metaData.createdDate);

		assertEquals(1, asset.metaData.categories.length);
		assertEquals("testLoadRuleAsset", asset.metaData.categories[0]);

		AssetItem rule = impl.repository.loadPackage("testLoadRuleAsset")
				.loadAsset("testLoadRuleAsset");
		impl.repository.createState("whee");
		rule.updateState("whee");
		rule.checkin("changed state");
		asset = impl.loadRuleAsset(uuid);

		assertEquals("whee", asset.metaData.status);
		assertEquals("changed state", asset.metaData.checkinComment);

		uuid = impl.createNewRule("testBRLFormatSugComp", "description",
				"testLoadRuleAsset", "testLoadRuleAsset",
				AssetFormats.BUSINESS_RULE);
		asset = impl.loadRuleAsset(uuid);
		assertTrue(asset.content instanceof RuleModel);

		uuid = impl.createNewRule("testLoadRuleAssetBRL", "description",
				"testLoadRuleAsset", "testLoadRuleAsset",
				AssetFormats.DSL_TEMPLATE_RULE);
		asset = impl.loadRuleAsset(uuid);
		assertTrue(asset.content instanceof RuleContentText);
	}

    public void testTrackRecentOpenedChanged() throws Exception {
        ServiceImplementation impl = getService();
        UserInbox ib = new UserInbox(impl.repository);
        ib.clearAll();
        impl.repository.createPackage("testTrackRecentOpenedChanged", "desc");
        impl.createCategory("", "testTrackRecentOpenedChanged", "this is a cat");

        String id = impl.createNewRule("myrule", "desc", "testTrackRecentOpenedChanged", "testTrackRecentOpenedChanged", "drl");
        RuleAsset ass = impl.loadRuleAsset(id);

        impl.checkinVersion(ass);

        List<UserInbox.InboxEntry> es = ib.loadRecentEdited();
        assertEquals(1, es.size());
        assertEquals(ass.uuid, es.get(0).assetUUID);
        assertEquals(ass.metaData.name, es.get(0).note);

        ib.clearAll();

        impl.loadRuleAsset(ass.uuid);
        es = ib.loadRecentEdited();
        assertEquals(0, es.size());

        //now check they have it in their opened list...
        es = ib.loadRecentOpened();
        assertEquals(1, es.size());
        assertEquals(ass.uuid, es.get(0).assetUUID);
        assertEquals(ass.metaData.name, es.get(0).note);
        
        assertEquals(0, ib.loadRecentEdited().size());
    }

	public void testLoadAssetHistoryAndRestore() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testLoadAssetHistory", "desc");
		impl.createCategory("", "testLoadAssetHistory", "this is a cat");

		String uuid = impl.createNewRule("testLoadAssetHistory", "description",
				"testLoadAssetHistory", "testLoadAssetHistory", AssetFormats.DRL);
		RuleAsset asset = impl.loadRuleAsset(uuid);
		impl.checkinVersion(asset); // 1
		asset = impl.loadRuleAsset(uuid);
		impl.checkinVersion(asset); // 2
		asset = impl.loadRuleAsset(uuid);
		impl.checkinVersion(asset); // HEAD

		TableDataResult result = impl.loadAssetHistory(uuid);
		assertNotNull(result);
		TableDataRow[] rows = result.data;
		assertEquals(2, rows.length);
		assertFalse(rows[0].id.equals(uuid));
		assertFalse(rows[1].id.equals(uuid));

		RuleAsset old = impl.loadRuleAsset(rows[0].id);
		RuleAsset newer = impl.loadRuleAsset(rows[1].id);
		assertFalse(old.metaData.versionNumber == newer.metaData.versionNumber);

		RuleAsset head = impl.loadRuleAsset(uuid);

		long oldVersion = old.metaData.versionNumber;
		assertFalse(oldVersion == head.metaData.versionNumber);

		impl.restoreVersion(old.uuid, head.uuid, "this was cause of a mistake");

		RuleAsset newHead = impl.loadRuleAsset(uuid);

		assertEquals("this was cause of a mistake",
				newHead.metaData.checkinComment);

	}

	public void testCheckin() throws Exception {
		ServiceImplementation serv = getService();

        UserInbox ib = new UserInbox(serv.repository);
        List<UserInbox.InboxEntry> inbox = ib.loadRecentEdited();

		serv.listPackages();

		serv
				.createCategory("/", "testCheckinCategory",
						"this is a description");
		serv.createCategory("/", "testCheckinCategory2",
				"this is a description");
		serv.createCategory("testCheckinCategory", "deeper", "description");

		String uuid = serv.createNewRule("testChecking",
				"this is a description", "testCheckinCategory",
				RulesRepository.DEFAULT_PACKAGE, AssetFormats.DRL);

		RuleAsset asset = serv.loadRuleAsset(uuid);

		assertNotNull(asset.metaData.lastModifiedDate);

		asset.metaData.coverage = "boo";
		asset.content = new RuleContentText();
		((RuleContentText) asset.content).content = "yeah !";

		Date start = new Date();
		Thread.sleep(100);

		String uuid2 = serv.checkinVersion(asset);
		assertEquals(uuid, uuid2);


        
        assertTrue(ib.loadRecentEdited().size() > inbox.size());


		RuleAsset asset2 = serv.loadRuleAsset(uuid);
		assertNotNull(asset2.metaData.lastModifiedDate);
		assertTrue(asset2.metaData.lastModifiedDate.after(start));

		assertEquals("boo", asset2.metaData.coverage);
		assertEquals(1, asset2.metaData.versionNumber);

		assertEquals("yeah !", ((RuleContentText) asset2.content).content);

		asset2.metaData.coverage = "ya";
		asset2.metaData.checkinComment = "checked in";

		String cat = asset2.metaData.categories[0];
		asset2.metaData.categories = new String[3];
		asset2.metaData.categories[0] = cat;
		asset2.metaData.categories[1] = "testCheckinCategory2";
		asset2.metaData.categories[2] = "testCheckinCategory/deeper";

		serv.checkinVersion(asset2);

		asset2 = serv.loadRuleAsset(uuid);
		assertEquals("ya", asset2.metaData.coverage);
		assertEquals(2, asset2.metaData.versionNumber);
		assertEquals("checked in", asset2.metaData.checkinComment);
		assertEquals(3, asset2.metaData.categories.length);
		assertEquals("testCheckinCategory", asset2.metaData.categories[0]);
		assertEquals("testCheckinCategory2", asset2.metaData.categories[1]);
		assertEquals("testCheckinCategory/deeper",
				asset2.metaData.categories[2]);

		// now lets try a concurrent edit of an asset.
		// asset3 will be loaded and edited, and then asset2 will try to
		// clobber, it, which should fail.
		// as it is optimistically locked.
		RuleAsset asset3 = serv.loadRuleAsset(asset2.uuid);
		asset3.metaData.subject = "new sub";
		serv.checkinVersion(asset3);

		asset3 = serv.loadRuleAsset(asset2.uuid);
		assertFalse(asset3.metaData.versionNumber == asset2.metaData.versionNumber);

		String result = serv.checkinVersion(asset2);
		assertTrue(result.startsWith("ERR"));
		System.err.println(result.substring(5));

	}

	public void testArchivePackage() throws Exception {
		ServiceImplementation impl = getService();

		PackageConfigData[] pkgs = impl.listPackages();

		PackageConfigData[] arch = impl.listArchivedPackages();

		String uuid = impl.createPackage("testCreateArchivedPackage",
				"this is a new package");


		PackageItem item = impl.repository
				.loadPackage("testCreateArchivedPackage");
		TableDataResult td = impl.loadArchivedAssets(0, 1000);

		item.archiveItem(true);



		TableDataResult td2 = impl.loadArchivedAssets(0, 1000);
		assertEquals(td2.data.length, td.data.length);

		PackageConfigData[] arch2 = impl.listArchivedPackages();
		assertEquals(arch2.length, arch.length + 1);



		assertEquals(pkgs.length, impl.listPackages().length);

		item.archiveItem(false);
		arch2 = impl.listArchivedPackages();
		assertEquals(arch2.length, arch.length);
	}

	public void testCreatePackage() throws Exception {
		ServiceImplementation impl = getService();
		PackageConfigData[] pkgs = impl.listPackages();
		String uuid = impl.createPackage("testCreatePackage",
				"this is a new package");
		assertNotNull(uuid);

		PackageItem item = impl.repository.loadPackage("testCreatePackage");
		assertNotNull(item);
		assertEquals("this is a new package", item.getDescription());

		assertEquals(pkgs.length + 1, impl.listPackages().length);

		PackageConfigData conf = impl.loadPackageConfig(uuid);
		assertEquals("this is a new package", conf.description);
		assertNotNull(conf.lastModified);

		pkgs = impl.listPackages();

		impl.copyPackage("testCreatePackage", "testCreatePackage_COPY");

		assertEquals(pkgs.length + 1, impl.listPackages().length);
		try {
			impl.copyPackage("testCreatePackage", "testCreatePackage_COPY");
		} catch (RulesRepositoryException e) {
			assertNotNull(e.getMessage());
		}
	}

	public void testLoadPackageConfig() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem it = impl.repository.loadDefaultPackage();
		String uuid = it.getUUID();
		it.updateCoverage("xyz");
		it.updateExternalURI("ext");
		ServiceImplementation.updateDroolsHeader("header", it);
		impl.repository.save();

		PackageConfigData data = impl.loadPackageConfig(uuid);
		assertNotNull(data);

		assertEquals(RulesRepository.DEFAULT_PACKAGE, data.name);
		assertEquals("header", data.header);
		assertEquals("ext", data.externalURI);

		assertNotNull(data.uuid);
		assertFalse(data.isSnapshot);

		assertNotNull(data.dateCreated);
		Date original = data.lastModified;

		Thread.sleep(100);

		impl.createPackageSnapshot(RulesRepository.DEFAULT_PACKAGE,
				"TEST SNAP 2.0", false, "ya");
		PackageItem loaded = impl.repository.loadPackageSnapshot(
				RulesRepository.DEFAULT_PACKAGE, "TEST SNAP 2.0");

		data = impl.loadPackageConfig(loaded.getUUID());
		assertTrue(data.isSnapshot);
		assertEquals("TEST SNAP 2.0", data.snapshotName);
		assertFalse(original.equals(data.lastModified));
		assertEquals("ya", data.checkinComment);
	}
	
	public void testArchiveAndUnarchivePackageAndHeader() throws Exception {
        ServiceImplementation impl = getService();
        String uuid = impl.createPackage( "testArchiveAndUnarchivePackageAndHeader",
                                          "a desc" );
        PackageConfigData data = impl.loadPackageConfig( uuid );
        PackageItem it = impl.repository.loadPackageByUUID( uuid );
        data.archived = true;
        
        AssetItem rule1 = it.addAsset("rule_1", "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1
                .updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
        rule1.archiveItem( true );
        rule1.checkin("");
        impl.repository.save();
        
        impl.savePackage( data );
        data = impl.loadPackageConfig( uuid );
        it = impl.repository.loadPackage( data.name );
        assertTrue( data.archived );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.archived = false;

        impl.savePackage( data );
        data = impl.loadPackageConfig( uuid );
        it = impl.repository.loadPackage( data.name );
        assertFalse( data.archived );
        assertFalse( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

        data.archived = true;

        impl.savePackage( data );
        data = impl.loadPackageConfig( uuid );
        it = impl.repository.loadPackage( data.name );
        assertTrue( data.archived );
        assertTrue( it.loadAsset( "drools" ).isArchived() );
        assertTrue( it.loadAsset( "rule_1" ).isArchived() );

    }

	public void testPackageConfSave() throws Exception {
		RepositoryService impl = getService();
		String uuid = impl.createPackage("testPackageConfSave", "a desc");
		PackageConfigData data = impl.loadPackageConfig(uuid);

		data.description = "new desc";
		data.header = "wa";
		data.externalURI = "new URI";

		ValidatedResponse res = impl.savePackage(data);
		assertNotNull(res);
		assertTrue(res.hasErrors);
		assertNotNull(res.errorMessage);

		data = impl.loadPackageConfig(uuid);
		assertEquals("new desc", data.description);
		assertEquals("wa", data.header);
		assertEquals("new URI", data.externalURI);

		data.header = "";
		res = impl.savePackage(data);
		if (res.hasErrors) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(res.errorMessage);
			System.out
					.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}

		assertFalse(res.hasErrors);
	}

	public void testListByFormat() throws Exception {
		RepositoryService impl = getService();
		String cat = "testListByFormat";
		impl.createCategory("/", cat, "ya");
		String pkgUUID = impl.createPackage("testListByFormat",
				"used for listing by format.");

		String uuid = impl.createNewRule("testListByFormat", "x", cat,
				"testListByFormat", "testListByFormat");
		String uuid2 = impl.createNewRule("testListByFormat2", "x", cat,
				"testListByFormat", "testListByFormat");
		String uuid3 = impl.createNewRule("testListByFormat3", "x", cat,
				"testListByFormat", "testListByFormat");
		String uuid4 = impl.createNewRule("testListByFormat4", "x", cat,
				"testListByFormat", "testListByFormat");

		TableDataResult res = impl.listAssets(pkgUUID, arr("testListByFormat"),
				0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);
		assertEquals(uuid, res.data[0].id);
		assertEquals("testListByFormat", res.data[0].values[0]);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 0, 4, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 0, 2, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(2, res.data.length);
		assertEquals(uuid, res.data[0].id);
		assertEquals(4, res.total);
		assertTrue(res.hasNext);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 2, 2, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(2, res.data.length);
		assertEquals(uuid3, res.data[0].id);
		assertEquals(4, res.total);
		assertFalse(res.hasNext);

		uuid = impl.createNewRule("testListByFormat5", "x", cat,
				"testListByFormat", "otherFormat");

		res = impl.listAssets(pkgUUID, arr("otherFormat"), 0, 40, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(1, res.data.length);
		assertEquals(uuid, res.data[0].id);

		res = impl.listAssets(pkgUUID, new String[] { "otherFormat",
				"testListByFormat" }, 0, 40, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(5, res.data.length);

		TableDataResult result = impl.quickFindAsset("testListByForma", 5,
				false);
		assertEquals(5, result.data.length);

		assertNotNull(result.data[0].id);
		assertTrue(result.data[0].values[0].startsWith("testListByFormat"));

		result = impl.quickFindAsset("testListByForma", 3, false);
		assertEquals(4, result.data.length);

		assertEquals("MORE", result.data[3].id);

	}


	public void testListUnregisteredAssetFormats() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem pkg = impl.repository.createPackage("testListUnregisteredAssetFormats", "");
		AssetItem as = pkg.addAsset("whee", "");
		as.updateFormat(AssetFormats.DRL);
		as.checkin("");

		as = pkg.addAsset("whee2", "");
		as.updateFormat("something_silly");
		as.checkin("");

		TableDataResult res = impl.listAssets(pkg.getUUID(), new String[0], 0, 40, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(1, res.data.length);
	}


	public void testQuickFind() throws Exception  {
		RepositoryService impl = getService();
		String cat = "testQuickFind";
		impl.createCategory("/", cat, "qkfnd");
		impl.createPackage("testQuickFind",
				"for testing quick find.");
		String uuid = impl.createNewRule("testQuickFindmyRule1", "desc", cat, "testQuickFind", AssetFormats.DRL);
		TableDataResult res = impl.quickFindAsset("testQuickFindmyRule", 20, false);
		assertEquals(1, res.data.length);

		impl.createNewRule("testQuickFindmyRule2", "desc", cat, "testQuickFind", AssetFormats.DRL);
		res = impl.quickFindAsset("testQuickFindmyRule", 20, false);
		assertEquals(2, res.data.length);

		impl.copyAsset(uuid, "testQuickFind", "testQuickFindmyRule3");
		res = impl.quickFindAsset("testQuickFindmyRule", 20, false);
		assertEquals(3, res.data.length);

		res = impl.quickFindAsset("testQuickFindm*Rule", 20, false);
		assertEquals(3, res.data.length);


	}

	public void testSearchText() throws Exception  {
		ServiceImplementation impl = getService();
		String cat = "testTextSearch";
		impl.createCategory("/", cat, "qkfnd");
		impl.createPackage("testTextSearch",
				"for testing search.");
		String uuid = impl.createNewRule("testTextRule1", "desc", cat, "testTextSearch", AssetFormats.DRL);
		TableDataResult res = impl.queryFullText("testTextRule1", false, 0, -1);
		assertEquals(1, res.data.length);
	}

	public void testSearchMetaData() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem pkg = impl.repository.createPackage("testMetaDataSearch", "");

		AssetItem asset = pkg.addAsset("testMetaDataSearchAsset", "");
		asset.updateSubject("testMetaDataSearch");
		asset.updateExternalSource("numberwang");
		asset.checkin("");

		MetaDataQuery[] qr = new MetaDataQuery[2];
		qr[0] = new MetaDataQuery();
		qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
		qr[0].valueList = "wang, testMetaDataSearch";
		qr[1] = new MetaDataQuery();
		qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
		qr[1].valueList = "numberwan*";
		TableDataResult res = impl.queryMetaData(qr, DateUtils.parseDate("10-Jul-1974"), null, null,null, false, 0, -1);
		assertEquals(1, res.data.length);

	}


	public String[] arr(String s) {
		return new String[] { s };
	}

	public void testStatus() throws Exception {
		RepositoryService impl = getService();
		String uuid = impl.createState("testStatus1");
		assertNotNull(uuid);

		String[] states = impl.listStates();
		assertTrue(states.length > 0);

		impl.createState("testStatus2");
		String[] states2 = impl.listStates();
		assertEquals(states.length + 1, states2.length);

		int match = 0;
		for (int i = 0; i < states2.length; i++) {
			if (states2[i].equals("testStatus2")) {
				match++;
			} else if (states2[i].equals("testStatus1")) {
				match++;
			}
		}

		assertEquals(2, match);

		String packagUUID = impl.createPackage("testStatus", "description");
		String ruleUUID = impl.createNewRule("testStatus", "desc", null,
				"testStatus", AssetFormats.DRL);
		String ruleUUID2 = impl.createNewRule("testStatus2", "desc", null,
				"testStatus", AssetFormats.DRL);
		impl.createState("testState");

		RuleAsset asset = impl.loadRuleAsset(ruleUUID);
		assertEquals(StateItem.DRAFT_STATE_NAME, asset.metaData.status);
		impl.changeState(ruleUUID, "testState", false);
		asset = impl.loadRuleAsset(ruleUUID);
		assertEquals("testState", asset.metaData.status);
		asset = impl.loadRuleAsset(ruleUUID2);
		assertEquals(StateItem.DRAFT_STATE_NAME, asset.metaData.status);

		impl.createState("testState2");
		impl.changeState(packagUUID, "testState2", true);

		PackageConfigData pkg = impl.loadPackageConfig(packagUUID);
		assertEquals("testState2", pkg.state);

		asset = impl.loadRuleAsset(ruleUUID2);
		assertEquals("testState2", asset.metaData.status);

		impl.checkinVersion(asset);
		asset = impl.loadRuleAsset(asset.uuid);
		assertEquals("testState2", asset.metaData.status);

	}

	public void testMovePackage() throws Exception {
		RepositoryService impl = getService();
		String[] cats = impl.loadChildCategories("/");
		if (cats.length == 0) {
			impl.createCategory("/", "la", "d");
		}
		String sourcePkgId = impl.createPackage("sourcePackage", "description");
		String destPkgId = impl.createPackage("targetPackage", "description");

		String cat = impl.loadChildCategories("/")[0];

		String uuid = impl.createNewRule("testMovePackage", "desc", cat,
				"sourcePackage", AssetFormats.DRL);

		TableDataResult res = impl.listAssets(destPkgId,
				new String[] { "drl" }, 0, 2, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(0, res.data.length);

		impl.changeAssetPackage(uuid, "targetPackage", "yeah");
		res = impl.listAssets(destPkgId, new String[] { "drl" }, 0, 2, AssetItemGrid.RULE_LIST_TABLE_ID);

		assertEquals(1, res.data.length);

		res = impl.listAssets(sourcePkgId, new String[] { "drl" }, 0, 2,AssetItemGrid.RULE_LIST_TABLE_ID);

		assertEquals(0, res.data.length);

	}

	public void testCopyAsset() throws Exception {
		RepositoryService impl = getService();
		impl.createCategory("/", "templates", "ya");
		String uuid = impl.createNewRule("testCopyAsset", "", "templates",
				RulesRepository.DEFAULT_PACKAGE, AssetFormats.DRL);
		String uuid2 = impl.copyAsset(uuid, RulesRepository.DEFAULT_PACKAGE,
				"testCopyAsset2");
		assertNotSame(uuid, uuid2);

		RuleAsset asset = impl.loadRuleAsset(uuid2);
		assertNotNull(asset);
		assertEquals(RulesRepository.DEFAULT_PACKAGE,
				asset.metaData.packageName);
		assertEquals("testCopyAsset2", asset.metaData.name);
	}

	public void testSnapshot() throws Exception {
		RepositoryService impl = getService();
		impl.createCategory("/", "snapshotTesting", "y");
		impl.createPackage("testSnapshot", "d");
		String uuid = impl.createNewRule("testSnapshotRule", "",
				"snapshotTesting", "testSnapshot", AssetFormats.DRL);

		impl.createPackageSnapshot("testSnapshot", "X", false, "ya");
		SnapshotInfo[] snaps = impl.listSnapshots("testSnapshot");
		assertEquals(1, snaps.length);
		assertEquals("X", snaps[0].name);
		assertEquals("ya", snaps[0].comment);
		assertNotNull(snaps[0].uuid);
		PackageConfigData confSnap = impl.loadPackageConfig(snaps[0].uuid);
		assertEquals("testSnapshot", confSnap.name);

		impl.createPackageSnapshot("testSnapshot", "Y", false, "we");
		assertEquals(2, impl.listSnapshots("testSnapshot").length);
		impl.createPackageSnapshot("testSnapshot", "X", true, "we");
		assertEquals(2, impl.listSnapshots("testSnapshot").length);

		impl.copyOrRemoveSnapshot("testSnapshot", "X", false, "Q");
		assertEquals(3, impl.listSnapshots("testSnapshot").length);

		try {
			impl.copyOrRemoveSnapshot("testSnapshot", "X", false, "");
			fail("should not be able to copy snapshot to empty detination");
		} catch (SerializableException e) {
			assertNotNull(e.getMessage());
		}

		impl.copyOrRemoveSnapshot("testSnapshot", "X", true, null);
		assertEquals(2, impl.listSnapshots("testSnapshot").length);

	}

	public void testSnapshotRebuild() throws Exception {

		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// get rid of other snapshot crap
		Iterator pkit = repo.listPackages();
		while (pkit.hasNext()) {
			PackageItem pkg = (PackageItem) pkit.next();
			String[] snaps = repo.listPackageSnapshots(pkg.getName());
			for (String snapName : snaps) {
				repo.removePackageSnapshot(pkg.getName(), snapName);
			}
		}

		final PackageItem pkg = repo.createPackage("testSnapshotRebuild", "");
		ServiceImplementation.updateDroolsHeader("import java.util.List", pkg);
		repo.save();

		AssetItem item = pkg.addAsset("anAsset", "");
		item.updateFormat(AssetFormats.DRL);
		item
				.updateContent(" rule abc \n when \n then \n System.out.println(42); \n end");
		item.checkin("");

		BuilderResult[] res = impl.buildPackage(pkg.getUUID(), true, null, null, null, "");
		assertNull(res);

		impl.createPackageSnapshot("testSnapshotRebuild", "SNAP", false, "");

		PackageItem snap = repo.loadPackageSnapshot("testSnapshotRebuild",
				"SNAP");
		long snapTime = snap.getLastModified().getTimeInMillis();

		Thread.sleep(100);

		impl.rebuildSnapshots();

		PackageItem snap_ = repo.loadPackageSnapshot("testSnapshotRebuild", "SNAP");
		long newTime = snap_.getLastModified().getTimeInMillis();

		assertTrue(newTime > snapTime);

		item.updateContent("garbage");
		item.checkin("");

		impl.createPackageSnapshot("testSnapshotRebuild", "SNAP2", false, "");

		try {
			impl.rebuildSnapshots();
		} catch (DetailedSerializableException e) {
			assertNotNull(e.getMessage());
			assertNotNull(e.getLongDescription());
		}



	}

	public void testPackageRebuild() throws Exception {

		ServiceImplementation impl = getService();

		RulesRepository repo = impl.repository;


		final PackageItem pkg = repo.createPackage("testPackageRebuild", "");
		ServiceImplementation.updateDroolsHeader("import java.util.List", pkg);
		repo.save();

		AssetItem item = pkg.addAsset("anAsset", "");
		item.updateFormat(AssetFormats.DRL);
		item.updateContent(" rule abc \n when \n then \n System.out.println(42); \n end");
		item.checkin("");

		assertNull(pkg.getCompiledPackageBytes());

		long last = pkg.getLastModified().getTimeInMillis();
		Thread.sleep(100);
		try {
			impl.rebuildPackages();
		} catch (DetailedSerializableException e) {
			assertNotNull(e.getMessage());
			assertNotNull(e.getLongDescription());
		}

		assertFalse(pkg.getLastModified().getTimeInMillis() == last);
		assertNotNull(pkg.getCompiledPackageBytes());




	}

	public void testRemoveCategory() throws Exception {

		RepositoryService impl = getService();
		String[] children = impl.loadChildCategories("/");
		impl.createCategory("/", "testRemoveCategory", "foo");


		impl.removeCategory("testRemoveCategory");
		String[] _children = impl.loadChildCategories("/");
		assertEquals(children.length, _children.length);



	}

	public void testRemoveAsset() throws Exception {
		RepositoryService impl = getService();
		String cat = "testRemoveAsset";
		impl.createCategory("/", cat, "ya");
		String pkgUUID = impl.createPackage("testRemoveAsset", "");

		String uuid = impl.createNewRule("testRemoveAsset", "x", cat,
				"testRemoveAsset", "testRemoveAsset");

		String uuid2 = impl.createNewRule("testRemoveAsset2", "x", cat,
				"testRemoveAsset", "testRemoveAsset");

		String uuid3 = impl.createNewRule("testRemoveAsset3", "x", cat,
				"testRemoveAsset", "testRemoveAsset");
		String uuid4 = impl.createNewRule("testRemoveAsset4", "x", cat,
				"testRemoveAsset", "testRemoveAsset");

		TableDataResult res = impl.listAssets(pkgUUID, arr("testRemoveAsset"),
				0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);

		impl.removeAsset(uuid4);

		res = impl.listAssets(pkgUUID, arr("testRemoveAsset"), 0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(3, res.data.length);
	}

	public void testRemovePackage() throws Exception {
		ServiceImplementation impl = getService();
		int n = impl.listPackages().length;
		PackageItem p = impl.repository.createPackage("testRemovePackage", "");
		assertNotNull(impl.loadPackageConfig(p.getUUID()));

		impl.removePackage(p.getUUID());
		assertEquals(n, impl.listPackages().length);
	}


	public void testArchiveAsset() throws Exception {
		RepositoryService impl = getService();
		String cat = "testArchiveAsset";
		impl.createCategory("/", cat, "ya");
		String pkgUUID = impl.createPackage("testArchiveAsset", "");

		String uuid = impl.createNewRule("testArchiveAsset", "x", cat,
				"testArchiveAsset", "testArchiveAsset");

		String uuid2 = impl.createNewRule("testArchiveAsset2", "x", cat,
				"testArchiveAsset", "testArchiveAsset");

		String uuid3 = impl.createNewRule("testArchiveAsset3", "x", cat,
				"testArchiveAsset", "testArchiveAsset");
		String uuid4 = impl.createNewRule("testArchiveAsset4", "x", cat,
				"testArchiveAsset", "testArchiveAsset");

		TableDataResult res = impl.listAssets(pkgUUID, arr("testArchiveAsset"),
				0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);
		assertEquals(4, res.total);
		assertFalse(res.hasNext);

		TableDataResult td = impl.loadArchivedAssets(0, 1000);
		assertEquals(-1, td.total);
		impl.archiveAsset(uuid4, true);

		TableDataResult td2 = impl.loadArchivedAssets(0, 1000);
		assertTrue(td2.data.length == td.data.length + 1);

		res = impl.listAssets(pkgUUID, arr("testArchiveAsset"), 0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(3, res.data.length);

		impl.archiveAsset(uuid4, false);

		res = impl.listAssets(pkgUUID, arr("testArchiveAsset"), 0, -1, AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);




	}
	
	public void testArchiveAssetWhenParentPackageArchived() throws Exception {
		ServiceImplementation impl = getService();
		String packageName = "testArchiveAssetWhenParentPackageArchived";
		String cat = packageName;
		impl.createCategory("/", cat, "ya");
		String pkgUUID = impl.createPackage(packageName, "");

		String uuid = impl.createNewRule(packageName, "x", cat, packageName,
				packageName);

		String uuid2 = impl.createNewRule(
				"testArchiveAssetWhenParentPackageArchived2", "x", cat,
				packageName, packageName);

		String uuid3 = impl.createNewRule(
				"testArchiveAssetWhenParentPackageArchived3", "x", cat,
				packageName, packageName);
		String uuid4 = impl.createNewRule(
				"testArchiveAssetWhenParentPackageArchived4", "x", cat,
				packageName, packageName);

		TableDataResult res = impl.listAssets(pkgUUID, arr(packageName), 0, -1,
				AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(4, res.data.length);
		assertEquals(4, res.total);
		assertFalse(res.hasNext);

		TableDataResult td = impl.loadArchivedAssets(0, 1000);
		assertEquals(-1, td.total);
		impl.archiveAsset(uuid4, true);
		PackageItem packageItem = impl.repository.loadPackage(packageName);
		packageItem.archiveItem(true);

		TableDataResult td2 = impl.loadArchivedAssets(0, 1000);
		assertTrue(td2.data.length == td.data.length + 1);

		res = impl.listAssets(pkgUUID, arr(packageName), 0, -1,
				AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(3, res.data.length);

		try {
			impl.archiveAsset(uuid4, false);
			fail("Should throw an exception");
		} catch (RulesRepositoryException e) {
			// Works
		}
		
		res = impl.listAssets(pkgUUID, arr(packageName), 0, -1,
				AssetItemGrid.RULE_LIST_TABLE_ID);
		assertEquals(3, res.data.length);

	}

	public void testLoadSuggestionCompletionEngine() throws Exception {
        ServiceImplementation impl = getService();
        RulesRepository repo = impl.repository;

        // create our package
        PackageItem pkg = repo.createPackage("testSILoadSCE", "");

        AssetItem model = pkg.addAsset("MyModel", "");
        model.updateFormat(AssetFormats.MODEL);
        model.updateBinaryContentAttachment(this.getClass()
                .getResourceAsStream("/billasurf.jar"));
        model.checkin("");
        ServiceImplementation.updateDroolsHeader("import com.billasurf.Board", pkg);

        AssetItem m2 = pkg.addAsset("MyModel2", "");
        m2.updateFormat(AssetFormats.DRL_MODEL);
        m2.updateContent("declare Whee\n name: String\nend");
        m2.checkin("");


        AssetItem r1 = pkg.addAsset("garbage", "");
        r1.updateFormat(AssetFormats.DRL);
        r1.updateContent("this will not compile");
        r1.checkin("");



        SuggestionCompletionEngine eng = impl
                .loadSuggestionCompletionEngine(pkg.getName());
        assertNotNull(eng);
        assertEquals(2, eng.factTypes.length);

        for (String ft : eng.factTypes) {
            if (!(ft.equals("Board") || ft.equals("Whee"))) {
                fail("Should be one of the above...");
            }
        }
	}


    public void testDiscussion() throws Exception {
        ServiceImplementation impl = getService();
        RulesRepository repo = impl.repository;

        PackageItem pkg = repo.createPackage("testDiscussionFeature", "");
        AssetItem rule1 = pkg.addAsset("rule_1", "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
        rule1.checkin("");
        repo.save();

        List<DiscussionRecord> dr = impl.loadDiscussionForAsset(rule1.getUUID());
        assertEquals(0, dr.size());

        List<DiscussionRecord> dr_ = impl.addToDiscussionForAsset(rule1.getUUID(), "This is a note");
        assertEquals(1, dr_.size()) ;
        assertNotNull(dr_.get(0).author);
        assertEquals("This is a note", dr_.get(0).note);
        Thread.sleep(100);
        impl.addToDiscussionForAsset(rule1.getUUID(), "This is a note2");


        List<DiscussionRecord> d_ = impl.loadDiscussionForAsset(rule1.getUUID());
        assertEquals(2, d_.size());

        assertEquals("This is a note", d_.get(0).note);
        assertEquals("This is a note2", d_.get(1).note);
        assertTrue(d_.get(1).timestamp > d_.get(0).timestamp);



        rule1.updateContent("some more content");
        rule1.checkin("");


        impl.addToDiscussionForAsset(rule1.getUUID(), "This is a note2");
        d_ = impl.loadDiscussionForAsset(rule1.getUUID());
        assertEquals(3, d_.size());

        assertEquals("This is a note", d_.get(0).note);
        assertEquals("This is a note2", d_.get(1).note);

        impl.clearAllDiscussionsForAsset(rule1.getUUID());
        d_ = impl.loadDiscussionForAsset(rule1.getUUID());
        assertEquals(0, d_.size());



        impl.addToDiscussionForAsset(rule1.getUUID(), "This is a note2");
        d_ = impl.loadDiscussionForAsset(rule1.getUUID());
        assertEquals(1, d_.size());
    }



	/**
	 * This will test creating a package, check it compiles, and can exectute
	 * rules, then take a snapshot, and check that it reports errors.
	 */
	public void testBinaryPackageCompileAndExecute() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBinaryPackageCompile", "");
		ServiceImplementation.updateDroolsHeader("global java.util.List ls \n import org.drools.Person", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(),true);
		assertNull(results);

		pkg = repo.loadPackage("testBinaryPackageCompile");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		assertNotNull(binPackage);

		Package binPkg = (Package) DroolsStreamUtils.streamIn(binPackage);

		assertNotNull(binPkg);
		assertTrue(binPkg.isValid());

		Person p = new Person();

		BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
		loader.addPackage(new ByteArrayInputStream(binPackage));
		RuleBase rb = loader.getRuleBase();

		StatelessSession sess = rb.newStatelessSession();
        sess.setGlobal("ls", new ArrayList());
		sess.execute(p);

		assertEquals(42, p.getAge());

		impl.createPackageSnapshot("testBinaryPackageCompile", "SNAP1", false,
				"");

		rule1
				.updateContent("rule 'rule1' \n when p:PersonX() \n then System.err.println(42); \n end");
		rule1.checkin("");

		results = impl.buildPackage(pkg.getUUID(), true);
		assertNotNull(results);
		assertEquals(1, results.length);
		assertEquals(rule1.getName(), results[0].assetName);
		assertEquals(AssetFormats.DRL, results[0].assetFormat);
		assertNotNull(results[0].message);
		assertEquals(rule1.getUUID(), results[0].uuid);

		pkg = repo.loadPackageSnapshot("testBinaryPackageCompile", "SNAP1");
		results = impl.buildPackage(pkg.getUUID(),true);
		assertNull(results);

	}

	/**
	 * This will test creating a package with a BRL rule, check it compiles, and
	 * can exectute rules, then take a snapshot, and check that it reports
	 * errors.
	 */
	public void testBinaryPackageCompileAndExecuteWithBRXML() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBinaryPackageCompileBRL", "");
		ServiceImplementation.updateDroolsHeader("import org.drools.Person", pkg);
		AssetItem rule2 = pkg.addAsset("rule2", "");
		rule2.updateFormat(AssetFormats.BUSINESS_RULE);

		RuleModel model = new RuleModel();
		model.name = "rule2";
		FactPattern pattern = new FactPattern("Person");

		SingleFieldConstraint con = new SingleFieldConstraint();
		con.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
		con.value = "name soundslike 'foobar'";
		pattern.addConstraint(con);

		pattern.boundName = "p";
		ActionSetField action = new ActionSetField("p");
		ActionFieldValue value = new ActionFieldValue("age", "42",
				SuggestionCompletionEngine.TYPE_NUMERIC);
		action.addFieldValue(value);

		model.addLhsItem(pattern);
		model.addRhsItem(action);

		rule2.updateContent(BRXMLPersistence.getInstance().marshal(model));
		rule2.checkin("");
		repo.save();

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), true);
		if (results != null) {
			for (int i = 0; i < results.length; i++) {
				System.err.println(results[i].message);
			}
		}
		assertNull(results);

		pkg = repo.loadPackage("testBinaryPackageCompileBRL");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		// Here is where we write it out if needed... UNCOMMENT if needed for
		// the binary test
		// FileOutputStream out = new
		// FileOutputStream("/Users/michaelneale/RepoBinPackage.pkg");
		// out.write( binPackage );
		// out.flush();
		// out.close();

		assertNotNull(binPackage);

        Package binPkg = (Package) DroolsStreamUtils.streamIn(binPackage);

		assertNotNull(binPkg);
		assertTrue(binPkg.isValid());

		// and this shows off the "soundex" thing...
		Person p = new Person("fubar");

		BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
		loader.addPackage(new ByteArrayInputStream(binPackage));
		RuleBase rb = loader.getRuleBase();

		StatelessSession sess = rb.newStatelessSession();
		sess.execute(p);
		assertEquals(42, p.getAge());

		impl.createPackageSnapshot("testBinaryPackageCompileBRL", "SNAP1",
				false, "");

		pattern.factType = "PersonX";
		rule2.updateContent(BRXMLPersistence.getInstance().marshal(model));
		rule2.checkin("");

		results = impl.buildPackage(pkg.getUUID(), true);
		assertNotNull(results);
		assertTrue(results.length > 0);
		// assertEquals(2, results.length);
		assertEquals(rule2.getName(), results[0].assetName);
		assertEquals(AssetFormats.BUSINESS_RULE, results[0].assetFormat);
		assertNotNull(results[0].message);
		assertEquals(rule2.getUUID(), results[0].uuid);

		pkg = repo.loadPackageSnapshot("testBinaryPackageCompileBRL", "SNAP1");
		results = impl.buildPackage(pkg.getUUID(), true);
		assertNull(results);

		// check that the rule name in the model is being set
		AssetItem asset2 = pkg.addAsset("testSetRuleName", "");
		asset2.updateFormat(AssetFormats.BUSINESS_RULE);
		asset2.checkin("");

		RuleModel model2 = new RuleModel();
		assertNull(model2.name);
		RuleAsset asset = impl.loadRuleAsset(asset2.getUUID());
		asset.content = (PortableObject) model2;

		impl.checkinVersion(asset);

		asset = impl.loadRuleAsset(asset2.getUUID());

		model2 = (RuleModel) asset.content;
		assertNotNull(model2);
		assertNotNull(model2.name);
		assertEquals(asset2.getName(), model2.name);

	}

	/**
	 * this loads up a precompile binary package. If this fails, then it means
	 * it needs to be updated. It gets the package form the BRL example above.
	 */
	public void IGNORE_testLoadAndExecBinary() throws Exception {
		Person p = new Person("fubar");
		BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
		loader.addPackage(this.getClass().getResourceAsStream(
				"/RepoBinPackage.pkg"));
		RuleBase rb = loader.getRuleBase();
		StatelessSession sess = rb.newStatelessSession();
		sess.execute(p);
		assertEquals(42, p.getAge());
	}

    public void testSuggestionCompletionLoading() throws Exception {
            ServiceImplementation impl = getService();
            RulesRepository repo = impl.repository;

            // create our package
            PackageItem pkg = repo.createPackage("testSISuggestionCompletionLoading", "");
            ServiceImplementation.updateDroolsHeader("import org.drools.Person", pkg);
            AssetItem rule1 = pkg.addAsset("model_1", "");
            rule1.updateFormat(AssetFormats.DRL_MODEL);
            rule1.updateContent("declare Whee\n name: String \nend");
            rule1.checkin("");
            repo.save();



    }

	public void testPackageSource() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testPackageSource", "");
		ServiceImplementation.updateDroolsHeader("import org.goo.Ber", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when p:Person() \n then p.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		AssetItem func = pkg.addAsset("funky", "");
		func.updateFormat(AssetFormats.FUNCTION);
		func.updateContent("this is a func");
		func.checkin("");

		String drl = impl.buildPackageSource(pkg.getUUID());
		assertNotNull(drl);

		assertTrue(drl.indexOf("import org.goo.Ber") > -1);
		assertTrue(drl.indexOf("package testPackageSource") > -1);
		assertTrue(drl.indexOf("rule 'rule1'") > -1);
		assertTrue(drl.indexOf("this is a func") > -1);
		assertTrue(drl.indexOf("this is a func") < drl.indexOf("rule 'rule1'"));
		assertTrue(drl.indexOf("package testPackageSource") < drl
				.indexOf("this is a func"));
		assertTrue(drl.indexOf("package testPackageSource") < drl
				.indexOf("import org.goo.Ber"));

		AssetItem dsl = pkg.addAsset("MyDSL", "");
		dsl.updateFormat(AssetFormats.DSL);
		dsl
				.updateContent("[when]This is foo=bar()\n[then]do something=yeahMan();");
		dsl.checkin("");

		AssetItem asset = pkg.addAsset("MyDSLRule", "");
		asset.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
		asset.updateContent("when \n This is foo \n then \n do something");
		asset.checkin("");

		drl = impl.buildPackageSource(pkg.getUUID());
		assertNotNull(drl);

		assertTrue(drl.indexOf("import org.goo.Ber") > -1);
		assertTrue(drl.indexOf("This is foo") == -1);
		assertTrue(drl.indexOf("do something") == -1);
		assertTrue(drl.indexOf("bar()") > 0);
		assertTrue(drl.indexOf("yeahMan();") > 0);

	}

	public void testAssetSource() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testAssetSource", "");
		AssetItem asset = pkg.addAsset("testRule", "");
		asset.updateFormat(AssetFormats.DRL);
		asset.updateContent("rule 'n' \n when Foo() then bar(); \n end");
		asset.checkin("");
		repo.save();

		RuleAsset rule = impl.loadRuleAsset(asset.getUUID());
		String drl = impl.buildAssetSource(rule);
		assertEquals("rule 'n' \n when Foo() then bar(); \n end", drl);

		asset = pkg.addAsset("DT", "");
		asset.updateFormat(AssetFormats.DECISION_SPREADSHEET_XLS);
		asset.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/SampleDecisionTable.xls"));
		asset.checkin("");

		rule = impl.loadRuleAsset(asset.getUUID());
		drl = impl.buildAssetSource(rule);
		assertNotNull(drl);
		assertTrue(drl.indexOf("rule") > -1);
		assertTrue(drl.indexOf("policy: Policy") > -1);

		AssetItem dsl = pkg.addAsset("MyDSL", "");
		dsl.updateFormat(AssetFormats.DSL);
		dsl
				.updateContent("[when]This is foo=bar()\n[then]do something=yeahMan();");
		dsl.checkin("");

		asset = pkg.addAsset("MyDSLRule", "");
		asset.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
		asset.updateContent("when \n This is foo \n then \n do something");
		asset.checkin("");

		rule = impl.loadRuleAsset(asset.getUUID());
		drl = impl.buildAssetSource(rule);
		assertNotNull(drl);
		assertTrue(drl.indexOf("This is foo") == -1);
		assertTrue(drl.indexOf("do something") == -1);
		assertTrue(drl.indexOf("bar()") > -1);
		assertTrue(drl.indexOf("yeahMan();") > -1);

		rule = impl.loadRuleAsset(repo.copyAsset(asset.getUUID(),
				"testAssetSource", "newRuleName"));
		// System.err.println(((RuleContentText)rule.content).content);
		drl = impl.buildAssetSource(rule);
		assertNotNull(drl);
		assertTrue(drl.indexOf("newRuleName") > 0);

	}


    	public void testBuildAssetWithError() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBuildAssetWithError", "");
		AssetItem model = pkg.addAsset("MyModel", "");
		model.updateFormat(AssetFormats.MODEL);
		model.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/billasurf.jar"));
		model.checkin("");

		ServiceImplementation.updateDroolsHeader("import com.billasurf.Person", pkg);

		AssetItem asset = pkg.addAsset("testRule", "");
		asset.updateFormat(AssetFormats.DRL);
		asset.updateContent("rule 'MyGoodRule' \n when Personx() then System.err.println(42); \n end");
		asset.checkin("");
		repo.save();

		RuleAsset rule = impl.loadRuleAsset(asset.getUUID());


		BuilderResult[] result = impl.buildAsset(rule);
		assertNotNull(result);
        assertEquals(-1, result[0].message.indexOf("Check log for"));
        assertTrue(result[0].message.indexOf("Unable to resolve") > -1);


	}

	public void testBuildAsset() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBuildAsset", "");
		AssetItem model = pkg.addAsset("MyModel", "");
		model.updateFormat(AssetFormats.MODEL);
		model.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/billasurf.jar"));
		model.checkin("");

		ServiceImplementation.updateDroolsHeader("import com.billasurf.Person", pkg);

		AssetItem asset = pkg.addAsset("testRule", "");
		asset.updateFormat(AssetFormats.DRL);
		asset
				.updateContent("rule 'MyGoodRule' \n when Person() then System.err.println(42); \n end");
		asset.checkin("");
		repo.save();

		RuleAsset rule = impl.loadRuleAsset(asset.getUUID());

		// check its all OK
		BuilderResult[] result = impl.buildAsset(rule);
		assertNull(result);

        ServiceImplementation.ruleBaseCache.clear();

		// try it with a bad rule
		RuleContentText text = new RuleContentText();
		text.content = "rule 'MyBadRule' \n when Personx() then System.err.println(42); \n end";
		rule.content = text;

		result = impl.buildAsset(rule);
		assertNotNull(result);
		assertNotNull(result[0].message);
		assertEquals(AssetFormats.DRL, result[0].assetFormat);

		// now mix in a DSL
		AssetItem dsl = pkg.addAsset("MyDSL", "");
		dsl.updateFormat(AssetFormats.DSL);
		dsl
				.updateContent("[when]There is a person=Person()\n[then]print out 42=System.err.println(42);");
		dsl.checkin("");

		AssetItem dslRule = pkg.addAsset("dslRule", "");
		dslRule.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
		dslRule
				.updateContent("when \n There is a person \n then \n print out 42");
		dslRule.checkin("");

		rule = impl.loadRuleAsset(dslRule.getUUID());

		result = impl.buildAsset(rule);
		assertNull(result);

		asset = pkg.addAsset("someEnumThing", "");
		asset.updateFormat(AssetFormats.ENUMERATION);
		asset.updateContent("goober boy");
		asset.checkin("");
		result = impl.buildAsset(impl.loadRuleAsset(asset.getUUID()));
		assertFalse(result.length == 0);

	}

	public void testBuildAssetBRXMLAndCopy() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBuildAssetBRL", "");
		AssetItem model = pkg.addAsset("MyModel", "");
		model.updateFormat(AssetFormats.MODEL);
		model.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/billasurf.jar"));
		model.checkin("");

		ServiceImplementation.updateDroolsHeader("import com.billasurf.Person", pkg);
		impl.createCategory("/", "brl", "");

		String uuid = impl.createNewRule("testBRL", "", "brl",
				"testBuildAssetBRL", AssetFormats.BUSINESS_RULE);

		RuleAsset rule = impl.loadRuleAsset(uuid);

		RuleModel m = (RuleModel) rule.content;
		assertNotNull(m);
		m.name = "testBRL";

		FactPattern p = new FactPattern("Person");
		p.boundName = "p";
		SingleFieldConstraint con = new SingleFieldConstraint();
		con.fieldName = "name";
		con.value = "mark";
		con.operator = "==";
		con.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;

		p.addConstraint(con);

		m.addLhsItem(p);

		ActionSetField set = new ActionSetField("p");
		ActionFieldValue f = new ActionFieldValue("name", "42-ngoo",
				SuggestionCompletionEngine.TYPE_STRING);
		set.addFieldValue(f);

		m.addRhsItem(set);

		impl.checkinVersion(rule);

		// check its all OK
		BuilderResult[] result = impl.buildAsset(rule);
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				System.err.println(result[i].message);
			}
		}
		assertNull(result);

		List assets = iteratorToList(pkg.getAssets());
		assertEquals(3, assets.size());
		// now lets copy...
		String newUUID = impl.copyAsset(rule.uuid, rule.metaData.packageName,
				"ruleName2");

		assets = iteratorToList(pkg.getAssets());
		assertEquals(4, assets.size()); //we have 4 due to the drools.package file.
		RuleAsset asset = impl.loadRuleAsset(newUUID);

		String pkgSource = impl.buildPackageSource(pkg.getUUID());

		assertTrue(pkgSource.indexOf("ruleName2") > 0);
		assertTrue(impl.buildAssetSource(asset).indexOf("ruleName2") > 0);
		assertTrue(impl.buildAssetSource(asset).indexOf("testBRL") == -1);

		// RuleModel model2 = (RuleModel) asset.content;
		// assertEquals("ruleName2", model2.name);

	}

	private List iteratorToList(Iterator assets) {
		List result = new ArrayList();
		while (assets.hasNext()) {
			result.add(assets.next());

		}
		return result;
	}

	public void testBuildAssetWithPackageConfigError() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage(
				"testBuildAssetWithPackageConfigError", "");
		// AssetItem model = pkg.addAsset( "MyModel", "" );
		// model.updateFormat( AssetFormats.MODEL );
		// model.updateBinaryContentAttachment(
		// this.getClass().getResourceAsStream( "/billasurf.jar" ) );
		// model.checkin( "" );

		// pkg.updateHeader( "import com.billasurf.Person" );

		AssetItem asset = pkg.addAsset("testRule", "");
		asset.updateFormat(AssetFormats.DRL);
		asset.updateContent("rule 'MyGoodRule' \n when \n then \n end");
		asset.checkin("");
		repo.save();

		RuleAsset rule = impl.loadRuleAsset(asset.getUUID());

		// check its all OK
		BuilderResult[] result = impl.buildAsset(rule);
		if (!(result == null)) {
			System.err.println(result[0].assetName + " " + result[0].message);
		}
		assertNull(result);

		ServiceImplementation.updateDroolsHeader("importxxxx", pkg);
		repo.save();
		result = impl.buildAsset(rule);
		assertNotNull(result);

		assertEquals(1, result.length);
		assertEquals("package", result[0].assetFormat);
		assertNotNull(result[0].message);

	}

	public void testRuleNameList() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testRuleNameList", "");
		ServiceImplementation.updateDroolsHeader("import org.goo.Ber", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1.updateContent("package wee.wee \nrule 'rule1' \n  when p:Person() \n then p.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		AssetItem rule2 = pkg.addAsset("rule_2", "");
		rule2.updateFormat(AssetFormats.DRL);
		rule2
				.updateContent("rule 'rule2' \n ruleflow-group 'whee' \nwhen p:Person() \n then p.setAge(42); \n end");
		rule2.checkin("");
		repo.save();

		String[] list = impl.listRulesInPackage(pkg.getName());
		assertEquals(2, list.length);
		assertEquals("rule1", list[0]);
		assertEquals("rule2", list[1]);


		rule2.updateContent("wang");
		rule2.checkin("");

		list = impl.listRulesInPackage(pkg.getName());
		assertEquals(2, list.length);


	}

	/**
	 * This idea of this is to not compile packages more then we have to.
	 */
	public void testBinaryUpToDate() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testBinaryPackageUpToDate", "");
		assertFalse(pkg.isBinaryUpToDate());
		ServiceImplementation.updateDroolsHeader("import org.drools.Person", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		assertFalse(pkg.isBinaryUpToDate());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));
		impl.ruleBaseCache.remove("XXX");

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), true);
		assertNull(results);

		pkg = repo.loadPackage("testBinaryPackageUpToDate");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		assertNotNull(binPackage);

		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertTrue(pkg.isBinaryUpToDate());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		RuleAsset asset = impl.loadRuleAsset(rule1.getUUID());
		impl.checkinVersion(asset);

		assertFalse(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		impl.buildPackage(pkg.getUUID(), false);

		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		PackageConfigData config = impl.loadPackageConfig(pkg.getUUID());
		impl.savePackage(config);

		assertFalse(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertFalse(pkg.isBinaryUpToDate());
		impl.buildPackage(pkg.getUUID(), false);
		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertTrue(pkg.isBinaryUpToDate());

	}

	public void testRunScenario() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage("testScenarioRun", "");
		ServiceImplementation.updateDroolsHeader("import org.drools.Person\n global org.drools.Cheese cheese\n", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		Scenario sc = new Scenario();
		FactData person = new FactData();
		person.name = "p";
		person.type = "Person";
		person.fieldData.add(new FieldData("age", "40"));
		person.fieldData.add(new FieldData("name", "michael"));

		sc.fixtures.add(person);
		sc.fixtures.add(new ExecutionTrace());
		VerifyRuleFired vr = new VerifyRuleFired("rule1", 1, null);
		sc.fixtures.add(vr);

		VerifyFact vf = new VerifyFact();
		vf.name = "p";
		vf.fieldValues.add(new VerifyField("name", "michael", "=="));
		vf.fieldValues.add(new VerifyField("age", "42", "=="));
		sc.fixtures.add(vf);

		FactData cheese = new FactData();
		cheese.name = "cheese";
		cheese.type = "Cheese";
		cheese.fieldData.add(new FieldData("price", "42"));
		sc.globals.add(cheese);

		ScenarioRunResult res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());



		res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());


		impl.ruleBaseCache.clear();
		res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());

		//BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
		//assertNull(results);

		rule1.updateContent("Junk");
		rule1.checkin("");



		impl.ruleBaseCache.clear();
		pkg.updateBinaryUpToDate(false);
		repo.save();
		res = impl.runScenario(pkg.getName(), sc).result;
		assertNotNull(res.errors);
		assertNull(res.scenario);

		assertTrue(res.errors.length > 0);


		impl.createCategory("/", "sc", "");

		String scenarioId = impl.createNewRule("sc1", "s", "sc", pkg.getName(), AssetFormats.TEST_SCENARIO);

		RuleAsset asset = impl.loadRuleAsset(scenarioId);
		assertNotNull(asset.content);
		assertTrue(asset.content instanceof Scenario);

		Scenario sc_ = (Scenario) asset.content;
		sc_.fixtures.add(new ExecutionTrace());
		impl.checkinVersion(asset);
		asset = impl.loadRuleAsset(scenarioId);
		assertNotNull(asset.content);
		assertTrue(asset.content instanceof Scenario);
		sc_ = (Scenario) asset.content;
		assertEquals(1, sc_.fixtures.size());

	}

	public void testRunScenarioWithGeneratedBeans() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage("testScenarioRunWithGeneratedBeans", "");
		ServiceImplementation.updateDroolsHeader("declare GenBean\n name: String \n age: int \nend\n", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \n p : GenBean(name=='mic') \n then \n p.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		Scenario sc = new Scenario();
		FactData person = new FactData();
		person.name = "c";
		person.type = "GenBean";
		person.fieldData.add(new FieldData("age", "40"));
		person.fieldData.add(new FieldData("name", "mic"));

		sc.fixtures.add(person);
		sc.fixtures.add(new ExecutionTrace());
		VerifyRuleFired vr = new VerifyRuleFired("rule1", 1, null);
		sc.fixtures.add(vr);

		VerifyFact vf = new VerifyFact();
		vf.name = "c";
		vf.fieldValues.add(new VerifyField("name", "mic", "=="));
		vf.fieldValues.add(new VerifyField("age", "42", "=="));
		sc.fixtures.add(vf);


		SingleScenarioResult res_ = impl.runScenario(pkg.getName(), sc);
		assertTrue(res_.auditLog.size() > 0);

		String[] logEntry = res_.auditLog.get(0);
		assertNotNull(logEntry[0], logEntry[1]);

		ScenarioRunResult res = res_.result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());



	}

    public void testRunPackageScenariosWithDeclaredFacts() throws Exception {
        ServiceImplementation impl = getService();
        RulesRepository repo = impl.repository;

        PackageItem pkg = repo.createPackage("testScenarioRunBulkWithDeclaredFacts", "");
        ServiceImplementation.updateDroolsHeader("declare Wang \n age: Integer \n name: String \n end", pkg);
        AssetItem rule1 = pkg.addAsset("rule_1", "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1
                .updateContent("rule 'rule1' \n when \np : Wang() \n then \np.setAge(42); \n end");
        rule1.checkin("");

        //this rule will never fire
        AssetItem rule2 = pkg.addAsset("rule_2", "");
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateContent("rule 'rule2' \n when \np : Wang(age == 1000) \n then \np.setAge(46); \n end");
        rule2.checkin("");
        repo.save();



        //first, the green scenario
        Scenario sc = new Scenario();
        FactData person = new FactData();
        person.name = "p";
        person.type = "Wang";
        person.fieldData.add(new FieldData("age", "40"));
        person.fieldData.add(new FieldData("name", "michael"));

        sc.fixtures.add(person);
        sc.fixtures.add(new ExecutionTrace());
        VerifyRuleFired vr = new VerifyRuleFired("rule1", 1, null);
        sc.fixtures.add(vr);

        VerifyFact vf = new VerifyFact();
        vf.name = "p";
        vf.fieldValues.add(new VerifyField("name", "michael", "=="));
        vf.fieldValues.add(new VerifyField("age", "42", "=="));
        sc.fixtures.add(vf);

        AssetItem scenario1 = pkg.addAsset("scen1", "");
        scenario1.updateFormat(AssetFormats.TEST_SCENARIO);
        scenario1.updateContent(ScenarioXMLPersistence.getInstance().marshal(sc));
        scenario1.checkin("");

        //now the bad scenario
        sc = new Scenario();
        person = new FactData();
        person.name = "p";
        person.type = "Wang";
        person.fieldData.add(new FieldData("age", "40"));
        person.fieldData.add(new FieldData("name", "michael"));

        sc.fixtures.add(person);
        sc.fixtures.add(new ExecutionTrace());
        vr = new VerifyRuleFired("rule2", 1, null);
        sc.fixtures.add(vr);


        AssetItem scenario2 = pkg.addAsset("scen2", "");
        scenario2.updateFormat(AssetFormats.TEST_SCENARIO);
        scenario2.updateContent(ScenarioXMLPersistence.getInstance().marshal(sc));
        scenario2.checkin("");

        BulkTestRunResult result = impl.runScenariosInPackage(pkg.getUUID());
        assertNull(result.errors);

        assertEquals(50, result.percentCovered);
        assertEquals(1, result.rulesNotCovered.length);
        assertEquals("rule2", result.rulesNotCovered[0]);

        assertEquals(2, result.results.length);

        ScenarioResultSummary s1 = result.results[0];
        assertEquals(0, s1.failures);
        assertEquals(3, s1.total);
        assertEquals(scenario1.getUUID(), s1.uuid);
        assertEquals(scenario1.getName(), s1.scenarioName);

        ScenarioResultSummary s2 = result.results[1];
        assertEquals(1, s2.failures);
        assertEquals(1, s2.total);
        assertEquals(scenario2.getUUID(), s2.uuid);
        assertEquals(scenario2.getName(), s2.scenarioName);
    }

	public void testRunScenarioWithJar() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testRunScenarioWithJar", "");
		AssetItem model = pkg.addAsset("MyModel", "");
		model.updateFormat(AssetFormats.MODEL);
		model.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/billasurf.jar"));
		model.checkin("");

		ServiceImplementation.updateDroolsHeader("import com.billasurf.Board", pkg);

		AssetItem asset = pkg.addAsset("testRule", "");
		asset.updateFormat(AssetFormats.DRL);
		asset.updateContent("rule 'MyGoodRule' \n dialect 'mvel' \n when Board() then System.err.println(42); \n end");
		asset.checkin("");
		repo.save();

		Scenario sc = new Scenario();
		FactData person = new FactData();
		person.name = "p";
		person.type = "Board";
		person.fieldData.add(new FieldData("cost", "42"));


		sc.fixtures.add(person);
		sc.fixtures.add(new ExecutionTrace());
		VerifyRuleFired vr = new VerifyRuleFired("MyGoodRule", 1, null);
		sc.fixtures.add(vr);

		VerifyFact vf = new VerifyFact();
		vf.name = "p";

		vf.fieldValues.add(new VerifyField("cost", "42", "=="));
		sc.fixtures.add(vf);

		ScenarioRunResult res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());


		res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());

		impl.ruleBaseCache.clear();

		res = impl.runScenario(pkg.getName(), sc).result;
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());




	}
	
	public void testRunScenarioWithJarThatHasSourceFiles() throws Exception {
	    ServiceImplementation impl = getService();
	    RulesRepository repo = impl.repository;
	    
	    // create our package
	    PackageItem pkg = repo.createPackage("testRunScenarioWithJarThatHasSourceFiles", "");
	    AssetItem model = pkg.addAsset("MyModel", "");
	    model.updateFormat(AssetFormats.MODEL);
	    model.updateBinaryContentAttachment(this.getClass()
	                                        .getResourceAsStream("/jarWithSourceFiles.jar"));
	    model.checkin("");
	    
	    ServiceImplementation.updateDroolsHeader("import org.test.Person; \n import org.test.Banana; \n ", pkg);
	    
	    AssetItem asset = pkg.addAsset("testRule", "");
	    asset.updateFormat(AssetFormats.DRL);
	    asset.updateContent("rule 'MyGoodRule' \n dialect 'mvel' \n when \n Person() \n then \n insert( new Banana() ); \n end");
	    asset.checkin("");
	    repo.save();
	    
	    Scenario sc = new Scenario();
	    FactData person = new FactData();
	    person.name = "p";
	    person.type = "Person";
	    
	    
	    sc.fixtures.add(person);
	    sc.fixtures.add(new ExecutionTrace());
	    VerifyRuleFired vr = new VerifyRuleFired("MyGoodRule", 1, null);
	    sc.fixtures.add(vr);
	   
	    
	    ScenarioRunResult res = null;
	    try {
	        res = impl.runScenario( pkg.getName(),
                                                      sc ).result;
        } catch ( ClassFormatError e ) {
            fail( "Probably failed when loading a source file instead of class file. " + e );
        }
	    assertEquals(null, res.errors);
	    assertNotNull(res.scenario);
	    assertTrue(vr.wasSuccessful());
	    
	    
	    res = impl.runScenario(pkg.getName(), sc).result;
	    assertEquals(null, res.errors);
	    assertNotNull(res.scenario);
	    assertTrue(vr.wasSuccessful());
	    
	    impl.ruleBaseCache.clear();
	    
	    res = impl.runScenario(pkg.getName(), sc).result;
	    assertEquals(null, res.errors);
	    assertNotNull(res.scenario);
	    assertTrue(vr.wasSuccessful());
	    
	    
	    
	    
	}

	public void testRunPackageScenarios() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage("testScenarioRunBulk", "");
		ServiceImplementation.updateDroolsHeader("import org.drools.Person", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");

		//this rule will never fire
		AssetItem rule2 = pkg.addAsset("rule_2", "");
		rule2.updateFormat(AssetFormats.DRL);
		rule2.updateContent("rule 'rule2' \n when \np : Person(age == 1000) \n then \np.setAge(46); \n end");
		rule2.checkin("");
		repo.save();



		//first, the green scenario
		Scenario sc = new Scenario();
		FactData person = new FactData();
		person.name = "p";
		person.type = "Person";
		person.fieldData.add(new FieldData("age", "40"));
		person.fieldData.add(new FieldData("name", "michael"));

		sc.fixtures.add(person);
		sc.fixtures.add(new ExecutionTrace());
		VerifyRuleFired vr = new VerifyRuleFired("rule1", 1, null);
		sc.fixtures.add(vr);

		VerifyFact vf = new VerifyFact();
		vf.name = "p";
		vf.fieldValues.add(new VerifyField("name", "michael", "=="));
		vf.fieldValues.add(new VerifyField("age", "42", "=="));
		sc.fixtures.add(vf);

		AssetItem scenario1 = pkg.addAsset("scen1", "");
		scenario1.updateFormat(AssetFormats.TEST_SCENARIO);
		scenario1.updateContent(ScenarioXMLPersistence.getInstance().marshal(sc));
		scenario1.checkin("");

		//now the bad scenario
		sc = new Scenario();
		person = new FactData();
		person.name = "p";
		person.type = "Person";
		person.fieldData.add(new FieldData("age", "40"));
		person.fieldData.add(new FieldData("name", "michael"));

		sc.fixtures.add(person);
		sc.fixtures.add(new ExecutionTrace());
		vr = new VerifyRuleFired("rule2", 1, null);
		sc.fixtures.add(vr);


		AssetItem scenario2 = pkg.addAsset("scen2", "");
		scenario2.updateFormat(AssetFormats.TEST_SCENARIO);
		scenario2.updateContent(ScenarioXMLPersistence.getInstance().marshal(sc));
		scenario2.checkin("");

        AssetItem scenario3 = pkg.addAsset("scenBOGUS", "");
        scenario3.updateFormat(AssetFormats.TEST_SCENARIO);
        scenario3.updateContent("SOME RUBBISH");
        scenario3.updateDisabled(true);
        scenario3.checkin("");



		//love you
		long time = System.currentTimeMillis();
		BulkTestRunResult result = impl.runScenariosInPackage(pkg.getUUID());
		System.err.println("Time taken for runScenariosInPackage " + (System.currentTimeMillis() - time));
		assertNull(result.errors);

		assertEquals(50, result.percentCovered);
		assertEquals(1, result.rulesNotCovered.length);
		assertEquals("rule2", result.rulesNotCovered[0]);

		assertEquals(2, result.results.length);

		ScenarioResultSummary s1 = result.results[0];
		assertEquals(0, s1.failures);
		assertEquals(3, s1.total);
		assertEquals(scenario1.getUUID(), s1.uuid);
		assertEquals(scenario1.getName(), s1.scenarioName);

		ScenarioResultSummary s2 = result.results[1];
		assertEquals(1, s2.failures);
		assertEquals(1, s2.total);
		assertEquals(scenario2.getUUID(), s2.uuid);
		assertEquals(scenario2.getName(), s2.scenarioName);
	}

	public void testVerifier() throws Exception {
		ServiceImplementation impl = getService();
		PackageItem pkg = impl.repository.createPackage("testVerifier", "");
		AssetItem asset = pkg.addAsset("SomeDRL", "");
		asset.updateFormat(AssetFormats.DRL);

		asset.updateContent(IO.read(this.getClass().getResourceAsStream("/AnalysisSample.drl")));
		asset.checkin("");

		AnalysisReport report = impl.analysePackage(pkg.getUUID());
		assertNotNull(report);
		assertEquals(0, report.errors.length);
		assertEquals(11, report.warnings.length);
		assertEquals(16, report.notes.length);
		assertEquals(2, report.factUsages.length);

		assertNotNull(report.notes[0].description);
		assertNotNull(report.notes[0].reason);
		assertEquals(2, report.notes[0].cause.length);
		assertNotNull(report.notes[0].cause[0]);
		assertNotNull(report.notes[0].cause[1]);

		assertEquals("RedundancyPattern", report.factUsages[0].name);
		assertEquals("RedundancyPattern2", report.factUsages[1].name);

		assertEquals(1, report.factUsages[0].fields.length);
		assertEquals(1, report.factUsages[1].fields.length);

		assertEquals("a", report.factUsages[0].fields[0].name);
		assertEquals("a", report.factUsages[1].fields[0].name);


		assertEquals(3, report.factUsages[0].fields[0].rules.length);
		assertEquals(2, report.factUsages[1].fields[0].rules.length);

		assertNotNull(report.factUsages[0].fields[0].rules[0]);

	}

	public void testListFactTypesAvailableInPackage() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage("testAvailableTypes", "");
		AssetItem model = pkg.addAsset("MyModel", "");
		model.updateFormat(AssetFormats.MODEL);
		model.updateBinaryContentAttachment(this.getClass()
				.getResourceAsStream("/billasurf.jar"));
		model.checkin("");
		repo.save();

		String[] s = impl.listTypesInPackage(pkg.getUUID());
		assertNotNull(s);
		assertEquals(2, s.length);
		assertEquals("com.billasurf.Person", s[0]);
		assertEquals("com.billasurf.Board", s[1]);

		AssetItem asset = pkg.addAsset("declaretTypes", "");
		asset.updateFormat(AssetFormats.DRL_MODEL);
		asset.updateContent("declare Whee\n name: String \n end");
		asset.checkin("");

		s = impl.listTypesInPackage(pkg.getUUID());
		assertEquals(3, s.length);
		assertEquals("Whee", s[2]);

	}


	public void testGuidedDTExecute() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;
		impl.createCategory("/", "decisiontables", "");

		PackageItem pkg = repo.createPackage("testGuidedDTCompile", "");
		ServiceImplementation.updateDroolsHeader("import org.drools.Person", pkg);
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();


		GuidedDecisionTable dt = new GuidedDecisionTable();
		ConditionCol col = new ConditionCol();
		col.boundName = "p";
		col.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		col.factField = "hair";
		col.factType = "Person";
		col.operator = "==";
		dt.conditionCols.add(col);

		ActionSetFieldCol ac = new ActionSetFieldCol();
		ac.boundName = "p";
		ac.factField = "likes";
		ac.type = SuggestionCompletionEngine.TYPE_STRING;
		dt.actionCols.add(ac);

		dt.data = new String[][] {
			new String[] {"1", "descrip", "pink", "cheese"}
		};

		String uid = impl.createNewRule("decTable", "", "decisiontables", pkg.getName(), AssetFormats.DECISION_TABLE_GUIDED);
		RuleAsset ass = impl.loadRuleAsset(uid);
		ass.content = dt;
		impl.checkinVersion(ass);

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), true);
		assertNull(results);

		pkg = repo.loadPackage("testGuidedDTCompile");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		assertNotNull(binPackage);

        Package binPkg = (Package) DroolsStreamUtils.streamIn(binPackage);


		assertEquals(2, binPkg.getRules().length);

		assertNotNull(binPkg);
		assertTrue(binPkg.isValid());

		Person p = new Person();


		p.setHair("pink");

		BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
		loader.addPackage(new ByteArrayInputStream(binPackage));
		RuleBase rb = loader.getRuleBase();

		StatelessSession sess = rb.newStatelessSession();
		sess.execute(p);
		assertEquals(42, p.getAge());
		assertEquals("cheese", p.getLikes());
	}

	public void testPackageNameSorting() {
		PackageConfigData c1 = new PackageConfigData("org.foo");
		PackageConfigData c2 = new PackageConfigData("org.foo.bar");

		List<PackageConfigData> ls = new ArrayList<PackageConfigData>();
		ls.add(c2);
		ls.add(c1);
		ServiceImplementation serv = new ServiceImplementation();
		serv.sortPackages(ls);
		assertEquals(c1, ls.get(0));
		assertEquals(c2, ls.get(1));
	}

	public void testLoadDropDown() throws Exception {
		ServiceImplementation serv = new ServiceImplementation();
		String[] pairs = new String[]{"f1=x", "f2=2"};
		String expression = "['@{f1}', '@{f2}']";
		String[] r = serv.loadDropDownExpression(pairs, expression);
		assertEquals(2, r.length);

		assertEquals("x", r[0]);
		assertEquals("2", r[1]);

	}

	public void testLoadDropDownNoValuePairs() throws Exception {
	    ServiceImplementation serv = new ServiceImplementation();
	    String[] pairs = new String[]{null};
	    String expression = "['@{f1}', '@{f2}']";
	    String[] r = serv.loadDropDownExpression(pairs, expression);
	    
	    assertEquals(0, r.length);
	    
	}

	public void testListUserPermisisons() throws Exception {
		ServiceImplementation serv = getService();
		Map<String, List<String>> r = serv.listUserPermissions();
		assertNotNull(r);
	}

	public void testManageUserPermissions() throws Exception {
		ServiceImplementation serv = getService();
		Map<String, List<String>> perms = new HashMap<String, List<String>>();
		serv.updateUserPermissions("googoo", perms);

		Map<String, List<String>> perms_ = serv.retrieveUserPermissions("googoo");
		assertEquals(0, perms_.size());
	}

	public void testImportSampleRepository() throws Exception {
		ServiceImplementation serv = getService();
		serv.installSampleRepository();
		PackageConfigData[] cfgs = serv.listPackages();
		assertEquals(2, cfgs.length);
		assertTrue(cfgs[0].name.equals("mortgages") || cfgs[1].name.equals("mortgages"));
		String puuid = (cfgs[0].name.equals("mortgages")) ? cfgs[0].uuid : cfgs[1].uuid;
		BulkTestRunResult res = serv.runScenariosInPackage(puuid);
		assertEquals(null, res.errors);
	}

	public void testAddCategories() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testAddCategoriesPackage", "desc");
		impl.createCategory("", "testAddCategoriesCat1", "this is a cat");
		impl.createCategory("", "testAddCategoriesCat2", "this is a cat");

		String uuid = impl.createNewRule("testCreateNewRuleName",
				"an initial desc", "testAddCategoriesCat1", "testAddCategoriesPackage",
				AssetFormats.DSL_TEMPLATE_RULE);

		AssetItem dtItem = impl.repository.loadAssetByUUID(uuid);
		dtItem.addCategory("testAddCategoriesCat1");
		impl.repository.save();
        
		AssetItem dtItem1 = impl.repository.loadAssetByUUID(uuid);
		assertEquals(1, dtItem1.getCategories().size());
		assertTrue(dtItem1.getCategorySummary().contains("testAddCategoriesCat1"));		

		AssetItem dtItem2 = impl.repository.loadAssetByUUID(uuid);
		dtItem2.addCategory("testAddCategoriesCat2");
		impl.repository.save();
        
		AssetItem dtItem3 = impl.repository.loadAssetByUUID(uuid);
		assertEquals(2, dtItem3.getCategories().size());
		assertTrue(dtItem3.getCategorySummary().contains("testAddCategoriesCat2"));		
	}
	
	/**
	 * Set up enough of the Seam environment to test it.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Map<String, Object> ap = new HashMap<String, Object>();
		ap.put("org.drools.guvnor.client.rpc.RepositoryService", getService());
		Lifecycle.beginApplication(ap);
		Lifecycle.beginCall();

		MockIdentity mi = new MockIdentity();
		mi.inject();
		mi.create();
		//mi.addRole(RoleTypes.ADMIN);
		RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
		resolver.setEnableRoleBasedAuthorization(false);
		mi.addPermissionResolver(new RoleBasedPermissionResolver());
		//mi.addPermissionResolver(new PackageBasedPermissionResolver());


	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (Contexts.isApplicationContextActive()) {
			Lifecycle.endApplication();
		}
	}

	private ServiceImplementation getService() throws Exception {
		ServiceImplementation impl = new ServiceImplementation();
		impl.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());


		return impl;
	}

}