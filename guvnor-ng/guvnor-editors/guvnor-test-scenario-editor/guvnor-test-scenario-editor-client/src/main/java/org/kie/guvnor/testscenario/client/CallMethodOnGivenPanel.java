package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.testscenario.model.CallFixtureMap;
import org.kie.guvnor.testscenario.model.CallMethod;
import org.kie.guvnor.testscenario.model.ExecutionTrace;
import org.kie.guvnor.testscenario.model.Fixture;
import org.kie.guvnor.testscenario.model.FixtureList;
import org.kie.guvnor.testscenario.model.Scenario;

import java.util.List;
import java.util.Map;

public class CallMethodOnGivenPanel extends VerticalPanel {
    public CallMethodOnGivenPanel(List<ExecutionTrace> listExecutionTrace,
                                  int executionTraceLine, CallFixtureMap given,
                                  final Scenario scenario, final ScenarioEditorPresenter parent) {

        for (Map.Entry<String, FixtureList> e : given.entrySet()) {
            FixtureList itemList = given.get(e.getKey());
            for (Fixture f : itemList) {
                CallMethod mCall = (CallMethod) f;
                add(new CallMethodWidget(e.getKey(), parent, scenario, mCall,
                        listExecutionTrace.get(executionTraceLine)));
            };
        }
    }
}
