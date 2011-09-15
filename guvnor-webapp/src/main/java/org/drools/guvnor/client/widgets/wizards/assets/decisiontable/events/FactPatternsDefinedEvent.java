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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing whether Fact Patterns are correctly defined
 */
public class FactPatternsDefinedEvent extends GwtEvent<FactPatternsDefinedEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onFactPatternsDefined(FactPatternsDefinedEvent event);
    }

    public static Type<FactPatternsDefinedEvent.Handler> TYPE = new Type<FactPatternsDefinedEvent.Handler>();

    private boolean                                      areFactPatternsDefined;

    public FactPatternsDefinedEvent(boolean areFactPatternsDefined) {
        this.areFactPatternsDefined = areFactPatternsDefined;
    }

    public boolean getAreFactPatternsDefined() {
        return this.areFactPatternsDefined;
    }

    @Override
    public Type<FactPatternsDefinedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FactPatternsDefinedEvent.Handler handler) {
        handler.onFactPatternsDefined( this );
    }

}
