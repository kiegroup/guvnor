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

import org.drools.guvnor.client.decisiontable.analysis.action.ActionDetector;
import org.drools.guvnor.client.decisiontable.analysis.action.ActionDetectorKey;
import org.drools.guvnor.client.decisiontable.analysis.action.InsertFactActionDetectorKey;
import org.drools.guvnor.client.decisiontable.analysis.action.SetFieldColActionDetectorKey;
import org.drools.guvnor.client.decisiontable.analysis.action.UnrecognizedActionDetectorKey;
import org.drools.guvnor.client.decisiontable.analysis.condition.BooleanConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.ConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.DateConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.EnumConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.NumericConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.StringConditionDetector;
import org.drools.guvnor.client.decisiontable.analysis.condition.UnrecognizedConditionDetector;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
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

    @SuppressWarnings("rawtypes")
    public List<Analysis> analyze(GuidedDecisionTable52 model) {
        List<List<DTCellValue52>> data = model.getData();
        List<Analysis> analysisData = new ArrayList<Analysis>( data.size() );
        List<RowDetector> rowDetectorList = new ArrayList<RowDetector>( data.size() );
        for ( List<DTCellValue52> row : data ) {
            RowDetector rowDetector = new RowDetector( row.get( 0 ).getNumericValue().longValue() - 1 );
            for ( Pattern52 pattern : model.getPatterns() ) {
                for ( ConditionCol52 conditionCol : pattern.getChildColumns() ) {
                    int columnIndex = model.getExpandedColumns().indexOf( conditionCol );
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
                        ConditionDetector conditionDetector = buildConditionDetector(model,
                                pattern, conditionCol,
                                realCellValue);
                        rowDetector.putOrMergeConditionDetector(conditionDetector);
                    }
                }
            }
            for (ActionCol52 actionCol : model.getActionCols()) {
                //BRLActionColumns cannot be analysed
                if(actionCol instanceof BRLActionColumn) {
                    continue;
                }
                int columnIndex = model.getExpandedColumns().indexOf( actionCol );
                DTCellValue52 visibleCellValue = row.get( columnIndex );
                DTCellValue52 realCellValue;
                boolean cellIsNotBlank;
                if ( actionCol instanceof LimitedEntryCol ) {
                    realCellValue = ((LimitedEntryCol) actionCol).getValue();
                    cellIsNotBlank = visibleCellValue.getBooleanValue();
                } else {
                    realCellValue = visibleCellValue;
                    cellIsNotBlank = visibleCellValue.hasValue();
                }
                // Blank cells are ignored
                if ( cellIsNotBlank ) {
                    ActionDetector actionDetector = buildActionDetector(model, actionCol, realCellValue);
                    rowDetector.putOrMergeActionDetector(actionDetector);
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
    private ConditionDetector buildConditionDetector(GuidedDecisionTable52 model,
            Pattern52 pattern, ConditionCol52 conditionCol,
            DTCellValue52 realCellValue) {
        String factField = conditionCol.getFactField();
        String operator = conditionCol.getOperator();
        String type = model.getType( conditionCol,
                                     sce );
        // Retrieve "Guvnor" enums
        String[] allValueList = model.getValueList( conditionCol,
                                                    sce );
        ConditionDetector newDetector;
        if ( allValueList.length != 0 ) {
            // Guvnor enum
            newDetector = new EnumConditionDetector( pattern, factField, Arrays.asList( allValueList ),
                    realCellValue.getStringValue(), operator );
        } else if ( type == null ) {
            // type null means the field is free-format
            newDetector = new UnrecognizedConditionDetector( pattern, factField,
                    operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_STRING ) ) {
            newDetector = new StringConditionDetector( pattern, factField,
                    realCellValue.getStringValue(), operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            newDetector = new NumericConditionDetector( pattern, factField,
                    realCellValue.getNumericValue(), operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            newDetector = new BooleanConditionDetector( pattern, factField,
                    realCellValue.getBooleanValue(), operator );
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            newDetector = new DateConditionDetector( pattern, factField,
                    realCellValue.getDateValue(), operator );
        } else {
            newDetector = new UnrecognizedConditionDetector( pattern, factField,
                    operator );
        }
        return newDetector;
    }

    private ActionDetector buildActionDetector(GuidedDecisionTable52 model,
            ActionCol52 actionCol,
            DTCellValue52 realCellValue) {
        ActionDetectorKey key;
        if (actionCol instanceof ActionSetFieldCol52) {
            key = new SetFieldColActionDetectorKey((ActionSetFieldCol52) actionCol);
        } else if (actionCol instanceof ActionInsertFactCol52) {
            key = new InsertFactActionDetectorKey((ActionInsertFactCol52) actionCol);
        } else {
            key = new UnrecognizedActionDetectorKey(actionCol);
        }
        return new ActionDetector(key, realCellValue);
    }

}
