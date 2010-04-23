package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.helper.ConstraintsContainer;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;

public class WorkingSetEditor extends Composite {
	private static int idGenerator = 0;
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
	private Map<String, ConstraintConfiguration> contraintsMap = new HashMap<String, ConstraintConfiguration>();
	
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
		tPanel.setWidth(800);
		Panel pnl = new Panel();
//        pnl.setAutoWidth(true);
        pnl.setClosable(false);
        pnl.setTitle("WS Definition"); //TODO {bauna} i18n
//        pnl.setAutoHeight(true);
        pnl.add(buildDoubleList(wsData));
		tPanel.add(pnl);
		
		pnl = new Panel();
//        pnl.setAutoWidth(true);
        pnl.setClosable(false);
        pnl.setTitle("WS Constraints"); //TODO {bauna} i18n
//        pnl.setAutoHeight(true);
        pnl.add(buildFactsConstraintsEditor(tPanel));
		tPanel.add(pnl);
		
		tPanel.setActiveTab(0);
		initWidget(tPanel);
	}

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
		vp.add(new SmallLabel(constants.FactTypes()));
		vp.add(factsCombo);
		table.setWidget(0, 0, vp);
		
		vp = new VerticalPanel();
		vp.add(new SmallLabel(constants.Field()));
		vp.add(fieldsCombo);
		table.setWidget(1, 0, vp);
		
		vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		vp.add(new SmallLabel("Constraints")); //TODO i18n
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
		
		vpConstraintConf.add(new SmallLabel("Contraints Parameters")); //TODO i18n
		vpConstraintConf.add(new SmallLabel(""));
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
			ConstraintConfiguration c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
			((WorkingSetConfigData) workingSet.content).constraints = getConstraintsConstrainer().removeConstraint(c);
		}
		fillFieldConstrains();
	}

	private void showConstraintConfig() {
		if (constraintsCombo.getItemCount() == 0) {
			vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
			vpConstraintConf.add(new SmallLabel());
			return;
		}
		if (constraintsCombo.getSelectedIndex() != -1) {
			ConstraintConfiguration c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
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
        
        List<String> names = new ArrayList<String>(ConstraintsContainer.getAllConfigurations().keySet());
        Collections.sort(names);
        for (String name : names) {
        	consDefsCombo.addItem(name);	
		}
        
        addbutton.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	String name = consDefsCombo.getItemText(consDefsCombo.getSelectedIndex());
            	ConstraintConfiguration config = ConstraintsContainer.getEmptyConfiguration(name);
            	if (config != null) {
            		
            		String factName = factsCombo.getItemText(factsCombo.getSelectedIndex());
            		String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
            		config.setFactType(factName);
            		config.setFieldName(fieldName);
            		if (((WorkingSetConfigData) workingSet.content).constraints == null) {
            			((WorkingSetConfigData) workingSet.content).constraints = new ArrayList<ConstraintConfiguration>();
            		}
            		((WorkingSetConfigData) workingSet.content).constraints.add(config);
            		constraintsCombo.addItem(config.getConstraintName(), addContrainsMap(config));
            		getConstraintsConstrainer().addConstraint(config);
            		
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
			for (ConstraintConfiguration c : getConstraintsConstrainer().getConstraints(factField, fieldName)) {
				constraintsCombo.addItem(c.getConstraintName(), addContrainsMap(c));
			}
			vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
			vpConstraintConf.add(new SmallLabel());
		}
		showConstraintConfig();
	}
	
	synchronized private String addContrainsMap(ConstraintConfiguration c) {
		String id = String.valueOf(idGenerator++);
		contraintsMap.put(id, c);
		return id;
	}
	
	private Grid buildDoubleList(WorkingSetConfigData wsData) {
		Grid grid = new Grid(2, 3);
		
		SuggestionCompletionEngine sce = SuggestionCompletionCache.getInstance().getEngineFromCache(workingSet.metaData.packageName);
		boolean filteringFact = sce.isFilteringFacts();
		sce.setFilteringFacts(false);
		
		try {
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

			grid.setWidget(0, 0, new SmallLabel("Available Facts")); //TODO i18n
			grid.setWidget(0, 1, new SmallLabel(""));
			grid.setWidget(0, 2, new SmallLabel("WorkingSet Facts")); //TODO i18n
			grid.setWidget(1, 0, availFacts);
			grid.setWidget(1, 1, btnsPanel);
			grid.setWidget(1, 2, validFacts);
			
			grid.getColumnFormatter().setWidth(0, "45%");
			grid.getColumnFormatter().setWidth(0, "10%");
			grid.getColumnFormatter().setWidth(0, "45%");
			return grid;
		} finally {
			sce.setFilteringFacts(filteringFact);
		}
	}
	
	/**
     * This will get the save widgets.
     */
	
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
	
	public SuggestionCompletionEngine getCompletionEngine() {
		return sce;
	}
	
	public ConstraintsContainer getConstraintsConstrainer() {
		return cc;
	}
}
