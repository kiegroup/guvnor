package org.drools.brms.client.qa;

import org.drools.brms.client.modeldriven.testing.Scenario;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;


/**
 * For reporting the results of a scenario run.
 * @author Michael Neale
 */
public class ScenarioResultsWidget extends Composite {



	public ScenarioResultsWidget(Scenario sc) {
		Grid outer = new Grid(1, 1);

		Grid success = new Grid(1, 100);
		final CellFormatter cf = success.getCellFormatter();

		VerticalPanel vert = new VerticalPanel();





		vert.add(success);
		success.setStyleName("testBar");

		outer.setWidget(0, 0, vert);
		initWidget(outer);
	}



	private void renderGrey(CellFormatter cf) {
		for (int i = 0; i < 50; i++) {
				cf.setStyleName(0, i, "testGreyed");
		}
	}

	private void renderSuccess(CellFormatter cf, int percent) {
		int num = percent / 2;
		for (int i = 0; i < 50; i++) {
			if (i < num) {
				cf.setStyleName(0, i, "testSuccessBackground");
			} else {
				cf.setStyleName(0, i, "testFailureBackground");
			}
		}
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
