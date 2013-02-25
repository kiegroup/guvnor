package org.kie.guvnor.testscenario.client;

import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.CallFixtureMap;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.testscenario.service.model.CallFixtureMap;
import org.kie.guvnor.testscenario.service.model.CallMethod;
import org.kie.guvnor.testscenario.service.model.ExecutionTrace;
import org.kie.guvnor.testscenario.service.model.Fixture;
import org.kie.guvnor.testscenario.service.model.FixtureList;
import org.kie.guvnor.testscenario.service.model.Scenario;

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
            }
            ;
        }
    }
}
