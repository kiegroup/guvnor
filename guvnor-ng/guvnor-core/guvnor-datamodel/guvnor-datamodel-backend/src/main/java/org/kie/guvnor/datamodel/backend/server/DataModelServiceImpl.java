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
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.rule.TypeMetaInfo;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.builder.Builder;
import org.kie.guvnor.commons.service.builder.model.Message;
import org.kie.guvnor.commons.service.builder.model.Results;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.oracle.DataType;
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

    @Override
    public String[] getFactTypes( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        final Path projectPath = resolveProjectPath( resourcePath );

        //Resource was not within a Project structure
        if ( projectPath == null ) {
            return new String[ 0 ];
        }

        assertDataModelOracle( projectPath );
        return cache.getDataModelOracle( projectPath ).getFactTypes();
    }

    @Override
    public DataModelOracle getDataModel( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        final Path projectPath = resolveProjectPath( resourcePath );

        //Resource was not within a Project structure
        if ( projectPath == null ) {
            return makeEmptyDataModelOracle();
        }

        assertDataModelOracle( projectPath );
        return cache.getDataModelOracle( projectPath );
    }

    //Check the DataModelOracle for the Project has been created, otherwise create one!
    private void assertDataModelOracle( final Path projectPath ) {
        DataModelOracle oracle = cache.getDataModelOracle( projectPath );
        if ( oracle == null ) {
            oracle = makeDataModelOracle( projectPath );
            cache.setDataModelOracle( projectPath,
                                      oracle );
        }
    }

    private Path resolveProjectPath( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private DataModelOracle makeEmptyDataModelOracle() {
        return DataModelBuilder.newDataModelBuilder().build();
    }

    private DataModelOracle makeDataModelOracle( final Path projectPath ) {
        //Build the project to get all available classes
        final Path pomPath = paths.convert( paths.convert( projectPath ).resolve( "pom.xml" ) );
        final GroupArtifactVersionModel gav = projectService.loadGav( pomPath );
        final Builder builder = new Builder( paths.convert( projectPath ),
                                             gav.getArtifactId(),
                                             paths,
                                             sourceServices );

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

        //TODO {manstis} - Add Guvnor enumerations
        final org.kie.commons.java.nio.file.Path nioProjectPath = paths.convert( projectPath );
        final Collection<org.kie.commons.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles( nioProjectPath,
                                                                                                             ".enumeration" );

        //TODO {manstis} - Add DSLs
        final Collection<org.kie.commons.java.nio.file.Path> dslFiles = fileDiscoveryService.discoverFiles( nioProjectPath,
                                                                                                            ".dsl" );
        //TODO {manstis} - Add Globals

        //If there were errors constructing the DataModelOracle advise the user and return an empty DataModelOracle
        if ( !results.isEmpty() ) {
            messagesEvent.fire( results );
            return makeEmptyDataModelOracle();
        }

        //Mock DMO for now... until the above is complete
        dmoBuilder.addFact( "Driver" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "date",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .addField( new ModelField( "approved",
                                           Boolean.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_BOOLEAN ) )
                .end()
                .addFact( "Incident",
                          true )
                .addField( new ModelField( "dateOfIncident",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .addField( new ModelField( "typeOfIncident",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "claimAmount",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .end();
        return dmoBuilder.build();
    }

    private Message makeMessage( final Exception e ) {
        final Message message = new Message();
        message.setLevel( Message.Level.ERROR );
        message.setText( e.getMessage() );
        return message;
    }

}
