package org.drools.brms.client.modeldriven.ui;

import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionAssertFact;
import org.drools.brms.client.modeldriven.model.ActionRetractFact;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IAction;
import org.drools.brms.client.modeldriven.model.IPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the parent widget that contains the model based rule builder.
 * 
 * @author Michael Neale
 *
 */
public class RuleModeller extends Composite {
 
    private FlexTable layout;
    private SuggestionCompletionEngine completions;
    private RuleModel model;
    
    public RuleModeller(SuggestionCompletionEngine com, RuleModel model) {
        this.model = model;
        this.completions = com;
        
        layout = new FlexTable();
        
        refreshWidget();
        
        layout.setStyleName( "model-builder-Background" );
        initWidget( layout );        
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void refreshWidget() {
        layout.clear();
        
        Image addPattern = new Image( "images/new_item.gif" );
        addPattern.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showPatternSelector(w);               
            }            
        });
        
        layout.setWidget( 0, 0, new Label("IF") );
        layout.setWidget( 0, 2, addPattern );
        
        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.setWidget( 2, 0, new Label("THEN") );
        
        Image addAction = new Image("images/new_item.gif");
        addAction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showActionSelector(w);
            }            
        });
        layout.setWidget( 2, 2, addAction );
        
        layout.setWidget( 3, 1, renderRhs(this.model) );
    }


    /**
     * Do the widgets for the RHS.
     */
    private Widget renderRhs(final RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
        
        for ( int i = 0; i < model.rhs.length; i++ ) {
            IAction action = model.rhs[i];
            
            Widget w = null;            
            if (action instanceof ActionSetField) {                
                w =  new ActionSetFieldWidget(this, this.model, (ActionSetField) action, completions ) ; 
            } else if (action instanceof ActionAssertFact) {
                w = new ActionAssertFactWidget(this, (ActionAssertFact) action, completions );
            } else if (action instanceof ActionRetractFact) {
                w = new ActionRetractFactWidget((ActionRetractFact) action);
            }
            
            HorizontalPanel horiz = new HorizontalPanel();
            
            Image remove = new Image("images/delete_item_small.gif");
            remove.setTitle( "Remove this action." );
            final int idx = i;
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    YesNoDialog diag = new YesNoDialog("Remove this item?", new Command() {
                        public void execute() {
                            model.removeRhsItem(idx);
                            refreshWidget();                            
                        }
                    });
                    diag.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                    diag.show();
                }
            } );
            horiz.add( w );
            horiz.add( remove );
            
            vert.add( horiz );
            
        }
        
        return vert;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showPatternSelector(final Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "New fact pattern...");

        

        //
        // The list of facts 
        //
        String[] facts = completions.getFactTypes();
        final ListBox factTypeBox = new ListBox();
        factTypeBox.addItem( "Choose fact type...", "IGNORE" );  
        for ( int i = 0; i < facts.length; i++ ) {
            factTypeBox.addItem( facts[i] );
        }
        factTypeBox.setSelectedIndex( 0 );
        popup.addAttribute( "Fact", factTypeBox );
        factTypeBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String s = factTypeBox.getItemText( factTypeBox.getSelectedIndex() );
                if (!s.equals( "IGNORE" )) {
                    addNewFact(s);
                    popup.hide();
                }
            }
        });
        popup.setStyleName( "ks-popups-Popup" );
        
        //
        // The list of top level CEs
        //
        String ces[]  = completions.getListOfCEs();
        final ListBox ceBox = new ListBox();
        ceBox.addItem( "Choose condition type...", "IGNORE" ); 
        for ( int i = 0; i < ces.length; i++ ) {
            ceBox.addItem( ces[i] );
        }
        ceBox.setSelectedIndex( 0 );
        
        popup.addAttribute( "Condition type", ceBox );
        ceBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String s = ceBox.getItemText( ceBox.getSelectedIndex() );
                if (!s.equals( "IGNORE" )) {
                    addNewCE(s);
                    popup.hide();
                }
            }
        });        
        
        popup.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() );
        popup.show();

    }
    
    protected void showActionSelector(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "Add a new action...");
        
        popup.setStyleName( "ks-popups-Popup" );
        
        List vars = model.getBoundFacts();
        
        final ListBox varBox = new ListBox();
        final ListBox retractBox = new ListBox();
        varBox.addItem( "Choose ..." );        
        retractBox.addItem( "Choose..." );
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            String v = (String) iter.next();
            varBox.addItem( v );
            retractBox.addItem( v );
        }
        String[] globals = this.completions.getGlobalVariables();
        for ( int i = 0; i < globals.length; i++ ) {
            varBox.addItem( globals[i] );
        }
        
        varBox.setSelectedIndex( 0 );
        varBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addModifyVar(varBox.getItemText( varBox.getSelectedIndex() ));
                popup.hide();
            }
        });
        
        retractBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addRetract(retractBox.getItemText( retractBox.getItemCount() ));
                popup.hide();
            }            
        });
        
        popup.addAttribute( "Modify a field on", varBox );
        popup.addAttribute( "Retract a fact", retractBox );

        popup.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() );
        popup.show();
    }
    


    protected void addRetract(String var) {
        this.model.addRhsItem( new ActionRetractFact(var) );
        refreshWidget();        
    }

    protected void addModifyVar(String itemText) {        
        this.model.addRhsItem(new ActionSetField(itemText));
        refreshWidget();        
    }

    protected void addNewCE(String s) {
        this.model.addLhsItem( new CompositeFactPattern(s) );
        refreshWidget();        
    }

    /**
     * Adds a fact to the model, and then refreshes the display.
     */
    protected void addNewFact(String itemText) {
        this.model.addLhsItem( new FactPattern(itemText) );
        refreshWidget();
    }

    private Widget renderLhs(final RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
        
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            Widget w;
            if (pattern instanceof FactPattern) {                  
                w = new FactPatternWidget(this, pattern, completions, true) ;
            } else if (pattern instanceof CompositeFactPattern) {
                w = new CompositeFactPatternWidget(this, (CompositeFactPattern) pattern, completions) ;
            } else {
                throw new RuntimeException("I don't know what type of pattern that is.");
            }
            
            HorizontalPanel horiz = new HorizontalPanel();
            
            Image remove = new Image("images/delete_item_small.gif");
            remove.setTitle( "Remove this condition, and all the field constraints that belong to it." );
            final int idx = i;
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    YesNoDialog diag = new YesNoDialog("Remove this item?", new Command() {
                        public void execute() {
                            if (model.removeLhsItem(idx)) {
                                refreshWidget();
                            } else {
                                ErrorPopup.showMessage( "Can't remove that item as it is used in the action part of the rule." );
                            }                            
                        }                        
                    });
                    diag.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                    diag.show();
                }
            } );
            horiz.add( w );
            horiz.add( remove );


            vert.add( horiz );
        }
        
        return vert;
    }


    
    public RuleModel getModel() {
        return model;
    }
    
    
}
