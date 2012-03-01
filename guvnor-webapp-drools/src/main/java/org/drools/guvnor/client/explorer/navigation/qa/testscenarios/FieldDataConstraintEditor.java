/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.EnumDropDown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.TextBoxFactory;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import java.util.List;

/**
 * Constraint editor for the FieldData in the Given Section
 */
public class FieldDataConstraintEditor extends DirtyableComposite implements HasValueChangeHandlers<String> {

    private FieldData field;
    private final Panel panel = new SimplePanel();
    private final FieldDataConstraintHelper helper;

    public FieldDataConstraintEditor(String factType,
                                     FieldData field,
                                     FactData givenFact,
                                     SuggestionCompletionEngine sce,
                                     Scenario scenario,
                                     ExecutionTrace executionTrace) {
        this.field = field;
        helper = new FieldDataConstraintHelper(scenario, executionTrace, sce, factType, field, givenFact);
        refreshEditor();
        initWidget( panel );
    }

    private void refreshEditor() {
        String flType = helper.getFieldType();
        panel.clear();

        if (flType != null && flType.equals(SuggestionCompletionEngine.TYPE_BOOLEAN)) {
            panel.add(new EnumDropDown(field.getValue(),
                    new DropDownValueChanged() {
                        public void valueChanged(String newText,
                                                 String newValue) {
                            valueHasChanged(newValue);
                        }

                    },
                    DropDownData.create(new String[]{"true", "false"})));

        } else if (flType != null && flType.equals(SuggestionCompletionEngine.TYPE_DATE)) {
            final DatePickerTextBox datePicker = new DatePickerTextBox(field.getValue());
            datePicker.setTitle(Constants.INSTANCE.ValueFor0(field.getName()));
            datePicker.addValueChanged(new ValueChanged() {
                public void valueChanged(String newValue) {
                    field.setValue(newValue);
                }
            });
            panel.add(datePicker);

        } else {
            DropDownData dropDownData = helper.getEnums();

            if (dropDownData != null) {
                field.setNature(FieldData.TYPE_ENUM);
                panel.add(new EnumDropDown(field.getValue(),
                        new DropDownValueChanged() {
                            public void valueChanged(String newText,
                                                     String newValue) {
                                valueHasChanged(newValue);
                            }
                        },
                        dropDownData));

            } else {
                if (field.getValue() != null && field.getValue().length() > 0 && field.getNature() == FieldData.TYPE_UNDEFINED) {
                    if (field.getValue().length() > 1 && field.getValue().charAt(1) == '[' && field.getValue().charAt(0) == '=') {
                        field.setNature(FieldData.TYPE_LITERAL);
                    } else if (field.getValue().charAt(0) == '=') {
                        field.setNature(FieldData.TYPE_VARIABLE);
                    } else {
                        field.setNature(FieldData.TYPE_LITERAL);
                    }
                }
                if ( field.getNature() == FieldData.TYPE_UNDEFINED && (helper.isThereABoundVariableToSet() == true || helper.isItAList() == true) ) {
                    Image clickme = new Image( DroolsGuvnorImages.INSTANCE.edit() );
                    clickme.addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            showTypeChoice(w,
                                    field);
                        }
                    });
                    panel.add(clickme);
                } else if (field.getNature() == FieldData.TYPE_VARIABLE) {
                    panel.add(variableEditor());
                } else if (field.getNature() == FieldData.TYPE_COLLECTION) {
                    panel.add(listEditor());
                } else {
                    panel.add(editableTextBox(
                            new ValueChanged() {
                                @Override
                                public void valueChanged(String newValue) {
                                    valueChanged(newValue);
                                }
                            },
                            flType,
                            field.getName(),
                            field.getValue()));
                }
            }
        }

    }

    private static TextBox editableTextBox(final ValueChanged changed,
                                           final String dataType,
                                           String fieldName,
                                           String initialValue) {
        final TextBox tb = TextBoxFactory.getTextBox( dataType );
        tb.setText(initialValue);
        tb.setTitle(Constants.INSTANCE.ValueFor0(fieldName));
        tb.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                changed.valueChanged( tb.getText() );
            }
        } );

        return tb;
    }

    private Widget variableEditor() {
        List<String> vars = helper.getFactNamesInScope();


        final ListBox box = new ListBox();

        if (this.field.getValue() == null) {
            box.addItem(Constants.INSTANCE.Choose());
        }
        int j = 0;
        for (String var : vars) {
            if (helper.getFactTypeByVariableName(var).getType().equals(helper.resolveFieldType())) {
                if (box.getItemCount() == 0) {
                    box.addItem("...");
                    j++;
                }
                box.addItem("=" + var);
                if (this.field.getValue() != null && this.field.getValue().equals("=" + var)) {
                    box.setSelectedIndex(j);

                }
                j++;
            }
        }

        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                field.setValue(box.getItemText(box.getSelectedIndex()));
                valueHasChanged(field.getValue());
            }
        });

        return box;
    }

    private Widget listEditor() {
        Panel panel = new VerticalPanel();
        int i = 0;
        for (final FieldData f : this.field.collectionFieldList) {

            DirtyableHorizontalPane hpanel = new DirtyableHorizontalPane();

            FieldDataConstraintEditor fieldDataConstraintEditor = helper.createFieldDataConstraintEditor(f);
            fieldDataConstraintEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                    f.setValue(stringValueChangeEvent.getValue());
                    calculateValueFromList();
                    makeDirty();
                }
            });
            hpanel.add(fieldDataConstraintEditor);
            final int index = i;

            hpanel.add(new ImageButton(DroolsGuvnorImages.INSTANCE.deleteItemSmall(),
                    Constants.INSTANCE.AElementToDelInCollectionList(),
                    new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            field.collectionFieldList.remove(index);
                            calculateValueFromList();
                            refreshEditor();
                        }
                    }));

            Image addPattern = new ImageButton(DroolsGuvnorImages.INSTANCE.newItemBelow());
            addPattern.setTitle(Constants.INSTANCE.AddElementBelow());

            addPattern.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    FieldData newFieldData = new FieldData();
                    newFieldData.setName(field.getName());
                    newFieldData.collectionType = field.collectionType;
                    field.collectionFieldList.add(index + 1,
                            newFieldData);
                    calculateValueFromList();
                    refreshEditor();
                }
            });
            hpanel.add(addPattern);
            Image moveDown = new ImageButton(DroolsGuvnorImages.INSTANCE.shuffleDown());
            moveDown.setTitle(Constants.INSTANCE.MoveDownListMove());
            moveDown.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    if (index < field.collectionFieldList.size() - 1) {
                        FieldData onMyLine = field.collectionFieldList.get(index);
                        FieldData onDown = field.collectionFieldList.get(index + 1);
                        field.collectionFieldList.set(index + 1,
                                onMyLine);
                        field.collectionFieldList.set(index,
                                onDown);
                        calculateValueFromList();
                        refreshEditor();
                    }
                }
            });
            hpanel.add(moveDown);

            Image moveUp = new ImageButton(DroolsGuvnorImages.INSTANCE.shuffleUp());
            moveUp.setTitle(Constants.INSTANCE.MoveUpList());
            moveUp.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    if (index > 0) {
                        FieldData oneUp = field.collectionFieldList.get(index - 1);
                        FieldData onMyLine = field.collectionFieldList.get(index);
                        field.collectionFieldList.set(index,
                                oneUp);
                        field.collectionFieldList.set(index - 1,
                                onMyLine);
                        calculateValueFromList();
                        refreshEditor();
                    }
                }
            });
            hpanel.add(moveUp);
            panel.add(hpanel);
            i++;
        }

        if (this.field.collectionFieldList.size() == 0) {
            Image add = new ImageButton(DroolsGuvnorImages.INSTANCE.newItem(),
                    Constants.INSTANCE.AElementToAddInCollectionList(),
                    new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            FieldData newFieldData = new FieldData();
                            newFieldData.setName(field.getName());
                            newFieldData.collectionType = field.collectionType;
                            field.collectionFieldList.add(newFieldData);
                            calculateValueFromList();
                            refreshEditor();
                        }
                    });
            panel.add(add);
        }
        return panel;
    }

    private void calculateValueFromList() {
        if (this.field.collectionFieldList == null || this.field.collectionFieldList.isEmpty()) {
            this.field.setValue("=[]");
            return;
        }
        StringBuilder listContent = new StringBuilder();
        for (final FieldData f : this.field.collectionFieldList) {
            listContent.append(',');
            if (f.getValue() != null) {
                listContent.append(f.getValue());
            }
        }
        this.field.setValue("=[" + listContent.substring(1) + "]");
    }

    private void showTypeChoice(ClickEvent w,
                                final FieldData con) {


        TypeChoiceFormPopup typeChoiceForm = new TypeChoiceFormPopup(helper);
        typeChoiceForm.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> selectionEvent) {
                if (selectionEvent.getSelectedItem() == FieldData.TYPE_COLLECTION) {
                    con.setNature(
                            FieldData.TYPE_COLLECTION,
                            helper.getParametricFieldType());
                } else {
                    con.setNature(selectionEvent.getSelectedItem());
                }

                refreshEditor();
            }
        });

        typeChoiceForm.show();
    }

    private void valueHasChanged(String newValue) {
        ValueChangeEvent.fire(this, newValue);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
        return addHandler(stringValueChangeHandler, ValueChangeEvent.getType());
    }
}
