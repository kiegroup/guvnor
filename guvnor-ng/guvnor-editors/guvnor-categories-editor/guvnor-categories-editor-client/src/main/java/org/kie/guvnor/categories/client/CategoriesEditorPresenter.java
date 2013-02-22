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

package org.kie.guvnor.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.metadata.model.Categories;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.services.metadata.CategoriesService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 */
@Dependent
@WorkbenchEditor(identifier = "CategoryManager", supportedTypes = { CategoryDefinitionResourceType.class })
public class CategoriesEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final Categories categories );

        Categories getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private View view;

    @Inject
    private Caller<CategoriesService> categoryService;

    @Inject
    private FileMenuBuilder menuBuilder;

    private Path path;

    private Menus menus;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        makeMenuBar();

        categoryService.call( new RemoteCallback<Categories>() {

            @Override
            public void callback( final Categories response ) {
                view.setContent( response );
            }
        } ).getContent( path );
    }

    private void makeMenuBar() {
        menus = menuBuilder.addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

    @OnSave
    public void onSave() {
        categoryService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setNotDirty();
            }
        } ).save( path, view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Categories Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
