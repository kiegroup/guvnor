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

/**
 * Represents a summary result from a run.
 * To get more detail, need to drill in and run it.
 */
public class ScenarioResultSummary
    implements
    IsSerializable {

    private int    failures;
    private int    total;
    private String scenarioName;
    private String scenarioDescription;
    private String uuid;

    public ScenarioResultSummary() {
    }

    public ScenarioResultSummary(int failures,
                                 int total,
                                 String scenarioName,
                                 String scenarioDescription,
                                 String uuid) {
        super();
        this.setFailures( failures );
        this.setTotal( total );
        this.setScenarioName( scenarioName );
        this.setScenarioDescription( scenarioDescription );
        this.setUuid( uuid );
    }

    public String toString() {
        if ( getFailures() == 0 ) return "SUCCESS " + getScenarioName();
        return "FAILURE " + getScenarioName() + " (" + getFailures() + " failures out of " + getTotal() + ")";
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getFailures() {
        return failures;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioDescription(String scenarioDescription) {
        this.scenarioDescription = scenarioDescription;
    }

    public String getScenarioDescription() {
        return scenarioDescription;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

}
