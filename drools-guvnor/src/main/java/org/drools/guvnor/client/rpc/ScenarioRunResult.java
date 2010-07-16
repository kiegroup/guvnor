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

import org.drools.ide.common.client.modeldriven.testing.Scenario;

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
