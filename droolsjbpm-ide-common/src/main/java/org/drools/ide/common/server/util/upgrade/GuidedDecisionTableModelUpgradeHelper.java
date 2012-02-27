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
package org.drools.ide.common.server.util.upgrade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

/**
 * Helper class to upgrade model used for Guided Decision Table
 */
@SuppressWarnings("deprecation")
public class GuidedDecisionTableModelUpgradeHelper
    implements
    IUpgradeHelper<GuidedDecisionTable52, GuidedDecisionTable> {

    /**
     * Convert the legacy Decision Table model to the new
     * 
     * @param legacyDTModel
     * @return The new DTModel
     */
    public GuidedDecisionTable52 upgrade(GuidedDecisionTable legacyDTModel) {

        assertConditionColumnPatternGrouping( legacyDTModel );

        GuidedDecisionTable52 newDTModel = new GuidedDecisionTable52();

        newDTModel.setTableName( legacyDTModel.tableName );
        newDTModel.setParentName( legacyDTModel.parentName );

        newDTModel.setRowNumberCol( new RowNumberCol52() );
        newDTModel.setDescriptionCol( new DescriptionCol52() );

        //Metadata columns' data-type is implicitly defined in the metadata value. For example
        //a String metadata attribute is: "value", a numerical: 1. No conversion action required
        for ( MetadataCol c : legacyDTModel.getMetadataCols() ) {
            newDTModel.getMetadataCols().add( makeNewColumn( c ) );
        }

        //Attribute columns' data-type is based upon the attribute name
        for ( AttributeCol c : legacyDTModel.attributeCols ) {
            newDTModel.getAttributeCols().add( makeNewColumn( c ) );
        }

        //Legacy decision tables did not have Condition field data-types. Set all Condition 
        //fields to a *sensible* default of String (as this matches legacy behaviour).
        List<Pattern52> patterns = new ArrayList<Pattern52>();
        Map<String, Pattern52> uniquePatterns = new HashMap<String, Pattern52>();
        for ( int i = 0; i < legacyDTModel.conditionCols.size(); i++ ) {
            ConditionCol c = legacyDTModel.conditionCols.get( i );
            String boundName = c.boundName;
            Pattern52 p = uniquePatterns.get( boundName );
            if ( p == null ) {
                p = new Pattern52();
                p.setBoundName( boundName );
                p.setFactType( c.factType );
                patterns.add( p );
                uniquePatterns.put( boundName,
                                    p );
            }
            if ( p.getFactType() != null && !p.getFactType().equals( c.factType ) ) {
                throw new IllegalArgumentException( "Inconsistent FactTypes for ConditionCols bound to '" + boundName + "' detected." );
            }
            p.getConditions().add( makeNewColumn( c ) );
        }
        for ( Pattern52 p : patterns ) {
            newDTModel.getConditionPatterns().add( p );
        }

        //Action columns have a discrete data-type. No conversion action required.
        for ( ActionCol c : legacyDTModel.actionCols ) {
            newDTModel.getActionCols().add( makeNewColumn( c ) );
        }

        //Copy across data
        newDTModel.setData( makeDataLists( legacyDTModel.data ) );

        return newDTModel;
    }

    // Ensure Condition columns are grouped by pattern (as we merge equal
    // patterns in the UI). This operates on the original Model data and
    // therefore should be called before the Decision Table's internal data
    // representation (i.e. DynamicData, DynamicDataRow and CellValue) is
    // populated
    private void assertConditionColumnPatternGrouping(GuidedDecisionTable model) {

        class ConditionColData {
            ConditionCol col;
            String[]     data;
        }

        // Offset into Model's data array
        final int DATA_COLUMN_OFFSET = model.getMetadataCols().size() + model.attributeCols.size() + GuidedDecisionTable.INTERNAL_ELEMENTS;
        Map<String, List<ConditionColData>> uniqueGroups = new TreeMap<String, List<ConditionColData>>();
        List<List<ConditionColData>> groups = new ArrayList<List<ConditionColData>>();
        final int DATA_ROWS = model.data.length;

        // Copy conditions and related data into temporary groups
        for ( int iCol = 0; iCol < model.conditionCols.size(); iCol++ ) {

            ConditionCol col = model.conditionCols.get( iCol );
            String pattern = col.boundName + "";
            List<ConditionColData> groupCols = uniqueGroups.get( pattern );
            if ( groupCols == null ) {
                groupCols = new ArrayList<ConditionColData>();
                groups.add( groupCols );
                uniqueGroups.put( pattern,
                                  groupCols );
            }

            // Make a ConditionColData object
            ConditionColData ccd = new ConditionColData();
            int colIndex = DATA_COLUMN_OFFSET + iCol;
            ccd.data = new String[DATA_ROWS];
            for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                String[] row = model.data[iRow];
                ccd.data[iRow] = row[colIndex];
            }
            ccd.col = col;
            groupCols.add( ccd );
        }

        // Copy temporary groups back into the model
        int iCol = 0;
        model.conditionCols.clear();
        for ( List<ConditionColData> me : groups ) {
            for ( ConditionColData ccd : me ) {
                model.conditionCols.add( ccd.col );
                int colIndex = DATA_COLUMN_OFFSET + iCol;
                for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                    String[] row = model.data[iRow];
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
    public List<List<DTCellValue52>> makeDataLists(String[][] oldData) {
        List<List<DTCellValue52>> newData = new ArrayList<List<DTCellValue52>>();
        for ( int iRow = 0; iRow < oldData.length; iRow++ ) {
            String[] oldRow = oldData[iRow];
            List<DTCellValue52> newRow = makeDataRowList( oldRow );
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
    public List<DTCellValue52> makeDataRowList(String[] oldRow) {
        List<DTCellValue52> row = new ArrayList<DTCellValue52>();

        DTCellValue52 rowDcv = new DTCellValue52( new BigDecimal( oldRow[0] ) );
        row.add( rowDcv );

        for ( int iCol = 1; iCol < oldRow.length; iCol++ ) {

            //The original model was purely String based. Conversion to typed fields
            //occurs when the Model is re-saved in Guvnor. Ideally the conversion 
            //should occur here but that requires reference to a SuggestionCompletionEngine
            //which requires RepositoryServices. I did not want to make a dependency between
            //common IDE classes and the Repository
            DTCellValue52 dcv = new DTCellValue52( oldRow[iCol] );
            row.add( dcv );
        }
        return row;
    }

    private AttributeCol52 makeNewColumn(AttributeCol c) {
        AttributeCol52 nc = new AttributeCol52();
        nc.setAttribute( c.attr );
        nc.setDefaultValue( c.defaultValue );
        nc.setHideColumn( c.hideColumn );
        nc.setReverseOrder( c.reverseOrder );
        nc.setUseRowNumber( c.useRowNumber );
        nc.setWidth( c.width );
        return nc;
    }

    private MetadataCol52 makeNewColumn(MetadataCol c) {
        MetadataCol52 nc = new MetadataCol52();
        nc.setDefaultValue( c.defaultValue );
        nc.setHideColumn( c.hideColumn );
        nc.setMetadata( c.attr );
        nc.setWidth( c.width );
        return nc;
    }

    private ConditionCol52 makeNewColumn(ConditionCol c) {
        ConditionCol52 nc = new ConditionCol52();
        nc.setConstraintValueType( c.constraintValueType );
        nc.setDefaultValue( c.defaultValue );
        nc.setFactField( c.factField );
        nc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setOperator( c.operator );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionCol52 makeNewColumn(ActionCol c) {
        if ( c instanceof ActionInsertFactCol ) {
            return makeNewColumn( (ActionInsertFactCol) c );
        } else if ( c instanceof ActionRetractFactCol ) {
            return makeNewColumn( (ActionRetractFactCol) c );
        } else if ( c instanceof ActionSetFieldCol ) {
            return makeNewColumn( (ActionSetFieldCol) c );
        }
        ActionCol52 nc = new ActionCol52();
        nc.setDefaultValue( c.defaultValue );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionInsertFactCol52 makeNewColumn(ActionInsertFactCol c) {
        ActionInsertFactCol52 nc = new ActionInsertFactCol52();
        nc.setBoundName( c.boundName );
        nc.setDefaultValue( c.defaultValue );
        nc.setFactField( c.factField );
        nc.setFactType( c.factType );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setType( c.type );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionRetractFactCol52 makeNewColumn(ActionRetractFactCol c) {
        ActionRetractFactCol52 nc = new ActionRetractFactCol52();
        nc.setBoundName( c.boundName );
        nc.setDefaultValue( c.defaultValue );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setWidth( c.width );
        return nc;

    }

    private ActionSetFieldCol52 makeNewColumn(ActionSetFieldCol c) {
        ActionSetFieldCol52 nc = new ActionSetFieldCol52();
        nc.setBoundName( c.boundName );
        nc.setDefaultValue( c.defaultValue );
        nc.setFactField( c.factField );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setType( c.type );
        nc.setUpdate( c.update );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

}
