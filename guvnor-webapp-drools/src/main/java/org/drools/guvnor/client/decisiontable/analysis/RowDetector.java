/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.decisiontable.analysis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

public class RowDetector {

    private long rowIndex;

    private Map<Pattern52, Map<String, FieldDetector>> fieldDetectorMap
            = new LinkedHashMap<Pattern52, Map<String, FieldDetector>>();

    public RowDetector(long rowIndex) {
        this.rowIndex = rowIndex;
    }

    public long getRowIndex() {
        return rowIndex;
    }

    public FieldDetector getFieldDetector(Pattern52 pattern, String factField) {
        Map<String, FieldDetector> subMap = fieldDetectorMap.get(pattern);
        if (subMap == null) {
            return null;
        }
        return subMap.get(factField);
    }

    public void putOrMergeFieldDetector(Pattern52 pattern, String factField, FieldDetector fieldDetector) {
        Map<String, FieldDetector> subMap = fieldDetectorMap.get(pattern);
        if (subMap == null) {
            subMap = new LinkedHashMap<String, FieldDetector>();
            fieldDetectorMap.put(pattern, subMap);
        }
        FieldDetector originalFieldDetector = subMap.get(factField);
        FieldDetector mergedFieldDetector;
        if (originalFieldDetector == null) {
            mergedFieldDetector = fieldDetector;
        } else {
            mergedFieldDetector = originalFieldDetector.merge(fieldDetector);
        }
        subMap.put(factField, mergedFieldDetector);
    }

    public Analysis buildAnalysis(List<RowDetector> rowDetectorList) {
        Analysis analysis = new Analysis();
        detectImpossibleMatch(analysis);
        for (RowDetector otherRowDetector : rowDetectorList) {
            if (this != otherRowDetector) {
                detectConflict(analysis, otherRowDetector);
            }
        }
        return analysis;
    }

    private void detectImpossibleMatch(Analysis analysis) {
        for (Map.Entry<Pattern52, Map<String, FieldDetector>> entry : fieldDetectorMap.entrySet()) {
            Pattern52 pattern = entry.getKey();
            for (Map.Entry<String, FieldDetector> subEntry : entry.getValue().entrySet()) {
                String factField = subEntry.getKey();
                FieldDetector fieldDetector = subEntry.getValue();
                if (fieldDetector.isImpossibleMatch()) {
                    analysis.addImpossibleMatch("Impossible match on " + factField);
                }
            }
        }
    }

    private void detectConflict(Analysis analysis, RowDetector otherRowDetector) {
        boolean overlapping = true;
        boolean hasUnrecognized = false;
        for (Map.Entry<Pattern52, Map<String, FieldDetector>> entry : fieldDetectorMap.entrySet()) {
            Pattern52 pattern = entry.getKey();
            for (Map.Entry<String, FieldDetector> subEntry : entry.getValue().entrySet()) {
                String factField = subEntry.getKey();
                FieldDetector fieldDetector = subEntry.getValue();
                FieldDetector otherFieldDetector = otherRowDetector.getFieldDetector(pattern, factField);
                if (otherFieldDetector != null) {
                    FieldDetector mergedFieldDetector = fieldDetector.merge(otherFieldDetector);
                    if (mergedFieldDetector.isImpossibleMatch()) {
                        // If 1 field is in both and not overlapping then the entire 2 rows are not overlapping
                        overlapping = false;
                    }
                    if (mergedFieldDetector.hasUnrecognizedConstraint()) {
                        // If 1 field is in both and unrecognized, they might or might not be overlapping
                        hasUnrecognized = true;
                    }
                }
            }
        }
        if (overlapping) {
            if (!hasUnrecognized) {
                analysis.addConflictingMatch("Conflicting match with row " + (otherRowDetector.getRowIndex() + 1));
            } else {
                System.out.println("Possible conflicting match with row " + (otherRowDetector.getRowIndex() + 1));
            }
        }
    }

}
