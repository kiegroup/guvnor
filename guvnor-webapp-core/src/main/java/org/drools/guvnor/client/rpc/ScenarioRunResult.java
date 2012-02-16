/*
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
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import java.util.List;

/**
 * This is essentially a "Either" class.
 * It will either be a list of rule compiler errors (should it have to compile), or the scenario run results.
 */
public class ScenarioRunResult
        implements
        IsSerializable {

    private List<BuilderResultLine> errors = null;
    private Scenario scenario = null;

    public ScenarioRunResult() {
    }

    public ScenarioRunResult(List<BuilderResultLine> errors) {
        this.errors = errors;
    }

    public ScenarioRunResult(Scenario scenario) {

        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public List<BuilderResultLine> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors != null;
    }
}
