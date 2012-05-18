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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.EnumDropDown;
import org.drools.guvnor.client.common.DatePickerTextBox;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.TextBoxFactory;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fact;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Constraint editor for the FieldData in the Given Section
 */
public class FieldDataConstraintEditor
        extends DirtyableComposite
        implements
    HasValueChangeHandlers<String>,
    ScenarioParentWidget {

    private FieldData                       field;
    private IsWidget                        valueEditorWidget;
    private final Panel                     panel                = new SimplePanel();
    private final FieldConstraintHelper     helper;

    private List<FieldDataConstraintEditor> dependentEnumEditors = null;

    public FieldDataConstraintEditor(String factType,
                                     FieldData field,
                                     Fact givenFact,
                                     SuggestionCompletionEngine sce,
                                     Scenario scenario,
                                     ExecutionTrace executionTrace) {
        this.field = field;
        this.helper = new FieldConstraintHelper( scenario,
                                                 executionTrace,
                                                 sce,
                                                 factType,
                                                 field,
                                                 givenFact );
        renderEditor();
        initWidget( panel );
    }

    @Override
    public void renderEditor() {
        final String flType = helper.getFieldType();
        panel.clear();

        if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            valueEditorWidget = booleanEditor();
            panel.add( valueEditorWidget );

        } else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            valueEditorWidget = dateEditor();
            panel.add( valueEditorWidget );

        } else {
            final DropDownData dropDownData = helper.getEnums();

            if ( dropDownData != null ) {
                field.setNature( FieldData.TYPE_ENUM );
                dependentEnumEditors = new ArrayList<FieldDataConstraintEditor>();
                valueEditorWidget = dropDownEditor( dropDownData );
                panel.add( valueEditorWidget );

            } else {
                if ( field.getValue() != null && field.getValue().length() > 0 && field.getNature() == FieldData.TYPE_UNDEFINED ) {
                    if ( field.getValue().length() > 1 && field.getValue().charAt( 1 ) == '[' && field.getValue().charAt( 0 ) == '=' ) {
                        field.setNature( FieldData.TYPE_LITERAL );
                    } else if ( field.getValue().charAt( 0 ) == '=' ) {
                        field.setNature( FieldData.TYPE_VARIABLE );
                    } else {
                        field.setNature( FieldData.TYPE_LITERAL );
                    }
                }
                if ( field.getNature() == FieldData.TYPE_UNDEFINED && (helper.isThereABoundVariableToSet() == true || helper.isItAList() == true) ) {
                    valueEditorWidget = new FieldSelectorWidget( field,
                                                                 helper,
                                                                 this );
                    panel.add( valueEditorWidget );

                } else if ( field.getNature() == FieldData.TYPE_VARIABLE ) {
                    valueEditorWidget = variableEditor();
                    panel.add( valueEditorWidget );

                } else if ( field.getNature() == FieldData.TYPE_COLLECTION ) {
                    valueEditorWidget = listEditor();
                    panel.add( valueEditorWidget );

                } else {
                    valueEditorWidget = textBoxEditor( new ValueChangeHandler<String>() {
                                                           @Override
                                                           public void onValueChange(ValueChangeEvent<String> newValue) {
                                                               valueHasChanged( newValue.getValue() );
                                                           }
                                                       },
                                                       flType,
                                                       field.getName(),
                                                       field.getValue() );

                    panel.add( valueEditorWidget );
                }
            }
        }

    }

    private EnumDropDown booleanEditor() {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged(String newText,
                                                              String newValue) {
                                         valueHasChanged( newValue );
                                     }

                                 },
                                 DropDownData.create( new String[]{"true", "false"} ) );
    }

    private EnumDropDown dropDownEditor(final DropDownData dropDownData) {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged(String newText,
                                                              String newValue) {
                                         valueHasChanged( newValue );
                                         for ( FieldDataConstraintEditor dependentEnumEditor : dependentEnumEditors ) {
                                             dependentEnumEditor.refreshDropDownData();
                                         }
                                     }
                                 },
                                 dropDownData );

    }

    private DatePickerTextBox dateEditor() {
        DatePickerTextBox editor = new DatePickerTextBox( field.getValue() );
        editor.setTitle( Constants.INSTANCE.ValueFor0( field.getName() ) );
        editor.addValueChanged( new ValueChanged() {
            public void valueChanged(String newValue) {
                field.setValue( newValue );
            }
        } );
        return editor;
    }

    private TextBox textBoxEditor(final ValueChangeHandler<String> valueChangeHandler,
                                  final String dataType,
                                  String fieldName,
                                  String initialValue) {
        final TextBox tb = TextBoxFactory.getTextBox( dataType );
        tb.setText( initialValue );
        tb.setTitle( Constants.INSTANCE.ValueFor0( fieldName ) );
        tb.addValueChangeHandler( valueChangeHandler );
        return tb;
    }

    private Widget variableEditor() {
        List<String> vars = helper.getFactNamesInScope();

        final ListBox box = new ListBox();

        if ( this.field.getValue() == null ) {
            box.addItem( Constants.INSTANCE.Choose() );
        }
        int j = 0;
        for ( String var : vars ) {
            if ( helper.getFactTypeByVariableName( var ).getType().equals( helper.resolveFieldType() ) ) {
                if ( box.getItemCount() == 0 ) {
                    box.addItem( "..." );
                    j++;
                }
                box.addItem( "=" + var );
                if ( this.field.getValue() != null && this.field.getValue().equals( "=" + var ) ) {
                    box.setSelectedIndex( j );

                }
                j++;
            }
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                field.setValue( box.getItemText( box.getSelectedIndex() ) );
                valueHasChanged( field.getValue() );
            }
        } );

        return box;
    }

    private Widget listEditor() {
        Panel panel = new VerticalPanel();
        int i = 0;
        if (this.field.collectionFieldList != null) {
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
                                renderEditor();
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
                        renderEditor();
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
                            renderEditor();
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
                            renderEditor();
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
                                renderEditor();
                            }
                        });
                panel.add(add);
            }
        }
        return panel;
    }

    private void calculateValueFromList() {
        if ( this.field.collectionFieldList == null || this.field.collectionFieldList.isEmpty() ) {
            this.field.setValue( "=[]" );
            return;
        }
        StringBuilder listContent = new StringBuilder();
        for ( final FieldData f : this.field.collectionFieldList ) {
            listContent.append( ',' );
            if ( f.getValue() != null ) {
                listContent.append( f.getValue() );
            }
        }
        this.field.setValue( "=[" + listContent.substring( 1 ) + "]" );
    }

    private void valueHasChanged(String newValue) {
        ValueChangeEvent.fire( this,
                               newValue );
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
        return addHandler( stringValueChangeHandler,
                           ValueChangeEvent.getType() );
    }

    public void addIfDependentEnumEditor(FieldDataConstraintEditor candidateDependentEnumEditor) {
        if ( helper.isDependentEnum( candidateDependentEnumEditor.helper ) ) {
            dependentEnumEditors.add( candidateDependentEnumEditor );
        }
    }

    private void refreshDropDownData() {
        if ( this.valueEditorWidget instanceof EnumDropDown ) {
            final EnumDropDown dropdown = (EnumDropDown) this.valueEditorWidget;
            dropdown.setDropDownData( field.getValue(),
                                      helper.getEnums() );
        }
    }

}
