package org.drools.guvnor.server.files;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.util.DroolsStreamUtils;

public class PackageDeploymentServletTest extends TestCase {


	public void testDoGetPackage() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );

		ServiceImplementation impl = new ServiceImplementation();
		impl.repository = repo;

		PackageItem pkg = repo.createPackage("testPDSGetPackage", "");
		AssetItem header  = pkg.addAsset("drools", "");
		header.updateFormat("package");
		header.updateContent("import org.drools.SampleFact\n global org.drools.SampleFact sf");
		header.checkin("");


		AssetItem asset = pkg.addAsset("someRule", "");
		asset.updateContent("when \n SampleFact() \n then \n System.err.println(42);");
		asset.updateFormat(AssetFormats.DRL);
		asset.checkin("");

		assertNull(impl.buildPackage(pkg.getUUID(), null, true));



		//check source
		PackageDeploymentServlet serv = new PackageDeploymentServlet();
		MockHTTPRequest req = new MockHTTPRequest("/package/testPDSGetPackage/LATEST.drl", null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MockHTTPResponse res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertNotNull(out.toByteArray());
		String drl = new String(out.toByteArray());
		assertTrue(drl.indexOf("rule") > -1);


		//now binary
		serv = new PackageDeploymentServlet();
		req = new MockHTTPRequest("/package/testPDSGetPackage/LATEST", null);
		out = new ByteArrayOutputStream();
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertNotNull(out.toByteArray());
		byte[] bin = out.toByteArray();
		byte[] bin_ = pkg.getCompiledPackageBytes();

        org.drools.rule.Package o = (org.drools.rule.Package) DroolsStreamUtils.streamIn( new ByteArrayInputStream(bin) );
        assertNotNull(o);
        assertEquals(1, o.getRules().length);
        assertEquals(1, o.getGlobals().size());

		assertEquals(bin_.length, bin.length);

		assertSameArray(bin_, bin);

		//now some snapshots
		impl.createPackageSnapshot("testPDSGetPackage", "SNAP1", false, "hey");

		serv = new PackageDeploymentServlet();
		req = new MockHTTPRequest("/package/testPDSGetPackage/SNAP1.drl", null);
		out = new ByteArrayOutputStream();
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertNotNull(out.toByteArray());
		drl = new String(out.toByteArray());
		assertTrue(drl.indexOf("rule") > -1);

		//now binary
		serv = new PackageDeploymentServlet();
		req = new MockHTTPRequest("/package/testPDSGetPackage/SNAP1", null);
		out = new ByteArrayOutputStream();
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertNotNull(out.toByteArray());
		bin = out.toByteArray();
		bin_ = pkg.getCompiledPackageBytes();
		assertEquals(bin_.length, bin.length);


		//now get an individual asset source
		serv = new PackageDeploymentServlet();
		req = new MockHTTPRequest("/package/testPDSGetPackage/SNAP1/someRule.drl", null);
		out = new ByteArrayOutputStream();
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);

		assertNotNull(out.toByteArray());
		drl = new String(out.toByteArray());
		System.err.println(drl);
		assertTrue(drl.indexOf("rule") > -1);
		assertEquals(-1, drl.indexOf("package"));


        //now test HEAD
        serv = new PackageDeploymentServlet();
        req = new MockHTTPRequest("/package/testPDSGetPackage/LATEST", null);
        req.method = "HEAD";
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        serv.doHead(req, res);
        assertTrue(res.headers.size() > 0);
        String lm = res.headers.get("Last-Modified");
        assertNotNull(lm);

        serv = new PackageDeploymentServlet();
        req = new MockHTTPRequest("/package/testPDSGetPackage/LATEST", null);
        req.method = "HEAD";
        out = new ByteArrayOutputStream();
        res = new MockHTTPResponse(out);
        serv.doHead(req, res);
        assertTrue(res.headers.size() > 0);

        assertEquals(lm, res.headers.get("Last-Modified"));


	}

	public void testScenarios() throws Exception {
		RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession( true ) );

		ServiceImplementation impl = new ServiceImplementation();
		impl.repository = repo;

		PackageItem pkg = repo.createPackage("testScenariosURL", "");
		impl.createPackageSnapshot("testScenariosURL", "SNAP1", false, "");


		//now run the scenarios
		PackageDeploymentServlet serv = new PackageDeploymentServlet();
		MockHTTPRequest req = new MockHTTPRequest("/package/testScenariosURL/LATEST/SCENARIOS", null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MockHTTPResponse res = new MockHTTPResponse(out);
		serv.doGet(req, res);
		String testResult = new String(out.toByteArray());
		assertNotNull(testResult);
		assertEquals("No test scenarios found.", testResult);


		serv = new PackageDeploymentServlet();
		req = new MockHTTPRequest("/package/testScenariosURL/SNAP1/SCENARIOS", null);
		out = new ByteArrayOutputStream();
		res = new MockHTTPResponse(out);
		serv.doGet(req, res);
		testResult = new String(out.toByteArray());
		assertNotNull(testResult);
		assertEquals("No test scenarios found.", testResult);

	}

	private void assertSameArray(byte[] bin_, byte[] bin) {
		for (int i = 0; i < bin.length; i++) {
			assertEquals(bin_[i], bin[i]);
		}

	}

}
