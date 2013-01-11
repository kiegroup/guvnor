package org.kie.guvnor.enums.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.enums.client.resources.ImageResources;
import org.kie.guvnor.enums.client.resources.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new Enumerations
 */
@ApplicationScoped
public class NewEnumHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "enumeration";

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
        return Constants.INSTANCE.newEnumDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.enumIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        vfs.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path response ) {
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path,
                                                                 "EnumEditor" );
                placeManager.goTo( place );
            }
        } ).write( path,
                   "" );
    }

}
