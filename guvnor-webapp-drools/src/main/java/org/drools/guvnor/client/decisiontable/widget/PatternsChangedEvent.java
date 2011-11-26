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
package org.drools.guvnor.client.decisiontable.widget;

import java.util.List;

import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing a change in Patterns (added, deleted, edited)
 */
public class PatternsChangedEvent extends GwtEvent<PatternsChangedEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onPatternsChanged(PatternsChangedEvent event);
    }

    public static Type<PatternsChangedEvent.Handler> TYPE = new Type<PatternsChangedEvent.Handler>();

    private final List<Pattern52>                    patterns;

    public PatternsChangedEvent(List<Pattern52> patterns) {
        this.patterns = patterns;
    }

    public List<Pattern52> getPatterns() {
        return patterns;
    }

    @Override
    public Type<PatternsChangedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PatternsChangedEvent.Handler handler) {
        handler.onPatternsChanged( this );
    }

}
