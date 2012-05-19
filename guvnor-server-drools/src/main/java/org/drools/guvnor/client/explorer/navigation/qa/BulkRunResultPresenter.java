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
import org.drools.guvnor.client.util.PercentageCalculator;

/**
 * This presents the results of a bulk run.
 */
public class BulkRunResultPresenter
        implements
        BulkRunResultView.Presenter {

    private BulkTestRunResult result;
    private Command closeCommand;

    private BulkRunResultView view;

    private int totalAmountOfExpectations = 0;
    private int totalAmountOfFailedExpectations = 0;

    public BulkRunResultPresenter(BulkRunResultView view) {
        this.view = view;

        view.setPresenter(this);
    }

    private void bind() {
        if (resultHasErrors()) {
            showErrors();
        } else {
            showResult();
        }
    }

    private void showErrors() {
        view.showErrors(result.getResult());
    }

    private void showResult() {
        showSummaries();

        showFailuresOutOfExpectations();

        showResultPercent();

        showRulesCoveredPercent();

        showOverAllStatus();

        showUncoveredRules();
    }

    private void showRulesCoveredPercent() {
        view.setRulesCoveredPercent(result.getPercentCovered());
    }

    private void showResultPercent() {
        view.setResultsPercent(calculatePercentage());
    }

    private int calculatePercentage() {
        return (int) (((float) (totalAmountOfExpectations - totalAmountOfFailedExpectations) / (float) totalAmountOfExpectations) * 100);
    }

    private void showFailuresOutOfExpectations() {
        view.setFailuresOutOfExpectation(
                totalAmountOfFailedExpectations,
                totalAmountOfExpectations);
    }

    private void countTestsAndFailures() {
        ScenarioResultSummary[] scenarioResultSummaries = result.getResults();

        if (scenarioResultSummaries != null) {
            for (ScenarioResultSummary scenarioResultSummary : scenarioResultSummaries) {
                totalAmountOfExpectations = totalAmountOfExpectations + scenarioResultSummary.getTotal();
                totalAmountOfFailedExpectations = totalAmountOfFailedExpectations + scenarioResultSummary.getFailures();
            }
        }
    }

    private void showSummaries() {
        ScenarioResultSummary[] scenarioResultSummaries = result.getResults();

        if (scenarioResultSummaries != null) {
            for (ScenarioResultSummary scenarioResultSummary : scenarioResultSummaries) {
                if (scenarioResultSummary.getTotal() == 0) {
                    view.addMissingExpectationSummaryTableRow(
                            scenarioResultSummary.getScenarioName(),
                            scenarioResultSummary.getUuid());
                } else {
                    view.addNormalSummaryTableRow(
                            scenarioResultSummary.getFailures(),
                            scenarioResultSummary.getTotal(),
                            scenarioResultSummary.getScenarioName(),
                            PercentageCalculator.calculatePercent(
                                    scenarioResultSummary.getFailures(),
                                    scenarioResultSummary.getTotal()),
                            scenarioResultSummary.getUuid());
                }
            }
        }
    }

    private void showOverAllStatus() {
        if (hasFailures()) {
            view.setFailed();
        } else {
            view.setSuccess();
        }
    }

    private boolean hasFailures() {
        return totalAmountOfFailedExpectations > 0;
    }

    private void showUncoveredRules() {
        String[] rulesNotCovered = result.getRulesNotCovered();
        if (rulesNotCovered != null) {
            for (String ruleName : rulesNotCovered) {
                view.addUncoveredRules(ruleName);
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
