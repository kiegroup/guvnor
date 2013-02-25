package org.kie.guvnor.testscenario.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldPlaceHolder;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.testscenario.service.model.FactData;
import org.kie.guvnor.testscenario.service.model.FieldPlaceHolder;
import org.kie.guvnor.testscenario.service.model.Fixture;
import org.kie.guvnor.testscenario.service.model.FixtureList;

class AddFieldToFactDataClickHandler
        extends AddFieldClickHandler {


    private final FixtureList definitionList;

    AddFieldToFactDataClickHandler(FixtureList definitionList,
                                   DataModelOracle dmo,
                                   ScenarioParentWidget parent) {
        super(dmo, parent);
        this.definitionList = definitionList;
    }


    @Override
    public void onSelection(SelectionEvent<String> stringSelectionEvent) {
        for (Fixture fixture : definitionList) {
            if (fixture instanceof FactData) {
                ((FactData) fixture).getFieldData().add(
                        new FieldPlaceHolder(stringSelectionEvent.getSelectedItem()));
            }
        }
    }

    protected FactFieldSelector createFactFieldSelector() {
        FactFieldSelector factFieldSelector = new FactFieldSelector();
        for (String fieldName : dmo.getModelFields(definitionList.getFirstFactData().getType())) {
            if (!definitionList.isFieldNameInUse(fieldName)) {
                factFieldSelector.addField(fieldName);
            }
        }
        return factFieldSelector;
    }
}
