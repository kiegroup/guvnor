/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.explorer.navigation.qa;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.drools.guvnor.client.explorer.navigation.qa.BulkRunResult;
import org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultView;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.junit.Before;
import org.junit.Test;

public class BulkRunResultTest {

    private BulkRunResult bulkRunResult;
    private BulkRunResultView mockView;

    @Before
    public void setUp() {
        mockView = mock( BulkRunResultView.class );

        bulkRunResult = new BulkRunResult( mockView );
    }

    @Test
    public void emptyScenarioTestSummary() throws Exception {
        BulkTestRunResult bulkTestRunResult = new BulkTestRunResult();
        bulkRunResult.setBulkTestRunResult( bulkTestRunResult );

        verifyBulkTestSucceeded();
        verify( mockView ).setFailuresOutOfExpectation( 0,
                                                        0 );
        verify( mockView ).setResultsPercent( 0 );
        verify( mockView ).setRulesCoveredPercent( 0 );
        verifyNoUncoveredRules();

        verify( mockView,
                never() ).addSummary( eq( new ScenarioResultSummary() ) );
    }

    @Test
    public void singleTestWithoutFailures() throws Exception {

        ScenarioResultSummary scenarioResultSummary = getScenarioResultSummary( 0,
                                                                                1 );
        ScenarioResultSummary[] scenarioResultSummaries = new ScenarioResultSummary[1];
        scenarioResultSummaries[0] = scenarioResultSummary;
        BulkTestRunResult bulkTestRunResult = getBulkRunResult( 100,
                                                                scenarioResultSummaries );

        bulkRunResult.setBulkTestRunResult( bulkTestRunResult );

        verifyBulkTestSucceeded();
        verify( mockView ).setFailuresOutOfExpectation( 0,
                                                        1 );
        verify( mockView ).setResultsPercent( 100 );
        verify( mockView ).setRulesCoveredPercent( 100 );
        verifyNoUncoveredRules();

        verifyThatResultSummaryWasAdded( scenarioResultSummary );
    }

    @Test
    public void singleTestWithAFailure() throws Exception {

        ScenarioResultSummary scenarioResultSummary = getScenarioResultSummary( 1,
                                                                                1 );
        ScenarioResultSummary[] scenarioResultSummaries = new ScenarioResultSummary[1];
        scenarioResultSummaries[0] = scenarioResultSummary;
        BulkTestRunResult bulkTestRunResult = getBulkRunResult( 100,
                                                                scenarioResultSummaries );

        bulkRunResult.setBulkTestRunResult( bulkTestRunResult );

        verifyBulkTestFailed();
        verify( mockView ).setFailuresOutOfExpectation( 1,
                                                        1 );
        verify( mockView ).setResultsPercent( 0 );
        verify( mockView ).setRulesCoveredPercent( 100 );
        verifyNoUncoveredRules();

        verifyThatResultSummaryWasAdded( scenarioResultSummary );
    }

    @Test
    public void threeTestsOneFailingTwoUncoveredRules() throws Exception {
        ScenarioResultSummary scenarioResultSummary1 = getScenarioResultSummary( 1,
                                                                                 2 );
        ScenarioResultSummary scenarioResultSummary2 = getScenarioResultSummary( 0,
                                                                                 1 );
        ScenarioResultSummary[] scenarioResultSummaries = new ScenarioResultSummary[2];
        scenarioResultSummaries[0] = scenarioResultSummary1;
        scenarioResultSummaries[1] = scenarioResultSummary2;
        BulkTestRunResult bulkTestRunResult = getBulkRunResult( 100,
                                                                scenarioResultSummaries );
        String[] uncoveredRules = new String[2];
        uncoveredRules[0] = "I'm not covered 1";
        uncoveredRules[1] = "I'm not covered 2";
        bulkTestRunResult.setRulesNotCovered( uncoveredRules );

        bulkRunResult.setBulkTestRunResult( bulkTestRunResult );

        verifyBulkTestFailed();
        verify( mockView ).setFailuresOutOfExpectation( 1,
                                                        3 );
        verify( mockView ).setResultsPercent( 66 );
        verify( mockView ).setRulesCoveredPercent( 100 );
        verify( mockView ).addUncoveredRules( "I'm not covered 1" );
        verify( mockView ).addUncoveredRules( "I'm not covered 2" );

        verifyThatResultSummaryWasAdded( scenarioResultSummary1,
                                         scenarioResultSummary2 );

    }

    private void verifyBulkTestSucceeded() {
        verify( mockView,
                never() ).setFailed();
        verify( mockView ).setSuccess();
    }

    private void verifyBulkTestFailed() {
        verify( mockView ).setFailed();
        verify( mockView,
                never() ).setSuccess();
    }

    private void verifyNoUncoveredRules() {
        verify( mockView,
                never() ).addUncoveredRules( anyString() );
    }

    private void verifyThatResultSummaryWasAdded(ScenarioResultSummary... scenarioResultSummaries) {
        for ( ScenarioResultSummary scenarioResultSummary : scenarioResultSummaries ) {
            verify( mockView ).addSummary( scenarioResultSummary );
        }
    }

    private ScenarioResultSummary getScenarioResultSummary(int failures,
                                                           int total) {
        ScenarioResultSummary scenarioResultSummary = new ScenarioResultSummary();
        scenarioResultSummary.setFailures( failures );
        scenarioResultSummary.setTotal( total );
        return scenarioResultSummary;
    }

    private BulkTestRunResult getBulkRunResult(int percentCovered,
                                               ScenarioResultSummary[] scenarioResultSummaries) {
        BulkTestRunResult bulkTestRunResult = new BulkTestRunResult();
        bulkTestRunResult.setPercentCovered( percentCovered );
        bulkTestRunResult.setResults( scenarioResultSummaries );
        return bulkTestRunResult;
    }

}
