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

import java.util.HashMap;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.template.parser.DecisionTableParseException;

/**
 * Builder for Condition columns
 */
public class GuidedDecisionTableLHSBuilder
    implements
    HasColumnHeadings,
    GuidedDecisionTableSourceBuilder {

    private final int                                     headerRow;
    private final int                                     headerCol;

    //Map of column constraints (code snippets), keyed on XLS column index
    private final Map<Integer, String>                    constraints   = new HashMap<Integer, String>();

    //Map of column headers, keyed on XLS column index
    private final Map<Integer, String>                    columnHeaders = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    //Utility class to convert XLS parameters to BRLFragment Template keys
    private final ParameterUtilities                      parameterUtilities;

    public GuidedDecisionTableLHSBuilder(int row,
                                         int column,
                                         String colDefinition,
                                         ParameterUtilities parameterUtilities) {
        this.headerRow = row;
        this.headerCol = column;
        this.parameterUtilities = parameterUtilities;
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        // TODO {manstis} Implement
    }

    public void addTemplate(int row,
                            int column,
                            String content) {
        if ( constraints.containsKey( column ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a code snippet added twice to one spreadsheet col." );
        }
        this.constraints.put( column,
                              content.trim() );
    }

    @Override
    public void setColumnHeader(int column,
                                String value) {
        this.columnHeaders.put( column,
                                value.trim() );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        String content = this.constraints.get( column );
        if ( content == null ) {
            throw new DecisionTableParseException( "No code snippet for CONDITION in cell " +
                                                   RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                this.headerCol ) );
        }

        DTCellValue52 dcv = new DTCellValue52( value );
        //this.values.add( dcv );
    }

    public ActionType.Code getActionTypeCode() {
        return ActionType.Code.CONDITION;
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableLHSBuilder does not return DRL." );
    }

    public void clearValues() {
        throw new UnsupportedOperationException();
    }

    public boolean hasValues() {
        throw new UnsupportedOperationException();
    }

}
