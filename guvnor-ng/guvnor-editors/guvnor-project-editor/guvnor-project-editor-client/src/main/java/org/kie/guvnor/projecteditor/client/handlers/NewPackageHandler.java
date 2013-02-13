package org.kie.guvnor.projecteditor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.projecteditor.client.resources.ProjectEditorResources;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.backend.vfs.Path;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewPackageHandler
        extends DefaultNewResourceHandler {

    private static String FILE_TYPE = null;

    @Inject
    private Caller<ProjectService> projectService;

    @Override
    public String getFileType() {
        return FILE_TYPE;
    }

    @Override
    public String getDescription() {
        return ProjectEditorConstants.INSTANCE.newPackageDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public void create( final String fileName ) {
        final Path path = buildFullPathName( fileName );

        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                notifySuccess();
                notifyResourceAdded( path );
            }
        } ).newPackage( path );
    }

}
