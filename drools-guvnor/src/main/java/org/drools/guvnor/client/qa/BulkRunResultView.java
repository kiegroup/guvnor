package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.HasValue;

interface BulkRunResultView {

    interface Presenter {
        void onClose();

        void onOpenTestScenario(String uuid);
    }

    void showErrors(BuilderResult errors,
                    EditItemEvent editEvent);

    HasValue<String[]> getUncoveredRules();

    HasValue<Integer> getCoveredPercent();

    HasValue<Integer> getTotalFailuresPercent();

    void addSummary(ScenarioResultSummary scenarioResultSummary);

    void setTotalFailures(int totalFailures,
                          int grandTotal);

    void setCoveredPercentText(String percentCovered);

    HasValue<Boolean> getOverAllStatus();

    void setPresenter(Presenter presenter);
}
