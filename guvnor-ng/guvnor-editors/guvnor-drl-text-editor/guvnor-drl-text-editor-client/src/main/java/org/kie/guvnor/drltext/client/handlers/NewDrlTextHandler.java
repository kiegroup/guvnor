package org.kie.guvnor.drltext.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.drltext.client.resources.ImageResources;
import org.kie.guvnor.drltext.client.resources.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Handler for the creation of new Fact Models
 */
@ApplicationScoped
public class NewDrlTextHandler implements NewResourceHandler {

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
    public void create( final Path path ) {
        vfs.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                final PlaceRequest place = new DefaultPlaceRequest( "DRLEditor" );
                place.addParameter( "path:uri",
                                    path.toURI() );
                place.addParameter( "path:name",
                                    path.getFileName() );
                placeManager.goTo( place );
            }
        } ).write( path,
                   "" );
    }

    @Override
    public IsWidget getExtension() {
        //No additional parameters required.
        return null;
    }

    @Override
    public boolean validate() {
        //Item is always valid.
        return true;
    }

}
