/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.decoratedgrid.events;

import java.util.List;

import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;

/**
 * An event to insert a Decision Table column
 */
public class InsertDecisionTableColumnEvent extends InsertColumnEvent<DTColumnConfig52, DTCellValue52> {

    public InsertDecisionTableColumnEvent(DTColumnConfig52 column,
                                          List<DTCellValue52> columnData,
                                          int index,
                                          boolean redraw) {
        super( column,
               columnData,
               index,
               redraw );
    }

    public InsertDecisionTableColumnEvent(DTColumnConfig52 column,
                                          List<DTCellValue52> columnData,
                                          int index) {
        super( column,
               columnData,
               index );
    }

    public static Type<InsertColumnEvent.Handler<DTColumnConfig52, DTCellValue52>> TYPE = new Type<InsertColumnEvent.Handler<DTColumnConfig52, DTCellValue52>>();

    @Override
    public Type<InsertColumnEvent.Handler<DTColumnConfig52, DTCellValue52>> getAssociatedType() {
        return TYPE;
    }

}
