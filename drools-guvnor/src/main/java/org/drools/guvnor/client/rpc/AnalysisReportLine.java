package org.drools.guvnor.client.rpc;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a single line of an analysis report.
 */
public class AnalysisReportLine
    implements
    IsSerializable {

    public String              description;
    public String              reason;
    public Cause[]             causes;
    public Map<String, String> impactedRules;

    public AnalysisReportLine() {
    }

    public AnalysisReportLine(String description,
                              String reason,
                              Cause[] causes) {
        this.description = description;
        this.reason = reason;
        this.causes = causes;
    }

}
