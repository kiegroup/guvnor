package org.kie.guvnor.projecteditor.client.forms;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.DefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.popups.text.FormPopup;
import org.kie.guvnor.project.model.KBaseModel;
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

public class KModuleEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private final Caller<KModuleService> projectEditorServiceCaller;

    private KModuleModel model;
    private Path path;

    private final KModuleEditorPanelView view;
    private boolean hasBeenInitialized = false;

    @Inject
    public KModuleEditorPanel( Caller<KModuleService> projectEditorServiceCaller,
                               KBaseForm form,
                               FormPopup namePopup,
                               KModuleEditorPanelView view ) {
        super( view, form, namePopup );

        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.view = view;
    }

    public void init( Path path,
                      boolean readOnly ) {
        this.path = path;
        if ( readOnly ) {
            view.makeReadOnly();
        }
        //Busy popup is handled by ProjectEditorScreen
        projectEditorServiceCaller.call( getModelSuccessCallback(),
                                         new DefaultErrorCallback() ).load( path );
    }

    private RemoteCallback<KModuleModel> getModelSuccessCallback() {
        return new RemoteCallback<KModuleModel>() {

            @Override
            public void callback( final KModuleModel model ) {
                KModuleEditorPanel.this.model = model;
                setItems( model.getKBases() );
                hasBeenInitialized = true;
            }
        };
    }

    @Override
    protected KBaseModel createNew( String name ) {
        KBaseModel model = new KBaseModel();
        model.setName( name );
        return model;
    }

    public void save( String commitMessage,
                      Metadata metadata ) {
        //Busy popup is handled by ProjectEditorScreen
        projectEditorServiceCaller.call( getSaveSuccessCallback(),
                                         new DefaultErrorCallback() ).save( path,
                                                                            model,
                                                                            metadata,
                                                                            commitMessage );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                view.showSaveSuccessful( "kmodule.xml" );
            }
        };
    }

    public boolean hasBeenInitialized() {
        return hasBeenInitialized;
    }
}
