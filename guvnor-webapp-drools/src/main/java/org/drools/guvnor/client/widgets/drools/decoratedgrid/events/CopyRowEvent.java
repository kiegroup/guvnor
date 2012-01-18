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
 * An event to copy a row
 */
public class CopyRowEvent extends GwtEvent<CopyRowEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onCopyRow(CopyRowEvent event);
    }

    public static Type<CopyRowEvent.Handler> TYPE = new Type<CopyRowEvent.Handler>();

    private int                              sourceRowIndex;
    private int                              targetRowIndex;

    public CopyRowEvent(int sourceRowIndex,
                        int targetRowIndex) {
        this.sourceRowIndex = sourceRowIndex;
        this.targetRowIndex = targetRowIndex;
    }

    public int getSourceRowIndex() {
        return this.sourceRowIndex;
    }

    public int getTargetRowIndex() {
        return this.targetRowIndex;
    }

    @Override
    public Type<CopyRowEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CopyRowEvent.Handler handler) {
        handler.onCopyRow( this );
    }

}
