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

import com.google.gwt.user.client.ui.SimplePanel;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.common.MultiPageEditorView;
import org.uberfire.client.common.Page;

public class ProjectEditorScreenViewImpl
        extends MultiPageEditorView
        implements ProjectEditorScreenView {



    public ProjectEditorScreenViewImpl() {

    }

    @Override
    public void setPresenter(Presenter presenter) {
        //TODO -Rikkola-
    }

    @Override
    public void setGroupArtifactVersionEditorPanel(GroupArtifactVersionEditorPanel gavPanel) {
        addPage(new Page(gavPanel, "pom.xml") {
            @Override
            public void onFocus() {
                //TODO -Rikkola-
            }

            @Override
            public void onLostFocus() {
                //TODO -Rikkola-
            }
        });
    }

    @Override
    public void setKProjectEditorPanel(KProjectEditorPanel kProjectEditorPanel) {
        addPage(new Page(kProjectEditorPanel, "kproject.xml") {
            @Override
            public void onFocus() {
                //TODO -Rikkola-
            }

            @Override
            public void onLostFocus() {
                //TODO -Rikkola-
            }
        });
    }


    @Override
    public String getEnableKieProjectMenuItemText() {
        return ProjectEditorConstants.INSTANCE.EnableKieProject();
    }

    @Override
    public String getSaveMenuItemText() {
        return ProjectEditorConstants.INSTANCE.Save();
    }

    @Override
    public String getBuildMenuItemText() {
        return ProjectEditorConstants.INSTANCE.Build();
    }

    @Override
    public void showBuildSuccessful() {
        //TODO -Rikkola-
    }
}
