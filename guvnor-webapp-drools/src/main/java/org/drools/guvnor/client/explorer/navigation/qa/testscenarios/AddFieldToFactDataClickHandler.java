package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.logical.shared.SelectionEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldPlaceHolder;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;

class AddFieldToFactDataClickHandler
        extends AddFieldClickHandler {


    private final FixtureList definitionList;

    AddFieldToFactDataClickHandler(FixtureList definitionList,
                                   SuggestionCompletionEngine suggestionCompletionEngine,
                                   ScenarioParentWidget parent) {
        super(suggestionCompletionEngine, parent);
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
        for (String fieldName : suggestionCompletionEngine.getModelFields(definitionList.getFirstFactData().getType())) {
            if (!definitionList.isFieldNameInUse(fieldName)) {
                factFieldSelector.addField(fieldName);
            }
        }
        return factFieldSelector;
    }
}
