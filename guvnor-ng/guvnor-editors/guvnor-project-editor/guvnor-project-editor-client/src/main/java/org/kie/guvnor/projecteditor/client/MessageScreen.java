/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.projecteditor.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.guvnor.projecteditor.model.builder.Message;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.Messages")
public class MessageScreen {


    @Inject
    private PlaceManager placeManager;

    public void onMessageSent(@Observes Message message) {

    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Messages";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return new Label("Messages");
    }
}
