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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.Coordinate;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to signal that the UI has changed the underlying model data
 */
public class UpdateModelEvent extends GwtEvent<UpdateModelEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onUpdateModel(UpdateModelEvent event);
    }

    public static Type<UpdateModelEvent.Handler>                               TYPE = new Type<UpdateModelEvent.Handler>();

    //Updated data: Key=Coordinate of start, Value=Cell-row data
    private Map<Coordinate, List<List<CellValue< ? extends Comparable< ? >>>>> updates;

    public UpdateModelEvent(Map<Coordinate, List<List<CellValue< ? extends Comparable< ? >>>>> updates) {
        this.updates = updates;
    }

    public UpdateModelEvent(Coordinate c,
                            List<List<CellValue< ? extends Comparable< ? >>>> data) {
        this.updates = new HashMap<Coordinate, List<List<CellValue< ? extends Comparable< ? >>>>>();
        this.updates.put( c,
                          data );
    }

    public Map<Coordinate, List<List<CellValue< ? extends Comparable< ? >>>>> getUpdates() {
        return this.updates;
    }

    @Override
    public Type<UpdateModelEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateModelEvent.Handler handler) {
        handler.onUpdateModel( this );
    }

}
