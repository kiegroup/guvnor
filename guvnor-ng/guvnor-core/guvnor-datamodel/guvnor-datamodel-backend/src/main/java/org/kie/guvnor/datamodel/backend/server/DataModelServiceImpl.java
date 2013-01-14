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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.rule.TypeMetaInfo;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.builder.Builder;
import org.kie.guvnor.commons.service.builder.model.Message;
import org.kie.guvnor.commons.service.builder.model.Results;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.datamodel.service.FileDiscoveryService;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    @Inject
    private DataModelOracleCache cache;

    @Inject
    private ProjectService projectService;

    @Inject
    private Paths paths;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private Event<Results> messagesEvent;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public String[] getFactTypes( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        final Path projectPath = resolveProjectPath( resourcePath );
        final Path packagePath = resolvePackagePath( resourcePath );

        //Resource was not within a Project structure
        if ( projectPath == null ) {
            return new String[ 0 ];
        }

        assertDataModelOracle( projectPath,
                               packagePath );
        return cache.getDataModelOracle( packagePath ).getFactTypes();
    }

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

        assertDataModelOracle( projectPath,
                               packagePath );
        return cache.getDataModelOracle( packagePath );
    }

    //Check the DataModelOracle for the Project has been created, otherwise create one!
    private void assertDataModelOracle( final Path projectPath,
                                        final Path packagePath ) {
        DataModelOracle oracle = cache.getDataModelOracle( packagePath );
        if ( oracle == null ) {
            oracle = makeDataModelOracle( projectPath,
                                          packagePath );
            cache.setDataModelOracle( packagePath,
                                      oracle );
        }
    }

    private Path resolveProjectPath( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private Path resolvePackagePath( final Path resourcePath ) {
        return projectService.resolvePackage( resourcePath );
    }

    private DataModelOracle makeEmptyDataModelOracle() {
        return DataModelBuilder.newDataModelBuilder().build();
    }

    private DataModelOracle makeDataModelOracle( final Path projectPath,
                                                 final Path packagePath ) {
        //Build the project to get all available classes
        final Path pomPath = paths.convert( paths.convert( projectPath ).resolve( "pom.xml" ) );
        final GroupArtifactVersionModel gav = projectService.loadGav( pomPath );
        final Builder builder = new Builder( paths.convert( projectPath ),
                                             gav.getArtifactId(),
                                             paths,
                                             sourceServices,
                                             new ModelBuilderFilter() );

        //If the Project had errors report them to the user and return an empty DataModelOracle
        final Results results = builder.build();
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyDataModelOracle();
        }

        //Otherwise create a DataModelOracle...
        final KieModuleMetaData metaData = KieModuleMetaData.Factory.newKieModuleMetaData( builder.getKieModule() );
        final DataModelBuilder dmoBuilder = DataModelBuilder.newDataModelBuilder();

        //Add all classes from the KieModule metaData
        for ( final String packageName : metaData.getPackages() ) {
            for ( final String className : metaData.getClasses( packageName ) ) {
                final Class clazz = metaData.getClass( packageName,
                                                       className );
                final TypeMetaInfo typeMetaInfo = metaData.getTypeMetaInfo( clazz );
                try {
                    dmoBuilder.addClass( clazz,
                                         typeMetaInfo.isEvent() );
                } catch ( IOException ioe ) {
                    results.getMessages().add( makeMessage( ioe ) );
                }
            }
        }

        //Add Guvnor enumerations
        loadEnumsForPackage( dmoBuilder,
                             packagePath );

        //Add DSLs
        loadDslsForPackage( dmoBuilder,
                            packagePath );

        //TODO {manstis} - Add Globals

        //If there were errors constructing the DataModelOracle advise the user and return an empty DataModelOracle
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyDataModelOracle();
        }

        return dmoBuilder.build();
    }

    private Message makeMessage( final Exception e ) {
        final Message message = new Message();
        message.setLevel( Message.Level.ERROR );
        message.setText( e.getMessage() );
        return message;
    }

    private void loadEnumsForPackage( final DataModelBuilder dmoBuilder,
                                      final Path packagePath ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final Collection<org.kie.commons.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                             ".enumeration" );
        for ( final org.kie.commons.java.nio.file.Path path : enumFiles ) {
            final String enumDefinition = ioService.readAllString( path );
            dmoBuilder.addEnum( enumDefinition );
        }
    }

    private void loadDslsForPackage( final DataModelBuilder dmoBuilder,
                                     final Path packagePath ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final Collection<org.kie.commons.java.nio.file.Path> dslFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                            ".dsl" );
        for ( final org.kie.commons.java.nio.file.Path path : dslFiles ) {
            final String dslDefinition = ioService.readAllString( path );
            dmoBuilder.addDsl( dslDefinition );
        }
    }

}
