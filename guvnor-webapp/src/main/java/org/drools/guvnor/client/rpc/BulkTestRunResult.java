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
 * This is returned when running a suite of tests.
 */
public class BulkTestRunResult
    implements
    IsSerializable {

    /**
     * Will be either errors, or results.
     */
    private BuilderResult           result;
    private ScenarioResultSummary[] results;

    private int                     percentCovered;
    private String[]                rulesNotCovered;

    public BulkTestRunResult() {
    }

    public BulkTestRunResult(BuilderResult errs,
                             ScenarioResultSummary[] res,
                             int percentCovered,
                             String[] rulesNotCovered) {
        this.setResult( errs );
        this.setResults( res );
        this.setPercentCovered( percentCovered );
        this.setRulesNotCovered( rulesNotCovered );
    }

    public String toString() {
        if ( getResult() != null && getResult().hasLines() ) return "Unable to run tests"; //NON-NLS
        if ( getResults() == null || getResults().length == 0 ) return "No test scenarios found."; //NON-NLS
        String res = "";
        if ( getResults() != null ) {
            for ( int i = 0; i < getResults().length; i++ ) {
                res = res + "\n" + getResults()[i].toString();
            }
        }
        return res.trim();
    }

    public void setResult(BuilderResult result) {
        this.result = result;
    }

    public BuilderResult getResult() {
        return result;
    }

    public void setResults(ScenarioResultSummary[] results) {
        this.results = results;
    }

    public ScenarioResultSummary[] getResults() {
        return results;
    }

    public void setPercentCovered(int percentCovered) {
        this.percentCovered = percentCovered;
    }

    public int getPercentCovered() {
        return percentCovered;
    }

    public void setRulesNotCovered(String[] rulesNotCovered) {
        this.rulesNotCovered = rulesNotCovered;
    }

    public String[] getRulesNotCovered() {
        return rulesNotCovered;
    }

}
