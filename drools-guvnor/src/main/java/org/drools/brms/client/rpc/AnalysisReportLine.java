package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a single line of an analysis report.
 */
public class AnalysisReportLine implements IsSerializable {

	public String description;
	public String reason;
	public String[] cause;

	public AnalysisReportLine() {}
	public AnalysisReportLine(String description, String reason, String[] cause) {
		this.description = description;
		this.reason = reason;
		this.cause = cause;
	}



}
