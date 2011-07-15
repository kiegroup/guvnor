/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.packages;

import com.google.gwt.event.shared.GwtEvent;

public class CloseTabContentWidgetEvent extends GwtEvent<CloseTabContentWidgetEventHandler> {

    public static Type<CloseTabContentWidgetEventHandler> TYPE = new Type<CloseTabContentWidgetEventHandler>();

    private final String key;

    public CloseTabContentWidgetEvent( String key ) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public Type<CloseTabContentWidgetEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CloseTabContentWidgetEventHandler eventHandler ) {
        eventHandler.onCloseTabContentWidget( this );
    }
}
