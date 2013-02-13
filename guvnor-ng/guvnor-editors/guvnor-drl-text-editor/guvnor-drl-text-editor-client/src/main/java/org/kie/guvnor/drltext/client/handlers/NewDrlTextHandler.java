package org.kie.guvnor.drltext.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.kie.guvnor.drltext.client.resources.images.ImageResources;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewDrlTextHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "drl";

    @Inject
    private Caller<DRLTextEditorService> drlTextService;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return DRLTextEditorConstants.INSTANCE.NewDrlTextDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.classImage() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );

        new SaveOperationService().save( path, new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                drlTextService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void aVoid ) {
                        notifySuccess();
                        notifyResourceAdded( path );
                        final PlaceRequest place = new PathPlaceRequest( path,
                                                                         "DRLEditor" );
                        placeManager.goTo( place );
                    }
                } ).save( path,
                          "",
                          comment );
            }
        } );
    }

}
