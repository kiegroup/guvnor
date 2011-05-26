/*
 * Copyright 2011 JBoss Inc
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
package org.drools.ide.common.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt.DTCellValue;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.Pattern;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable52;

/**
 * Helper class to upgrade model used for Guided Decision Table
 */
@SuppressWarnings("deprecation")
public class RepositoryUpgradeHelper {

    /**
     * Convert the legacy Decision Table model to the new
     * 
     * @param legacyDTModel
     * @return The new DTModel
     */
    public static GuidedDecisionTable52 convertGuidedDTModel(GuidedDecisionTable legacyDTModel) {

        GuidedDecisionTable52 newDTModel = new GuidedDecisionTable52();

        newDTModel.setTableName( legacyDTModel.getTableName() );
        newDTModel.setParentName( legacyDTModel.getParentName() );

        newDTModel.setRowNumberCol( legacyDTModel.getRowNumberCol() );
        newDTModel.setDescriptionCol( legacyDTModel.getDescriptionCol() );

        //Metadata columns' data-type is implicitly defined in the metadata value. For example
        //a String metadata attribute is: "value", a numerical: 1. No conversion action required
        for ( MetadataCol c : legacyDTModel.getMetadataCols() ) {
            newDTModel.getMetadataCols().add( c );
        }

        //Attribute columns' data-type is based upon the attribute name
        for ( AttributeCol c : legacyDTModel.getAttributeCols() ) {
            newDTModel.getAttributeCols().add( c );
        }

        //Legacy decision tables did not have Condition field data-types. Set all Condition 
        //fields to a *sensible* default of String (as this matches legacy behaviour).
        assertConditionColumnPatternGrouping( legacyDTModel );
        Map<String, Pattern> patterns = new HashMap<String, Pattern>();
        for ( int i = 0; i < legacyDTModel.getConditionCols().size(); i++ ) {
            ConditionCol52 c = legacyDTModel.getConditionCols().get( i );
            String boundName = c.getBoundName();
            Pattern p = patterns.get( boundName );
            if ( p == null ) {
                p = new Pattern();
                p.setBoundName( boundName );
                p.setFactType( c.getFactType() );
                patterns.put( boundName,
                              p );
            }
            if ( p.getFactType() != null && !p.getFactType().equals( c.getFactType() ) ) {
                throw new IllegalArgumentException( "Inconsistent FactTypes for ConditionCols bound to '" + boundName + "' detected." );
            }
            c.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
            p.getConditions().add( c );
        }
        for ( Pattern p : patterns.values() ) {
            newDTModel.getConditionPatterns().add( p );
        }

        //Action columns have a discrete data-type. No conversion action required.
        for ( ActionCol c : legacyDTModel.getActionCols() ) {
            newDTModel.getActionCols().add( c );
        }

        //Copy across data
        newDTModel.setData( makeDataLists( legacyDTModel.getData() ) );

        return newDTModel;
    }

    // Ensure Condition columns are grouped by pattern (as we merge equal
    // patterns in the UI). This operates on the original Model data and
    // therefore should be called before the Decision Table's internal data
    // representation (i.e. DynamicData, DynamicDataRow and CellValue) is
    // populated
    private static void assertConditionColumnPatternGrouping(GuidedDecisionTable model) {

        class ConditionColData {
            ConditionCol52 col;
            String[]     data;
        }

        // Offset into Model's data array
        final int DATA_COLUMN_OFFSET = model.getMetadataCols().size() + model.getAttributeCols().size() + GuidedDecisionTable.INTERNAL_ELEMENTS;
        Map<String, List<ConditionColData>> groups = new HashMap<String, List<ConditionColData>>();
        final int DATA_ROWS = model.getData().length;

        // Copy conditions and related data into temporary groups
        for ( int iCol = 0; iCol < model.getConditionCols().size(); iCol++ ) {

            ConditionCol52 col = model.getConditionCols().get( iCol );
            String pattern = col.getBoundName();
            if ( !groups.containsKey( pattern ) ) {
                List<ConditionColData> groupCols = new ArrayList<ConditionColData>();
                groups.put( pattern,
                            groupCols );
            }
            List<ConditionColData> groupCols = groups.get( pattern );

            // Make a ConditionColData object
            ConditionColData ccd = new ConditionColData();
            int colIndex = DATA_COLUMN_OFFSET + iCol;
            ccd.data = new String[DATA_ROWS];
            for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                String[] row = model.getData()[iRow];
                ccd.data[iRow] = row[colIndex];
            }
            ccd.col = col;
            groupCols.add( ccd );

        }

        // Copy temporary groups back into the model
        int iCol = 0;
        model.getConditionCols().clear();
        for ( Map.Entry<String, List<ConditionColData>> me : groups.entrySet() ) {
            for ( ConditionColData ccd : me.getValue() ) {
                model.getConditionCols().add( ccd.col );
                int colIndex = DATA_COLUMN_OFFSET + iCol;
                for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                    String[] row = model.getData()[iRow];
                    row[colIndex] = ccd.data[iRow];
                }
                iCol++;
            }
        }

    }

    /**
     * Convert a two-dimensional array of Strings to a List of Lists, with
     * type-safe individual entries
     * 
     * @param oldData
     * @return New data
     */
    public static List<List<DTCellValue>> makeDataLists(String[][] oldData) {
        List<List<DTCellValue>> newData = new ArrayList<List<DTCellValue>>();
        for ( int iRow = 0; iRow < oldData.length; iRow++ ) {
            String[] oldRow = oldData[iRow];
            List<DTCellValue> newRow = makeDataRowList( oldRow );
            newData.add( newRow );
        }
        return newData;
    }

    /**
     * Convert a single dimension array of Strings to a List with type-safe
     * entries. The first entry is converted into a numerical row number
     * 
     * @param oldRow
     * @return New row
     */
    public static List<DTCellValue> makeDataRowList(String[] oldRow) {
        List<DTCellValue> row = new ArrayList<DTCellValue>();

        DTCellValue rowDcv = new DTCellValue( new BigDecimal( oldRow[0] ) );
        row.add( rowDcv );

        for ( int iCol = 1; iCol < oldRow.length; iCol++ ) {

            //The original model was purely String based. Conversion to typed fields
            //occurs when the Model is re-saved in Guvnor. Ideally the conversion 
            //should occur here but that requires reference to a SuggestionCompletionEngine
            //which requires RepositoryServices. I did not want to make a dependency between
            //common IDE classes and the Repository
            DTCellValue dcv = new DTCellValue( oldRow[iCol] );
            row.add( dcv );
        }
        return row;
    }

}
