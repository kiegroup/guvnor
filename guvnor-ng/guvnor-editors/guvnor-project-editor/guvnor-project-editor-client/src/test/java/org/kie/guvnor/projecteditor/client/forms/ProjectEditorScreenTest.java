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
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectEditorScreenTest {

    private ProjectEditorScreenView view;
    private ProjectEditorScreenView.Presenter presenter;
    private GroupArtifactVersionEditorPanel gavPanel;
    private KProjectEditorPanel kProjectEditorPanel;
    private ProjectEditorScreen screen;
    private MockProjectEditorServiceCaller projectEditorServiceCaller;

    @Before
    public void setUp() throws Exception {
        view = mock(ProjectEditorScreenView.class);
        gavPanel = mock(GroupArtifactVersionEditorPanel.class);
        kProjectEditorPanel = mock(KProjectEditorPanel.class);
        projectEditorServiceCaller = new MockProjectEditorServiceCaller();
        screen = new ProjectEditorScreen(view, gavPanel, kProjectEditorPanel, projectEditorServiceCaller);
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

}
