package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a summary result from a run.
 * To get more detail, need to drill in and run it.
 * @author Michael Neale
 */
public class ScenarioResultSummary implements IsSerializable {

	public int failures;
	public int total;
	public String scenarioName;
	public String scenarioDescription;
	public String uuid;

	public ScenarioResultSummary() {}
	public ScenarioResultSummary(int failures, int total, String scenarioName,
			String scenarioDescription, String uuid) {
		super();
		this.failures = failures;
		this.total = total;
		this.scenarioName = scenarioName;
		this.scenarioDescription = scenarioDescription;
		this.uuid = uuid;
	}

}
