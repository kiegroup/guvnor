package org.drools.guvnor.client.explorer.navigation.qa;

import org.drools.guvnor.client.rpc.BuilderResult;

interface BulkRunResultView {

    interface Presenter {

        void onClose();
    }

    void showErrors(BuilderResult errors);

    public void addNormalSummaryTableRow(
            int totalFailures,
            int grandTotal,
            String scenarioName,
            int percentage,
            String uuid);

    public void addMissingExpectationSummaryTableRow(
            String scenarioName,
            String uuid);

    void setPresenter(Presenter presenter);

    void setFailed();

    void setSuccess();

    void setFailuresOutOfExpectation(
            int i,
            int j);

    void setResultsPercent(int i);

    void setRulesCoveredPercent(int i);

    void addUncoveredRules(String anyString);
}
