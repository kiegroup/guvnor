package org.drools.guvnor.client.qa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
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

	private ListBox availableRules;
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


        int layoutRow = 1;
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

				editorLayout.setWidget(layoutRow, 1, new ExecutionWidget(previousEx, showResults));
				//layout.setWidget(layoutRow, 2, getNewExpectationButton(previousEx, scenario, availableRules));
				editorLayout.getFlexCellFormatter().setHorizontalAlignment(layoutRow, 2, HasHorizontalAlignment.ALIGN_LEFT);

			} else if (f instanceof Map) {
				HorizontalPanel h = new HorizontalPanel();
				h.add(getNewDataButton(previousEx, scenario));
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
		            	vert.add(new DataInputWidget((String)e.getKey(), factList, false, scenario, sce, this));
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
					doVerifyFacts(l, editorLayout, layoutRow, scenario);
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
            globalPanel.add(new DataInputWidget((String)e.getKey(), (List) globals.get(e.getKey()), true, scenario, sce, this));
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
	private Widget getNewDataButton(final ExecutionTrace previousEx, final Scenario scenario) {
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

		        List varsInScope = scenario.getFactNamesInScope(previousEx, false);
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






	private void doVerifyFacts(List l, FlexTable layout, int layoutRow, final Scenario scenario) {
		VerticalPanel vert = new VerticalPanel();
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			final VerifyFact f = (VerifyFact) iterator.next();
			HorizontalPanel h = new HorizontalPanel();
			h.add(new VerifyFactWidget(f, scenario, sce, showResults));
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
									availableRules = new ListBox();
									availableRules.addItem(constants.pleaseChoose1());
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

		Button ok = new Button(constants.OK());
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				selected.ruleSelected(t.getText());
			}
		});
		h.add(ok);
		return h;
	}

	public static Widget editableCell(final ValueChanged changeEvent, String factType, String fieldName, String initialValue, SuggestionCompletionEngine sce) {
		String key  = factType + "." + fieldName;
		String flType = sce.fieldTypes.get(key);
		if (flType.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
			final TextBox box = editableTextBox(changeEvent, fieldName, initialValue);
			box.addKeyboardListener(ActionValueEditor.getNumericFilter(box));
	        return box;
		} else if (flType.equals(SuggestionCompletionEngine.TYPE_BOOLEAN )) {
			String[] c = new String[] {"true", "false"};
			return ConstraintValueEditor.enumDropDown(initialValue, changeEvent, DropDownData.create(c));
		} else {
			String[] enums = sce.dataEnumLists.get(key);
			if (enums != null) {
				return ConstraintValueEditor.enumDropDown(initialValue, changeEvent, DropDownData.create(enums));

			} else {
				return editableTextBox(changeEvent, fieldName, initialValue);
			}
		}

	}

	private static TextBox editableTextBox(final ValueChanged changed,  String fieldName, String initialValue) {
		final TextBox tb = new TextBox();
		tb.setText(initialValue);
        String m = Format.format(((Constants) GWT.create(Constants.class)).ValueFor0(), fieldName);
		tb.setTitle(m);
		tb.addChangeListener(new ChangeListener() {
		    public void onChange(Widget w) {
		        changed.valueChanged(tb.getText());
		    }
		});

		return tb;
	}


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



interface RuleSelectionEvent {
	public void ruleSelected(String name);
}

/**
 * For capturing input for all the facts of a given type.
 * @author Michael Neale
 */
class DataInputWidget extends DirtyableComposite {


    private Grid outer;
	private Scenario scenario;
	private SuggestionCompletionEngine sce;
	private String type;
	private ScenarioWidget parent;
    private Constants constants = ((Constants) GWT.create(Constants.class));


    public DataInputWidget(String factType, List defList, boolean isGlobal, Scenario sc, SuggestionCompletionEngine sce, ScenarioWidget parent) {

        outer = new Grid(2, 1);
        scenario = sc;
        this.sce = sce;
        this.type = factType;

        this.parent = parent;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget"); //NON-NLS


        if (isGlobal) {
            outer.setWidget(0, 0, getLabel(Format.format(constants.globalForScenario(), factType), defList));
        } else {
            FactData first = (FactData) defList.get(0);
            if (first.isModify) {
                outer.setWidget(0, 0,  getLabel(Format.format(constants.modifyForScenario(), factType), defList));
            } else {
                outer.setWidget(0, 0, getLabel(Format.format(constants.insertForScenario(), factType), defList));
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
        h.add(new SmallLabel(text)); h.add(newField);
        return h;
	}

	private Image getNewFieldButton(final List defList) {
		Image newField = new ImageButton("images/add_field_to_fact.gif", constants.AddAField()); //NON-NLS
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
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.ChooseAFieldToAdd()); //NON-NLS
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					String fld = fields[i];
					if (!existingFields.contains(fld)) b.addItem(fld);
				}
				pop.addRow(b);
				Button ok = new Button(constants.OK());
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
                final FieldData fd = d.fieldData.get(i);
                if (!fields.containsKey(fd.name)) {
                    int idx = fields.size() + 1;
                    fields.put(fd.name, new Integer(idx));
                    t.setWidget(idx, 0, new SmallLabel(fd.name + ":"));
                    Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisRow(), new ClickListener() {
        				public void onClick(Widget w) {
        					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisRow())) {
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
            t.setWidget(0, ++col, new SmallLabel("[" + d.name + "]"));
            Image del = new ImageButton("images/delete_item_small.gif", Format.format(constants.RemoveTheColumnForScenario(), d.name), new ClickListener() {
				public void onClick(Widget w) {
					if (scenario.isFactNameUsed(d)) {
                        Window.alert(Format.format(constants.CanTRemoveThisColumnAsTheName0IsBeingUsed(), d.name));
					} else if (Window.confirm(constants.AreYouSureYouWantToRemoveThisColumn())) {
						scenario.removeFixture(d);
						defList.remove(d);
						outer.setWidget(1, 0, render(defList));
					}
				}
			});
            t.setWidget(totalRows + 1, col, del);
            Map presentFields = new HashMap(fields);
            for (int i = 0; i < d.fieldData.size(); i++) {
                FieldData fd = d.fieldData.get(i);
                int fldRow = ((Integer) fields.get(fd.name)).intValue();
                t.setWidget(fldRow, col, editableCell(fd, d.type));
                presentFields.remove(fd.name);
            }

            for (Iterator missing = presentFields.entrySet().iterator(); missing.hasNext();) {
                Map.Entry e = (Map.Entry) missing.next();
                int fldRow = ((Integer) e.getValue()).intValue();
                FieldData fd = new FieldData((String) e.getKey(), "");
                d.fieldData.add(fd);
                t.setWidget(fldRow, col, editableCell(fd, d.type));
            }
        }

        if (fields.size() == 0) {
        	//HorizontalPanel h = new HorizontalPanel();
        	Button b = new Button(constants.AddAField());
        	b.addClickListener(addFieldCL(defList));

        	//h.add(new HTML("<i><small>Add fields:</small></i>"));
        	//h.add(getNewFieldButton(defList));
        	t.setWidget(1, 1, b);
        }
        return t;
	}


	/**
	 * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
	 * @param fd
	 * @param factType
	 * @return
	 */
	private Widget editableCell(final FieldData fd, String factType) {

		return ScenarioWidget.editableCell(new ValueChanged() {
			public void valueChanged(String newValue) {
				fd.value = newValue;
				makeDirty();
			}
		}, factType, fd.name, fd.value, sce);
    }








}

class ConfigWidget extends Composite {
    private final Constants constants = ((Constants) GWT.create(Constants.class));

    public ConfigWidget(final Scenario sc, final String packageName, final ScenarioWidget scWidget) {

        final ListBox box = new ListBox(true);

        for (int i = 0; i < sc.rules.size(); i++) {
            box.addItem((String)sc.rules.get(i));
        }
        HorizontalPanel filter = new HorizontalPanel();


        final Image add = new ImageButton("images/new_item.gif", constants.AddANewRule());
        add.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showRulePopup(w, box, packageName, sc.rules, scWidget);
            }
        });

        final Image remove = new ImageButton("images/trash.gif", constants.RemoveSelectedRule());
        remove.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                if (box.getSelectedIndex() == -1) {
                    Window.alert(constants.PleaseChooseARuleToRemove());
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
        drop.addItem(constants.AllowTheseRulesToFire(), "inc"); //NON-NLS
        drop.addItem(constants.PreventTheseRulesFromFiring(), "exc");    //NON-NLS
        drop.addItem(constants.AllRulesMayFire());
        drop.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                String s = drop.getValue(drop.getSelectedIndex());
                if (s.equals("inc")) {   //NON-NLS
                    sc.inclusive = true;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else if (s.equals("exc")) {     //NON-NLS
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
        final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.SelectRule()); //NON-NLS

        Widget ruleSelector = scw.getRuleSelectionWidget(packageName, new RuleSelectionEvent() {
			public void ruleSelected(String r) {
                filterList.add(r);
                box.addItem(r);
                pop.hide();

			}
        });

        pop.addRow(ruleSelector);

        pop.show();

    }


}

class ExecutionWidget extends Composite {
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public ExecutionWidget(final ExecutionTrace ext, boolean showResults) {


    	final Widget dt = simulDate(ext);
    	dt.setVisible(ext.scenarioSimulatedDate != null);

    	final ListBox choice = new ListBox();

        choice.addItem(constants.UseRealDateAndTime());
    	choice.addItem(constants.UseASimulatedDateAndTime());
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
    	p.add(new Image("images/execution_trace.gif"));   //NON-NLS
    	p.add(choice);
    	p.add(dt);

    	VerticalPanel vert = new VerticalPanel();
    	if (showResults && ext.executionTimeResult != null
    			&& ext.numberOfRulesFired != null) {
            HTML rep = new HTML("<i><small>" + Format.format(constants.property0RulesFiredIn1Ms(), ext.numberOfRulesFired.toString(), ext.executionTimeResult.toString()) + "</small></i>");


    		final HorizontalPanel h = new HorizontalPanel();
    		h.add(rep);
    		vert.add(h);

    		final Button show = new Button(constants.ShowRulesFired());
    		show.addClickListener(new ClickListener() {
				public void onClick(Widget w) {
					ListBox rules = new ListBox(true);
					for (int i = 0; i < ext.rulesFired.length; i++) {
						rules.addItem(ext.rulesFired[i]);
					}
					h.add(new SmallLabel("&nbsp:" + constants.RulesFired()));
					h.add(rules);
					show.setVisible(false);
				}
    		});
    		h.add(show);


    		vert.add(p);
    		initWidget(vert);
    	} else {
    		initWidget(p);
    	}
    }



    private Widget simulDate(final ExecutionTrace ext) {
    	HorizontalPanel ab = new HorizontalPanel();
        final String fmt = "dd-MMM-YYYY"; //NON-NLS
        final TextBox dt = new TextBox();
        if (ext.scenarioSimulatedDate == null) {
            dt.setText("<" + fmt + ">");
        } else {
            dt.setText(ext.scenarioSimulatedDate.toLocaleString());
        }
        final SmallLabel dateHint = new SmallLabel();
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
                    dt.setText(constants.currentDateAndTime());
                } else {
                    try {
                        Date d = new Date(dt.getText());
                        ext.scenarioSimulatedDate = d;
                        dt.setText(d.toLocaleString());
                        dateHint.setText("");
                    } catch (Exception e) {
                        ErrorPopup.showMessage(Format.format(constants.BadDateFormatPleaseTryAgainTryTheFormatOf0(), fmt));
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
	private String type;
	private SuggestionCompletionEngine sce;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public VerifyFactWidget(final VerifyFact vf, final Scenario sc, final SuggestionCompletionEngine sce, boolean showResults) {
        outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");  //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");                //NON-NLS
        this.sce = sce;
        HorizontalPanel ab = new HorizontalPanel();
        if (!vf.anonymous) {
	        type = (String) sc.getVariableTypes().get(vf.name);
            ab.add(new SmallLabel(Format.format(constants.scenarioFactTypeHasValues(), type, vf.name)));
        } else {
        	type = vf.name;
            ab.add(new SmallLabel(Format.format(constants.AFactOfType0HasValues(), vf.name)));
        }
        this.showResults = showResults;

        Image add = new ImageButton("images/add_field_to_fact.gif", constants.AddAFieldToThisExpectation(), new ClickListener() { //NON-NLS
			public void onClick(Widget w) {

				String[] fields = (String[]) sce.fieldsForType.get(type);
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.ChooseAFieldToAdd()); //NON-NLS
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					b.addItem(fields[i]);
				}
				pop.addRow(b);
				Button ok = new Button(constants.OK());
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
            data.setWidget(i, 1, new SmallLabel(fld.fieldName + ":"));
            data.getFlexCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            final ListBox opr = new ListBox();
            opr.addItem(constants.equalsScenario(), "==");
            opr.addItem(constants.doesNotEqualScenario(), "!=");
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

            Widget cellEditor = ScenarioWidget.editableCell(new ValueChanged() {

				public void valueChanged(String newValue) {
					fld.expected = newValue;
				}

            }, type, fld.fieldName, fld.expected, sce);

            data.setWidget(i, 3, cellEditor);

            Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisFieldExpectation(), new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisFieldExpectation())) {
						vf.fieldValues.remove(fld);
				        FlexTable data = render(vf);
				        outer.setWidget(1, 0, data);
					}
				}
			});
            data.setWidget(i, 4, del);

            if (showResults && fld.successResult != null) {
            	if (!fld.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif"));        //NON-NLS
                    data.setWidget(i, 5, new HTML(Format.format(constants.ActualResult(), fld.actualResult )));

            		data.getCellFormatter().addStyleName(i, 5, "testErrorValue"); //NON-NLS

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png")); //NON-NLS
            	}
            }



        }
		return data;
	}

}

class VerifyRulesFiredWidget extends Composite {
    private Grid outer;
    private boolean showResults;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
     * @param rfl List<VeryfyRuleFired>
     * @param scenario = the scenario to add/remove from
     */
    public VerifyRulesFiredWidget(final List rfl, final Scenario scenario, boolean showResults) {
        outer = new Grid(2, 1);
        this.showResults = showResults;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");    //NON-NLS

        outer.setWidget(0, 0, new SmallLabel(constants.ExpectRules()));
        initWidget(outer);

        FlexTable data = render(rfl, scenario);
        outer.setWidget(1, 0, data);
    }



	private FlexTable render(final List rfl, final Scenario sc) {
		FlexTable data = new DirtyableFlexTable();


        for (int i = 0; i < rfl.size(); i++) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get(i);

            if (showResults && v.successResult != null) {
            	if (!v.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif")); //NON-NLS
                    data.setWidget(i, 4, new HTML(Format.format(constants.ActualResult(), v.actualResult)));

            		data.getCellFormatter().addStyleName(i, 4, "testErrorValue");   //NON-NLS

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png"));     //NON-NLS
            	}

            }
            data.setWidget(i, 1, new SmallLabel(v.ruleName + ":"));
            data.getFlexCellFormatter().setAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);


            final ListBox b = new ListBox();
            b.addItem(constants.firedAtLeastOnce(), "y");
            b.addItem(constants.didNotFire(), "n");
            b.addItem(constants.firedThisManyTimes(), "e");
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
                        v.expectedFire = (s.equals("y")) ? Boolean.TRUE : Boolean.FALSE;
                        v.expectedCount = null;
                    } else {
                        num.setVisible(true);
                        v.expectedFire = null;
                        num.setText("1"); v.expectedCount = new Integer(1);
                    }
                }
            });

            b.addItem(constants.ChooseDotDotDot());

            num.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    v.expectedCount = new Integer(num.getText());
                }
            });

            HorizontalPanel h = new HorizontalPanel();
            h.add(b); h.add(num);
            data.setWidget(i, 2, h);

            Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisRuleExpectation(), new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisRuleExpectation())) {
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
    private Constants constants = ((Constants) GWT.create(Constants.class));

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
        outer.setWidget(0, 0, new SmallLabel(constants.RetractFacts()));
        outer.getFlexCellFormatter().setColSpan(0, 0, 2);

        int row = 1;
        for (Iterator iterator = retList.iterator(); iterator.hasNext();) {
			final RetractFact r = (RetractFact) iterator.next();
			outer.setWidget(row, 0, new SmallLabel(r.name));
			Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisRetractStatement(), new ClickListener() {
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
	//Grid layout = new Grid(2, 1);
	VerticalPanel layout = new VerticalPanel();

	//private HorizontalPanel busy = new HorizontalPanel();
	private SimplePanel actions = new SimplePanel();
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public TestRunnerWidget(final ScenarioWidget parent, final String packageName) {

		final Button run = new Button(constants.RunScenario());
		run.setTitle(constants.RunScenarioTip());
		run.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				LoadingPopup.showMessage(constants.BuildingAndRunningScenario());
				RepositoryServiceFactory.getService().runScenario(parent.asset.metaData.packageName, (Scenario) parent.asset.content, new GenericCallback<SingleScenarioResult> () {
					public void onSuccess(SingleScenarioResult data) {
						LoadingPopup.close();
						layout.clear();
						layout.add(actions);
						layout.add(results);
						actions.setVisible(true);
						ScenarioRunResult result = data.result;
						if (result.errors != null) {
							showErrors(result.errors);
						} else {
							showResults(parent, data);
						}
					}
				});
			}
		});

		actions.add(run);
		layout.add(actions);
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
                errTable.setText( row, 1, constants.packageConfigurationProblem1() + res.message );
            } else {
                errTable.setText( row, 1, "[" + res.assetName + "] " + res.message );
            }

        }
        ScrollPanel scroll = new ScrollPanel(errTable);

        scroll.setWidth( "100%" );
        results.setWidget(0, 0, scroll);

	}

	private void showResults(final ScenarioWidget parent,
			final SingleScenarioResult data) {
		results.clear();
		results.setVisible(true);

		parent.asset.content = data.result.scenario;
		parent.showResults = true;
		parent.renderEditor();

		int failures = 0;
		int total = 0;
		VerticalPanel resultsDetail = new VerticalPanel();


		for (Iterator iterator = data.result.scenario.fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof VerifyRuleFired) {

				VerifyRuleFired vr = (VerifyRuleFired)f;
				HorizontalPanel h = new HorizontalPanel();
				if (!vr.successResult.booleanValue()) {
					h.add(new Image("images/warning.gif"));
					failures++;
				} else {
					h.add(new Image("images/test_passed.png"));
				}
				h.add(new SmallLabel(vr.explanation));
				resultsDetail.add(h);
				total++;
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
					h.add(new SmallLabel(vfl.explanation));
					resultsDetail.add(h);
				}


			} else if (f instanceof ExecutionTrace) {
				ExecutionTrace ex = (ExecutionTrace) f;
				if (ex.numberOfRulesFired == data.result.scenario.maxRuleFirings) {
                    Window.alert(Format.format(constants.MaxRuleFiringsReachedWarning(), data.result.scenario.maxRuleFirings));
				}
			}


		}

		results.setWidget(0, 0, new SmallLabel(constants.Results()));
		results.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		if (failures > 0) {
			results.setWidget(0, 1, ScenarioWidget.getBar("#CC0000" , 150, failures, total));
		} else {
			results.setWidget(0, 1, ScenarioWidget.getBar("GREEN" , 150, failures, total));
		}

		results.setWidget(1, 0, new SmallLabel(constants.SummaryColon()));
		results.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		results.setWidget(1, 1, resultsDetail);
		results.setWidget(2, 0, new SmallLabel(constants.AuditLogColon()));

		final Button showExp = new Button(constants.ShowEventsButton());
		results.setWidget(2, 1, showExp);
		showExp.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showExp.setVisible(false);
				results.setWidget(2, 1, doAuditView(data.auditLog));
			}
		});



	}



	private Widget doAuditView(List<String[]> auditLog) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(new HTML("<hr/>"));
		FlexTable g = new FlexTable();
		String indent = "";
		int row = 0;
		boolean firing = false;
		for (int i = 0; i < auditLog.size(); i++) {
			String[] lg = auditLog.get(i);

			int id = Integer.parseInt(lg[0]);
			if (id <= 7) {
				if (id <= 3) {
					if (!firing) {
						g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
						g.setWidget(row, 1, new SmallLabel(lg[1]));
					} else {
						g.setWidget(row, 1, hz(new Image("images/audit_events/" + lg[0] + ".gif"), new SmallLabel(lg[1])));
					}
					row++;
				} else	if (id == 6) {
					firing = true;
					g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
					g.setWidget(row, 1, new SmallLabel("<b>" + lg[1] + "</b>"));
					row++;
				} else if (id == 7) {
					firing = false;
				} else {
					g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
					g.setWidget(row, 1, new SmallLabel("<font color='grey'>" +   lg[1] + "</font>"));
					row++;
				}
			} else {
				g.setWidget(row, 0, new Image("images/audit_events/misc_event.gif"));
				g.setWidget(row, 1, new SmallLabel("<font color='grey'>" + lg[1] + "</font>"));
				row++;
			}
		}
		vp.add(g);
		vp.add(new HTML("<hr/>"));
		return vp;
	}


	private Widget hz(Image image, SmallLabel smallLabel) {
		HorizontalPanel h = new HorizontalPanel();
		h.add(image);
		h.add(smallLabel);
		return h;
	}






}
