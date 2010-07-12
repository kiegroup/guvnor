package org.drools.guvnor.client.qa.testscenarios;

import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author rikkola
 *
 */
public class GivenPanel extends VerticalPanel {
    public GivenPanel(List<ExecutionTrace> listExecutionTrace,
                      int executionTraceLine,
                      FixturesMap given,
                      final Scenario scenario,
                      final ScenarioWidget parent) {

        for ( Map.Entry<String, FixtureList> e : given.entrySet() ) {
            FixtureList itemList = given.get( e.getKey() );
            if ( e.getKey().equals( ScenarioHelper.RETRACT_KEY ) ) {
                add( new RetractWidget( itemList,
                                        scenario,
                                        parent ) );
            } else if ( e.getKey().equals( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP ) ) {
                add( new ActivateRuleFlowWidget( itemList,
                                                 scenario,
                                                 parent ) );
            } else {
                add( new DataInputWidget( e.getKey(),
                                          itemList,
                                          false,
                                          scenario,
                                          parent,
                                          listExecutionTrace.get( executionTraceLine ) ) );
            }
        }
    }
}
