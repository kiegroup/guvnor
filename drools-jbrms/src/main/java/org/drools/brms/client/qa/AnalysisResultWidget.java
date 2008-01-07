package org.drools.brms.client.qa;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.rpc.AnalysisReport;
import org.drools.brms.client.rpc.AnalysisReportLine;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Shows the results of an analysis run.
 * @author Michael Neale
 */
public class AnalysisResultWidget extends Composite {

	public AnalysisResultWidget(String packageName, AnalysisReport report) {
		FormStyleLayout layout = new FormStyleLayout();

		Tree t = new Tree();
		TreeItem warnings = new TreeItem("Warnings [" + report.warnings.length + "] items.");

		t.addItem(warnings);



		for (int i = 0; i < report.warnings.length; i++) {
			AnalysisReportLine r = report.warnings[i];
			TreeItem w = new TreeItem(r.description);
			TreeItem reason = new TreeItem("Reason: " + r.reason);
			w.addItem(reason);
			TreeItem causes = new TreeItem("Cause:");

			for (int j = 0; j < r.cause.length; j++) {
				causes.addItem(new TreeItem(r.cause[j]));
			}
			if (r.cause.length > 0 ) w.addItem(causes);
			warnings.addItem(w);


		}

		layout.addRow(t);

		warnings.setState(true);
		initWidget(layout);
	}

}
