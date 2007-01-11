package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is for setting fields on a bound fact or global variable.
 * 
 * @author Michael Neale
 */
public class ActionSetFieldWidget extends Composite {

    final private ActionSetField model;
    final private SuggestionCompletionEngine completions;
    final private FlexTable layout;
    private boolean isBoundFact = false;
    
    final private String[] fieldCompletions;
    private RuleModeller modeller;
    
    
    public ActionSetFieldWidget(RuleModeller mod, RuleModel rule, ActionSetField set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new FlexTable();
        this.modeller = mod;
        layout.setStyleName( "model-builderInner-Background" );
        if (completions.isGlobalVariable( set.variable )) {
            this.fieldCompletions = completions.getFieldCompletionsForGlobalVariable( set.variable );            
        } else {
            FactPattern pattern = rule.getBoundFact( set.variable );
            this.fieldCompletions = completions.getFieldCompletions( pattern.factType );
            this.isBoundFact = true;
        }
        
        doLayout();
        
        initWidget( this.layout );
    }


    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getSetterLabel() );
        
        FlexTable inner = new FlexTable();
        

        
        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];
            
            inner.setWidget( i, 0, fieldSelector(val) );
            inner.setWidget( i, 1, valueEditor(val) );
            final int idx = i;
            Image remove = new Image("images/delete_item_small.gif");
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    YesNoDialog diag = new YesNoDialog("Remove this item?", new Command() {
                        public void execute() {
                            model.removeField( idx );
                            modeller.refreshWidget();
                        }                        
                    });
                    diag.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                    diag.show();
                }                
            });
            inner.setWidget( i, 2, remove );
        }
        
        layout.setWidget( 0, 1, inner );
        
        
    }


    private Widget getSetterLabel() {
        
        HorizontalPanel horiz = new HorizontalPanel();
        
        
        Image edit = new Image("images/add_field_to_fact.gif");
        edit.setTitle( "Add another field to this so you can set its value." );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showAddFieldPopup(w);
            }
        } );
        
        
        horiz.add( new Label(HumanReadable.getActionDisplayName("set") + " [" + model.variable + "]") );
        horiz.add( edit );
        
        return horiz;
    }


    protected void showAddFieldPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", "Add a field");
        popup.setStyleName( "ks-popups-Popup" );
        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[i] );
        }
        
        box.setSelectedIndex( 0 );
        
        popup.addAttribute( "Add field", box );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                model.addFieldValue( new ActionFieldValue(box.getItemText( box.getSelectedIndex() ), "") );
                modeller.refreshWidget();
                popup.hide();
            }
        });
        

        
        popup.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        popup.show();
 
    }


    private Widget valueEditor(final ActionFieldValue val) {
        final TextBox box = new TextBox();
        box.setText( val.value );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                val.value = box.getText();
            }            
        });
        return box;
    }


    private Widget fieldSelector(final ActionFieldValue val) {        
        return new Label(val.field);        
    }
    
    /**
     * This returns true if the values being set are on a fact.
     */
    public boolean isBoundFact() {
        return isBoundFact;
    }
    
}
