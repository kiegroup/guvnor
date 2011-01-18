package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

interface BulkRunResultView {

    interface Presenter {
        void onClose();

        void onOpenTestScenario(String uuid);
    }

    void showErrors(BuilderResult errors,
                    OpenItemCommand editEvent);

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
