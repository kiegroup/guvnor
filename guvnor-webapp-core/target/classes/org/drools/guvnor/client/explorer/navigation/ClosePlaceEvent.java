/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.place.shared.Place;

public class ClosePlaceEvent extends GwtEvent<ClosePlaceEvent.Handler> {

    public interface Handler extends EventHandler {
        public void onClosePlace(ClosePlaceEvent closePlaceEvent);
    }

    public static Type<ClosePlaceEvent.Handler> TYPE = new Type<ClosePlaceEvent.Handler>();

    private final Place place;

    public ClosePlaceEvent(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    @Override
    public Type<ClosePlaceEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ClosePlaceEvent.Handler eventHandler) {
        eventHandler.onClosePlace(this);
    }
}
