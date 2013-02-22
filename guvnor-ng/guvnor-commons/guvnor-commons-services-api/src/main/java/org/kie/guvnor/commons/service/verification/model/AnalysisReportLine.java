/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.commons.service.verification.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.Map;

/**
 * This is a single line of an analysis report.
 */
@Portable
public class AnalysisReportLine {

    private String              description;
    private String              reason;
    private Integer             patternOrderNumber;
    private Cause[]             causes;
    private Map<String, String> impactedRules;

    public AnalysisReportLine() {
    }

    public AnalysisReportLine( String description,
                               String reason,
                               Cause[] causes ) {
        this.description = description;
        this.reason = reason;
        this.causes = causes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason( String reason ) {
        this.reason = reason;
    }

    public Integer getPatternOrderNumber() {
        return patternOrderNumber;
    }

    public void setPatternOrderNumber( Integer patternOrderNumber ) {
        this.patternOrderNumber = patternOrderNumber;
    }

    public Cause[] getCauses() {
        return causes;
    }

    public void setCauses( Cause[] causes ) {
        this.causes = causes;
    }

    public Map<String, String> getImpactedRules() {
        return impactedRules;
    }

    public void setImpactedRules( Map<String, String> impactedRules ) {
        this.impactedRules = impactedRules;
    }
}
