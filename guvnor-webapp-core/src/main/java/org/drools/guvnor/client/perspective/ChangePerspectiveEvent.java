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
package org.drools.guvnor.client.perspective;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ChangePerspectiveEvent extends GwtEvent<ChangePerspectiveEvent.Handler> {

    public interface Handler
        extends
        EventHandler {

        void onChangePerspective(ChangePerspectiveEvent changePerspectiveEvent);
    }

    public static final Type<ChangePerspectiveEvent.Handler> TYPE = new Type<ChangePerspectiveEvent.Handler>();

    private final Perspective perspective;

    public ChangePerspectiveEvent(Perspective perspective) {
        this.perspective = perspective;
    }

    public Perspective getPerspective() {
        return perspective;
    }

    @Override
    public Type<ChangePerspectiveEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangePerspectiveEvent.Handler handler) {
        handler.onChangePerspective( this );
    }
}
