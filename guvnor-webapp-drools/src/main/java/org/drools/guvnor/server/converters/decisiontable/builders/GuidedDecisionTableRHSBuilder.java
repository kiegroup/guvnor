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
import java.util.Set;
import java.util.TreeSet;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.template.model.SnippetBuilder;
import org.drools.template.model.SnippetBuilder.SnippetType;
import org.drools.template.parser.DecisionTableParseException;

/**
 * Builder for Action columns
 */
public class GuidedDecisionTableRHSBuilder
        implements
        GuidedDecisionTableBuilder {

    private int                                     headerRow;
    private int                                     headerCol;
    private String                                  variable;

    //Map of column definitions (code snippets), keyed on XLS column index
    private Map<Integer, String>                    definitions   = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private Map<Integer, ValueBuilder>              valueBuilders = new HashMap<Integer, ValueBuilder>();

    //Map of column data, keyed on XLS column index. Value is row/column(s)
    private Map<Integer, List<List<DTCellValue52>>> values        = new HashMap<Integer, List<List<DTCellValue52>>>();

    public GuidedDecisionTableRHSBuilder(int row,
                                         int column,
                                         String boundVariable) {
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        //Create column
        BRLActionColumn column = new BRLActionColumn();
        column.setHeader( "Smurf - needs to come from XLS" );

        //Define column
        Set<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );
        for ( Integer index : sortedIndexes ) {
            ValueBuilder vb = this.valueBuilders.get( index );

            //Everything is a BRL fragment (for now)
            FreeFormLine brlFragment = new FreeFormLine();
            brlFragment.text = vb.template;
            column.getDefinition().add( brlFragment );

            for ( String parameter : vb.parameters ) {
                BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn( parameter,
                                                                                       null,
                                                                                       null,
                                                                                       null );
                column.getChildColumns().add( parameterColumn );
            }
        }
        dtable.getActionCols().add( column );

        //Add data
        //TODO {manstis} This looks up the number of columns. doh!
        int rowCount = this.values.size();
        for ( BRLActionVariableColumn parameterColumn : column.getChildColumns() ) {

            int iColIndex = dtable.getExpandedColumns().indexOf( parameterColumn );

            //Add column data
            for ( int iRow = 0; iRow < rowCount; iRow++ ) {
                List<DTCellValue52> rowData = dtable.getData().get( iRow );
                while ( rowData.size() < iColIndex ) {
                    rowData.add( new DTCellValue52() );
                }
                //TODO {manstis} Lookup value from this.values
                rowData.add( iColIndex,
                             new DTCellValue52() );
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

    private ValueBuilder getValueBuilder(String template) {
        final SnippetType type = SnippetBuilder.getType( template );
        switch ( type ) {
            case INDEXED :
                return new IndexedParametersValueBuilder( template );
            case PARAM :
                return new SingleParameterValueBuilder( template );
            case SINGLE :
                return new SimpleValueBuilder( template );
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

        //Get column data
        List<List<DTCellValue52>> xlsColumnData = this.values.get( column );
        if ( xlsColumnData == null ) {
            xlsColumnData = new ArrayList<List<DTCellValue52>>();
            this.values.put( column,
                             xlsColumnData );
        }

        //Add new row to column data
        ValueBuilder vb = this.valueBuilders.get( column );
        if ( vb == null ) {
            throw new DecisionTableParseException( "No ValueBuilder for ACTION, above cell " +
                                                   RuleSheetParserUtil.rc2name( this.headerRow + 2,
                                                                                this.headerCol ) );
        }
        List<DTCellValue52> values = vb.build( value );
        xlsColumnData.add( values );
    }

    public String getResult() {
        throw new UnsupportedOperationException( "GuidedDecisionTableRHSBuilder does not return DRL." );
    }

    public Code getActionTypeCode() {
        return Code.ACTION;
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

}
