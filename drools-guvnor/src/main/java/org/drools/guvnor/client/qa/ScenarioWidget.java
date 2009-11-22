package org.drools.guvnor.client.qa;

import java.util.*;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.testing.ExecutionTrace;
import org.drools.guvnor.client.modeldriven.testing.FactData;
import org.drools.guvnor.client.modeldriven.testing.FieldData;
import org.drools.guvnor.client.modeldriven.testing.Fixture;
import org.drools.guvnor.client.modeldriven.testing.RetractFact;
import org.drools.guvnor.client.modeldriven.testing.Scenario;
import org.drools.guvnor.client.modeldriven.testing.VerifyFact;
import org.drools.guvnor.client.modeldriven.testing.VerifyField;
import org.drools.guvnor.client.modeldriven.testing.VerifyRuleFired;
import org.drools.guvnor.client.modeldriven.ui.ActionValueEditor;
import org.drools.guvnor.client.modeldriven.ui.ConstraintValueEditor;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

public class ScenarioWidget extends Composite {

	private String[] availableRules;
	private SuggestionCompletionEngine sce;
	private ChangeListener ruleSelectionCL;
	RuleAsset asset;
	VerticalPanel layout;
	boolean showResults;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public ScenarioWidget(RuleAsset asset, RuleViewer viewer) {
        this(asset);
    }

    public ScenarioWidget(RuleAsset asset) {
		this.asset = asset;
		this.layout = new VerticalPanel();
		this.showResults = false;


    	this.sce = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.metaData.packageName);

    	Scenario scenario = (Scenario) asset.content;
    	if (scenario.fixtures.size() == 0) {
    		scenario.fixtures.add(new ExecutionTrace());
    	}

    	if (!asset.isreadonly) {
//    		layout.setWidget(0, 0, new TestRunnerWidget(this, asset.metaData.packageName));
    		layout.add(new TestRunnerWidget(this, asset.metaData.packageName));
    	}


        renderEditor();




        initWidget(layout);

        setStyleName("scenario-Viewer");

        layout.setWidth("100%");
        //layout.setHeight("100%");

    }



	void renderEditor() {

		if (this.layout.getWidgetCount() == 2) {
			this.layout.remove(1);
		}


		final Scenario scenario = (Scenario) asset.content;
		DirtyableFlexTable editorLayout = new DirtyableFlexTable();
		editorLayout.clear();
		editorLayout.setWidth("100%");
		editorLayout.setStyleName("model-builder-Background");
//		this.layout.setWidget(1, 0, editorLayout);
		this.layout.add(editorLayout);
		ScenarioHelper hlp = new ScenarioHelper();
		List fixtures = hlp.lumpyMap(scenario.fixtures);
        List<ExecutionTrace> listExecutionTrace = new ArrayList<ExecutionTrace>();
        for (int i = 0; i < fixtures.size(); i++) {
           final Object f = fixtures.get(i);
			if (f instanceof ExecutionTrace) {
                listExecutionTrace.add((ExecutionTrace)f);
            }
         }
        int layoutRow = 1;
        int executionTraceLine=0;
        ExecutionTrace previousEx = null;
        for (int i = 0; i < fixtures.size(); i++) {
			final Object f = fixtures.get(i);
			if (f instanceof ExecutionTrace) {
				previousEx = (ExecutionTrace) f;
				HorizontalPanel h = new HorizontalPanel();
				h.add(getNewExpectationButton(previousEx, scenario));
				h.add(new SmallLabel(constants.EXPECT()));
				editorLayout.setWidget(layoutRow, 0, h);

                final ExecutionTrace et = (ExecutionTrace) previousEx;
                Image del = new ImageButton("images/delete_item_small.gif", constants.DeleteItem(), new ClickListener() {
                    public void onClick(Widget w) {
                         if ( Window.confirm(constants.AreYouSureYouWantToRemoveThisItem()) ) {
                             scenario.removeExecutionTrace( et );
                             renderEditor();
                         }
                    }
                });
                h.add(del);
                executionTraceLine++;
                if (executionTraceLine>= listExecutionTrace.size()){
                    executionTraceLine= listExecutionTrace.size()-1;
                }
				editorLayout.setWidget(layoutRow, 1, new ExecutionWidget(previousEx, showResults));
				//layout.setWidget(layoutRow, 2, getNewExpectationButton(previousEx, scenario, availableRules));
				editorLayout.getFlexCellFormatter().setHorizontalAlignment(layoutRow, 2, HasHorizontalAlignment.ALIGN_LEFT);

			} else if (f instanceof Map) {
				HorizontalPanel h = new HorizontalPanel();
                h.add(getNewDataButton(previousEx, scenario,listExecutionTrace.get(executionTraceLine)));
				h.add(new SmallLabel(constants.GIVEN()));


				editorLayout.setWidget(layoutRow, 0, h);

				layoutRow++;
				Map facts = (Map) f;
				VerticalPanel vert = new VerticalPanel();
		        for (Iterator iterator = facts.entrySet().iterator(); iterator.hasNext();) {
		            Map.Entry e = (Map.Entry) iterator.next();
		            List factList = (List) facts.get(e.getKey());
		            if (e.getKey().equals(ScenarioHelper.RETRACT_KEY)) {
		            	vert.add(new RetractWidget(factList, scenario));
		            } else {
                        vert.add(new DataInputWidget((String)e.getKey(), factList, false, scenario, sce, this,listExecutionTrace.get(executionTraceLine)));                             
 		            }
		        }


		        if (facts.size() > 0) {
		        	editorLayout.setWidget(layoutRow, 1, vert);
		        } else {
		        	editorLayout.setWidget(layoutRow, 1, new HTML("<i><small>" + constants.AddInputDataAndExpectationsHere() + "</small></i>"));
		        }
			} else {
				List l = (List) f;
				Fixture first = (Fixture) l.get(0);
				if (first instanceof VerifyFact) {
                    doVerifyFacts(l, editorLayout, layoutRow, scenario,listExecutionTrace.get(executionTraceLine));
				} else if (first instanceof VerifyRuleFired) {
					editorLayout.setWidget(layoutRow, 1, new VerifyRulesFiredWidget(l, scenario, showResults));
				}

			}
			layoutRow++;
		}

        //add more execution sections.
		Button addExecute = new Button(constants.MoreDotDot());
		addExecute.setTitle(constants.AddAnotherSectionOfDataAndExpectations());
		addExecute.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				scenario.fixtures.add(new ExecutionTrace());
				renderEditor();
			}
		});
        editorLayout.setWidget(layoutRow, 0, addExecute);
        //layout.getFlexCellFormatter().setHorizontalAlignment(layoutRow, 1, HasHorizontalAlignment.ALIGN_CENTER);
        layoutRow++;


        editorLayout.setWidget(layoutRow, 0, new SmallLabel(constants.configuration()));
        //layoutRow++;

        //config section
        ConfigWidget conf = new ConfigWidget(scenario, asset.metaData.packageName, this);
        editorLayout.setWidget(layoutRow, 1, conf);

        layoutRow++;

        //global section
        Map globals = hlp.lumpyMapGlobals(scenario.globals);
        VerticalPanel globalPanel = new VerticalPanel();
        for (Iterator iterator = globals.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry e = (Map.Entry) iterator.next();
            globalPanel.add(new DataInputWidget((String)e.getKey(), (List) globals.get(e.getKey()), true, scenario, sce, this,previousEx));
        }
        HorizontalPanel h = new HorizontalPanel();
        h.add(getNewGlobalButton(scenario));
        h.add(new SmallLabel(constants.globals()));
        editorLayout.setWidget(layoutRow, 0, h);

        //layoutRow++;
        editorLayout.setWidget(layoutRow, 1, globalPanel);
	}




	private Widget getNewGlobalButton(final Scenario scenario) {
		Image newItem = new ImageButton("images/new_item.gif", constants.AddANewGlobalToThisScenario(), new ClickListener() {
			public void onClick(Widget w) {

				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.NewGlobal());

		        final ListBox factTypes = new ListBox();
		        for (Iterator iterator = sce.globalTypes.keySet().iterator(); iterator
						.hasNext();) {
					String g = (String) iterator.next();
					factTypes.addItem(g);
				}

		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
							String fn = factTypes.getItemText(factTypes.getSelectedIndex());
							if (scenario.isFactNameExisting(fn)) {
                                Window.alert(Format.format(constants.TheName0IsAlreadyInUsePleaseChooseAnotherName(), fn));
							} else {
								FactData ng = new FactData((String) sce.globalTypes.get(fn), fn, new ArrayList(), false);
								scenario.globals.add(ng);
								renderEditor();
								pop.hide();
							}
					}
				});

		        HorizontalPanel insertFact = new HorizontalPanel();
		        insertFact.add(factTypes); insertFact.add(add);
		        pop.addAttribute(constants.GlobalColon(), insertFact);

				pop.show();
			}
		});

		return newItem;
	}


	/**
	 * This button gives a choice of modifying data, based on the positional context.
	 * @param previousEx
	 */
	private Widget getNewDataButton(final ExecutionTrace previousEx, final Scenario scenario,final ExecutionTrace currentEx) {
		Image newItem = new ImageButton("images/new_item.gif", constants.AddANewDataInputToThisScenario(), new ClickListener() {
			public void onClick(Widget w) {

				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.NewInput());

		        final ListBox factTypes = new ListBox();
		        for (int i = 0; i < sce.factTypes.length; i++) {
		            factTypes.addItem(sce.factTypes[i]);
		        }
		        final TextBox factName = new TextBox();
		        factName.setVisibleLength(5);

		        Button add = new Button(constants.Add());
		        add.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String fn = ("" + factName.getText()).trim();
						if (fn.equals("")
								|| factName.getText().indexOf(' ') > -1) {
							Window.alert(constants.YouMustEnterAValidFactName());
						} else {
							if (scenario.isFactNameExisting(fn)) {
                                Window.alert(Format.format(constants.TheFactName0IsAlreadyInUsePleaseChooseAnotherName(), fn));
							} else {
								scenario.insertBetween(previousEx, new FactData(factTypes.getItemText(factTypes.getSelectedIndex()), factName.getText(), new ArrayList(), false ));
								renderEditor();
								pop.hide();
							}
						}
					}
				});

		        HorizontalPanel insertFact = new HorizontalPanel();
		        insertFact.add(factTypes); insertFact.add(new SmallLabel(constants.FactName())); insertFact.add(factName); insertFact.add(add);
		        pop.addAttribute(constants.InsertANewFact1(), insertFact);

		        List varsInScope = scenario.getFactNamesInScope(currentEx, false);
		        //now we do modifies & retracts
		        if (varsInScope.size() > 0) {
		        	final ListBox modifyFacts = new ListBox();
			        for (int j = 0; j < varsInScope.size(); j++) { modifyFacts.addItem((String) varsInScope.get(j));}
			        add = new Button(constants.Add());
			        add.addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							String fn = modifyFacts.getItemText(modifyFacts.getSelectedIndex());
							String type  = (String) scenario.getVariableTypes().get(fn);
							scenario.insertBetween(previousEx, new FactData(type, fn, new ArrayList(), true));
							renderEditor();
							pop.hide();
						}
					});
			        HorizontalPanel modifyFact = new HorizontalPanel();
			        modifyFact.add(modifyFacts); modifyFact.add(add);
			        pop.addAttribute(constants.ModifyAnExistingFactScenario(), modifyFact);

			        //now we do retracts
		        	final ListBox retractFacts = new ListBox();
			        for (int j = 0; j < varsInScope.size(); j++) { retractFacts.addItem((String) varsInScope.get(j));}
			        add = new Button(constants.Add());
			        add.addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							String fn = retractFacts.getItemText(retractFacts.getSelectedIndex());
							scenario.insertBetween(previousEx, new RetractFact(fn));
							renderEditor();
							pop.hide();
						}
					});
			        HorizontalPanel retractFact = new HorizontalPanel();
			        retractFact.add(retractFacts); retractFact.add(add);
			        pop.addAttribute(constants.RetractAnExistingFactScenario(), retractFact);


		        }


				pop.show();

			}
		});

		return newItem;
	}



	private Widget getNewExpectationButton(final ExecutionTrace ex,
			final Scenario sc) {

		Image add = new ImageButton("images/new_item.gif", constants.AddANewExpectation(), new ClickListener() {
			public void onClick(Widget w) {
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.NewExpectation());

				Widget selectRule = getRuleSelectionWidget(asset.metaData.packageName, new RuleSelectionEvent()  {

					public void ruleSelected(String name) {
		                VerifyRuleFired vr = new VerifyRuleFired(name, null, new Boolean(true));
		                sc.insertBetween(ex, vr);
		                renderEditor();
		                pop.hide();
					}

				});

				pop.addAttribute(constants.Rule(), selectRule);

				final ListBox facts = new ListBox();
				List names = sc.getFactNamesInScope(ex, true);
				for (Iterator iterator = names.iterator(); iterator.hasNext();) {
					facts.addItem((String) iterator.next());
				}

				Button ok = new Button(constants.Add());
				ok.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String factName = facts.getItemText(facts.getSelectedIndex());
						sc.insertBetween(ex, new VerifyFact(factName, new ArrayList()));
						renderEditor();
						pop.hide();
					}
				});



				HorizontalPanel h = new HorizontalPanel();
				h.add(facts);
				h.add(ok);
				pop.addAttribute(constants.FactValue(), h);

				//add in list box for anon facts
				final ListBox factTypes = new ListBox();
				for (int i = 0; i < sce.factTypes.length; i++) {
					String ft = sce.factTypes[i];
					factTypes.addItem(ft);
				}

				ok = new Button(constants.Add());
				ok.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String t = factTypes.getItemText(factTypes.getSelectedIndex());
						sc.insertBetween(ex, new VerifyFact(t, new ArrayList(), true));
						renderEditor();
						pop.hide();
					}

				});

				h = new HorizontalPanel();
				h.add(factTypes);
				h.add(ok);
				pop.addAttribute(constants.AnyFactThatMatches(), h);


				pop.show();
			}
		});


		return add;
	}






	private void doVerifyFacts(List l, FlexTable layout, int layoutRow, final Scenario scenario,ExecutionTrace executionTrace) {
		VerticalPanel vert = new VerticalPanel();
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			final VerifyFact f = (VerifyFact) iterator.next();
			HorizontalPanel h = new HorizontalPanel();
			h.add(new VerifyFactWidget(f, scenario, sce,executionTrace,showResults));
			Image del = new ImageButton("images/delete_item_small.gif", constants.DeleteTheExpectationForThisFact(), new ClickListener() {     //NON-NLS
				public void onClick(Widget w) {
					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisExpectation())) {
						scenario.removeFixture(f);
						renderEditor();
					}
				}
			});
			h.add(del);
			vert.add(h);
		}
		layout.setWidget(layoutRow, 1, vert);

	}

	public Widget getRuleSelectionWidget(final String packageName, final RuleSelectionEvent selected) {
	       final HorizontalPanel h = new HorizontalPanel();
	       final TextBox t = new TextBox();
	       t.setTitle(constants.EnterRuleNameScenario());
	       h.add(t);
	       if (!(availableRules == null)) {
	           final ListBox availableRulesBox = new ListBox();

	           availableRulesBox.addItem(constants.pleaseChoose1());
	           for (int i = 0; i < availableRules.length; i++) {
	               availableRulesBox.addItem(availableRules[i]);
	           }
	                      availableRulesBox.setSelectedIndex(0);
	           availableRulesBox.removeChangeListener(ruleSelectionCL);
	           ruleSelectionCL  = new ChangeListener() {
	               public void onChange(Widget w) {
	                  t.setText(availableRulesBox.getItemText(availableRulesBox.getSelectedIndex()));
	               }
	           };

	           availableRulesBox.addChangeListener(ruleSelectionCL);
	           h.add(availableRulesBox);

	       } else {

	           final Button showList = new Button(constants.showListButton());
	           h.add(showList);
	           showList.addClickListener(new ClickListener() {
	               public void onClick(Widget w) {
	                   h.remove(showList);
	                   final Image busy = new Image("images/searching.gif"); //NON-NLS
	                   final Label loading = new SmallLabel(constants.loadingList1());
	                   h.add(busy);
	                   h.add(loading);


	                   DeferredCommand.addCommand(new Command() {
	                       public void execute() {
	                          RepositoryServiceFactory.getService().listRulesInPackage(packageName, new GenericCallback<String[]>() {
	                               public void onSuccess(String[] list) {
	                                   availableRules = (list);
	                                   final ListBox availableRulesBox = new ListBox();
	                                                                     availableRulesBox.addItem(constants.pleaseChoose1());
	                                   for (int i = 0; i < list.length; i++) {
	                                       availableRulesBox.addItem(list[i]);
	                                   }
	                                   ruleSelectionCL  = new ChangeListener() {
	                                       public void onChange(Widget w) {
	                                          t.setText(availableRulesBox.getItemText(availableRulesBox.getSelectedIndex()));
	                                       }
	                                   };
	                                   availableRulesBox.addChangeListener(ruleSelectionCL);
	                                   availableRulesBox.setSelectedIndex(0);
	                                   h.add(availableRulesBox);
	                                   h.remove(busy);
	                                   h.remove(loading);
	                               }
	                           });
	                       }
	                   });


	               }
	           });

	       }

	       Button ok = new Button(constants.OK());
	       ok.addClickListener(new ClickListener() {
	           public void onClick(Widget w) {
	               selected.ruleSelected(t.getText());
	           }
	       });
	       h.add(ok);
	       return h;
	   } 

//	public static Widget editableCell(final ValueChanged changeEvent, String factType, String fieldName, String initialValue, SuggestionCompletionEngine sce) {
//		String key  = factType + "." + fieldName;
//		String flType = sce.fieldTypes.get(key);
//		if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
//			final TextBox box = editableTextBox(changeEvent, fieldName, initialValue);
//			box.addKeyboardListener(ActionValueEditor.getNumericFilter(box));
//	        return box;
//		} else if ( flType != null && flType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
//			String[] c = new String[] {"true", "false"};
//			return ConstraintValueEditor.enumDropDown(initialValue, changeEvent, DropDownData.create(c));
//		} else {
//			String[] enums = sce.dataEnumLists.get(key);
//			if (enums != null) {
//				return ConstraintValueEditor.enumDropDown(initialValue, changeEvent, DropDownData.create(enums));
//
//			} else {
//				return editableTextBox(changeEvent, fieldName, initialValue);
//			}
//		}
//
//	}
//
//	private static TextBox editableTextBox(final ValueChanged changed,  String fieldName, String initialValue) {
//		final TextBox tb = new TextBox();
//		tb.setText(initialValue);
//        String m = Format.format(((Constants) GWT.create(Constants.class)).ValueFor0(), fieldName);
//		tb.setTitle(m);
//		tb.addChangeListener(new ChangeListener() {
//		    public void onChange(Widget w) {
//		        changed.valueChanged(tb.getText());
//		    }
//		});
//
//		return tb;
//	}


	/**
	 * Use some CSS trickery to get a percent bar.
	 */
	public static Widget getBar(String colour, int width, float percent) {
		int pixels = (int) (width * (percent / 100));
		String h = "<div class=\"smallish-progress-wrapper\" style=\"width: " + width + "px\">" +
					"<div class=\"smallish-progress-bar\" style=\"width: " + pixels + "px; background-color: " + colour + ";\"></div>" +
					"<div class=\"smallish-progress-text\" style=\"width: " + width + "px\">" + (int)percent
					+ "%</div></div>"; //NON-NLS
		return new HTML(h);

	}

	public static Widget getBar(String colour, int width, int numerator, int denominator) {
		 	int percent = 0;

			if (denominator != 0) {
				percent = (int) ((((float)denominator - (float)numerator) / (float)denominator) * 100);
			}
			return getBar(colour, width, percent);
	}





}










