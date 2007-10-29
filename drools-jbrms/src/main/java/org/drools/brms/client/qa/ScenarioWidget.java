package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class ScenarioWidget extends Composite {

	public ScenarioWidget() {




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

		//now we have them grouped by type and global/fact, so we can render them appropriately.
		//maps are a map of Type => List of FactData


		initWidget(new FactInput("Driver", facts));


	}

	private void addToMap(Map globals, FactData fd) {
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
	public FactInput(String factType, Map facts) {
		//need to keep track of what fields are in what row in the table.
		Map fields = new HashMap();
		t.setWidget(0, 0, new Label("Insert " + factType));
		int col = 0, row = 0;
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
				t.setWidget(fldRow, col, new Label(fd.value));
			}
		}

		initWidget(t);
	}
}

