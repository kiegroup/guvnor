package org.kie.guvnor.factmodel.client.handlers;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.factmodel.client.FactModelResourceType;
import org.kie.guvnor.factmodel.client.resources.i18n.Constants;
import org.kie.guvnor.factmodel.client.resources.images.ImageResources;
import org.kie.guvnor.factmodel.model.FactModels;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Handler for the creation of new Fact Models
 */
@ApplicationScoped
public class NewFactModelHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<FactModelService> factModelService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private FactModelResourceType resourceType;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newFactModelDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.factModelIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName ) {
        final FactModels factModel = new FactModels();

        new SaveOperationService().save( contextPath, new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                factModelService.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( final Path path ) {
                        notifySuccess();
                        notifyResourceAdded( path );
                        final PlaceRequest place = new PathPlaceRequest( path,
                                                                         "FactModelsEditor" );
                        placeManager.goTo( place );
                    }
                } ).save( contextPath, buildFileName( resourceType, baseFileName ), factModel, comment );
            }
        } );
    }

}
