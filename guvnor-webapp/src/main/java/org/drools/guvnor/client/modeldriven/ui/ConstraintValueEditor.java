/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.modeldriven.ui;

import java.util.List;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.factconstraints.customform.CustomFormConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.packages.WorkingSetManager;
import org.drools.guvnor.client.resources.Images;

/**
 * This is an editor for constraint values.
 * How this behaves depends on the constraint value type.
 * When the constraint value has no type, it will allow the user to choose the first time.
 */
public class ConstraintValueEditor extends DirtyableComposite {

    private Constants                        constants = ((Constants) GWT.create( Constants.class ));
    private static Images                    images    = GWT.create( Images.class );

    private final FactPattern                pattern;
    private final String                     fieldName;
    private final SuggestionCompletionEngine sce;
    private final BaseSingleFieldConstraint  constraint;
    private final Panel                      panel;
    private final RuleModel                  model;
    private final RuleModeller               modeller;
    private final boolean                    numericValue;
    private DropDownData                     dropDownData;
    private String                           fieldType;
    private boolean                          readOnly;
    private Command                          onValueChangeCommand;
    private boolean                          isDropDownDataEnum;

    public ConstraintValueEditor(FactPattern pattern,
                                 String fieldName,
                                 BaseSingleFieldConstraint con,
                                 RuleModeller modeller,
                                 String valueType,
                                 boolean readOnly) {
        this.pattern = pattern;
        this.fieldName = fieldName;
        this.sce = modeller.getSuggestionCompletions();
        this.constraint = con;
        this.panel = new SimplePanel();
        this.model = modeller.getModel();
        this.modeller = modeller;

        valueType = sce.getFieldType( pattern.getFactType(),
                                      fieldName );
        this.fieldType = valueType;
        this.numericValue = SuggestionCompletionEngine.TYPE_NUMERIC.equals( valueType );

        this.readOnly = readOnly;
        if ( SuggestionCompletionEngine.TYPE_BOOLEAN.equals( valueType ) ) {
            this.dropDownData = DropDownData.create( new String[]{"true", "false"} ); //NON-NLS
            isDropDownDataEnum = false;
        } else {
            this.dropDownData = sce.getEnums( pattern,
                                              fieldName );
            isDropDownDataEnum = true;
        }

        refreshEditor();
        initWidget( panel );
    }

    private void refreshEditor() {
        panel.clear();
        Widget constraintWidget = null;
        if ( constraint.getConstraintValueType() == SingleFieldConstraint.TYPE_UNDEFINED ) {
            Image clickme = new Image( images.edit() );
            clickme.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    showTypeChoice( (Widget) event.getSource(),
                                    constraint );
                }
            } );
            constraintWidget = clickme;
        } else {
            switch ( constraint.getConstraintValueType() ) {
                case SingleFieldConstraint.TYPE_LITERAL :
                case SingleFieldConstraint.TYPE_ENUM :

                    if ( this.constraint instanceof SingleFieldConstraint ) {
                        final SingleFieldConstraint con = (SingleFieldConstraint) this.constraint;
                        CustomFormConfiguration customFormConfiguration = WorkingSetManager.getInstance().getCustomFormConfiguration( modeller.getAsset().metaData.packageName,
                                                                                                                                      pattern.getFactType(),
                                                                                                                                      fieldName );
                        if ( customFormConfiguration != null ) {
                            constraintWidget = new Button( con.getValue(),
                                                           new ClickHandler() {

                                                               public void onClick(ClickEvent event) {
                                                                   showTypeChoice( (Widget) event.getSource(),
                                                                                   constraint );
                                                               }
                                                           } );
                            ((Button)constraintWidget).setEnabled(!this.readOnly);
                            break;
                        }
                    }

                    if ( this.dropDownData != null ) {
                        constraintWidget = new EnumDropDownLabel( this.pattern,
                                                                  this.fieldName,
                                                                  this.sce,
                                                                  this.constraint, !this.readOnly);
                        if (!this.readOnly){
                        	((EnumDropDownLabel) constraintWidget).setOnValueChangeCommand( new Command() {

                                public void execute() {
                                    executeOnValueChangeCommand();
                                }
                            } );
                        }
                        
                    } else if ( SuggestionCompletionEngine.TYPE_DATE.equals( this.fieldType ) ) {

                        DatePickerLabel datePicker = new DatePickerLabel( constraint.getValue() );

                        // Set the default time
                        this.constraint.setValue( datePicker.getDateString() );

                        if ( !this.readOnly ) {
                            datePicker.addValueChanged( new ValueChanged() {

                                public void valueChanged(String newValue) {
                                    executeOnValueChangeCommand();
                                    constraint.setValue( newValue );
                                }
                            } );

                            constraintWidget = datePicker;
                        } else {
                            constraintWidget = new SmallLabel( this.constraint.getValue() );
                        }
                    } else {
                        if ( !this.readOnly ) {
                            constraintWidget = new DefaultLiteralEditor( this.constraint,
                                                                         this.numericValue );
                            ((DefaultLiteralEditor) constraintWidget).setOnValueChangeCommand( new Command() {

                                public void execute() {
                                    executeOnValueChangeCommand();
                                }
                            } );
                        } else {
                            constraintWidget = new SmallLabel( this.constraint.getValue() );
                        }
                    }
                    break;
                case SingleFieldConstraint.TYPE_RET_VALUE :
                    constraintWidget = returnValueEditor();
                    break;
                case SingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE :
                    constraintWidget = expressionEditor();
                    break;
                case SingleFieldConstraint.TYPE_VARIABLE :
                    constraintWidget = variableEditor();
                    break;
                case BaseSingleFieldConstraint.TYPE_TEMPLATE :
                    constraintWidget = new DefaultLiteralEditor( this.constraint,
                                                                 false );
                    break;
                default :
                    break;
            }
        }
        panel.add( constraintWidget );
    }

    private Widget variableEditor() {

        if ( this.readOnly ) {
            return new SmallLabel( this.constraint.getValue() );
        }

        List<String> vars = this.model.getBoundVariablesInScope( this.constraint );

        final ListBox box = new ListBox();

        if ( this.constraint.getValue() == null ) {
            box.addItem( constants.Choose() );
        }

        int j = 0;
        for ( String var : vars ) {
            FactPattern f = model.getBoundFact( var );
            String fv = model.getBindingType( var );
            if ( (f != null && f.getFactType().equals( this.fieldType )) || (fv != null && fv.equals( this.fieldType )) ) {
                box.addItem( var );
                if ( this.constraint.getValue() != null && this.constraint.getValue().equals( var ) ) {
                    box.setSelectedIndex( j );
                }
                j++;
            } else {
                // for collection, present the list of possible bound variable
                String factCollectionType = sce.getParametricFieldType( pattern.getFactType(),
                                                                        this.fieldName );
                if ( (f != null && factCollectionType != null && f.getFactType().equals( factCollectionType )) || (factCollectionType != null && factCollectionType.equals( fv )) ) {
                    box.addItem( var );
                    if ( this.constraint.getValue() != null && this.constraint.getValue().equals( var ) ) {
                        box.setSelectedIndex( j );
                    }
                    j++;
                }
            }
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                executeOnValueChangeCommand();
                constraint.setValue( box.getItemText( box.getSelectedIndex() ) );
            }
        } );

        return box;
    }

    /**
     * An editor for the retval "formula" (expression).
     */
    private Widget returnValueEditor() {
    	TextBox box = new BoundTextBox( constraint );
    	
    	if ( this.readOnly ) {
            return new SmallLabel( box.getText() );
        }
    	
        String msg = constants.FormulaEvaluateToAValue();
        Image img = new Image( images.functionAssets() );
        img.setTitle( msg );
        box.setTitle( msg );        
        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                executeOnValueChangeCommand();
            }
        } );
        
        Widget ed = widgets( img,
                             box );
        return ed;
    }

    private Widget expressionEditor() {
        if ( !(this.constraint instanceof SingleFieldConstraint) ) {
            throw new IllegalArgumentException( "Expected SingleFieldConstraint, but " + constraint.getClass().getName() + " found." );
        }
        ExpressionBuilder builder = new ExpressionBuilder( this.modeller,
                                                           ((SingleFieldConstraint) this.constraint).getExpressionValue(), this.readOnly );
        builder.addExpressionTypeChangeHandler( new ExpressionTypeChangeHandler() {

            public void onExpressionTypeChanged(ExpressionTypeChangeEvent event) {
                System.out.println( "type changed: " + event.getOldType() + " -> " + event.getNewType() );
            }
        } );
        builder.addOnModifiedCommand( new Command() {

            public void execute() {
                executeOnValueChangeCommand();
            }
        } );
        Widget ed = widgets( new HTML( "&nbsp;" ),
                             builder );
        return ed;
    }

    /**
     * Show a list of possibilities for the value type.
     */
    private void showTypeChoice(Widget w,
                                final BaseSingleFieldConstraint con) {

        CustomFormConfiguration customFormConfiguration = WorkingSetManager.getInstance().getCustomFormConfiguration( modeller.getAsset().metaData.packageName,
                                                                                                                      pattern.getFactType(),
                                                                                                                      fieldName );

        if ( customFormConfiguration != null ) {
            if ( !(con instanceof SingleFieldConstraint) ) {
                Window.alert( "Unexpected constraint type!" );
                return;
            }
            final CustomFormPopUp customFormPopUp = new CustomFormPopUp( images.newexWiz(),
                                                                         constants.FieldValue(),
                                                                         customFormConfiguration );

            final SingleFieldConstraint sfc = (SingleFieldConstraint) con;

            customFormPopUp.addOkButtonHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    sfc.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
                    sfc.setId( customFormPopUp.getFormId() );
                    sfc.setValue( customFormPopUp.getFormValue() );
                    doTypeChosen( customFormPopUp );
                }
            } );

            customFormPopUp.show( sfc.getId(),
                                  sfc.getValue() );
            return;
        }

        final FormStylePopup form = new FormStylePopup( images.newexWiz(),
                                                        constants.FieldValue() );

        Button lit = new Button( constants.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                con.setConstraintValueType( isDropDownDataEnum ? SingleFieldConstraint.TYPE_ENUM : SingleFieldConstraint.TYPE_LITERAL );
                doTypeChosen( form );
            }
        } );
        form.addAttribute( constants.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( constants.LiteralValue(),
                                                   constants.LiteralValTip() ) ) );

        if ( modeller.isTemplate() ) {
            String templateKeyLabel = constants.TemplateKey();
            Button templateKeyButton = new Button( templateKeyLabel );
            templateKeyButton.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
                    doTypeChosen( form );
                }
            } );

            form.addAttribute( templateKeyLabel + ":",
                               widgets( templateKeyButton,
                                        new InfoPopup( templateKeyLabel,
                                                       constants.LiteralValTip() ) ) );
        }

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( constants.AdvancedOptions() ) );

        //only want to show variables if we have some !
        if ( this.model.getBoundVariablesInScope( this.constraint ).size() > 0 || SuggestionCompletionEngine.TYPE_COLLECTION.equals( this.fieldType ) ) {
            List<String> vars = this.model.getBoundFacts();
            boolean foundABouncVariableThatMatches = false;
            for ( String var : vars ) {
                FactPattern f = model.getBoundFact( var );
                String fieldConstraint = model.getBindingType( var );

                if ( (f != null && f.getFactType() != null && f.getFactType().equals( this.fieldType )) || (this.fieldType != null && this.fieldType.equals( fieldConstraint )) ) {
                    foundABouncVariableThatMatches = true;
                    break;
                } else {
                    // for collection, present the list of possible bound variable
                    String factCollectionType = sce.getParametricFieldType( pattern.getFactType(),
                                                                            this.fieldName );
                    if ( (f != null && factCollectionType != null && f.getFactType().equals( factCollectionType )) || (factCollectionType != null && factCollectionType.equals( fieldConstraint )) ) {
                        foundABouncVariableThatMatches = true;
                        break;
                    }
                }
            }
            if ( foundABouncVariableThatMatches ) {
                Button variable = new Button( constants.BoundVariable() );
                variable.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        con.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
                        doTypeChosen( form );
                    }
                } );
                form.addAttribute( constants.AVariable(),
                                   widgets( variable,
                                            new InfoPopup( constants.ABoundVariable(),
                                                           constants.BoundVariableTip() ) ) );
            }
        }

        Button formula = new Button( constants.NewFormula() );
        formula.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                con.setConstraintValueType( SingleFieldConstraint.TYPE_RET_VALUE );
                doTypeChosen( form );
            }
        } );

        form.addAttribute( constants.AFormula() + ":",
                           widgets( formula,
                                    new InfoPopup( constants.AFormula(),
                                                   constants.FormulaExpressionTip() ) ) );

        Button expression = new Button( constants.ExpressionEditor() );
        expression.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                con.setConstraintValueType( SingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE );
                doTypeChosen( form );
            }
        } );

        form.addAttribute( constants.ExpressionEditor() + ":",
                           widgets( expression,
                                    new InfoPopup( constants.ExpressionEditor(),
                                                   constants.ExpressionEditor() ) ) );

        form.show();
    }

    private void doTypeChosen(final FormStylePopup form) {
        executeOnValueChangeCommand();
        refreshEditor();
        form.hide();
    }

    private Panel widgets(Widget left,
                          Widget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        panel.add( left );
        panel.add( right );
        panel.setWidth( "100%" );
        return panel;
    }

    private void executeOnValueChangeCommand() {
        if ( this.onValueChangeCommand != null ) {
            this.onValueChangeCommand.execute();
        }
    }

    public boolean isDirty() {
        return super.isDirty();
    }

    public void setOnValueChangeCommand(Command onValueChangeCommand) {
        this.onValueChangeCommand = onValueChangeCommand;
    }
}
