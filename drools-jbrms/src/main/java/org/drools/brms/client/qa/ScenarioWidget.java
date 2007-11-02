package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScenarioWidget extends Composite {

	public ScenarioWidget() {
		DirtyableFlexTable layout  = new DirtyableFlexTable();


		//Sample data
		FactData d1 = new FactData("Driver", "d1", new FieldData[] {new FieldData("age", "42", false), new FieldData("name", "david", false)}, false);
		FactData d2 = new FactData("Driver", "d2", new FieldData[] {new FieldData("name", "michael", false)}, false);
		FactData d3 = new FactData("Driver", "d3", new FieldData[] {new FieldData("name", "michael2", false)}, false);
		FactData d4 = new FactData("Accident", "a1", new FieldData[] {new FieldData("name", "michael2", false)}, false);
		Scenario sc = new Scenario();
		sc.fixtures.add(d1);
		sc.fixtures.add(d2);
		sc.globals.add(d3);
		sc.globals.add(d4);
		sc.rules.add("rule1");
		sc.rules.add("rule2");

		//now have to sort this out for the view.
		//we want data grouped by type, for the purposes of the grid
		Map facts = breakUpFactData(sc.fixtures);
		Map globals = breakUpFactData(sc.globals);


		//now we have them grouped by type and global/fact, so we can render them appropriately.
		//maps are a map of Type => List of FactData
		VerticalPanel factPanel = new VerticalPanel();
		for (Iterator iterator = facts.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry e = (Map.Entry) iterator.next();
			factPanel.add(new DataInputWidget((String)e.getKey(), facts, false, false));
		}
		VerticalPanel globalPanel = new VerticalPanel();
		for (Iterator iterator = globals.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry e = (Map.Entry) iterator.next();
			globalPanel.add(new DataInputWidget((String)e.getKey(), globals, true, false));
		}

		ExecutionTrace ex = new ExecutionTrace();
		ExecutionWidget exw = new ExecutionWidget(ex, false);


		ConfigWidget conf = new ConfigWidget(sc, new String[] {"rule1", "rule2", "rule3"});

		layout.setWidget(0, 0, conf);
		layout.setWidget(1, 0, globalPanel);
		layout.setWidget(2, 0, factPanel);
		layout.setWidget(3, 0, exw);

		layout.setStyleName("model-builder-Background");
		initWidget(layout);

	}

	/**
	 * Separate out the elements into the appropriate types.
	 * Will be keyed on the type name.
	 */
    static Map breakUpFactData(List factData) {
    	Map facts = new HashMap();
		for (Iterator iterator = factData.iterator(); iterator.hasNext();) {
			Object f = (Object) iterator.next();
			if (f instanceof FactData) {
				FactData fd = (FactData) f;
				addToMap(facts, fd);
			}
		}
		return facts;
	}

	private static void addToMap(Map m, FactData fd) {
		if (!m.containsKey(fd.type)) {
			m.put(fd.type, new ArrayList());
		}
		List l = (List) m.get(fd.type);
		l.add(fd);
	}

}


/**
 * For capturing input for all the facts of a given type.
 * @author Michael Neale
 */
class DataInputWidget extends Composite {


	public DataInputWidget(String factType, Map facts, boolean isGlobal, boolean isModify) {
		//need to keep track of what fields are in what row in the table.

		Grid outer = new Grid(2, 1);
		outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
		outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
		outer.setStyleName("modeller-fact-pattern-Widget");


		if (isGlobal) {
			outer.setWidget(0, 0, new Label("Global: " + factType));
		} else if (isModify) {
			outer.setWidget(0, 0, new Label("Modify: " + factType));
		} else  {
			outer.setWidget(0, 0, new Label("Insert: " + factType));
		}

		final FlexTable t = new FlexTable();

		//This will work out what row is for what field
		Map fields = new HashMap();
		int col = 0;
		List defList = (List) facts.get(factType);
		for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
			FactData d = (FactData) iterator.next();
			for (int i = 0; i < d.fieldData.length; i++) {
				FieldData fd = d.fieldData[i];
				if (!fields.containsKey(fd.name)) {
					int idx = fields.size() + 1;
					fields.put(fd.name, new Integer(idx));
					t.setWidget(idx, 0, new Label(fd.name));
				}
			}
		}

		//now we go through the facts and the fields, adding them to the grid
		//if a fact is missing a FieldData, we will add it in (so people can enter data later on)
  		col = 0;
		for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
			FactData d = (FactData) iterator.next();
			t.setWidget(0, ++col, new Label(d.name));
			Map presentFields = new HashMap(fields);
			for (int i = 0; i < d.fieldData.length; i++) {
				FieldData fd = d.fieldData[i];
				int fldRow = ((Integer) fields.get(fd.name)).intValue();
				t.setWidget(fldRow, col, editableCell(fd));
				presentFields.remove(fd.name);
			}

			for (Iterator missing = presentFields.entrySet().iterator(); missing.hasNext();) {
				Map.Entry e = (Map.Entry) missing.next();
				int fldRow = ((Integer) e.getValue()).intValue();
				FieldData fd = new FieldData((String) e.getKey(), "", false);
				d.addFieldData(fd);
				t.setWidget(fldRow, col, editableCell(fd));
			}
		}

		outer.setWidget(1, 0, t);
		initWidget(outer);
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
		return tb;
	}




}

class ConfigWidget extends Composite {
	public ConfigWidget(final Scenario sc, final String[] fullRuleList) {
		FormStyleLayout layout = new FormStyleLayout("images/scenario_conf.gif", "Rules");
		final ListBox box = new ListBox(true);

		for (int i = 0; i < sc.rules.size(); i++) {
			box.addItem((String)sc.rules.get(i));
		}
		HorizontalPanel filter = new HorizontalPanel();

		Image add = new ImageButton("images/new_item.gif", "Add a new rule.");
		add.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showRulePopup(w, box, fullRuleList, sc.rules);
			}
		});

		Image remove = new ImageButton("images/trash.gif", "Remove selected rule.");
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
		filter.add(actions);


		filter.add(box);
		VerticalPanel vert = new VerticalPanel();
		RadioButton include = new RadioButton("inc", "Include all rules listed");
		include.setChecked(sc.rules.size() > 0 && sc.inclusive);
		include.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				sc.inclusive = true;
			}
		});


		RadioButton exclude = new RadioButton("inc", "Exclude all rules listed");
		exclude.setChecked(sc.rules.size() > 0 && !sc.inclusive);
		exclude.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				sc.inclusive = false;
			}
		});

		RadioButton all = new RadioButton("inc", "All rules");
		all.setChecked(sc.rules.size() ==0);
		all.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				box.clear();
				sc.rules.clear();
			}
		});
		vert.add(include); vert.add(exclude); vert.add(all);


		filter.add(vert);
		layout.addAttribute("Rules to filter:", filter);
		initWidget(layout);
	}

	private void showRulePopup(Widget w, final ListBox box, String[] fullRuleList, final List filterList) {
		final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "Select rule");
		final ListBox rules = new ListBox();
		for (int i = 0; i < fullRuleList.length; i++) {
			rules.addItem(fullRuleList[i]);
		}
		pop.addRow(rules);
		Button ok = new Button("Add");
		pop.addRow(ok);
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				String r = rules.getItemText(rules.getSelectedIndex());
				filterList.add(r);
				box.addItem(r);
				pop.hide();
			}
		});
		pop.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
		pop.show();

	}


}

class ExecutionWidget extends Composite {
	public ExecutionWidget(ExecutionTrace ext, boolean showResults) {
		final SimplePanel p = new SimplePanel();
		render(ext, showResults, p);
		initWidget(p);
	}

	private void render(final ExecutionTrace ext, boolean showResults,
			final SimplePanel p) {
		FormStyleLayout layout = new FormStyleLayout("images/execution_trace.gif", "Run the rules");
		p.add(layout);
		if (showResults) {
			layout.addAttribute("Execution time:", new Label(ext.executionTimeResult + " ms"));
			layout.addAttribute("Number of rules fired:", new Label(ext.numberOfRulesFired + ""));
		}
		layout.addAttribute("Simulation date:", simulDate(ext));


	}

	private Widget simulDate(final ExecutionTrace ext) {
		final String fmt = "dd-MMM-YYYY";
		final TextBox dt = new TextBox();
		if (ext.scenarioSimulatedDate == null) {
			dt.setText("<current date and time>");
		} else {
			dt.setText(ext.scenarioSimulatedDate.toLocaleString());
		}
		dt.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				if (dt.getText().trim().equals("")) {
					dt.setText("<current date and time>");
				} else {
					try {
						Date d = new Date(dt.getText());
						ext.scenarioSimulatedDate = d;
						dt.setText(d.toLocaleString());
					} catch (Exception e) {
						ErrorPopup.showMessage("Bad date format - please try again (try the format of " + fmt + ").");
					}
				}
			}
		});
		return dt;
	}


}

