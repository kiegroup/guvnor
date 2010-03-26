package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.factconstraints.helper.ConstraintsContainer;
import org.drools.guvnor.client.factconstraints.predefined.IntegerConstraint;
import org.drools.guvnor.client.factconstraints.predefined.NotNullConstraint;
import org.drools.guvnor.client.factconstraints.predefined.RangeConstraint;
import org.drools.guvnor.client.factcontraints.Constraint;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;

public class WorkingSetEditor extends Composite {
	private Constants constants =  GWT.create(Constants.class);
	private RuleAsset workingSet;
	
	private ListBox availFacts = new ListBox(true);
	private ListBox validFacts = new ListBox(true);
	private ListBox factsCombo = new ListBox(false);
	private boolean validFactsChanged = true;
	private SuggestionCompletionEngine sce;
	private ConstraintsContainer cc;

	private ListBox fieldsCombo = new ListBox(false);
	private ListBox constraintsCombo = new ListBox(false);
	private VerticalPanel vpConstraintConf = new VerticalPanel();
	private Map<String, Constraint> contraintsMap = new HashMap<String, Constraint>();
	
	public WorkingSetEditor(RuleAsset asset) {
		if (!AssetFormats.WORKING_SET.equals(asset.metaData.format)) {
			throw new IllegalArgumentException("asset must a be a workingset not a: " + asset.metaData.format);
		}
		workingSet = asset;
		sce = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.metaData.packageName);
		WorkingSetConfigData wsData = (WorkingSetConfigData) workingSet.content;
		cc = new ConstraintsContainer(wsData.constraints);
		refreshWidgets();
		setWidth( "100%" );
	}
	
	private void refreshWidgets() {
        WorkingSetConfigData wsData = (WorkingSetConfigData) workingSet.content;
		
		TabPanel tPanel = new TabPanel();
		Panel pnl = new Panel();
        pnl.setAutoWidth(true);
        pnl.setClosable(false);
        pnl.setTitle("WS Definition");
        pnl.setAutoHeight(true);
        pnl.add(buildDoubleList(wsData));
		tPanel.add(pnl);
		
		pnl = new Panel();
        pnl.setAutoWidth(true);
        pnl.setClosable(false);
        pnl.setTitle("WS Constraints");
        pnl.setAutoHeight(true);
        pnl.add(buildFactsConstraintsEditor(tPanel));
		tPanel.add(pnl);
		
		tPanel.setActiveTab(0);
		initWidget(tPanel);
	}

//	private int lastSelectedFact = -1;
//	private int lastSelectedField = -1;
//	private int lastSelectedConstraint = -1;
	private Widget buildFactsConstraintsEditor(TabPanel tPanel) {
		factsCombo.setVisibleItemCount(1);
		fieldsCombo.setVisibleItemCount(1);
		constraintsCombo.setVisibleItemCount(5);
		
		tPanel.addListener(new PanelListenerAdapter() {
			@Override
			public boolean doBeforeShow(Component component) {
				fillSelectedFacts();
				return true;
			}
		});
		
		factsCombo.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				fillSelectedFactFields();
			}
		});
		
		fieldsCombo.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				fillFieldConstrains();
			}
		});
		
		Image addNewConstraint = new ImageButton("images/new_item.gif"); // NON-NLS
		addNewConstraint.setTitle(constants.AddNewConstraint());

		addNewConstraint.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showNewConstrainPop();
			}
		});
		
        Image removeConstraint = new Image( "images/trash.gif" ); //NON-NLS
        removeConstraint.setTitle(constants.removeConstraint());
        removeConstraint.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				removeConstraint();
			}
		});
        
        
        final FlexTable table = new FlexTable();
        
		VerticalPanel vp = new VerticalPanel();
		vp.add(new Label(constants.FactTypes()));
		vp.add(factsCombo);
		table.setWidget(0, 0, vp);
		
		vp = new VerticalPanel();
		vp.add(new Label(constants.Field()));
		vp.add(fieldsCombo);
		table.setWidget(1, 0, vp);
		
		vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		vp.add(new Label(constants.ConstraintsSection()));
		hp.add(constraintsCombo);
		
		VerticalPanel btnPanel = new VerticalPanel();
		btnPanel.add(addNewConstraint);
		btnPanel.add(removeConstraint);
		hp.add(btnPanel);
		vp.add(hp);
		table.setWidget(2, 0, vp);
		table.getFlexCellFormatter().setRowSpan(2, 0, 3);
		
		constraintsCombo.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				showConstraintConfig();
			}
		});
		
		vpConstraintConf.add(new Label(constants.ConstraintsSection()));
		vpConstraintConf.add(new Label(""));
		table.setWidget(0, 1, vpConstraintConf);
		table.getFlexCellFormatter().setRowSpan(0, 1, 5);
	
		fillSelectedFacts();
		fillSelectedFactFields();
		fillFieldConstrains();
		showConstraintConfig();
		return table;
	}

	protected void removeConstraint() {
		if (constraintsCombo.getSelectedIndex() != -1) {
			Constraint c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
			getConstraintsConstrainer().removeConstraint(c);
		}
		fillFieldConstrains();
	}

	private void showConstraintConfig() {
		if (constraintsCombo.getItemCount() == 0) {
			vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
			vpConstraintConf.add(new Label());
			return;
		}
		if (constraintsCombo.getSelectedIndex() != -1) {
			Constraint c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
			ConstraintEditor editor = new ConstraintEditor(c);
			vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
			vpConstraintConf.add(editor);
		}
	}

	private void showNewConstrainPop() {
        final FormStylePopup pop = new FormStylePopup("images/config.png", constants.AddNewConstraint()); //NON-NLS
        final Button addbutton = new Button(constants.OK());
        final ListBox consDefsCombo = new ListBox(false);

        consDefsCombo.setVisibleItemCount(5);

        addbutton.setTitle(constants.AddNewConstraint());
        
        consDefsCombo.addItem("NotNull");
        consDefsCombo.addItem("Range");
        consDefsCombo.addItem("Integer");
        
        addbutton.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	String consDef = consDefsCombo.getItemText(consDefsCombo.getSelectedIndex());
            	Constraint cons = null;
            	if ("NotNull".equals(consDef)) {
            		cons = new NotNullConstraint();
            	} else if ("Range".equals(consDef)) {
            		cons = new RangeConstraint();
            	} else if ("Integer".equals(consDef)) {
            		cons = new IntegerConstraint();
            	}
            	if (cons != null) {
            		
            		String factName = factsCombo.getItemText(factsCombo.getSelectedIndex());
            		String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
            		cons.setFactType(factName);
            		cons.setFieldName(fieldName);
            		if (((WorkingSetConfigData) workingSet.content).constraints == null) {
            			((WorkingSetConfigData) workingSet.content).constraints = new ArrayList<Constraint>();
            		}
            		((WorkingSetConfigData) workingSet.content).constraints.add(cons);
            		constraintsCombo.addItem(cons.getConstraintName(), addContrainsMap(cons));
            		getConstraintsConstrainer().addConstraint(cons);
            		
            	}
            	pop.hide();

            }
        });

        pop.addAttribute(constants.WillExtendTheFollowingRuleCalled(), consDefsCombo );
        pop.addAttribute("", addbutton);

        pop.show();
	}

	private void fillSelectedFacts() {
		if (validFactsChanged) {
			String s = factsCombo.getSelectedIndex() != -1 ? factsCombo.getItemText(factsCombo.getSelectedIndex()) : "";
			factsCombo.clear();
			validFactsChanged = false;
			for (int i = 0; i < validFacts.getItemCount(); i++) {
				String itemText = validFacts.getItemText(i);
				factsCombo.addItem(itemText);
				if (s.equals(itemText)) {
					factsCombo.setSelectedIndex(i);
				}
			}
			if (factsCombo.getSelectedIndex() == -1 && factsCombo.getItemCount() > 0) {
				factsCombo.setSelectedIndex(0);
			}
			fillSelectedFactFields();
		}
	}

	private void fillSelectedFactFields() {
		if (factsCombo.getSelectedIndex() != -1) {
			String fact = factsCombo.getItemText(factsCombo.getSelectedIndex());
			fieldsCombo.clear();
			for(String field : getCompletionEngine().getFieldCompletions(fact)) {
				fieldsCombo.addItem(field);
			}
		}
		if (fieldsCombo.getSelectedIndex() == -1 && fieldsCombo.getItemCount() > 0) {
			fieldsCombo.setSelectedIndex(0);
		}
		fillFieldConstrains();
	}

	private void fillFieldConstrains() {
		if (fieldsCombo.getSelectedIndex() != -1) {
			String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
			String factField = factsCombo.getItemText(factsCombo.getSelectedIndex());
			constraintsCombo.clear();
			contraintsMap.clear();
			for (Constraint c : getConstraintsConstrainer().getConstraints(factField, fieldName)) {
				constraintsCombo.addItem(c.getConstraintName(), addContrainsMap(c));
			}
			vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
			vpConstraintConf.add(new Label());
		}
		showConstraintConfig();
	}
	
	private String addContrainsMap(Constraint c) {
		String id = "" + contraintsMap.size();
		contraintsMap.put(id, c);
		return id;
	}
	
	private Grid buildDoubleList(WorkingSetConfigData wsData) {
		Grid grid = new Grid(1, 3);
		
		SuggestionCompletionEngine sce = SuggestionCompletionCache.getInstance().getEngineFromCache(workingSet.metaData.packageName);
		
		Set<String> elem = new HashSet<String>();

		availFacts.setVisibleItemCount(10);
		validFacts.setVisibleItemCount(10);
		
		if (wsData.validFacts != null) {
			elem.addAll(Arrays.asList(wsData.validFacts));
			for (String factName : wsData.validFacts) {
				validFacts.addItem(factName);
			}
		}
			
		for (String factName : sce.getFactTypes()) {
			if (!elem.contains(factName)) {
				availFacts.addItem(factName);
			}
		}
		
		Grid btnsPanel = new Grid(2,1);
		
		btnsPanel.setWidget(0, 0, new Button(">", new ClickListener() {
			public void onClick(Widget sender) {
				copySelected(availFacts, validFacts);
				updateAsset(validFacts);
				fillSelectedFacts();
			}
		}));

		btnsPanel.setWidget(1, 0, new Button("&lt;", new ClickListener() {
			public void onClick(Widget sender) {
				copySelected(validFacts, availFacts);
				updateAsset(validFacts);
				fillSelectedFacts();
			}
		}));

		grid.setWidget(0, 0, availFacts);
		grid.setWidget(0, 1, btnsPanel);
		grid.setWidget(0, 2, validFacts);
		
		grid.getColumnFormatter().setWidth(0, "45%");
		grid.getColumnFormatter().setWidth(0, "10%");
		grid.getColumnFormatter().setWidth(0, "45%");
		return grid;
	}
	
	/**
     * This will get the save widgets.
     */
//    private Widget modifyWidgets() {
//
//        HorizontalPanel horiz = new HorizontalPanel();
//
//        Button copy = new Button(constants.Copy());
//        copy.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {
//                showCopyDialog();
//            }
//        } );
//        horiz.add( copy );
//
//        Button rename = new Button(constants.Rename());
//        rename.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {
//                showRenameDialog();
//            }
//        } );
//        horiz.add( rename );
//
//
//        Button archive = new Button(constants.Archive());
//        archive.addClickListener(new ClickListener() {
//            public void onClick(Widget w) {
//                if ( Window.confirm(constants.AreYouSureYouWantToArchiveRemoveThisPackage()) ) {
////                    conf.archived = true;
//                    Command ref = new Command() {
//						public void execute() {
////		                    close.execute();
////		                    refreshPackageList.execute();
//						}
//                    };
////                    doSaveAction(ref);
//                }
//            }
//        });
//        horiz.add(archive);
//
//        return horiz;
//    }
	
	private void updateAsset(ListBox availFacts) {
		List<String> l = new ArrayList<String>(availFacts.getItemCount()); 
		for (int i = 0; i < availFacts.getItemCount(); i++) {
			l.add(availFacts.getItemText(i));
		}
		((WorkingSetConfigData) workingSet.content).validFacts = l.toArray(new String[l.size()]);
	}

	private void copySelected(final ListBox from, final ListBox to) {
		int selected;
		while ((selected = from.getSelectedIndex()) != -1) {
			to.addItem(from.getItemText(selected));
			from.removeItem(selected);
			validFactsChanged = true;
		}
	}
	
	/**
	 * Will show a copy dialog for copying the whole package.
	 */
//	private void showCopyDialog() {
//		final FormStylePopup pop = new FormStylePopup("images/new_wiz.gif", constants.CopyTheWorkingSet()); // NON-NLS
//		pop.addRow(new HTML(constants.CopyTheWorkingSetTip()));
//		final TextBox name = new TextBox();
//		pop.addAttribute(constants.NewWorkingSetNameIs(), name);
//		Button ok = new Button(constants.OK());
//		pop.addAttribute("", ok);
//
//		ok.addClickListener(new ClickListener() {
//			public void onClick(Widget w) {
//				if (!PackageNameValidator.validatePackageName(name.getText())) {
//					Window.alert(constants.NotAValidWorkingSetName());
//					return;
//				}
//				LoadingPopup.showMessage(constants.PleaseWaitDotDotDot());
//				RepositoryServiceFactory.getService().copyAsset(workingSet.uuid, workingSet.metaData.packageName, name.getText(), 
//						new GenericCallback<String>() {
//							public void onSuccess(String uuid) {
//								//TODO {bauna} refreshPackageList.execute();
//								Window.alert(constants.WorkingSetCopiedSuccessfully());
//								pop.hide();
//								LoadingPopup.close();
//							}
//					
//				});
//			}
//		});
//
//		pop.show();
//	}
	
//	private void showRenameDialog() {
//		final FormStylePopup pop = new FormStylePopup("images/new_wiz.gif", constants.RenameTheWorkingSet());
//		pop.addRow(new HTML(constants.RenameTheWorkingSetTip()));
//		final TextBox name = new TextBox();
//		pop.addAttribute(constants.NewWorkingSetNameIs(), name);
//		Button ok = new Button(constants.OK());
//		pop.addAttribute("", ok);
//
//		ok.addClickListener(new ClickListener() {
//			public void onClick(Widget w) {
//				LoadingPopup.showMessage(constants.PleaseWaitDotDotDot());
//				RepositoryServiceFactory.getService().renameAsset(workingSet.uuid, name.getText(),
//						new GenericCallback<String>() {
//							public void onSuccess(String uuid) {
//								Window.alert(constants.WorkingSetRenamedSuccessfully());
//								pop.hide();
//								LoadingPopup.close();
//							}
//						});
//			}
//		});
//
//		pop.show();
//	}

	public SuggestionCompletionEngine getCompletionEngine() {
		return sce;
	}
	
	public ConstraintsContainer getConstraintsConstrainer() {
		return cc;
	}
}
