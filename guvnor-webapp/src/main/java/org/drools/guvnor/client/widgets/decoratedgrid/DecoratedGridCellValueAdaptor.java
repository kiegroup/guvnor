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

import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Cell that casts values to whatever is appropriate for the wrapped Cell
 * 
 * 
 * @param <T>
 *            The data-type required by the wrapped cell
 * @param <C>
 *            The data-type of columns represented in the domain model
 */
public class DecoratedGridCellValueAdaptor<T, C> extends
        AbstractCell<CellValue< ? extends Comparable< ? >>> {

    // Really we want AbstractCell<?> but that leads to generics hell
    private AbstractCell<T>         cell;

    protected MergableGridWidget<C> grid;

    /**
     * @param cell
     */
    public DecoratedGridCellValueAdaptor(AbstractCell<T> cell) {
        super( cell.getConsumedEvents() );
        this.cell = cell;
    }

    @Override
    public boolean dependsOnSelection() {
        return cell.dependsOnSelection();
    }

    @Override
    public Set<String> getConsumedEvents() {
        return cell.getConsumedEvents();
    }

    @Override
    public boolean handlesSelection() {
        return cell.handlesSelection();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEditing(Context context,
                             Element parent,
                             CellValue< ? extends Comparable< ? >> value) {
        return cell.isEditing( context,
                               parent,
                               (T) value.getValue() );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBrowserEvent(Context context,
                               Element parent,
                               CellValue< ? extends Comparable< ? >> value,
                               NativeEvent event,
                               ValueUpdater<CellValue< ? extends Comparable< ? >>> valueUpdater) {

        // Updates are passed back to the DecoratedGridWidget where merged cells
        // are also updated. Override the Column's FieldUpdater because
        // a Horizontal MergableGridWidget will potentially have a different
        // data-type per row.
        cell.onBrowserEvent( context,
                             parent,
                             (T) value.getValue(),
                             event,
                             new ValueUpdater<T>() {

                                 public void update(T value) {
                                     grid.update( value );
                                 }

                             } );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(Context context,
                       CellValue< ? extends Comparable< ? >> value,
                       SafeHtmlBuilder sb) {
        cell.render( context,
                     (T) value.getValue(),
                     sb );
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean resetFocus(Context context,
                              Element parent,
                              CellValue< ? extends Comparable< ? >> value) {
        return cell.resetFocus( context,
                                parent,
                                (T) value.getValue() );
    }

    /**
     * Inject a MergableGridWidget to handle value updates
     * 
     * @param manager
     */
    public void setMergableGridWidget(MergableGridWidget<C> grid) {
        this.grid = grid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Context context,
                         Element parent,
                         CellValue< ? extends Comparable< ? >> value) {
        cell.setValue( context,
                       parent,
                       (T) value.getValue() );
    }

}
