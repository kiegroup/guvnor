package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.Expectation;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.Fixture;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

/**
 * Some utility methods as the display logic is a bit hairy.
 */
public class ScenarioHelper {




	/**
	 * Called lumpy map - as this takes a flat list of fixtures, and groups things together.
	 * It will return a list - of which each element will either be a list - or a map.
	 * If its a map - then its a map of FactData to the fact type. If its a list, then it will be
	 * expectations or retractions.
	 *
	 * Man, this will be so much nicer with generics.
	 * @return List<List<Expectation or RetractFact> OR Map<String, List<FactData>> OR ExecutionTrace>
	 */
	public List lumpyMap(List fixtures) {
		List output = new ArrayList();

		Map dataInput = new HashMap();
		List expectations = new ArrayList();
		List retractFacts = new ArrayList();


		for (Iterator iterator = fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof FactData) {
				FactData fd = (FactData) f;
				if (! dataInput.containsKey(fd.type)) {
					dataInput.put(fd.type, new ArrayList());
				}
				((List) dataInput.get(fd.type)).add(fd);
			} else if (f instanceof RetractFact) {
				retractFacts.add(f);
			} else if (f instanceof Expectation) {
				expectations.add(f);
			} else if (f instanceof ExecutionTrace) {
				if (expectations.size() > 0) output.add(expectations);
				if (retractFacts.size() > 0) output.add(retractFacts);
				if (dataInput.size() > 0) output.add(dataInput);
				output.add(f);

				expectations = new ArrayList();
				retractFacts = new ArrayList();
				dataInput = new HashMap();
			}
		}
		return output;
	}






}


