package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.EditItemEvent;

interface BulkRunResultView {

    interface Presenter {
        void onClose();

        void onOpen(String uuid);
    }

    void showErrors(BuilderResult errors,
                    EditItemEvent editEvent);

    void showResult(int percentCovered);

    void setPresenter(Presenter presenter);

    void setUncoveredRules(String[] rulesNotCovered);

    void setCoveredPercent(int percentCovered);

    void addSummary(int i,
                    ScenarioResultSummary scenarioResultSummary);

    void setTotalFailures(int totalFailures,
                          int grandTotal);

    void setOverAllStatus(boolean success);
}
