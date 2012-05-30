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

import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing the removal of a Pattern
 */
public class PatternRemovedEvent extends GwtEvent<PatternRemovedEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onPatternRemoved(PatternRemovedEvent event);
    }

    public static Type<PatternRemovedEvent.Handler> TYPE = new Type<PatternRemovedEvent.Handler>();

    private final Pattern52                         pattern;

    public PatternRemovedEvent(Pattern52 pattern) {
        this.pattern = pattern;
    }

    public Pattern52 getPattern() {
        return pattern;
    }

    @Override
    public Type<PatternRemovedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PatternRemovedEvent.Handler handler) {
        handler.onPatternRemoved( this );
    }

}
