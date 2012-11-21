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

package org.kie.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.kie.projecteditor.shared.model.KProjectModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;
import org.kie.projecteditor.shared.service.ProjectEditorService;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.PlaceRequest;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ProjectEditorScreenTest {


    private Path path;
    private ProjectEditorService projectEditorService;
    private ProjectEditorScreenView view;
    private MockCaller caller;
    private ArgumentCaptor<RemoteCallback> argumentCaptor;
    private PlaceRequest placeRequest;
    private ProjectEditorScreen screen;
    private ProjectEditorScreenView.Presenter presenter;
    private AddNewKBasePopup addNewKBasePopup;

    @Before
    public void setUp() throws Exception {
        path = mock(Path.class);
        projectEditorService = mock(ProjectEditorService.class);
        view = mock(ProjectEditorScreenView.class);
        caller = mock(MockCaller.class);

        argumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);
        when(
                caller.call(argumentCaptor.capture())
        ).thenReturn(
                projectEditorService
        );

        placeRequest = mock(PlaceRequest.class);
        when(
                placeRequest.getParameter("path", null)
        ).thenReturn(
                path
        );

        addNewKBasePopup = mock(AddNewKBasePopup.class);
        screen = new ProjectEditorScreen(caller, addNewKBasePopup, view);
        presenter = screen;

        screen.init(placeRequest);
    }

    @Test
    public void testShowEmptyModel() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        callback.callback(new KProjectModel());

        verify(projectEditorService).load(path);
        verify(view, never()).addKnowledgeBaseConfiguration(anyString());
    }

    @Test
    public void testShowModelWithSessions() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        kProjectModel.add(createKBaseConfiguration("First"));
        kProjectModel.add(createKBaseConfiguration("Second"));
        kProjectModel.add(createKBaseConfiguration("Third"));
        callback.callback(kProjectModel);

        verify(projectEditorService).load(path);
        verify(view).addKnowledgeBaseConfiguration("First");
        verify(view).addKnowledgeBaseConfiguration("Second");
        verify(view).addKnowledgeBaseConfiguration("Third");
        verify(view, times(3)).addKnowledgeBaseConfiguration(anyString());
    }

    @Test
    public void testSelectKBase() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        KnowledgeBaseConfiguration theOne = createKBaseConfiguration("TheOne");
        kProjectModel.add(theOne);
        callback.callback(kProjectModel);

        presenter.onKBaseSelection("TheOne");

        verify(view).showForm(theOne);
    }

    @Test
    public void testAddKBase() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        callback.callback(kProjectModel);

        presenter.onAddNewKBase();

        KnowledgeBaseConfiguration theOne = createKBaseConfiguration("TheOne");

        ArgumentCaptor<AddKBaseCommand> addKBaseCommandArgumentCaptor = ArgumentCaptor.forClass(AddKBaseCommand.class);
        verify(addNewKBasePopup).show(addKBaseCommandArgumentCaptor.capture());
        addKBaseCommandArgumentCaptor.getValue().add(theOne);

        assertEquals(theOne, kProjectModel.get("TheOne"));
        verify(view).addKnowledgeBaseConfiguration("TheOne");
        verify(view).selectKBase("TheOne");
        verify(view).showForm(theOne);
    }

    @Test
    public void testRemoveKBase() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        kProjectModel.add(createKBaseConfiguration("RemoveMe"));
        callback.callback(kProjectModel);

        presenter.onKBaseSelection("RemoveMe");

        presenter.onRemoveKBase();

        assertNull(kProjectModel.get("RemoveMe"));
        verify(view).removeKnowledgeBaseConfiguration("RemoveMe");
    }

    @Test
    public void testRemoveKBaseNoItemSelected() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        kProjectModel.add(createKBaseConfiguration("CantRemoveMe"));
        callback.callback(kProjectModel);

        presenter.onRemoveKBase();

        verify(view).showPleaseSelectAKBaseInfo();

        assertNotNull(kProjectModel.get("CantRemoveMe"));
        verify(view, never()).removeKnowledgeBaseConfiguration("CantRemoveMe");
    }

    @Test
    public void testDoubleClickRemoveSecondTimeWithoutATarget() throws Exception {

        RemoteCallback callback = argumentCaptor.getValue();
        KProjectModel kProjectModel = new KProjectModel();
        kProjectModel.add(createKBaseConfiguration("RemoveMe"));
        kProjectModel.add(createKBaseConfiguration("CantRemoveMe"));
        callback.callback(kProjectModel);

        // Select one and remove.
        presenter.onKBaseSelection("RemoveMe");
        presenter.onRemoveKBase();

        // Click again, nothing is selected.
        presenter.onRemoveKBase();

        verify(view).showPleaseSelectAKBaseInfo();

        assertNotNull(kProjectModel.get("CantRemoveMe"));
        verify(view, never()).removeKnowledgeBaseConfiguration("CantRemoveMe");
    }

    private KnowledgeBaseConfiguration createKBaseConfiguration(String name) {
        KnowledgeBaseConfiguration knowledgeBaseConfiguration = new KnowledgeBaseConfiguration();
        knowledgeBaseConfiguration.setFullName(name);
        return knowledgeBaseConfiguration;
    }

    abstract class MockCaller
            implements Caller<ProjectEditorService> {
    }
}

