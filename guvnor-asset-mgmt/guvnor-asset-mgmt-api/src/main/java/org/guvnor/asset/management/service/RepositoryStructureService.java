package org.guvnor.asset.management.service;

import java.util.List;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RepositoryStructureService {

    Path initRepositoryStructure( final GAV gav, final Repository repo);

    Path initRepositoryStructure( POM pom, String baseUrl, Repository repo, boolean multiProject );

    Repository initRepository( final Repository repo, boolean managed );

    Path convertToMultiProjectStructure( final List<Project> projects,
            final GAV parentGav,
            final Repository repo,
            final boolean updateChildrenGav,
            final String comment );

    RepositoryStructureModel load( final Repository repository );


    RepositoryStructureModel load( final Repository repository, boolean includeModules );

    void save( final Path pathToPomXML,
            final RepositoryStructureModel model,
            final String comment );


    boolean validate( final POM pom );

    void delete( final Path pathToPomXML, final String comment );

}
