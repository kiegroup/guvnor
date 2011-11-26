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
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing whether Action Insert Fact Fields are correctly defined
 */
public class ActionInsertFactFieldsDefinedEvent extends GwtEvent<ActionInsertFactFieldsDefinedEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onActionInsertFactFieldsDefined(ActionInsertFactFieldsDefinedEvent event);
    }

    public static Type<ActionInsertFactFieldsDefinedEvent.Handler> TYPE = new Type<ActionInsertFactFieldsDefinedEvent.Handler>();

    private boolean                                                areActionInsertFactFieldsDefined;

    public ActionInsertFactFieldsDefinedEvent(boolean areActionInsertFactFieldsDefined) {
        this.areActionInsertFactFieldsDefined = areActionInsertFactFieldsDefined;
    }

    public boolean getAreActionInsertFactFieldsDefined() {
        return this.areActionInsertFactFieldsDefined;
    }

    @Override
    public Type<ActionInsertFactFieldsDefinedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActionInsertFactFieldsDefinedEvent.Handler handler) {
        handler.onActionInsertFactFieldsDefined( this );
    }

}
