package org.kie.guvnor.drltext.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.drltext.client.resources.ImageResources;
import org.kie.guvnor.drltext.client.resources.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewDrlTextHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "drl";

    @Inject
    private Caller<VFSService> vfs;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewDrlTextDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.classImage() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        vfs.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path,
                                                                 "DRLEditor" );
                placeManager.goTo( place );
            }
        } ).write( path,
                   "" );
    }

}
