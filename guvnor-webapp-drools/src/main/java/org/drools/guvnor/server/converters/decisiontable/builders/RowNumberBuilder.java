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
import java.util.List;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

/**
 * Builder for RowNumber columns
 */
public class RowNumberBuilder
    implements
    GuidedDecisionTableSourceBuilder {

    private List<DTCellValue52> values = new ArrayList<DTCellValue52>();

    public void populateDecisionTable(GuidedDecisionTable52 dtable) {
        for ( int iRow = 0; iRow < this.values.size(); iRow++ ) {
            dtable.getData().add( new ArrayList<DTCellValue52>() );
            DTCellValue52 dcv = this.values.get( iRow );
            dcv.setNumericValue( new Integer( iRow + 1 ) );
            dtable.getData().get( iRow ).add( 0,
                                              dcv );
        }
    }

    public void addCellValue(int row,
                             int col,
                             String value) {
        this.values.add( new DTCellValue52() );
    }

    public void clearValues() {
        this.values.clear();
    }

    public boolean hasValues() {
        return this.values.size() > 0;
    }

    public Code getActionTypeCode() {
        throw new UnsupportedOperationException( "RowNumberBuilder does implement an ActionType.Code" );
    }

    public String getResult() {
        throw new UnsupportedOperationException( "RowNumberBuilder does not return DRL." );
    }

    public void addTemplate(int row,
                            int col,
                            String content) {
        throw new UnsupportedOperationException( "RowNumberBuilder does implement code snippets." );
    }

}
