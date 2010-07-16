/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is returned when running a suite of tests.
 */
public class BulkTestRunResult implements IsSerializable {

	public BulkTestRunResult()  {}
	public BulkTestRunResult(BuilderResult errs, ScenarioResultSummary[] res, int percentCovered, String[] rulesNotCovered) {
		this.result = errs;
		this.results = res;
		this.percentCovered = percentCovered;
		this.rulesNotCovered = rulesNotCovered;
	}
	/**
	 * Will be either errors, or results.
	 */
	public BuilderResult result;
	public ScenarioResultSummary[] results;

	public int percentCovered;
	public String[] rulesNotCovered;

	public String toString() {
		if (result != null && result.lines.length > 0) return "Unable to run tests";  //NON-NLS
		if (results == null || results.length == 0) return "No test scenarios found.";   //NON-NLS
		String res = "";
		if (results != null) {
			for (int i = 0; i < results.length; i++) {
				res  = res + "\n" + results[i].toString();
			}
		}
		return res.trim();
	}

}
