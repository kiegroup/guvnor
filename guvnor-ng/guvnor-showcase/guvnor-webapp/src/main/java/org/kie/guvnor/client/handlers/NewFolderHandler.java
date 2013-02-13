package org.kie.guvnor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.client.resources.i18n.Constants;
import org.kie.guvnor.client.resources.images.ImageResources;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewFolderHandler extends DefaultNewResourceHandler {

    private static String FILE_TYPE = null;

    @Inject
    private Caller<VFSService> vfs;

    @Inject
    private Caller<ProjectService> projectService;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newFolderDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );
        vfs.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                notifySuccess();
                notifyResourceAdded( path );
            }
        } ).createDirectory( path );
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> callback ) {
        //You can only create new Folders outside of packages
        if ( path == null ) {
            callback.onSuccess( false );
        }
        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                callback.onSuccess( path == null );
            }
        } ).resolvePackage( path );
    }

}
