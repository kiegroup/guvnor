package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnalysisReport implements IsSerializable {
	public AnalysisReportLine[] errors;
	public AnalysisReportLine[] warnings;
	public AnalysisReportLine[] notes;
	public AnalysisFactUsage[] factUsages;
}
