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
package org.drools.guvnor.client.widgets.decoratedgrid;

/**
 * This is a wrapper around a value. The wrapper provides additional information
 * required to use the vanilla value in a Decision Table with merge
 * capabilities.
 * 
 * One coordinate is maintained and two indexes to map to and from HTML table
 * coordinates. The indexes used to be maintained in SelectionManager however it
 * required two more N x N collections of "mapping" objects in addition to that
 * containing the actual data.
 * 
 * The coordinate represents the physical location of the cell on an (R, C)
 * grid. One index maps the physical coordinate of the cell to the logical
 * coordinate of the HTML table whilst the other index maps from the logical
 * coordinate to the physical cell.
 * 
 * For example, given data (0,0), (0,1), (1,0) and (1,1) with cell at (0,0)
 * merged into (1,0) only the HTML coordinates (0,0), (0,1) and (1,0) exist;
 * with physical coordinates (0,0) and (1,0) relating to HTML coordinate (0,0)
 * which has a row span of 2. Therefore physical cells (0,0) and (1,0) have a
 * <code>mapDataToHtml</code> coordinate of (0,0) whilst physical cell (1,0) has
 * a <code>mapHtmlToData</code> coordinate of (1,1).
 * 
 * @param <T>
 *            The data-type of the value
 * 
 *
 */
public class CellValue<T extends Comparable<T>>
    implements
        Comparable<CellValue<T>> {
    private T          value;
    private int        rowSpan = 1;
    private Coordinate coordinate;
    private Coordinate mapHtmlToData;
    private Coordinate mapDataToHtml;
    private boolean    isSelected;

    public CellValue(T value,
                     int row,
                     int col) {
        this.value = value;
        this.coordinate = new Coordinate( row,
                                          col );
        this.mapHtmlToData = new Coordinate( row,
                                             col );
        this.mapDataToHtml = new Coordinate( row,
                                             col );
    }

    // Used for sorting
    public int compareTo(CellValue<T> cv) {
        if ( this.value == null ) {
            if ( cv.value == null ) {
                return 0;
            }
            return 1;
        } else {
            if ( cv.value == null ) {
                return -1;
            }
        }
        return this.value.compareTo( cv.value );
    }

    public T getValue() {
        return this.value;
    }

    public void setHtmlCoordinate(Coordinate c) {
        if ( c == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.mapDataToHtml = c;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        this.value = (T) value;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public Coordinate getHtmlCoordinate() {
        return new Coordinate( this.mapDataToHtml );
    }

    public Coordinate getPhysicalCoordinate() {
        return new Coordinate( this.mapHtmlToData );
    }

    public int getRowSpan() {
        return this.rowSpan;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public void setCoordinate(Coordinate coordinate) {
        if ( coordinate == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.coordinate = coordinate;
    }

    public void setPhysicalCoordinate(Coordinate c) {
        if ( c == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.mapHtmlToData = c;
    }

    public void setRowSpan(int rowSpan) {
        if ( rowSpan < 0 ) {
            throw new IllegalArgumentException( "rowSpan cannot be less than zero." );
        }
        this.rowSpan = rowSpan;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    @SuppressWarnings("rawtypes")
    // Used by calls to DynamicDataRow.equals()
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( !(obj instanceof CellValue) ) {
            return false;
        }
        CellValue that = (CellValue) obj;
        return nullOrEqual( this.value,
                            that.value )
                && this.rowSpan == that.rowSpan
                && nullOrEqual( this.coordinate,
                                that.coordinate )
                && nullOrEqual( this.mapHtmlToData,
                                that.mapHtmlToData )
                && nullOrEqual( this.mapDataToHtml,
                                that.mapDataToHtml )
                && this.isSelected == that.isSelected;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash
               * 31
               + (value == null ? 0 : value.hashCode());
        hash = hash
               * 31
               + rowSpan;
        hash = hash
               * 31
               + (coordinate == null ? 0 : coordinate.hashCode());
        hash = hash
               * 31
                + (mapHtmlToData == null ? 0 : mapHtmlToData.hashCode());
        hash = hash
               * 31
                + (mapDataToHtml == null ? 0 : mapDataToHtml.hashCode());
        hash = hash
               * 31
               + ((Boolean) isSelected).hashCode();
        return hash;
    }

    // Check whether two objects are equal or both null
    private boolean nullOrEqual(Object thisAttr,
                                Object thatAttr) {
        if ( thisAttr == null
             && thatAttr == null ) {
            return true;
        }
        if ( thisAttr == null
             && thatAttr != null ) {
            return false;
        }
        return thisAttr.equals( thatAttr );
    }

}
