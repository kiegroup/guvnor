package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.Expectation;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Fixture;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

/**
 * Some utility methods as the display logic is a bit hairy.
 */
public class ScenarioHelper {




	static final String RETRACT_KEY = "retract";


	/**
	 * Called lumpy map - as this takes a flat list of fixtures, and groups things together.
	 * It will return a list - of which each element will either be a list - or a map.
	 * If its a map - then its a map of FactData to the fact type. If its a list, then it will be
	 * expectations or retractions.
	 *
	 * Man, this will be so much nicer with generics.
	 * @return List<List<VeryifyRuleFired or VerifyFact or RetractFact> OR Map<String, List<FactData>> OR ExecutionTrace>
	 */
	public List lumpyMap(List fixtures) {
		List output = new ArrayList();

		Map dataInput = new HashMap();
		List verifyFact = new ArrayList();
		List verifyRule = new ArrayList();
		List retractFacts = new ArrayList();


		for (Iterator iterator = fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof FactData) {
				accumulateFactData(dataInput, f);
			} else if (f instanceof RetractFact) {
				retractFacts.add(f);
			} else if (f instanceof VerifyRuleFired) {
				verifyRule.add(f);
			} else if (f instanceof VerifyFact) {
				verifyFact.add(f);
			} else if (f instanceof ExecutionTrace) {
				gatherFixtures(output, dataInput, verifyFact, verifyRule,
						retractFacts, false);

				output.add(f);

				verifyRule = new ArrayList();
				verifyFact = new ArrayList();
				retractFacts = new ArrayList();
				dataInput = new HashMap();
			}
		}
		gatherFixtures(output, dataInput, verifyFact, verifyRule,
				retractFacts, true);

		return output;
	}

	private void gatherFixtures(List output, Map dataInput, List verifyFact,
			List verifyRule, List retractFacts, boolean end) {
		if (verifyRule.size() > 0) output.add(verifyRule);
		if (verifyFact.size() > 0) output.add(verifyFact);
		if (retractFacts.size() > 0) dataInput.put(RETRACT_KEY, retractFacts);
		if (dataInput.size() > 0 || !end) output.add(dataInput); //want to have a place holder for the GUI
	}

	/**
	 * Group the globals together by fact type.
	 */
 	public Map lumpyMapGlobals(List globals) {
 		Map g = new HashMap();
 		for (Iterator iterator = globals.iterator(); iterator.hasNext();) {
			FactData f = (FactData) iterator.next();
			accumulateFactData(g, f);
		}
 		return g;
 	}

	private void accumulateFactData(Map dataInput, Fixture f) {
		FactData fd = (FactData) f;
		if (! dataInput.containsKey(fd.type)) {
			dataInput.put(fd.type, new ArrayList());
		}
		((List) dataInput.get(fd.type)).add(fd);
	}


	static void removeFields(List factData, String field) {
		for (Iterator iterator = factData.iterator(); iterator.hasNext();) {
			FactData fa = (FactData) iterator.next();
			for (Iterator iterator2 = fa.fieldData.iterator(); iterator2.hasNext();) {
				FieldData fi = (FieldData) iterator2.next();
				if (fi.name.equals(field)) {
					iterator2.remove();
				}
			}
		}
	}






}


