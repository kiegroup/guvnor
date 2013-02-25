package org.kie.guvnor.dtablexls.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.dtablexls.client.type.DecisionTableXLSResourceType;
import org.kie.guvnor.dtablexls.client.editor.NewDecisionTableXLSPopup;
import org.kie.guvnor.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.kie.guvnor.dtablexls.client.resources.images.ImageResources;
import org.kie.guvnor.dtablexls.service.DecisionTableXLSService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewDecisionTableXLSHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DecisionTableXLSService> decisionTableXLSService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DecisionTableXLSResourceType resourceType;

    @Override
    public String getDescription() {
        return DecisionTableXLSEditorConstants.INSTANCE.NewDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.classImage() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName ) {
        NewDecisionTableXLSPopup popup = new NewDecisionTableXLSPopup(contextPath, buildFileName( resourceType, baseFileName ), new Command() {

            @Override
            public void execute() {
                notifySuccess();
                Path newPath = PathFactory.newPath(contextPath.getFileSystem(), buildFileName( resourceType, baseFileName ), contextPath.toURI());
                notifyResourceAdded( newPath );
                final PlaceRequest place = new PathPlaceRequest( newPath,
                                                                 "DecisionTableXLSEditor" );
                placeManager.goTo( place );                
            }
            
        });
        popup.show();
    }

}
