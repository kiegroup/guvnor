package org.kie.guvnor.globals.client.editor;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.globals.client.resources.i18n.GlobalsEditorConstants;
import org.kie.guvnor.globals.client.type.GlobalResourceType;
import org.kie.guvnor.globals.model.GlobalsEditorContent;
import org.kie.guvnor.globals.model.GlobalsModel;
import org.kie.guvnor.globals.service.GlobalsEditorService;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.services.version.VersionService;
import org.kie.guvnor.services.version.events.RestoreEvent;
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

/**
 * Globals Editor Presenter
 */
@WorkbenchEditor(identifier = "org.kie.guvnor.globals", supportedTypes = { GlobalResourceType.class }, priority = 101)
public class GlobalsEditorPresenter {

    @Inject
    private Caller<GlobalsEditorService> globalsEditorService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Caller<VersionService> versionService;

    @Inject
    private GlobalsEditorView view;

    @Inject
    private ViewSourceView viewSource;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<RestoreEvent> restoreEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private Path path;
    private PlaceRequest place;
    private boolean isReadOnly;

    private GlobalsModel model;
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
                globalsEditorService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        viewSource.setContent( response );
                    }
                } ).toSource( path, model );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );

        multiPage.addWidget( metadataWidget,
                             MetadataConstants.INSTANCE.Metadata() );

        loadContent();
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

    private void loadContent() {
        globalsEditorService.call( new RemoteCallback<GlobalsEditorContent>() {
            @Override
            public void callback( final GlobalsEditorContent content ) {
                model = content.getModel();
                oracle = content.getDataModel();
                oracle.filter();

                view.setContent( content.getDataModel(),
                                 content.getModel().getGlobals(),
                                 isReadOnly );

                view.hideBusyIndicator();
            }
        } ).loadContent( path );

        metadataService.call( new RemoteCallback<Metadata>() {
            @Override
            public void callback( final Metadata metadata ) {
                metadataWidget.setContent( metadata,
                                           isReadOnly );
            }
        } ).getMetadata( path );
    }

    @OnSave
    public void onSave() {
        if ( isReadOnly ) {
            view.alertReadOnly();
            return;
        }

        new SaveOperationService().save( path, new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                globalsEditorService.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( final Path response ) {
                        view.setNotDirty();
                        view.hideBusyIndicator();
                        metadataWidget.resetDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                    }
                } ).save( path,
                          model,
                          metadataWidget.getContent(),
                          comment );
            }
        } );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @IsDirty
    public boolean isDirty() {
        if ( isReadOnly ) {
            return false;
        }
        return ( view.isDirty() || metadataWidget.isDirty() );
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
        if ( isReadOnly ) {
            return GlobalsEditorConstants.INSTANCE.globalsEditorReadOnlyTitle0( path.getFileName() );
        }
        return GlobalsEditorConstants.INSTANCE.globalsEditorTitle0( path.getFileName() );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    public void onRestore( @Observes RestoreEvent restore ) {
        if ( path == null || restore == null || restore.getPath() == null ) {
            return;
        }
        if ( path.equals( restore.getPath() ) ) {
            loadContent();
            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRestored() ) );
        }
    }

}
