package org.drools.brms.server;

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
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ActionFieldValue;
import org.drools.brms.client.modeldriven.brl.ActionSetField;
import org.drools.brms.client.modeldriven.brl.FactPattern;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.ScenarioRunResult;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.rpc.ValidatedResponse;
import org.drools.brms.server.util.BRXMLPersistence;
import org.drools.brms.server.util.TableDisplayHandler;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.common.DroolsObjectInputStream;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.rule.Package;
import org.drools.util.BinaryRuleBaseLoader;

import com.google.gwt.user.client.rpc.SerializableException;

public class ServiceImplementationTest extends TestCase {

	public void testCategory() throws Exception {
		// ServiceImpl impl = new ServiceImpl(new
		// RulesRepository(SessionHelper.getSession()));

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

	public void testRuleTableLoad() throws Exception {
		ServiceImplementation impl = getService();
		TableConfig conf = impl
				.loadTableConfig(TableDisplayHandler.DEFAULT_TABLE_TEMPLATE);
		assertNotNull(conf.headers);

		CategoryItem cat = impl.repository.loadCategory("/");
		cat.addCategory("testRuleTableLoad", "yeah");

		impl.repository.createPackage("testRuleTableLoad", "yeah");
		impl.createNewRule("testRuleTableLoad", "ya", "testRuleTableLoad",
				"testRuleTableLoad", "rule");
		impl.createNewRule("testRuleTableLoad2", "ya", "testRuleTableLoad",
				"testRuleTableLoad", "rule");

		TableDataResult result = impl
				.loadRuleListForCategories("testRuleTableLoad");
		assertEquals(2, result.data.length);

		String key = result.data[0].id;
		assertFalse(key.startsWith("testRule"));

		assertEquals(result.data[0].format, "rule");
		assertTrue(result.data[0].values[0].startsWith("rule"));
	}

	public void testDateFormatting() throws Exception {
		Calendar cal = Calendar.getInstance();
		TableDisplayHandler handler = new TableDisplayHandler(
				TableDisplayHandler.DEFAULT_TABLE_TEMPLATE);
		String fmt = handler.formatDate(cal);
		assertNotNull(fmt);

		assertTrue(fmt.length() > 8);
	}

	public void testLoadRuleAsset() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testLoadRuleAsset", "desc");
		impl.createCategory("", "testLoadRuleAsset", "this is a cat");

		impl.createNewRule("testLoadRuleAsset", "description",
				"testLoadRuleAsset", "testLoadRuleAsset", "drl");

		TableDataResult res = impl
				.loadRuleListForCategories("testLoadRuleAsset");
		assertEquals(1, res.data.length);

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
		assertEquals("drl", asset.metaData.format);
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

	public void testLoadAssetHistoryAndRestore() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testLoadAssetHistory", "desc");
		impl.createCategory("", "testLoadAssetHistory", "this is a cat");

		String uuid = impl.createNewRule("testLoadAssetHistory", "description",
				"testLoadAssetHistory", "testLoadAssetHistory", "drl");
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
		RepositoryService serv = getService();

		serv.listPackages();

		serv
				.createCategory("/", "testCheckinCategory",
						"this is a description");
		serv.createCategory("/", "testCheckinCategory2",
				"this is a description");
		serv.createCategory("testCheckinCategory", "deeper", "description");

		String uuid = serv.createNewRule("testChecking",
				"this is a description", "testCheckinCategory",
				RulesRepository.DEFAULT_PACKAGE, "drl");

		RuleAsset asset = serv.loadRuleAsset(uuid);

		assertNotNull(asset.metaData.lastModifiedDate);

		asset.metaData.coverage = "boo";
		asset.content = new RuleContentText();
		((RuleContentText) asset.content).content = "yeah !";

		Date start = new Date();
		Thread.sleep(100);

		String uuid2 = serv.checkinVersion(asset);
		assertEquals(uuid, uuid2);

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

		String uuid = impl.createPackage("testCreateArchivedPackage",
				"this is a new package");
		PackageItem item = impl.repository
				.loadPackage("testCreateArchivedPackage");
		item.archiveItem(true);
		assertEquals(pkgs.length, impl.listPackages().length);
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
		it.updateHeader("header");
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
				-1, 0);
		assertEquals(4, res.data.length);
		assertEquals(uuid, res.data[0].id);
		assertEquals("testListByFormat", res.data[0].values[0]);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 4, 0);
		assertEquals(4, res.data.length);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 2, 0);
		assertEquals(2, res.data.length);
		assertEquals(uuid, res.data[0].id);

		res = impl.listAssets(pkgUUID, arr("testListByFormat"), 2, 2);
		assertEquals(2, res.data.length);
		assertEquals(uuid3, res.data[0].id);

		uuid = impl.createNewRule("testListByFormat5", "x", cat,
				"testListByFormat", "otherFormat");

		res = impl.listAssets(pkgUUID, arr("otherFormat"), 40, 0);
		assertEquals(1, res.data.length);
		assertEquals(uuid, res.data[0].id);

		res = impl.listAssets(pkgUUID, new String[] { "otherFormat",
				"testListByFormat" }, 40, 0);
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
				"testStatus", "drl");
		String ruleUUID2 = impl.createNewRule("testStatus2", "desc", null,
				"testStatus", "drl");
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
				"sourcePackage", "drl");

		TableDataResult res = impl.listAssets(destPkgId,
				new String[] { "drl" }, 2, 0);
		assertEquals(0, res.data.length);

		impl.changeAssetPackage(uuid, "targetPackage", "yeah");
		res = impl.listAssets(destPkgId, new String[] { "drl" }, 2, 0);

		assertEquals(1, res.data.length);

		res = impl.listAssets(sourcePkgId, new String[] { "drl" }, 2, 0);

		assertEquals(0, res.data.length);

	}

	public void testCopyAsset() throws Exception {
		RepositoryService impl = getService();
		impl.createCategory("/", "templates", "ya");
		String uuid = impl.createNewRule("testCopyAsset", "", "templates",
				RulesRepository.DEFAULT_PACKAGE, "drl");
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
				"snapshotTesting", "testSnapshot", "drl");

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

		PackageItem pkg = repo.createPackage("testSnapshotRebuild", "");
		pkg.updateHeader("import java.util.List");
		repo.save();

		AssetItem item = pkg.addAsset("anAsset", "");
		item.updateFormat(AssetFormats.DRL);
		item
				.updateContent(" rule abc \n when \n then \n System.out.println(42); \n end");
		item.checkin("");

		BuilderResult[] res = impl.buildPackage(pkg.getUUID(), "", true);
		assertNull(res);

		impl.createPackageSnapshot("testSnapshotRebuild", "SNAP", false, "");

		PackageItem snap = repo.loadPackageSnapshot("testSnapshotRebuild",
				"SNAP");
		long snapTime = snap.getLastModified().getTimeInMillis();

		Thread.sleep(100);

		impl.rebuildSnapshots();

		PackageItem snap_ = repo.loadPackageSnapshot("testSnapshotRebuild",
				"SNAP");
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
				-1, 0);
		assertEquals(4, res.data.length);

		impl.removeAsset(uuid4);

		res = impl.listAssets(pkgUUID, arr("testRemoveAsset"), -1, 0);
		assertEquals(3, res.data.length);
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
				-1, 0);
		assertEquals(4, res.data.length);

		impl.archiveAsset(uuid4, true);

		res = impl.listAssets(pkgUUID, arr("testArchiveAsset"), -1, 0);
		assertEquals(3, res.data.length);

		impl.archiveAsset(uuid4, false);

		res = impl.listAssets(pkgUUID, arr("testArchiveAsset"), -1, 0);
		assertEquals(4, res.data.length);

	}

	public void testLoadSuggestionCompletionEngine() throws Exception {
		RepositoryService impl = getService();
		String uuid = impl.createPackage("testSuggestionComp", "x");
		PackageConfigData conf = impl.loadPackageConfig(uuid);
		conf.header = "import java.util.List";

		SuggestionCompletionEngine eng = impl
				.loadSuggestionCompletionEngine("testSuggestionComp");
		assertNotNull(eng);

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
		pkg.updateHeader("import org.drools.Person");
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
		assertNull(results);

		pkg = repo.loadPackage("testBinaryPackageCompile");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		assertNotNull(binPackage);

		ByteArrayInputStream bin = new ByteArrayInputStream(binPackage);
		ObjectInputStream in = new DroolsObjectInputStream(bin);
		Package binPkg = (Package) in.readObject();

		assertNotNull(binPkg);
		assertTrue(binPkg.isValid());

		Person p = new Person();

		BinaryRuleBaseLoader loader = new BinaryRuleBaseLoader();
		loader.addPackage(new ByteArrayInputStream(binPackage));
		RuleBase rb = loader.getRuleBase();

		StatelessSession sess = rb.newStatelessSession();
		sess.execute(p);
		assertEquals(42, p.getAge());

		impl.createPackageSnapshot("testBinaryPackageCompile", "SNAP1", false,
				"");

		rule1
				.updateContent("rule 'rule1' \n when p:PersonX() \n then System.err.println(42); \n end");
		rule1.checkin("");

		results = impl.buildPackage(pkg.getUUID(), null, true);
		assertNotNull(results);
		assertEquals(1, results.length);
		assertEquals(rule1.getName(), results[0].assetName);
		assertEquals(AssetFormats.DRL, results[0].assetFormat);
		assertNotNull(results[0].message);
		assertEquals(rule1.getUUID(), results[0].uuid);

		pkg = repo.loadPackageSnapshot("testBinaryPackageCompile", "SNAP1");
		results = impl.buildPackage(pkg.getUUID(), null, true);
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
		pkg.updateHeader("import org.drools.Person");
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

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
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

		ByteArrayInputStream bin = new ByteArrayInputStream(binPackage);
		ObjectInputStream in = new DroolsObjectInputStream(bin);
		Package binPkg = (Package) in.readObject();

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

		results = impl.buildPackage(pkg.getUUID(), null, true);
		assertNotNull(results);
		assertTrue(results.length > 0);
		// assertEquals(2, results.length);
		assertEquals(rule2.getName(), results[0].assetName);
		assertEquals(AssetFormats.BUSINESS_RULE, results[0].assetFormat);
		assertNotNull(results[0].message);
		assertEquals(rule2.getUUID(), results[0].uuid);

		pkg = repo.loadPackageSnapshot("testBinaryPackageCompileBRL", "SNAP1");
		results = impl.buildPackage(pkg.getUUID(), null, true);
		assertNull(results);

		// check that the rule name in the model is being set
		AssetItem asset2 = pkg.addAsset("testSetRuleName", "");
		asset2.updateFormat(AssetFormats.BUSINESS_RULE);
		asset2.checkin("");

		RuleModel model2 = new RuleModel();
		assertNull(model2.name);
		RuleAsset asset = impl.loadRuleAsset(asset2.getUUID());
		asset.content = model2;

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

	public void testPackageSource() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		// create our package
		PackageItem pkg = repo.createPackage("testPackageSource", "");
		pkg.updateHeader("import org.goo.Ber");
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

		pkg.updateHeader("import com.billasurf.Person");

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

		pkg.updateHeader("import com.billasurf.Person");
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
		assertEquals(2, assets.size());
		// now lets copy...
		String newUUID = impl.copyAsset(rule.uuid, rule.metaData.packageName,
				"ruleName2");

		assets = iteratorToList(pkg.getAssets());
		assertEquals(3, assets.size());
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

		pkg.updateHeader("importxxxx");
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
		pkg.updateHeader("import org.goo.Ber");
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when p:Person() \n then p.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		AssetItem rule2 = pkg.addAsset("rule_2", "");
		rule2.updateFormat(AssetFormats.DRL);
		rule2
				.updateContent("rule 'rule2' \n when p:Person() \n then p.setAge(42); \n end");
		rule2.checkin("");
		repo.save();

		String[] list = impl.listRulesInPackage(pkg.getName());
		assertEquals(2, list.length);
		assertEquals("rule1", list[0]);
		assertEquals("rule2", list[1]);

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
		pkg.updateHeader("import org.drools.Person");
		AssetItem rule1 = pkg.addAsset("rule_1", "");
		rule1.updateFormat(AssetFormats.DRL);
		rule1
				.updateContent("rule 'rule1' \n when \np : Person() \n then \np.setAge(42); \n end");
		rule1.checkin("");
		repo.save();

		assertFalse(pkg.isBinaryUpToDate());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));
		impl.ruleBaseCache.remove("XXX");

		BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
		assertNull(results);

		pkg = repo.loadPackage("testBinaryPackageUpToDate");
		byte[] binPackage = pkg.getCompiledPackageBytes();

		assertNotNull(binPackage);

		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertTrue(pkg.isBinaryUpToDate());
		assertTrue(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		RuleAsset asset = impl.loadRuleAsset(rule1.getUUID());
		impl.checkinVersion(asset);

		assertFalse(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertFalse(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		impl.buildPackage(pkg.getUUID(), null, false);

		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertTrue(impl.ruleBaseCache.containsKey(pkg.getUUID()));

		PackageConfigData config = impl.loadPackageConfig(pkg.getUUID());
		impl.savePackage(config);

		assertFalse(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertFalse(pkg.isBinaryUpToDate());
		impl.buildPackage(pkg.getUUID(), null, false);
		assertTrue(pkg.getNode().getProperty("drools:binaryUpToDate")
				.getBoolean());
		assertTrue(pkg.isBinaryUpToDate());

	}

	public void testRunScenario() throws Exception {
		ServiceImplementation impl = getService();
		RulesRepository repo = impl.repository;

		PackageItem pkg = repo.createPackage("testScenarioRun", "");
		pkg.updateHeader("import org.drools.Person");
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

		ScenarioRunResult res = impl.runScenario(pkg.getUUID(), sc);
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());



		res = impl.runScenario(pkg.getUUID(), sc);
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());


		impl.ruleBaseCache.clear();
		res = impl.runScenario(pkg.getUUID(), sc);
		assertEquals(null, res.errors);
		assertNotNull(res.scenario);
		assertTrue(vf.wasSuccessful());
		assertTrue(vr.wasSuccessful());

		//BuilderResult[] results = impl.buildPackage(pkg.getUUID(), null, true);
		//assertNull(results);

	}

	private ServiceImplementation getService() throws Exception {
		ServiceImplementation impl = new ServiceImplementation();

		impl.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());
		return impl;
	}

}