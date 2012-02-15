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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.EnumDropDown;
import org.drools.guvnor.client.common.DatePickerTextBox;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.NumbericFilterKeyPressHandler;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.testscenarios.Scenario;
import org.drools.ide.common.client.testscenarios.fixtures.ExecutionTrace;
import org.drools.ide.common.client.testscenarios.fixtures.FactData;
import org.drools.ide.common.client.testscenarios.fixtures.FieldData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Constraint editor for the FieldData in the Given Section
 */

public class FieldDataConstraintEditor extends DirtyableComposite {

    private String                     factType;
    private FieldData                  field;
    private FactData                   givenFact;
    private final Panel                panel;
    private Scenario                   scenario;
    private ExecutionTrace             executionTrace;
    private SuggestionCompletionEngine sce;
    private ValueChanged               callback;

    public FieldDataConstraintEditor(String factType,
                                     ValueChanged callback,
                                     FieldData field,
                                     FactData givenFact,
                                     SuggestionCompletionEngine sce,
                                     Scenario scenario,
                                     ExecutionTrace exec) {
        this.field = field;
        this.sce = sce;
        this.factType = factType;
        this.callback = callback;
        this.scenario = scenario;
        this.executionTrace = exec;
        this.givenFact = givenFact;
        panel = new SimplePanel();
        refreshEditor();
        initWidget( panel );
    }

    private void refreshEditor() {
        String key = factType + "." + field.getName();
        String flType = sce.getFieldType( key );
        panel.clear();
        if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            final TextBox box = editableTextBox( callback,
                                                 field.getName(),
                                                 field.getValue() );
            box.addKeyPressHandler( new NumbericFilterKeyPressHandler( box ) );
            panel.add( box );
        } else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            String[] c = new String[]{"true", "false"};
            panel.add( new EnumDropDown( field.getValue(),
                                         new DropDownValueChanged() {
                                             public void valueChanged(String newText,
                                                                      String newValue) {
                                                 callback.valueChanged( newValue );
                                             }
                                         },
                                         DropDownData.create( c ) ) );
        } else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            final DatePickerTextBox datePicker = new DatePickerTextBox( field.getValue() );
            datePicker.setTitle( Constants.INSTANCE.ValueFor0( field.getName() ) );
            datePicker.addValueChanged( new ValueChanged() {
                public void valueChanged(String newValue) {
                    field.setValue( newValue );
                }
            } );

            panel.add( datePicker );
        } else {
            Map<String, String> currentValueMap = new HashMap<String, String>();
            for (FieldData otherFieldData : givenFact.getFieldData()) {
                currentValueMap.put(otherFieldData.getName(), otherFieldData.getValue());
            }
            DropDownData dropDownData = sce.getEnums(factType, field.getName(), currentValueMap);
            if ( dropDownData != null ) {
                field.setNature( FieldData.TYPE_ENUM );
                panel.add( new EnumDropDown( field.getValue(),
                                             new DropDownValueChanged() {
                                                 public void valueChanged(String newText,
                                                                          String newValue) {
                                                     callback.valueChanged( newValue );
                                                 }
                                             },
                                             dropDownData ) );

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
                if ( field.getNature() == FieldData.TYPE_UNDEFINED && (isThereABoundVariableToSet() == true || isItAList() == true) ) {
                    Image clickme = new Image( Images.INSTANCE.edit() );
                    clickme.addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            showTypeChoice( w,
                                            field );
                        }
                    } );
                    panel.add( clickme );
                } else if ( field.getNature() == FieldData.TYPE_VARIABLE ) {
                    panel.add( variableEditor( callback ) );
                } else if ( field.getNature() == FieldData.TYPE_COLLECTION ) {
                    panel.add( listEditor( callback ) );
                } else {
                    panel.add( editableTextBox( callback,
                                                field.getName(),
                                                field.getValue() ) );
                }
            }
        }

    }

    private static TextBox editableTextBox(final ValueChanged changed,
                                           String fieldName,
                                           String initialValue) {
        final TextBox tb = new TextBox();
        tb.setText( initialValue );
        tb.setTitle( Constants.INSTANCE.ValueFor0( fieldName ) );
        tb.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                changed.valueChanged( tb.getText() );
            }
        } );

        return tb;
    }

    private Widget variableEditor(final ValueChanged changed) {
        List<String> vars = this.scenario.getFactNamesInScope( this.executionTrace,
                                                               true );

        final ListBox box = new ListBox();

        if ( this.field.getValue() == null ) {
            box.addItem( Constants.INSTANCE.Choose() );
        }
        int j = 0;
        for ( int i = 0; i < vars.size(); i++ ) {
            String var =  vars.get( i );
            FactData f = this.scenario.getFactTypes().get( var );
            String fieldType = null;
            if ( field.collectionType == null ) {
                fieldType = sce.getFieldType( this.factType,
                                              field.getName() );
            } else {
                fieldType = field.collectionType;
            }

            if ( f.getType().equals( fieldType ) ) {
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
                changed.valueChanged( field.getValue() );
            }
        } );

        return box;
    }

    private Widget listEditor(final ValueChanged changed) {
        Panel panel = new VerticalPanel();
        int i = 0;
        for ( final FieldData f : this.field.collectionFieldList ) {

            DirtyableHorizontalPane hpanel = new DirtyableHorizontalPane();

            FieldDataConstraintEditor fieldElement = new FieldDataConstraintEditor( f.collectionType,
                                                                                    new ValueChanged() {
                                                                                        public void valueChanged(String newValue) {
                                                                                            f.setValue( newValue );
                                                                                            calculateValueFromList();
                                                                                            makeDirty();
                                                                                        }
                                                                                    },
                                                                                    f,
                                                                                    givenFact,
                                                                                    sce,
                                                                                    scenario,
                                                                                    executionTrace );
            hpanel.add( fieldElement );
            final int index = i;
            Image del = new ImageButton( Images.INSTANCE.deleteItemSmall(),
                                         Constants.INSTANCE.AElementToDelInCollectionList(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 field.collectionFieldList.remove( index );
                                                 calculateValueFromList();
                                                 refreshEditor();
                                             }
                                         } );

            hpanel.add( del );

            Image addPattern = new ImageButton( Images.INSTANCE.newItemBelow() );
            addPattern.setTitle( Constants.INSTANCE.AddElementBelow() );

            addPattern.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    FieldData newFieldData = new FieldData();
                    newFieldData.setName( field.getName() );
                    newFieldData.collectionType = field.collectionType;
                    field.collectionFieldList.add( index + 1,
                                                   newFieldData );
                    calculateValueFromList();
                    refreshEditor();
                }
            } );
            hpanel.add( addPattern );
            Image moveDown = new ImageButton( Images.INSTANCE.shuffleDown() );
            moveDown.setTitle( Constants.INSTANCE.MoveDownListMove() );
            moveDown.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    if ( index < field.collectionFieldList.size() - 1 ) {
                        FieldData onMyLine = field.collectionFieldList.get( index );
                        FieldData onDown = field.collectionFieldList.get( index + 1 );
                        field.collectionFieldList.set( index + 1,
                                                       onMyLine );
                        field.collectionFieldList.set( index,
                                                       onDown );
                        calculateValueFromList();
                        refreshEditor();
                    }
                }
            } );
            hpanel.add( moveDown );

            Image moveUp = new ImageButton( Images.INSTANCE.shuffleUp() );
            moveUp.setTitle( Constants.INSTANCE.MoveUpList() );
            moveUp.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent sender) {
                    if ( index > 0 ) {
                        FieldData oneUp = field.collectionFieldList.get( index - 1 );
                        FieldData onMyLine = field.collectionFieldList.get( index );
                        field.collectionFieldList.set( index,
                                                       oneUp );
                        field.collectionFieldList.set( index - 1,
                                                       onMyLine );
                        calculateValueFromList();
                        refreshEditor();
                    }
                }
            } );
            hpanel.add( moveUp );
            panel.add( hpanel );
            i++;
        }

        if ( this.field.collectionFieldList.size() == 0 ) {
            Image add = new ImageButton( Images.INSTANCE.newItem(),
                                         Constants.INSTANCE.AElementToAddInCollectionList(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 FieldData newFieldData = new FieldData();
                                                 newFieldData.setName( field.getName() );
                                                 newFieldData.collectionType = field.collectionType;
                                                 field.collectionFieldList.add( newFieldData );
                                                 calculateValueFromList();
                                                 refreshEditor();
                                             }
                                         } );
            panel.add( add );
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

    private void showTypeChoice(ClickEvent w,
                                final FieldData con) {
        final FormStylePopup form = new FormStylePopup( Images.INSTANCE.newexWiz(),
                                                        Constants.INSTANCE.FieldValue() );

        Button lit = new Button( Constants.INSTANCE.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                con.setNature( FieldData.TYPE_LITERAL );
                doTypeChosen( form );
            }

        } );
        form.addAttribute( Constants.INSTANCE.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( Constants.INSTANCE.LiteralValue(),
                                                   Constants.INSTANCE.LiteralValTip() ) ) );

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( Constants.INSTANCE.AdvancedOptions() ) );

        // If we are here, then there must be a bound variable compatible with
        // me
        if ( isThereABoundVariableToSet() == true ) {
            Button variable = new Button( Constants.INSTANCE.BoundVariable() );
            variable.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    con.setNature( FieldData.TYPE_VARIABLE );
                    doTypeChosen( form );
                }
            } );
            form.addAttribute( Constants.INSTANCE.AVariable(),
                               widgets( variable,
                                        new InfoPopup( Constants.INSTANCE.ABoundVariable(),
                                                       Constants.INSTANCE.BoundVariableTip() ) ) );
        }
        if ( isItAList() == true ) {
            Button variable = new Button( Constants.INSTANCE.GuidedList() );
            variable.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    String factCollectionType = sce.getParametricFieldType( factType,
                                                                            field.getName() );
                    con.setNature( FieldData.TYPE_COLLECTION,
                                   factCollectionType );
                    doTypeChosen( form );
                }
            } );
            form.addAttribute( Constants.INSTANCE.AVariable(),
                               widgets( variable,
                                        new InfoPopup( Constants.INSTANCE.AGuidedList(),
                                                       Constants.INSTANCE.AGuidedListTip() ) ) );
        }
        form.show();
    }

    private boolean isThereABoundVariableToSet() {
        boolean retour = false;
        List< ? > vars = scenario.getFactNamesInScope( this.executionTrace,
                                                       true );
        if ( vars.size() > 0 ) {
            for ( int i = 0; i < vars.size(); i++ ) {
                String var = (String) vars.get( i );
                FactData f = (FactData) scenario.getFactTypes().get( var );
                String fieldType = null;
                if ( field.collectionType == null ) {
                    fieldType = sce.getFieldType( this.factType,
                                                  field.getName() );
                } else {
                    fieldType = field.collectionType;
                }
                if ( f.getType().equals( fieldType ) ) {
                    retour = true;
                    break;
                }
            }
        }
        return retour;
    }

    private boolean isItAList() {
        boolean retour = false;
        String fieldType = sce.getFieldType( this.factType,
                                             field.getName() );
        if ( fieldType != null && fieldType.equals( "Collection" ) ) {
            retour = true;
        }
        return retour;
    }

    private void doTypeChosen(final FormStylePopup form) {
        refreshEditor();
        form.hide();
    }

    private Panel widgets(Widget left,
                          Widget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( left );
        panel.add( right );
        panel.setWidth( "100%" );
        return panel;
    }

}
