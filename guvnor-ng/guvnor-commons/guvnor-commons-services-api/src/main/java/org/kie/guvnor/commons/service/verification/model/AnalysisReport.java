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

@Portable
public class AnalysisReport {

    private AnalysisReportLine[] errors;
    private AnalysisReportLine[] warnings;
    private AnalysisReportLine[] notes;
    private AnalysisFactUsage[]  factUsages;

    public AnalysisReport() {

    }

    public AnalysisReportLine[] getErrors() {
        return errors;
    }

    public void setErrors( AnalysisReportLine[] errors ) {
        this.errors = errors;
    }

    public AnalysisReportLine[] getWarnings() {
        return warnings;
    }

    public void setWarnings( AnalysisReportLine[] warnings ) {
        this.warnings = warnings;
    }

    public AnalysisReportLine[] getNotes() {
        return notes;
    }

    public void setNotes( AnalysisReportLine[] notes ) {
        this.notes = notes;
    }

    public AnalysisFactUsage[] getFactUsages() {
        return factUsages;
    }

    public void setFactUsages( AnalysisFactUsage[] factUsages ) {
        this.factUsages = factUsages;
    }
}
