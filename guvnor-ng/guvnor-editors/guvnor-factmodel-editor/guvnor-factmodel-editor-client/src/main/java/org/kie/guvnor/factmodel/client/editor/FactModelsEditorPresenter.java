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

package org.kie.guvnor.factmodel.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.factmodel.model.FactMetaModel;
import org.kie.guvnor.factmodel.model.FactModelContent;
import org.kie.guvnor.factmodel.model.FactModels;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.LoadingPopup;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

import static org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder.*;

@Dependent
@WorkbenchEditor(identifier = "FactModelsEditor", fileTypes = "model.drl")
public class FactModelsEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final FactModels content,
                         final List<FactMetaModel> superTypeFactModels,
                         final ModelNameHelper modelNameHelper );

        FactModels getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private Caller<FactModelService> factModelService;

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    private Path path;

    @PostConstruct
    public void init() {
        multiPage.addWidget( view,
                             CommonConstants.INSTANCE.EditTabTitle() );
        multiPage.addPage( new Page( viewSource,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                factModelService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        viewSource.setContent( response );
                    }
                } ).toSource( view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        factModelService.call( new RemoteCallback<FactModelContent>() {
            @Override
            public void callback( final FactModelContent content ) {

                final ModelNameHelper modelNameHelper = new ModelNameHelper();

                for ( final FactMetaModel currentModel : content.getSuperTypes() ) {
                    modelNameHelper.getTypeDescriptions().put( currentModel.getName(),
                                                               currentModel.getName() );
                }

                view.setContent( content.getFactModels(),
                                 content.getSuperTypes(),
                                 modelNameHelper );
            }
        } ).loadContent( path );
    }

    @OnSave
    public void onSave() {
        factModelService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setNotDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        } ).save( path,
                  view.getContent() );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Fact Models Editor [" + path.getFileName() + "]";
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        return newResourceMenuBuilder().addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                factModelService.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path, view.getContent() );
            }
        } ).addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

}
