package org.drools.brms.client.qa;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.rpc.AnalysisFactUsage;
import org.drools.brms.client.rpc.AnalysisFieldUsage;
import org.drools.brms.client.rpc.AnalysisReport;
import org.drools.brms.client.rpc.AnalysisReportLine;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows the results of an analysis run.
 * @author Michael Neale
 */
public class AnalysisResultWidget extends Composite {

	public AnalysisResultWidget(AnalysisReport report) {
		FormStyleLayout layout = new FormStyleLayout();

		Tree t = new Tree();

		t.addItem(renderItems(report.errors, "images/error.gif", "Errors"));
		t.addItem(renderItems(report.warnings, "images/warning.gif", "Warnings"));
		t.addItem(renderItems(report.notes, "images/note.gif", "Notes"));
		t.addItem(renderFactUsage(report.factUsages));
		t.addTreeListener(swapTitleWithUserObject());
		layout.addRow(t);



		initWidget(layout);
	}

	private TreeListener swapTitleWithUserObject() {
		return new TreeListener() {
			public void onTreeItemSelected(TreeItem x) {}

			//swap around with user object to toggle
			public void onTreeItemStateChanged(TreeItem x) {
				if (x.getUserObject() != null) {
					Widget currentW = x.getWidget();
					x.setWidget((Widget)x.getUserObject());
					x.setUserObject(currentW);
				}
			}
		};
	}

	private TreeItem renderFactUsage(AnalysisFactUsage[] factUsages) {

		TreeItem root = new TreeItem(new HTML("<img src='images/fact_template.gif'/><b>Show fact usages...</b>"));
		root.setUserObject(new HTML("<img src='images/fact_template.gif'/><b>Fact usages:</b>"));
		root.setStyleName("analysis-Report");


		for (int i = 0; i < factUsages.length; i++) {

			System.err.println("fact usage !");
			AnalysisFactUsage fu = factUsages[i];
			TreeItem fact = new TreeItem(new HTML("<img src='images/fact.gif'/>" + fu.name));

			TreeItem fieldList = new TreeItem(new HTML("<i>Fields used:</i>"));

			for (int j = 0; j < fu.fields.length; j++) {
				AnalysisFieldUsage fiu = fu.fields[j];
				TreeItem field = new TreeItem(new HTML("<img src='images/field.gif'/>" + fiu.name));
				fieldList.addItem(field);
				TreeItem ruleList = new TreeItem(new HTML("<i>Show rules affected ...</i>"));
				ruleList.setUserObject(new HTML("<i>Rules affected:</i>"));
				for (int k = 0; k < fiu.rules.length; k++) {
					ruleList.addItem(new TreeItem(new HTML("<img src='images/rule_asset.gif'/>" + fiu.rules[k])));
				}
				field.addItem(ruleList);
				field.setState(true);
			}

			fact.addItem(fieldList);
			fieldList.setState(true);

			root.addItem(fact);
			fact.setState(true);
		}




		return root;
	}

	private TreeItem renderItems(AnalysisReportLine[] lines, String icon, String msg) {
		if (lines.length == 0) {
			TreeItem nil = new TreeItem(new HTML("<i>No " + msg + "</i>"));
			nil.setStyleName("analysis-Report");
			return nil;
		}
		TreeItem lineNode = new TreeItem(new HTML("<img src='" + icon + "' /> &nbsp;  <b>" + msg + "</b> ("+ lines.length + " items)."));

		lineNode.setStyleName("analysis-Report");

		for (int i = 0; i < lines.length; i++) {
			AnalysisReportLine r = lines[i];
			TreeItem w = new TreeItem(new HTML(r.description));
			w.addItem(new TreeItem(new HTML("<b>Reason:</b>&nbsp;" + r.reason)));
			TreeItem causes = new TreeItem(new HTML("<b>Cause:</b>"));

			for (int j = 0; j < r.cause.length; j++) {
 				causes.addItem(new HTML(r.cause[j]));
			}
			if (r.cause.length > 0 ) {
				w.addItem(causes);
				causes.setState(true);
			}
			lineNode.addItem(w);
		}
		lineNode.setState(true);
		return lineNode;
	}

}
