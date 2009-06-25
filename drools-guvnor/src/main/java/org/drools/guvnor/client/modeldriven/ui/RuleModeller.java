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



import java.util.*;

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
import com.google.gwt.user.client.Command;
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



        layout.setWidget( 0, 0, new SmallLabel(constants.WHEN()) );
        layout.setWidget( 0, 2, addPattern );



        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
        layout.getFlexCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        layout.setWidget( 2, 0, new SmallLabel(constants.THEN()) );

        Image addAction = new ImageButton("images/new_item.gif"); //NON-NLS
        addAction.setTitle(constants.AddAnActionToThisRule());
        addAction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showActionSelector(w);
            }
        });
        layout.setWidget( 2, 2, addAction );

        layout.setWidget( 3, 1, renderRhs(this.model) );
        layout.getFlexCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT);
        layout.getFlexCellFormatter().setVerticalAlignment(3, 1, HasVerticalAlignment.ALIGN_TOP);

        layout.setWidget( 4, 0, new SmallLabel(constants.optionsRuleModeller()) );
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
                w = new ActionRetractFactWidget(this.completions, (ActionRetractFact) action , this.getModel());
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
        popup.setWidth(-1);
        popup.setTitle(constants.AddAConditionToTheRule());

        final Map<String, Command> cmds = new HashMap<String, Command>();

        final ListBox choices = new ListBox(true);

        

        //
        // The list of DSL sentences
        //
        if (completions.getDSLConditions().length > 0) {



            for(int i = 0; i < completions.getDSLConditions().length; i++ ) {
                final DSLSentence sen = completions.getDSLConditions()[i];
                String key = "DSL" + i;
                choices.addItem(sen.toString(), key);
                cmds.put(key, new Command() {
                    public void execute() {
                       addNewDSLLhs(sen);
                       popup.hide();
                    }
                });
            }

        }

        //
        // The list of facts
        //
        final String[] facts = completions.getFactTypes();
        if (facts != null && facts.length > 0) {
            choices.addItem("..................");
            
            for ( int i = 0; i < facts.length; i++ ) {
                final String f = facts[i];
                String key = "NF" + f;

                choices.addItem(f + " ...",  key);
                cmds.put(key, new Command() {
                    public void execute() {
                        addNewFact(f);
                        popup.hide();
                    }
                });
            }
        }


        //
        // The list of top level CEs
        //
        String ces[]  = HumanReadable.CONDITIONAL_ELEMENTS;

        choices.addItem("..................");
        for ( int i = 0; i < ces.length; i++ ) {
            final String ce = ces[i];
            String key = "CE" + ce;
            choices.addItem( HumanReadable.getCEDisplayName( ce ) + " ...", key );
            cmds.put(key, new Command() {
                public void execute() {
                    addNewCE(ce);
                    popup.hide();
                }
            });


        }






        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
            choices.addItem("..................");
            choices.addItem(constants.FreeFormDrl(), "FF");
            cmds.put("FF", new Command() {
                public void execute() {
                    model.addLhsItem(new FreeFormLine());
                    refreshWidget();
                    popup.hide();
                }
            });
        }


        if (completions.getDSLConditions().length == 0 && facts.length == 0) {
        	popup.addRow(new HTML("<div class='highlight'>" + constants.NoModelTip() + "</div>")); //NON-NLS
        }

        final ChangeListener cl = new ChangeListener() {
            public void onChange(Widget sender) {
                int sel = choices.getSelectedIndex();
                if (sel != -1) {
                    Command cmd = cmds.get(choices.getValue(choices.getSelectedIndex()));
                    if (cmd != null) cmd.execute();
                }
            }
        };
        //choices.addChangeListener(cl);

        choices.addKeyboardListener(new KeyboardListenerAdapter() {
            @Override
            public void onKeyUp(final Widget sender, char keyCode, int modifiers) {
                if (keyCode == KeyboardListener.KEY_ENTER) {
                    cl.onChange(sender);
                }
            }
        });


        HorizontalPanel hp = new HorizontalPanel();
        hp.add(choices);
        Button b = new Button(constants.OK());
        hp.add(b);
        b.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                cl.onChange(sender);
            }
        });
        popup.addRow(hp);

        popup.show();
        choices.setFocus(true);

        popup.setAfterShow(new Command() {
            public void execute() {
                choices.setFocus(true);
            }
        });

    }


    protected void addNewDSLLhs(DSLSentence sentence) {
        model.addLhsItem( sentence.copy() );
        refreshWidget();

    }


    protected void showActionSelector(Widget w) {
        final FormStylePopup popup = new FormStylePopup();
        popup.setWidth(-1);
        popup.setTitle(constants.AddANewAction());

        final ListBox choices = new ListBox(true);
        final Map<String, Command> cmds = new HashMap<String, Command>();

        //
        // First load up the stuff to do with bound variables or globals
        //
        List<String> vars = model.getBoundFacts();
        List<String> vars2 = model.getRhsBoundFacts();
        String[] globals = this.completions.getGlobalVariables();


        //
        // The list of DSL sentences
        //
        if (completions.getDSLActions().length > 0) {

            for(int i = 0; i < completions.getDSLActions().length; i++ ) {
                final DSLSentence sen = completions.getDSLActions()[ i ];
                if(sen!=null) {
                    String sentence = sen.toString();
                    choices.addItem(sentence, "DSL" + sentence);  //NON-NLS
                    cmds.put("DSL" + sentence, new Command() {    //NON-NLS
                        public void execute() {
                          addNewDSLRhs(sen);
                          popup.hide();
                        }
                    });
                }
            }

            choices.addItem("................");

        }


        //Do Set field (NOT modify)
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            final String v = (String) iter.next();

            //varBox.addItem( v );
            choices.addItem(Format.format(constants.ChangeFieldValuesOf0(), v), "VAR" + v); //NON-NLS
            cmds.put("VAR" + v, new Command() {        //NON-NLS
                public void execute() {
                    addActionSetField(v);
                    popup.hide();
                }
            });


        }

        for ( int i = 0; i < globals.length; i++ ) {     //we also do globals here...
            final String v = globals[i];
            choices.addItem(Format.format(constants.ChangeFieldValuesOf0(), v), "GLOBVAR" + v);   //NON-NLS
            cmds.put("GLOBVAR" + v, new Command() {        //NON-NLS
                public void execute() {
                    addActionSetField(v);
                    popup.hide();
                }
            });
        }


        //RETRACT
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            final String v = (String) iter.next();
            //retractBox.addItem( v );
            choices.addItem(Format.format(constants.Retract0(), v), "RET" + v); //NON-NLS
            cmds.put("RET" + v, new Command() {                                          //NON-NLS
                public void execute() {
                     addRetract(v);
                     popup.hide();
                }
            });
        }

        //MODIFY
        for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
            final String v = (String) iter.next();
            // modifyBox.addItem( v );

            choices.addItem(Format.format(constants.Modify0(), v), "MOD" + v);    //NON-NLS
            cmds.put("MOD" + v, new Command() {                                            //NON-NLS
                public void execute() {
                    addModify(v);
                    popup.hide();
                }
            });
        }

        choices.addItem("................");


        //Now inserts:
        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            final String item = completions.getFactTypes()[i];
            choices.addItem(Format.format(constants.InsertFact0(), item), "INS" + item); //NON-NLS
            cmds.put("INS" + item, new Command() {                                                //NON-NLS
                public void execute() {
                    model.addRhsItem( new ActionInsertFact(item) );
                    refreshWidget();
                    popup.hide();
                }
            });
        }

        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            final String item = completions.getFactTypes()[i];
            choices.addItem(Format.format(constants.LogicallyInsertFact0(), item), "LINS" + item); //NON-NLS
            cmds.put("LINS" + item, new Command() {                                                         //NON-NLS
                public void execute() {
                       model.addRhsItem( new ActionInsertLogicalFact(item) );
                       refreshWidget();
                       popup.hide();
                }
            });
        }




        choices.addItem("................");


        //now global collections
        if (completions.globalCollections.length > 0 && vars.size() > 0) {
            for (String bf : vars) {
                for(int i = 0; i < completions.globalCollections.length; i++) {
                    final String glob = completions.globalCollections[i];
                    final String var = bf;
                    choices.addItem(Format.format(constants.Append0ToList1(), var, glob), "GLOBCOL" + glob + var); //NON-NLS
                    cmds.put("GLOBCOL" + glob + var, new Command() {                                                        //NON-NLS
                        public void execute() {
                            ActionGlobalCollectionAdd gca = new ActionGlobalCollectionAdd();
                            gca.globalName = glob;
                            gca.factName = var;
                            model.addRhsItem(gca);
                            refreshWidget();
                            popup.hide();
                        }
                    });
                }
            }
        }


        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
            choices.addItem(constants.AddFreeFormDrl(), "FF");  //NON-NLS
            cmds.put("FF", new Command() {                     //NON-NLS
                public void execute() {
                    model.addRhsItem(new FreeFormLine());
                    refreshWidget();
                    popup.hide();
                }
            });
            for ( int i = 0; i < globals.length; i++ ) {     //we also do globals here...
                final String v = globals[i];
                choices.addItem(Format.format(constants.CallMethodOn0(), v ), "GLOBCALL" + v); //NON-NLS
                cmds.put("GLOBCALL" + v, new Command() {      //NON-NLS
                    public void execute() {
                        addCallMethod(v);
                        popup.hide();
                    }
                });

            }

                    //CALL methods
            for ( Iterator iter = vars.iterator(); iter.hasNext(); ) {
                final String v = (String) iter.next();

                choices.addItem(Format.format(constants.CallMethodOn0(), v ), "CALL" + v); //NON-NLS
                cmds.put("CALL" + v, new Command() { //NON-NLS
                    public void execute() {
                        addCallMethod(v);
                        popup.hide();
                    }
                });
            }
            //Do Set field (NOT modify)
            for ( Iterator iter = vars2.iterator(); iter.hasNext(); ) {
                final String v = (String) iter.next();

                choices.addItem(Format.format(constants.CallMethodOn0(), v), "CALL" + v); //NON-NLS
                cmds.put("CALL" + v, new Command() {        //NON-NLS
                    public void execute() {
                        addCallMethod(v);
                        popup.hide();
                    }
                });


            }

        }

        HorizontalPanel hp = new HorizontalPanel();
        final ClickListener cl = new ClickListener() {
            public void onClick(Widget sender) {
                int sel = choices.getSelectedIndex();
                if (sel != -1) {
                    cmds.get(choices.getValue(sel)).execute();
                }
            }
        };

        choices.addKeyboardListener(new KeyboardListenerAdapter() {
            @Override
            public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                if (keyCode == KeyboardListener.KEY_ENTER) {
                    cl.onClick(sender);
                }
            }
        });

        Button ok = new Button(constants.OK());
        ok.addClickListener(cl);
        hp.add(choices);
        hp.add(ok);
        popup.addRow(hp);

        popup.show();
        choices.setFocus(true);
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

    protected void addCallMethod(String itemText) {
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