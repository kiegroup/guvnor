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
        GuidedDecisionTableSourceBuilder {

    private final int                                     headerRow;
    private final int                                     headerCol;
    private final String                                  variable;

    //Map of column definitions (code snippets), keyed on XLS column index
    private final Map<Integer, String>                    definitions   = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    private final ParameterUtilities                      parameterUtilities;

    public GuidedDecisionTableRHSBuilder(int row,
                                         int column,
                                         String boundVariable,
                                         ParameterUtilities parameterUtilities) {
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.parameterUtilities = parameterUtilities;
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {

        //Sort column builders by column index
        Set<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );

        for ( Integer index : sortedIndexes ) {

            ParameterizedValueBuilder vb = this.valueBuilders.get( index );

            //Create column - Everything is a BRL fragment (for now)
            BRLActionColumn column = new BRLActionColumn();
            FreeFormLine ffl = new FreeFormLine();
            ffl.text = vb.getTemplate();
            column.getDefinition().add( ffl );

            for ( String parameter : vb.getParameters() ) {
                BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( parameter,
                                                                                       null,
                                                                                       null,
                                                                                       null );
                column.getChildColumns().add( parameterColumn );
            }
            column.setHeader( "Smurf[" + index + "] - needs to come from XLS" );
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

    }

    public void addTemplate(int row,
                            int column,
                            String content) {
        if ( definitions.containsKey( column ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a code snippet added twice to one spreadsheet col." );
        }

        content = content.trim();
        if ( isBoundVar() ) {
            content = variable + "." + content + ";";
        }
        this.definitions.put( column,
                              content );
        this.valueBuilders.put( column,
                                getValueBuilder( content ) );

    }

    private boolean isBoundVar() {
        return !("".equals( variable ));
    }

    private ParameterizedValueBuilder getValueBuilder(String template) {
        final SnippetType type = SnippetBuilder.getType( template );
        switch ( type ) {
            case INDEXED :
                return new IndexedParametersValueBuilder( template, parameterUtilities );
            case PARAM :
                return new SingleParameterValueBuilder( template, parameterUtilities );
            case SINGLE :
                return new LiteralValueBuilder( template );
        }
        throw new DecisionTableParseException( "SnippetBuilder.SnippetType '" + type.toString() + "' is not supported." );
    }

    public void addCellValue(int row,
                             int column,
                             String value) {
        String definition = this.definitions.get( column );
        if ( definition == null ) {
            throw new DecisionTableParseException( "No code snippet for ACTION, above cell " +
                                                   RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                this.headerCol ) );
        }

        //Add new row to column data
        ParameterizedValueBuilder vb = this.valueBuilders.get( column );
        if ( vb == null ) {
            throw new DecisionTableParseException( "No ValueBuilder for ACTION, above cell " +
                                                   RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                this.headerCol ) );
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
