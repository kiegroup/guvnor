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
package org.drools.guvnor.server.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.template.parser.DecisionTableParseException;

/**
 * 
 */
public class GuidedDecisionTableGenericBuilder
    implements
    GuidedDecisionTableBuilder {

    private int                  headerRow;
    private int                  headerCol;
    private ActionType.Code      actionType;
    private Map<Integer, String> definitions;
    private List<DTCellValue52>  values;

    public GuidedDecisionTableGenericBuilder(int row,
                                             int column,
                                             ActionType.Code actionType) {
        this.headerRow = row;
        this.headerCol = column;
        this.actionType = actionType;
        this.definitions = new HashMap<Integer, String>();
        this.values = new ArrayList<DTCellValue52>();
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        DTColumnConfig52 column = getColumn();

        //Null columns are not convertible
        if ( column == null ) {
            return;
        }

        addColumn( dtable,
                   column );
        addColumnData( dtable,
                       column );
    }

    private DTColumnConfig52 getColumn() {
        DTColumnConfig52 column = null;
        switch ( actionType ) {
            case NAME :
                break;
            case DESCRIPTION :
                column = new DescriptionCol52();
                break;
            case METADATA :
                column = new MetadataCol52();
                String value = this.definitions.get( headerCol );
                ((MetadataCol52) column).setMetadata( value );
                break;
            case ACTIVATIONGROUP :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.ACTIVATION_GROUP_ATTR );
                break;
            case AGENDAGROUP :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.AGENDA_GROUP_ATTR );
                break;
            case AUTOFOCUS :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.AUTO_FOCUS_ATTR );
                break;
            case DURATION :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.DURATION_ATTR );
                break;
            case LOCKONACTIVE :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR );
                break;
            case NOLOOP :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.NO_LOOP_ATTR );
                break;
            case RULEFLOWGROUP :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.RULEFLOW_GROUP_ATTR );
                break;
            case SALIENCE :
                column = new AttributeCol52();
                ((AttributeCol52) column).setAttribute( GuidedDecisionTable52.SALIENCE_ATTR );
                break;

            default :
                throw new IllegalArgumentException( "Internal error: ActionType '" + actionType.getColHeader() + "' cannot be handled." );
        }
        return column;
    }

    private void addColumn(GuidedDecisionTable52 dtable,
                           DTColumnConfig52 column) {
        if ( column instanceof DescriptionCol52 ) {
            dtable.setDescriptionCol( (DescriptionCol52) column );
        } else if ( column instanceof MetadataCol52 ) {
            dtable.getMetadataCols().add( (MetadataCol52) column );
        } else if ( column instanceof AttributeCol52 ) {
            dtable.getAttributeCols().add( (AttributeCol52) column );
        }
    }

    private void addColumnData(GuidedDecisionTable52 dtable,
                               DTColumnConfig52 column) {

        int rowCount = this.values.size();
        int iColIndex = dtable.getExpandedColumns().indexOf( column );

        //Add column data
        for ( int iRow = 0; iRow < rowCount; iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            while ( rowData.size() < iColIndex ) {
                rowData.add( new DTCellValue52() );
            }
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

        switch ( actionType ) {
            case METADATA :
                definitions.put( column,
                                 content.trim() );
                break;

            default :
                throw new IllegalArgumentException( "Internal error: ActionType '" + actionType.getColHeader() + "' does not need a code snippet." );
        }
    }

    public void addCellValue(int row,
                             int column,
                             String value) {

        DTCellValue52 dcv = null;

        switch ( actionType ) {
            case METADATA :
                String content = this.definitions.get( column );
                if ( content == null ) {
                    throw new DecisionTableParseException( "No code snippet for METADATA in cell " +
                                                           RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                        this.headerCol ) );
                }
                dcv = new DTCellValue52( value );
                break;
            case SALIENCE :
                if ( value.startsWith( "(" ) && value.endsWith( ")" ) ) {
                    value = value.substring( 1,
                                             value.lastIndexOf( ")" ) - 1 );
                }
                try {
                    dcv = new DTCellValue52( new Integer( value ) );
                } catch ( NumberFormatException nfe ) {
                    throw new DecisionTableParseException( "Priority is not an integer literal, in cell " + RuleSheetParserUtil.rc2name( row,
                                                                                                                                         column ) );
                }
                break;
            case NAME :
            case DESCRIPTION :
            case ACTIVATIONGROUP :
            case AGENDAGROUP :
            case RULEFLOWGROUP :
                dcv = new DTCellValue52( value );
                break;
            case NOLOOP :
            case LOCKONACTIVE :
            case AUTOFOCUS :
                dcv = new DTCellValue52( RuleSheetParserUtil.isStringMeaningTrue( value ) );
                break;
            case DURATION :
                try {
                    dcv = new DTCellValue52( new Integer( value ) );
                } catch ( NumberFormatException nfe ) {
                    throw new DecisionTableParseException( "Duration is not an integer literal, in cell " + RuleSheetParserUtil.rc2name( row,
                                                                                                                                         column ) );
                }
                break;
        }
        if ( dcv == null ) {
            throw new IllegalArgumentException( "Internal error: ActionType." + actionType.getColHeader() + " is not handled by GuidedDecisionTableGenericBuilder" );
        }

        this.values.add( dcv );
    }

    public ActionType.Code getActionTypeCode() {
        return this.actionType;
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableGenericBuilder does not return DRL." );
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

}
