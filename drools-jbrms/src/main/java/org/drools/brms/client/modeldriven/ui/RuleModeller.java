package org.drools.brms.client.modeldriven.ui;

import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionAssertFact;
import org.drools.brms.client.modeldriven.brxml.ActionAssertLogicalFact;
import org.drools.brms.client.modeldriven.brxml.ActionModifyField;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IAction;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
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
    
    public RuleModeller(RuleAsset asset) {
        this.model = (RuleModel) asset.content;
        this.completions = SuggestionCompletionCache.getInstance().getEngineFromCache( asset.metaData.packageName );
        
        layout = new FlexTable();
        
        refreshWidget();
        
        layout.setStyleName( "model-builder-Background" );
        initWidget( layout );  
        setWidth( "100%" );
        setHeight( "100%" );
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void refreshWidget() {
        layout.clear();
        
        Image addPattern = new ImageButton( "images/new_item.gif" );
        addPattern.setTitle( "Add a condition to this rule." );
        addPattern.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showConditionSelector(w);               
            }            
        });
        
        layout.setWidget( 0, 0, new Label("IF") );
        layout.setWidget( 0, 2, addPattern );
        
        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.setWidget( 2, 0, new Label("THEN") );
        
        Image addAction = new ImageButton("images/new_item.gif");
        addAction.setTitle( "Add an action to this rule." );
        addAction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showActionSelector(w);
            }            
        });
        layout.setWidget( 2, 2, addAction );
        
        layout.setWidget( 3, 1, renderRhs(this.model) );
        
        layout.setWidget( 4, 0, new Label("(options)") );
        layout.setWidget( 4, 2, getAddAttribute() );
        layout.setWidget( 5, 1, new RuleAttributeWidget(this, this.model) );
        
    }


    private Widget getAddAttribute() {
        Image add = new ImageButton("images/new_item.gif");
        add.setTitle( "Add an option to the rule, to modify its behavior when evaluated or executed." );
        
        add.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showAttributeSelector(w);
            }            
        });
        return add;
    }

    protected void showAttributeSelector(Widget w) {
        final FormStylePopup pop = new FormStylePopup("images/config.png", "Add an option to the rule");
        final ListBox list = new ListBox();
        list.addItem( "Choose..." );
        
        list.addItem( "salience" );
        list.addItem( "no-loop" );
        list.addItem( "agenda-group" );
        list.addItem( "activation-group" );
        list.addItem( "duration" );
        list.addItem( "auto-focus" ); 
        list.addItem( "date-effective" );
        list.addItem( "date-expires" );
        list.addItem( "enabled" );
        
        list.setSelectedIndex( 0 );
        
        list.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
              model.addAttribute( new RuleAttribute(list.getItemText( list.getSelectedIndex() ), "") );
              refreshWidget();
              pop.hide();
            }            
        });

        pop.setStyleName( "ks-popups-Popup" );
        
        pop.addAttribute( "Attribute", list );
        pop.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() );
        pop.show();
    }

    /**
     * Do all the widgets for the RHS.
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
                w = new ActionRetractFactWidget(this.completions, (ActionRetractFact) action );
            } else if (action instanceof DSLSentence) {
                w = new DSLSentenceWidget((DSLSentence) action);
                w.setStyleName( "model-builderInner-Background" );
            }
            
            //w.setWidth( "100%" );            
            vert.add( spacerWidget() );            
            //vert.setWidth( "100%" );
            
            HorizontalPanel horiz = new HorizontalPanel();
            
            Image remove = new ImageButton("images/delete_item_small.gif");
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
            if (!(w instanceof ActionRetractFactWidget)) {
                w.setWidth( "100%" );
                horiz.setWidth( "100%" );
            }
            
            horiz.add( remove );
            vert.add( horiz );
            
        }
        
        return vert;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showConditionSelector(final Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "Add a condition to the rule...");

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
        if (facts.length > 0) popup.addAttribute( "Fact", factTypeBox );
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
        String ces[]  = HumanReadable.CONDITIONAL_ELEMENTS;
        final ListBox ceBox = new ListBox();
        ceBox.addItem( "Choose condition type...", "IGNORE" ); 
        for ( int i = 0; i < ces.length; i++ ) {
            String ce = ces[i];
            ceBox.addItem( HumanReadable.getCEDisplayName( ce ), ce );
        }
        ceBox.setSelectedIndex( 0 );
        
        if (ces.length > 0) popup.addAttribute( "Condition type", ceBox );
        ceBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String s = ceBox.getValue( ceBox.getSelectedIndex() );
                if (!s.equals( "IGNORE" )) {
                    addNewCE(s);
                    popup.hide();
                }
            }
        });        

        
        //
        // The list of DSL sentences
        //
        if (completions.getDSLConditions().length > 0) {
            final ListBox dsls = new ListBox();
            dsls.addItem( "Choose..." );
            for(int i = 0; i < completions.getDSLConditions().length; i++ ) {
                DSLSentence sen = completions.getDSLConditions()[i];
                dsls.addItem( sen.toString(), Integer.toString( i ) );
            }
            
            dsls.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    int idx = Integer.parseInt( dsls.getValue( dsls.getSelectedIndex() ) );
                    addNewDSLLhs( (DSLSentence) completions.getDSLConditions()[ idx ] );
                    popup.hide();
                }
            });
            popup.addAttribute( "Template conditions", dsls );
        }
        
        popup.setPopupPosition( w.getAbsoluteLeft() - 400, w.getAbsoluteTop() );
        popup.show();

    }
    
    protected void addNewDSLLhs(DSLSentence sentence) {
        model.addLhsItem( sentence.copy() );
        refreshWidget();
        
    }

    protected void showActionSelector(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "Add a new action...");
        
        popup.setStyleName( "ks-popups-Popup" );
        
        //
        // First load up the stuff to do with bound variables or globals
        //
        List vars = model.getBoundFacts();        
        final ListBox varBox = new ListBox();
        final ListBox retractBox = new ListBox();
        final ListBox modifyBox = new ListBox();
        varBox.addItem( "Choose ..." );        
        retractBox.addItem( "Choose ..." );
        modifyBox.addItem( "Choose ..." );
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            String v = (String) iter.next();
            varBox.addItem( v );
            retractBox.addItem( v );
            modifyBox.addItem( v );
        }
        String[] globals = this.completions.getGlobalVariables();
        for ( int i = 0; i < globals.length; i++ ) {
            varBox.addItem( globals[i] );
        }
        
        varBox.setSelectedIndex( 0 );
        varBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addActionSetField(varBox.getItemText( varBox.getSelectedIndex() ));
                popup.hide();
            }
        });
        
        retractBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addRetract(retractBox.getItemText( retractBox.getSelectedIndex() ));
                popup.hide();
            }            
        });
        
        modifyBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addModify(modifyBox.getItemText( modifyBox.getSelectedIndex() ));
                popup.hide();
            }            
        });        
        
        if (varBox.getItemCount() > 1) {
            popup.addAttribute( "Set the values of a field on", varBox );            
        }

        
        if (modifyBox.getItemCount() > 1) {
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.add( modifyBox );
            Image img = new Image("images/information.gif");
            img.setTitle( "Modify a field on a fact, and notify the engine to re-evaluate rules." );
            horiz.add( img );
            popup.addAttribute( "Modify a fact", horiz );
        }
        
        popup.addRow( new HTML("<hr/>") );
        
        if (retractBox.getItemCount() > 1) {
            popup.addAttribute( "Retract the fact", retractBox );
        }        
        
        popup.addRow( new HTML("<hr/>") );
        
        
        final ListBox factsToAssert = new ListBox();
        final ListBox factsToLogicallyAssert = new ListBox();
        factsToAssert.addItem( "Choose ..." );
        factsToLogicallyAssert.addItem( "Choose ..." );
        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            String item = completions.getFactTypes()[i];
            factsToAssert.addItem( item );
            factsToLogicallyAssert.addItem( item );
        }
        
        factsToAssert.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
               String fact = factsToAssert.getItemText( factsToAssert.getSelectedIndex() );
               model.addRhsItem( new ActionAssertFact(fact) );
               refreshWidget();
               popup.hide();
                
            }            
        });
        
        factsToLogicallyAssert.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
               String fact = factsToLogicallyAssert.getItemText( factsToLogicallyAssert.getSelectedIndex() );
               model.addRhsItem( new ActionAssertLogicalFact(fact) );
               refreshWidget();
               popup.hide();
                
            }            
        });        
        
        
        if (factsToAssert.getItemCount() > 1) {
            popup.addAttribute( "Assert a new fact", factsToAssert );
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.add( factsToLogicallyAssert );
            Image img = new Image("images/information.gif");
            img.setTitle( "Logically assert a fact - the fact will be retracted when the supporting evidence is removed." );
            horiz.add( img );
            popup.addAttribute( "Logically assert a new fact", horiz );
        }
        
        
        
        //
        // The list of DSL sentences
        //
        if (completions.getDSLActions().length > 0) {
            final ListBox dsls = new ListBox();
            dsls.addItem( "Choose..." );
            for(int i = 0; i < completions.getDSLActions().length; i++ ) {
                DSLSentence sen = (DSLSentence) completions.getDSLActions()[ i ];
                dsls.addItem( sen.toString(), Integer.toString( i ) );
            }
            
            dsls.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    int idx = Integer.parseInt( dsls.getValue( dsls.getSelectedIndex() ) );
                    addNewDSLRhs( (DSLSentence) completions.getDSLActions()[ idx ] );
                    popup.hide();
                }
            });
            popup.addAttribute( "Template actions", dsls );
        }
        

        popup.setPopupPosition( Window.getClientWidth()/3, Window.getClientHeight()/3 );
        popup.show();
    }
    


    protected void addModify(String itemText) {
        this.model.addRhsItem(new ActionModifyField(itemText));
        refreshWidget(); 
        
    }

    protected void addNewDSLRhs(DSLSentence sentence) {
        this.model.addRhsItem( sentence.copy() );
        refreshWidget();        
    }

    protected void addRetract(String var) {
        this.model.addRhsItem( new ActionRetractFact(var) );
        refreshWidget();        
    }

    protected void addActionSetField(String itemText) {        
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

    /**
     * Builds all the condition widgets.
     */
    private Widget renderLhs(final RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
         
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            Widget w = null;
            if (pattern instanceof FactPattern) {                  
                w = new FactPatternWidget(this, pattern, completions, true) ;
                vert.add( wrapLHSWidget( model,
                              i,
                              w ) );
                vert.add( spacerWidget() );
            } else if (pattern instanceof CompositeFactPattern) {
                w = new CompositeFactPatternWidget(this, (CompositeFactPattern) pattern, completions) ;
                vert.add( wrapLHSWidget( model, i, w ));
                vert.add( spacerWidget() );
            } else if (pattern instanceof DSLSentence) {
                //ignore this time                
            } else {
                throw new RuntimeException("I don't know what type of pattern that is.");
            }
            

        }
        
        
        VerticalPanel dsls = new VerticalPanel();
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            Widget w = null;
            
            if (pattern instanceof DSLSentence) {
                w = new DSLSentenceWidget((DSLSentence) pattern);

                dsls.add( wrapLHSWidget( model, i, w ) );
                dsls.setStyleName( "model-builderInner-Background" );
            }
        }
        vert.add( dsls );

        
        
        return vert;
    }

    private HTML spacerWidget() {
        HTML h = new HTML("&nbsp;");
        h.setHeight( "2px" );
        return h;
    }

    /**
     * This adds the widget to the UI, also adding the remove icon.
     */
    private Widget wrapLHSWidget(final RuleModel model,
                              int i,
                              Widget w) {
        HorizontalPanel horiz = new HorizontalPanel();
        
        Image remove = new ImageButton("images/delete_item_small.gif");
        remove.setTitle( "Remove this ENTIRE condition, and all the field constraints that belong to it." );
        final int idx = i;
        remove.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                YesNoDialog diag = new YesNoDialog("Remove this entire condition?", new Command() {
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
        

        horiz.setWidth( "100%" );
        w.setWidth( "100%" );
        
        horiz.add( w );
        horiz.add( remove );

        return horiz;


    }


    
    public RuleModel getModel() {
        return model;
    }

    /**
     * Returns true is a var name has already been used
     * either by the rule, or as a global.
     */
    public boolean isVariableNameUsed(String name) {
        return model.isVariableNameUsed( name ) || completions.isGlobalVariable( name );
    }
    
    
}
