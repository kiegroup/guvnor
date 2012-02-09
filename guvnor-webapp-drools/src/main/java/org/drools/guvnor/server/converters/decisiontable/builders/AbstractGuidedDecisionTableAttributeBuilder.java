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

import org.drools.decisiontable.parser.ActionType;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

/**
 * Abstract builder for all Attribute columns
 */
public abstract class AbstractGuidedDecisionTableAttributeBuilder
    implements
    GuidedDecisionTableSourceBuilder {

    protected int                  headerRow;
    protected int                  headerCol;
    protected ActionType.Code      actionType;
    protected Map<Integer, String> definitions;
    protected List<DTCellValue52>  values;

    public AbstractGuidedDecisionTableAttributeBuilder(int row,
                                              int column,
                                              ActionType.Code actionType) {
        this.headerRow = row;
        this.headerCol = column;
        this.actionType = actionType;
        this.definitions = new HashMap<Integer, String>();
        this.values = new ArrayList<DTCellValue52>();
    }

    protected void addColumnData(GuidedDecisionTable52 dtable,
                                 DTColumnConfig52 column) {

        int rowCount = this.values.size();
        int iColIndex = dtable.getExpandedColumns().indexOf( column );

        //Add column data
        for ( int iRow = 0; iRow < rowCount; iRow++ ) {
            List<DTCellValue52> rowData = dtable.getData().get( iRow );
            rowData.add( iColIndex,
                         this.values.get( iRow ) );
        }
    }

    public ActionType.Code getActionTypeCode() {
        return this.actionType;
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

    public void addTemplate(int row,
                            int column,
                            String content) {
        throw new IllegalArgumentException( "Internal error: ActionType '" + actionType.getColHeader() + "' does not need a code snippet." );
    }

    public String getResult() {
        throw new UnsupportedOperationException( this.getClass().getSimpleName() + " does not return DRL." );
    }

}
