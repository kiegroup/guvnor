package org.guvnor.asset.management.service;

import java.util.List;

import org.guvnor.asset.management.model.ProjectStructureModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface ProjectStructureService {

    Path initProjectStructure( final GAV gav, final Repository repo);

    Path convertToMultiProjectStructure( final List<Project> projects,
            final GAV parentGav,
            final Repository repo,
            final boolean updateChildrenGav,
            final String comment );

    ProjectStructureModel load( final Repository repository );


    ProjectStructureModel load( final Repository repository, boolean includeModules );

    void save( final Path pathToPomXML,
            final ProjectStructureModel model,
            final String comment );


    boolean validate( final POM pom );

    void delete( final Path pathToPomXML, final String comment );

}
