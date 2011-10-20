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
 * An event representing whether Pattern bindings are unique
 */
public class DuplicatePatternsEvent extends GwtEvent<DuplicatePatternsEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onDuplicatePatterns(DuplicatePatternsEvent event);
    }

    public static Type<DuplicatePatternsEvent.Handler> TYPE = new Type<DuplicatePatternsEvent.Handler>();

    private boolean                                    arePatternBindingsUnique;

    public DuplicatePatternsEvent(boolean arePatternBindingsUnique) {
        this.arePatternBindingsUnique = arePatternBindingsUnique;
    }

    public boolean getArePatternBindingsUnique() {
        return this.arePatternBindingsUnique;
    }

    @Override
    public Type<DuplicatePatternsEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DuplicatePatternsEvent.Handler handler) {
        handler.onDuplicatePatterns( this );
    }

}
