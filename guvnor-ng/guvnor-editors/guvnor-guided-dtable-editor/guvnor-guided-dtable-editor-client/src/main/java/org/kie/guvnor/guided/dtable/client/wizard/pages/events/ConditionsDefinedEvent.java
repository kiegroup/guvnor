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
package org.kie.guvnor.guided.dtable.client.wizard.pages.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing whether Conditions are correctly defined
 */
public class ConditionsDefinedEvent extends GwtEvent<ConditionsDefinedEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onConditionsDefined( ConditionsDefinedEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private boolean areConditionsDefined;

    public ConditionsDefinedEvent( final boolean areConditionsDefined ) {
        this.areConditionsDefined = areConditionsDefined;
    }

    public boolean getAreConditionsDefined() {
        return this.areConditionsDefined;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final ConditionsDefinedEvent.Handler handler ) {
        handler.onConditionsDefined( this );
    }

}
