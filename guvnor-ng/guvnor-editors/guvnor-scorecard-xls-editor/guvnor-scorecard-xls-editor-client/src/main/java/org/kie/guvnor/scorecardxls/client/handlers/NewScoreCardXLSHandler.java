package org.kie.guvnor.scorecardxls.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.scorecardxls.client.editor.NewScoreCardXLSPopup;
import org.kie.guvnor.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.kie.guvnor.scorecardxls.client.resources.images.ImageResources;
import org.kie.guvnor.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.kie.guvnor.scorecardxls.service.ScoreCardXLSService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewScoreCardXLSHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<ScoreCardXLSService> scoreCardXLSService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ScoreCardXLSResourceType resourceType;

    @Override
    public String getDescription() {
        return ScoreCardXLSEditorConstants.INSTANCE.NewScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.classImage() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName ) {
        NewScoreCardXLSPopup popup = new NewScoreCardXLSPopup( contextPath,
                                                               buildFileName( resourceType,
                                                                              baseFileName ),
                                                               new Command() {

                                                                   @Override
                                                                   public void execute() {
                                                                       notifySuccess();
                                                                       final Path newPath = PathFactory.newPath( contextPath.getFileSystem(),
                                                                                                                 buildFileName( resourceType,
                                                                                                                                baseFileName ),
                                                                                                                 contextPath.toURI() );
                                                                       final PlaceRequest place = new PathPlaceRequest( newPath );
                                                                       placeManager.goTo( place );
                                                                   }

                                                               } );
        popup.show();
    }

}
