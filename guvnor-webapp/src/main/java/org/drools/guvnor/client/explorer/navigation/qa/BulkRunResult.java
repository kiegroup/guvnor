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

package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;

/**
 * This presents the results of a bulk run.
 */
public class BulkRunResult
        implements
        BulkRunResultView.Presenter {

    private BulkTestRunResult result;
    private Command closeCommand;

    private BulkRunResultView display;

    private int grandTotal = 0;
    private int totalFailures = 0;

    public BulkRunResult(BulkRunResultView display) {
        this.display = display;

        display.setPresenter(this);
    }

    private void bind() {
        if (resultHasErrors()) {
            showErrors();
        } else {
            showResult();
        }
    }

    private void showErrors() {
        display.showErrors(result.getResult());
    }

    private void showResult() {
        showSummaries();

        showFailuresOutOfExpetations();

        showResultPercent();

        showRulesCoveredPercent();

        showOverAllStatus();

        showUncoveredRules();
    }

    private void showRulesCoveredPercent() {
        display.setRulesCoveredPercent(result.getPercentCovered());
    }

    private void showResultPercent() {
        display.setResultsPercent(calculatePercentage());
    }

    private int calculatePercentage() {
        return (int) (((float) (grandTotal - totalFailures) / (float) grandTotal) * 100);
    }

    private void showFailuresOutOfExpetations() {
        display.setFailuresOutOfExpectation(totalFailures,
                grandTotal);
    }

    private void countTestsAndFailures() {
        ScenarioResultSummary[] scenarioResultSummaries = result.getResults();

        if (scenarioResultSummaries != null) {
            for (ScenarioResultSummary scenarioResultSummary : scenarioResultSummaries) {
                grandTotal = grandTotal + scenarioResultSummary.getTotal();
                totalFailures = totalFailures + scenarioResultSummary.getFailures();
            }
        }
    }

    private void showSummaries() {
        ScenarioResultSummary[] scenarioResultSummaries = result.getResults();

        if (scenarioResultSummaries != null) {
            for (ScenarioResultSummary scenarioResultSummary : scenarioResultSummaries) {
                display.addSummary(scenarioResultSummary);
            }
        }
    }

    private void showOverAllStatus() {
        if (hasFailures()) {
            display.setFailed();
        } else {
            display.setSuccess();
        }
    }

    private boolean hasFailures() {
        return totalFailures > 0;
    }

    private void showUncoveredRules() {
        String[] rulesNotCovered = result.getRulesNotCovered();
        if (rulesNotCovered != null) {
            for (String ruleName : rulesNotCovered) {
                display.addUncoveredRules(ruleName);
            }
        }
    }

    private boolean resultHasErrors() {
        if (result != null && result.getResult() != null) {
            return result.getResult().hasLines();
        } else {
            return false;
        }
    }

    public void onClose() {
        closeCommand.execute();
    }

    public void setCloseCommand(Command closeCommand) {
        this.closeCommand = closeCommand;
    }

    public void setBulkTestRunResult(BulkTestRunResult bulkTestRunResult) {
        this.result = bulkTestRunResult;

        countTestsAndFailures();
        bind();
    }

}
