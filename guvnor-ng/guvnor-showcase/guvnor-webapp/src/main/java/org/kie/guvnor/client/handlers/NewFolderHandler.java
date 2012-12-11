package org.kie.guvnor.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.commons.data.Pair;
import org.kie.guvnor.client.resources.i18n.Constants;
import org.kie.guvnor.client.resources.images.ImageResources;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewFolderHandler implements NewResourceHandler {

    private static String FILE_TYPE = null;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newFolderDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public void create( final Path path ) {
        placeManager.goTo( "newFolderPopup" );
    }

    @Override
    public List<Pair<String, IsWidget>> getExtensions() {
        return null;
    }

    @Override
    public boolean validate() {
        return true;
    }

}
