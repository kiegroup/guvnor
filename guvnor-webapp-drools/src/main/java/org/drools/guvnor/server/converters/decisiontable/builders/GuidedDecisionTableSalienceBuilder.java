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

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

/**
 * Builder for Salience Attribute columns
 */
public class GuidedDecisionTableSalienceBuilder extends AbstractGuidedDecisionTableAttributeBuilder {

    private final boolean isSequential;

    public GuidedDecisionTableSalienceBuilder(int row,
                                              int column,
                                              boolean isSequential,
                                              ConversionResult conversionResult) {
        super( row,
               column,
               ActionType.Code.SALIENCE,
               conversionResult );
        this.isSequential = isSequential;
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        AttributeCol52 column = new AttributeCol52();
        column.setAttribute( GuidedDecisionTable52.SALIENCE_ATTR );

        //If sequential set column to use reverse row number
        if ( isSequential ) {
            column.setUseRowNumber( true );
            column.setReverseOrder( true );
            final int maxRow = this.values.size();
            for ( int iRow = 0; iRow < maxRow; iRow++ ) {
                DTCellValue52 dcv = this.values.get( iRow );
                dcv.setLongValue( new Long( maxRow - iRow ) );
            }
        }
        dtable.getAttributeCols().add( column );
        addColumnData( dtable,
                       column );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        if ( value.startsWith( "(" ) && value.endsWith( ")" ) ) {
            value = value.substring( 1,
                                     value.lastIndexOf( ")" ) - 1 );
        }
        DTCellValue52 dcv = new DTCellValue52( new Long( 0 ) );
        try {
            dcv.setLongValue( new Long( value ) );
        } catch ( NumberFormatException nfe ) {
            final String message = "Priority is not an integer literal, in cell " + RuleSheetParserUtil.rc2name( row,
                                                                                                                 column );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.WARNING );
        }
        this.values.add( dcv );
    }

}
