package org.kie.guvnor.globals.client.handlers;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.globals.client.resources.i18n.GlobalsEditorConstants;
import org.kie.guvnor.globals.client.resources.images.GlobalsEditorImageResources;
import org.kie.guvnor.globals.model.Global;
import org.kie.guvnor.globals.service.GlobalsEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewGlobalHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "global.drl";

    @Inject
    private Caller<GlobalsEditorService> globalsService;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return GlobalsEditorConstants.INSTANCE.newGlobalDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GlobalsEditorImageResources.INSTANCE.globalsIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        final List<Global> model = new ArrayList<Global>();

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                globalsService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void aVoid ) {
                        notifySuccess();
                        final PlaceRequest place = new PathPlaceRequest( path,
                                                                         "org.kie.guvnor.globals" );
                        placeManager.goTo( place );
                    }
                } ).save( path,
                          model,
                          comment );
            }
        } );
    }

}
