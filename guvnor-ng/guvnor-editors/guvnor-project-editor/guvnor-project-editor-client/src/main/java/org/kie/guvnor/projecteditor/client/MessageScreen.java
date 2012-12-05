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
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.Messages")
public class MessageScreen
        implements MessageScreenView.Presenter {

    private final PlaceManager placeManager;
    private final MessageScreenView view;
    private final MessageService messageService;

    @Inject
    public MessageScreen(MessageScreenView view,
                         PlaceManager placeManager,
                         MessageService messageService) {
        this.view = view;
        this.placeManager = placeManager;
        this.messageService = messageService;

        view.setPresenter(this);
    }


    @OnFocus
    public void onFocus() {
        view.clean();
        init();
    }

    @OnStart
    public void init() {
        for (Message message : messageService.getMessageLog()) {
            switch (message.getLevel()) {
                case INFO:
                    view.addInfoLine(message.getId(), message.getText(), message.getColumn(), message.getLine());
                    break;
                case WARNING:
                    view.addWarningLine(message.getId(), message.getText(), message.getColumn(), message.getLine());
                    break;
                case ERROR:
                    view.addErrorLine(message.getId(), message.getText(), message.getColumn(), message.getLine());
                    break;
            }
        }
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
