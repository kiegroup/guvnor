package org.drools.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import junit.framework.TestCase;
import org.drools.repository.events.StorageEventManager;

public class LinkedAssetItemTest extends TestCase {

	private RulesRepository getRepo() {
		return RepositorySessionUtil.getRepository();
	}

	private PackageItem getDefaultPackage() {
		return getRepo().loadDefaultPackage();
	}

	public void testAssetItemCreation() throws Exception {

		Calendar now = Calendar.getInstance();

		Thread.sleep(500); // MN: need this sleep to get the correct date

		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestRuleItem",
				"test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestRuleItem", ruleItem1.getUUID(), null);

		assertNotNull(linkedRuleItem1);
		assertNotNull(linkedRuleItem1.getNode());
		assertEquals("LinkedAssetItemlinkedTestRuleItem", linkedRuleItem1.getName());
		assertEquals("LinkedAssetItemtestRuleItem", ruleItem1.getName());

		assertNotNull(ruleItem1.getCreatedDate());
		assertNotNull(linkedRuleItem1.getCreatedDate());

		assertTrue(now.before(ruleItem1.getCreatedDate()));
		assertTrue(now.before(linkedRuleItem1.getCreatedDate()));

		String packName = getDefaultPackage().getName();

		assertEquals(packName, ruleItem1.getPackageName());
		assertEquals(packName, linkedRuleItem1.getPackageName());

		assertNotNull(ruleItem1.getUUID());
		assertNotNull(linkedRuleItem1.getUUID());
		assertTrue(linkedRuleItem1.getUUID() != linkedRuleItem1.getUUID());

		// try constructing with node of wrong type
		try {

			PackageItem pitem = getRepo().loadDefaultPackage();
			new AssetItem(getRepo(), pitem.getNode());
			fail("Exception not thrown for node of wrong type");
		} catch (RulesRepositoryException e) {
			assertNotNull(e.getMessage());
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e);
		}

		// try constructing with node of wrong type
		try {

			PackageItem pitem = getRepo().loadDefaultPackage();
			new LinkedAssetItem(getRepo(), pitem.getNode());
			fail("Exception not thrown for node of wrong type");
		} catch (RulesRepositoryException e) {
			assertNotNull(e.getMessage());
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e);
		}
	}

	public void testGetContentLength() throws Exception {
		RulesRepository repo = getRepo();
		PackageItem pkg = repo.loadDefaultPackage();
		AssetItem asset = pkg.addAsset("LinkedAssetItemtestGetContentLength", "");
		AssetItem linkedAsset = pkg.addLinkedAsset("LinkedAssetItemlinkedTestGetContentLength", asset.getUUID(), null);
		
		assertEquals(0, asset.getContentLength());
		assertEquals(0, linkedAsset.getContentLength());
		asset.updateContent("boo");
		asset.checkin("");
		assertEquals("boo".getBytes().length, asset.getContentLength());
		assertEquals("boo".getBytes().length, linkedAsset.getContentLength());
		
		asset = pkg.addAsset("LinkedAssetItemtestGetContentLength2", "");
		linkedAsset = pkg.addLinkedAsset("LinkedAssetItemlinkedTestGetContentLength2", asset.getUUID(), null);

		assertEquals(0, asset.getContentLength());
		linkedAsset.updateBinaryContentAttachment(new ByteArrayInputStream("foobar"
				.getBytes()));
		linkedAsset.checkin("");
		assertEquals("foobar".getBytes().length, asset.getContentLength());
		assertEquals("foobar".getBytes().length, linkedAsset.getContentLength());
	}

	public void testGetPackageItem() throws Exception {
		RulesRepository repo = getRepo();
		PackageItem def = repo.loadDefaultPackage();
		AssetItem asset = repo.loadDefaultPackage().addAsset("LinkedAssetItemtestPackageItem",
				"test content");
		AssetItem linkedAsset = repo.loadDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestPackageItem",asset.getUUID(), null);

		PackageItem pkg = asset.getPackage();
		assertEquals(def.getName(), pkg.getName());
		assertEquals(def.getUUID(), pkg.getUUID());
		
		PackageItem linkedPkg = linkedAsset.getPackage();
		assertEquals(def.getName(), linkedPkg.getName());
		assertEquals(def.getUUID(), linkedPkg.getUUID());
	}

	public void testUpdateStringProperty() throws Exception {
		RulesRepository repo = getRepo();
		AssetItem asset = repo.loadDefaultPackage().addAsset(
				"LinkedAssetItemtestUpdateStringProperty", "test content");
		AssetItem linkedAsset = repo.loadDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestUpdateStringProperty",asset.getUUID(), null);		
		linkedAsset.updateContent("new content");
		linkedAsset.checkin("");
		Calendar lm = linkedAsset.getLastModified();

		Thread.sleep(100);
		linkedAsset.updateStringProperty("Anything", "AField");

		assertEquals("Anything", linkedAsset.getStringProperty("AField"));
		assertEquals("Anything", asset.getStringProperty("AField"));
		
		Calendar lm1 = asset.getLastModified();
		assertTrue(lm1.getTimeInMillis() > lm.getTimeInMillis());
		Calendar lm2 = asset.getLastModified();
		assertTrue(lm2.getTimeInMillis() > lm.getTimeInMillis());
		
		Thread.sleep(100);

		asset.updateStringProperty("More", "AField", false);

		assertEquals(lm1.getTimeInMillis(), asset.getLastModified()
				.getTimeInMillis());
		assertEquals(lm2.getTimeInMillis(), linkedAsset.getLastModified()
				.getTimeInMillis());
		
		asset.updateContent("more content");
		asset.checkin("");

		asset = repo.loadAssetByUUID(asset.getUUID());
		assertEquals("More", asset.getStringProperty("AField"));
		assertEquals("more content", asset.getContent());		
		linkedAsset = repo.loadAssetByUUID(linkedAsset.getUUID());
		assertEquals("More", linkedAsset.getStringProperty("AField"));
		assertEquals("more content", asset.getContent());
	}

	public void testGetPackageItemHistorical() throws Exception {
		RulesRepository repo = getRepo();
		PackageItem pkg = repo
				.createPackage("LinkedAssetItemtestGetPackageItemHistorical", "");
		AssetItem asset = pkg.addAsset("LinkedAssetItemwhee", "");
		
		//Version 1, created by the original asset
		asset.checkin("");
		assertNotNull(asset.getPackage());

		AssetItem linkedAsset = pkg.addLinkedAsset("LinkedAssetItemlinkedWhee", asset.getUUID(), null);
		
		//Version 2, created by LinkedAssetItem
		linkedAsset.checkin("");
		
		repo.createPackageSnapshot(pkg.getName(), "SNAP");

		PackageItem pkg_ = repo.loadPackageSnapshot(pkg.getName(), "SNAP");
		AssetItem asset_ = pkg_.loadAsset("LinkedAssetItemwhee");
		PackageItem pkg__ = asset_.getPackage();
		assertTrue(pkg__.isSnapshot());
		assertTrue(pkg_.isSnapshot());
		assertFalse(pkg.isSnapshot());
		assertEquals(pkg.getName(), pkg__.getName());
		
		AssetItem linkedAsset_ = pkg_.loadAsset("LinkedAssetItemlinkedWhee");
		PackageItem linkedPkg__ = linkedAsset_.getPackage();
		assertTrue(linkedPkg__.isSnapshot());
		assertFalse(pkg.isSnapshot());
		assertEquals(pkg.getName(), linkedPkg__.getName());
		
		linkedAsset.updateDescription("yeah !");
		
		//Version 3, created by LinkedAssetItem
		linkedAsset.checkin("new");

		linkedAsset = pkg.loadAsset("LinkedAssetItemlinkedWhee");
		assertNotNull(linkedAsset.getPackage());

		AssetHistoryIterator linkedIt = linkedAsset.getHistory();
		assertEquals(4, iteratorToList(linkedIt).size());		
		
		asset = pkg.loadAsset("LinkedAssetItemwhee");
		AssetHistoryIterator it = asset.getHistory();
		assertEquals(4, iteratorToList(it).size());		
	}
	
    List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }

	public void testGetContent() {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetContent", "test content");
		AssetItem linkedRuleItem1 = getRepo().loadDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestGetContent", ruleItem1.getUUID(), null);
		
		linkedRuleItem1.updateContent("test content");
		linkedRuleItem1.updateFormat("drl");

		assertNotNull(linkedRuleItem1.getNode());
		assertEquals("test content", linkedRuleItem1.getContent());
		assertEquals("test content", ruleItem1.getContent());

		assertFalse(linkedRuleItem1.isBinary());
		assertFalse(ruleItem1.isBinary());

		assertNotNull(linkedRuleItem1.getBinaryContentAsBytes());
		assertNotNull(linkedRuleItem1.getBinaryContentAttachment());
		String content = new String(linkedRuleItem1.getBinaryContentAsBytes());
		assertNotNull(content);
		content = new String(ruleItem1.getBinaryContentAsBytes());
		assertNotNull(content);	
	}

	public void testUpdateContent() throws Exception {
		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestUpdateContent",
				"test description");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestUpdateContent",
				ruleItem1.getUUID(), null);

		assertFalse(ruleItem1.getCreator().equals(""));
		assertFalse(linkedRuleItem1.getCreator().equals(""));
		linkedRuleItem1.updateContent("test content");
		linkedRuleItem1.checkin("yeah");

		assertFalse(linkedRuleItem1.getLastContributor().equals(""));
		assertFalse(ruleItem1.getLastContributor().equals(""));

		linkedRuleItem1.updateContent("new rule content");

		assertEquals("new rule content", linkedRuleItem1.getContent());

		assertTrue(linkedRuleItem1.getNode().getSession().hasPendingChanges());
		assertTrue(ruleItem1.getNode().getSession().hasPendingChanges());

		ruleItem1.checkin("yeah !");
		assertFalse(ruleItem1.getNode().getSession().hasPendingChanges());
		assertEquals("yeah !", ruleItem1.getCheckinComment());
		
		try {
		    linkedRuleItem1.checkin("yeah linked !");
		    fail("Did not get expected exception: Unable to checkin");
		} catch (RulesRepositoryException e) {
			
		}

		AssetItem prev = (AssetItem) ruleItem1.getPrecedingVersion();
		assertEquals("test content", prev.getContent());
		assertFalse("yeah !".equals(prev.getCheckinComment()));

		ruleItem1 = getDefaultPackage().loadAsset("LinkedAssetItemtestUpdateContent");
		VersionIterator it = ruleItem1.getNode().getVersionHistory()
				.getAllVersions();

		// and this shows using a version iterator.
		// perhaps migrate to using this rather then next/prev methods.
		// this way, we can skip.
		assertTrue(it.hasNext());
		while (it.hasNext()) {
			Version n = it.nextVersion();
			AssetItem item = new AssetItem(ruleItem1.getRulesRepository(), n);
			assertNotNull(item);
		}
	}

	public void testCategoriesPagination() {
		PackageItem pkg = getRepo().createPackage("LinkedAssetItemtestPagination", "");
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestPagedTag", "description");

		AssetItem a = pkg.addAsset("LinkedAssetItemtestPage1", "test content");
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");
		
		a = pkg.addLinkedAsset("LinkedAssetItemlinkedTestPage1", a.getUUID(), null);
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");		
		
		a = pkg.addAsset("LinkedAssetItemtestPage2", "test content");
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");


		a = pkg.addAsset("LinkedAssetItemtestPage3", "test content");
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");

		a = pkg.addAsset("LinkedAssetItemtestPage4", "test content");
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");

		a = pkg.addAsset("LinkedAssetItemtestPage5", "test content");
		a.addCategory("LinkedAssetItemtestPagedTag");
		a.checkin("");

		AssetPageList list = getRepo().findAssetsByCategory("LinkedAssetItemtestPagedTag", 0,
				-1);
		assertTrue(list.currentPosition > 0);
		assertEquals(5, list.assets.size());
		assertEquals(false, list.hasNext);

		list = getRepo().findAssetsByCategory("LinkedAssetItemtestPagedTag", 0, 2);
		assertTrue(list.currentPosition > 0);
		assertEquals(true, list.hasNext);
		assertEquals(2, list.assets.size());

		assertEquals("LinkedAssetItemtestPage1", ((AssetItem) list.assets.get(0)).getName());
		assertEquals("LinkedAssetItemtestPage2", ((AssetItem) list.assets.get(1)).getName());

		list = getRepo().findAssetsByCategory("LinkedAssetItemtestPagedTag", 2, 2);
		assertTrue(list.currentPosition > 0);
		assertEquals(true, list.hasNext);
		assertEquals(2, list.assets.size());

		assertEquals("LinkedAssetItemtestPage3", ((AssetItem) list.assets.get(0)).getName());
		assertEquals("LinkedAssetItemtestPage4", ((AssetItem) list.assets.get(1)).getName());

		list = getRepo().findAssetsByCategory("LinkedAssetItemtestPagedTag", 2, 3);
		assertTrue(list.currentPosition > 0);
		assertEquals(false, list.hasNext);
		assertEquals(3, list.assets.size());

		assertEquals("LinkedAssetItemtestPage3", ((AssetItem) list.assets.get(0)).getName());
		assertEquals("LinkedAssetItemtestPage4", ((AssetItem) list.assets.get(1)).getName());
		assertEquals("LinkedAssetItemtestPage5", ((AssetItem) list.assets.get(2)).getName());
	}

	public void testCategories() {
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestAddTagTestTag",
				"description");
		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestAddTag",
				"test content");	
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestAddTag", ruleItem1.getUUID(), null);

		linkedRuleItem1.addCategory("LinkedAssetItemtestAddTagTestTag");
		List tags = linkedRuleItem1.getCategories();
		assertEquals(1, tags.size());
		assertEquals("LinkedAssetItemtestAddTagTestTag", ((CategoryItem) tags.get(0))
				.getName());

		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestAddTagTestTag2",
				"description");
		linkedRuleItem1.addCategory("LinkedAssetItemtestAddTagTestTag2");
		tags = linkedRuleItem1.getCategories();
		assertEquals(2, tags.size());

		linkedRuleItem1.checkin("woot");

		// now test retrieve by tags
		List result = getRepo()
				.findAssetsByCategory("LinkedAssetItemtestAddTagTestTag", 0, -1).assets;
		assertEquals(1, result.size());
		AssetItem retItem = (AssetItem) result.get(0);
		assertEquals("LinkedAssetItemtestAddTag", retItem.getName());

		ruleItem1.updateContent("foo");
		ruleItem1.checkin("latest");

		assertTrue(ruleItem1.getCategories().size() > 0);
		assertNotNull(ruleItem1.getCategorySummary());
		assertEquals("LinkedAssetItemtestAddTagTestTag LinkedAssetItemtestAddTagTestTag2 ", ruleItem1
				.getCategorySummary());

		//REVISIT: findAssetsByCategory wont return a LinkedAssetItem
		result = getRepo().findAssetsByCategory("LinkedAssetItemtestAddTagTestTag", 0, -1).assets;

		assertEquals(1, result.size());

		ruleItem1 = (AssetItem) result.get(0);
		assertEquals(2, ruleItem1.getCategories().size());

		assertEquals("foo", ruleItem1.getContent());
		AssetItem prev = (AssetItem) ruleItem1.getPrecedingVersion();
		assertNotNull(prev);
	}

	public void testUpdateCategories() {
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestUpdateCategoriesOnAsset",
				"la");
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestUpdateCategoriesOnAsset2",
				"la");

		AssetItem item = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestUpdateCategoriesOnAsset", "huhuhu");
		AssetItem linkedItem = getRepo().loadDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestUpdateCategoriesOnAsset",
				item.getUUID(), null);
		
		String[] cats = new String[] { "LinkedAssetItemtestUpdateCategoriesOnAsset",
				"LinkedAssetItemtestUpdateCategoriesOnAsset2" };
		linkedItem.updateCategoryList(cats);

		linkedItem.checkin("aaa");

		item = getRepo().loadDefaultPackage().loadAsset(
				"LinkedAssetItemlinkedTestUpdateCategoriesOnAsset");
		assertEquals(2, item.getCategories().size());

		for (Iterator iter = item.getCategories().iterator(); iter.hasNext();) {
			CategoryItem cat = (CategoryItem) iter.next();
			assertTrue(cat.getName().startsWith("LinkedAssetItemtestUpdateCategoriesOnAsset"));
		}
	}

	public void testFindRulesByCategory() throws Exception {
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestFindRulesByCat", "yeah");
		AssetItem as1 = getDefaultPackage().addAsset(
				"LinkedAssetItemtestFindRulesByCategory1", "ya", "LinkedAssetItemtestFindRulesByCat", "drl");
		getDefaultPackage().addAsset("LinkedAssetItemtestFindRulesByCategory2", "ya",
				"LinkedAssetItemtestFindRulesByCat", AssetItem.DEFAULT_CONTENT_FORMAT)
				.checkin("version0");

		as1.checkin("version0");
		
		AssetItem linkedItem = getRepo().loadDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestFindRulesByCategory1",
				as1.getUUID(), "LinkedAssetItemtestFindRulesByCat");

		//REVISIT: findAssetsByCategory wont return a LinkedAssetItem
		List rules = getRepo()
				.findAssetsByCategory("LinkedAssetItemtestFindRulesByCat", 0, -1).assets;
		assertEquals(2, rules.size());

		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			AssetItem element = (AssetItem) iter.next();
			assertTrue(element.getName().startsWith("LinkedAssetItemtestFindRulesByCategory"));
		}
		
		getRepo().loadCategory("/").addCategory("LinkedAssetItemtestFindRulesByCat1", "yeah");
		AssetItem linkedItem1 = getRepo().loadDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestFindRulesByCategory2",
				as1.getUUID(), "LinkedAssetItemtestFindRulesByCat1");
		linkedItem1.checkin("version2");
		rules = getRepo().findAssetsByCategory("LinkedAssetItemtestFindRulesByCat1", 0, -1).assets;
        assertEquals(1, rules.size());

		try {
			getRepo().loadCategory("LinkedAssetItemtestFindRulesByCat").remove();
			fail("should not be able to remove");
		} catch (RulesRepositoryException e) {
			// assertTrue(e.getCause() instanceof
			// ReferentialIntegrityException);
			assertNotNull(e.getMessage());
		}
	}

	public void testRemoveTag() {
		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestRemoveTag",
				"test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset("LinkedAssetItemlinkedTestRemoveTag",
				ruleItem1.getUUID(), null);

		getRepo().loadCategory("/").addCategory("LinkedAssetItemTestRemoveCategory",
				"description");

		linkedRuleItem1.addCategory("LinkedAssetItemTestRemoveCategory");
		List tags = linkedRuleItem1.getCategories();
		assertEquals(1, tags.size());
		linkedRuleItem1.removeCategory("LinkedAssetItemTestRemoveCategory");
		tags = linkedRuleItem1.getCategories();
		assertEquals(0, tags.size());

		getRepo().loadCategory("/").addCategory("LinkedAssetItemTestRemoveCategory2",
				"description");
		getRepo().loadCategory("/").addCategory("LinkedAssetItemTestRemoveCategory3",
				"description");
		linkedRuleItem1.addCategory("LinkedAssetItemTestRemoveCategory2");
		linkedRuleItem1.addCategory("LinkedAssetItemTestRemoveCategory3");
		linkedRuleItem1.removeCategory("LinkedAssetItemTestRemoveCategory2");
		tags = linkedRuleItem1.getCategories();
		assertEquals(1, tags.size());
		assertEquals("LinkedAssetItemTestRemoveCategory3", ((CategoryItem) tags.get(0))
				.getName());
	}

	public void testSetStateString() {
		AssetItem ruleItem1 = getDefaultPackage().addAsset(
				"LinkedAssetItemtestSetStateString", "test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestSetStateString", ruleItem1.getUUID());

		getRepo().createState("TestState1");

		linkedRuleItem1.updateState("TestState1");
		assertNotNull(linkedRuleItem1.getState());
		assertEquals("TestState1", linkedRuleItem1.getState().getName());

		getRepo().createState("TestState2");
		linkedRuleItem1.updateState("TestState2");
		assertNotNull(linkedRuleItem1.getState());
		assertEquals("TestState2", linkedRuleItem1.getState().getName());
	}

	public void testStatusStuff() {
		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestGetState",
				"test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestGetState", ruleItem1.getUUID());
		
		StateItem stateItem1 = linkedRuleItem1.getState();
		assertEquals(StateItem.DRAFT_STATE_NAME, stateItem1.getName());

		linkedRuleItem1.updateState("TestState1");
		assertNotNull(linkedRuleItem1.getState());
		assertEquals("TestState1", linkedRuleItem1.getState().getName());

		ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestGetState2", "wa");
		linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestGetState2", ruleItem1.getUUID());
		assertEquals(StateItem.DRAFT_STATE_NAME, linkedRuleItem1
				.getStateDescription());
		assertEquals(getRepo().getState(StateItem.DRAFT_STATE_NAME), linkedRuleItem1
				.getState());
	}

	public void testToString() {
		AssetItem ruleItem1 = getDefaultPackage().addAsset("LinkedAssetItemtestToString",
				"test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestToString", ruleItem1.getUUID());
		assertNotNull(linkedRuleItem1.toString());
	}

	public void testGetLastModifiedOnCheckin() throws Exception {
		AssetItem ruleItem1 = getDefaultPackage().addAsset(
				"LinkedAssetItemtestGetLastModified", "test content");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedTestGetLastModified", ruleItem1.getUUID());
		
		Calendar cal = Calendar.getInstance();
		long before = cal.getTimeInMillis();

		Thread.sleep(100);
		linkedRuleItem1.updateContent("new lhs");
		linkedRuleItem1.checkin("woot");
		Calendar cal2 = linkedRuleItem1.getLastModified();
		long lastMod = cal2.getTimeInMillis();

		cal = Calendar.getInstance();
		long after = cal.getTimeInMillis();

		assertTrue(before < lastMod);
		assertTrue(lastMod < after);
	}

	public void testGetDateEffective() {
		AssetItem ruleItem1 = getDefaultPackage().addAsset(
				"LinkedAssetItemtestGetDateEffective", "test content");

		// it should be initialized to null
		assertTrue(ruleItem1.getDateEffective() == null);

		// now try setting it, then retrieving it
		Calendar cal = Calendar.getInstance();
		ruleItem1.updateDateEffective(cal);
		Calendar cal2 = ruleItem1.getDateEffective();

		assertEquals(cal, cal2);
	}

	public void testGetDateExpired() {
		try {
			AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
					"LinkedAssetItemtestGetDateExpired", "test content");
			AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
					"LinkedAssetItemlinkedtestGetDateExpired", ruleItem1.getUUID());
			
			// it should be initialized to null
			assertTrue(linkedRuleItem1.getDateExpired() == null);

			// now try setting it, then retrieving it
			Calendar cal = Calendar.getInstance();
			linkedRuleItem1.updateDateExpired(cal);
			Calendar cal2 = linkedRuleItem1.getDateExpired();

			assertEquals(cal, cal2);
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e);
		}
	}

	public void testSaveAndCheckinDescriptionAndTitle() throws Exception {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetDescription", "");
		ruleItem1.checkin("version0");
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetDescription", ruleItem1.getUUID());
		//This check in has no effect on ruleItem1, as nothing related to ruleItem1 has been changed
		linkedRuleItem1.checkin("version1");		

		// it should be "" to begin with
		assertEquals("", linkedRuleItem1.getDescription());

		linkedRuleItem1.updateDescription("test description");
		assertEquals("test description", linkedRuleItem1.getDescription());

		assertTrue(getRepo().getSession().hasPendingChanges());

		linkedRuleItem1.updateTitle("This is a title");
		assertTrue(getRepo().getSession().hasPendingChanges());
		linkedRuleItem1.checkin("ya");

		// we can save without a checkin
		getRepo().getSession().save();

		assertFalse(getRepo().getSession().hasPendingChanges());

		try {
			linkedRuleItem1.getPrecedingVersion().updateTitle("baaad");
			fail("should not be able to do this");
		} catch (RulesRepositoryException e) {
			assertNotNull(e.getMessage());
		}
	}

	public void testGetPrecedingVersionAndRestore() throws Exception {
		getRepo().loadCategory("/").addCategory("LinkedAssetItemfoo", "ka");
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetPrecedingVersion", "descr");
		ruleItem1.checkin("version0");
		assertTrue(ruleItem1.getPrecedingVersion() == null);
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetPrecedingVersion", ruleItem1.getUUID());
		linkedRuleItem1.checkin("version0");		
		assertNotNull(ruleItem1.getPrecedingVersion());
		assertNotNull(linkedRuleItem1.getPrecedingVersion());

		linkedRuleItem1.addCategory("LinkedAssetItemfoo");
		linkedRuleItem1.updateContent("test content");
		linkedRuleItem1.updateDescription("descr2");
		Thread.sleep(100);
		linkedRuleItem1.checkin("boo");

		AssetItem predecessorRuleItem = (AssetItem) linkedRuleItem1
				.getPrecedingVersion();
		assertNotNull(predecessorRuleItem);

		// check version handling
		assertNotNull(predecessorRuleItem.getVersionSnapshotUUID());
		assertFalse(predecessorRuleItem.getVersionSnapshotUUID().equals(
				ruleItem1.getUUID()));

		// assertEquals(predecessorRuleItem.getCreatedDate().getTimeInMillis(),
		// ruleItem1.getCreatedDate().getTimeInMillis());

		assertEquals(ruleItem1.getState().getName(), predecessorRuleItem
				.getState().getName());
		// assertEquals(ruleItem1.getName(), predecessorRuleItem.getName());

		AssetItem loadedHistorical = getRepo().loadAssetByUUID(
				predecessorRuleItem.getVersionSnapshotUUID());
		assertTrue(loadedHistorical.isHistoricalVersion());
		assertFalse(ruleItem1.getVersionNumber() == loadedHistorical
				.getVersionNumber());

		linkedRuleItem1.updateContent("new content");
		linkedRuleItem1.checkin("two changes");

		predecessorRuleItem = (AssetItem) linkedRuleItem1.getPrecedingVersion();
		assertNotNull(predecessorRuleItem);
		assertEquals(1, predecessorRuleItem.getCategories().size());
		CategoryItem cat = (CategoryItem) predecessorRuleItem.getCategories()
				.get(0);
		assertEquals("LinkedAssetItemfoo", cat.getName());

		assertEquals("test content", predecessorRuleItem.getContent());

		assertEquals(RulesRepository.DEFAULT_PACKAGE, predecessorRuleItem
				.getPackageName());

		linkedRuleItem1.updateContent("newer lhs");
		linkedRuleItem1.checkin("another");

		predecessorRuleItem = (AssetItem) linkedRuleItem1.getPrecedingVersion();
		assertNotNull(predecessorRuleItem);
		assertEquals("new content", predecessorRuleItem.getContent());
		predecessorRuleItem = (AssetItem) predecessorRuleItem
				.getPrecedingVersion();
		assertNotNull(predecessorRuleItem);
		assertEquals("test content", predecessorRuleItem.getContent());

		// now try restoring
		long oldVersionNumber = ruleItem1.getVersionNumber();

		AssetItem toRestore = getRepo().loadAssetByUUID(
				predecessorRuleItem.getVersionSnapshotUUID());

		getRepo().restoreHistoricalAsset(toRestore, linkedRuleItem1,
				"cause I want to");

		AssetItem restored = getRepo().loadDefaultPackage().loadAsset(
				"LinkedAssetItemtestGetPrecedingVersion");

		// assertEquals( predecessorRuleItem.getCheckinComment(),
		// restored.getCheckinComment());
		assertEquals(predecessorRuleItem.getDescription(), restored
				.getDescription());
		assertEquals("cause I want to", restored.getCheckinComment());
		assertEquals(6, restored.getVersionNumber());
		assertFalse(oldVersionNumber == restored.getVersionNumber());
	}

	public void testGetSucceedingVersion() {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetSucceedingVersion", "test description");
		ruleItem1.checkin("version0");
		assertEquals(1, ruleItem1.getVersionNumber());
		
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetSucceedingVersion", ruleItem1.getUUID());
		linkedRuleItem1.checkin("version1");	
		assertEquals(2, linkedRuleItem1.getVersionNumber());
		
		AssetItem succeedingRuleItem = (AssetItem) linkedRuleItem1
				.getSucceedingVersion();
		assertTrue(succeedingRuleItem == null);

		linkedRuleItem1.updateContent("new content");
		linkedRuleItem1.checkin("la");

		assertEquals(3, linkedRuleItem1.getVersionNumber());

		AssetItem predecessorRuleItem = (AssetItem) linkedRuleItem1
				.getPrecedingVersion();
		assertEquals("", predecessorRuleItem.getContent());
		succeedingRuleItem = (AssetItem) predecessorRuleItem
				.getSucceedingVersion();
		assertNotNull(succeedingRuleItem);
		assertEquals(linkedRuleItem1.getContent(), succeedingRuleItem.getContent());
	}

	public void testGetSuccessorVersionsIterator() {
		try {
			AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
					"LinkedAssetItemtestGetSuccessorVersionsIterator", "test content");
			ruleItem1.checkin("version0");
			
			AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
					"LinkedAssetItemlinkedtestGetSuccessorVersionsIterator", ruleItem1.getUUID());
			linkedRuleItem1.checkin("version1");	
			
			Iterator iterator = linkedRuleItem1.getSuccessorVersionsIterator();
			assertNotNull(iterator);
			assertFalse(iterator.hasNext());

			linkedRuleItem1.updateContent("new content").checkin("ya");

			iterator = linkedRuleItem1.getSuccessorVersionsIterator();
			assertNotNull(iterator);
			assertFalse(iterator.hasNext());

			AssetItem predecessorRuleItem = (AssetItem) linkedRuleItem1
					.getPrecedingVersion();
			iterator = predecessorRuleItem.getSuccessorVersionsIterator();
			assertNotNull(iterator);
			assertTrue(iterator.hasNext());
			AssetItem nextRuleItem = (AssetItem) iterator.next();
			assertEquals("new content", nextRuleItem.getContent());
			assertFalse(iterator.hasNext());

			linkedRuleItem1.updateContent("newer content");
			linkedRuleItem1.checkin("boo");

			iterator = predecessorRuleItem.getSuccessorVersionsIterator();
			assertNotNull(iterator);
			assertTrue(iterator.hasNext());
			nextRuleItem = (AssetItem) iterator.next();
			assertEquals("new content", nextRuleItem.getContent());
			assertTrue(iterator.hasNext());
			nextRuleItem = (AssetItem) iterator.next();
			assertEquals("newer content", nextRuleItem.getContent());
			assertFalse(iterator.hasNext());
		} catch (Exception e) {
			fail("Caught unexpected exception: " + e);
		}
	}

	public void testGetPredecessorVersionsIterator() {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetPredecessorVersionsIterator", "test description");
		ruleItem1.checkin("version0");

		Iterator iterator = ruleItem1.getPredecessorVersionsIterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
		
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetPredecessorVersionsIterator", ruleItem1.getUUID());
		linkedRuleItem1.checkin("version1");	

		ruleItem1.updateContent("test content");
		ruleItem1.checkin("lalalalala");

		iterator = linkedRuleItem1.getPredecessorVersionsIterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());

		ruleItem1.updateContent("new content");
		ruleItem1.checkin("boo");

		iterator = linkedRuleItem1.getPredecessorVersionsIterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		AssetItem nextRuleItem = (AssetItem) iterator.next();

		assertEquals("test content", nextRuleItem.getContent());

		ruleItem1.updateContent("newer content");
		ruleItem1.checkin("wee");

		iterator = linkedRuleItem1.getPredecessorVersionsIterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		nextRuleItem = (AssetItem) iterator.next();
		assertTrue(iterator.hasNext());
		assertEquals("new content", nextRuleItem.getContent());
		nextRuleItem = (AssetItem) iterator.next();

		assertEquals("test content", nextRuleItem.getContent());

		assertEquals("", ((AssetItem) iterator.next()).getContent());

	}

	public void testHistoryIterator() throws Exception {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestHistoryIterator", "test description");
		ruleItem1.checkin("version0");
		
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestHistoryIterator", ruleItem1.getUUID());
		linkedRuleItem1.checkin("version1");	
		
		ruleItem1 = getRepo().loadAssetByUUID(linkedRuleItem1.getUUID());
		ruleItem1.updateContent("wo");
		ruleItem1.checkin("version2");

		ruleItem1 = getRepo().loadAssetByUUID(ruleItem1.getUUID());
		ruleItem1.updateContent("ya");
		ruleItem1.checkin("version3");

		Iterator it = ruleItem1.getHistory();
		for (int i = 0; i < 3; i++) {
			assertTrue(it.hasNext());
			it.next();
		}
	}

	public void testGetTitle() {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetTitle", "test content");
		
		AssetItem linkedRuleItem1 = getDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetTitle", ruleItem1.getUUID());		

		assertEquals("LinkedAssetItemlinkedtestGetTitle", linkedRuleItem1.getName());
		assertEquals("LinkedAssetItemtestGetTitle", ruleItem1.getName());
		//NOTE: LinkedAssetItem does not have its own Title property.
		assertEquals("LinkedAssetItemtestGetTitle", linkedRuleItem1.getTitle());
		assertEquals("LinkedAssetItemtestGetTitle", ruleItem1.getTitle());
	}

	public void testDublinCoreProperties() {
		PackageItem pkg = getRepo().createPackage("LinkedAssetItemtestDublinCore", "wa");

		AssetItem ruleItem = pkg.addAsset("LinkedAssetItemtestDublinCoreProperties",
				"yeah yeah yeah");
		ruleItem.checkin("woo");
		
		AssetItem linkedRuleItem1 = pkg.addLinkedAsset(
				"LinkedAssetItemlinkedtestDublinCoreProperties", ruleItem.getUUID());
		
		linkedRuleItem1.updateCoverage("b");
		assertEquals("b", linkedRuleItem1.getCoverage());
		linkedRuleItem1.checkin("woo");

		ruleItem = getRepo().loadPackage("LinkedAssetItemtestDublinCore").loadAsset("LinkedAssetItemtestDublinCoreProperties");
		assertEquals("b", ruleItem.getCoverage());
		assertEquals("", ruleItem.getExternalRelation());
		assertEquals("", ruleItem.getExternalSource());
		
		ruleItem = getRepo().loadPackage("LinkedAssetItemtestDublinCore").loadAsset("LinkedAssetItemlinkedtestDublinCoreProperties");
		assertEquals("b", ruleItem.getCoverage());
		assertEquals("", ruleItem.getExternalRelation());
		assertEquals("", ruleItem.getExternalSource());
	}

	public void testGetFormat() throws Exception {
		AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestGetFormat", "test content");
		
		AssetItem linkedRuleItem1 = getRepo().loadDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestGetFormat", ruleItem1.getUUID());
		
		linkedRuleItem1.updateContent("la");
		assertEquals(AssetItem.DEFAULT_CONTENT_FORMAT, linkedRuleItem1.getFormat());

		assertTrue(linkedRuleItem1.getNode().hasProperty(
				AssetItem.CONTENT_PROPERTY_NAME));
		assertFalse(linkedRuleItem1.getNode().hasProperty(
				AssetItem.CONTENT_PROPERTY_BINARY_NAME));

		linkedRuleItem1.updateFormat("blah");
		assertEquals("blah", linkedRuleItem1.getFormat());
	}

	public void testAnonymousProperties() {
		AssetItem item = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemanonymousproperty", "lalalalala");
		
		AssetItem linkedRuleItem1 = getRepo().loadDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedanonymousproperty", item.getUUID());
		
		linkedRuleItem1.updateUserProperty("fooBar", "value");
		assertEquals("value", linkedRuleItem1.getUserProperty("fooBar"));

		linkedRuleItem1.checkin("lalalala");
		try {
			linkedRuleItem1.updateUserProperty("drools:content", "whee");
			fail("should not be able to set built in properties this way.");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	public void testBinaryAsset() throws Exception {
		AssetItem item = getRepo().loadDefaultPackage().addAsset(
				"LinkedAssetItemtestBinaryAsset", "yeah");
		
		AssetItem linkedRuleItem1 = getRepo().loadDefaultPackage().addLinkedAsset(
				"LinkedAssetItemlinkedtestBinaryAsset", item.getUUID());
		
		String data = "abc 123";
		ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
		linkedRuleItem1.updateBinaryContentAttachment(in);
		linkedRuleItem1.updateBinaryContentAttachmentFileName("x.x");
		in.close();

		assertEquals(data, linkedRuleItem1.getContent());

		assertFalse(linkedRuleItem1.getNode().hasProperty(AssetItem.CONTENT_PROPERTY_NAME));
		assertTrue(linkedRuleItem1.getNode().hasProperty(
				AssetItem.CONTENT_PROPERTY_BINARY_NAME));
		linkedRuleItem1.checkin("lalalala");

		assertTrue(linkedRuleItem1.isBinary());

		item = getRepo().loadDefaultPackage().loadAsset("LinkedAssetItemtestBinaryAsset");
		InputStream in2 = item.getBinaryContentAttachment();
		assertNotNull(in2);

		byte[] data2 = item.getBinaryContentAsBytes();
		assertEquals(data, new String(data2));
		assertEquals("x.x", item.getBinaryContentAttachmentFileName());
		assertTrue(item.isBinary());

		linkedRuleItem1.updateContent("qed");
		linkedRuleItem1.checkin("");
		item = getRepo().loadAssetByUUID(item.getUUID());
		assertEquals("qed", item.getContent());
	}

}
