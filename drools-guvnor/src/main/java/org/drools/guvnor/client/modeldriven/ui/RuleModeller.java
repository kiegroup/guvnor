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



import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.*;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * This is the parent widget that contains the model based rule builder.
 *
 * @author Michael Neale
 *
 */
public class RuleModeller extends DirtyableComposite {

    private DirtyableFlexTable layout;
    private SuggestionCompletionEngine completions;
    private RuleModel model;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public RuleModeller(RuleAsset asset, RuleViewer viewer) {
        this(asset);
    }

    public RuleModeller(RuleAsset asset) {
        this.model = (RuleModel) asset.content;

        this.completions = SuggestionCompletionCache.getInstance().getEngineFromCache( asset.metaData.packageName );

        layout = new DirtyableFlexTable();

        initWidget();

        layout.setStyleName( "model-builder-Background" );
        initWidget( layout );
        setWidth( "100%" );
        setHeight( "100%" );
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void initWidget() {
        layout.clear();

        Image addPattern = new ImageButton( "images/new_item.gif" );
        addPattern.setTitle(constants.AddAConditionToThisRule());
        addPattern.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showConditionSelector(w);
            }
        });

        layout.getColumnFormatter().setWidth(0, "8%");
        layout.getColumnFormatter().setWidth(1, "87%");
        layout.getColumnFormatter().setWidth(2, "5%");



        layout.setWidget( 0, 0, new SmallLabel("WHEN") );
        layout.setWidget( 0, 2, addPattern );



        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
        layout.getFlexCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        layout.setWidget( 2, 0, new SmallLabel("THEN") );

        Image addAction = new ImageButton("images/new_item.gif");
        addAction.setTitle( "Add an action to this rule." );
        addAction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showActionSelector(w);
            }
        });
        layout.setWidget( 2, 2, addAction );

        layout.setWidget( 3, 1, renderRhs(this.model) );
        layout.getFlexCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT);
        layout.getFlexCellFormatter().setVerticalAlignment(3, 1, HasVerticalAlignment.ALIGN_TOP);

        layout.setWidget( 4, 0, new SmallLabel("(options)") );
        layout.setWidget( 4, 2, getAddAttribute() );
        layout.setWidget( 5, 1, new RuleAttributeWidget(this, this.model) );

    }

    public void refreshWidget() {
        initWidget();
        makeDirty();
    }

    private Widget getAddAttribute() {
        Image add = new ImageButton("images/new_item.gif"); //NON-NLS
        add.setTitle(constants.AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted());

        add.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showAttributeSelector(w);
            }
        });
        return add;
    }


    protected void showAttributeSelector(Widget w) {
        final FormStylePopup pop = new FormStylePopup("images/config.png", constants.AddAnOptionToTheRule()); //NON-NLS
        final ListBox list = RuleAttributeWidget.getAttributeList();
        final Image addbutton = new ImageButton("images/new_item.gif");                                                //NON-NLS
        final TextBox box = new TextBox();


        list.setSelectedIndex( 0 );

        list.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
              model.addAttribute( new RuleAttribute(list.getItemText( list.getSelectedIndex() ), "") );
              refreshWidget();
              pop.hide();
            }
        });
        box.setVisibleLength( 15 );

        addbutton.setTitle(constants.AddMetadataToTheRule());

        addbutton.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

            	model.addMetadata( new RuleMetadata(box.getText(), "") );
            	refreshWidget();
                pop.hide();
            }
        });
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
        horiz.add( box );
        horiz.add( addbutton );





        pop.addAttribute(constants.Metadata3(), horiz );
        pop.addAttribute(constants.Attribute1(), list );

        //add text field
        //add button
        //add listener that adds the rule Attribute
//        pop.addAttribute( "Metadata:",
//                editableText( new FieldBinding() {
//                                  public String getValue() {
//                                      return data.subject;
//                                  }
//
//                                  public void setValue(String val) {
//                                      data.subject = val;
//                                  }
//                              },
//                              "A short description of the subject matter." ) );


        pop.show();
    }

    /**
     * Do all the widgets for the RHS.
     */
    private Widget renderRhs(final RuleModel model) {
        DirtyableVerticalPane widget = new DirtyableVerticalPane();

        for ( int i = 0; i < model.rhs.length; i++ ) {
            IAction action = model.rhs[i];

            Widget w = null;
            if (action instanceof ActionCallMethod) {
                w = new ActionCallMethodWidget(this, (ActionCallMethod) action, completions);
            } else if (action instanceof ActionSetField) {
                w =  new ActionSetFieldWidget(this, (ActionSetField) action, completions ) ;
            } else if (action instanceof ActionInsertFact) {
                w = new ActionInsertFactWidget(this, (ActionInsertFact) action, completions );
            } else if (action instanceof ActionRetractFact) {
                w = new ActionRetractFactWidget(this.completions, (ActionRetractFact) action );
            } else if (action instanceof DSLSentence) {
                w = new DSLSentenceWidget((DSLSentence) action,this.completions);
                w.setStyleName( "model-builderInner-Background" ); //NON-NLS
            } else if (action instanceof FreeFormLine) {
            	final TextBox tb = new TextBox();
            	final FreeFormLine ffl = (FreeFormLine) action;
            	tb.setText(ffl.text);
            	tb.addChangeListener(new ChangeListener() {
					public void onChange(Widget arg0) {
						ffl.text = tb.getText();
					}
            	});
            	w = tb;
            } else if (action instanceof ActionGlobalCollectionAdd) {
                ActionGlobalCollectionAdd gca = (ActionGlobalCollectionAdd) action;
                SimplePanel sp = new SimplePanel();
                sp.setStyleName("model-builderInner-Background"); //NON-NLS
                w = sp;
                sp.add(new SmallLabel("&nbsp;" + Format.format(constants.AddXToListY(), gca.factName, gca.globalName)));
            }

            //w.setWidth( "100%" );
            widget.add( spacerWidget() );
            //vert.setWidth( "100%" );

            DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();

            Image remove = new ImageButton("images/delete_item_small.gif"); //NON-NLS
            remove.setTitle(constants.RemoveThisAction());
            final int idx = i;
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                	if (Window.confirm(constants.RemoveThisItem())) {
                            model.removeRhsItem(idx);
                            refreshWidget();
                    }
                }
            } );
            horiz.add( w );
            if (!(w instanceof ActionRetractFactWidget)) {
                w.setWidth( "100%" );               //NON-NLS
                horiz.setWidth( "100%" );
            }

            horiz.add( remove );
            widget.add( horiz );

        }

        return widget;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showConditionSelector(final Widget w) {
        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.AddAConditionToTheRule());


        //
        // The list of facts
        //
        String[] facts = completions.getFactTypes();
        final ListBox factTypeBox = new ListBox();
        factTypeBox.addItem(constants.ChooseFactType(), "IGNORE" );
        for ( int i = 0; i < facts.length; i++ ) {
            factTypeBox.addItem( facts[i] );
        }
        factTypeBox.setSelectedIndex( 0 );
        if (facts.length > 0) popup.addAttribute(constants.Fact1(), factTypeBox );
        factTypeBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String s = factTypeBox.getItemText( factTypeBox.getSelectedIndex() );
                if (!s.equals( "IGNORE" )) {         //NON-NLS
                    addNewFact(s);
                    popup.hide();
                }
            }
        });

        //
        // The list of top level CEs
        //
        String ces[]  = HumanReadable.CONDITIONAL_ELEMENTS;
        final ListBox ceBox = new ListBox();
        ceBox.addItem(constants.ChooseOtherConditionType(), "IGNORE" ); //NON-NLS
        for ( int i = 0; i < ces.length; i++ ) {
            String ce = ces[i];
            ceBox.addItem( HumanReadable.getCEDisplayName( ce ), ce );
        }
        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
        	ceBox.addItem(constants.FreeFormDrl(), "FF"); //NON-NLS
        }
        ceBox.setSelectedIndex( 0 );

        if (facts.length > 0) popup.addAttribute(constants.ConditionTypeButton(), ceBox );
        ceBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String s = ceBox.getValue( ceBox.getSelectedIndex() );
                if (!s.equals( "IGNORE" )) {                //NON-NLS
                	if (s.equals("FF")) {                   //NON-NLS
                		model.addLhsItem(new FreeFormLine());
                		refreshWidget();
                	} else {
	                    addNewCE(s);
                	}
                    popup.hide();
                }
            }
        });


        //
        // The list of DSL sentences
        //
        if (completions.getDSLConditions().length > 0) {
            final ListBox dsls = new ListBox();
            dsls.addItem(constants.ChooseDotDotDot());
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
            popup.addAttribute(constants.DSLSentence(), dsls );
        }

        if (completions.getDSLConditions().length == 0 && facts.length == 0) {
        	popup.addRow(new HTML("<div class='highlight'>" + constants.NoModelTip() + "</div>")); //NON-NLS
        }
        popup.show();

    }

    protected void addNewDSLLhs(DSLSentence sentence) {
        model.addLhsItem( sentence.copy() );
        refreshWidget();

    }

    protected void showActionSelector(Widget w) {
        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.AddANewAction());


        //
        // First load up the stuff to do with bound variables or globals
        //
        List<String> vars = model.getBoundFacts();
        final ListBox varBox = new ListBox();
        final ListBox retractBox = new ListBox();
        final ListBox modifyBox = new ListBox();
        final ListBox callMethodBox = new ListBox();

        varBox.addItem( constants.ChooseDotDotDot() );
        retractBox.addItem( constants.ChooseDotDotDot() );
        modifyBox.addItem( constants.ChooseDotDotDot() );
        callMethodBox.addItem( constants.ChooseDotDotDot() );
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            String v = (String) iter.next();
            varBox.addItem( v );
            retractBox.addItem( v );
            modifyBox.addItem( v );
            callMethodBox.addItem( v );
        }
        String[] globals = this.completions.getGlobalVariables();
        for ( int i = 0; i < globals.length; i++ ) {
            varBox.addItem( globals[i] );
            callMethodBox.addItem( globals[i] );
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

        callMethodBox.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                addModifyField(callMethodBox.getItemText( callMethodBox.getSelectedIndex() ));
                popup.hide();
            }
        });





        if (modifyBox.getItemCount() > 1) {
            final HorizontalPanel horiz = new HorizontalPanel();

            final AbsolutePanel p = new AbsolutePanel();
            p.add(varBox);


            final CheckBox cb = new CheckBox(constants.NotifyEngineOfChanges());
            cb.setChecked(false);
            cb.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                     if (cb.isChecked()) {
                         p.clear();
                         p.add(modifyBox);
                     } else {
                         p.clear();
                         p.add(varBox);
                     }
                }
            });

            horiz.add(p);
            horiz.add(cb);


            InfoPopup img = new InfoPopup(constants.NotifyEngineOfChangesUpdateModify(), constants.ModifyEngineTip());
            horiz.add( img );
            HorizontalPanel variablePanel = new HorizontalPanel();
            popup.addAttribute(constants.SetFieldValues(), horiz );

        } else {
            if (varBox.getItemCount() > 1) {
                popup.addAttribute(constants.SetFieldValues(), varBox );
            }
        }

        //popup.addRow( new HTML("<hr/>") );

        if (retractBox.getItemCount() > 1) {
            popup.addAttribute(constants.RetractTheFact(), retractBox );
        }

        //popup.addRow( new HTML("<hr/>") );


        final ListBox factsToAssert = new ListBox();
        final ListBox factsToLogicallyAssert = new ListBox();
        factsToAssert.addItem( constants.ChooseDotDotDot() );
        factsToLogicallyAssert.addItem( constants.ChooseDotDotDot() );
        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            String item = completions.getFactTypes()[i];
            factsToAssert.addItem( item );
            factsToLogicallyAssert.addItem( item );
        }

        factsToAssert.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
               String fact = factsToAssert.getItemText( factsToAssert.getSelectedIndex() );
               model.addRhsItem( new ActionInsertFact(fact) );
               refreshWidget();
               popup.hide();

            }
        });

        //
        // The list of DSL sentences
        //
        if (completions.getDSLActions().length > 0) {
            final ListBox dsls = new ListBox();
            dsls.addItem( constants.ChooseDotDotDot());
            for(int i = 0; i < completions.getDSLActions().length; i++ ) {
                DSLSentence sen = completions.getDSLActions()[ i ];
                if(sen!=null)
                	dsls.addItem( sen.toString(), Integer.toString( i ) );
            }

            dsls.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    int idx = Integer.parseInt( dsls.getValue( dsls.getSelectedIndex() ) );
                    addNewDSLRhs( (DSLSentence) completions.getDSLActions()[ idx ] );
                    popup.hide();
                }
            });
            popup.addAttribute(constants.DSLSentence(), dsls );
        }

        popup.addRow(new HTML(constants.AdvancedOptionsColon()));

        if (completions.globalCollections.length > 0) {
            if (vars.size() > 0) {
                final ListBox cols = new ListBox();
                final ListBox facts = new ListBox();
                for(int i = 0; i < completions.globalCollections.length; i++) {
                    cols.addItem(completions.globalCollections[i]);
                }

                for (String bf : vars) {
                    facts.addItem(bf);
                }

                HorizontalPanel h = new HorizontalPanel();
                h.add(facts);
                h.add(new SmallLabel("&nbsp;to&nbsp;"));
                h.add(cols);
                Button ok = new Button(constants.Add());
                ok.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        ActionGlobalCollectionAdd gca = new ActionGlobalCollectionAdd();
                        gca.globalName = cols.getItemText(cols.getSelectedIndex());
                        gca.factName = facts.getItemText(facts.getSelectedIndex());
                        model.addRhsItem(gca);
                        refreshWidget();
                        popup.hide();
                    }
                });
                h.add(ok);
                popup.addAttribute(constants.AddAnItemToACollection(), h);
            }
        }

        factsToLogicallyAssert.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
               String fact = factsToLogicallyAssert.getItemText( factsToLogicallyAssert.getSelectedIndex() );
               model.addRhsItem( new ActionInsertLogicalFact(fact) );
               refreshWidget();
               popup.hide();

            }
        });


        if (factsToAssert.getItemCount() > 1) {
            popup.addAttribute(constants.InsertANewFact(), factsToAssert );
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.add( factsToLogicallyAssert );
            Image img = new Image("images/information.gif"); //NON-NLS
            img.setTitle(constants.LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved());
            horiz.add( img );
            popup.addAttribute(constants.LogicallyInsertANewFact(), horiz );
        }


        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {

	        if (callMethodBox.getItemCount() > 1) {
	            popup.addAttribute(constants.CallAMethodOnFollowing(), callMethodBox );
	        }

	        Button ff = new Button(constants.AddFreeFormDrl());
	        popup.addAttribute(constants.FreeFormAction(), ff);
	        ff.addClickListener(new ClickListener() {
				public void onClick(Widget arg0) {
					model.addRhsItem(new FreeFormLine());
					refreshWidget();
					popup.hide();
				}
	        });
        }

        popup.show();
    }



    protected void addModify(String itemText) {
        this.model.addRhsItem(new ActionUpdateField(itemText));
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

    protected void addModifyField(String itemText) {
        this.model.addRhsItem(new ActionCallMethod(itemText));
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
        DirtyableVerticalPane vert = new DirtyableVerticalPane();

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
            } else if (pattern instanceof FreeFormLine){
            	final FreeFormLine ffl = (FreeFormLine) pattern;
            	final TextBox tb = new TextBox();
            	tb.setText(ffl.text);
            	tb.setTitle(constants.ThisIsADrlExpressionFreeForm());
            	tb.addChangeListener(new ChangeListener() {
            		public void onChange(Widget arg0) {
            			ffl.text = tb.getText();
					}
            	});
            	vert.add(wrapLHSWidget(model, i, tb));
            	vert.add( spacerWidget() );
            } else {
                throw new RuntimeException("I don't know what type of pattern that is.");
            }

        }


        DirtyableVerticalPane dsls = new DirtyableVerticalPane();
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            Widget w = null;

            if (pattern instanceof DSLSentence) {
                w = new DSLSentenceWidget((DSLSentence) pattern,completions);

                dsls.add( wrapLHSWidget( model, i, w ) );
                dsls.setStyleName( "model-builderInner-Background" ); //NON-NLS
            }
        }
        vert.add( dsls );

        return vert;
    }

    private HTML spacerWidget() {
        HTML h = new HTML("&nbsp;");       //NON-NLS
        h.setHeight( "2px" );              //NON-NLS
        return h;
    }

    /**
     * This adds the widget to the UI, also adding the remove icon.
     */
    private Widget wrapLHSWidget(final RuleModel model,
                              int i,
                              Widget w) {
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();

        Image remove = new ImageButton("images/delete_item_small.gif"); //NON-NLS
        remove.setTitle(constants.RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt());
        final int idx = i;
        remove.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if (Window.confirm(constants.RemoveThisEntireConditionQ())) {
                        if (model.removeLhsItem(idx)) {
                            refreshWidget();
                        } else {
                            ErrorPopup.showMessage(constants.CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule());
                        }
                }
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

    public boolean isDirty() {
        return ( layout.hasDirty() || dirtyflag) ;
    }

    public SuggestionCompletionEngine getSuggestionCompletions() {
        return this.completions;
    }
}