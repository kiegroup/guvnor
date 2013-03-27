package org.kie.guvnor.projecteditor.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.widget.BusyIndicatorView;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.projecteditor.client.places.ProjectEditorPlace;
import org.kie.guvnor.projecteditor.client.resources.ProjectEditorResources;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.ErrorPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;

/**
 * Handler for the creation of new Projects
 */
@ApplicationScoped
public class NewProjectHandler
        implements NewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Caller<ProjectService> projectServiceCaller;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return ProjectEditorConstants.INSTANCE.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newProjectIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String projectName,
                        final NewResourcePresenter presenter ) {
        if ( contextPath != null ) {
            busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
            String url = GWT.getModuleBaseURL();
            String baseURL = url.substring(0, url.indexOf("org.kie.guvnor.GuvnorShowcase"));
            projectServiceCaller.call( getSuccessCallback( presenter ),
                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).newProject( contextPath, projectName, baseURL );
        } else {
            ErrorPopup.showMessage( ProjectEditorConstants.INSTANCE.NoRepositorySelectedPleaseSelectARepository() );
        }
    }

    private RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( Path pathToPom ) {
                busyIndicatorView.hideBusyIndicator();
                presenter.complete();
                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
                placeManager.goTo( new ProjectEditorPlace( pathToPom ) );
            }
        };
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        return null;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> response ) {
        //You can always create a new Project (provided a repository has been selected)
        response.onSuccess( path != null );
    }

}
