package org.drools.repository.remoteapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
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
		assertEquals("text/plain", res.getContentType());
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
		String dt = p.getProperty("asset1.drl");
		System.err.println(dt);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date d= sdf.parse(dt);
		assertNotNull(d);

		assertEquals(sdf.format(asset1.getLastModified().getTime()), dt);


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

	public void testSplit() {
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

	}
}
