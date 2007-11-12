package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * The parent host widget for all QA schtuff.
 * QA for the purposes of this is testing and analysis tools.
 * @author Michael Neale
 */
public class QAManagerWidget extends Composite {


	public QAManagerWidget() {
		TabPanel tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("30%");

        tab.add( new ScenarioWidget(getDemo(), new String[] {"rule1", "rule2"}),  "<img src='images/test_manager.gif'/>Test", true);
        tab.add(new Label("TODO"), "<img src='images/analyze.gif'/>Analyze", true);
        tab.selectTab( 0 );

        initWidget(tab);
	}

	private Scenario getDemo() {
        //Sample data
        FactData d1 = new FactData("Driver", "d1", new FieldData[] {new FieldData("age", "42"), new FieldData("name", "david")}, false);
        FactData d2 = new FactData("Driver", "d2", new FieldData[] {new FieldData("name", "michael")}, false);
        FactData d3 = new FactData("Driver", "d3", new FieldData[] {new FieldData("name", "michael2")}, false);
        FactData d4 = new FactData("Accident", "a1", new FieldData[] {new FieldData("name", "michael2")}, false);
        Scenario sc = new Scenario();
        sc.fixtures.add(d1);
        sc.fixtures.add(d2);
        sc.globals.add(d3);
        sc.globals.add(d4);
        sc.rules.add("rule1");
        sc.rules.add("rule2");

        sc.fixtures.add(new ExecutionTrace());

        VerifyFact vf = new VerifyFact("d1", new VerifyField[] {
            new VerifyField("age", "42", "=="),
            new VerifyField("name", "michael", "!=")

        });

        sc.fixtures.add(vf);

        VerifyRuleFired vf1 = new VerifyRuleFired("xxx fdsfds", new Integer(42), null);
        VerifyRuleFired vf2 = new VerifyRuleFired("yyyyy fdsfdsfds fds", null, new Boolean(true));
        sc.fixtures.add(vf1);
        sc.fixtures.add(vf2);

		return sc;
	}


}
