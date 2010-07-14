package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.gwtext.client.util.Format;

/**
 * 
 * @author rikkola
 *
 */
public class InsertFactWidget extends FactWidget {

    public InsertFactWidget(String factType,
                            FixtureList definitionList,
                            Scenario scenario,
                            ScenarioWidget parent,
                            ExecutionTrace executionTrace) {
        super( factType,
               definitionList,
               scenario,
               parent,
               executionTrace,
               Format.format( constants.insertForScenario(),
                              factType ) );
    }

    public void onDelete() {
        if ( scenario.isFactNameUsed( definitionList ) ) {
            ErrorPopup.showMessage( constants.CantRemoveThisBlockAsOneOfTheNamesIsBeingUsed() );
        } else {
            super.onDelete();
        }

    }
}
