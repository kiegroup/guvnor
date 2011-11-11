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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.factPattern;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.HasConstraints;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PopupCreator {

    private Constants                  constants = ((Constants) GWT.create( Constants.class ));
    private static Images              images    = (Images) GWT.create( Images.class );

    private FactPattern                pattern;
    private SuggestionCompletionEngine completions;
    private RuleModeller               modeller;
    private boolean                    bindable;

    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern
     *            the pattern to set
     */
    public void setPattern(FactPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the completions.
     */
    public SuggestionCompletionEngine getCompletions() {
        return completions;
    }

    /**
     * @param completions
     *            the completions to set
     */
    public void setCompletions(SuggestionCompletionEngine completions) {
        this.completions = completions;
    }

    /**
     * Returns the modeller.
     */
    public RuleModeller getModeller() {
        return modeller;
    }

    /**
     * @param modeller
     *            the modeller to set
     */
    public void setModeller(RuleModeller modeller) {
        this.modeller = modeller;
    }

    /**
     * Returns the bindable.
     */
    public boolean isBindable() {
        return bindable;
    }

    /**
     * @param bindable
     *            the bindable to set
     */
    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    /**
     * Display a little editor for field bindings.
     */
    public void showBindFieldPopup(final Widget w,
                                   final SingleFieldConstraint con,
                                   final HasConstraints hasConstraints,
                                   String[] fields,
                                   final PopupCreator popupCreator) {
        final FormStylePopup popup = new FormStylePopup();
        popup.setWidth( 500 + "px" );
        final HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new BindingTextBox();
        if ( con.getFieldBinding() != null ) {
            varName.setText( con.getFieldBinding() );
        }
        final Button ok = new Button( constants.Set() );
        vn.add( varName );
        vn.add( ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                String var = varName.getText();
                if ( modeller.isVariableNameUsed( var ) ) {
                    Window.alert( constants.TheVariableName0IsAlreadyTaken( var ) );
                    return;
                }
                con.setFieldBinding( var );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( constants.BindTheFieldCalled0ToAVariable( con.getFieldName() ),
                            vn );
        if ( fields != null ) {
            Button sub = new Button( constants.ShowSubFields() );
            popup.addAttribute( constants.ApplyAConstraintToASubFieldOf0( con.getFieldName() ),
                                sub );
            sub.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    popup.hide();
                    popupCreator.showPatternPopup( w,
                                                   con.getFieldType(),
                                                   con,
                                                   hasConstraints,
                                                   true );
                }
            } );
        }

        popup.show();
    }

    /**
     * This shows a popup for adding fields to a composite
     */
    public void showPatternPopupForComposite(Widget w,
                                             final HasConstraints hasConstraints) {
        final FormStylePopup popup = new FormStylePopup( images.newexWiz(),
                                                         constants.AddFieldsToThisConstraint() );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( this.pattern.getFactType() );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }

        box.setSelectedIndex( 0 );

        box.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                String fieldType = getCompletions().getFieldType( pattern.getFactType(),
                                                                  fieldName );
                hasConstraints.addConstraint( new SingleFieldConstraint( fieldName,
                                                                         fieldType,
                                                                         null ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( constants.AddARestrictionOnAField(),
                            box );

        final ListBox composites = new ListBox();
        composites.addItem( "..." ); //NON-NLS
        composites.addItem( constants.AllOfAnd(),
                            CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( constants.AnyOfOr(),
                            CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                hasConstraints.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        } );

        InfoPopup infoComp = new InfoPopup( constants.MultipleFieldConstraints(),
                                            constants.MultipleConstraintsTip() );

        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( composites );
        horiz.add( infoComp );
        popup.addAttribute( constants.MultipleFieldConstraint(),
                            horiz );

        //Include Expression Editor
        popup.addRow( new SmallLabel( "<i>" + constants.AdvancedOptionsColon() + "</i>" ) );
        Button ebBtn = new Button( constants.ExpressionEditor() );

        ebBtn.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
                con.setConstraintValueType( SingleFieldConstraint.TYPE_UNDEFINED );
                con.setExpressionLeftSide( new ExpressionFormLine( new ExpressionUnboundFact( pattern ) ) );
                hasConstraints.addConstraint( con );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( constants.ExpressionEditor(),
                            ebBtn );

        popup.show();

    }

    /**
     * This shows a popup allowing you to add field constraints to a pattern
     * (its a popup).
     */
    public void showPatternPopup(Widget w,
                                 final String factType,
                                 final FieldConstraint con,
                                 final HasConstraints hasConstraints,
                                 final boolean isNested) {

        String title = (con == null) ? constants.ModifyConstraintsFor0( factType ) : constants.AddSubFieldConstraint();
        final FormStylePopup popup = new FormStylePopup( images.newexWiz(),
                                                         title );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                                factType );
        for ( int i = 0; i < fields.length; i++ ) {
            //You can't use "this" in a nested accessor
            if ( !isNested || !fields[i].equals( SuggestionCompletionEngine.TYPE_THIS ) ) {
                box.addItem( fields[i] );
            }
        }

        box.setSelectedIndex( 0 );

        box.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                if ( "...".equals( fieldName ) ) {
                    return;
                }
                String qualifiedName = factType + "." + fieldName;
                String fieldType = completions.getFieldType( qualifiedName );
                hasConstraints.addConstraint( new SingleFieldConstraint( qualifiedName,
                                                                         fieldType,
                                                                         con ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( constants.AddARestrictionOnAField(),
                            box );

        final ListBox composites = new ListBox();
        composites.addItem( "..." );
        composites.addItem( constants.AllOfAnd(),
                            CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( constants.AnyOfOr(),
                            CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                hasConstraints.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        } );

        InfoPopup infoComp = new InfoPopup( constants.MultipleFieldConstraints(),
                                            constants.MultipleConstraintsTip1() );

        HorizontalPanel horiz = new HorizontalPanel();

        horiz.add( composites );
        horiz.add( infoComp );
        if ( con == null ) {
            popup.addAttribute( constants.MultipleFieldConstraint(),
                                horiz );
        }

        if ( con == null ) {
            popup.addRow( new SmallLabel( "<i>" + constants.AdvancedOptionsColon() + "</i>" ) ); //NON-NLS
            Button predicate = new Button( constants.NewFormula() );
            predicate.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    SingleFieldConstraint con = new SingleFieldConstraint();
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_PREDICATE );
                    hasConstraints.addConstraint( con );
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );
            popup.addAttribute( constants.AddANewFormulaStyleExpression(),
                                predicate );

            Button ebBtn = new Button( constants.ExpressionEditor() );

            ebBtn.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_UNDEFINED );
                    hasConstraints.addConstraint( con );
                    con.setExpressionLeftSide( new ExpressionFormLine( new ExpressionUnboundFact( pattern ) ) );
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );
            popup.addAttribute( constants.ExpressionEditor(),
                                ebBtn );

            doBindingEditor( popup );
        }

        popup.show();
    }

    /**
     * This adds in (optionally) the editor for changing the bound variable
     * name. If its a bindable pattern, it will show the editor, if it is
     * already bound, and the name is used, it should not be editable.
     */
    private void doBindingEditor(final FormStylePopup popup) {
        if ( bindable || !(modeller.getModel().isBoundFactUsed( pattern.getBoundName() )) ) {
            HorizontalPanel varName = new HorizontalPanel();
            final TextBox varTxt = new BindingTextBox();
            if ( pattern.getBoundName() == null ) {
                varTxt.setText( "" );
            } else {
                varTxt.setText( pattern.getBoundName() );
            }

            varTxt.setVisibleLength( 6 );
            varName.add( varTxt );

            Button bindVar = new Button( constants.Set() );
            bindVar.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    String var = varTxt.getText();
                    if ( modeller.isVariableNameUsed( var ) ) {
                        Window.alert( constants.TheVariableName0IsAlreadyTaken( var ) );
                        return;
                    }
                    pattern.setBoundName( varTxt.getText() );
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );

            varName.add( bindVar );
            popup.addAttribute( constants.VariableName(),
                                varName );

        }
    }
}
