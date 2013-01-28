package org.kie.guvnor.guided.scorecard.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.SaveCommand;
import org.kie.guvnor.commons.ui.client.save.SaveOpWrapper;
import org.kie.guvnor.guided.scorecard.client.resources.i18n.Constants;
import org.kie.guvnor.guided.scorecard.client.resources.images.ImageResources;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;
import org.kie.guvnor.guided.scorecard.service.GuidedScoreCardEditorService;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Handler for the creation of new Guided Score Cards
 */
@ApplicationScoped
public class NewGuidedScoreCardHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = "scgd";

    @Inject
    private Caller<GuidedScoreCardEditorService> scoreCardService;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newGuidedScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.scoreCardIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( fileName );

        new SaveOpWrapper( path, new SaveCommand() {
            @Override
            public void execute( final String comment ) {
                scoreCardService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void aVoid ) {
                        notifySuccess();
                        final PlaceRequest place = new PathPlaceRequest( path,
                                                                         "GuidedScoreCardEditor" );
                        placeManager.goTo( place );
                    }
                } ).save( path,
                          model );
            }
        } ).save();
    }

}
