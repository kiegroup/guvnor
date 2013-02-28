package org.kie.guvnor.guided.scorecard.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.guided.scorecard.shared.ScoreCardModel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.guided.scorecard.client.resources.i18n.Constants;
import org.kie.guvnor.guided.scorecard.client.resources.images.ImageResources;
import org.kie.guvnor.guided.scorecard.client.type.GuidedScoreCardResourceType;
import org.kie.guvnor.guided.scorecard.service.GuidedScoreCardEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new Guided Score Cards
 */
@ApplicationScoped
public class NewGuidedScoreCardHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<GuidedScoreCardEditorService> scoreCardService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private GuidedScoreCardResourceType resourceType;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newGuidedScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.scoreCardIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName ) {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( baseFileName );

        new SaveOperationService().save( contextPath,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 scoreCardService.call( new RemoteCallback<Path>() {
                                                     @Override
                                                     public void callback( final Path path ) {
                                                         notifySuccess();
                                                         final PlaceRequest place = new PathPlaceRequest( path,
                                                                                                          "GuidedScoreCardEditor" );
                                                         placeManager.goTo( place );
                                                     }
                                                 } ).create( contextPath,
                                                             buildFileName( resourceType,
                                                                            baseFileName ),
                                                             model,
                                                             comment );
                                             }
                                         } );
    }

}
