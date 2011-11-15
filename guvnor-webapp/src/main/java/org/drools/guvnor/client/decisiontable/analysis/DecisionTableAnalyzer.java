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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<Analysis> analyze(GuidedDecisionTable52 model) {
        return detectImpossibleMatches( model );
    }

    @SuppressWarnings("rawtypes")
    private List<Analysis> detectImpossibleMatches(GuidedDecisionTable52 model) {
        List<List<DTCellValue52>> data = model.getData();
        List<Analysis> analysisData = new ArrayList<Analysis>( data.size() );
        List<RowDetector> rowDetectorList = new ArrayList<RowDetector>( data.size() );
        for ( List<DTCellValue52> row : data ) {
            RowDetector rowDetector = new RowDetector( row.get( 0 ).getNumericValue().longValue() - 1 );
            for ( Pattern52 pattern : model.getConditionPatterns() ) {
                List<ConditionCol52> conditions = pattern.getConditions();
                for ( ConditionCol52 conditionCol : conditions ) {
                    int columnIndex = model.getAllColumns().indexOf( conditionCol );
                    DTCellValue52 visibleCellValue = row.get( columnIndex );
                    DTCellValue52 realCellValue;
                    boolean cellIsNotBlank;
                    if ( conditionCol instanceof LimitedEntryCol ) {
                        realCellValue = ((LimitedEntryCol) conditionCol).getValue();
                        cellIsNotBlank = visibleCellValue.getBooleanValue();
                    } else {
                        realCellValue = visibleCellValue;
                        cellIsNotBlank = visibleCellValue.hasValue();
                    }
                    // Blank cells are ignored
                    if ( cellIsNotBlank ) {
                        FieldDetector fieldDetector = buildDetector( model,
                                                                     conditionCol,
                                                                     realCellValue );
                        String factField = conditionCol.getFactField();
                        rowDetector.putOrMergeFieldDetector( pattern,
                                                             factField,
                                                             fieldDetector );
                    }
                }
            }
            rowDetectorList.add( rowDetector );
        }
        for ( RowDetector rowDetector : rowDetectorList ) {
            analysisData.add( rowDetector.buildAnalysis( rowDetectorList ) );
        }
        return analysisData;
    }

    @SuppressWarnings("rawtypes")
    private FieldDetector buildDetector(GuidedDecisionTable52 model,
                                        ConditionCol52 conditionCol,
                                        DTCellValue52 realCellValue) {
        String operator = conditionCol.getOperator();
        String type = model.getType( conditionCol,
                                     sce );
        // Retrieve "Guvnor" enums
        String[] allValueList = model.getValueList( conditionCol,
                                                    sce );
        FieldDetector newDetector;
        if ( allValueList.length != 0 ) {
            // Guvnor enum
            newDetector = new EnumFieldDetector( Arrays.asList( allValueList ),
                                                 realCellValue.getStringValue(),
                                                 operator );
        } else if ( type == null ) {
            // type null means the field is free-format
            newDetector = new UnrecognizedFieldDetector( operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_STRING ) ) {
            newDetector = new StringFieldDetector( realCellValue.getStringValue(),
                                                   operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            newDetector = new NumericFieldDetector( realCellValue.getNumericValue(),
                                                    operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            newDetector = new BooleanFieldDetector( realCellValue.getBooleanValue(),
                                                    operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            newDetector = new DateFieldDetector( realCellValue.getDateValue(),
                                                 operator );
        } else {
            newDetector = new UnrecognizedFieldDetector( operator );
        }
        return newDetector;
    }

}
