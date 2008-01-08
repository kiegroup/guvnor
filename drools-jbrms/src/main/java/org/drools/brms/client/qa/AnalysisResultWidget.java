package org.drools.brms.client.qa;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.rpc.AnalysisReport;
import org.drools.brms.client.rpc.AnalysisReportLine;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Shows the results of an analysis run.
 * @author Michael Neale
 */
public class AnalysisResultWidget extends Composite {

	public AnalysisResultWidget(String packageName, AnalysisReport report) {
		FormStyleLayout layout = new FormStyleLayout("images/analyse_large.png", "Analysis of package: " + packageName);

		Tree t = new Tree();

		t.addItem(renderItems(report.errors, "images/error.gif", "Errors"));
		t.addItem(renderItems(report.warnings, "images/warning.gif", "Warnings"));
		t.addItem(renderItems(report.notes, "images/note.gif", "Notes"));

		layout.addRow(t);

		initWidget(layout);
	}

	private TreeItem renderItems(AnalysisReportLine[] lines, String icon, String msg) {
		if (lines.length == 0) {
			TreeItem nil = new TreeItem(new HTML("<i>No " + msg + "</i>"));
			nil.setStyleName("model-builder-Background");
			return nil;
		}
		TreeItem lineNode = new TreeItem(new HTML("<img src='" + icon + "' /> &nbsp;  <b>" + msg + "</b> ("+ lines.length + " items)."));

		lineNode.setStyleName("model-builder-Background");

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
