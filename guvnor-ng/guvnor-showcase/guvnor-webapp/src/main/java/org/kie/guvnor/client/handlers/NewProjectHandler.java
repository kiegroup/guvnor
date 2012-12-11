package org.kie.guvnor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.client.resources.i18n.Constants;
import org.kie.guvnor.client.resources.images.ImageResources;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Handler for the creation of new Projects
 */
@ApplicationScoped
public class NewProjectHandler implements NewResourceHandler {

    private static String FILE_TYPE = null;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.newProjectIcon() );
    }

    @Override
    public void create( final Path path ) {
        // TODO: Just show the new project popup here, it does not need to be a place. -Rikkola-
        placeManager.goTo( "newProjectPopup" );
    }

    @Override
    public IsWidget getExtension() {
        return null;
    }

    @Override
    public boolean validate() {
        return true;
    }

}
