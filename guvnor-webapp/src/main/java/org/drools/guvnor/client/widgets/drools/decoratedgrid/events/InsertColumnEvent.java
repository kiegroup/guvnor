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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to insert a column
 */
public abstract class InsertColumnEvent<T> extends GwtEvent<InsertColumnEvent.Handler<T>> {

    public static interface Handler<T>
        extends
        EventHandler {

        void onInsertColumn(InsertColumnEvent<T> event);
    }

    private int     index;

    private boolean redraw = true;

    private T       column;

    public InsertColumnEvent(T column,
                             int index,
                             boolean redraw) {
        this.column = column;
        this.index = index;
        this.redraw = redraw;
    }

    public InsertColumnEvent(T column,
                             int index) {
        this.column = column;
        this.index = index;
    }

    public T getColumn() {
        return this.column;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean redraw() {
        return this.redraw;
    }

    @Override
    protected void dispatch(InsertColumnEvent.Handler<T> handler) {
        handler.onInsertColumn( this );
    }

}
