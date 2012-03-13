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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellTableDropDownDataValueMapProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor.ValueHolder;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;

import com.google.gwt.cell.client.Cell.Context;

/**
 * A utility class to get the values of all InterpolationVariables in the scope
 * of a Template Key to drive dependent enumerations. A value is in scope if it
 * is on a Constraint or Action on the same Pattern of the base column.
 */
public class TemplateDropDownManager
    implements
    CellTableDropDownDataValueMapProvider {

    private final SuggestionCompletionEngine sce;
    private TemplateModel                    model;
    private DynamicData                      data;

    public TemplateDropDownManager(final SuggestionCompletionEngine sce) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;
    }

    public TemplateDropDownManager(final TemplateModel model,
                                   final DynamicData data,
                                   final SuggestionCompletionEngine sce) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.model = model;
        this.data = data;
        this.sce = sce;
    }

    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        this.model = model;
    }

    @Override
    public void setData(DynamicData data) {
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        this.data = data;
    }

    /**
     * Create a map of Field Values keyed on Field Names used by
     * SuggestionCompletionEngine.getEnums(String, String, Map<String, String>)
     * to drive dependent enumerations.
     * 
     * @param context
     *            The Context of the cell being edited containing physical
     *            coordinate in the data-space.
     */
    @Override
    public Map<String, String> getCurrentValueMap(Context context) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final int iBaseRowIndex = context.getIndex();
        final int iBaseColIndex = context.getColumn();

        //Get variable for the column being edited
        InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        InterpolationVariable baseVariable = allVariables[iBaseColIndex];
        final String baseVariableName = baseVariable.getVarName();

        //Get other variables (and literals) in the same scope as the base variable
        final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( model,
                                                                                                   baseVariableName );
        List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

        //Add other variables values
        for ( ValueHolder valueHolder : peerVariables ) {
            switch ( valueHolder.getType() ) {
                case TEMPLATE_KEY :
                    final int iCol = getVariableColumnIndex( valueHolder.getValue() );
                    final InterpolationVariable variable = allVariables[iCol];
                    final String field = variable.getFactField();

                    //The generic class CellValue can have different data-types however enumerations
                    //in a Decision Table are always String-based hence we can safely cast to a String
                    //to retrieve the correct String representation of the CellValue's value.
                    final String value = (String) this.data.get( iBaseRowIndex ).get( iCol ).getValue();
                    currentValueMap.put( field,
                                         value );
                    break;
                case VALUE :
                    currentValueMap.put( valueHolder.getFieldName(),
                                         valueHolder.getValue() );
            }
        }

        return currentValueMap;
    }

    private int getVariableColumnIndex(final String variableName) {
        final InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        for ( int iCol = 0; iCol < allVariables.length; iCol++ ) {
            final InterpolationVariable var = allVariables[iCol];
            if ( var.getVarName().equals( variableName ) ) {
                return iCol;
            }
        }
        //This should never happen
        throw new IllegalArgumentException( "Variable '" + variableName + "' not found. This suggests an programming error." );
    }

    @Override
    public Set<Integer> getDependentColumnIndexes(int iBaseColIndex) {

        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        //Get variable for the column being edited
        final InterpolationVariable[] allVariables = this.model.getInterpolationVariablesList();
        final InterpolationVariable baseVariable = allVariables[iBaseColIndex];
        final String baseVariableName = baseVariable.getVarName();

        //Get other variables (and literals) in the same scope as the base variable
        final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( model,
                                                                                                   baseVariableName );
        List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

        //Add other variables values
        for ( ValueHolder valueHolder : peerVariables ) {
            switch ( valueHolder.getType() ) {
                case TEMPLATE_KEY :
                    final int iCol = getVariableColumnIndex( valueHolder.getValue() );
                    final InterpolationVariable variable = allVariables[iCol];
                    final String field = variable.getFactField();

                    if ( sce.isDependentEnum( baseVariable.getFactType(),
                                              baseVariable.getFactField(),
                                              field ) ) {
                        dependentColumnIndexes.add( iCol );
                    }
                    break;
            }
        }

        return dependentColumnIndexes;
    }

}
