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

	public String toString() {
		if (failures == 0) return "SUCCESS " + scenarioName;
		return "FAILURE " + scenarioName + " (" + failures + " failures out of " + total + ")";
	}

}
