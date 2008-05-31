package org.drools.brms.server.files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

import junit.framework.Assert;
import junit.framework.TestCase;

public class WebDAVImplTest extends TestCase {

	public void testPath() {
		WebDAVImpl imp = new WebDAVImpl(new File(""));
		String[] path = imp.getPath("http://goober/whee/webdav/packages/packagename/resource.drl");
		assertEquals("packages", path[0]);
		assertEquals("packagename", path[1]);
		assertEquals("resource.drl", path[2]);

		path = imp.getPath("foo/webdav");
		assertEquals(0, path.length);

	}

	public void testBadCopy() throws Exception {
		//OSX does stupid shit when copying in the same directory
		//for instance, it creates the copy as foobar.x copy - totally hosing
		//the file extension.
		WebDAVImpl imp = new WebDAVImpl(new File(""));
		try {
			imp.objectExists("/foo/webdav/packages/foobar/Something.drl copy 42");
			fail("should not be allowed");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}

	}

	public void testListRoot() throws Exception {
		WebDAVImpl imp = new WebDAVImpl(new File(""));
		String[] children = imp.getChildrenNames("foobar/webdav");
		assertEquals(2, children.length);
		assertEquals("packages", children[0]);
		assertEquals("snapshots", children[1]);
	}

	public void testChildrenNames() throws Exception {
		WebDAVImpl imp = getImpl();
		RulesRepository repo = imp.getRepo();
		String[] children = imp.getChildrenNames("http://goo/webdav/packages");
		assertTrue(children.length > 0);
		int packageCount = children.length;

		PackageItem pkg = repo.createPackage("testWebDavChildNames1", "");
		repo.createPackage("testWebDavChildNames2", "");
		repo.save();
		children = imp.getChildrenNames("http://goo/webdav/packages");
		assertEquals(packageCount + 2, children.length);
		assertContains("testWebDavChildNames1", children);
		assertContains("testWebDavChildNames2", children);

		AssetItem asset = pkg.addAsset("asset1", "something");
		asset.updateFormat("drl");
		asset.checkin("");
		asset = pkg.addAsset("asset2", "something");
		asset.updateFormat("dsl");
		asset.checkin("");

		children = imp.getChildrenNames("foo/webdav/packages/testWebDavChildNames1");
		assertEquals(2, children.length);
		assertEquals("asset1.drl", children[0]);
		assertEquals("asset2.dsl", children[1]);

		children = imp.getChildrenNames("foo/webdav/packages/testWebDavChildNames1/asset1.drl");
		assertNull(children);


	}

	private WebDAVImpl getImpl() throws Exception {
		return new WebDAVImpl(new RulesRepository(TestEnvironmentSessionHelper.getSession(true)));
	}

	public void testCreateFolder() throws Exception {
		WebDAVImpl imp = getImpl();
		RulesRepository repo = imp.getRepo();
		String[] children = imp.getChildrenNames("http://goo/webdav/packages");
		int packageCount = children.length;

		imp.createFolder("foo/bar/webdav/packages/testCreateWebDavFolder");
		children = imp.getChildrenNames("http://goo/webdav/packages");

		assertEquals(packageCount+1, children.length);
		assertContains("testCreateWebDavFolder", children);

		PackageItem pkg = repo.loadPackage("testCreateWebDavFolder");
		assertNotNull(pkg);

		pkg.addAsset("someAsset", "");


		try {
			imp.createFolder("foo/bar/webdav/somethingElse");
			fail("this should not work !");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e.getMessage());
		}

	}

	public void testDates() throws Exception {
		String uri = "/foo/webdav";
		WebDAVImpl imp = getImpl();
		assertNotNull(imp.getCreationDate(uri));
		assertNotNull(imp.getLastModified(uri));

		uri = "/foo/webdav/packages";
		assertNotNull(imp.getCreationDate(uri));
		assertNotNull(imp.getLastModified(uri));

	}

	public void testCreateResourceAndCreatedDate() throws Exception {
		WebDAVImpl imp = getImpl();
		RulesRepository repo = imp.getRepo();
		imp.createFolder("foo/bar/webdav/packages/testCreateResourceDAVFolder");

		Thread.sleep(100);

		imp.createResource("fpp/bar/webdav/packages/testCreateResourceDAVFolder/asset.drl");

		String[] resources = imp.getChildrenNames("foo/bar/webdav/packages/testCreateResourceDAVFolder");
		assertEquals(1, resources.length);
		assertEquals("asset.drl", resources[0]);

		//should be ignored
		imp.createResource("fpp/bar/webdav/packages/testCreateResourceDAVFolder/._asset.drl");
		imp.createResource("fpp/bar/webdav/packages/.DS_Store");


		PackageItem pkg = repo.loadPackage("testCreateResourceDAVFolder");
		assertFalse(pkg.containsAsset("._asset"));
		assertTrue(pkg.containsAsset("asset"));

		Iterator<AssetItem> it = pkg.getAssets();
		AssetItem ass = it.next();
		assertEquals("asset", ass.getName());
		assertEquals("drl", ass.getFormat());


		Date create = imp.getCreationDate("foo/bar/webdav/packages/testCreateResourceDAVFolder");
		assertNotNull(create);
		assertTrue(create.after(new Date("10-Jul-1974")));

		Date assetCreate = imp.getCreationDate("fpp/bar/webdav/packages/testCreateResourceDAVFolder/asset.drl");
		assertTrue(assetCreate.after(create));


		Date lm = imp.getLastModified("foo/bar/webdav/packages/testCreateResourceDAVFolder");
		assertNotNull(lm);
		assertTrue(lm.after(new Date("10-Jul-1974")));

		Date alm = imp.getLastModified("fpp/bar/webdav/packages/testCreateResourceDAVFolder/asset.drl");
		assertTrue(alm.after(lm));


		try {
			imp.createResource("boo/bar/webdav/hummer.drl");
			fail("Shouldn't be able to do this");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e.getMessage());
		}



	}

	public void testResourceContent() throws Exception {
		WebDAVImpl imp = getImpl();
		RulesRepository repo = imp.getRepo();
		PackageItem pkg = repo.createPackage("testWebDAVContent", "");

		AssetItem asset = pkg.addAsset("asset", "something");
		asset.updateFormat("drl");
		asset.updateContent("Some content");
		asset.checkin("");
		InputStream data = imp.getResourceContent("foo/webdav/packages/testWebDAVContent/asset.drl");
		assertEquals("Some content", IOUtils.toString(data));

		asset = pkg.addAsset("asset2", "something");
		asset.updateFormat("xls");
		asset.updateBinaryContentAttachment(IOUtils.toInputStream("This is binary"));
		asset.checkin("");

		data = imp.getResourceContent("foo/webdav/packages/testWebDAVContent/asset2.xls");
		assertEquals("This is binary", IOUtils.toString(data));


		AssetItem asset_ = pkg.addAsset("somethingelse", "");
		asset_.updateFormat("drl");
		asset_.checkin("");

		data = imp.getResourceContent("foo/webdav/packages/testWebDAVContent/somethingelse.drl");
		assertEquals("", IOUtils.toString(data));





	}

	public void testIsFolder() throws Exception {
		WebDAVImpl imp = getImpl();
		assertTrue(imp.isFolder("/com/foo/webdav"));
		assertTrue(imp.isFolder("/com/foo/webdav/"));
		assertTrue(imp.isFolder("/com/foo/webdav/packages"));
		assertTrue(imp.isFolder("/com/foo/webdav/packages/"));
		assertFalse(imp.isFolder("/com/foo/webdav/packages/somePackage"));

		imp.createFolder("/com/foo/webdav/packages/testDAVIsFolder");
		assertTrue(imp.isFolder("/com/foo/webdav/packages/testDAVIsFolder"));
		assertFalse(imp.isFolder("/com/foo/webdav/packages/somePackage/SomeFile.drl"));
	}

	public void testIsResource() throws Exception {
		WebDAVImpl imp = getImpl();
		assertFalse(imp.isResource("/com/foo/webdav/packages"));
		assertFalse(imp.isResource("/com/foo/webdav/packages/somePackage"));
		assertFalse(imp.isResource("/com/foo/webdav/packages/somePackage/SomeFile.drl"));

		imp.createFolder("/com/foo/webdav/packages/testDAVIsResource");
		imp.createResource("/com/foo/webdav/packages/testDAVIsResource/SomeFile.drl");

		assertTrue(imp.isResource("/com/foo/webdav/packages/testDAVIsResource/SomeFile.drl"));

	}

	public void testResourceLength() throws Exception {
		WebDAVImpl imp = getImpl();
		assertEquals(0, imp.getResourceLength("foo/bar/webdav/packages"));
		imp.createFolder("/foo/webdav/packages/testResourceLengthDAV");
		imp.createResource("/foo/webdav/packages/testResourceLengthDAV/testResourceLength");
		assertEquals(0, imp.getResourceLength("/foo/webdav/packages/testResourceLengthDAV/testResourceLength"));
		imp.setResourceContent("/foo/webdav/packages/testResourceLengthDAV/testResourceLength", IOUtils.toInputStream("some input"), null, null);
		assertEquals("some input".getBytes().length, imp.getResourceLength("/foo/webdav/packages/testResourceLengthDAV/testResourceLength"));

	}


	public void testObjectExists() throws Exception {
		WebDAVImpl imp = getImpl();
		assertFalse(imp.objectExists("foo/webdav/bar"));
		assertTrue(imp.objectExists("foo/webdav"));
		assertTrue(imp.objectExists("foo/webdav/packages"));

		imp.createFolder("foo/webdav/packages/testDavObjectExists");
		assertTrue(imp.objectExists("foo/webdav/packages/testDavObjectExists"));
		assertFalse(imp.objectExists("foo/webdav/packages/testDavObjectExistsXXXX"));
		assertFalse(imp.objectExists("foo/webdav/packages/testDavObjectExists/foobar.drl"));
		assertFalse(imp.objectExists("foo/webdav/packages/testDavObjectExistsXXXX/foobar.drl"));
	}


	public void testRemoveObject() throws Exception {
		WebDAVImpl imp = getImpl();
		assertFalse(imp.objectExists("foo/webdav/packages/testDavRemoveObjectFolder"));
		imp.createFolder("foo/webdav/packages/testDavRemoveObjectFolder");
		assertTrue(imp.objectExists("foo/webdav/packages/testDavRemoveObjectFolder"));
		imp.removeObject("foo/webdav/packages/testDavRemoveObjectFolder");
		assertFalse(imp.objectExists("foo/webdav/packages/testDavRemoveObjectFolder"));


		imp.createFolder("foo/webdav/packages/testDavRemoveObjectAsset");
		imp.createResource("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl");

		AssetItem as = imp.getRepo().loadPackage("testDavRemoveObjectAsset").loadAsset("asset");
		long origVer = as.getVersionNumber();

		assertTrue(imp.objectExists("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl"));
		imp.removeObject("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl");
		assertFalse(imp.objectExists("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl"));
		assertTrue(imp.objectExists("foo/webdav/packages/testDavRemoveObjectAsset"));

		imp.createResource("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl");
		assertTrue(imp.objectExists("foo/webdav/packages/testDavRemoveObjectAsset/asset.drl"));

		as = imp.getRepo().loadPackage("testDavRemoveObjectAsset").loadAsset("asset");
		assertTrue(as.getVersionNumber() > origVer);
		imp.createFolder("foo/webdav/packages/testDavRemoveObjectFolder");
		assertTrue(imp.objectExists("foo/webdav/packages/testDavRemoveObjectFolder"));


	}

	public void testShouldCheckIn() throws Exception {
		WebDAVImpl imp  = getImpl();
		Calendar recently = Calendar.getInstance();
		recently.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - 1000);
		assertFalse(imp.shouldCreateNewVersion(recently));
		recently.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - 86400001);
		assertTrue(imp.shouldCreateNewVersion(recently));

	}

	public void testSetContent() throws Exception {
		WebDAVImpl imp  = getImpl();
		imp.createFolder("/foo/webdav/packages/testSetDavContent");
		imp.createResource("/foo/webdav/packages/testSetDavContent/Something.drl");
		imp.setResourceContent("/foo/webdav/packages/testSetDavContent/Something.drl", IOUtils.toInputStream("some input"), null, null);

		AssetItem as = imp.getRepo().loadPackage("testSetDavContent").loadAsset("Something");
		assertTrue(as.isBinary());


		String result = IOUtils.toString(imp.getResourceContent("/foo/webdav/packages/testSetDavContent/Something.drl"));
		assertEquals("some input", result);

		PackageItem pkg = imp.getRepo().loadPackage("testSetDavContent");
		AssetItem asset = pkg.loadAsset("Something");
		assertEquals("drl", asset.getFormat());
		assertEquals("some input", asset.getContent());
		assertEquals("some input", IOUtils.toString(asset.getBinaryContentAttachment()));

		imp.setResourceContent("/foo/webdav/packages/testSetDavContent/Something.drl", IOUtils.toInputStream("some more input"), null, null);
		result = IOUtils.toString(imp.getResourceContent("/foo/webdav/packages/testSetDavContent/Something.drl"));
		assertEquals("some more input", result);


	}

	public void testSnapshot() throws Exception {
		WebDAVImpl imp  = getImpl();
		imp.createFolder("/foo/webdav/packages/testDavSnapshot");
		imp.createResource("/foo/webdav/packages/testDavSnapshot/Something.drl");
		imp.setResourceContent("/foo/webdav/packages/testDavSnapshot/Something.drl", IOUtils.toInputStream("some input"), null, null);

		RulesRepository repo = imp.getRepo();

		repo.createPackageSnapshot("testDavSnapshot", "SNAP1");
		repo.createPackageSnapshot("testDavSnapshot", "SNAP2");

		String[] packages = imp.getChildrenNames("/foo/webdav/snapshots");
		assertTrue(packages.length > 0);
		assertContains("testDavSnapshot", packages);

		String[] snaps = imp.getChildrenNames("/foo/webdav/snapshots/testDavSnapshot");
		assertEquals(2, snaps.length);

		assertEquals("SNAP1", snaps[0]);
		assertEquals("SNAP2", snaps[1]);


		String[] list = imp.getChildrenNames("/foo/webdav/snapshots/testDavSnapshot/SNAP1");
		assertEquals(1, list.length);
		assertEquals("Something.drl", list[0]);

		list = imp.getChildrenNames("/foo/webdav/snapshots/testDavSnapshot/SNAP2");
		assertEquals(1, list.length);
		assertEquals("Something.drl", list[0]);

		assertNotNull(imp.getCreationDate("/foo/webdav/snapshots"));
		assertNotNull(imp.getCreationDate("/foo/webdav/snapshots/testDavSnapshot"));
		assertNotNull(imp.getCreationDate("/foo/webdav/snapshots/testDavSnapshot/SNAP1"));
		assertNotNull(imp.getCreationDate("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl"));

		assertNotNull(imp.getLastModified("/foo/webdav/snapshots"));
		assertNotNull(imp.getLastModified("/foo/webdav/snapshots/testDavSnapshot"));
		assertNotNull(imp.getLastModified("/foo/webdav/snapshots/testDavSnapshot/SNAP1"));
		assertNotNull(imp.getLastModified("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl"));

		createFolderTry(imp, "/foo/webdav/snapshots/randomAss");
		createFolderTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAPX");
		createFolderTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl");
		createFolderTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAP1/Another.drl");

		createResourceTry(imp, "/foo/webdav/snapshots/randomAss");
		createResourceTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAPX");
		createResourceTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl");
		createResourceTry(imp, "/foo/webdav/snapshots/testDavSnapshot/SNAP1/Another.drl");


		InputStream in = imp.getResourceContent("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl");
		assertEquals("some input", IOUtils.toString(in));


		assertEquals(0, imp.getResourceLength("/foo/webdav/snapshots/testDavSnapshot/SNAP1"));
		assertEquals("some input".getBytes().length, imp.getResourceLength("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl"));


		assertTrue(imp.isFolder("/foo/webdav/snapshots"));
		assertTrue(imp.isFolder("/foo/webdav/snapshots/testDavSnapshot"));
		assertTrue(imp.isFolder("/foo/webdav/snapshots/testDavSnapshot/SNAP2"));
		assertFalse(imp.isFolder("/foo/webdav/snapshots/testDavSnapshot/SNAP2/Something.drl"));

		assertFalse(imp.isResource("/foo/webdav/snapshots"));
		assertFalse(imp.isResource("/foo/webdav/snapshots/testDavSnapshot"));
		assertFalse(imp.isResource("/foo/webdav/snapshots/testDavSnapshot/SNAP2"));
		assertTrue(imp.isResource("/foo/webdav/snapshots/testDavSnapshot/SNAP2/Something.drl"));

		assertFalse(imp.isResource("/foo/webdav/snapshots/testDavSnapshot/SNAP2/DoesNotExist.drl"));

		assertTrue(imp.objectExists("/foo/webdav/snapshots"));
		assertFalse(imp.objectExists("/foo/webdav/snapshots/testDavSnapshotXX"));
		assertTrue(imp.objectExists("/foo/webdav/snapshots/testDavSnapshot"));
		assertTrue(imp.objectExists("/foo/webdav/snapshots/testDavSnapshot/SNAP1"));
		assertFalse(imp.objectExists("/foo/webdav/snapshots/testDavSnapshot/SNAPX"));

		assertFalse(imp.objectExists("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Foo.drl"));
		assertTrue(imp.objectExists("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl"));

		assertNull(imp.getChildrenNames("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl"));

		try {
			imp.removeObject("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl");
			fail("Should not delete files from snapshots");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}

		try {
			imp.setResourceContent("/foo/webdav/snapshots/testDavSnapshot/SNAP1/Something.drl", null, null, null);
			fail("should not be allowed to update content in snapshots.");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}

	}

	private void createResourceTry(WebDAVImpl imp, String path) {
		try {
			imp.createResource(path);
			fail("Should not be allowed");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e.getMessage());
		}
	}

	private void createFolderTry(WebDAVImpl imp, String path) {
		try {
			imp.createFolder(path);
			fail("should not be allowed");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e.getMessage());
		}
	}

	public void testThreadLocal() throws Exception {
		Thread t = new Thread(new Runnable() {
			public void run()  {
				WebDAVImpl i = new WebDAVImpl();
				assertNull(i.getRepo());
				try {
					i.begin(null);
				} catch (Exception e) {
					fail("should not happen");
				}
				assertNotNull(i.getRepo());
			}
		});
		t.start();
		t.join();
	}


	private void assertContains(String string, String[] children) {
		for (int i = 0; i < children.length; i++) {
			if (children[i].equals(string)) {
				return;
			}
		}
		Assert.fail("Array did not contain " + string);
	}


}
