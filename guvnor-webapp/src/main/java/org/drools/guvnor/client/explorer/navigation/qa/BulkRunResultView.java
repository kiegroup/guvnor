package org.drools.guvnor.client.explorer.navigation.qa;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;

interface BulkRunResultView {

    interface Presenter {
        void onClose();
    }

    void showErrors(BuilderResult errors);

    void addSummary(ScenarioResultSummary scenarioResultSummary);

    void setPresenter(Presenter presenter);

    void setFailed();

    void setSuccess();

    void setFailuresOutOfExpectation(int i,
                                     int j);

    void setResultsPercent(int i);

    void setRulesCoveredPercent(int i);

    void addUncoveredRules(String anyString);
}
