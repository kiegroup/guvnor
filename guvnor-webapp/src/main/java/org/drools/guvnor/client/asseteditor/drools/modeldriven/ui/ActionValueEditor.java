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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.NumbericFilterKeyPressHandler;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.FieldNature;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides for editing of fields in the RHS of a rule.
 */
public class ActionValueEditor extends DirtyableComposite {

    private Constants        constants    = GWT.create( Constants.class );
    private Images           images       = GWT.create( Images.class );

    private ActionFieldValue value;
    private DropDownData     enums;
    private SimplePanel      root;
    private RuleModeller     model        = null;
    private String           variableType = null;
    private boolean          readOnly;
    private Command          onChangeCommand;

    public ActionValueEditor(final ActionFieldValue val,
                             final DropDownData enums,
                             boolean readOnly) {
        this( val,
              enums,
              null,
              null,
              readOnly );
    }

    public ActionValueEditor(final ActionFieldValue val,
                             final DropDownData enums) {
        this( val,
              enums,
              false );
    }

    public ActionValueEditor(final ActionFieldValue val,
                             final DropDownData enums,
                             RuleModeller model,
                             String variableType) {
        this( val,
              enums,
              model,
              variableType,
              false );
    }

    public ActionValueEditor(final ActionFieldValue val,
                             final DropDownData enums,
                             RuleModeller model,
                             String variableType,
                             boolean readOnly) {

        this.readOnly = readOnly;

        if ( val.type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            this.enums = DropDownData.create( new String[]{"true", "false"} );
        } else {
            this.enums = enums;
        }
        this.root = new SimplePanel();
        this.value = val;
        this.model = model;
        this.variableType = variableType;
        refresh();
        initWidget( root );
    }

    private void refresh() {
        root.clear();

        //If undefined let the user pick
        if ( value.nature == FieldNature.TYPE_UNDEFINED ) {

            //Automatic decisions regarding FieldNature
            if ( value.value != null && value.value.length() > 0 ) {
                if ( value.value.charAt( 0 ) == '=' ) {
                    value.nature = FieldNature.TYPE_VARIABLE;
                } else {
                    value.nature = FieldNature.TYPE_LITERAL;
                }
            } else {
                root.add( choice() );
                return;
            }
        }

        //Template TextBoxes are always Strings as they hold the template key for the actual value
        if ( value.nature == FieldNature.TYPE_TEMPLATE ) {
            Widget box = boundTextBox( this.value );
            root.add( box );
            return;
        }

        //Variable fields (including bound enumeration fields)
        if ( value.nature == FieldNature.TYPE_VARIABLE ) {
            Widget list = boundVariable( value );
            root.add( list );
            return;
        }

        //Enumerations - since this does not use FieldNature it should follow those that do
        if ( enums != null && (enums.fixedList != null || enums.queryExpression != null) ) {
            Widget list = boundEnum( value );
            root.add( list );
            return;
        }

        //Fall through for all remaining FieldNatures
        Widget box = boundTextBox( this.value );
        root.add( box );

    }

    private Widget boundVariable(final FieldNature c) {
        // If there is a bound variable that is the same type of the current variable type, then display a list
        ListBox listVariable = new ListBox();
        listVariable.addItem( constants.Choose() );
        List<String> bindings = getApplicableBindings();
        for ( String v : bindings ) {
            listVariable.addItem( v );
        }

        //Pre-select applicable item
        if ( value.value.equals( "=" ) ) {
            listVariable.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listVariable.getItemCount(); i++ ) {
                if ( listVariable.getItemText( i ).equals( value.value.substring( 1 ) ) ) {
                    listVariable.setSelectedIndex( i );
                }
            }
        }

        //Add event handler
        if ( listVariable.getItemCount() > 0 ) {
            listVariable.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    ListBox w = (ListBox) event.getSource();
                    value.value = "=" + w.getValue( w.getSelectedIndex() );
                    executeOnChageCommand();
                    makeDirty();
                    refresh();
                }
            } );
        }

        if ( this.readOnly ) {
            return new SmallLabel( listVariable.getItemText( listVariable.getSelectedIndex() ) );
        }

        return listVariable;
    }

    private Widget boundEnum(final FieldNature c) {
        EnumDropDown enumDropDown = new EnumDropDown( value.value,
                                                      new DropDownValueChanged() {

                                                          public void valueChanged(String newText,
                                                                                   String newValue) {
                                                              value.value = newValue;
                                                              executeOnChageCommand();
                                                              makeDirty();
                                                          }
                                                      },
                                                      enums );

        if ( this.readOnly ) {
            return new SmallLabel( enumDropDown.getItemText( enumDropDown.getSelectedIndex() ) );
        } else {
            return enumDropDown;
        }
    }

    private Widget boundTextBox(final ActionFieldValue c) {
        final TextBox box = new TextBox();
        box.setStyleName( "constraint-value-Editor" );
        if ( c.value == null ) {
            box.setText( "" );
        } else {
            if ( c.value.trim().equals( "" ) ) {
                c.value = "";
            }
            box.setText( c.value );
        }

        if ( c.value == null || c.value.length() < 5 ) {
            box.setVisibleLength( 6 );
        } else {
            box.setVisibleLength( c.value.length() - 1 );
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                c.value = box.getText();
                executeOnChageCommand();
                makeDirty();
            }
        } );

        box.addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp(KeyUpEvent event) {
                box.setVisibleLength( box.getText().length() );
            }
        } );

        //Template TextBoxes are always Strings as they hold the template key for the actual value
        if ( value.nature != FieldNature.TYPE_TEMPLATE && value.type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            box.addKeyPressHandler( new NumbericFilterKeyPressHandler( box ) );
        }

        if ( this.readOnly ) {
            return new SmallLabel( box.getText() );
        }

        return box;
    }

    private Widget choice() {
        if ( this.readOnly ) {
            return new HTML();
        } else {
            Image clickme = new Image( images.edit() );
            clickme.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    showTypeChoice( (Widget) event.getSource() );
                }
            } );
            return clickme;
        }
    }

    protected void showTypeChoice(Widget w) {
        final FormStylePopup form = new FormStylePopup( images.newexWiz(),
                                                        constants.FieldValue() );
        Button lit = new Button( constants.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                value.nature = FieldNature.TYPE_LITERAL;
                value.value = "";
                makeDirty();
                executeOnChageCommand();
                refresh();
                form.hide();
            }
        } );

        form.addAttribute( constants.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( constants.Literal(),
                                                   constants.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation() ) ) );

        if ( model.isTemplate() ) {
            Button templateButton = new Button( constants.TemplateKey() );
            templateButton.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    value.nature = FieldNature.TYPE_TEMPLATE;
                    value.value = "";
                    makeDirty();
                    refresh();
                    form.hide();
                }
            } );
            form.addAttribute( constants.TemplateKey() + ":",
                               widgets( templateButton,
                                        new InfoPopup( constants.Literal(),
                                                       constants.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation() ) ) );
        }

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( constants.AdvancedSection() ) );

        Button formula = new Button( constants.Formula() );
        formula.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                value.nature = FieldNature.TYPE_FORMULA;
                value.value = "=";
                makeDirty();
                refresh();
                form.hide();
            }
        } );

        // If there is a bound Facts or Fields that are of the same type as the current variable type, then show a button
        List<String> bindings = getApplicableBindings();
        if ( bindings.size() > 0 ) {
            Button variable = new Button( constants.BoundVariable() );
            form.addAttribute( constants.BoundVariable() + ":",
                               variable );
            variable.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    value.nature = FieldNature.TYPE_VARIABLE;
                    value.value = "=";
                    makeDirty();
                    refresh();
                    form.hide();
                }
            } );
        }

        form.addAttribute( constants.Formula() + ":",
                           widgets( formula,
                                    new InfoPopup( constants.Formula(),
                                                   constants.FormulaTip() ) ) );

        form.show();
    }

    private List<String> getApplicableBindings() {
        List<String> bindings = new ArrayList<String>();

        //Examine LHS Fact and Field bindings and RHS (new) Fact bindings
        for ( String v : model.getModel().getAllVariables() ) {

            //LHS FactPattern
            FactPattern fp = model.getModel().getLHSBoundFact( v );
            if ( fp != null ) {
                if ( isLHSFactTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }

            //LHS FieldConstraint
            FieldConstraint fc = model.getModel().getLHSBoundField( v );
            if ( fc != null ) {
                if ( isLHSFieldTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }

            //RHS ActionInsertFact
            ActionInsertFact aif = model.getModel().getRHSBoundFact( v );
            if ( aif != null ) {
                if ( isRHSFieldTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }
        }

        return bindings;
    }

    private boolean isLHSFactTypeEquivalent(String boundVariable) {
        String boundFactType = model.getModel().getLHSBoundFact( boundVariable ).getFactType();

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.model.getSuggestionCompletions().getEnumValues( boundFactType,
                                                                               this.value.field );
            return isEnumEquivalent( dd );
        }

        //If the types are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFactType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isLHSFieldTypeEquivalent(String boundVariable) {
        String boundFieldType = model.getModel().getLHSBindingType( boundVariable );

        //If the fieldTypes are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFieldType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
                return false;
            }
            FieldConstraint fc = this.model.getModel().getLHSBoundField( boundVariable );
            if ( fc instanceof SingleFieldConstraint ) {
                String fieldName = ((SingleFieldConstraint) fc).getFieldName();
                String parentFactTypeForBinding = this.model.getModel().getLHSParentFactPatternForBinding( boundVariable ).getFactType();
                String[] dd = this.model.getSuggestionCompletions().getEnumValues( parentFactTypeForBinding,
                                                                                   fieldName );
                return isEnumEquivalent( dd );
            }
            return false;
        }

        //If the fieldTypes are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFieldType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isRHSFieldTypeEquivalent(String boundVariable) {
        String boundFactType = model.getModel().getRHSBoundFact( boundVariable ).factType;
        if ( boundFactType == null ) {
            return false;
        }
        if ( this.variableType == null ) {
            return false;
        }

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.model.getSuggestionCompletions().getEnumValues( boundFactType,
                                                                               this.value.field );
            return isEnumEquivalent( dd );
        }

        //If the types are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFactType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isEnumEquivalent(String[] values) {
        if ( values == null && this.enums.fixedList != null ) {
            return false;
        }
        if ( values != null && this.enums.fixedList == null ) {
            return false;
        }
        if ( values.length != this.enums.fixedList.length ) {
            return false;
        }
        for ( int i = 0; i < values.length; i++ ) {
            if ( !values[i].equals( this.enums.fixedList[i] ) ) {
                return false;
            }
        }
        return true;
    }

    private Widget widgets(Button lit,
                           InfoPopup popup) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( lit );
        h.add( popup );
        return h;
    }

    private void executeOnChageCommand() {
        if ( this.onChangeCommand != null ) {
            this.onChangeCommand.execute();
        }
    }

    public Command getOnChangeCommand() {
        return onChangeCommand;
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
    }

}
