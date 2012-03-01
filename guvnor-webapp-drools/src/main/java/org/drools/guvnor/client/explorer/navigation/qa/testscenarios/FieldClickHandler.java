package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.*;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;

import java.util.HashSet;

class FieldClickHandler implements ClickHandler {


    private final FactData factData;
    private final SuggestionCompletionEngine suggestionCompletionEngine;
    private final FixtureList definitionList;
    private final ScenarioParentWidget parent;

    FieldClickHandler(FactData factData,
                      SuggestionCompletionEngine suggestionCompletionEngine,
                      FixtureList definitionList,
                      ScenarioParentWidget parent) {
        this.factData = factData;
        this.suggestionCompletionEngine = suggestionCompletionEngine;
        this.definitionList = definitionList;
        this.parent = parent;
    }

    public void onClick(ClickEvent event) {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle(Constants.INSTANCE.ChooseDotDotDot());

        pop.addAttribute(Constants.INSTANCE.ChooseAFieldToAdd(), createAddNewField(pop));
        pop.addAttribute(Constants.INSTANCE.ChooseAFieldToAssignAFactTo(), assignNewFact(pop));

        pop.show();
    }

    private FactFieldSelector assignNewFact(final FormStylePopup pop) {
        FactFieldSelector factFieldSelector = createFactFieldSelector();

        factFieldSelector.addSelectionHandler(new SelectionHandler<String>() {
            @Override
            public void onSelection(SelectionEvent<String> stringSelectionEvent) {
                factData.getFieldData().add(
                        new FactAssignmentField(
                                stringSelectionEvent.getSelectedItem(),
                                suggestionCompletionEngine.getFieldType(factData.getType(), stringSelectionEvent.getSelectedItem())
                        ));

                parent.renderEditor();
                pop.hide();
            }
        });

        return factFieldSelector;
    }

    private FactFieldSelector createAddNewField(final FormStylePopup pop) {
        FactFieldSelector factFieldSelector = createFactFieldSelector();

        factFieldSelector.addSelectionHandler(new SelectionHandler<String>() {
            @Override
            public void onSelection(SelectionEvent<String> stringSelectionEvent) {
                factData.getFieldData().add(
                        new FieldData(
                                stringSelectionEvent.getSelectedItem(),
                                ""));
                parent.renderEditor();
                pop.hide();
            }
        });

        return factFieldSelector;
    }

    private FactFieldSelector createFactFieldSelector() {
        //build up a list of what we have got, don't want to add it twice
        HashSet<String> existingFields = new HashSet<String>();
        if (definitionList.size() > 0) {
            FactData factData = (FactData) definitionList.get(0);
            for (Field fieldData : factData.getFieldData()) {
                existingFields.add(fieldData.getName());
            }

        }
        String[] fields = suggestionCompletionEngine.getModelFields(factData.getType());

        FactFieldSelector factFieldSelector = new FactFieldSelector();
        for (String field : fields) {
            if (!existingFields.contains(field)) {
                factFieldSelector.addField(field);
            }
        }
        return factFieldSelector;
    }
}
