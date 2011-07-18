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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CloseTabEvent extends GwtEvent<CloseTabEvent.Handler> {

    public interface Handler extends EventHandler {
        public void onCloseTab( CloseTabEvent closeTabEvent );
    }

    public static Type<CloseTabEvent.Handler> TYPE = new Type<CloseTabEvent.Handler>();

    private final String key;

    public CloseTabEvent( String key ) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public Type<CloseTabEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CloseTabEvent.Handler eventHandler ) {
        eventHandler.onCloseTab( this );
    }
}
