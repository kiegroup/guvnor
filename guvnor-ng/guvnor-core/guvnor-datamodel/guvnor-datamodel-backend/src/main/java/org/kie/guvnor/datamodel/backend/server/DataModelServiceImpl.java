/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.datamodel.backend.server;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.models.commons.shared.imports.Import;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.drools.rule.TypeMetaInfo;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.builder.Builder;
import org.kie.guvnor.commons.service.builder.model.BuildMessage;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.guvnor.datamodel.backend.server.builder.projects.ProjectDefinitionBuilder;
import org.kie.guvnor.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.guvnor.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.backend.file.FileExtensionFilter;
import org.kie.guvnor.services.file.FileDiscoveryService;
import org.kie.guvnor.services.file.Filter;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    private static final Filter FILTER_ENUMERATIONS = new FileExtensionFilter( ".enumeration" );

    private static final Filter FILTER_DSLS = new FileExtensionFilter( ".dsl" );

    private static final Filter FILTER_GLOBALS = new FileExtensionFilter( ".global.drl" );

    @Inject
    @Named("PackageDataModelOracleCache")
    private LRUDataModelOracleCache cachePackages;

    @Inject
    @Named("ProjectDataModelOracleCache")
    private LRUProjectDataModelOracleCache cacheProjects;

    @Inject
    private ProjectService projectService;

    @Inject
    private POMService pomService;

    @Inject
    private Paths paths;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private Event<BuildResults> messagesEvent;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public DataModelOracle getDataModel( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        final Path projectPath = resolveProjectPath( resourcePath );
        final Path packagePath = resolvePackagePath( resourcePath );

        //Resource was not within a Project structure
        if ( projectPath == null ) {
            return makeEmptyDataModelOracle();
        }

        assertProjectDataModelOracle( projectPath );
        assertPackageDataModelOracle( projectPath,
                                      packagePath );

        final DataModelOracle oracle = cachePackages.getEntry( packagePath );
        return oracle;
    }

    //Check the ProjectDefinition for the Project has been created, otherwise create one!
    private void assertProjectDataModelOracle( final Path projectPath ) {
        ProjectDefinition projectDefinition = cacheProjects.getEntry( projectPath );
        if ( projectDefinition == null ) {
            projectDefinition = makeProjectDefinition( projectPath );
            cacheProjects.setEntry( projectPath,
                                    projectDefinition );
        }
    }

    //Check the DataModelOracle for the Package has been created, otherwise create one!
    private void assertPackageDataModelOracle( final Path projectPath,
                                               final Path packagePath ) {
        DataModelOracle oracle = cachePackages.getEntry( packagePath );
        if ( oracle == null ) {
            oracle = makePackageDataModelOracle( projectPath,
                                                 packagePath );
            cachePackages.setEntry( packagePath,
                                    oracle );
        }
    }

    private Path resolveProjectPath( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private Path resolvePackagePath( final Path resourcePath ) {
        return projectService.resolvePackage( resourcePath );
    }

    private String resolvePackageName( final Path packagePath ) {
        return projectService.resolvePackageName( packagePath );
    }

    private DataModelOracle makeEmptyDataModelOracle() {
        return new PackageDataModelOracle();
    }

    private ProjectDefinition makeEmptyProjectDefinition() {
        return new ProjectDefinition();
    }

    private ProjectDefinition makeProjectDefinition( final Path projectPath ) {
        //Build the Project to get all available classes
        final Path pomPath = paths.convert( paths.convert( projectPath ).resolve( "pom.xml" ) );
        final POM gav = pomService.loadPOM( pomPath );
        final Builder builder = new Builder( paths.convert( projectPath ),
                                             gav.getGav().getArtifactId(),
                                             paths,
                                             sourceServices,
                                             ioService,
                                             new ModelFilter() );

        //If the Project had errors report them to the user and return an empty ProjectDefinition
        final BuildResults results = builder.build();
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyProjectDefinition();
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
                    results.getBuildMessages().add( makeMessage( ioe ) );
                }
            }
        }

        //Add external imports
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = paths.convert( projectPath ).resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final PackageConfiguration packageConfiguration = projectService.loadPackageConfiguration( externalImportsPath );
            final Imports imports = packageConfiguration.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                    pdBuilder.addClass( clazz );
                } catch ( ClassNotFoundException cnfe ) {
                    results.getBuildMessages().add( makeMessage( cnfe ) );
                } catch ( IOException ioe ) {
                    results.getBuildMessages().add( makeMessage( ioe ) );
                }
            }
        }

        //If there were errors constructing the DataModelOracle advise the user and return an empty DataModelOracle
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyProjectDefinition();
        }

        return pdBuilder.build();
    }

    private DataModelOracle makePackageDataModelOracle( final Path projectPath,
                                                        final Path packagePath ) {
        final String packageName = projectService.resolvePackageName( packagePath );
        final PackageDataModelOracleBuilder dmoBuilder = PackageDataModelOracleBuilder.newDataModelBuilder( packageName );
        final ProjectDefinition projectDefinition = cacheProjects.getEntry( projectPath );
        dmoBuilder.setProjectDefinition( projectDefinition );

        //Add Guvnor enumerations
        loadEnumsForPackage( dmoBuilder,
                             packagePath );

        //Add DSLs
        loadDslsForPackage( dmoBuilder,
                            packagePath );

        //Add Globals
        loadGlobalsForPackage( dmoBuilder,
                               packagePath );

        //Report any errors
        final BuildResults results = new BuildResults();
        final List<String> errors = dmoBuilder.getErrors();
        for ( final String error : errors ) {
            results.getBuildMessages().add( makeMessage( error ) );
        }
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyDataModelOracle();
        }

        return dmoBuilder.build();
    }

    private BuildMessage makeMessage( final Exception e ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( e.getMessage() );
        return buildMessage;
    }

    private BuildMessage makeMessage( final String msg ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( msg );
        return buildMessage;
    }

    private void loadEnumsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                      final Path packagePath ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final Collection<org.kie.commons.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                             FILTER_ENUMERATIONS );
        for ( final org.kie.commons.java.nio.file.Path path : enumFiles ) {
            final String enumDefinition = ioService.readAllString( path );
            dmoBuilder.addEnum( enumDefinition );
        }
    }

    private void loadDslsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                     final Path packagePath ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final Collection<org.kie.commons.java.nio.file.Path> dslFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                            FILTER_DSLS );
        for ( final org.kie.commons.java.nio.file.Path path : dslFiles ) {
            final String dslDefinition = ioService.readAllString( path );
            dmoBuilder.addDsl( dslDefinition );
        }
    }

    private void loadGlobalsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                        final Path packagePath ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final Collection<org.kie.commons.java.nio.file.Path> globalFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                               FILTER_GLOBALS );
        for ( final org.kie.commons.java.nio.file.Path path : globalFiles ) {
            final String definition = ioService.readAllString( path );
            dmoBuilder.addGlobals( definition );
        }
    }

}
