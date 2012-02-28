/*
 * Copyright 2011 JBoss Inc
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
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.cells.PopupDropDownEditCell;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor.ValueHolder;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.shared.EventBus;

public class TemplateDataCellFactory extends AbstractCellFactory<TemplateDataColumn> {

    private TemplateModel model;

    /**
     * Construct a Cell Factory for a specific Template Data Widget
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param eventBus
     *            EventBus to which cells can send update events
     */
    public TemplateDataCellFactory(SuggestionCompletionEngine sce,
                                   boolean isReadOnly,
                                   EventBus eventBus) {
        super( sce,
               isReadOnly,
               eventBus );
    }

    /**
     * Set the model for which cells will be created
     * 
     * @param model
     */
    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
    }

    /**
     * Create a Cell for the given TemplateDataColumn
     * 
     * @param column
     *            The Template Data Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> getCell(TemplateDataColumn column) {

        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = null;

        //Check if the column has an enumeration
        String factType = column.getFactType();
        String factField = column.getFactField();
        if ( sce.hasEnums( factType,
                           factField ) ) {

            // Columns with lists of values, enums etc are always Text (for now)
            PopupDropDownEditCell pudd = new PopupDropDownEditCell( factType,
                                                                    factField,
                                                                    sce,
                                                                    this,
                                                                    isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                              eventBus );

        } else {
            String dataType = column.getDataType();
            if ( column.getDataType().equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
                cell = makeBooleanCell();
            } else if ( column.getDataType().equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
                cell = makeDateCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                cell = makeNumericCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGDECIMAL ) ) {
                cell = makeNumericBigDecimalCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGINTEGER ) ) {
                cell = makeNumericBigIntegerCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BYTE ) ) {
                cell = makeNumericByteCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE ) ) {
                cell = makeNumericDoubleCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT ) ) {
                cell = makeNumericFloatCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER ) ) {
                cell = makeNumericIntegerCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_LONG ) ) {
                cell = makeNumericLongCell();
            } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_SHORT ) ) {
                cell = makeNumericShortCell();
            } else {
                cell = makeTextCell();
            }
        }

        return cell;

    }

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
                    final InterpolationVariable variable = getInterpolationVariable( valueHolder.getValue(),
                                                                                     allVariables );
                    final String field = variable.getFactField();
                    //TODO {manstis} This should not be required. We don't want to do this until the row had been 
                    //added to the data. Perhaps we should use the UI data-model and not the persisted data-model?
                    final List<String> columnData = this.model.getTable().get( variable.getVarName() );
                    final String value = iBaseRowIndex < columnData.size() ? columnData.get( iBaseRowIndex ) : "";
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

    private InterpolationVariable getInterpolationVariable(final String variableName,
                                                           final InterpolationVariable[] allVariables) {
        for ( InterpolationVariable variable : allVariables ) {
            if ( variable.getVarName().equals( variableName ) ) {
                return variable;
            }
        }
        //This should never happen
        throw new IllegalArgumentException( "Variable '" + variableName + "' not found. This suggests an programming error." );
    }

}
