package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.FieldEditListener;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IPattern;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is the new smart widget that works off the model.
 * @author Michael Neale
 *
 */
public class FactPatternWidget extends Composite {

    private FactPattern pattern;
    private FlexTable layout = new FlexTable();
    private SuggestionCompletionEngine completions;
    private RuleModeller modeller;
    private boolean bindable;
    
    
    public FactPatternWidget(RuleModeller mod, IPattern p, SuggestionCompletionEngine com, boolean canBind) {
        this.pattern = (FactPattern) p;
        this.completions = com;
        this.modeller = mod;
        this.bindable = canBind;
        layout.setWidget( 0, 0, getPatternLabel() );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        formatter.setStyleName( 0, 0, "modeller-fact-TypeHeader" );
        
        final FlexTable inner = new FlexTable();
        
        layout.setWidget( 1, 0, inner );
        
        for ( int row = 0; row < pattern.constraints.length; row++ ) {
            final Constraint c = pattern.constraints[row];
            final int currentRow = row;

            inner.setWidget( row, 0, fieldLabel(c));/*, new Command() {
                public void execute() {
                    inner.setWidget( currentRow, 1, operatorDropDown( c ));
                }                
            }));
            */
            
            inner.setWidget( row, 1, operatorDropDown(c) );
            inner.setWidget( row, 2, valueEditor(c) );            
            inner.setWidget( row, 3, connectives(c) );
            
            Image clear = new Image("images/delete_item_small.gif");
            clear.setTitle( "Remove this field constraint" );
            
            clear.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    YesNoDialog d = new YesNoDialog("Remove this item?", new Command() {
                        public void execute() {
                            pattern.removeConstraint( currentRow );
                            modeller.refreshWidget();
                        }                        
                    });
                    d.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop());
                    d.show();
                }
            } );

            Image addConnective = new Image("images/add_connective.gif");
            addConnective.setTitle( "Add more options to this fields values." );            
            addConnective.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    c.addNewConnective();
                    modeller.refreshWidget();
                }                
            });

            inner.setWidget( row, 4, addConnective );
            inner.setWidget( row, 5, clear );

        }
        if (bindable) {
            //layout.setStyleName( "model-builderInner-Background" );
            layout.setStyleName( "modeller-fact-pattern-Widget" );
        } else {
            //layout.setStyleName( "model-builderInnerInner-Background" );
        }
        
        initWidget( layout );
        
    }


    /**
     * This returns the pattern label.
     */
    private Widget getPatternLabel() {
        HorizontalPanel horiz = new HorizontalPanel();
        
        Image edit = new Image("images/add_field_to_fact.gif");
        edit.setTitle( "Add a field to this condition, or bind a varible to this fact." );
        
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showPatternPopup(w);
            }
        } );
        
        if (pattern.boundName != null) {
            horiz.add( new Label(pattern.factType + " [" + pattern.boundName + "]" ));
        } else {
            horiz.add( new Label(pattern.factType));
        }
        horiz.add( edit );
        
        return horiz;
        
    }


    protected void showPatternPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", "Modify constraints for " + pattern.factType);
        popup.setStyleName( "ks-popups-Popup" );
        final ListBox box = new ListBox();
        box.addItem( "..." );
        String[] fields = this.completions.getFieldCompletions( this.pattern.factType );
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }
        
        box.setSelectedIndex( 0 );
        
        popup.addAttribute( "Add field", box );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                pattern.addConstraint( new Constraint(box.getItemText( box.getSelectedIndex() )) );
                modeller.refreshWidget();
                popup.hide();
            }
        });
        
        doBindingEditor( popup );
        
        popup.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        popup.show();
    }


    /**
     * This adds in (optionally) the editor for changing the bound variable name.
     * If its a bindable pattern, it will show the editor,
     * if it is already bound, and the name is used, it should 
     * not be editable.
     */
    private void doBindingEditor(final FormStylePopup popup) {
        if (bindable && !(modeller.getModel().isBoundFactUsed( pattern.boundName ))) {
            HorizontalPanel varName = new HorizontalPanel();
            final TextBox varTxt = new TextBox();
            varTxt.setText( pattern.boundName );
            varTxt.setVisibleLength( 3 );
            varName.add( varTxt );
            Button bindVar = new Button("Set");
            bindVar.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    pattern.boundName = varTxt.getText();
                    modeller.refreshWidget();
                    popup.hide();
                }
            } );
            
            varName.add( bindVar );
            popup.addAttribute("Variable name", varName);
            
        }
    }


    private Widget connectives(Constraint c) {
        if (c.connectives != null && c.connectives.length > 0) {
            HorizontalPanel horiz = new HorizontalPanel();
            for ( int i = 0; i < c.connectives.length; i++ ) {
                ConnectiveConstraint con = c.connectives[i];
                horiz.add( connectiveOperatorDropDown(con, c.fieldName) );
                horiz.add( connectiveValueEditor(con) );
            }        
            return horiz;
        } else {
            //nothing to do
            return null;
        }
        
    }


    private Widget connectiveValueEditor(final ConnectiveConstraint con) {
        
        final TextBox box = new TextBox();
        box.setVisibleLength( 4 );
        box.setText( con.value );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                con.value = box.getText();                
            }            
        });
        return box;
    }


    private Widget connectiveOperatorDropDown(final ConnectiveConstraint con, String fieldName) {
        String[] ops = completions.getConnectiveOperatorCompletions( pattern.factType, fieldName );
        final ListBox box = new ListBox();
        box.addItem( "--- please choose ---" );
        for ( int i = 0; i < ops.length; i++ ) {
            String op = ops[i];
            box.addItem(HumanReadable.getOperatorDisplayName( op ), op );
            if (op.equals( con.operator )) {
                box.setSelectedIndex( i + 1 );
            }

        }
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                con.operator = box.getValue( box.getSelectedIndex() );                
            }            
        });
        
        return box;
    }


    private Widget valueEditor(final Constraint c) {
        final TextBox box = new TextBox();
        box.setText( c.value );
        if (c.value == null || c.value.length() < 5) {
            box.setVisibleLength( 3 );
        } else {
            box.setVisibleLength( c.value.length() - 1 );
        }
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.value = box.getText();                
            }
            
        });
        
        box.addKeyboardListener( new FieldEditListener(new Command() {
            public void execute() {
                box.setVisibleLength( box.getText().length() );
            }            
        }));
        
        return box;
    }


    private Widget operatorDropDown(final Constraint c) {
        String[] ops = completions.getOperatorCompletions( pattern.factType, c.fieldName );
        final ListBox box = new ListBox();
        box.addItem( "--- please choose ---" );
        for ( int i = 0; i < ops.length; i++ ) {
            String op = ops[i];
            box.addItem( HumanReadable.getOperatorDisplayName( op ) , op );
            if (op.equals( c.operator )) {
                box.setSelectedIndex( i + 1 );
            }

        }
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.operator = box.getValue( box.getSelectedIndex() );
                System.out.println("Set operator to :" + c.operator);
            }            
        });
        
        
        return box;
    }


    private Widget fieldLabel(final Constraint con) {//, final Command onChange) {
        return new Label(con.fieldName);
    }
    
}
