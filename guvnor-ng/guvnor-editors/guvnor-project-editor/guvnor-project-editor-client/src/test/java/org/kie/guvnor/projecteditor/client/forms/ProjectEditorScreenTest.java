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

import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

import static org.junit.Assert.assertTrue;
import static org.kie.guvnor.projecteditor.client.forms.MenuBarTestHelpers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectEditorScreenTest {
    private ProjectEditorScreenView view;
    private POMEditorPanel gavPanel;
    private KModuleEditorPanel kModuleEditorPanel;
    private ProjectEditorScreenPresenter screen;
    private MockProjectEditorServiceCaller projectEditorServiceCaller;
    private MockBuildServiceCaller buildServiceCaller;
    private MockMetadataServiceCaller metadataServiceCaller;

    @Before
    public void setUp() throws Exception {
        view = mock(ProjectEditorScreenView.class);
        when(view.getSaveMenuItemText()).thenReturn("");
        when(view.getBuildMenuItemText()).thenReturn("");
        when(view.getEnableKieProjectMenuItemText()).thenReturn("");
        gavPanel = mock(POMEditorPanel.class);
        kModuleEditorPanel = mock(KModuleEditorPanel.class);
        projectEditorServiceCaller = new MockProjectEditorServiceCaller();
        buildServiceCaller = new MockBuildServiceCaller();
        metadataServiceCaller = new MockMetadataServiceCaller();
        screen = new ProjectEditorScreenPresenter(view, gavPanel, kModuleEditorPanel, projectEditorServiceCaller, buildServiceCaller, metadataServiceCaller);
    }

    @Test
    public void testBasicSetup() throws Exception {
        verify(view).setPOMEditorPanel(gavPanel);
        verify(view, never()).setKModuleEditorPanel(kModuleEditorPanel);
    }

    @Test
    public void testInit() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        verify(gavPanel).init(path);
        verify(kModuleEditorPanel, never()).init(any(Path.class));
    }

    @Test
    public void testInitWithKModuleFile() throws Exception {
        Path pathToKModuleXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKModuleXML);
        Path path = mock(Path.class);
        screen.init(path);

        verify(gavPanel).init(path);
        verify(kModuleEditorPanel).init(pathToKModuleXML);
    }

    @Test
    public void testEnableKProject() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path pathToGav = mock(Path.class);
        screen.init(pathToGav);

        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKProjectXML);
        MenuBar menuBar = screen.buildMenuBar();
        clickThird(menuBar);

        verify(kModuleEditorPanel).init(pathToKProjectXML);
        verify(view).setKModuleEditorPanel(kModuleEditorPanel);
    }

    @Test
    public void testSave() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickFirst(menuBar);

        verify(gavPanel).save(anyString(), any(Command.class), any(Metadata.class));
        verify(kModuleEditorPanel, never()).save(anyString(), any(Metadata.class));
    }

    @Test
    public void testSaveBoth() throws Exception {
        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKProjectXML);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickFirst(menuBar);

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(gavPanel).save(anyString(), commandArgumentCaptor.capture(), any(Metadata.class));
        commandArgumentCaptor.getValue().execute();
        verify(kModuleEditorPanel).save(anyString(), any(Metadata.class));
    }

    @Test
    public void testBuild() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);
        screen.init(path);

        MenuBar menuBar = screen.buildMenuBar();
        clickSecond(menuBar);

        assertTrue(buildServiceCaller.isBuildWasCalled());
    }
}
