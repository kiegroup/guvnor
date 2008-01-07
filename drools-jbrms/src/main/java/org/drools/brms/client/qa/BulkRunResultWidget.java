package org.drools.brms.client.qa;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.packages.PackageBuilderWidget;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.BulkTestRunResult;
import org.drools.brms.client.rpc.ScenarioResultSummary;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private FormStyleLayout layout;
	private SimplePanel parent;
	private String packageName;


	public BulkRunResultWidget(BulkTestRunResult result, EditItemEvent editEvent, final TabPanel tab, String packageName) {


		this.result = result;
		this.editEvent = editEvent;
		this.packageName = packageName;
		parent = new SimplePanel();

		if (result.errors != null && result.errors.length > 0) {
			showErrors();
		} else {
			showResult();
		}

		final BulkRunResultWidget self = this;

		Button close = new Button("Close");
		close.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				tab.remove(self);
				tab.selectTab(0);
			}
		});



		layout.addAttribute("", close);
		initWidget(parent);
	}


	private void showResult() {

		clear();

		int grandTotal = 0;
		int totalFailures = 0;
		FlexTable summaryTable = new FlexTable();
		ScenarioResultSummary[] summaries = result.results;
		for (int i = 0; i < summaries.length; i++) {
			final ScenarioResultSummary s = summaries[i];
			grandTotal = grandTotal + s.total;
			totalFailures = totalFailures + s.failures;

			//now render this summary
			summaryTable.setWidget(i, 0, new Label(s.scenarioName + ":"));
			summaryTable.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);

			if (s.failures > 0) {
				summaryTable.setWidget(i, 1, ScenarioWidget.getBar("#CC0000", 150, s.total - s.failures, s.total));
			} else {
				summaryTable.setWidget(i, 1, ScenarioWidget.getBar("GREEN", 150, 100));
			}

			summaryTable.setWidget(i, 2, new Label("[" + s.failures +" failures out of " + s.total + "]"));
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

		HorizontalPanel resultsH = new HorizontalPanel();

		if (totalFailures > 0) {
			resultsH.add(ScenarioWidget.getBar("#CC0000", 300, totalFailures, grandTotal));
		} else {
			resultsH.add(ScenarioWidget.getBar("GREEN", 300, 100));
		}
		resultsH.add(new Label(totalFailures + " failures out of " + grandTotal + " expectations."));

		layout.addAttribute("Results:", resultsH);

		HorizontalPanel coveredH = new HorizontalPanel();
		if (result.percentCovered < 100) {
			coveredH.add(ScenarioWidget.getBar("YELLOW", 300, result.percentCovered));
		} else {
			coveredH.add(ScenarioWidget.getBar("GREEN", 300, 100));
		}

		coveredH.add(new Label(result.percentCovered + "% of the rules were tested."));
		layout.addAttribute("Rules covered:", coveredH);


		if (result.percentCovered < 100) {

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

				layout.addAttribute("Uncovered rules:", uncoveredRules);
		}



		layout.addAttribute("Scenarios:", summaryTable);

	}


	private void clear() {
		parent.clear();
		layout = new FormStyleLayout("images/scenario_large.png", "Testing: " + packageName);
		parent.add(layout);
	}

	private void showErrors() {
		clear();
		BuilderResult[] errors = result.errors;

		Panel err = new SimplePanel();

		PackageBuilderWidget.showBuilderErrors(errors, err, editEvent);
		layout.addRow(err);
	}

}
