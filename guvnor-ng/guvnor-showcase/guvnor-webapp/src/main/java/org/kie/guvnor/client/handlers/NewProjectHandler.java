package org.kie.guvnor.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.client.resources.i18n.Constants;
import org.kie.guvnor.client.resources.images.ImageResources;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.projecteditor.client.places.ProjectEditorPlace;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

/**
 * Handler for the creation of new Projects
 */
@ApplicationScoped
public class NewProjectHandler implements NewResourceHandler {

    private static String FILE_TYPE = null;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private Caller<ProjectEditorService> projectEditorServiceCaller;

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
    public void create( final String fileName ) {
        projectEditorServiceCaller.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path pathToPom ) {

                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
                placeManager.goTo( new ProjectEditorPlace( pathToPom ) );
            }
        } ).newProject( fileName );
    }

    @Override
    public List<Pair<String, IsWidget>> getExtensions() {
        return null;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean requiresProjectPath() {
        return false;
    }

}
