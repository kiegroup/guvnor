package org.drools.guvnor.client.rpc;

import org.drools.guvnor.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is essentially a "Either" class.
 * It will either be a list of rule compiler errors (should it have to compile), or the scenario run results.
 * @author Michael Neale
 */
public class ScenarioRunResult implements IsSerializable {

	public ScenarioRunResult() {}


	public BuilderResultLine[] errors;
	public Scenario scenario;
	public ScenarioRunResult(BuilderResultLine[] errors, Scenario scenario) {

		this.errors = errors;
		this.scenario = scenario;
	}

}
