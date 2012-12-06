package org.kie.guvnor.factmodel.client.handlers;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.factmodel.client.resources.ImageResources;
import org.kie.guvnor.factmodel.client.resources.i18n.Constants;
import org.uberfire.backend.vfs.Path;

/**
 * Handler for the creation of new Fact Models
 */
@ApplicationScoped
public class NewFactModelHandler implements NewResourceHandler {

    private static String FILE_TYPE = "model.drl";

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newFactModelDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.factModelIcon() );
    }

    @Override
    public void create( final Path path ) {
        Window.alert( "Creating new Fact Model" + ( path == null ? "" : " at: " + path.toURI() ) );
    }
}
