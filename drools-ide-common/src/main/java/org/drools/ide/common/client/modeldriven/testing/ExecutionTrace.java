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

package org.drools.ide.common.client.modeldriven.testing;

import java.util.Date;

/**
 * This contains lists of rules to include in the scenario (or exclude, as the case may be !).
 * This will be used to filter the rule engines behaviour under test.
 * @author Michael Neale
 */
public class ExecutionTrace implements Fixture {



	/**
	 * This is the simulated date - leaving it as null means it will use
	 * the current time.
	 */
	public Date		scenarioSimulatedDate = null;


	/**
	 * The time taken for execution.
	 */
	public Long executionTimeResult;

	/**
	 * This is pretty obvious really. The total firing count of all rules.
	 */
	public Long numberOfRulesFired;

	/**
	 * A summary of the rules fired.
	 */
	public String[] rulesFired;

	public ExecutionTrace() {}



}
