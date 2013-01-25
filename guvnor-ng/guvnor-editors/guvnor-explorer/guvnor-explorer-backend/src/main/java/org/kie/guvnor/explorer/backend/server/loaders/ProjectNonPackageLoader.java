package org.kie.guvnor.explorer.backend.server.loaders;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.guvnor.explorer.model.Item;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects, Folders and Files for a Project path that is not within a Package
 */
@Dependent
@Named("projectNonPackageList")
public class ProjectNonPackageLoader implements ItemsLoader {

    @Inject
    private ProjectService projectService;

    @Inject
    @Named("projectRootList")
    private ItemsLoader projectRootListLoader;

    @Override
    public List<Item> load( final Path path ) {
        // A Path that is within a Project but not a Package can be selected from File
        // Explorer. Simply return the Project's content for use in Project Explorer.
        final Path projectPath = projectService.resolveProject( path );
        return projectRootListLoader.load( projectPath );
    }

}
