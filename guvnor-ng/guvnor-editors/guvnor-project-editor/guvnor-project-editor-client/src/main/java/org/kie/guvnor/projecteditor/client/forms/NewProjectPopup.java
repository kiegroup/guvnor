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

package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.projecteditor.client.places.KProjectEditorPlace;
import org.kie.guvnor.projecteditor.service.FileService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@WorkbenchPopup(identifier = "newProjectPopup")
public class NewProjectPopup
        implements NewProjectPopupView.Presenter {

    private final NewProjectPopupView view;

    private String name;

    private final Event<ClosePlaceEvent> workbenchPartCloseEvent;
    private PlaceRequest placeRequest;
    private final Caller<FileService> projectEditorServiceCaller;
    private final PlaceManager placeManager;

    @Inject
    public NewProjectPopup(Caller<FileService> projectEditorServiceCaller,
                           NewProjectPopupView view,
                           Event<ClosePlaceEvent> workbenchPartCloseEvent,
                           PlaceManager placeManager) {
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.view = view;
        this.workbenchPartCloseEvent = workbenchPartCloseEvent;
        this.placeManager = placeManager;
        view.setPresenter(this);
    }

    @OnStart
    public void init(PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Project";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @Override
    public void onOk() {
        projectEditorServiceCaller.call(new RemoteCallback<Path>() {
            @Override
            public void callback(Path folderPath) {

                //TODO Fire hilight selected file/folder event instead of goto
                placeManager.goTo(new KProjectEditorPlace(folderPath));

                close();
            }
        }).newProject(name);
    }

    @Override
    public void onCancel() {
        close();
    }

    @Override
    public void onNameChange(String text) {
        name = text;
    }

    private void close() {
        workbenchPartCloseEvent.fire(new ClosePlaceEvent(placeRequest));
    }
}
