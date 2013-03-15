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

package org.kie.guvnor.guided.rule.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.commons.shared.rule.RuleModel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.guvnor.datamodel.events.ImportAddedEvent;
import org.kie.guvnor.datamodel.events.ImportRemovedEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.rule.client.type.GuidedRuleResourceType;
import org.kie.guvnor.guided.rule.model.GuidedEditorContent;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.kie.guvnor.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
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
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchEditor(identifier = "GuidedRuleEditor", supportedTypes = { GuidedRuleResourceType.class }, priority = 102)
public class GuidedRuleEditorPresenter {

    @Inject
    private ImportsWidgetPresenter importsWidget;

    @Inject
    private GuidedRuleEditorView view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<GuidedRuleEditorService> service;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Path path;
    private PlaceRequest place;
    private boolean isReadOnly;

    private RuleModel model;
    private DataModelOracle oracle;

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest place ) {
        this.path = path;
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        makeMenuBar();

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        multiPage.addWidget( view,
                             CommonConstants.INSTANCE.EditTabTitle() );

        multiPage.addPage( new Page( viewSource,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                service.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        viewSource.setContent( response );
                    }
                } ).toSource( path, view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );

        multiPage.addWidget( importsWidget,
                             CommonConstants.INSTANCE.ConfigTabTitle() );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                   isReadOnly ),
                                      new HasBusyIndicatorDefaultErrorCallback( view ) ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );

        service.call( getModelSuccessCallback(),
                      new HasBusyIndicatorDefaultErrorCallback( view ) ).loadContent( path );
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addCopy( path )
                    .addRename( path )
                    .addDelete( path )
                    .build();
        }
    }

    private RemoteCallback<GuidedEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GuidedEditorContent>() {

            @Override
            public void callback( final GuidedEditorContent response ) {
                model = response.getRuleModel();
                oracle = response.getDataModel();
                oracle.filter( model.getImports() );

                view.setContent( path,
                                 model,
                                 oracle,
                                 isReadOnly );
                importsWidget.setContent( oracle,
                                          model.getImports(),
                                          isReadOnly );

                view.hideBusyIndicator();
            }
        };
    }

    public void handleImportAddedEvent( @Observes ImportAddedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refresh();
    }

    public void handleImportRemovedEvent( @Observes ImportRemovedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refresh();
    }

    @OnSave
    public void onSave() {
        if ( isReadOnly ) {
            view.alertReadOnly();
            return;
        }

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 service.call( getSaveSuccessCallback(),
                                                               new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                                                                        view.getContent(),
                                                                                                                        metadataWidget.getContent(),
                                                                                                                        commitMessage );
                                             }
                                         } );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                view.setNotDirty();
                view.hideBusyIndicator();
                metadataWidget.resetDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        };
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
        return "Guided Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {

        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
