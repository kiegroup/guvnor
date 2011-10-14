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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.cells.PopupDropDownEditCell;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

public class DecisionTableAnalyzer {

    private SuggestionCompletionEngine sce;

    public DecisionTableAnalyzer(SuggestionCompletionEngine sce) {
        this.sce = sce;
    }

    public List<Analysis> analyze(GuidedDecisionTable52 modelWithWrongData, List<List<DTCellValue52>> data) {
        return detectImpossibleMatches(modelWithWrongData, data);
    }

    private List<Analysis> detectImpossibleMatches(GuidedDecisionTable52 modelWithWrongData, List<List<DTCellValue52>> data) {
        List<Analysis> analysisData = new ArrayList<Analysis>(data.size());
        for (List<DTCellValue52> row : data) {
            long rowIndex = row.get(0).getNumericValue().longValue();
            Analysis analysis = new Analysis();
            analysisData.add(analysis);
            for (Pattern52 pattern : modelWithWrongData.getConditionPatterns()) {
                List<ConditionCol52> conditions = pattern.getConditions();
                Map<String, DisjointDetector> detectorMap = new HashMap<String, DisjointDetector>(
                        conditions.size());
                for (ConditionCol52 conditionCol : conditions) {
                    int columnIndex = modelWithWrongData.getAllColumns().indexOf(conditionCol);
                    DTCellValue52 value = row.get(columnIndex);
                    if (value.hasValue()) {
                        DisjointDetector newDetector = buildDetector(modelWithWrongData, conditionCol, value);
                        if (newDetector != null) {
                            String factField = conditionCol.getFactField();
                            DisjointDetector detector = detectorMap.get(factField);
                            if (detector == null) {
                                detector = newDetector;
                                detectorMap.put(factField, detector);
                            } else {
                                boolean previousImpossibleMatch = detector.isImpossibleMatch();
                                detector.merge(newDetector);
                                if (!previousImpossibleMatch && detector.isImpossibleMatch()) {
                                    analysis.addWarning("Impossible match on " + factField);
                                }
                            }
                        }
                    }
                }
            }
        }
        return analysisData;
    }

    private DisjointDetector buildDetector(GuidedDecisionTable52 model, ConditionCol52 conditionCol,
            DTCellValue52 value) {
        DisjointDetector newDetector;
        String operator = conditionCol.getOperator();
        if (conditionCol instanceof LimitedEntryCol) {
            newDetector = new BooleanDisjointDetector(value.getBooleanValue(), operator);
        } else {
            //Extended Entry...
            String type = model.getType( conditionCol, sce );
            //Retrieve "Guvnor" enums
            String[] allValueList = model.getValueList( conditionCol, sce );
            if (allValueList.length != 0) {
                // use vals
                newDetector = new EnumDisjointDetector(allValueList, value.getStringValue(), operator);
            } else if ( type == null ) {
                // Null means the field is free-format
                newDetector = null;
            } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                newDetector = new NumericDisjointDetector(value.getNumericValue(), operator);
            } else if ( type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
                newDetector = new BooleanDisjointDetector(value.getBooleanValue(), operator);
            } else if ( type.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
                newDetector = new DateDisjointDetector(value.getDateValue(), operator);
            } else {
                newDetector = null;
            }
        }
        return newDetector;
    }

}
