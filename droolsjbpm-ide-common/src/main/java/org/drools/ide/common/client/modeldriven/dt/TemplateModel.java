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
package org.drools.ide.common.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.ide.common.client.modeldriven.FieldNature;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

public class TemplateModel extends RuleModel
    implements
    PortableObject {

    /**
     * Template interpolation variable including data-type, Fact Type and Fact
     * Field
     */
    public static class InterpolationVariable {

        private String varName;
        private String dataType;
        private String factType;
        private String factField;

        public InterpolationVariable(String varName,
                                     String dataType) {
            this.varName = varName;
            this.dataType = dataType;
        }

        public InterpolationVariable(String varName,
                                     String dataType,
                                     String factType,
                                     String factField) {
            this.varName = varName;
            this.dataType = dataType;
            this.factType = factType;
            this.factField = factField;
        }

        private boolean equalOrNull(Object lhs,
                                    Object rhs) {
            if ( lhs == null && rhs == null ) {
                return true;
            }
            if ( lhs != null && rhs == null ) {
                return false;
            }
            if ( lhs == null && rhs != null ) {
                return false;
            }
            return lhs.equals( rhs );
        }

        @Override
        public boolean equals(Object obj) {
            if ( obj == null ) {
                return false;
            }
            if ( !(obj instanceof InterpolationVariable) ) {
                return false;
            }
            InterpolationVariable that = (InterpolationVariable) obj;
            return equalOrNull( this.varName,
                                that.varName ) && equalOrNull( this.dataType,
                                                               that.dataType ) && equalOrNull( this.factType,
                                                                                               that.factType ) && equalOrNull( this.factField,
                                                                                                                               that.factField );
        }

        public String getDataType() {
            return dataType;
        }

        public String getFactField() {
            return factField;
        }

        public String getFactType() {
            return factType;
        }

        public String getVarName() {
            return varName;
        }

        @Override
        public int hashCode() {
            int hashCode = (varName == null ? 1 : varName.hashCode());
            hashCode = hashCode + 31 * (dataType == null ? 7 : dataType.hashCode());
            hashCode = hashCode + 31 * (factType == null ? 7 : factType.hashCode());
            hashCode = hashCode + 31 * (factField == null ? 7 : factField.hashCode());
            return hashCode;
        }

        public void setFactField(String factField) {
            this.factField = factField;
        }

        public void setFactType(String factType) {
            this.factType = factType;
        }

        public void setVarName(String varName) {
            this.varName = varName;
        }

    }

    public static class RuleModelVisitor {

        private IFactPattern                        factPattern;
        private Map<InterpolationVariable, Integer> vars;
        private RuleModel                           model;

        public RuleModelVisitor(RuleModel model,
                                Map<InterpolationVariable, Integer> vars) {
            this.vars = vars;
            this.model = model;
        }

        private void parseStringPattern(String text) {
            if ( text == null || text.length() == 0 ) {
                return;
            }
            int pos = 0;
            while ( (pos = text.indexOf( "@{",
                                         pos )) != -1 ) {
                int end = text.indexOf( '}',
                                        pos + 2 );
                if ( end != -1 ) {
                    String varName = text.substring( pos + 2,
                                                     end );
                    pos = end + 1;
                    InterpolationVariable var = new InterpolationVariable( varName,
                                                                           SuggestionCompletionEngine.TYPE_OBJECT );
                    if ( !vars.containsKey( var ) ) {
                        vars.put( var,
                                  vars.size() );
                    }
                }
            }
        }

        public void visit(Object o) {
            if ( o == null ) {
                return;
            }
            if ( o instanceof RuleModel ) {
                visitRuleModel( (RuleModel) o );
            } else if ( o instanceof FactPattern ) {
                visitFactPattern( (FactPattern) o );
            } else if ( o instanceof CompositeFieldConstraint ) {
                visitCompositeFieldConstraint( (CompositeFieldConstraint) o );
            } else if ( o instanceof SingleFieldConstraintEBLeftSide ) {
                visitSingleFieldConstraint( (SingleFieldConstraintEBLeftSide) o );
            } else if ( o instanceof SingleFieldConstraint ) {
                visitSingleFieldConstraint( (SingleFieldConstraint) o );
            } else if ( o instanceof CompositeFactPattern ) {
                visitCompositeFactPattern( (CompositeFactPattern) o );
            } else if ( o instanceof FreeFormLine ) {
                visitFreeFormLine( (FreeFormLine) o );
            } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
                visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
            } else if ( o instanceof FromCollectCompositeFactPattern ) {
                visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
            } else if ( o instanceof FromCompositeFactPattern ) {
                visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
            } else if ( o instanceof DSLSentence ) {
                visitDSLSentence( (DSLSentence) o );
            } else if ( o instanceof ActionInsertFact ) {
                visitActionFieldList( (ActionInsertFact) o );
            } else if ( o instanceof ActionSetField ) {
                visitActionFieldList( (ActionSetField) o );
            } else if ( o instanceof ActionUpdateField ) {
                visitActionFieldList( (ActionUpdateField) o );
            }
        }

        //ActionInsertFact, ActionSetField, ActionpdateField
        private void visitActionFieldList(ActionInsertFact afl) {
            String factType = afl.factType;
            for ( ActionFieldValue afv : afl.fieldValues ) {
                if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                    InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                           afv.getType(),
                                                                           factType,
                                                                           afv.getField() );
                    vars.put( var,
                              vars.size() );
                }
            }
        }

        private void visitActionFieldList(ActionSetField afl) {
            String factType = model.getLHSBindingType( afl.variable );
            for ( ActionFieldValue afv : afl.fieldValues ) {
                if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                    InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                           afv.getType(),
                                                                           factType,
                                                                           afv.getField() );
                    vars.put( var,
                              vars.size() );
                }
            }
        }

        private void visitActionFieldList(ActionUpdateField afl) {
            String factType = model.getLHSBindingType( afl.variable );
            for ( ActionFieldValue afv : afl.fieldValues ) {
                if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                    InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                           afv.getType(),
                                                                           factType,
                                                                           afv.getField() );
                    vars.put( var,
                              vars.size() );
                }
            }
        }

        private void visitCompositeFactPattern(CompositeFactPattern pattern) {
            if ( pattern.getPatterns() != null ) {
                for ( IFactPattern fp : pattern.getPatterns() ) {
                    visit( fp );
                }
            }
        }

        private void visitCompositeFieldConstraint(CompositeFieldConstraint cfc) {
            if ( cfc.constraints != null ) {
                for ( FieldConstraint fc : cfc.constraints ) {
                    visit( fc );
                }
            }
        }

        //TODO Handle definition and value
        private void visitDSLSentence(final DSLSentence sentence) {
            String text = sentence.getDefinition();
            parseStringPattern( text );
        }

        private void visitFactPattern(FactPattern pattern) {
            this.factPattern = pattern;
            for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
                visit( fc );
            }
        }

        private void visitFreeFormLine(FreeFormLine ffl) {
            parseStringPattern( ffl.text );
        }

        private void visitFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern pattern) {
            visit( pattern.getFactPattern() );
            visit( pattern.getSourcePattern() );

            parseStringPattern( pattern.getActionCode() );
            parseStringPattern( pattern.getInitCode() );
            parseStringPattern( pattern.getReverseCode() );
        }

        private void visitFromCollectCompositeFactPattern(FromCollectCompositeFactPattern pattern) {
            visit( pattern.getFactPattern() );
            visit( pattern.getRightPattern() );
        }

        private void visitFromCompositeFactPattern(FromCompositeFactPattern pattern) {
            visit( pattern.getFactPattern() );
            parseStringPattern( pattern.getExpression().getText() );
        }

        public void visitRuleModel(RuleModel model) {
            if ( model.lhs != null ) {
                for ( IPattern pat : model.lhs ) {
                    visit( pat );
                }
            }
            if ( model.rhs != null ) {
                for ( IAction action : model.rhs ) {
                    visit( action );
                }
            }
        }

        private void visitSingleFieldConstraint(SingleFieldConstraint sfc) {
            if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfc.getConstraintValueType() && !vars.containsKey( sfc.getValue() ) ) {
                InterpolationVariable var = new InterpolationVariable( sfc.getValue(),
                                                                       sfc.getFieldType(),
                                                                       factPattern.getFactType(),
                                                                       sfc.getFieldName() );
                vars.put( var,
                          vars.size() );
            }

            //Visit Connection constraints
            if ( sfc.connectives != null ) {
                for ( int i = 0; i < sfc.connectives.length; i++ ) {
                    final ConnectiveConstraint cc = sfc.connectives[i];
                    if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( cc.getValue() ) ) {
                        InterpolationVariable var = new InterpolationVariable( cc.getValue(),
                                                                               cc.getFieldType(),
                                                                               factPattern.getFactType(),
                                                                               cc.getFieldName() );
                        vars.put( var,
                                  vars.size() );
                    }
                }
            }
        }

        private void visitSingleFieldConstraint(SingleFieldConstraintEBLeftSide sfexp) {
            if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfexp.getConstraintValueType() && !vars.containsKey( sfexp.getValue() ) ) {
                InterpolationVariable var = new InterpolationVariable( sfexp.getValue(),
                                                                       sfexp.getExpressionLeftSide().getGenericType(),
                                                                       factPattern.getFactType(),
                                                                       sfexp.getFieldName() );
                vars.put( var,
                          vars.size() );
            }

            //Visit Connection constraints
            if ( sfexp.connectives != null ) {
                for ( int i = 0; i < sfexp.connectives.length; i++ ) {
                    final ConnectiveConstraint cc = sfexp.connectives[i];
                    if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( cc.getValue() ) ) {
                        InterpolationVariable var = new InterpolationVariable( cc.getValue(),
                                                                               sfexp.getExpressionLeftSide().getGenericType(),
                                                                               factPattern.getFactType(),
                                                                               cc.getFieldName() );
                        vars.put( var,
                                  vars.size() );
                    }
                }
            }

        }

    }

    public static final String        ID_COLUMN_NAME = "__ID_KOL_NAME__";
    private long                      idCol          = 0;
    private Map<String, List<String>> table          = new HashMap<String, List<String>>();

    private int                       rowsCount      = 0;

    /**
     * Append a row of data
     * 
     * @param rowId
     * @param row
     * @return
     */
    public String addRow(String rowId,
                         String[] row) {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        if ( row.length != vars.size() - 1 ) {
            throw new IllegalArgumentException( "Invalid numbers of columns: " + row.length + " expected: "
                                                + vars.size() );
        }
        if ( rowId == null || rowId.length() == 0 ) {
            rowId = getNewIdColValue();
        }
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            List<String> list = table.get( entry.getKey().getVarName() );
            if ( list == null ) {
                list = new ArrayList<String>();
                table.put( entry.getKey().getVarName(),
                           list );
            }
            if ( rowsCount != list.size() ) {
                throw new IllegalArgumentException( "invalid list size for " + entry.getKey() + ", expected: "
                                                    + rowsCount + " was: " + list.size() );
            }
            if ( ID_COLUMN_NAME.equals( entry.getKey().getVarName() ) ) {
                list.add( rowId );
            } else {
                list.add( row[entry.getValue()] );
            }
        }
        rowsCount++;
        return rowId;
    }

    /**
     * Add a row of data at the specified index
     * 
     * @param index
     * @param row
     * @return
     */
    public String addRow(int index,
                         String[] row) {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        if ( row.length != vars.size() - 1 ) {
            throw new IllegalArgumentException( "Invalid numbers of columns: " + row.length + " expected: "
                                                + vars.size() );
        }
        String rowId = getNewIdColValue();
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            List<String> list = table.get( entry.getKey().getVarName() );
            if ( list == null ) {
                list = new ArrayList<String>();
                table.put( entry.getKey().getVarName(),
                           list );
            }
            if ( rowsCount != list.size() ) {
                throw new IllegalArgumentException( "invalid list size for " + entry.getKey() + ", expected: "
                                                    + rowsCount + " was: " + list.size() );
            }
            if ( ID_COLUMN_NAME.equals( entry.getKey().getVarName() ) ) {
                list.add( index,
                          rowId );
            } else {
                list.add( index,
                          row[entry.getValue()] );
            }
        }
        rowsCount++;
        return rowId;
    }

    public String addRow(String[] row) {
        return addRow( null,
                       row );
    }

    public void clearRows() {
        if ( rowsCount > 0 ) {
            for ( List<String> col : table.values() ) {
                col.clear();
            }
            rowsCount = 0;
        }
    }

    public int getColsCount() {
        return getInterpolationVariables().size() - 1;
    }

    private Map<InterpolationVariable, Integer> getInterpolationVariables() {
        Map<InterpolationVariable, Integer> result = new HashMap<InterpolationVariable, Integer>();
        new RuleModelVisitor( this,
                              result ).visit( this );

        InterpolationVariable id = new InterpolationVariable( ID_COLUMN_NAME,
                                                              SuggestionCompletionEngine.TYPE_NUMERIC );
        result.put( id,
                    result.size() );
        return result;
    }

    public InterpolationVariable[] getInterpolationVariablesList() {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        InterpolationVariable[] ret = new InterpolationVariable[vars.size() - 1];
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            if ( !ID_COLUMN_NAME.equals( entry.getKey().varName ) ) {
                ret[entry.getValue()] = entry.getKey();
            }
        }
        return ret;
    }

    private String getNewIdColValue() {
        idCol++;
        return String.valueOf( idCol );
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public Map<String, List<String>> getTable() {
        return table;
    }

    public String[][] getTableAsArray() {
        if ( rowsCount <= 0 ) {
            return new String[0][0];
        }

        //Refresh against interpolation variables
        putInSync();

        String[][] ret = new String[rowsCount][table.size() - 1];
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            InterpolationVariable var = entry.getKey();
            String varName = var.varName;
            if ( ID_COLUMN_NAME.equals( varName ) ) {
                continue;
            }
            int idx = entry.getValue();
            for ( int row = 0; row < rowsCount; row++ ) {
                ret[row][idx] = table.get( varName ).get( row );
            }
        }
        return ret;
    }

    public List<List<String>> getTableAsList() {
        List<List<String>> rows = new ArrayList<List<String>>();
        if ( rowsCount <= 0 ) {
            rows.add( new ArrayList<String>() );
            return rows;
        }

        //Refresh against interpolation variables
        putInSync();

        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            InterpolationVariable var = entry.getKey();
            String varName = var.varName;
            if ( ID_COLUMN_NAME.equals( varName ) ) {
                continue;
            }
            int idx = entry.getValue();
            for ( int iRow = 0; iRow < rowsCount; iRow++ ) {
                List<String> row = rows.get( iRow );
                if ( row == null ) {
                    row = new ArrayList<String>();
                    rows.add( row );
                }
                row.set( idx,
                         table.get( varName ).get( iRow ) );
            }
        }
        return rows;
    }

    public void putInSync() {

        //vars.KeySet is a set of InterpolationVariable, whereas table.keySet is a set of String
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();

        // Retain all columns in table that are in vars
        Set<String> requiredVars = new HashSet<String>();
        for ( InterpolationVariable var : vars.keySet() ) {
            if ( table.containsKey( var.varName ) ) {
                requiredVars.add( var.varName );
            }
        }
        table.keySet().retainAll( requiredVars );

        // Add empty columns for all vars that are not in table
        List<String> aux = new ArrayList<String>( rowsCount );
        for ( int i = 0; i < rowsCount; i++ ) {
            aux.add( "" );
        }
        for ( InterpolationVariable var : vars.keySet() ) {
            if ( !requiredVars.contains( var.varName ) ) {
                table.put( var.varName,
                           new ArrayList<String>( aux ) );
            }
        }

    }

    public void removeRow(int row) {
        if ( row >= 0 && row < rowsCount ) {
            for ( List<String> col : table.values() ) {
                col.remove( row );
            }
            rowsCount--;
        } else {
            throw new ArrayIndexOutOfBoundsException( row );
        }
    }

    public boolean removeRowById(String rowId) {
        int idx = table.get( ID_COLUMN_NAME ).indexOf( rowId );
        if ( idx != -1 ) {
            for ( List<String> col : table.values() ) {
                col.remove( idx );
            }
            rowsCount--;
        }
        return idx != -1;
    }

    public void setValue(String varName,
                         int rowIndex,
                         String newValue) {
        getTable().get( varName ).set( rowIndex,
                                       newValue );
    }
}
