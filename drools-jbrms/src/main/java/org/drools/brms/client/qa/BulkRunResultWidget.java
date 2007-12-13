package org.drools.brms.client.qa;

import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.packages.PackageBuilderWidget;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.BulkTestRunResult;
import org.drools.brms.client.rpc.ScenarioResultSummary;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This presents the results of a bulk run.
 * @author Michael Neale
 */
public class BulkRunResultWidget extends Composite {

	private BulkTestRunResult result;
	private EditItemEvent editEvent;
	private FlexTable layout;


	public BulkRunResultWidget(BulkTestRunResult result, EditItemEvent editEvent, final TabPanel tab) {

		layout = new FlexTable();
		this.result = result;
		this.editEvent = editEvent;

		if (result.errors != null && result.errors.length > 0) {
			showErrors();
		} else {
			showResult();
		}

		final BulkRunResultWidget self = this;

		Image close = new ImageButton("images/close.gif", "Close", new ClickListener() {
			public void onClick(Widget w) {
				tab.remove(self);
				tab.selectTab(0);
			}
		});
		layout.setWidget(0, 2, close);



		initWidget(layout);
	}

	private void showResult() {
		layout.clear();
		int grandTotal = 0;
		int totalFailures = 0;
		FlexTable summaryTable = new FlexTable();
		ScenarioResultSummary[] summaries = result.results;
		for (int i = 0; i < summaries.length; i++) {
			final ScenarioResultSummary s = summaries[i];
			grandTotal = grandTotal + s.total;
			totalFailures = totalFailures + s.failures;

			//now render this summary
			summaryTable.setWidget(i, 0, ScenarioWidget.greenBar(s.failures, s.total));
			summaryTable.setWidget(i, 2, new Label("[" + s.failures +" failures out of " + s.total + "]"));
			summaryTable.setWidget(i, 1, new Label(s.scenarioName));
			Button open = new Button("Open");
			open.addClickListener(new ClickListener() {
				public void onClick(Widget w) {
					editEvent.open(s.uuid);
				}
			});
			summaryTable.setWidget(i, 3, open);
		}

		//add the summary to the layout
		summaryTable.setWidth("100%");
		layout.setWidget(1, 0, summaryTable);

		summaryTable.setStyleName("model-builder-Background");

		//now add the grand totals and we are done !
		FlexTable totals = new FlexTable();
		totals.setWidget(0, 0, new Label("Results:"));
		totals.setWidget(0, 1, ScenarioWidget.greenBar(totalFailures, grandTotal));
		totals.setWidget(0, 2, new Label(totalFailures + " failures out of " + grandTotal + " expectations."));

		totals.setWidget(1, 0, new Label("Rules covered:"));


		totals.setWidget(1, 1, ScenarioWidget.greenBar(100 - result.percentCovered, 100));
		Button showRulesUncovered = new Button("Show uncovered rules");
		totals.setWidget(1, 2, new Label(result.percentCovered + "% of the rules were tested."));
		if (result.percentCovered != 100) {
			totals.setWidget(1, 3, showRulesUncovered);

		}



		showRulesUncovered.addClickListener(new ClickListener()  {
			public void onClick(Widget w) {
				ListBox uncoveredRules = new ListBox();
				for (int i = 0; i < result.rulesNotCovered.length; i++) {
					uncoveredRules.addItem(result.rulesNotCovered[i]);
				}
				uncoveredRules.setMultipleSelect(true);
				if (result.rulesNotCovered.length > 20) {
					uncoveredRules.setVisibleItemCount(20);
				} else {
					uncoveredRules.setVisibleItemCount(result.rulesNotCovered.length);
				}

				HorizontalPanel un = new HorizontalPanel();
				un.add(new Label("Uncovered rules: "));
				un.add(uncoveredRules);

				DialogBox box = new DialogBox(true);
				box.setTitle("Uncovered rules");
				box.setWidget(un);
				box.setPopupPosition(w.getAbsoluteLeft() - 40, w.getAbsoluteTop());
				box.show();

			}
		});

		totals.setStyleName("model-builder-Background");
		layout.setWidget(0, 0, totals);

	}

	private void showErrors() {
		BuilderResult[] errors = result.errors;
		layout.clear();
		layout.setWidget(0, 0, new HTML("<i><b>Scenarios were not able to run due to the following package errors.</b></i>"));
		Panel err = new SimplePanel();
		layout.setWidget(1, 0, err);
		PackageBuilderWidget.showBuilderErrors(errors, err, editEvent);

	}

}
