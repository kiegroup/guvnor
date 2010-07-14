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
public class GlobalFactWidget extends FactWidget {

    public GlobalFactWidget(String factType,
                            FixtureList definitionList,
                            Scenario sc,
                            ScenarioWidget parent,
                            ExecutionTrace executionTrace) {
        super( factType,
               definitionList,
               sc,
               parent,
               executionTrace,
               Format.format( constants.globalForScenario(),
                              factType ) );
    }

}
