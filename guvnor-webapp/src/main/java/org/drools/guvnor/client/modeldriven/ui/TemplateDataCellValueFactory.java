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
package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellValueFactory;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class TemplateDataCellValueFactory extends AbstractCellValueFactory<TemplateDataColumn> {

    /**
     * Construct a Cell Value Factory for a specific Template data editor
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     */
    public TemplateDataCellValueFactory(SuggestionCompletionEngine sce) {
        super( sce );
    }

    // Get the Data Type corresponding to a given column
    protected DATA_TYPES getDataType(TemplateDataColumn column) {

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = null;
        String factType = column.getFactType();
        String factField = column.getFactField();
        if ( factType != null && factField != null ) {
            vals = sce.getEnumValues( factType,
                                      factField );
            if ( vals != null && vals.length > 0 ) {
                return DATA_TYPES.STRING;
            }
        }

        //Otherwise use data type extracted from model
        String dataType = column.getDataType();
        if ( dataType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            return DATA_TYPES.BOOLEAN;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            return DATA_TYPES.DATE;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            return DATA_TYPES.NUMERIC;
        } else {
            return DATA_TYPES.STRING;
        }
    }

}
