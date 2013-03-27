package org.kie.guvnor.datamodel.backend.server.cache;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.rule.TypeMetaInfo;
import org.drools.guvnor.models.commons.shared.imports.Import;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.builder.Builder;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.builder.model.BuildMessage;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.datamodel.backend.server.ModelFilter;
import org.kie.guvnor.datamodel.backend.server.builder.projects.ProjectDefinitionBuilder;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.cache.LRUCache;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<Path, ProjectDefinition> {

    private static final String ERROR_CLASS_NOT_FOUND = "Class not found";

    private static final String ERROR_IO = "IO Error";

    @Inject
    private Paths paths;

    @Inject
    private POMService pomService;

    @Inject
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Path projectPath = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( projectPath != null ) {
            invalidateCache( projectPath );
        }
    }

    //Check the ProjectDefinition for the Project has been created, otherwise create one!
    public synchronized ProjectDefinition assertProjectDataModelOracle( final Path projectPath ) throws BuildException {
        ProjectDefinition projectDefinition = getEntry( projectPath );
        if ( projectDefinition == null ) {
            projectDefinition = makeProjectDefinition( projectPath );
            setEntry( projectPath,
                      projectDefinition );
        }
        return projectDefinition;
    }

    private ProjectDefinition makeProjectDefinition( final Path projectPath ) throws BuildException {
        //Build the Project to get all available classes
        final Path pathToPom = paths.convert( paths.convert( projectPath ).resolve( "pom.xml" ) );
        final POM gav = pomService.load( pathToPom );

        //If we need a Project DMO chances are we're editing an asset. Therefore perform a full build to get
        //the validation errors for the project. This could be moved to ProjectExplorer when opening a Project
        buildService.build( pathToPom );

        //Cannot re-use Builder cache as the Builders in the cache are configured to perform a full build
        //of all assets. If any asset is invalid the underlying KieBuilder will not produce a package and
        //it is therefore impossible to retrieve model details if, for example, a rule is invalid.
        final Builder builder = new Builder( paths.convert( pathToPom ).getParent(),
                                             gav.getGav().getArtifactId(),
                                             paths,
                                             ioService,
                                             projectService,
                                             new ModelFilter() );

        //If the Project had errors report them to the user and return an empty ProjectDefinition
        final BuildResults results = builder.build();
        if ( !results.getMessages().isEmpty() ) {
            throw new BuildException( results );
        }

        //Otherwise create the ProjectDefinition...
        final KieModuleMetaData metaData = KieModuleMetaData.Factory.newKieModuleMetaData( builder.getKieModule() );
        final ProjectDefinitionBuilder pdBuilder = ProjectDefinitionBuilder.newProjectDefinitionBuilder();

        //Add all classes from the KieModule metaData
        for ( final String packageName : metaData.getPackages() ) {
            for ( final String className : metaData.getClasses( packageName ) ) {
                final Class clazz = metaData.getClass( packageName,
                                                       className );
                final TypeMetaInfo typeMetaInfo = metaData.getTypeMetaInfo( clazz );
                try {
                    pdBuilder.addClass( clazz,
                                        typeMetaInfo.isEvent() );
                } catch ( IOException ioe ) {
                    results.addBuildMessage( makeMessage( ERROR_IO,
                                                          ioe ) );
                }
            }
        }

        //Add external imports. The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = paths.convert( projectPath ).resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final PackageConfiguration packageConfiguration = projectService.load( externalImportsPath );
            final Imports imports = packageConfiguration.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                    pdBuilder.addClass( clazz );
                } catch ( ClassNotFoundException cnfe ) {
                    //This should not happen as Builder would have failed to load them and failed fast.
                    results.addBuildMessage( makeMessage( ERROR_CLASS_NOT_FOUND,
                                                          cnfe ) );
                } catch ( IOException ioe ) {
                    results.addBuildMessage( makeMessage( ERROR_IO,
                                                          ioe ) );
                }
            }
        }

        //If there were errors constructing the DataModelOracle advise the user and return an empty DataModelOracle
        if ( !results.getMessages().isEmpty() ) {
            throw new BuildException( results );
        }

        return pdBuilder.build();
    }

    private BuildMessage makeMessage( final String prefix,
                                      final Exception e ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( prefix + ": " + e.getMessage() );
        return buildMessage;
    }

}
