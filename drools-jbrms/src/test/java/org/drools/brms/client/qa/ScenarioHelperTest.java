package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

import junit.framework.TestCase;

public class ScenarioHelperTest extends TestCase {


	//need to get out chunks, so we get data (insert, update, retract)
	//then execute
	//then expectations
	//then data
	//then execute

	//want it in chunks

	public void testChunks() {
		List l = new ArrayList();
		l.add(new FactData("Q", "x", new FieldData[0], false));
		l.add(new FactData("Q", "y", new FieldData[0], false));
		l.add(new FactData("X", "a", new FieldData[0], false));
		l.add(new FactData("X", "b", new FieldData[0], false));
		ExecutionTrace ex1 = new ExecutionTrace();
		l.add(ex1);

		l.add(new FactData("Z", "z", new FieldData[0], false));
		l.add(new FactData("Q", "x", new FieldData[0], true));
		l.add(new FactData("Q", "y", new FieldData[0], true));
		l.add(new RetractFact("y"));

		VerifyFact vf1 = new VerifyFact();
		VerifyFact vf2 = new VerifyFact();
		VerifyRuleFired vr1 = new VerifyRuleFired();
		VerifyRuleFired vr2 = new VerifyRuleFired();
		l.add(vf1);
		l.add(vf2);
		l.add(vr1);
		l.add(vr2);

		ExecutionTrace ex2 = new ExecutionTrace();
		l.add(ex2);

		VerifyFact vf3 = new VerifyFact();
		l.add(vf3);

		ScenarioHelper hlp = new ScenarioHelper();

		List fx = hlp.lumpyMap(l);
		assertEquals(8, fx.size());

		Map first  = (Map) fx.get(0);
		assertEquals(2, first.size());
		List fdl = (List) first.get("Q");
		assertEquals(2, fdl.size());
		FactData fd = (FactData) fdl.get(0);
		assertEquals("x", fd.name);
		fd = (FactData) fdl.get(1);
		assertEquals("y", fd.name);

		fdl = (List) first.get("X");
		assertEquals(2, fdl.size());
		fd = (FactData) fdl.get(0);
		assertEquals("a", fd.name);

		assertEquals(ex1, fx.get(1));

		List ruleFired = (List) fx.get(2);
		assertEquals(2, ruleFired.size());
		assertEquals(vr1, ruleFired.get(0));
		assertEquals(vr2, ruleFired.get(1));

		List verifyFact = (List) fx.get(3);
		assertEquals(2, verifyFact.size());
		assertEquals(vf1, verifyFact.get(0));
		assertEquals(vf2, verifyFact.get(1));


		List retracts = (List) fx.get(4);
		assertEquals(1, retracts.size());
		RetractFact ret = (RetractFact) retracts.get(0);
		assertEquals("y", ret.name);


		Map second = (Map) fx.get(5);
		assertEquals(2, second.size());
		assertTrue(second.containsKey("Z"));
		assertTrue(second.containsKey("Q"));
		fdl = (List) second.get("Q");
		assertEquals(2, fdl.size());

		assertEquals(ex2, fx.get(6));

		List last = (List) fx.get(7);
		assertEquals(1, last.size());
		assertEquals(vf3, last.get(0));

	}

	public void testGlobals() {
		List l = new ArrayList();
		l.add(new FactData("X", "d", new FieldData[0], true));
		l.add(new FactData("X", "c", new FieldData[0], true));
		l.add(new FactData("Q", "a", new FieldData[0], true));
		l.add(new FactData("Q", "b", new FieldData[0], true));

		ScenarioHelper hlp = new ScenarioHelper();
		Map m = hlp.lumpyMapGlobals(l);
		assertEquals(2, m.size());
		List fd = (List) m.get("X");
		assertEquals(2, fd.size());
		assertEquals("d", ((FactData)fd.get(0)).name);
		assertEquals("c", ((FactData)fd.get(1)).name);

		fd = (List) m.get("Q");
		assertEquals(2, fd.size());
		assertEquals("a", ((FactData)fd.get(0)).name);
		assertEquals("b", ((FactData)fd.get(1)).name);



	}


}
