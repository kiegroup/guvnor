/*
 * Copyright 2014 JBoss Inc
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
package org.guvnor.client.editors;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.type.POMResourceType;
import org.kie.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.kie.uberfire.client.editors.texteditor.TextResourceType;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchEditor(identifier = "GuvnorTextEditor", supportedTypes = {POMResourceType.class}, priority = 1)
public class POMEditorScreenPresenter
        extends TextEditorPresenter {

    @Inject
    private TextResourceType type;


    @OnStartup
    public void onStartup(final ObservablePath path) {
        super.onStartup(path);
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Default POM Editor";
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory.newTopLevelMenu("File").menus()
                .menu("Save").respondsWith(new Command() {
                    @Override
                    public void execute() {

                    }
                }).endMenu().endMenus().endMenu().build();
    }
}
