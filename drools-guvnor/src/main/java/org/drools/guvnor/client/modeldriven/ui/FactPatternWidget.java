package org.drools.guvnor.client.modeldriven.ui;
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



import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is the new smart widget that works off the model.
 * @author Michael Neale
 *
 */
public class FactPatternWidget extends DirtyableComposite {

    private FactPattern                pattern;
    private DirtyableFlexTable         layout = new DirtyableFlexTable();
    private SuggestionCompletionEngine completions;
    private RuleModeller               modeller;
    private boolean                    bindable;

    public FactPatternWidget(RuleModeller mod, IPattern p, SuggestionCompletionEngine com, boolean canBind) {
        this.pattern = (FactPattern) p;
        this.completions = com;
        this.modeller = mod;
        this.bindable = canBind;

        layout.setWidget( 0, 0, getPatternLabel() );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        formatter.setStyleName( 0, 0, "modeller-fact-TypeHeader" );

        final DirtyableFlexTable inner = new DirtyableFlexTable();

        layout.setWidget( 1, 0, inner );

        for ( int row = 0; row < pattern.getFieldConstraints().length; row++ ) {

            FieldConstraint constraint = pattern.getFieldConstraints()[row];

            final int currentRow = row;


            renderFieldConstraint( inner, row, constraint, true );


            //now the clear icon
            Image clear = new ImageButton( "images/delete_item_small.gif" );
            clear.setTitle( "Remove this whole restriction" );
            clear.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    if (Window.confirm( "Remove this item?" )) {
                        pattern.removeConstraint( currentRow );
                        modeller.refreshWidget();
                    }
                }
            } );

            inner.setWidget( row, 5, clear );

        }
        if ( bindable ) layout.setStyleName( "modeller-fact-pattern-Widget" );
        initWidget( layout );

    }

    /**
     * This will render a field constraint into the given table.
     * The row is the row number to stick it into.
     */
    private void renderFieldConstraint(final DirtyableFlexTable inner, int row, FieldConstraint constraint, boolean showBinding) {
        //if nesting, or predicate, then it will need to span 5 cols.
        if (constraint instanceof SingleFieldConstraint) {
            renderSingleFieldConstraint( modeller, inner, row, constraint, showBinding );
        } else if (constraint instanceof CompositeFieldConstraint) {
            inner.setWidget( row, 0, compositeFieldConstraintEditor((CompositeFieldConstraint) constraint) );
            inner.getFlexCellFormatter().setColSpan( row, 0, 5 );
        }
    }

    /**
     * This will show the constraint editor - allowing field constraints to be nested etc.
     */
    private Widget compositeFieldConstraintEditor(final CompositeFieldConstraint constraint) {
        HorizontalPanel horiz = new HorizontalPanel();
        String desc = null;

        Image edit = new ImageButton( "images/add_field_to_fact.gif" );
        edit.setTitle( "Add a field to this nested constraint." );

        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showPatternPopupForComposite( w, constraint );
            }

        } );


        if (constraint.compositeJunctionType.equals(CompositeFieldConstraint.COMPOSITE_TYPE_AND)) {
            desc = "All of:";
        } else {
            desc = "Any of:";
        }

        //HorizontalPanel ab = new HorizontalPanel();
        //ab.setStyleName( "composite-fact-pattern" );
        horiz.add( edit );
        horiz.add( new SmallLabel(desc) );

        //horiz.add( ab );


        FieldConstraint[] nested = constraint.constraints;
        DirtyableFlexTable inner = new DirtyableFlexTable();
        inner.setStyleName( "modeller-inner-nested-Constraints" );
        if (nested != null) {
            for ( int i = 0; i < nested.length; i++ ) {
                this.renderFieldConstraint( inner, i, nested[i], false );
                //add in remove icon here...
                final int currentRow = i;
                Image clear = new ImageButton( "images/delete_item_small.gif" );
                clear.setTitle( "Remove this (nested) restriction" );

                clear.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        if (Window.confirm( "Remove this item from nested constraint?" )) {
                            constraint.removeConstraint( currentRow );
                            modeller.refreshWidget();
                        }
                    }
                } );
                inner.setWidget( i, 5, clear );
            }
        }

        horiz.add( inner );
        return horiz;
    }

    /**
     * Applies a single field constraint to the given table, and start row.
     */
    private void renderSingleFieldConstraint(final RuleModeller modeller, final DirtyableFlexTable inner, int row, FieldConstraint constraint, boolean showBinding) {
        final SingleFieldConstraint c = (SingleFieldConstraint) constraint;
        if ( c.constraintValueType != SingleFieldConstraint.TYPE_PREDICATE ) {
            inner.setWidget( row, 0, fieldLabel( c, showBinding ) );

            inner.setWidget( row, 1, operatorDropDown( c ) );
            inner.setWidget( row, 2, valueEditor( c, this.pattern.factType ) );
            inner.setWidget( row, 3, connectives( c, this.pattern.factType ) );

            Image addConnective = new ImageButton( "images/add_connective.gif" );
            addConnective.setTitle( "Add more options to this fields values." );
            addConnective.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    c.addNewConnective();
                    modeller.refreshWidget();
                }
            } );

            inner.setWidget( row, 4, addConnective );
        } else if (c.constraintValueType == SingleFieldConstraint.TYPE_PREDICATE) {
            inner.setWidget( row, 0, predicateEditor(c) );
            inner.getFlexCellFormatter().setColSpan( row, 0, 5 );
        }
    }

    /**
     * This provides an inline formula editor, not unlike a spreadsheet does.
     */
    private Widget predicateEditor(final SingleFieldConstraint c) {

        HorizontalPanel pred = new HorizontalPanel();
        pred.setWidth( "100%" );
        Image img = new Image("images/function_assets.gif");
        img.setTitle( "This is a formula expression that is evaluated to be true or false." );

        pred.add( img );
        if (c.value == null) {
        	c.value = "";
        }
        final TextBox box = new TextBox();
        box.setText( c.value );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.value = box.getText();
                modeller.makeDirty();
            }
        });

        box.setWidth( "100%" );
        pred.add( box );
        return pred;
    }

    /**
     * This returns the pattern label.
     */
    private Widget getPatternLabel() {
        HorizontalPanel horiz = new HorizontalPanel();

        Image edit = new ImageButton( "images/add_field_to_fact.gif" );
        edit.setTitle( "Add a field to this condition, or bind a varible to this fact." );

        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showPatternPopup( w );
            }
        } );

        if ( pattern.boundName != null ) {
            horiz.add( new SmallLabel( "[" + pattern.boundName + "] " + pattern.factType  ) );
        } else {
            horiz.add( new SmallLabel( pattern.factType ) );
        }
        horiz.add( edit );

        return horiz;

    }

    /**
     * This shows a popup for adding fields to a composite
     */
    private void showPatternPopupForComposite(Widget w, final CompositeFieldConstraint composite) {
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                         "Add fields to this constraint" );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( this.pattern.factType );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }

        box.setSelectedIndex( 0 );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                composite.addConstraint( new SingleFieldConstraint( box.getItemText( box.getSelectedIndex() ) ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Add a restriction on a field", box );


        final ListBox composites = new ListBox();
        composites.addItem("...");
        composites.addItem( "All of (And)", CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( "Any of (Or)", CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                composite.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup("Multiple field constraints", "You can specify constraints that span multiple fields (and more). The results of all these constraints can be combined with a 'and' or an 'or' logically." +
                "You can also have other multiple field constraints nested inside these restrictions.");

        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( composites );
        horiz.add( infoComp );
        popup.addAttribute( "Multiple field constraint", horiz );

        popup.show();

    }

    /**
     * This shows a popup allowing you to add field constraints to a pattern (its a popup).
     */
    private void showPatternPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                         "Modify constraints for " + pattern.factType );

        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( this.pattern.factType );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }

        box.setSelectedIndex( 0 );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                pattern.addConstraint( new SingleFieldConstraint( box.getItemText( box.getSelectedIndex() ) ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Add a restriction on a field", box );


        final ListBox composites = new ListBox();
        composites.addItem("...");
        composites.addItem( "All of (And)", CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        composites.addItem( "Any of (Or)", CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        composites.setSelectedIndex( 0 );

        composites.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                CompositeFieldConstraint comp = new CompositeFieldConstraint();
                comp.compositeJunctionType = composites.getValue( composites.getSelectedIndex() );
                pattern.addConstraint( comp );
                modeller.refreshWidget();
                popup.hide();
            }
        });

        InfoPopup infoComp = new InfoPopup("Multiple field constraints", "You can specify constraints that span multiple fields (and more). The results of all these constraints can be combined with a 'and' or an 'or' logically." +
                "You can also have other multiple field constraints nested inside these restrictions.");

        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( composites );
        horiz.add( infoComp );
        popup.addAttribute( "Multiple field constraint", horiz );


        //popup.addRow( new HTML("<hr/>") );

        popup.addRow( new SmallLabel("<i>Advanced options:</i>") );
        final Button predicate = new Button( "New formula" );
        predicate.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                SingleFieldConstraint con = new SingleFieldConstraint();
                con.constraintValueType = SingleFieldConstraint.TYPE_PREDICATE;
                pattern.addConstraint( con );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Add a new formula style expression", predicate );

        doBindingEditor( popup );

        popup.show();
    }

    /**
     * This adds in (optionally) the editor for changing the bound variable name.
     * If its a bindable pattern, it will show the editor,
     * if it is already bound, and the name is used, it should
     * not be editable.
     */
    private void doBindingEditor(final FormStylePopup popup) {
        if ( bindable && !(modeller.getModel().isBoundFactUsed( pattern.boundName )) ) {
            HorizontalPanel varName = new HorizontalPanel();
            final TextBox varTxt = new TextBox();
            if (pattern.boundName == null) {
            	varTxt.setText("");
            }else {
                varTxt.setText( pattern.boundName );
            }

            varTxt.setVisibleLength( 6 );
            varName.add( varTxt );

            Button bindVar = new Button( "Set" );
            bindVar.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    String var = varTxt.getText();
                    if (modeller.isVariableNameUsed( var )) {
                        Window.alert( "The variable name [" + var + "] is already taken." );
                        return;
                    }
                    pattern.boundName = varTxt.getText();
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );

            varName.add( bindVar );
            popup.addAttribute( "Variable name", varName );

        }
    }

    private Widget connectives(SingleFieldConstraint c, String factClass) {
        if ( c.connectives != null && c.connectives.length > 0 ) {
            DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
            for ( int i = 0; i < c.connectives.length; i++ ) {
                ConnectiveConstraint con = c.connectives[i];
                horiz.add( connectiveOperatorDropDown( con, c.fieldName ) );
                horiz.add( connectiveValueEditor( con, factClass, c.fieldName ) );
            }
            return horiz;
        } else {
            //nothing to do
            return null;
        }

    }

    private Widget connectiveValueEditor(final ISingleFieldConstraint con, String factClass, String fieldName) {
        String typeNumeric = this.modeller.getSuggestionCompletions().getFieldType( factClass, fieldName );
        return new ConstraintValueEditor(pattern, fieldName, con, this.modeller, typeNumeric);
    }

    private Widget connectiveOperatorDropDown(final ConnectiveConstraint con, String fieldName) {
        String[] ops = completions.getConnectiveOperatorCompletions( pattern.factType, fieldName );
        final ListBox box = new ListBox();
        box.addItem( "--- please choose ---" );
        for ( int i = 0; i < ops.length; i++ ) {
            String op = ops[i];
            box.addItem( HumanReadable.getOperatorDisplayName( op ), op );
            if ( op.equals( con.operator ) ) {
                box.setSelectedIndex( i + 1 );
            }

        }

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                con.operator = box.getValue( box.getSelectedIndex() );
            }
        } );

        return box;
    }

    private Widget valueEditor(final SingleFieldConstraint c, String factType) {
        String type = this.modeller.getSuggestionCompletions().getFieldType( factType, c.fieldName );
        return  new ConstraintValueEditor(pattern, c.fieldName, c, this.modeller,  type);
    }

    private Widget operatorDropDown(final SingleFieldConstraint c) {
        String[] ops = completions.getOperatorCompletions( pattern.factType, c.fieldName );
        final ListBox box = new ListBox();
        box.addItem( "--- please choose ---" );
        for ( int i = 0; i < ops.length; i++ ) {
            String op = ops[i];
            box.addItem( HumanReadable.getOperatorDisplayName( op ), op );
            if ( op.equals( c.operator ) ) {
                box.setSelectedIndex( i + 1 );
            }

        }

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.operator = box.getValue( box.getSelectedIndex() );
                modeller.makeDirty();
                System.out.println( "Set operator to :" + c.operator );
            }
        } );

        return box;
    }

    /**
     * get the field widget. This may be a simple label, or it may
     * be bound (and show the var name) or a icon to create a binding.
     * It will only show the binding option of showBinding is true.
     */
    private Widget fieldLabel(final SingleFieldConstraint con, boolean showBinding) {//, final Command onChange) {
        HorizontalPanel ab = new HorizontalPanel();
        ab.setStyleName( "modeller-field-Label" );
        if (!con.isBound()) {
            if (bindable && showBinding) {
                Image bind = new ImageButton( "images/add_field_to_fact.gif", "Give this field a variable name that can be used elsewhere." );
                bind.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        showBindFieldPopup(w, con);
                    }
                });
                ab.add( bind );
            }
        } else {
            ab.add( new SmallLabel("[" + con.fieldBinding + "]") );
        }

        ab.add(new SmallLabel( con.fieldName ));
        return ab;
    }

    /**
     * Display a little editor for field bindings.
     */
    private void showBindFieldPopup(final Widget w, final SingleFieldConstraint con) {
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                         "Bind the field called [" + con.fieldName + "] to a variable." );
        final AbsolutePanel vn = new AbsolutePanel();
        final TextBox varName = new TextBox();
        final Button ok = new Button("Set");
        vn.add( varName );
        vn.add( ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                String var = varName.getText();
                if (modeller.isVariableNameUsed( var )) {
                    Window.alert( "The variable name [" + var + "] is already taken.");
                    return;
                }
                con.fieldBinding = var;
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( "Variable name", vn );

        popup.show();
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }


}