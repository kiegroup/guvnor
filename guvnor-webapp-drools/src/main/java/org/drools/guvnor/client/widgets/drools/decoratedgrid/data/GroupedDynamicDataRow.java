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
package org.drools.guvnor.client.widgets.drools.decoratedgrid.data;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue.GroupedCellValue;

/**
 * A grouped row of data in the Decision Table. This object represents the row
 * within the table that is visible. It contains a collection of grouped rows
 * excluding the first row of the grouped block. For example: A set of five rows
 * grouped results in one GroupedDynamicDataRow containing four child
 * DynamicDataRows
 */
public class GroupedDynamicDataRow extends DynamicDataRow {

    private static final long    serialVersionUID = 5758783945346050329L;

    private List<DynamicDataRow> groupedRows      = new ArrayList<DynamicDataRow>();

    @Override
    public CellValue< ? extends Comparable< ? >> get(int index) {
        CellValue< ? > cv = super.get( index );
        return cv;
    }

    public List<DynamicDataRow> getChildRows() {
        return this.groupedRows;
    }

    @Override
    public CellValue< ? extends Comparable< ? >> set(int index,
                                                     CellValue< ? extends Comparable< ? >> element) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.set( index,
                            element );
        }
        return super.set( index,
                          element );
    }

    @Override
    public boolean add(CellValue< ? extends Comparable< ? >> e) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.add( e );
        }
        return super.add( e );
    }

    @Override
    public void add(int index,
                    CellValue< ? extends Comparable< ? >> element) {
        super.add( index,
                   element );
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.add( index,
                            element );
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean addChildRow(DynamicDataRow childRow) {
        for ( int iCol = 0; iCol < childRow.size(); iCol++ ) {
            if ( this.get( iCol ) instanceof GroupedCellValue ) {
                GroupedCellValue gcv = (GroupedCellValue) this.get( iCol );
                gcv.addCellToGroup( childRow.get( iCol ) );
            }
        }
        return this.groupedRows.add( childRow );
    }

    @Override
    void clear() {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.clear();
        }
        super.clear();
    }

    @Override
    public CellValue< ? extends Comparable< ? >> remove(int index) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.remove( index );
        }
        return super.remove( index );
    }

}
