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

import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.projecteditor.client.MessageService;
import org.kie.guvnor.projecteditor.model.builder.Message;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

import static org.kie.guvnor.projecteditor.client.forms.MenuBarTestHelpers.clickFirst;
import static org.kie.guvnor.projecteditor.client.forms.MenuBarTestHelpers.clickSecond;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectEditorScreenTest {

    private ProjectEditorScreenView view;
    private ProjectEditorScreenView.Presenter presenter;
    private GroupArtifactVersionEditorPanel gavPanel;
    private KProjectEditorPanel kProjectEditorPanel;
    private ProjectEditorScreen screen;
    private MockProjectEditorServiceCaller projectEditorServiceCaller;
    private MessageService messageService;

    @Before
    public void setUp() throws Exception {
        view = mock(ProjectEditorScreenView.class);
        when(view.getSaveMenuItemText()).thenReturn("");
        when(view.getBuildMenuItemText()).thenReturn("");
        gavPanel = mock(GroupArtifactVersionEditorPanel.class);
        kProjectEditorPanel = mock(KProjectEditorPanel.class);
        projectEditorServiceCaller = new MockProjectEditorServiceCaller();
        messageService = mock(MessageService.class);
        screen = new ProjectEditorScreen(view, gavPanel, kProjectEditorPanel, projectEditorServiceCaller, messageService);
        presenter = screen;
    }

    @Test
    public void testBasicSetup() throws Exception {
        verify(view).setPresenter(presenter);
        verify(view).setGroupArtifactVersionEditorPanel(gavPanel);
        verify(view).setKProjectEditorPanel(kProjectEditorPanel);
    }

    @Test
    public void testInit() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        verify(view).setKProjectToggleOff();
        verify(gavPanel).init(path);
        verify(kProjectEditorPanel, never()).init(any(Path.class));
    }

    @Test
    public void testInitWithKProjectFile() throws Exception {
        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(pathToKProjectXML);
        Path path = mock(Path.class);
        screen.init(path);

        verify(view).setKProjectToggleOn();
        verify(gavPanel).init(path);
        verify(kProjectEditorPanel).init(pathToKProjectXML);
    }

    @Test
    public void testEnableKProject() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(pathToKProjectXML);
        presenter.onKProjectToggleOn();

        verify(kProjectEditorPanel).init(pathToKProjectXML);
    }

    @Test
    public void testSave() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickFirst(menuBar);

        verify(gavPanel).save();
        verify(kProjectEditorPanel, never()).save();
    }

    @Test
    public void testSaveBoth() throws Exception {
        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(pathToKProjectXML);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickFirst(menuBar);

        verify(gavPanel).save();
        verify(kProjectEditorPanel).save();
    }

    @Test
    public void testBuild() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(null);
        projectEditorServiceCaller.setUpMessages(new Messages());
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickSecond(menuBar);

        verify(view).showBuildSuccessful();
        verify(messageService, never()).addMessages(any(Messages.class));
    }

    @Test
    public void testFailingBuild() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKProjectFileIfAny(null);
        Messages messages = new Messages();
        messages.getDeletedMessages().add(new Message());
        projectEditorServiceCaller.setUpMessages(messages);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickSecond(menuBar);

        verify(view, never()).showBuildSuccessful();
        verify(messageService).addMessages(any(Messages.class));
    }
}
