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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.template.model.SnippetBuilder;
import org.drools.template.model.SnippetBuilder.SnippetType;
import org.drools.template.parser.DecisionTableParseException;

/**
 * Builder for Action columns
 */
public class GuidedDecisionTableRHSBuilder
        implements
        HasColumnHeadings,
        GuidedDecisionTableSourceBuilder {

    private final int                                     headerRow;
    private final int                                     headerCol;
    private final String                                  variable;

    //Map of column headers, keyed on XLS column index
    private final Map<Integer, String>                    columnHeaders = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    //Utility class to convert XLS parameters to BRLFragment Template keys
    private final ParameterUtilities                      parameterUtilities;

    private ConversionResult                              conversionResult;

    public GuidedDecisionTableRHSBuilder(int row,
                                         int column,
                                         String boundVariable,
                                         ParameterUtilities parameterUtilities,
                                         ConversionResult conversionResult) {
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.parameterUtilities = parameterUtilities;
        this.conversionResult = conversionResult;
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        //Sort column builders by column index to ensure Actions are added in the correct sequence
        Set<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );

        for ( Integer index : sortedIndexes ) {
            ParameterizedValueBuilder vb = this.valueBuilders.get( index );
            addColumn( dtable,
                       vb,
                       index );
        }
    }

    private void addColumn(GuidedDecisionTable52 dtable,
                           ParameterizedValueBuilder vb,
                           int index) {
        if ( vb instanceof LiteralValueBuilder ) {
            addLiteralColumn( dtable,
                              (LiteralValueBuilder) vb,
                              index );
        } else {
            addBRLFragmentColumn( dtable,
                                  vb,
                                  index );
        }
    }

    private void addLiteralColumn(GuidedDecisionTable52 dtable,
                                  LiteralValueBuilder vb,
                                  int index) {
        //Create column - Everything is a BRL fragment (for now)
        BRLActionColumn column = new BRLActionColumn();
        FreeFormLine ffl = new FreeFormLine();
        ffl.text = vb.getTemplate();
        column.getDefinition().add( ffl );
        BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( "",
                                                                               SuggestionCompletionEngine.TYPE_BOOLEAN );
        column.getChildColumns().add( parameterColumn );
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getActionCols().add( column );

        //Add column data
        List<List<DTCellValue52>> columnData = vb.getColumnData();
        int iColIndex = dtable.getExpandedColumns().indexOf( column.getChildColumns().get( 0 ) );
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.addAll( iColIndex,
                            columnData.get( iRow ) );
        }

    }

    private void addBRLFragmentColumn(GuidedDecisionTable52 dtable,
                                      ParameterizedValueBuilder vb,
                                      int index) {
        //Create column - Everything is a BRL fragment (for now)
        BRLActionColumn column = new BRLActionColumn();
        FreeFormLine ffl = new FreeFormLine();
        ffl.text = vb.getTemplate();
        column.getDefinition().add( ffl );

        for ( String parameter : vb.getParameters() ) {
            BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( parameter,
                                                                                   SuggestionCompletionEngine.TYPE_OBJECT );
            column.getChildColumns().add( parameterColumn );
        }
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getActionCols().add( column );

        //Add column data
        List<List<DTCellValue52>> columnData = vb.getColumnData();

        //We can use the index of the first child column to add all data
        int iColIndex = dtable.getExpandedColumns().indexOf( column.getChildColumns().get( 0 ) );
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.addAll( iColIndex,
                            columnData.get( iRow ) );
        }
    }

    public void addTemplate(int row,
                            int column,
                            String template) {
        //Validate column template
        if ( valueBuilders.containsKey( column ) ) {
            final String message = "Internal error: Can't have a code snippet added twice to one spreadsheet column.";
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
            return;
        }

        //Add new template
        template = template.trim();
        if ( isBoundVar() ) {
            template = variable + "." + template;
        }
        if ( !template.endsWith( ";" ) ) {
            template = template + ";";
        }
        try {
            this.valueBuilders.put( column,
                                    getValueBuilder( template ) );
        } catch ( DecisionTableParseException pe ) {
            this.conversionResult.addMessage( pe.getMessage(),
                                              ConversionMessageType.WARNING );
        }
    }

    private boolean isBoundVar() {
        return !("".equals( variable ));
    }

    @Override
    public void setColumnHeader(int column,
                                String value) {
        this.columnHeaders.put( column,
                                value.trim() );
    }

    private ParameterizedValueBuilder getValueBuilder(String template) {
        final SnippetType type = SnippetBuilder.getType( template );
        switch ( type ) {
            case INDEXED :
                return new IndexedParametersValueBuilder( template,
                                                          parameterUtilities );
            case PARAM :
                return new SingleParameterValueBuilder( template,
                                                        parameterUtilities );
            case SINGLE :
                return new LiteralValueBuilder( template );
        }
        throw new DecisionTableParseException( "SnippetBuilder.SnippetType '" + type.toString() + "' is not supported. The column will not be added." );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        //Add new row to column data
        ParameterizedValueBuilder vb = this.valueBuilders.get( column );
        if ( vb == null ) {
            final String message = "No code snippet for ACTION, above cell " +
                                   RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                this.headerCol );
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
            return;
        }
        vb.addCellValue( row,
                         column,
                         value );
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableRHSBuilder does not return DRL." );
    }

    public Code getActionTypeCode() {
        return Code.ACTION;
    }

    public void clearValues() {
        throw new UnsupportedOperationException();
    }

    public boolean hasValues() {
        throw new UnsupportedOperationException();
    }

}
