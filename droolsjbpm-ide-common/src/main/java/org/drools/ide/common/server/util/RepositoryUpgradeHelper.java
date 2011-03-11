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

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable.DTCellValue;

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
    public static TypeSafeGuidedDecisionTable convertGuidedDTModel(GuidedDecisionTable legacyDTModel) {

        TypeSafeGuidedDecisionTable newDTModel = new TypeSafeGuidedDecisionTable();

        newDTModel.setTableName( legacyDTModel.getTableName() );
        newDTModel.setParentName( legacyDTModel.getParentName() );
        newDTModel.setRowNumberCol( legacyDTModel.getRowNumberCol() );
        newDTModel.setDescriptionCol( legacyDTModel.getDescriptionCol() );

        newDTModel.getMetadataCols().addAll( legacyDTModel.getMetadataCols() );
        newDTModel.getAttributeCols().addAll( legacyDTModel.getAttributeCols() );
        newDTModel.getConditionCols().addAll( legacyDTModel.getConditionCols() );
        newDTModel.getActionCols().addAll( legacyDTModel.getActionCols() );

        newDTModel.setData( makeDataLists( legacyDTModel.getData() ) );

        return newDTModel;
    }

    /**
     * Convert a two-dimensional array of Strings to a List of Lists, with
     * type-safe individual entries
     * 
     * @param oldData
     * @return New data
     */
    public static List<List<DTCellValue< ? >>> makeDataLists(String[][] oldData) {
        List<List<DTCellValue< ? >>> newData = new ArrayList<List<DTCellValue< ? >>>();
        for ( int iRow = 0; iRow < oldData.length; iRow++ ) {
            String[] oldRow = oldData[iRow];
            List<DTCellValue< ? >> newRow = makeDataRowList( oldRow );
            newData.add( newRow );
        }
        return newData;
    }

    /**
     * Convert a single dimension array of Strings to a List with type-safe
     * entries
     * 
     * @param oldRow
     * @return New row
     */
    public static List<DTCellValue< ? >> makeDataRowList(String[] oldRow) {
        List<DTCellValue< ? >> row = new ArrayList<DTCellValue< ? >>();
        for ( int iCol = 0; iCol < oldRow.length; iCol++ ) {

            //The original model was purely String based. Conversion to typed fields
            //occurs when the Model is re-saved in Guvnor. Ideally the conversion 
            //should occur here but that requires reference to a SuggestionCompletionEngine
            //which requires RepositoryServices. I did not want to make a dependency between
            //common IDE classes and the Repository
            DTCellValue<String> dcv = new DTCellValue<String>();
            dcv.setValue( oldRow[iCol] );
            row.add( dcv );
        }
        return row;
    }
}
