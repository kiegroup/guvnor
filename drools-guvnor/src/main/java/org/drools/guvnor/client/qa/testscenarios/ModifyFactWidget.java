package org.drools.guvnor.client.qa.testscenarios;

import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.gwtext.client.util.Format;

/**
 * 
 * @author rikkola
 *
 */
public class ModifyFactWidget extends FactWidget {

    public ModifyFactWidget(String factType,
                            FixtureList definitionList,
                            Scenario scenario,
                            ScenarioWidget parent,
                            ExecutionTrace executionTrace) {
        super( factType,
               definitionList,
               scenario,
               parent,
               executionTrace,
               Format.format( constants.modifyForScenario(),
                              factType ) );
    }
}
