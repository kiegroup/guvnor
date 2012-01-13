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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.events.TemplateVariablesChangedEvent;
import org.drools.guvnor.client.common.DatePickerLabel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.factconstraints.customform.CustomFormConfiguration;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.HasOperator;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
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

/**
 * This is an editor for constraint values. How this behaves depends on the
 * constraint value type. When the constraint value has no type, it will allow
 * the user to choose the first time.
 */
public class ConstraintValueEditor extends DirtyableComposite {

    private Constants                        constants        = ((Constants) GWT.create( Constants.class ));
    private static Images                    images           = GWT.create( Images.class );

    private final FactPattern                pattern;
    private String                           fieldName;
    private String                           qualifiedFieldName;
    private final SuggestionCompletionEngine sce;
    private final BaseSingleFieldConstraint  constraint;
    private final Panel                      panel;
    private final RuleModel                  model;
    private final RuleModeller               modeller;
    private final EventBus                   eventBus;

    private boolean                          isNumeric;
    private DropDownData                     dropDownData;
    private String                           fieldType;
    private boolean                          readOnly;
    private Command                          onValueChangeCommand;
    private boolean                          isDropDownDataEnum;
    private Widget                           constraintWidget = null;

    public ConstraintValueEditor(FactPattern pattern,
                                 String fieldName,
                                 BaseSingleFieldConstraint con,
                                 RuleModeller modeller,
                                 EventBus eventBus,
                                 boolean readOnly) {
        this.pattern = pattern;
        this.sce = modeller.getSuggestionCompletions();
        this.constraint = con;
        this.panel = new SimplePanel();
        this.model = modeller.getModel();
        this.modeller = modeller;
        this.eventBus = eventBus;
        this.readOnly = readOnly;

        if ( con instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) con;
            this.fieldName = sfexp.getExpressionLeftSide().getFieldName();
            this.fieldType = sfexp.getExpressionLeftSide().getGenericType();
            this.qualifiedFieldName = this.fieldName;

        } else if ( con instanceof ConnectiveConstraint ) {
            ConnectiveConstraint cc = (ConnectiveConstraint) con;
            fieldName = cc.getFieldName();
            if ( fieldName != null && fieldName.contains( "." ) ) {
                fieldName = fieldName.substring( fieldName.indexOf( "." ) + 1 );
            }
            this.fieldName = fieldName;
            this.fieldType = cc.getFieldType();
            this.qualifiedFieldName = cc.getFieldName();

        } else {
            this.qualifiedFieldName = fieldName;
            String factType = pattern.getFactType();
            if ( fieldName != null && fieldName.contains( "." ) ) {
                int index = fieldName.indexOf( "." );
                factType = fieldName.substring( 0,
                                                index );
                fieldName = fieldName.substring( index + 1 );
            }
            this.fieldName = fieldName;
            this.fieldType = sce.getFieldType( factType,
                                               fieldName );
        }

        refreshEditor();
        initWidget( panel );
    }

    public BaseSingleFieldConstraint getConstraint() {
        return constraint;
    }

    private void refreshEditor() {
        panel.clear();
        constraintWidget = null;

        //Expressions' fieldName and hence fieldType can change without creating a new ConstraintValueEditor. 
        //SingleFieldConstraints and their ConnectiveConstraints cannot have the fieldName or fieldType changed 
        //without first deleting and re-creating.
        if ( this.constraint instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) this.constraint;
            this.fieldName = sfexp.getExpressionLeftSide().getFieldName();
            this.fieldType = sfexp.getExpressionLeftSide().getGenericType();
        }

        //Set applicable flags and reference data depending upon type
        this.isNumeric = SuggestionCompletionEngine.TYPE_NUMERIC.equals( this.fieldType );
        if ( SuggestionCompletionEngine.TYPE_BOOLEAN.equals( this.fieldType ) ) {
            this.isDropDownDataEnum = false;
            this.dropDownData = DropDownData.create( new String[]{"true", "false"} );
        } else {
            this.isDropDownDataEnum = true;
            this.dropDownData = sce.getEnums( pattern,
                                              fieldName );
        }

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

                    constraintWidget = literalEditor();
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

    private Widget literalEditor() {

        //Custom screen
        if ( this.constraint instanceof SingleFieldConstraint ) {
            final SingleFieldConstraint con = (SingleFieldConstraint) this.constraint;
            CustomFormConfiguration customFormConfiguration = WorkingSetManager.getInstance().getCustomFormConfiguration( modeller.getAsset().getMetaData().getModuleName(),
                                                                                                                          pattern.getFactType(),
                                                                                                                          fieldName );
            if ( customFormConfiguration != null ) {
                Button btnCustom = new Button( con.getValue(),
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       showTypeChoice( (Widget) event.getSource(),
                                                                       constraint );
                                                   }
                                               } );
                btnCustom.setEnabled( !this.readOnly );
                return btnCustom;
            }
        }

        //Enumeration
        if ( this.dropDownData != null ) {
            EnumDropDownLabel enumDropDown = new EnumDropDownLabel( this.pattern,
                                                                    this.qualifiedFieldName,
                                                                    this.sce,
                                                                    this.constraint,
                                                                    !this.readOnly );
            if ( !this.readOnly ) {
                enumDropDown.setOnValueChangeCommand( new Command() {

                    public void execute() {
                        executeOnValueChangeCommand();
                    }
                } );
            }
            return enumDropDown;
        }

        //Date picker
        boolean isCEPOperator = false;
        if ( this.constraint instanceof HasOperator ) {
            isCEPOperator = SuggestionCompletionEngine.isCEPOperator( ((HasOperator) this.constraint).getOperator() );
        }
        if ( SuggestionCompletionEngine.TYPE_DATE.equals( this.fieldType ) || (SuggestionCompletionEngine.TYPE_THIS.equals( this.fieldName ) && isCEPOperator) ) {

            DatePickerLabel datePicker = new DatePickerLabel( constraint.getValue() );
            this.constraint.setValue( datePicker.getDateString() );

            if ( !this.readOnly ) {
                datePicker.addValueChanged( new ValueChanged() {

                    public void valueChanged(String newValue) {
                        executeOnValueChangeCommand();
                        constraint.setValue( newValue );
                    }
                } );

                return datePicker;

            } else {
                return new SmallLabel( this.constraint.getValue() );
            }
        }

        //Default editor
        if ( !this.readOnly ) {
            DefaultLiteralEditor dle = new DefaultLiteralEditor( this.constraint,
                                                                 this.isNumeric );
            dle.setOnValueChangeCommand( new Command() {

                public void execute() {
                    executeOnValueChangeCommand();
                }
            } );

            return dle;

        } else {
            return new SmallLabel( this.constraint.getValue() );
        }

    }

    private Widget variableEditor() {

        if ( this.readOnly ) {
            return new SmallLabel( this.constraint.getValue() );
        }

        final ListBox box = new ListBox();
        box.addItem( constants.Choose() );

        List<String> bindingsInScope = this.model.getBoundVariablesInScope( this.constraint );
        List<String> applicableBindingsInScope = getApplicableBindingsInScope( bindingsInScope );
        for ( String var : applicableBindingsInScope ) {
            box.addItem( var );
            if ( this.constraint.getValue() != null && this.constraint.getValue().equals( var ) ) {
                box.setSelectedIndex( box.getItemCount() - 1 );
            }
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                executeOnValueChangeCommand();
                int selectedIndex = box.getSelectedIndex();
                if ( selectedIndex > 0 ) {
                    constraint.setValue( box.getItemText( selectedIndex ) );
                } else {
                    constraint.setValue( null );
                }
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
        ExpressionBuilder builder = null;
        builder = new ExpressionBuilder( this.modeller,
                                         this.eventBus,
                                         this.constraint.getExpressionValue(),
                                         this.readOnly );

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

        CustomFormConfiguration customFormConfiguration = WorkingSetManager.getInstance().getCustomFormConfiguration( modeller.getAsset().getMetaData().getModuleName(),
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
                con.setConstraintValueType( isDropDownDataEnum && dropDownData != null ? SingleFieldConstraint.TYPE_ENUM : SingleFieldConstraint.TYPE_LITERAL );
                doTypeChosen( form );
            }
        } );

        boolean showLiteralOrFormula = true;
        if ( con instanceof SingleFieldConstraint ) {
            SingleFieldConstraint sfc = (SingleFieldConstraint) con;
            String fieldName = sfc.getFieldName();
            if ( fieldName.contains( "." ) ) {
                fieldName = fieldName.substring( fieldName.indexOf( "." ) + 1 );
            }
            if ( fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) ) {
                showLiteralOrFormula = SuggestionCompletionEngine.isCEPOperator( sfc.getOperator() );
            }
        } else if ( con instanceof ConnectiveConstraint ) {
            ConnectiveConstraint cc = (ConnectiveConstraint) con;
            String fieldName = cc.getFieldName();
            if ( fieldName.contains( "." ) ) {
                fieldName = fieldName.substring( fieldName.indexOf( "." ) + 1 );
            }
            if ( fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) ) {
                showLiteralOrFormula = SuggestionCompletionEngine.isCEPOperator( cc.getOperator() );
            }
        }

        if ( showLiteralOrFormula ) {
            form.addAttribute( constants.LiteralValue() + ":",
                               widgets( lit,
                                        new InfoPopup( constants.LiteralValue(),
                                                       constants.LiteralValTip() ) ) );
        }

        if ( modeller.isTemplate() ) {
            String templateKeyLabel = constants.TemplateKey();
            Button templateKeyButton = new Button( templateKeyLabel );
            templateKeyButton.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
                    doTypeChosen( form );

                    //Signal change in Template variables
                    TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent( model );
                    eventBus.fireEventFromSource( tvce,
                                                  model );
                }
            } );

            form.addAttribute( templateKeyLabel + ":",
                               widgets( templateKeyButton,
                                        new InfoPopup( templateKeyLabel,
                                                       constants.LiteralValTip() ) ) );
        }

        if ( showLiteralOrFormula ) {
            form.addRow( new HTML( "<hr/>" ) );
            form.addRow( new SmallLabel( constants.AdvancedOptions() ) );
        }

        //only want to show variables if we have some !
        List<String> bindingsInScope = this.model.getBoundVariablesInScope( this.constraint );
        if ( bindingsInScope.size() > 0
                || SuggestionCompletionEngine.TYPE_COLLECTION.equals( this.fieldType ) ) {

            List<String> applicableBindingsInScope = getApplicableBindingsInScope( bindingsInScope );
            if ( applicableBindingsInScope.size() > 0 ) {

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

        if ( showLiteralOrFormula ) {
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
        }

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

    private List<String> getApplicableBindingsInScope(List<String> bindingsInScope) {
        List<String> applicableBindingsInScope = new ArrayList<String>();

        //Examine LHS Fact and Field bindings and RHS (new) Fact bindings
        for ( String v : bindingsInScope ) {

            //LHS FactPattern
            FactPattern fp = model.getLHSBoundFact( v );
            if ( fp != null ) {
                if ( isLHSFactTypeEquivalent( v ) ) {
                    applicableBindingsInScope.add( v );
                }
            }

            //LHS FieldConstraint
            FieldConstraint fc = model.getLHSBoundField( v );
            if ( fc != null ) {
                if ( isLHSFieldTypeEquivalent( v ) ) {
                    applicableBindingsInScope.add( v );
                }
            }

        }

        return applicableBindingsInScope;
    }

    private boolean isLHSFactTypeEquivalent(String boundVariable) {
        String boundFactType = model.getLHSBoundFact( boundVariable ).getFactType();
        String boundFieldType = model.getLHSBindingType( boundVariable );

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
            if ( !this.fieldType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.sce.getEnumValues( boundFactType,
                                                  this.fieldName );
            return isEnumEquivalent( dd );
        }
        return isBoundVariableApplicable( boundFactType,
                                          boundFieldType );
    }

    private boolean isLHSFieldTypeEquivalent(String boundVariable) {
        String boundFieldType = this.model.getLHSBindingType( boundVariable );

        //If the fieldTypes are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFieldType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
            if ( !this.fieldType.equals( SuggestionCompletionEngine.TYPE_COMPARABLE ) ) {
                return false;
            }
            FieldConstraint fc = this.model.getLHSBoundField( boundVariable );
            if ( fc instanceof SingleFieldConstraint ) {
                String fieldName = ((SingleFieldConstraint) fc).getFieldName();
                String parentFactTypeForBinding = this.model.getLHSParentFactPatternForBinding( boundVariable ).getFactType();
                String[] dd = this.sce.getEnumValues( parentFactTypeForBinding,
                                                      fieldName );
                return isEnumEquivalent( dd );
            }
            return false;
        }

        return isBoundVariableApplicable( boundFieldType );
    }

    private boolean isEnumEquivalent(String[] values) {
        if ( values == null && this.dropDownData.fixedList != null ) {
            return false;
        }
        if ( values != null && this.dropDownData.fixedList == null ) {
            return false;
        }
        if ( values.length != this.dropDownData.fixedList.length ) {
            return false;
        }
        for ( int i = 0; i < values.length; i++ ) {
            if ( !values[i].equals( this.dropDownData.fixedList[i] ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean isBoundVariableApplicable(String boundFactType,
                                              String boundFieldType) {

        //Fields of the same type as the bound variable can be compared
        if ( boundFactType != null && boundFactType.equals( this.fieldType ) ) {
            return true;
        }

        //'this' can be compared to bound facts of the same type
        if ( this.fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) ) {
            if ( boundFactType != null && boundFactType.equals( this.pattern.getFactType() ) ) {
                return true;
            }
        }

        //For collection, present the list of possible bound variable
        String factCollectionType = sce.getParametricFieldType( pattern.getFactType(),
                                                                this.fieldName );
        if ( boundFactType != null && factCollectionType != null && boundFactType.equals( factCollectionType ) ) {
            return true;
        }

        return isBoundVariableApplicable( boundFieldType );
    }

    private boolean isBoundVariableApplicable(String boundFieldType) {

        //Field-types can be simply compared
        if ( boundFieldType != null && boundFieldType.equals( this.fieldType ) ) {
            return true;
        }

        //'this' can be compared to bound fields of the same type
        if ( this.fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) ) {
            if ( boundFieldType != null && boundFieldType.equals( this.pattern.getFactType() ) ) {
                return true;
            }
        }

        //'this' can be compared to bound events if using a CEP operator
        if ( this.fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) && sce.isFactTypeAnEvent( boundFieldType ) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( SuggestionCompletionEngine.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //'this' can be compared to bound Dates if using a CEP operator
        if ( this.fieldName.equals( SuggestionCompletionEngine.TYPE_THIS ) && boundFieldType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( SuggestionCompletionEngine.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //Dates can be compared to bound events if using a CEP operator
        if ( (this.fieldType.equals( SuggestionCompletionEngine.TYPE_DATE ) && sce.isFactTypeAnEvent( boundFieldType )) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( SuggestionCompletionEngine.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //For collection, present the list of possible bound variable
        String factCollectionType = sce.getParametricFieldType( pattern.getFactType(),
                                                                this.fieldName );
        if ( factCollectionType != null && factCollectionType.equals( boundFieldType ) ) {
            return true;
        }

        return false;
    }

    /**
     * Refresh the displayed drop-down
     */
    public void refreshDropDownData() {
        if ( this.dropDownData == null ) {
            return;
        }
        if ( !(this.constraintWidget instanceof EnumDropDownLabel) ) {
            return;
        }
        EnumDropDownLabel eddl = (EnumDropDownLabel) this.constraintWidget;
        eddl.refreshDropDownData();
    }

}
