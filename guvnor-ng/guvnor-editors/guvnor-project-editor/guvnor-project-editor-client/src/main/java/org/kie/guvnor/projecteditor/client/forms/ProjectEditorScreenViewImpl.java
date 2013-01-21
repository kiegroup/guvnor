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

import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.common.MultiPageEditorView;
import org.uberfire.client.common.Page;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class ProjectEditorScreenViewImpl
        extends MultiPageEditorView
        implements ProjectEditorScreenView {


    private final Event<NotificationEvent> notificationEvent;

    @Inject
    public ProjectEditorScreenViewImpl(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void setGroupArtifactVersionEditorPanel(POMEditorPanel gavPanel) {
        addPage(new Page(gavPanel, ProjectEditorConstants.INSTANCE.PomDotXml()) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public void setKModuleEditorPanel(KModuleEditorPanel kModuleEditorPanel) {
        addPage(new Page(kModuleEditorPanel, ProjectEditorConstants.INSTANCE.KModuleDotXml()) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
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
        notificationEvent.fire(new NotificationEvent(ProjectEditorConstants.INSTANCE.BuildSuccessful()));
    }
}
