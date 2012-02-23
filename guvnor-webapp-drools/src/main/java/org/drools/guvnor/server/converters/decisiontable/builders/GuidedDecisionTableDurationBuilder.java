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
 * Builder for Duration Attribute columns
 */
public class GuidedDecisionTableDurationBuilder extends AbstractGuidedDecisionTableAttributeBuilder {

    public GuidedDecisionTableDurationBuilder(int row,
                                              int column,
                                              ConversionResult conversionResult) {
        super( row,
               column,
               ActionType.Code.DURATION,
               conversionResult );
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        AttributeCol52 column = new AttributeCol52();
        column.setAttribute( GuidedDecisionTable52.DURATION_ATTR );
        dtable.getAttributeCols().add( column );
        addColumnData( dtable,
                       column );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        DTCellValue52 dcv = new DTCellValue52();
        try {
            dcv.setNumericValue( new Long( value ) );
        } catch ( NumberFormatException nfe ) {
            final String message = "Duration is not an long literal, in cell " + RuleSheetParserUtil.rc2name( row,
                                                                                                              column );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.WARNING );
        }
        this.values.add( dcv );
    }

}
