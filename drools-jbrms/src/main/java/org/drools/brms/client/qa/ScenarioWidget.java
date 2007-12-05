package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Fixture;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.ScenarioRunResult;

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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

public class ScenarioWidget extends Composite {


	private ListBox availableRules;
	//Scenario scenario;
	private SuggestionCompletionEngine sce;
	private ChangeListener ruleSelectionCL;
	RuleAsset asset;
	DirtyableFlexTable layout;
	boolean showResults;



	public ScenarioWidget(RuleAsset asset) {


		this.asset = asset;
		this.layout = new DirtyableFlexTable();
		this.showResults = false;


    	this.sce = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.metaData.packageName);

    	Scenario scenario = (Scenario) asset.content;
    	if (scenario.fixtures.size() == 0) {
    		scenario.fixtures.add(new ExecutionTrace());
    	}

    	layout.setWidget(0, 0, new TestRunnerWidget(this, asset.metaData.packageName));

        renderEditor();

        initWidget(layout);

        setWidth("100%");
        setHeight("100%");
        setStyleName("scenario-Viewer");
    }


	void renderEditor() {

		final Scenario scenario = (Scenario) asset.content;
		DirtyableFlexTable editorLayout = new DirtyableFlexTable();
		editorLayout.clear();
		editorLayout.setWidth("100%");
		editorLayout.setStyleName("model-builder-Background");
		this.layout.setWidget(1, 0, editorLayout);
		ScenarioHelper hlp = new ScenarioHelper();
		List fixtures = hlp.lumpyMap(scenario.fixtures);


        int layoutRow = 1;
        ExecutionTrace previousEx = null;
        for (int i = 0; i < fixtures.size(); i++) {
			Object f = fixtures.get(i);
			if (f instanceof ExecutionTrace) {
				previousEx = (ExecutionTrace) f;
				HorizontalPanel h = new HorizontalPanel();
				h.add(getNewExpectationButton(previousEx, scenario));
				h.add(new Label("EXPECT"));
				editorLayout.setWidget(layoutRow, 0, h);


				editorLayout.setWidget(layoutRow, 1, new ExecutionWidget(previousEx, showResults));
				//layout.setWidget(layoutRow, 2, getNewExpectationButton(previousEx, scenario, availableRules));
				editorLayout.getFlexCellFormatter().setHorizontalAlignment(layoutRow, 2, HasHorizontalAlignment.ALIGN_LEFT);

			} else if (f instanceof Map) {
				HorizontalPanel h = new HorizontalPanel();
				h.add(getNewDataButton(previousEx, scenario));
				h.add(new Label("GIVEN"));

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
		            	vert.add(new DataInputWidget((String)e.getKey(), factList, false, scenario, sce, this));
		            }
		        }

		        if (facts.size() > 0) {
		        	editorLayout.setWidget(layoutRow, 1, vert);
		        } else {
		        	editorLayout.setWidget(layoutRow, 1, new HTML("<i><small>Add input data and expectations here.</small></i>"));
		        }
			} else {
				List l = (List) f;
				Fixture first = (Fixture) l.get(0);
				if (first instanceof VerifyFact) {
					doVerifyFacts(l, editorLayout, layoutRow, scenario);
				} else if (first instanceof VerifyRuleFired) {
					editorLayout.setWidget(layoutRow, 1, new VerifyRulesFiredWidget(l, scenario, showResults));
				}

			}
			layoutRow++;
		}

        //add more execution sections.
		Button addExecute = new Button("More...");
		addExecute.setTitle("Add another section of data and expectations.");
		addExecute.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				scenario.fixtures.add(new ExecutionTrace());
				renderEditor();
			}
		});
        editorLayout.setWidget(layoutRow, 0, addExecute);
        //layout.getFlexCellFormatter().setHorizontalAlignment(layoutRow, 1, HasHorizontalAlignment.ALIGN_CENTER);
        layoutRow++;


        editorLayout.setWidget(layoutRow, 0, new Label("(configuration)"));
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
            globalPanel.add(new DataInputWidget((String)e.getKey(), (List) globals.get(e.getKey()), true, scenario, sce, this));
        }
        HorizontalPanel h = new HorizontalPanel();
        h.add(getNewGlobalButton(scenario));
        h.add(new Label("(globals)"));
        editorLayout.setWidget(layoutRow, 0, h);

        //layoutRow++;
        editorLayout.setWidget(layoutRow, 1, globalPanel);
	}




	private Widget getNewGlobalButton(final Scenario scenario) {
		Image newItem = new ImageButton("images/new_item.gif", "Add a new global to this scenario.", new ClickListener() {
			public void onClick(Widget w) {

				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "New global");

		        final ListBox factTypes = new ListBox();
		        for (int i = 0; i < sce.factTypes.length; i++) {
		            factTypes.addItem(sce.factTypes[i]);
		        }
		        final TextBox factName = new TextBox();
		        factName.setVisibleLength(5);

		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String fn = ("" + factName.getText()).trim();
						if (fn.equals("")
								|| factName.getText().indexOf(' ') > -1) {
							Window.alert("You must enter a valid name.");
						} else {
							if (scenario.isFactNameExisting(fn)) {
								Window.alert("The name [" + fn + "] is already in use. Please choose another name.");
							} else {
								scenario.globals.add(new FactData(factTypes.getItemText(factTypes.getSelectedIndex()), factName.getText(), new ArrayList(), false ));
								renderEditor();
								pop.hide();
							}
						}
					}
				});

		        HorizontalPanel insertFact = new HorizontalPanel();
		        insertFact.add(factTypes); insertFact.add(new Label("Fact name:")); insertFact.add(factName); insertFact.add(add);
		        pop.addAttribute("New global:", insertFact);

				pop.setPopupPosition(Window.getClientWidth()/3, w.getAbsoluteTop() );
				pop.show();
			}
		});

		return newItem;
	}


	/**
	 * This button gives a choice of modifying data, based on the positional context.
	 * @param previousEx
	 */
	private Widget getNewDataButton(final ExecutionTrace previousEx, final Scenario scenario) {
		Image newItem = new ImageButton("images/new_item.gif", "Add a new data input to this scenario.", new ClickListener() {
			public void onClick(Widget w) {

				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "New input");

		        final ListBox factTypes = new ListBox();
		        for (int i = 0; i < sce.factTypes.length; i++) {
		            factTypes.addItem(sce.factTypes[i]);
		        }
		        final TextBox factName = new TextBox();
		        factName.setVisibleLength(5);

		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String fn = ("" + factName.getText()).trim();
						if (fn.equals("")
								|| factName.getText().indexOf(' ') > -1) {
							Window.alert("You must enter a valid fact name.");
						} else {
							if (scenario.isFactNameExisting(fn)) {
								Window.alert("The fact name [" + fn + "] is already in use. Please choose another name.");
							} else {
								scenario.insertAfter(previousEx, new FactData(factTypes.getItemText(factTypes.getSelectedIndex()), factName.getText(), new ArrayList(), false ));
								renderEditor();
								pop.hide();
							}
						}
					}
				});

		        HorizontalPanel insertFact = new HorizontalPanel();
		        insertFact.add(factTypes); insertFact.add(new Label("Fact name:")); insertFact.add(factName); insertFact.add(add);
		        pop.addAttribute("Insert a new fact:", insertFact);

		        List varsInScope = scenario.getFactNamesInScope(previousEx, false);
		        //now we do modifies & retracts
		        if (varsInScope.size() > 0) {
		        	final ListBox modifyFacts = new ListBox();
			        for (int j = 0; j < varsInScope.size(); j++) { modifyFacts.addItem((String) varsInScope.get(j));}
			        add = new Button("Add");
			        add.addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							String fn = modifyFacts.getItemText(modifyFacts.getSelectedIndex());
							String type  = (String) scenario.getVariableTypes().get(fn);
							scenario.insertAfter(previousEx, new FactData(type, fn, new ArrayList(), true));
							renderEditor();
							pop.hide();
						}
					});
			        HorizontalPanel modifyFact = new HorizontalPanel();
			        modifyFact.add(modifyFacts); modifyFact.add(add);
			        pop.addAttribute("Modify an existing fact:", modifyFact);

			        //now we do retracts
		        	final ListBox retractFacts = new ListBox();
			        for (int j = 0; j < varsInScope.size(); j++) { retractFacts.addItem((String) varsInScope.get(j));}
			        add = new Button("Add");
			        add.addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							String fn = retractFacts.getItemText(retractFacts.getSelectedIndex());
							scenario.insertAfter(previousEx, new RetractFact(fn));
							renderEditor();
							pop.hide();
						}
					});
			        HorizontalPanel retractFact = new HorizontalPanel();
			        retractFact.add(retractFacts); retractFact.add(add);
			        pop.addAttribute("Retract an existing fact:", retractFact);


		        }





				pop.setPopupPosition(Window.getClientWidth()/3, w.getAbsoluteTop() );
				pop.show();

			}
		});

		return newItem;
	}



	private Widget getNewExpectationButton(final ExecutionTrace ex,
			final Scenario sc) {

		Image add = new ImageButton("images/new_item.gif", "Add a new expectation.", new ClickListener() {
			public void onClick(Widget w) {
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "New expectation");

				Widget selectRule = getRuleSelectionWidget(asset.metaData.packageName, new RuleSelectionEvent()  {

					public void ruleSelected(String name) {
		                VerifyRuleFired vr = new VerifyRuleFired(name, null, new Boolean(true));
		                sc.insertAfter(ex, vr);
		                renderEditor();
		                pop.hide();
					}

				});

				pop.addAttribute("Rule:", selectRule);

				final ListBox facts = new ListBox();
				List names = sc.getFactNamesInScope(ex, true);
				for (Iterator iterator = names.iterator(); iterator.hasNext();) {
					facts.addItem((String) iterator.next());
				}

				Button ok = new Button("Add");
				ok.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String factName = facts.getItemText(facts.getSelectedIndex());
						sc.insertAfter(ex, new VerifyFact(factName, new ArrayList()));
						renderEditor();
						pop.hide();
					}
				});

				HorizontalPanel h = new HorizontalPanel();
				h.add(facts);
				h.add(ok);
				pop.addAttribute("Fact value:", h);

				pop.setPopupPosition(Window.getClientWidth()/3, w.getAbsoluteTop() );
				pop.show();
			}
		});


		return add;
	}






	private void doVerifyFacts(List l, FlexTable layout, int layoutRow, final Scenario scenario) {
		VerticalPanel vert = new VerticalPanel();
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			final VerifyFact f = (VerifyFact) iterator.next();
			HorizontalPanel h = new HorizontalPanel();
			h.add(new VerifyFactWidget(f, scenario, sce, showResults));
			Image del = new ImageButton("images/delete_item_small.gif", "Delete the expectation for this fact.", new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm("Are you sure you want to remove this expectation?")) {
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
		t.setTitle("Enter name of rule, or pick from a list. If there are a very large number of rules, you will need to type in the name.");
		h.add(t);
		if (!(availableRules == null)) {
			availableRules.setSelectedIndex(0);
			availableRules.removeChangeListener(ruleSelectionCL);
			ruleSelectionCL  = new ChangeListener() {
				public void onChange(Widget w) {
					t.setText(availableRules.getItemText(availableRules.getSelectedIndex()));
				}
			};

			availableRules.addChangeListener(ruleSelectionCL);
			h.add(availableRules);

		} else {

			final Button showList = new Button("(show list)");
			h.add(showList);
			showList.addClickListener(new ClickListener() {
				public void onClick(Widget w) {
					h.remove(showList);
					final Image busy = new Image("images/searching.gif");
					final Label loading = new Label("(loading list)");
					h.add(busy);
					h.add(loading);


					DeferredCommand.addCommand(new Command() {
						public void execute() {
							RepositoryServiceFactory.getService().listRulesInPackage(packageName, new GenericCallback() {
								public void onSuccess(Object data) {
									String[] list = (String[]) data;
									availableRules = new ListBox();
									availableRules.addItem("-- please choose --");
									for (int i = 0; i < list.length; i++) {
										availableRules.addItem(list[i]);
									}
									ruleSelectionCL  = new ChangeListener() {
										public void onChange(Widget w) {
											t.setText(availableRules.getItemText(availableRules.getSelectedIndex()));
										}
									};
									availableRules.addChangeListener(ruleSelectionCL);
									availableRules.setSelectedIndex(0);
									h.add(availableRules);
									h.remove(busy);
									h.remove(loading);
								}
							});
						}
					});


				}
			});

		}

		Button ok = new Button("OK");
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				selected.ruleSelected(t.getText());
			}
		});
		h.add(ok);
		return h;
	}


}



interface RuleSelectionEvent {
	public void ruleSelected(String name);
}

/**
 * For capturing input for all the facts of a given type.
 * @author Michael Neale
 */
class DataInputWidget extends Composite {


    private Grid outer;
	private Scenario scenario;
	private SuggestionCompletionEngine sce;
	private String type;
	private ScenarioWidget parent;



	public DataInputWidget(String factType, List defList, boolean isGlobal, Scenario sc, SuggestionCompletionEngine sce, ScenarioWidget parent) {

        outer = new Grid(2, 1);
        scenario = sc;
        this.sce = sce;
        this.type = factType;
        this.parent = parent;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");


        if (isGlobal) {
            outer.setWidget(0, 0, getLabel("global [" + factType + "]", defList));
        } else {
            FactData first = (FactData) defList.get(0);
            if (first.isModify) {
            	outer.setWidget(0, 0,  getLabel("modify [" + factType + "]", defList));
            } else {
            	outer.setWidget(0, 0, getLabel("insert [" + factType + "]", defList));
            }
        }

        FlexTable t = render(defList);

        outer.setWidget(1, 0, t);
        initWidget(outer);
    }

	private Widget getLabel(String text, final List defList) {
        //now we put in button to add new fields
        //Image newField = new ImageButton("images/add_field_to_fact.gif", "Add a field.");
        Image newField = getNewFieldButton(defList);

        HorizontalPanel h = new HorizontalPanel();
        h.add(new Label(text)); h.add(newField);
        return h;
	}

	private Image getNewFieldButton(final List defList) {
		Image newField = new ImageButton("images/add_field_to_fact.gif", "Add a field");
        newField.addClickListener(addFieldCL(defList));
		return newField;
	}

	private ClickListener addFieldCL(final List defList) {
		return new ClickListener() {
			public void onClick(Widget w) {

				//build up a list of what we have got, don't want to add it twice
				HashSet existingFields = new HashSet();
				if (defList.size() > 0) {
					FactData d = (FactData) defList.get(0);
					for (Iterator iterator = d.fieldData.iterator(); iterator.hasNext();) {
						FieldData f = (FieldData) iterator.next();
						existingFields.add(f.name);
					}

				}
				String[] fields = (String[]) sce.fieldsForType.get(type);
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "Choose a field to add");
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					String fld = fields[i];
					if (!existingFields.contains(fld)) b.addItem(fld);
				}
				pop.addRow(b);
				Button ok = new Button("OK");
				ok.addClickListener(new ClickListener() {
									public void onClick(Widget w) {
										String f = b.getItemText(b.getSelectedIndex());
										for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
											FactData fd = (FactData) iterator.next();
											fd.fieldData.add(new FieldData(f, ""));
										}
								        outer.setWidget(1, 0, render(defList));
								        pop.hide();
									}
								});
				pop.addRow(ok);
				pop.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
				pop.show();
			}
		};
	}

	private FlexTable render(final List defList) {
		DirtyableFlexTable t = new DirtyableFlexTable();
		if (defList.size() == 0) {
			parent.renderEditor();
		}

		//This will work out what row is for what field, addin labels and remove icons

        Map fields = new HashMap();
        int col = 0;
        int totalCols = defList.size();
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            final FactData d = (FactData) iterator.next();

            for (int i = 0; i < d.fieldData.size(); i++) {
                final FieldData fd = (FieldData) d.fieldData.get(i);
                if (!fields.containsKey(fd.name)) {
                    int idx = fields.size() + 1;
                    fields.put(fd.name, new Integer(idx));
                    t.setWidget(idx, 0, new Label(fd.name + ":"));
                    Image del = new ImageButton("images/delete_item_small.gif", "Remove this row.", new ClickListener() {
        				public void onClick(Widget w) {
        					if (Window.confirm("Are you sure you want to remove this row ?")) {
        						ScenarioHelper.removeFields(defList, fd.name);
        						outer.setWidget(1, 0, render(defList));

        					}
        				}
        			});
                    t.setWidget(idx, totalCols + 1, del);
                    t.getCellFormatter().setHorizontalAlignment(idx, 0, HasHorizontalAlignment.ALIGN_RIGHT);
                }
            }
        }

        int totalRows = fields.size();

        t.getFlexCellFormatter().setHorizontalAlignment(totalRows + 1, 0, HasHorizontalAlignment.ALIGN_RIGHT);

        //now we go through the facts and the fields, adding them to the grid
        //if a fact is missing a FieldData, we will add it in (so people can enter data later on)
        col = 0;
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            final FactData d = (FactData) iterator.next();
            t.setWidget(0, ++col, new Label("[" + d.name + "]"));
            Image del = new ImageButton("images/delete_item_small.gif", "Remove the column for [" + d.name + "]", new ClickListener() {
				public void onClick(Widget w) {
					if (scenario.isFactNameUsed(d)) {
						Window.alert("Can't remove this column as the name [" + d.name + "] is being used.");
					} else if (Window.confirm("Are you sure you want to remove this column ?")) {
						scenario.removeFixture(d);
						defList.remove(d);
						outer.setWidget(1, 0, render(defList));
					}
				}
			});
            t.setWidget(totalRows + 1, col, del);
            Map presentFields = new HashMap(fields);
            for (int i = 0; i < d.fieldData.size(); i++) {
                FieldData fd = (FieldData) d.fieldData.get(i);
                int fldRow = ((Integer) fields.get(fd.name)).intValue();
                t.setWidget(fldRow, col, editableCell(fd));
                presentFields.remove(fd.name);
            }

            for (Iterator missing = presentFields.entrySet().iterator(); missing.hasNext();) {
                Map.Entry e = (Map.Entry) missing.next();
                int fldRow = ((Integer) e.getValue()).intValue();
                FieldData fd = new FieldData((String) e.getKey(), "");
                d.fieldData.add(fd);
                t.setWidget(fldRow, col, editableCell(fd));
            }
        }

        if (fields.size() == 0) {
        	//HorizontalPanel h = new HorizontalPanel();
        	Button b = new Button("Add a field");
        	b.addClickListener(addFieldCL(defList));

        	//h.add(new HTML("<i><small>Add fields:</small></i>"));
        	//h.add(getNewFieldButton(defList));
        	t.setWidget(1, 1, b);
        }
        return t;
	}



	private Widget editableCell(final FieldData fd) {
        final TextBox tb = new TextBox();
        tb.setText(fd.value);
        tb.setTitle("Value for: " + fd.name);
        tb.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                fd.value = tb.getText();
            }
        });

        //add stuff to check for numerics
        return tb;
    }




}

class ConfigWidget extends Composite {
    public ConfigWidget(final Scenario sc, final String packageName, final ScenarioWidget scWidget) {

        final ListBox box = new ListBox(true);

        for (int i = 0; i < sc.rules.size(); i++) {
            box.addItem((String)sc.rules.get(i));
        }
        HorizontalPanel filter = new HorizontalPanel();

        final Image add = new ImageButton("images/new_item.gif", "Add a new rule.");
        add.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showRulePopup(w, box, packageName, sc.rules, scWidget);
            }
        });

        final Image remove = new ImageButton("images/trash.gif", "Remove selected rule.");
        remove.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                if (box.getSelectedIndex() == -1) {
                    Window.alert("Please choose a rule to remove.");
                } else {
                    String r = box.getItemText(box.getSelectedIndex());
                    sc.rules.remove(r);
                    box.removeItem(box.getSelectedIndex());
                }
            }
        });
        VerticalPanel actions = new VerticalPanel();
        actions.add(add); actions.add(remove);




        final ListBox drop = new ListBox();
        drop.addItem("Allow these rules to fire:", "inc");
        drop.addItem("Prevent these rules from firing:", "exc");
        drop.addItem("All rules may fire");
        drop.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                String s = drop.getValue(drop.getSelectedIndex());
                if (s.equals("inc")) {
                    sc.inclusive = true;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else if (s.equals("exc")) {
                    sc.inclusive = false;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else {
                    sc.rules.clear();
                    box.clear();
                    box.setVisible(false); add.setVisible(false); remove.setVisible(false);
                }
            }
        });

        if (sc.rules.size() > 0) {
        	drop.setSelectedIndex((sc.inclusive) ? 0 : 1);
        } else {
        	drop.setSelectedIndex(2);
        	box.setVisible(false); add.setVisible(false); remove.setVisible(false);
        }


        filter.add(drop);
        filter.add(box);
        filter.add(actions);

        initWidget(filter);
    }

    private void showRulePopup(Widget w, final ListBox box, String packageName, final List filterList, ScenarioWidget scw) {
        final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "Select rule");

        Widget ruleSelector = scw.getRuleSelectionWidget(packageName, new RuleSelectionEvent() {
			public void ruleSelected(String r) {
                filterList.add(r);
                box.addItem(r);
                pop.hide();

			}
        });

        pop.addRow(ruleSelector);

        pop.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
        pop.show();

    }


}

class ExecutionWidget extends Composite {
    public ExecutionWidget(final ExecutionTrace ext, boolean showResults) {


    	final Widget dt = simulDate(ext);
    	dt.setVisible(ext.scenarioSimulatedDate != null);

    	final ListBox choice = new ListBox();
    	choice.addItem("Use real date and time");
    	choice.addItem("Use a simulated date and time");
    	choice.setSelectedIndex((ext.scenarioSimulatedDate == null) ? 0 : 1);
    	choice.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				if (choice.getSelectedIndex() == 0) {
					dt.setVisible( false );
					ext.scenarioSimulatedDate = null;
				} else {
					dt.setVisible(true);
				}
			}
		});

    	HorizontalPanel p = new HorizontalPanel();
    	p.add(new Image("images/execution_trace.gif"));
    	p.add(choice);
    	p.add(dt);

    	VerticalPanel vert = new VerticalPanel();
    	if (showResults && ext.executionTimeResult != null
    			&& ext.numberOfRulesFired != null) {
    		HTML rep = new HTML("<i><small>" + ext.numberOfRulesFired.longValue() + " rules fired in " + ext.executionTimeResult.longValue() + "ms.</small></i>");
    		vert.add(p);
    		vert.add(rep);
    		initWidget(vert);
    	} else {
    		initWidget(p);
    	}
    }



    private Widget simulDate(final ExecutionTrace ext) {
    	HorizontalPanel ab = new HorizontalPanel();
        final String fmt = "dd-MMM-YYYY";
        final TextBox dt = new TextBox();
        if (ext.scenarioSimulatedDate == null) {
            dt.setText("<" + fmt + ">");
        } else {
            dt.setText(ext.scenarioSimulatedDate.toLocaleString());
        }
        final Label dateHint = new Label();
        dt.addKeyboardListener(new KeyboardListener() {
			public void onKeyDown(Widget arg0, char arg1, int arg2) {}
			public void onKeyPress(Widget arg0, char arg1, int arg2) {}
			public void onKeyUp(Widget w, char arg1, int arg2) {
				try {
					Date d = new Date(dt.getText());
					dateHint.setText(d.toLocaleString());
				} catch (Exception e) {
					dateHint.setText("...");
				}
			}
        });

        dt.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                if (dt.getText().trim().equals("")) {
                    dt.setText("<current date and time>");
                } else {
                    try {
                        Date d = new Date(dt.getText());
                        ext.scenarioSimulatedDate = d;
                        dt.setText(d.toLocaleString());
                        dateHint.setText("");
                    } catch (Exception e) {
                        ErrorPopup.showMessage("Bad date format - please try again (try the format of " + fmt + ").");
                    }
                }
            }
        });
        ab.add(dt);
        ab.add(dateHint);
        return ab;
    }


}

class VerifyFactWidget extends Composite {
    private Grid outer;
	private boolean showResults;

	public VerifyFactWidget(final VerifyFact vf, final Scenario sc, final SuggestionCompletionEngine sce, boolean showResults) {
        outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");
        HorizontalPanel ab = new HorizontalPanel();
        final String type = (String) sc.getVariableTypes().get(vf.name);
        ab.add(new Label(type + " [" + vf.name + "] has values:"));
        this.showResults = showResults;

        Image add = new ImageButton("images/add_field_to_fact.gif", "Add a field to this expectation.", new ClickListener() {
			public void onClick(Widget w) {

				String[] fields = (String[]) sce.fieldsForType.get(type);
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "Choose a field to add");
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					b.addItem(fields[i]);
				}
				pop.addRow(b);
				Button ok = new Button("OK");
				ok.addClickListener(new ClickListener() {
									public void onClick(Widget w) {
										String f = b.getItemText(b.getSelectedIndex());
										vf.fieldValues.add(new VerifyField(f, "", "=="));
								        FlexTable data = render(vf);
								        outer.setWidget(1, 0, data);
								        pop.hide();
									}
								});
				pop.addRow(ok);
				pop.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
				pop.show();

			}
		});

        ab.add(add);
        outer.setWidget(0, 0, ab);
        initWidget(outer);

        FlexTable data = render(vf);
        outer.setWidget(1, 0, data);

    }

	private FlexTable render(final VerifyFact vf) {
		FlexTable data = new FlexTable();
        for (int i = 0; i < vf.fieldValues.size(); i++) {
            final VerifyField fld = (VerifyField) vf.fieldValues.get(i);
            data.setWidget(i, 1, new Label(fld.fieldName + ":"));
            data.getFlexCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            final ListBox opr = new ListBox();
            opr.addItem("equals", "==");
            opr.addItem("does not equal", "!=");
            if (fld.operator.equals("==")) {
                opr.setSelectedIndex(0);
            } else {
                opr.setSelectedIndex(1);
            }
            opr.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    fld.operator = opr.getValue(opr.getSelectedIndex());
                }
            });

            data.setWidget(i, 2, opr);

            final TextBox input = new TextBox();
            input.setText(fld.expected);
            input.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    fld.expected = input.getText();
                }
            });
            data.setWidget(i, 3, input);

            Image del = new ImageButton("images/delete_item_small.gif", "Remove this field expectation.", new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm("Are you sure you want to remove this field expectation?")) {
						vf.fieldValues.remove(fld);
				        FlexTable data = render(vf);
				        outer.setWidget(1, 0, data);
					}
				}
			});
            data.setWidget(i, 4, del);

            if (showResults && fld.successResult != null) {
            	if (fld.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif"));
            		data.setWidget(i, 5, new HTML("(Actual: " + fld.actualResult + ")"));

            		data.getCellFormatter().addStyleName(i, 5, "testErrorValue");

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png"));
            	}
            }



        }
		return data;
	}

}

class VerifyRulesFiredWidget extends Composite {
    private Grid outer;
    private boolean showResults;
	/**
     * @param rfl List<VeryfyRuleFired>
     * @param rules = the list of rules to choose from
     * @param scenario = the scenario to add/remove from
     */
    public VerifyRulesFiredWidget(final List rfl, final Scenario scenario, boolean showResults) {
        outer = new Grid(2, 1);
        this.showResults = showResults;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");

        outer.setWidget(0, 0, new Label("Expect rules"));
        initWidget(outer);

        FlexTable data = render(rfl, scenario);
        outer.setWidget(1, 0, data);
    }



	private FlexTable render(final List rfl, final Scenario sc) {
		FlexTable data = new DirtyableFlexTable();


        for (int i = 0; i < rfl.size(); i++) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get(i);

            if (showResults && v.successResult != null) {
            	if (v.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif"));
            		data.setWidget(i, 4, new HTML("(Actual: " + v.actualResult +")"));

            		data.getCellFormatter().addStyleName(i, 4, "testErrorValue");

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png"));
            	}

            }
            data.setWidget(i, 1, new Label(v.ruleName + ":"));
            data.getFlexCellFormatter().setAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);


            final ListBox b = new ListBox();
            b.addItem("fired at least once", "y");
            b.addItem("did not fire", "n");
            b.addItem("fired this many times: ", "e");
            final TextBox num = new TextBox();
            num.setVisibleLength(5);

            if (v.expectedFire != null ) {
                b.setSelectedIndex((v.expectedFire.booleanValue()) ? 0 : 1);
                num.setVisible(false);
            } else {
                b.setSelectedIndex(2);
                String xc = (v.expectedCount != null)? "" + v.expectedCount.intValue() : "0";
                num.setText(xc);
            }

            b.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    String s = b.getValue(b.getSelectedIndex());
                    if (s.equals("y") || s.equals("n")) {
                        num.setVisible(false);
                        v.expectedCount = null;
                    } else {
                        num.setVisible(true);
                        v.expectedFire = null;
                        num.setText("1"); v.expectedCount = new Integer(1);
                    }
                }
            });

            num.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    v.expectedCount = new Integer(num.getText());
                }
            });

            HorizontalPanel h = new HorizontalPanel();
            h.add(b); h.add(num);
            data.setWidget(i, 2, h);

            Image del = new ImageButton("images/delete_item_small.gif", "Remove this rule expectation.", new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm("Are you sure you want to remove this rule expectation?")) {
						rfl.remove(v);
						sc.removeFixture(v);
						outer.setWidget(1, 0, render(rfl, sc));
					}
				}
			});

            data.setWidget(i, 3, del);



            //we only want numbers here...
            num.addKeyboardListener(new KeyboardListener() {
                    public void onKeyDown(Widget arg0, char arg1, int arg2) {}
                    public void onKeyPress(Widget w, char c, int i) {
                        if (Character.isLetter( c ) ) {
                            ((TextBox) w).cancelKey();
                        }
                    }
                    public void onKeyUp(Widget arg0, char arg1, int arg2) {}
                } );
        }
		return data;
	}
}

class RetractWidget extends Composite {
	public RetractWidget(List retList, Scenario sc) {
        FlexTable outer = new FlexTable();
        render(retList, outer, sc);

        initWidget(outer);
	}

	private void render(final List retList, final FlexTable outer, final Scenario sc) {
		outer.clear();
		outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");
        outer.setWidget(0, 0, new Label("Retract facts"));
        outer.getFlexCellFormatter().setColSpan(0, 0, 2);

        int row = 1;
        for (Iterator iterator = retList.iterator(); iterator.hasNext();) {
			final RetractFact r = (RetractFact) iterator.next();
			outer.setWidget(row, 0, new Label(r.name));
			Image del = new ImageButton("images/delete_item_small.gif", "Remove this retract statement.", new ClickListener() {
				public void onClick(Widget w) {
					retList.remove(r);
					sc.fixtures.remove(r);
					render(retList, outer, sc);
				}
			});
			outer.setWidget(row, 1, del);

			row++;
		}
	}
}

/**
 * Runs the test, plus shows a summary view of the results.
 */
class TestRunnerWidget extends Composite {

	FlexTable results = new FlexTable();
	Grid layout = new Grid(2, 1);

	private HorizontalPanel busy = new HorizontalPanel();
	private HorizontalPanel actions = new HorizontalPanel();

	public TestRunnerWidget(final ScenarioWidget parent, final String packageName) {

		final Button run = new Button("Run scenario");
		run.setTitle("Run this scenario. This will build the package if it is not already built (which may take some time).");
		run.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				layout.setWidget(0, 0, busy);
				RepositoryServiceFactory.getService().runScenario(parent.asset.metaData.packageName, (Scenario) parent.asset.content, new GenericCallback () {
					public void onSuccess(Object data) {
						layout.setWidget(0, 0, actions);
						layout.setWidget(0, 1, results);
						busy.setVisible(false);
						actions.setVisible(true);
						ScenarioRunResult result = (ScenarioRunResult) data;
						if (result.errors != null) {
							showErrors(result.errors);
						} else {
							showResults(parent, result);
						}
					}
				});
			}
		});

		actions.add(run);
		busy.add(new Image("images/busy.gif"));
		busy.add(new HTML("&nbsp;&nbsp;<i><small>Building and running scenario, please wait...</small></i>"));
		layout.setWidget(0, 0, actions);
		//layout.add(results);

		initWidget(layout);
	}


	private void showErrors(BuilderResult[] rs) {
		results.clear();
		results.setVisible(true);

        FlexTable errTable = new FlexTable();
        errTable.setStyleName( "build-Results" );
        for ( int i = 0; i < rs.length; i++ ) {
            int row = i;
            final BuilderResult res = rs[i];
            errTable.setWidget( row, 0, new Image("images/error.gif"));
            if( res.assetFormat.equals( "package" )) {
                errTable.setText( row, 1, "[package configuration problem] " + res.message );
            } else {
                errTable.setText( row, 1, "[" + res.assetName + "] " + res.message );
            }

        }
        ScrollPanel scroll = new ScrollPanel(errTable);

        scroll.setWidth( "100%" );
        results.setWidget(0, 0, scroll);

	}

	private void showResults(final ScenarioWidget parent,
			ScenarioRunResult result) {
		results.clear();
		results.setVisible(true);

		parent.asset.content = result.scenario;
		parent.showResults = true;
		parent.renderEditor();

		int failures = 0;
		int total = 0;
		VerticalPanel resultsDetail = new VerticalPanel();

		for (Iterator iterator = result.scenario.fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof VerifyRuleFired) {
				total++;
				VerifyRuleFired vr = (VerifyRuleFired)f;
				HorizontalPanel h = new HorizontalPanel();
				if (!vr.successResult.booleanValue()) {
					h.add(new Image("images/warning.gif"));
					failures++;
				} else {
					h.add(new Image("images/test_passed.png"));
				}
				h.add(new Label(vr.explanation));
				resultsDetail.add(h);
			} else if (f instanceof VerifyFact) {
				VerifyFact vf = (VerifyFact)f;
				for (Iterator it = vf.fieldValues.iterator(); it.hasNext();) {
					total++;
					VerifyField vfl = (VerifyField) it.next();
					HorizontalPanel h = new HorizontalPanel();
					if (!vfl.successResult.booleanValue()) {
						h.add(new Image("images/warning.gif"));
						failures++;
					} else {
						h.add(new Image("images/test_passed.png"));
					}
					h.add(new Label(vfl.explanation));
					resultsDetail.add(h);
				}

			}

		}

		results.setWidget(0, 0, new Label("Results:"));
		results.setWidget(0, 1, greenBarGoodness(failures, total));
		results.setWidget(1, 0, new Label("Summary:"));
		results.setWidget(1, 1, resultsDetail);


	}

	private Widget greenBarGoodness(float failures, float total) {
		Grid g = new Grid(1, 100);
		g.setStyleName("testBar");
		CellFormatter cf = g.getCellFormatter();
		float num = ((total - failures) / total) * 50;
		for (int i = 0; i < 50; i++) {
			if (i < num) {
				cf.setStyleName(0, i, "testSuccessBackground");
			} else {
				cf.setStyleName(0, i, "testFailureBackground");
			}
		}
		VerticalPanel vert = new VerticalPanel();

		int percent = (int) (((total - failures) / total) * 100);
		Widget p = new HTML("<i><small>" + (int)failures + " out of " + (int)total + " expectations were met. (" + percent + "%) </small></i>");
		vert.add(p);
		vert.add(g);

		vert.setStyleName("successBar");
		return vert;
	}





}
