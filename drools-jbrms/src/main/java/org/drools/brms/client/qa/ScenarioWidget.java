package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScenarioWidget extends Composite {

	public ScenarioWidget() {

		DirtyableFlexTable layout  = new DirtyableFlexTable();



		List factData = new ArrayList();

		FactData d1 = new FactData("Driver", "d1", new FieldData[] {new FieldData("age", "42", false), new FieldData("name", "david", false)}, false);
		FactData d2 = new FactData("Driver", "d2", new FieldData[] {new FieldData("name", "michael", false)}, false);
		FactData d3 = new FactData("Driver", "d3", new FieldData[] {new FieldData("name", "michael2", false)}, true);
		FactData d4 = new FactData("Accident", "a1", new FieldData[] {new FieldData("name", "michael2", false)}, true);

		factData.addAll(Arrays.asList(new FactData[] {d1, d2, d3,d4}));


		//now have to sort this out for the view.

		//we want facts and globs separate, but grouped by type
		Map facts = new HashMap();
		Map globals = new HashMap();
		breakUpFactData(factData, facts, globals);

		//now we have them grouped by type and global/fact, so we can render them appropriately.
		//maps are a map of Type => List of FactData



		VerticalPanel factPanel = new VerticalPanel();
		for (Iterator iterator = facts.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry e = (Map.Entry) iterator.next();
			factPanel.add(new FactInput((String)e.getKey(), facts, false));
		}
		VerticalPanel globalPanel = new VerticalPanel();
		for (Iterator iterator = globals.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry e = (Map.Entry) iterator.next();
			globalPanel.add(new FactInput((String)e.getKey(), globals, true));
		}


		layout.setWidget(0, 0, factPanel);
		layout.setWidget(1, 0, globalPanel);


		layout.setStyleName("model-builder-Background");
		initWidget(layout);

	}

	/**
	 * Separate out the elements into the appropriate types.
	 * Will be keyed on the type name.
	 */
    static void breakUpFactData(List factData, Map facts, Map globals) {
		for (Iterator iterator = factData.iterator(); iterator.hasNext();) {
			Object f = (Object) iterator.next();
			if (f instanceof FactData) {
				FactData fd = (FactData) f;
				if (fd.isGlobal) {
					addToMap(globals, fd);
				} else {
					addToMap(facts, fd);
				}
			}
		}
	}

	private static void addToMap(Map globals, FactData fd) {
		if (!globals.containsKey(fd.type)) {
			globals.put(fd.type, new ArrayList());
		}
		List l = (List) globals.get(fd.type);
		l.add(fd);
	}

}


/**
 * For capturing input for all the facts of a given type.
 * @author Michael Neale
 */
class FactInput extends Composite {
	final FlexTable t = new FlexTable();
	public FactInput(String factType, Map facts, boolean isGlobal) {
		//need to keep track of what fields are in what row in the table.
		Map fields = new HashMap();
		if (isGlobal) {
			t.setWidget(0, 0, new Label("Global: " + factType));
		} else {
			t.setWidget(0, 0, new Label("Insert: " + factType));
		}
		int col = 0;
		List defList = (List) facts.get(factType);
		for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
			FactData d = (FactData) iterator.next();
			t.setWidget(0, ++col, new Label(d.name));
			for (int i = 0; i < d.fieldData.length; i++) {
				FieldData fd = d.fieldData[i];
				if (!fields.containsKey(fd.name)) {
					int idx = fields.size() + 1;
					fields.put(fd.name, new Integer(idx));
					t.setWidget(idx, 0, new Label(fd.name));
				}
				int fldRow = ((Integer) fields.get(fd.name)).intValue();
				t.setWidget(fldRow, col, editableCell(fd));
			}
		}

		initWidget(t);
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

