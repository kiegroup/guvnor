package org.kie.guvnor.commons.ui.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.commons.data.Pair;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
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

    protected Path buildFullPathName( final String fileName ) {
        final String pathName = this.pathLabel.getPath().toURI();
        final Path assetPath = PathFactory.newPath( fileName,
                                                    pathName + "/" + fileName );
        return assetPath;
    }

    protected void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
    }

}