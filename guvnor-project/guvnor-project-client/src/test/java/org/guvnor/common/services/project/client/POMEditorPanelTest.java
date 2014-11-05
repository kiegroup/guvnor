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

package org.guvnor.common.services.project.client;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.PlaceManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class POMEditorPanelTest {

    private POMEditorPanelView view;
    private POMEditorPanel panel;
    private PlaceManager placeManager;
    private POMEditorPanelView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock(POMEditorPanelView.class);
        placeManager = mock(PlaceManager.class);
        panel = new POMEditorPanel(view, placeManager);
        presenter = panel;
    }

    @Test
    public void testLoad() throws Exception {
        POM gavModel = createTestModel("group", "artifact", "1.1.1");
        gavModel.setParent(new GAV("org.parent", "parent", "1.1.1"));
        panel.setPOM(gavModel, false);

        verify(view).setGAV(gavModel.getGav());
        verify(view).setTitleText("artifact");
        verify(view).setParentGAV(gavModel.getParent());
        verify(view).disableGroupID("");
        verify(view).disableVersion("");
        verify(view).showParentGAV();

        gavModel = createTestModel("pomName", "pomDescription", "group", "artifact", "1.1.1");
        panel.setPOM(gavModel, false);

        verify(view).setName("pomName");
        verify(view).setDescription("pomDescription");
        verify(view).enableGroupID();
        verify(view).enableVersion();
        verify(view).hideParentGAV();
    }

    @Test
    public void testOpenProjectContext() throws Exception {
        presenter.onOpenProjectContext();
        verify(placeManager).goTo("repositoryStructureScreen");
    }

    private POM createTestModel(String group, String artifact, String version) {
        return new POM(new GAV(group, artifact, version));
    }

    private POM createTestModel(String name, String description, String group, String artifact, String version) {
        return new POM(name, description, new GAV(group, artifact, version));
    }
}
