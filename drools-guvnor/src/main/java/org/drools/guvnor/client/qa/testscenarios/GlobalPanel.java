package org.drools.guvnor.client.qa.testscenarios;

import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.ui.VerticalPanel;

public class GlobalPanel extends VerticalPanel {
    public GlobalPanel(Map<String, FixtureList> globals,
                       Scenario scenario,
                       ExecutionTrace previousEx,
                       ScenarioWidget scenarioWidget) {
        for ( Map.Entry<String, FixtureList> e : globals.entrySet() ) {
            add( new DataInputWidget( e.getKey(),
                                      globals.get( e.getKey() ),
                                      true,
                                      scenario,
                                      scenarioWidget,
                                      previousEx ) );
        }
    }
}
