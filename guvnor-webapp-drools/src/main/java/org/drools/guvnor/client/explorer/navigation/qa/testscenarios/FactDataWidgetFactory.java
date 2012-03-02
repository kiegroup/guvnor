/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.*;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;

import java.util.HashMap;
import java.util.Map;

public class FactDataWidgetFactory {

    private final DirtyableFlexTable widget;
    private final Scenario scenario;
    private final SuggestionCompletionEngine suggestionCompletionEngine;
    private final FixtureList definitionList;
    private final ExecutionTrace executionTrace;

    private final RowIndexByFieldName rowIndexByFieldName = new RowIndexByFieldName();
    private int col = 0;
    private final ScenarioParentWidget parent;

    public FactDataWidgetFactory(Scenario scenario,
                                 SuggestionCompletionEngine suggestionCompletionEngine,
                                 FixtureList definitionList,
                                 ExecutionTrace executionTrace,
                                 ScenarioParentWidget parent,
                                 DirtyableFlexTable widget) {
        this.scenario = scenario;
        this.suggestionCompletionEngine = suggestionCompletionEngine;
        this.definitionList = definitionList;
        this.executionTrace = executionTrace;
        this.parent = parent;
        this.widget = widget;

    }


    public void build(String headerText,
                      FactData factData) {

        if (factData.getName() != null && !factData.getName().isEmpty()) {
            widget.setWidget(0,
                    ++col,
                    new SmallLabel("[" + factData.getName() + "]"));
        } else {
            col++;
        }

        widget.setWidget(
                0,
                0,
                new ClickableLabel(headerText,
                        new FieldClickHandler(
                                factData,
                                suggestionCompletionEngine,
                                definitionList,
                                parent)));


        // Sets row name and delete button.
        for (final Field field : factData.getFieldData()) {
            // Avoid duplicate field rows, only one for each name.
            if (rowIndexByFieldName.doesNotContain(field.getName())) {
                newRow(factData.getName(),
                        field.getName());
            }

            // Sets row data
            int fieldRowIndex = rowIndexByFieldName.getRowIndex(field.getName());
            widget.setWidget(fieldRowIndex,
                    col,
                    editableCell(
                            field,
                            factData,
                            factData.getType()));
        }

        // Set Delete
        widget.setWidget(
                rowIndexByFieldName.amountOrRows() + 1,
                col,
                new DeleteFactColumnButton(factData));
    }

    private void newRow(final String factName,
                        final String fieldName) {
        rowIndexByFieldName.addRow(fieldName);

        int rowIndex = rowIndexByFieldName.getRowIndex(fieldName);

        widget.setWidget(rowIndex,
                0,
                createFieldNameWidget(fieldName));
        widget.setWidget(rowIndex,
                definitionList.size() + 1,
                new DeleteFieldRowButton(factName,
                        fieldName));
        widget.getCellFormatter().setHorizontalAlignment(rowIndex,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);
    }

    /**
     * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
     *
     * @param field
     * @param factType
     * @return
     */
    private IsWidget editableCell(final Field field,
                                  FactData factData,
                                  String factType) {
        if (field instanceof FieldData) {
            FieldDataConstraintEditor fieldDataConstraintEditor = new FieldDataConstraintEditor(
                    factType,
                    (FieldData) field,
                    factData,
                    suggestionCompletionEngine,
                    scenario,
                    executionTrace);
            fieldDataConstraintEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                    ((FieldData) field).setValue(stringValueChangeEvent.getValue());
                }
            });
            return fieldDataConstraintEditor;
        } else if (field instanceof FactAssignmentField) {
            FactAssignmentField factAssignmentField = (FactAssignmentField) field;
            return new FactAssignmentFieldWidget(
                    factAssignmentField.getFactData(),
                    definitionList,
                    scenario,
                    suggestionCompletionEngine,
                    parent,
                    executionTrace);
        }

        throw new IllegalArgumentException("Unknown field type: " + field.getClass());
    }


    private IsWidget createFieldNameWidget(String fieldName) {
        return new FieldNameWidgetImpl(fieldName);
    }

    public int amountOrRows() {
        return rowIndexByFieldName.amountOrRows();
    }

    class DeleteFactColumnButton extends ImageButton {

        public DeleteFactColumnButton(final FactData factData) {
            super(DroolsGuvnorImages.INSTANCE.deleteItemSmall(),
                    Constants.INSTANCE.RemoveTheColumnForScenario(factData.getName()));

            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (scenario.isFactDataReferenced(factData)) {
                        Window.alert(Constants.INSTANCE.CanTRemoveThisColumnAsTheName0IsBeingUsed(factData.getName()));
                    } else if (Window.confirm(Constants.INSTANCE.AreYouSureYouWantToRemoveColumn0(factData.getName()))) {
                        scenario.removeFixture(factData);
                        definitionList.remove(factData);

                        parent.renderEditor();
                    }
                }
            });
        }

    }

    class DeleteFieldRowButton extends ImageButton {
        public DeleteFieldRowButton(final String factName,
                                    final String fieldName) {
            super(DroolsGuvnorImages.INSTANCE.deleteItemSmall(),
                    Constants.INSTANCE.RemoveThisRow());

            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (Window.confirm(Constants.INSTANCE.AreYouSureYouWantToRemoveRow0(factName))) {
                        ScenarioHelper.removeFields(definitionList,
                                fieldName);

                        parent.renderEditor();
                    }
                }
            });
        }
    }

    class RowIndexByFieldName {
        private Map<String, Integer> rows = new HashMap<String, Integer>();

        public void addRow(String fieldName) {
            rows.put(fieldName,
                    rows.size() + 1);
        }

        public boolean doesNotContain(String fieldName) {
            return !rows.containsKey(fieldName);
        }

        public Integer getRowIndex(String fieldName) {
            return rows.get(fieldName);
        }

        public int amountOrRows() {
            return rows.size();
        }
    }
}
