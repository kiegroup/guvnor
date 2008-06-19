package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is returned when running a suite of tests.
 */
public class BulkTestRunResult implements IsSerializable {

	public BulkTestRunResult()  {}
	public BulkTestRunResult(BuilderResult[] errs, ScenarioResultSummary[] res, int percentCovered, String[] rulesNotCovered) {
		this.errors = errs;
		this.results = res;
		this.percentCovered = percentCovered;
		this.rulesNotCovered = rulesNotCovered;
	}
	/**
	 * Will be either errors, or results.
	 */
	public BuilderResult[] errors;
	public ScenarioResultSummary[] results;

	public int percentCovered;
	public String[] rulesNotCovered;

}
