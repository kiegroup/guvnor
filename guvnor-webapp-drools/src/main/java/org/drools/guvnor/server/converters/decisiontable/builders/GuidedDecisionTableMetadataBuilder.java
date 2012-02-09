/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.converters.decisiontable.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.template.parser.DecisionTableParseException;

/**
 * Builder for Metadata columns
 */
public class GuidedDecisionTableMetadataBuilder
    implements
    GuidedDecisionTableSourceBuilder {

    protected int                  headerRow;
    protected int                  headerCol;
    protected Map<Integer, String> definitions;
    protected List<DTCellValue52>  values;

    public GuidedDecisionTableMetadataBuilder(int row,
                                              int column) {
        this.headerRow = row;
        this.headerCol = column;
        this.definitions = new HashMap<Integer, String>();
        this.values = new ArrayList<DTCellValue52>();
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        MetadataCol52 column = new MetadataCol52();
        String value = this.definitions.get( headerCol );
        column.setHideColumn( true );
        column.setMetadata( value );
        dtable.getMetadataCols().add( column );
        addColumnData( dtable,
                       column );
    }

    private void addColumnData(GuidedDecisionTable52 dtable,
                               DTColumnConfig52 column) {

        int rowCount = this.values.size();
        int iColIndex = dtable.getExpandedColumns().indexOf( column );

        //Add column data
        for ( int iRow = 0; iRow < rowCount; iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.add( iColIndex,
                         this.values.get( iRow ) );
        }
    }

    public void addTemplate(int row,
                            int column,
                            String content) {
        if ( definitions.containsKey( column ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a code snippet added twice to one spreadsheet column." );
        }
        definitions.put( column,
                         content.trim() );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        String content = this.definitions.get( column );
        if ( content == null ) {
            throw new DecisionTableParseException( "No code snippet for METADATA in cell " + RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                                                          this.headerCol ) );
        }
        DTCellValue52 dcv = new DTCellValue52( value );
        this.values.add( dcv );
    }

    public ActionType.Code getActionTypeCode() {
        return ActionType.Code.METADATA;
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableMetadataBuilder does not return DRL." );
    }

}
