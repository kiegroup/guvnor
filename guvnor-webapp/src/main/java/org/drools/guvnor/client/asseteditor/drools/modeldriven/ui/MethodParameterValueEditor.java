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
import org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

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
public class MethodParameterValueEditor extends DirtyableComposite {

    private Constants           constants            = GWT.create( Constants.class );
    private static Images       images               = GWT.create( Images.class );

    private ActionFieldFunction methodParameter;
    private DropDownData        enums;
    private SimplePanel         root;
    private RuleModeller        model                = null;
    private String              parameterType        = null;
    private Command             onValueChangeCommand = null;

    public MethodParameterValueEditor(final ActionFieldFunction val,
                                      final DropDownData enums,
                                      RuleModeller model,
                                      String parameterType,
                                      Command onValueChangeCommand) {
        if ( val.type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            this.enums = DropDownData.create( new String[]{"true", "false"} );
        } else {
            this.enums = enums;
        }
        this.root = new SimplePanel();
        this.methodParameter = val;
        this.model = model;
        this.parameterType = parameterType;
        this.onValueChangeCommand = onValueChangeCommand;
        refresh();
        initWidget( root );
    }

    private void refresh() {
        root.clear();
        if ( enums != null && (enums.fixedList != null || enums.queryExpression != null) ) {
            root.add( new EnumDropDown( methodParameter.value,
                                        new DropDownValueChanged() {
                                            public void valueChanged(String newText,
                                                                     String newValue) {
                                                methodParameter.value = newValue;
                                                if ( onValueChangeCommand != null ) {
                                                    onValueChangeCommand.execute();
                                                }
                                                makeDirty();
                                            }
                                        },
                                        enums ) );
        } else {

            if ( methodParameter.nature == FieldNature.TYPE_UNDEFINED ) {
                // we have a blank slate..
                // have to give them a choice
                root.add( choice() );
            } else {
                if ( methodParameter.nature == FieldNature.TYPE_VARIABLE ) {
                    ListBox list = boundVariable( methodParameter );
                    root.add( list );
                } else {
                    TextBox box = boundTextBox( this.methodParameter );
                    root.add( box );
                }

            }

        }
    }

    private ListBox boundVariable(final FieldNature c) {
        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then propose a list
         */
        ListBox listVariable = new ListBox();
        List<String> vars = model.getModel().getLHSBoundFacts();
        for ( String v : vars ) {
            FactPattern factPattern = model.getModel().getLHSBoundFact( v );
            if ( factPattern.getFactType().equals( this.methodParameter.type ) ) {
                // First selection is empty
                if ( listVariable.getItemCount() == 0 ) {
                    listVariable.addItem( "..." );
                }

                listVariable.addItem( v );
            }
        }
        /*
         * add the bound variable of the rhs
         */
        List<String> vars2 = model.getModel().getRHSBoundFacts();
        for ( String v : vars2 ) {
            ActionInsertFact factPattern = model.getModel().getRHSBoundFact( v );
            if ( factPattern.factType.equals( this.methodParameter.type ) ) {
                // First selection is empty
                if ( listVariable.getItemCount() == 0 ) {
                    listVariable.addItem( "..." );
                }
                listVariable.addItem( v );
            }
        }
        if ( methodParameter.value.equals( "=" ) ) {
            listVariable.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listVariable.getItemCount(); i++ ) {
                if ( listVariable.getItemText( i ).equals( methodParameter.value ) ) {
                    listVariable.setSelectedIndex( i );
                }
            }
        }
        if ( listVariable.getItemCount() > 0 ) {

            listVariable.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    ListBox w = (ListBox) event.getSource();
                    methodParameter.value = w.getValue( w.getSelectedIndex() );
                    if ( onValueChangeCommand != null ) {
                        onValueChangeCommand.execute();
                    }
                    makeDirty();
                    refresh();
                }

            } );
        }
        return listVariable;
    }

    private TextBox boundTextBox(final ActionFieldValue c) {
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
                if ( onValueChangeCommand != null ) {
                    onValueChangeCommand.execute();
                }
                makeDirty();
            }

        } );

        box.addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp(KeyUpEvent event) {
                box.setVisibleLength( box.getText().length() );
            }
        } );

        if ( methodParameter.type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            box.addKeyPressHandler( new NumbericFilterKeyPressHandler( box ) );
        }

        return box;
    }

    private Widget choice() {
        Image clickme = new Image( images.edit() );
        clickme.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showTypeChoice( (Widget) event.getSource() );
            }
        } );
        return clickme;
    }

    protected void showTypeChoice(Widget w) {
        final FormStylePopup form = new FormStylePopup( images.newexWiz(),
                                                        constants.FieldValue() );
        Button lit = new Button( constants.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                methodParameter.nature = FieldNature.TYPE_LITERAL;
                methodParameter.value = " ";
                makeDirty();
                refresh();
                form.hide();
            }

        } );

        form.addAttribute( constants.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( constants.Literal(),
                                                   constants.LiteralValTip() ) ) );
        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( constants.AdvancedSection() ) );

        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then show abutton
         */
        List<String> vars = model.getModel().getLHSBoundFacts();
        List<String> vars2 = model.getModel().getRHSBoundFacts();
        for ( String i : vars2 ) {
            vars.add( i );
        }
        for ( String v : vars ) {
            boolean createButton = false;
            Button variable = new Button( constants.BoundVariable() );
            if ( vars2.contains( v ) == false ) {
                FactPattern factPattern = model.getModel().getLHSBoundFact( v );
                if ( factPattern.getFactType().equals( this.parameterType ) ) {
                    createButton = true;
                }
            } else {
                ActionInsertFact factPattern = model.getModel().getRHSBoundFact( v );
                if ( factPattern.factType.equals( this.parameterType ) ) {
                    createButton = true;
                }
            }
            if ( createButton == true ) {
                form.addAttribute( constants.BoundVariable() + ":",
                                   variable );
                variable.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        methodParameter.nature = FieldNature.TYPE_VARIABLE;
                        methodParameter.value = "=";
                        makeDirty();
                        refresh();
                        form.hide();
                    }

                } );
                break;
            }

        }

        form.show();
    }

    private Widget widgets(Button lit,
                           InfoPopup popup) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( lit );
        h.add( popup );
        return h;
    }

}
