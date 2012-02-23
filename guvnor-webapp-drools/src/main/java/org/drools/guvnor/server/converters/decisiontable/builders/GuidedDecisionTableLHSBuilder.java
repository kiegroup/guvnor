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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.guvnor.client.rpc.ConversionResult;
import org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.template.model.SnippetBuilder;
import org.drools.template.model.SnippetBuilder.SnippetType;
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

    //DRL generation parameters
    private String                                        colDefPrefix;
    private String                                        colDefSuffix;
    private boolean                                       hasPattern;
    private String                                        andop;

    //Operators used to detect whether a template contains an operator or implies "=="
    private static Set<String>                            operators;

    static {
        operators = new HashSet<String>();
        operators.add( "==" );
        operators.add( "=" );
        operators.add( "!=" );
        operators.add( "<" );
        operators.add( ">" );
        operators.add( "<=" );
        operators.add( ">=" );
        operators.add( "contains" );
        operators.add( "matches" );
        operators.add( "memberOf" );
        operators.add( "str[startsWith]" );
        operators.add( "str[endsWith]" );
        operators.add( "str[length]" );
    }

    private static final Pattern                          patParFrm     = Pattern.compile( "\\(\\s*\\)\\s*from\\b" );
    private static final Pattern                          patFrm        = Pattern.compile( "\\s+from\\s+" );
    private static final Pattern                          patPar        = Pattern.compile( "\\(\\s*\\)" );
    private static final Pattern                          patEval       = Pattern.compile( "\\beval\\s*(?:\\(\\s*\\)\\s*)?$" );

    //Map of column headers, keyed on XLS column index
    private final Map<Integer, String>                    columnHeaders = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    //Utility class to convert XLS parameters to BRLFragment Template keys
    private final ParameterUtilities                      parameterUtilities;

    private ConversionResult                              conversionResult;

    public GuidedDecisionTableLHSBuilder(int row,
                                         int column,
                                         String colDefinition,
                                         ParameterUtilities parameterUtilities,
                                         ConversionResult conversionResult) {
        this.headerRow = row;
        this.headerCol = column;
        this.parameterUtilities = parameterUtilities;
        this.conversionResult = conversionResult;
        preProcessColumnDefinition( colDefinition );
    }

    private void preProcessColumnDefinition(String colDefinition) {

        //Determine DRL generation parameters
        String colDef = colDefinition == null ? "" : colDefinition;
        if ( "".equals( colDef ) ) {
            colDefPrefix = colDefSuffix = "";
            hasPattern = false;
            andop = "";
            return;
        }
        hasPattern = true;

        // ...eval
        Matcher matEval = patEval.matcher( colDef );
        if ( matEval.find() ) {
            colDefPrefix = colDef.substring( 0,
                                             matEval.start() ) + "eval(";
            colDefSuffix = ")";
            andop = " && ";
            return;
        }
        andop = ", ";

        // ...(<b> ) from...
        Matcher matParFrm = patParFrm.matcher( colDef );
        if ( matParFrm.find() ) {
            colDefPrefix = colDef.substring( 0,
                                             matParFrm.start() ) + '(';
            colDefSuffix = ") from" + colDef.substring( matParFrm.end() );
            return;
        }

        // ...from...
        Matcher matFrm = patFrm.matcher( colDef );
        if ( matFrm.find() ) {
            colDefPrefix = colDef.substring( 0,
                                             matFrm.start() ) + "(";
            colDefSuffix = ") from " + colDef.substring( matFrm.end() );
            return;
        }

        // ...(<b> )...
        Matcher matPar = patPar.matcher( colDef );
        if ( matPar.find() ) {
            colDefPrefix = colDef.substring( 0,
                                             matPar.start() ) + '(';
            colDefSuffix = ")" + colDef.substring( matPar.end() );
            return;
        }

        // <a>
        colDefPrefix = colDef + '(';
        colDefSuffix = ")";

    }

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        if ( !hasPattern ) {
            //Add separate columns for each ValueBuilder
            addExplicitColumns( dtable );
        } else {
            //Add a single column for all ValueBuilders
            addPatternColumn( dtable );
        }
    }

    //An explicit column does not add constraints to a Pattern. It is does not have a value in the OBJECT row
    private void addExplicitColumns(GuidedDecisionTable52 dtable) {

        //Sort column builders by column index to ensure Actions are added in the correct sequence
        Set<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );

        for ( Integer index : sortedIndexes ) {
            ParameterizedValueBuilder vb = this.valueBuilders.get( index );
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
    }

    private void addLiteralColumn(GuidedDecisionTable52 dtable,
                                  LiteralValueBuilder vb,
                                  int index) {
        //Create column - Everything is a BRL fragment (for now)
        BRLConditionColumn column = new BRLConditionColumn();
        FreeFormLine ffl = new FreeFormLine();
        ffl.text = vb.getTemplate();
        column.getDefinition().add( ffl );
        BRLConditionVariableColumn parameterColumn = new BRLConditionVariableColumn( "",
                                                                                     SuggestionCompletionEngine.TYPE_BOOLEAN );
        column.getChildColumns().add( parameterColumn );
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getConditions().add( column );

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
        BRLConditionColumn column = new BRLConditionColumn();
        FreeFormLine ffl = new FreeFormLine();
        ffl.text = vb.getTemplate();
        column.getDefinition().add( ffl );

        for ( String parameter : vb.getParameters() ) {
            BRLConditionVariableColumn parameterColumn = new BRLConditionVariableColumn( parameter,
                                                                                         SuggestionCompletionEngine.TYPE_OBJECT );
            column.getChildColumns().add( parameterColumn );
        }
        column.setHeader( this.columnHeaders.get( index ) );
        dtable.getConditions().add( column );

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

    //A Pattern column adds constraints to a Pattern. It has a value in the OBJECT row
    private void addPatternColumn(GuidedDecisionTable52 dtable) {

        //Sort column builders by column index to ensure Actions are added in the correct sequence
        TreeSet<Integer> sortedIndexes = new TreeSet<Integer>( this.valueBuilders.keySet() );

        //If the Pattern spans multiple columns create a column header
        String columnHeader = this.columnHeaders.get( sortedIndexes.first() );
        if ( sortedIndexes.size() > 1 ) {
            columnHeader = "Converted from cell [" +
                           RuleSheetParserUtil.rc2name( this.headerRow + 1,
                                                        this.headerCol ) + "]";
        }

        //Create column - Everything is a BRL fragment (for now)
        BRLConditionColumn column = new BRLConditionColumn();
        dtable.getConditions().add( column );

        FreeFormLine ffl = new FreeFormLine();
        column.getDefinition().add( ffl );

        //DRL prefix
        StringBuffer drl = new StringBuffer();
        drl.append( this.colDefPrefix );
        String sep = "";

        int dataColumnIndex = 0;
        for ( Integer index : sortedIndexes ) {

            ParameterizedValueBuilder vb = this.valueBuilders.get( index );

            //DRL fragment
            drl.append( sep ).append( vb.getTemplate() );
            sep = this.andop;

            //Add columns for parameters
            for ( String parameter : vb.getParameters() ) {
                BRLConditionVariableColumn parameterColumn = new BRLConditionVariableColumn( parameter,
                                                                                             SuggestionCompletionEngine.TYPE_OBJECT );
                column.getChildColumns().add( parameterColumn );
            }

            //Add column data
            List<List<DTCellValue52>> columnData = vb.getColumnData();
            int iColIndex = dtable.getExpandedColumns().indexOf( column.getChildColumns().get( dataColumnIndex ) );
            for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
                List<DTCellValue52> rowData = dtable.getData().get( iRow );
                rowData.addAll( iColIndex,
                                columnData.get( iRow ) );
            }
            dataColumnIndex = dataColumnIndex + vb.getParameters().size();
        }

        //DRL suffix
        drl.append( this.colDefSuffix );
        ffl.text = drl.toString();

        //Set header after children have been added as it's copied into them
        column.setHeader( columnHeader );
    }

    public void addTemplate(int row,
                            int column,
                            String content) {
        //Validate column template
        if ( valueBuilders.containsKey( column ) ) {
            final String message = "Internal error: Can't have a code snippet added twice to one spreadsheet column.";
            this.conversionResult.addMessage( message,
                                              ConversionMessageType.ERROR );
            return;
        }

        //Add new template
        content = content.trim();
        try {
            this.valueBuilders.put( column,
                                    getValueBuilder( content ) );
        } catch ( DecisionTableParseException pe ) {
            this.conversionResult.addMessage( pe.getMessage(),
                                              ConversionMessageType.WARNING );
        }
    }

    @Override
    public void setColumnHeader(int column,
                                String value) {
        this.columnHeaders.put( column,
                                value.trim() );
    }

    private ParameterizedValueBuilder getValueBuilder(String template) {

        // Work out the type of "template":-
        // age                     ---> SnippetType.SINGLE
        // age ==                  ---> SnippetType.SINGLE
        // age == $param           ---> SnippetType.PARAM
        // age == $1 || age == $2  ---> SnippetType.INDEXED
        // forall{age < $}{,}      ---> SnippetType.FORALL
        template = template.trim();
        SnippetType type = SnippetBuilder.getType( template );
        if ( type == SnippetType.SINGLE ) {
            type = SnippetType.PARAM;
            boolean hasExplicitOperator = false;
            for ( String op : operators ) {
                if ( template.endsWith( op ) ) {
                    hasExplicitOperator = true;
                    break;
                }
            }
            if ( !hasExplicitOperator ) {
                template = template + " ==";
            }
            template = template + " \"";
            template = template + SnippetBuilder.PARAM_STRING + "\"";
        }

        //Make a ValueBuilder for the template
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
            final String message = "No code snippet for CONDITION, above cell " +
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
