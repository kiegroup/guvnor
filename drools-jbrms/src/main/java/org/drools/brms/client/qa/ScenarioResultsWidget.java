package org.drools.brms.client.qa;

import java.util.Iterator;

import org.drools.brms.client.modeldriven.testing.Fixture;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;


/**
 * For reporting the results of a scenario run.
 * @author Michael Neale
 */
public class ScenarioResultsWidget extends Composite {



	public ScenarioResultsWidget(Scenario sc) {

		VerticalPanel vert = new VerticalPanel();

		int failures = 0;
		int total = 0;

		VerticalPanel results = new VerticalPanel();

		for (Iterator iterator = sc.fixtures.iterator(); iterator.hasNext();) {
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
				results.add(h);
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
					results.add(h);
				}

			}

		}

		vert.add(greenBarGoodness(failures, total));
		vert.add(results);


		vert.setStyleName("model-builder-Background");

		vert.setWidth("100%");

		initWidget(vert);
	}



	private void renderGrey(CellFormatter cf) {
		for (int i = 0; i < 50; i++) {
				cf.setStyleName(0, i, "testGreyed");
		}
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

	private void knightRider(final CellFormatter cf) {
		Timer t = new Timer() {
			int pos = 0;
			int step = 1;
			public void run() {
				if (pos == 49) {
					step = -1;
				} else if (pos == 0) {
					step = 1;
				}
				cf.setStyleName(0, pos, "testGreyed");
				pos = pos + step;
				cf.setStyleName(0, pos, "testKit");
			}
		};
		t.scheduleRepeating(50);
	}
}
