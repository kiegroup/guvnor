package org.drools.repository.remoteapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.RulesRepositoryTest;
import org.drools.repository.remoteapi.Response.Binary;
import org.drools.repository.remoteapi.Response.Text;

public class RestAPITest extends TestCase {
	String someAsset = "packages/SomeName/SomeFile.drl";
	String getAList = "packages/SomeName"; //will show a list
	String getPackageConfig = "packages/SomeName/.package"; //should load package config


	public void testGetBasics() throws Exception {


		RulesRepository repo = RepositorySessionUtil.getRepository();
		PackageItem pkg = repo.createPackage("testRestGetBasics", "");
		pkg.updateHeader("This is some header");
		repo.save();


		AssetItem asset1 = pkg.addAsset("asset1", "");
		asset1.updateContent("this is content");
		asset1.updateFormat("drl");
		asset1.checkin("");

		AssetItem asset2 = pkg.addAsset("asset2", "");
		asset2.updateContent("this is content");
		asset2.updateFormat("xml");
		asset2.checkin("");

		AssetItem asset3 = pkg.addAsset("asset3", "");
		ByteArrayInputStream in = new ByteArrayInputStream("abc".getBytes());
		asset3.updateBinaryContentAttachment(in);
		asset3.updateFormat("xls");
		asset3.checkin("");

		assertTrue(asset3.isBinary());
		assertFalse(asset1.isBinary());

		RestAPI api = new RestAPI(repo);

		//this should get us the package configuration

		String url = "packages/testRestGetBasics/.package";
		Response res = api.get(url);
		assertNotNull(res.lastModified);
		assertEquals(pkg.getLastModified(), res.lastModified);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		res.writeData(out);

		String dotPackage = new String(out.toByteArray());
		assertEquals(pkg.getHeader(), dotPackage);

		res = api.get("packages/testRestGetBasics");
		assertTrue(res instanceof Text);
		assertNotNull(res.lastModified);
		out = new ByteArrayOutputStream();
		res.writeData(out);
		String listing = new String(out.toByteArray());
		assertNotNull(listing);

		Properties p = new Properties();
		p.load(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(3, p.size());

		assertTrue(p.containsKey("asset1.drl"));
		assertTrue(p.containsKey("asset2.xml"));
		assertTrue(p.containsKey("asset3.xls"));

		assertNotNull(p.getProperty("asset1.drl"));
		String prop = p.getProperty("asset1.drl");
		System.err.println(prop);
		String[] dt = prop.split(",");


		SimpleDateFormat sdf = RestAPI.getISODateFormat();
		Date d= sdf.parse(dt[0]);
		assertNotNull(d);

		assertEquals(sdf.format(asset1.getLastModified().getTime()), dt[0]);
		assertEquals(asset1.getVersionNumber(), Long.parseLong(dt[1]));


		//try text
		res = api.get("packages/testRestGetBasics/asset1.drl");
		assertTrue(res instanceof Text);
		out = new ByteArrayOutputStream();
		assertNotNull(res.lastModified);
		assertTrue(res.lastModified.getTime().after(sdf.parse("2000-04-14T18:36:37")));
		res.writeData(out);

		String s = new String(out.toByteArray());
		assertEquals(asset1.getContent(), s);


		//now binary
		res = api.get("packages/testRestGetBasics/asset3.xls");
		assertTrue(res instanceof Binary);
		out = new ByteArrayOutputStream();
		assertNotNull(res.lastModified);
		assertTrue(res.lastModified.getTime().after(sdf.parse("2000-04-14T18:36:37")));
		res.writeData(out);

		byte[] data = out.toByteArray();
		assertNotNull(data);

		assertEquals("abc", new String(data));


	}

	public void testPost() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		PackageItem pkg = repo.createPackage("testRestPost", "");
		pkg.updateHeader("This is some header");
		repo.save();

		RestAPI api = new RestAPI(repo);
		ByteArrayInputStream in = new ByteArrayInputStream("abc".getBytes());
		api.post("/packages/testRestPost/asset1.drl", in, "a comment");

		AssetItem a = pkg.loadAsset("asset1");
		assertFalse(a.isBinary());
		assertEquals("drl", a.getFormat());
		assertEquals("abc", a.getContent());
		assertEquals("a comment", a.getCheckinComment());


		in = new ByteArrayInputStream("qed".getBytes());
		api.post("/packages/testRestPost/asset2.xls", in, "a comment");
		a = pkg.loadAsset("asset2");

		assertTrue(a.isBinary());
		String s = new String(a.getBinaryContentAsBytes());
		assertEquals("qed", s);
		assertEquals("a comment", a.getCheckinComment());
		assertEquals("xls", a.getFormat());

		List<AssetItem> assets = RulesRepositoryTest.iteratorToList(repo.loadPackage("testRestPost").listAssetsByFormat(new String[] {"drl", "xls"}));
		assertEquals(2, assets.size());

	}

	public void testPostNewPackage() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		RestAPI api = new RestAPI(repo);

		api.post("/packages/testPostNewPackage/.package", new ByteArrayInputStream("qaz".getBytes()), "This is a new package");
		PackageItem pkg = repo.loadPackage("testPostNewPackage");
		assertEquals("qaz", pkg.getHeader());

		assertEquals("This is a new package", pkg.getCheckinComment());


	}

	public void testPut() throws Exception {
		//need to test both asset and .package shite.
		RulesRepository repo = RepositorySessionUtil.getRepository();
		PackageItem pkg = repo.createPackage("testRestPut", "");
		pkg.updateHeader("This is some header");
		repo.save();

		AssetItem asset1 = pkg.addAsset("asset1", "");
		asset1.updateContent("this is content");
		asset1.updateFormat("drl");
		asset1.checkin("");

		Calendar cd = asset1.getLastModified();

		AssetItem asset2 = pkg.addAsset("asset2", "");
		ByteArrayInputStream in = new ByteArrayInputStream("abc".getBytes());
		asset2.updateBinaryContentAttachment(in);
		asset2.updateFormat("xls");
		asset2.checkin("");

		RestAPI api = new RestAPI(repo);
		Thread.sleep(42);
		api.put("packages/testRestPut/asset1.drl", Calendar.getInstance(), new ByteArrayInputStream("qaz".getBytes()), "a new comment");

		AssetItem asset1_ = pkg.loadAsset("asset1");
		assertEquals("qaz", asset1_.getContent());
		assertEquals("a new comment", asset1_.getCheckinComment());
		assertTrue(asset1_.getLastModified().after(cd));

		api.put("packages/testRestPut/asset2.xls", Calendar.getInstance(), new ByteArrayInputStream("def".getBytes()), "a new comment");
		AssetItem asset2_ = pkg.loadAsset("asset2");
		assertEquals("def", new String(asset2_.getBinaryContentAsBytes()));
		assertEquals("a new comment", asset2_.getCheckinComment());
		assertTrue(asset2_.getLastModified().after(cd));

		//now check updating the package header
		api.put("packages/testRestPut/.package", Calendar.getInstance(), new ByteArrayInputStream("whee".getBytes()), "hey");
		pkg = repo.loadPackage("testRestPut");
		assertEquals("whee", pkg.getHeader());

		try {
			api.put("packages/testRestPut/asset1.drl", cd, new ByteArrayInputStream("qaz".getBytes()), "a new comment");
			fail("should not be able to do this as it is stale timestamp.");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}

		try {
			api.put("packages/testRestPut/.package", cd, new ByteArrayInputStream("whee".getBytes()), "hey");
			fail("should not be able to do this as it is stale timestamp.");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}


	}

	public void testDelete() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		PackageItem pkg = repo.createPackage("testRestDelete", "");
		pkg.updateHeader("This is some header");
		repo.save();

		AssetItem asset1 = pkg.addAsset("asset1", "");
		asset1.updateContent("this is content");
		asset1.updateFormat("drl");
		asset1.checkin("");

		RestAPI api = new RestAPI(repo);
		api.delete("packages/testRestDelete/asset1.drl");

		List l = RulesRepositoryTest.iteratorToList(pkg.listAssetsByFormat(new String[] {"drl"}));
		assertEquals(0, l.size());

		l = RulesRepositoryTest.iteratorToList(pkg.listArchivedAssets());
		assertEquals(1, l.size());


		//now test it back from the dead
		api.post("packages/testRestDelete/asset1.drl", new ByteArrayInputStream("123".getBytes()), "new comment");
		AssetItem ass = pkg.loadAsset("asset1");
		assertEquals("123", ass.getContent());
		assertEquals("new comment", ass.getCheckinComment());
		assertFalse(ass.isArchived());
		l = RulesRepositoryTest.iteratorToList(pkg.listAssetsByFormat(new String[] {"drl"}));
		assertEquals(1, l.size());

		try {
			api.post("packages/testRestDelete/asset1.drl", new ByteArrayInputStream("123".getBytes()),  "new comment");
			fail("this should be rejected as its not archived.");
		} catch (RulesRepositoryException e) {
			assertNotNull(e.getMessage());
		}



	}

	public void testSplit() throws Exception {
		RestAPI a = new RestAPI(null);
		String[] x = a.split("packages/foo/bar");
		assertEquals(3, x.length);
		assertEquals("packages", x[0]);
		assertEquals("foo", x[1]);
		assertEquals("bar", x[2]);

		x = a.split("/packages/foo/bar");
		assertEquals(3, x.length);
		assertEquals("packages", x[0]);
		assertEquals("foo", x[1]);
		assertEquals("bar", x[2]);

		String p = URLEncoder.encode("some package", "UTF-8");
		String asset = URLEncoder.encode("some asset", "UTF-8");
		x = a.split("packages/" + p + "/" + asset);
		assertEquals("packages", x[0]);
		assertEquals("some package", x[1]);
		assertEquals("some asset", x[2]);


		x = a.split("http://localhost:8080/drools-jbrms/org.dooby.doo.X.html/api/packages/foo/bar.drl");
		assertEquals(3, x.length);
		assertEquals("packages", x[0]);
		assertEquals("foo", x[1]);
		assertEquals("bar.drl", x[2]);

	}
}
