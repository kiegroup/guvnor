package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.testscenarios.Scenario;
import org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap;
import org.drools.ide.common.client.testscenarios.fixtures.CallMethod;
import org.drools.ide.common.client.testscenarios.fixtures.ExecutionTrace;
import org.drools.ide.common.client.testscenarios.fixtures.Fixture;
import org.drools.ide.common.client.testscenarios.fixtures.FixtureList;

import com.google.gwt.user.client.ui.VerticalPanel;

public class CallMethodOnGivenPanel extends VerticalPanel {
    public CallMethodOnGivenPanel(List<ExecutionTrace> listExecutionTrace,
            int executionTraceLine, CallFixtureMap given,
            final Scenario scenario, final ScenarioWidget parent) {

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
