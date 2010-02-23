package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * This presents the results of a bulk run.
 * @author Michael Neale
 */
public class BulkRunResultWidget extends Composite {

	private BulkTestRunResult result;
	private EditItemEvent editEvent;
	private PrettyFormLayout layout;
	private SimplePanel parent;
	private Command close;
    private Constants constants = GWT.create(Constants.class);


    public BulkRunResultWidget(BulkTestRunResult result, EditItemEvent editEvent, Command close) {

		this.close = close;
		this.result = result;
		this.editEvent = editEvent;
		parent = new SimplePanel();

		if (result.result != null && result.result.lines != null && result.result.lines.length > 0) {
			showErrors();
		} else {
			showResult();
		}


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
			summaryTable.setWidget(i, 0, new SmallLabel(s.scenarioName + ":"));
			summaryTable.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);

			if (s.failures > 0) {
				summaryTable.setWidget(i, 1, ScenarioWidget.getBar("#CC0000", 150, s.total - s.failures, s.total)); //NON-NLS
			} else {
				summaryTable.setWidget(i, 1, ScenarioWidget.getBar("GREEN", 150, 100));  //NON-NLS
			}

            
			summaryTable.setWidget(i, 2, new SmallLabel(Format.format(constants.TestFailureBulkFailures(), s.failures, s.total)));
			Button open = new Button(constants.Open());
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
			resultsH.add(ScenarioWidget.getBar("#CC0000", 300, totalFailures, grandTotal)); //NON-NLS
		} else {
			resultsH.add(ScenarioWidget.getBar("GREEN", 300, 100)); //NON-NLS
		}

		resultsH.add(new SmallLabel("&nbsp;" + Format.format(constants.failuresOutOFExpectations(),totalFailures, grandTotal)));

		layout.startSection();

		layout.addAttribute(constants.OverallResult(), new HTML((totalFailures==0) ? constants.SuccessOverall() : constants.FailureOverall()));



		layout.addAttribute(constants.Results(), resultsH);

		HorizontalPanel coveredH = new HorizontalPanel();
		if (result.percentCovered < 100) {
			coveredH.add(ScenarioWidget.getBar("YELLOW", 300, result.percentCovered));  //NON-NLS
		} else {
			coveredH.add(ScenarioWidget.getBar("GREEN", 300, 100)); //NON-NLS
		}

		coveredH.add(new SmallLabel("&nbsp;" + Format.format(constants.RuleCoveragePercent(), result.percentCovered)));

		layout.addAttribute(constants.RulesCovered(), coveredH);


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

				layout.addAttribute(constants.UncoveredRules(), uncoveredRules);
		}


		layout.endSection();

		layout.startSection(constants.Scenarios());

		layout.addAttribute("", summaryTable);

		Button c = new Button(constants.Close());
		c.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				close.execute();
			}
		});
		layout.addRow(c);

		layout.endSection();

	}


	private void clear() {
		parent.clear();
		layout = new PrettyFormLayout();
		parent.add(layout);

	}

	private void showErrors() {
		clear();
		BuilderResult errors = result.result;

		Panel err = new SimplePanel();

		PackageBuilderWidget.showBuilderErrors(errors, err, editEvent);
		layout.startSection(constants.BuildErrorsUnableToRunScenarios());

		layout.addRow(err);


		layout.endSection();
	}

}
