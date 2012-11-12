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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.DatePickerTextBox;
import org.drools.guvnor.client.modeldriven.ui.EnumDropDown;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.NumbericFilterKeyPressHandler;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

public class FieldDataConstraintEditor extends DirtyableComposite {

    private Constants                  constants = GWT.create( Constants.class );
    private static Images              images    = GWT.create( Images.class );

    private String                     factType;
    private FieldData                  field;
    private FactData                   givenFact;
    private final Panel                panel;
    private Scenario                   scenario;
    private ExecutionTrace             executionTrace;
    private SuggestionCompletionEngine sce;
    private ValueChanged               callback;

    private IsWidget                        valueEditorWidget;
    private List<FieldDataConstraintEditor> dependentEnumEditors = null;

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
            valueEditorWidget =  editableNumericTextBox( callback,
                                                          field.getName(),
                                                          field.getValue() );
            panel.add( valueEditorWidget );
            
        } else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            valueEditorWidget = booleanEditor();
            panel.add( valueEditorWidget );
            
        } else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            valueEditorWidget = dateEditor();
            panel.add( valueEditorWidget );

        } else {
            Map<String, String> currentValueMap = new HashMap<String, String>();
            for (FieldData otherFieldData : givenFact.getFieldData()) {
                currentValueMap.put(otherFieldData.getName(), otherFieldData.getValue());
            }
            DropDownData dropDownData = sce.getEnums(factType, field.getName(), currentValueMap);
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
                if ( field.getNature() == FieldData.TYPE_UNDEFINED && (isThereABoundVariableToSet() == true || isItAList() == true) ) {
                    Image clickme = GuvnorImages.INSTANCE.Edit();
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


    private TextBox editableNumericTextBox(final ValueChanged changed,
                                    String fieldName,
                                    String initialValue){
        TextBox textBox = editableTextBox(changed, fieldName, initialValue);

        textBox.addKeyPressHandler( new NumbericFilterKeyPressHandler( textBox ) );

        return textBox;
    }

    private TextBox editableTextBox(final ValueChanged changed,
                                    String fieldName,
                                    String initialValue) {
        final TextBox tb = new TextBox();
        tb.setText( initialValue );
        String m = ((Constants) GWT.create( Constants.class )).ValueFor0( fieldName );
        tb.setTitle( m );
        tb.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                changed.valueChanged( tb.getText() );
            }
        } );

        return tb;
    }
    
    private EnumDropDown booleanEditor() {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged(String newText,
                                                              String newValue) {
                                         callback.valueChanged( newValue );
                                     }

                                 },
                                 DropDownData.create( new String[]{"true", "false"} ) );
    }

    private EnumDropDown dropDownEditor(final DropDownData dropDownData) {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged(String newText,
                                                              String newValue) {
                                         callback.valueChanged( newValue );
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
    
    private Widget variableEditor(final ValueChanged changed) {
        List<String> vars = this.scenario.getFactNamesInScope( this.executionTrace,
                                                               true );

        final ListBox box = new ListBox();

        if ( this.field.getValue() == null ) {
            box.addItem( constants.Choose() );
        }
        int j = 0;
        for ( int i = 0; i < vars.size(); i++ ) {
            String var = (String) vars.get( i );
            FactData f = (FactData) this.scenario.getFactTypes().get( var );
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
            ImageButton del = new ImageButton(GuvnorImages.INSTANCE.DeleteItemSmall(),
                                         constants.AElementToDelInCollectionList(),
                                         new ClickHandler() {
                                             public void onClick(ClickEvent w) {
                                                 field.collectionFieldList.remove( index );
                                                 calculateValueFromList();
                                                 refreshEditor();
                                             }
                                         } );

            hpanel.add( del );

            ImageButton addPattern = new ImageButton( GuvnorImages.INSTANCE.NewItemBelow() );
            addPattern.setTitle( constants.AddElementBelow() );

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
            ImageButton moveDown = new ImageButton( GuvnorImages.INSTANCE.SuffleDown() );
            moveDown.setTitle( constants.MoveDownListMove() );
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

            ImageButton moveUp = new ImageButton( GuvnorImages.INSTANCE.SuffleUp() );
            moveUp.setTitle( constants.MoveUpList() );
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
            ImageButton add = new ImageButton( GuvnorImages.INSTANCE.NewItem(),
                                         constants.AElementToAddInCollectionList(),
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
        final FormStylePopup form = new FormStylePopup( images.newexWiz(),
                                                        constants.FieldValue() );

        Button lit = new Button( constants.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                con.setNature( FieldData.TYPE_LITERAL );
                doTypeChosen( form );
            }

        } );
        form.addAttribute( constants.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( constants.LiteralValue(),
                                                   constants.LiteralValTip() ) ) );

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( constants.AdvancedOptions() ) );

        // If we are here, then there must be a bound variable compatible with
        // me
        if ( isThereABoundVariableToSet() == true ) {
            Button variable = new Button( constants.BoundVariable() );
            variable.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    con.setNature( FieldData.TYPE_VARIABLE );
                    doTypeChosen( form );
                }
            } );
            form.addAttribute( constants.AVariable(),
                               widgets( variable,
                                        new InfoPopup( constants.ABoundVariable(),
                                                       constants.BoundVariableTip() ) ) );
        }
        if ( isItAList() == true ) {
            Button variable = new Button( constants.GuidedList() );
            variable.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    String factCollectionType = sce.getParametricFieldType( factType,
                                                                            field.getName() );
                    con.setNature( FieldData.TYPE_COLLECTION,
                                   factCollectionType );
                    doTypeChosen( form );
                }
            } );
            form.addAttribute( constants.AVariable(),
                               widgets( variable,
                                        new InfoPopup( constants.AGuidedList(),
                                                       constants.AGuidedListTip() ) ) );
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
    

    public void addIfDependentEnumEditor(FieldDataConstraintEditor candidateDependentEnumEditor) {
        if ( isDependentEnum( candidateDependentEnumEditor ) ) {
            dependentEnumEditors.add( candidateDependentEnumEditor );
        }
    }
    
    public boolean isDependentEnum(FieldDataConstraintEditor child) {
        if ( !factType.equals( child.factType ) ) {
            return false;
        }
        return sce.isDependentEnum( factType,
                                    field.getName(),
                                    child.field.getName() );
    }
    
    private void refreshDropDownData() {
        if ( this.valueEditorWidget instanceof EnumDropDown ) {
            final EnumDropDown dropdown = (EnumDropDown) this.valueEditorWidget;
            Map<String, String> currentValueMap = new HashMap<String, String>();
            for ( FieldData otherFieldData : givenFact.getFieldData() ) {
                currentValueMap.put( otherFieldData.getName(),
                                     otherFieldData.getValue() );
            }
            DropDownData dropDownData = sce.getEnums( factType,
                                                      field.getName(),
                                                      currentValueMap );
            dropdown.setDropDownData( field.getValue(),
                                      dropDownData );
        }
    }


}
