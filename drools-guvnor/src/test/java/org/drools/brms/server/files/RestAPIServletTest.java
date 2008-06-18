package org.drools.brms.server.files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.util.Base64;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.mock.MockHttpServletResponse;

import junit.framework.TestCase;

public class RestAPIServletTest extends TestCase {


	public void testUnpack() {
		String b42 = "BASIC " + new String( Base64.encode("user:pass".getBytes()) );
		RestAPIServlet serv = new RestAPIServlet();
		String[] d = serv.unpack(b42);
		assertEquals("user", d[0]);
		assertEquals("pass", d[1]);

	}

	public void testAllowUser() {
		RestAPIServlet serv = new RestAPIServlet();
		assertFalse(serv.allowUser(null));
		assertFalse(serv.allowUser(""));
		assertFalse(serv.allowUser("bgoo"));
		String b42 = "BASIC " + new String( Base64.encode("user:pass".getBytes()) );
		assertFalse(serv.allowUser(b42));
		b42 = "BASIC " + new String( Base64.encode("test:password".getBytes()) );
		assertTrue(serv.allowUser(b42));
	}



	public void testGet() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
		PackageItem pkg = repo.createPackage("testGetRestServlet", "");
		AssetItem ass = pkg.addAsset("asset1", "");
		ass.updateFormat("drl");
		ass.updateContent("some content");
		ass.checkin("hey ho");



		RestAPIServlet serv = new RestAPIServlet();
		assertNotNull(serv.getAPI());
		Map<String, String> headers = new HashMap<String, String>() {
			{
				put("Irrelevant", "garbage");
			}
		};
		String uri = "http://loser/api/packages/testGetRestServlet/asset1.drl";
		MockHTTPRequest req = new MockHTTPRequest(uri, headers);

		MockHTTPResponse res = new MockHTTPResponse(new ByteArrayOutputStream());

		//try with no password
		serv.doGet(req, res);
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);
		assertTrue(res.headers.containsKey("WWW-Authenticate"));


		//try again with bad password
		headers = new HashMap<String, String>() {
			{
				put("Authorization", new String(Base64.encode("foo:bar".getBytes())));
			}
		};
		req = new MockHTTPRequest(uri, headers);
		res = new MockHTTPResponse(new ByteArrayOutputStream());
		serv.doGet(req, res);
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.errorCode);
		assertTrue(res.headers.containsKey("WWW-Authenticate"));

		//finally, making it work
		headers = new HashMap<String, String>() {
			{
				put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
			}
		};

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		req = new MockHTTPRequest(uri, headers);
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertEquals(0, res.errorCode);
		String data = out.toString();
		assertEquals("some content", data);

		assertEquals("application/x-download", res.contentType);
		assertEquals(true, res.containsHeader("Content-Disposition"));

	}

	public void testPost() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
		PackageItem pkg = repo.createPackage("testPostRestServlet", "");

		HashMap<String, String> headers = new HashMap<String, String>() {
			{
				put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
			}
		};

		ByteArrayInputStream in = new ByteArrayInputStream("some new content".getBytes());
		RestAPIServlet serv = new RestAPIServlet();
		MockHTTPRequest req = new MockHTTPRequest("http://foo/api/packages/testPostRestServlet/asset1.drl", headers, in);

		MockHTTPResponse res = new MockHTTPResponse(null);
		serv.doPost(req, res);

		assertEquals("OK", res.stringWriter.toString());

		AssetItemIterator it = pkg.listAssetsByFormat(new String[] {"drl"});
		AssetItem ass = it.next();
		assertEquals("asset1", ass.getName());
		assertEquals("drl", ass.getFormat());
		assertFalse(ass.isBinary());
		assertEquals("some new content", ass.getContent());


		in = new ByteArrayInputStream("more content".getBytes());
		req = new MockHTTPRequest("http://foo/api/packages/testPostRestServlet/asset2.xls", headers, in);
		res = new MockHTTPResponse(null);
		serv.doPost(req, res);
		assertEquals("OK", res.stringWriter.toString());

		AssetItem ass2 = pkg.loadAsset("asset2");
		assertEquals("xls", ass2.getFormat());
		assertTrue(ass2.isBinary());


		String out = new String(ass2.getBinaryContentAsBytes());
		assertEquals("more content", out);

	}

	public void testPut() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
		PackageItem pkg = repo.createPackage("testPutRestServlet", "");
		AssetItem ass = pkg.addAsset("asset1", "abc");
		ass.updateFormat("drl");
		ass.checkin("");
		long ver = ass.getVersionNumber();

		HashMap<String, String> headers = new HashMap<String, String>() {
			{
				put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
				put("Checkin-Comment", "hey ho");
			}
		};

		ByteArrayInputStream in = new ByteArrayInputStream("some new content".getBytes());
		RestAPIServlet serv = new RestAPIServlet();
		MockHTTPRequest req = new MockHTTPRequest("http://foo/api/packages/testPutRestServlet/asset1.drl", headers, in);


		MockHTTPResponse res = new MockHTTPResponse(null);
		serv.doPut(req, res);

		assertEquals("OK", res.stringWriter.toString());

		ass = pkg.loadAsset("asset1");
		assertEquals("some new content", ass.getContent());
		assertEquals(ver + 1, ass.getVersionNumber());
		assertEquals("hey ho", ass.getCheckinComment());
	}


	public void testDelete() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );
		PackageItem pkg = repo.createPackage("testDeleteRestServlet", "");
		AssetItem ass = pkg.addAsset("asset1", "abc");
		ass.updateFormat("drl");
		ass.checkin("");

		HashMap<String, String> headers = new HashMap<String, String>() {
			{
				put("Authorization", "BASIC " + new String(Base64.encode("test:password".getBytes())));
			}
		};

		ByteArrayInputStream in = new ByteArrayInputStream("some new content".getBytes());
		RestAPIServlet serv = new RestAPIServlet();
		MockHTTPRequest req = new MockHTTPRequest("http://foo/api/packages/testDeleteRestServlet/asset1.drl", headers, in);

		MockHTTPResponse res = new MockHTTPResponse(null);
		serv.doDelete(req, res);

		assertEquals("OK", res.stringWriter.toString());



		pkg = repo.loadPackage("testDeleteRestServlet");
		assertFalse(pkg.listAssetsByFormat(new String[] {"drl"}).hasNext());


	}
}
