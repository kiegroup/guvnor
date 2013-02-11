package org.kie.guvnor.commons.ui.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

/**
 * Handler for the creation of new Items that require a Name and Path
 */
public abstract class DefaultNewResourceHandler implements NewResourceHandler {

    private final List<Pair<String, IsWidget>> extensions = new LinkedList<Pair<String, IsWidget>>();

    protected final PathLabel pathLabel = new PathLabel();

    @Inject
    protected WorkbenchContext context;

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    public DefaultNewResourceHandler() {
        final Pair extension = new Pair( CommonConstants.INSTANCE.ItemPathSubheading(),
                                         pathLabel );
        this.extensions.add( extension );
    }

    @Override
    public List<Pair<String, IsWidget>> getExtensions() {
        this.pathLabel.setPath( context.getActivePath() );
        return this.extensions;
    }

    @Override
    public boolean validate() {
        boolean isValid = true;
        if ( pathLabel.getPath() == null ) {
            Window.alert( CommonConstants.INSTANCE.MissingPath() );
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> callback ) {
        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                callback.onSuccess( path != null );
            }
        } ).resolvePackage( path );
    }

    protected Path buildFullPathName( final String fileName ) {
        final String pathName = this.pathLabel.getPath().toURI();
        final Path assetPath = PathFactory.newPath( pathLabel.getPath().getFileSystem(), fileName, pathName + "/" + fileName );
        return assetPath;
    }

    protected String stripFileExtension( final String fileName ) {
        final int dotIndex = fileName.indexOf( "." );
        if ( dotIndex == -1 ) {
            return fileName;
        }
        return fileName.substring( 0,
                                   dotIndex );
    }

    protected void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

}