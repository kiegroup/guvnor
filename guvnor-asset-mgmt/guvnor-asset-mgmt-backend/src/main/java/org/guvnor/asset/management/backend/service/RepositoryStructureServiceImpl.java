/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.ValidationUtils;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;

import static org.guvnor.structure.repositories.EnvironmentParameters.*;

@Service
@ApplicationScoped
public class RepositoryStructureServiceImpl
        implements RepositoryStructureService {

    private static final Logger logger = LoggerFactory.getLogger( RepositoryStructureServiceImpl.class );

    private IOService                                ioService;
    private POMService                               pomService;
    private ProjectService<? extends Project>        projectService;
    private RepositoryService                        repositoryService;
    private MetadataService                          metadataService;
    private GuvnorM2Repository                       m2service;
    private CommentedOptionFactory                   optionsFactory;
    private Event<RepositoryEnvironmentUpdatedEvent> repositoryUpdatedEvent;
    private ProjectRepositoryResolver                repositoryResolver;

    public RepositoryStructureServiceImpl() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public RepositoryStructureServiceImpl( final @Named( "ioStrategy" ) IOService ioService,
                                           final POMService pomService,
                                           final ProjectService<? extends Project> projectService,
                                           final RepositoryService repositoryService,
                                           final MetadataService metadataService,
                                           final GuvnorM2Repository m2service,
                                           final CommentedOptionFactory optionsFactory,
                                           final Event<RepositoryEnvironmentUpdatedEvent> repositoryUpdatedEvent,
                                           final ProjectRepositoryResolver repositoryResolver ) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.metadataService = metadataService;
        this.m2service = m2service;
        this.optionsFactory = optionsFactory;
        this.repositoryUpdatedEvent = repositoryUpdatedEvent;
        this.repositoryResolver = repositoryResolver;
    }

    @Override
    public Path initRepositoryStructure( final GAV gav,
                                         final Repository repo,
                                         final DeploymentMode mode ) {
        POM pom = new POM( repo.getAlias(),
                           repo.getAlias(),
                           gav,
                           true );

        if ( DeploymentMode.VALIDATED.equals( mode ) ) {
            checkRepositories( pom );
        }

        //Creating the parent pom
        final Path fsRoot = repo.getRoot();
        final Path pathToPom = pomService.create( fsRoot,
                                                  "",
                                                  pom );
        //Deploying the parent pom artifact,
        // it needs to be deployed before the first child is created
        m2service.deployParentPom( gav );

        updateManagedStatus( repo,
                             true );

        return pathToPom;
    }

    @Override
    public Path initRepositoryStructure( final POM pom,
                                         final String baseUrl,
                                         final Repository repository,
                                         final boolean multiProject,
                                         final DeploymentMode mode ) {
        if ( pom == null || baseUrl == null || repository == null ) {
            return null;
        }

        if ( DeploymentMode.VALIDATED.equals( mode ) ) {
            checkRepositories( pom );
        }

        if ( multiProject ) {

            pom.setPackaging( "pom" );

            //Creating the parent pom
            final Path fsRoot = repository.getRoot();
            final Path pathToPom = pomService.create( fsRoot,
                                                      "",
                                                      pom );
            //Deploying the parent pom artifact,
            // it needs to be deployed before the first child is created
            m2service.deployParentPom( pom.getGav() );

            updateManagedStatus( repository,
                                 true );

            return pathToPom;

        } else {
            Project project = projectService.newProject( repository.getBranchRoot( repository.getDefaultBranch() ),
                                                         pom,
                                                         baseUrl,
                                                         mode );
            return project.getPomXMLPath();
        }
    }

    private void checkRepositories( final POM pom ) {
        // Check is the POM's GAV resolves to any pre-existing artifacts. We don't need to filter
        // resolved Repositories by those enabled for the Project since this is a new Project.
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact( pom.getGav() );
        if ( repositories.size() > 0 ) {
            throw new GAVAlreadyExistsException( pom.getGav(),
                                                 repositories );
        }
    }

    @Override
    public Repository initRepository( final Repository repo,
                                      final boolean managed ) {
        return updateManagedStatus( repo,
                                    managed );
    }

    private Repository updateManagedStatus( final Repository repo,
                                            final boolean managed ) {
        final RepositoryEnvironmentConfigurations config = new RepositoryEnvironmentConfigurations();

        config.setManaged( managed );
        Repository updatedRepo = repositoryService.updateRepositoryConfiguration( repo,
                                                                                  config );
        repositoryUpdatedEvent.fire( new RepositoryEnvironmentUpdatedEvent( updatedRepo ) );

        return updatedRepo;
    }

    @Override
    public Path convertToMultiProjectStructure( final List<Project> projects,
                                                final GAV parentGav,
                                                final Repository repo,
                                                final boolean updateChildrenGav,
                                                final String comment ) {

        if ( projects == null || parentGav == null || repo == null ) {
            return null;
        }

        try {
            POM parentPom;
            Path path = initRepositoryStructure( parentGav,
                                                 repo,
                                                 DeploymentMode.FORCED );

            parentPom = pomService.load( path );
            if ( parentPom == null ) {
                //uncommon case, the pom was just created.
                return null;
            }

            ioService.startBatch( new FileSystem[]{ Paths.convert( path ).getFileSystem() },
                                  optionsFactory.makeCommentedOption( comment != null ? comment : "" ) );

            POM pom;
            boolean saveParentPom = false;
            for ( Project project : projects ) {
                pom = pomService.load( project.getPomXMLPath() );
                pom.setParent( parentGav );
                if ( updateChildrenGav ) {
                    pom.getGav().setGroupId( parentGav.getGroupId() );
                    pom.getGav().setVersion( parentGav.getVersion() );
                }
                pomService.save( project.getPomXMLPath(),
                                 pom,
                                 null,
                                 comment );

                parentPom.setPackaging( "pom" );
                parentPom.getModules().add( pom.getName() != null ? pom.getName() : pom.getGav().getArtifactId() );
                saveParentPom = true;
            }

            if ( saveParentPom ) {
                pomService.save( path,
                                 parentPom,
                                 null,
                                 comment );
            }

            return path;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public RepositoryStructureModel load( final Repository repository ) {
        return load( repository, true );
    }

    @Override
    public RepositoryStructureModel load( final Repository repository,
                                          final boolean includeModules ) {
        if ( repository == null ) {
            return null;
        }
        Repository _repository = repositoryService.getRepository( repository.getAlias() );

        if ( _repository == null ) {
            return null;
        }

        RepositoryStructureModel model = new RepositoryStructureModel();
        Boolean managedStatus = _repository.getEnvironment() != null ? (Boolean) _repository.getEnvironment().get( MANAGED ) : null;
        if ( managedStatus != null ) {
            model.setManaged( managedStatus );
        }

        Path path = repository.getRoot();
        final Project project = projectService.resolveToParentProject( path );

        if ( project != null ) {
            if ( !model.isManaged() ) {
                //uncommon case, the repository is managed. Update managed status.
                updateManagedStatus( _repository,
                                     true );
                model.setManaged( true );
            }
            model.setPOM( pomService.load( project.getPomXMLPath() ) );
            model.setPOMMetaData( metadataService.getMetadata( project.getPomXMLPath() ) );
            model.setPathToPOM( project.getPomXMLPath() );
            model.setModules( new ArrayList<String>( project.getModules() ) );
            if ( includeModules && project.getModules() != null ) {
                org.uberfire.java.nio.file.Path parentPath = Paths.convert( project.getRootPath() );
                Project moduleProject;
                for ( String module : project.getModules() ) {
                    moduleProject = projectService.resolveProject( Paths.convert( parentPath.resolve( module ) ) );
                    model.getModulesProject().put( module,
                                                   moduleProject );
                }
            }

        } else {
            //if no parent pom.xml present we must check if there are orphan projects for this repository.
            List<Project> repositoryProjects = getProjects( repository );
            if ( !repositoryProjects.isEmpty() ) {
                model.setOrphanProjects( repositoryProjects );
                POM pom;
                for ( Project orphanProject : repositoryProjects ) {
                    pom = pomService.load( orphanProject.getPomXMLPath() );
                    model.getOrphanProjectsPOM().put( orphanProject.getSignatureId(),
                                                      pom );
                }
                if ( managedStatus == null && repositoryProjects.size() > 1 ) {
                    //update managed status
                    updateManagedStatus( _repository,
                                         false );
                }

            } else if ( managedStatus == null ) {
                //there are no projects and the managed attribute is not set, means the repository was never initialized.
                model = null;
            }
        }

        return model;
    }

    @Override
    @SuppressWarnings("unused")
    public void save( final Path pathToPomXML,
                      final RepositoryStructureModel model,
                      final String comment ) {
        final FileSystem fs = Paths.convert( pathToPomXML ).getFileSystem();
        try {
            pomService.save( pathToPomXML,
                             model.getPOM(),
                             model.getPOMMetaData(),
                             comment,
                             true );

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public boolean isValidProjectName( final String name ) {
        return ValidationUtils.isFileName( name );
    }

    @Override
    public boolean isValidGroupId( final String groupId ) {
        if ( groupId == null || "".equals( groupId.trim() ) ) {
            return false;
        }
        final String[] groupIdComponents = groupId.split( "\\.", -1 );
        for ( String s : groupIdComponents ) {
            if ( !ValidationUtils.isArtifactIdentifier( s ) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidArtifactId( final String artifactId ) {
        if ( artifactId == null || "".equals( artifactId.trim() ) ) {
            return false;
        }
        final String[] artifactIdComponents = artifactId.split( "\\.", -1 );
        for ( String s : artifactIdComponents ) {
            if ( !ValidationUtils.isArtifactIdentifier( s ) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidVersion( final String version ) {
        if ( version == null || "".equals( version.trim() ) ) {
            return false;
        }
        return version.matches( "^[a-zA-Z0-9\\.\\-_]+$" );
    }

    @Override
    public void delete( final Path pathToPomXML,
                        final String comment ) {
        projectService.delete( pathToPomXML,
                               comment );
    }

    private List<Project> getProjects( final Repository repository ) {
        final List<Project> repositoryProjects = new ArrayList<Project>();
        if ( repository == null ) {
            return repositoryProjects;
        }
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( Paths.convert( repositoryRoot ) );
        for ( org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = Paths.convert( nioRepositoryPath );
                final Project project = projectService.resolveProject( projectPath );
                if ( project != null ) {
                    repositoryProjects.add( project );
                }
            }
        }
        return repositoryProjects;
    }

}
