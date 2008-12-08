package org.drools.guvnor.client.rpc;

import junit.framework.TestCase;

public class BulkTestRunResultTest extends TestCase {

	public void testPrinting() {
		BulkTestRunResult res = new BulkTestRunResult();
		assertNotNull(res.toString());

		res.results = new ScenarioResultSummary[2];
		res.results[0] = new ScenarioResultSummary(0, 2, "A", "", "");
		res.results[1] = new ScenarioResultSummary(0, 2, "A", "", "");
		assertNotNull(res.toString());
		//System.out.println(res.toString());
		assertTrue(res.toString().startsWith("SUCCESS"));

		res.results[1] = new ScenarioResultSummary(1, 2, "A", "", "");
		System.out.println(res.toString());
		assertTrue(res.toString().indexOf("FAILURE") > -1);

	}

}
