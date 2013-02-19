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

package org.kie.guvnor.defaulteditor.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.file.AnyResourceType;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

/**
 * A text based editor for Domain Specific Language definitions
 */
@Dependent
@WorkbenchEditor(identifier = "GuvnorDefaultFileEditor", supportedTypes = {AnyResourceType.class}, priority = 1)
public class GuvnorTextEditorPresenter
        extends DefaultFileEditorPresenter {

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    @New
    private ResourceMenuBuilder menuBuilder;
    private MenuBar menuBar;

    private final MetadataWidget metadataWidget = new MetadataWidget();
    private boolean isReadOnly;
    private Path path;

    @OnStart
    public void onStart(final Path path,
                        final PlaceRequest place) {
        super.onStart(path);
        this.path = path;
        isReadOnly = place.getParameter("readOnly", null) == null ? false : true;
        makeMenuBar();
    }

    private void makeMenuBar() {
        if (isReadOnly) {
            menuBar = menuBuilder.addFileMenu().addRestoreVersion(path).build();
        } else {
            menuBar = menuBuilder.addFileMenu().addSave(new Command() {
                @Override
                public void execute() {
                    //TODO -Rikkola-
                }
            }).addCopy(
                    new Command() {
                        @Override
                        public void execute() {
                            //TODO -Rikkola-
                        }
                    }
            ).addRename(new Command() {
                @Override
                public void execute() {
                    //TODO -Rikkola-
                }
            }).addDelete(new Command() {
                @Override
                public void execute() {
                    //TODO -Rikkola-
                }
            }).build();
        }
    }

    @WorkbenchMenu
    public MenuBar getMenuBar() {
        return menuBar;
    }

    @OnSave
    public void onSave() {
        super.onSave();
    }

    @IsDirty
    public boolean isDirty() {
        return super.isDirty();
    }

    @OnClose
    public void onClose() {
        super.onClose();
    }

    @OnReveal
    public void onReveal() {
        super.onReveal();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        multiPage.addWidget(super.getWidget(),
                CommonConstants.INSTANCE.EditTabTitle());

        multiPage.addPage(new Page(metadataWidget, CommonConstants.INSTANCE.MetadataTabTitle()) {
            @Override
            public void onFocus() {
                metadataService.call(new RemoteCallback<Metadata>() {
                    @Override
                    public void callback(final Metadata metadata) {
                        metadataWidget.setContent(metadata,
                                isReadOnly);
                    }
                }).getMetadata(path);
            }

            @Override
            public void onLostFocus() {

            }
        });

        return multiPage;
    }
}
