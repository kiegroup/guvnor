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

package org.guvnor.common.services.builder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.model.TypeSource;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.builder.BuildMessage;
import org.guvnor.common.services.shared.builder.BuildResults;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.workbench.events.ChangeType;
import org.uberfire.workbench.events.ResourceChange;

public class Builder {

    private final static String RESOURCE_PATH = "src/main/resources";

    private final static String ERROR_CLASS_NOT_FOUND = "Class not found";

    private KieBuilder kieBuilder;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;
    private final Path moduleDirectory;
    private final Paths paths;
    private final String artifactId;
    private final IOService ioService;
    private final ProjectService projectService;
    private final List<ValidationService> validators;

    private final String projectPrefix;

    private Map<String, org.uberfire.backend.vfs.Path> handles = new HashMap<String, org.uberfire.backend.vfs.Path>();

    private final Map<Path, ValidationService> nonKieResourceValidators = new HashMap<Path, ValidationService>();
    private final Map<Path, List<BuildMessage>> nonKieResourceValidatorMessages = new HashMap<Path, List<BuildMessage>>();

    private final DirectoryStream.Filter<Path> javaResourceFilter = new JavaFileFilter();
    private Set<String> javaResources = new HashSet<String>();

    private KieContainer kieContainer;

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final IOService ioService,
                    final ProjectService projectService,
                    final List<ValidationService> validators ) {
        this.moduleDirectory = moduleDirectory;
        this.artifactId = artifactId;
        this.paths = paths;
        this.ioService = ioService;
        this.projectService = projectService;
        this.validators = validators;

        projectPrefix = moduleDirectory.toUri().toString();
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream( moduleDirectory );
        visitPaths( directoryStream );
    }

    public BuildResults build() {
        //KieBuilder is not re-usable for successive "full" builds
        kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final Results kieResults = kieBuilder.buildAll().getResults();
        final BuildResults results = convertMessages( kieResults );

        //Validate paths that are not handled by Kie
        for ( Map.Entry<Path, ValidationService> e : nonKieResourceValidators.entrySet() ) {
            final Path resource = e.getKey();
            final ValidationService validator = e.getValue();
            final List<BuildMessage> messages = validator.validate( paths.convert( resource ) );
            if ( messages != null ) {
                for ( BuildMessage message : messages ) {
                    results.addBuildMessage( message );
                }
                nonKieResourceValidatorMessages.put( resource,
                                                     messages );
            }
        }

        if ( !results.getMessages().isEmpty() ) {
            return results;
        }

        kieContainer = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = moduleDirectory.resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final org.uberfire.backend.vfs.Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final ProjectImports projectImports = projectService.load( externalImportsPath );
            final Imports imports = projectImports.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                } catch ( ClassNotFoundException cnfe ) {
                    results.addBuildMessage( makeMessage( ERROR_CLASS_NOT_FOUND,
                                                          cnfe ) );
                }
            }
        }

        return results;
    }

    public IncrementalBuildResults addResource( final Path resource ) {
        PortablePreconditions.checkNotNull( "resource",
                                            resource );

        //Only files can be processed
        if ( !Files.isRegularFile( resource ) ) {
            return new IncrementalBuildResults();
        }

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }
        //Add new resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        final InputStream is = ioService.newInputStream( resource );
        final BufferedInputStream bis = new BufferedInputStream( is );
        IncrementalBuildResults results = new IncrementalBuildResults();

        if ( isResourceTypeSupportedByKie( resource ) ) {
            //Resource Type is known to KIE and hence is handled by CompositeKnowledgeBuilder
            kieFileSystem.write( destinationPath,
                                 KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
            addJavaClass( resource );
            handles.put( destinationPath,
                         paths.convert( resource ) );

            //Incremental build
            final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();
            results = convertMessages( incrementalResults );

            //Tidy-up removed message handles
            for ( Message message : incrementalResults.getRemovedMessages() ) {
                handles.remove( RESOURCE_PATH + "/" + message.getPath() );
            }

        } else {
            //Resource Type is unknown to KIE. Lookup a validation service to handle.
            final ValidationService validator = getValidationService( resource );
            if ( validator != null ) {
                final List<BuildMessage> addedMessages = validator.validate( paths.convert( resource ) );
                for ( BuildMessage message : addedMessages ) {
                    results.addAddedMessage( message );
                }
                nonKieResourceValidators.put( resource,
                                              validator );
                nonKieResourceValidatorMessages.put( resource,
                                                     addedMessages );
            }
            final List<BuildMessage> removedMessages = nonKieResourceValidatorMessages.remove( resource );
            if ( removedMessages != null ) {
                for ( BuildMessage message : removedMessages ) {
                    results.addRemovedMessage( message );
                }
            }
        }

        return results;
    }

    public IncrementalBuildResults deleteResource( final Path resource ) {
        PortablePreconditions.checkNotNull( "resource",
                                            resource );

        //Only files can be processed
        if ( !Files.isRegularFile( resource ) ) {
            return new IncrementalBuildResults();
        }

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Delete resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        IncrementalBuildResults results = new IncrementalBuildResults();

        if ( isResourceTypeSupportedByKie( resource ) ) {
            //Resource Type is known to KIE and hence is handled by CompositeKnowledgeBuilder
            kieFileSystem.delete( destinationPath );
            removeJavaClass( resource );

            //Incremental build
            final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();
            results = convertMessages( incrementalResults );

            //Tidy-up removed message handles
            for ( Message message : incrementalResults.getRemovedMessages() ) {
                handles.remove( RESOURCE_PATH + "/" + message.getPath() );
            }

        } else {
            //Resource Type is unknown to KIE. Lookup a validation service to handle.
            nonKieResourceValidators.remove( resource );
            final List<BuildMessage> removedMessages = nonKieResourceValidatorMessages.remove( resource );
            if ( removedMessages != null ) {
                for ( BuildMessage message : removedMessages ) {
                    results.addRemovedMessage( message );
                }
            }
        }

        return results;
    }

    public IncrementalBuildResults updateResource( final Path resource ) {
        return addResource( resource );
    }

    public IncrementalBuildResults applyBatchResourceChanges( final Set<ResourceChange> changes ) {
        PortablePreconditions.checkNotNull( "changes",
                                            changes );

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Add all changes to KieFileSystem before executing the build
        final List<String> changedFilesKieBuilderPaths = new ArrayList<String>();
        final List<BuildMessage> nonKieResourceValidatorAddedMessages = new ArrayList<BuildMessage>();
        final List<BuildMessage> nonKieResourceValidatorRemovedMessages = new ArrayList<BuildMessage>();
        for ( ResourceChange change : changes ) {
            final ChangeType type = change.getType();
            final Path resource = paths.convert( change.getPath() );

            //Only files can be processed
            if ( !Files.isRegularFile( resource ) ) {
                continue;
            }

            PortablePreconditions.checkNotNull( "type",
                                                type );
            PortablePreconditions.checkNotNull( "resource",
                                                resource );

            final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
            changedFilesKieBuilderPaths.add( destinationPath );
            switch ( type ) {
                case ADD:
                case UPDATE:
                    if ( isResourceTypeSupportedByKie( resource ) ) {
                        //Resource Type is known to KIE and hence is handled by CompositeKnowledgeBuilder
                        final InputStream is = ioService.newInputStream( resource );
                        final BufferedInputStream bis = new BufferedInputStream( is );
                        kieFileSystem.write( destinationPath,
                                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                        addJavaClass( resource );
                        handles.put( destinationPath,
                                     change.getPath() );

                    } else {
                        //Resource Type is unknown to KIE. Lookup a validation service to handle.
                        ValidationService validator = getValidationService( resource );
                        if ( validator != null ) {
                            final List<BuildMessage> addedMessages = validator.validate( paths.convert( resource ) );
                            for ( BuildMessage message : addedMessages ) {
                                nonKieResourceValidatorAddedMessages.add( message );
                            }
                            nonKieResourceValidators.put( resource,
                                                          validator );
                            nonKieResourceValidatorMessages.put( resource,
                                                                 addedMessages );
                        }
                        final List<BuildMessage> removedMessages = nonKieResourceValidatorMessages.remove( resource );
                        if ( removedMessages != null ) {
                            for ( BuildMessage message : removedMessages ) {
                                nonKieResourceValidatorRemovedMessages.add( message );
                            }
                        }
                    }

                    break;
                case DELETE:
                    if ( isResourceTypeSupportedByKie( resource ) ) {
                        //Resource Type is known to KIE and hence is handled by CompositeKnowledgeBuilder
                        kieFileSystem.delete( destinationPath );
                        removeJavaClass( resource );
                        break;

                    } else {
                        //Resource Type is unknown to KIE. Lookup a validation service to handle.
                        nonKieResourceValidators.remove( resource );
                        final List<BuildMessage> removedMessages = nonKieResourceValidatorMessages.remove( resource );
                        if ( removedMessages != null ) {
                            for ( BuildMessage message : removedMessages ) {
                                nonKieResourceValidatorRemovedMessages.add( message );
                            }
                        }
                    }
            }
        }

        //Perform the Incremental build
        final String[] kieBuilderPaths = new String[ changedFilesKieBuilderPaths.size() ];
        changedFilesKieBuilderPaths.toArray( kieBuilderPaths );
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( kieBuilderPaths ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Copy in BuildMessages for non-KIE resources
        for ( BuildMessage addedMessage : nonKieResourceValidatorAddedMessages ) {
            results.addAddedMessage( addedMessage );
        }
        for ( BuildMessage removedMessage : nonKieResourceValidatorRemovedMessages ) {
            results.addRemovedMessage( removedMessage );
        }

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public KieModule getKieModule() {
        return kieBuilder.getKieModule();
    }

    public KieModule getKieModuleIgnoringErrors() {
        return ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors();
    }

    public KieContainer getKieContainer() {
        if ( !isBuilt() ) {
            build();
        }
        return kieContainer;
    }

    public boolean isBuilt() {
        return kieBuilder != null;
    }

    private void visitPaths( final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream ) {
        for ( final org.kie.commons.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( Files.newDirectoryStream( path ) );

            } else {

                final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                final InputStream is = ioService.newInputStream( path );
                final BufferedInputStream bis = new BufferedInputStream( is );
                if ( isResourceTypeSupportedByKie( path ) ) {
                    //Resource Type is known to KIE and hence is handled by CompositeKnowledgeBuilder
                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );

                    //Java classes are handled by KIE so we can safely post-process them here
                    addJavaClass( path );
                    handles.put( destinationPath,
                                 paths.convert( path ) );
                } else {
                    //Resource Type is unknown to KIE. Lookup a validation service to handle.
                    final ValidationService validator = getValidationService( path );
                    if ( validator != null ) {
                        nonKieResourceValidators.put( path,
                                                      validator );
                    }
                }
            }
        }
    }

    private BuildResults convertMessages( final Results kieBuildResults ) {
        final BuildResults results = new BuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieBuildResults.getMessages() ) {
            results.addBuildMessage( convertMessage( message ) );
        }

        return results;
    }

    private IncrementalBuildResults convertMessages( final IncrementalResults kieIncrementalResults ) {
        final IncrementalBuildResults results = new IncrementalBuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieIncrementalResults.getAddedMessages() ) {
            results.addAddedMessage( convertMessage( message ) );
        }
        for ( final Message message : kieIncrementalResults.getRemovedMessages() ) {
            results.addRemovedMessage( convertMessage( message ) );
        }

        return results;
    }

    private BuildMessage convertMessage( final Message message ) {
        final BuildMessage m = new BuildMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                m.setLevel( BuildMessage.Level.ERROR );
                break;
            case WARNING:
                m.setLevel( BuildMessage.Level.WARNING );
                break;
            case INFO:
                m.setLevel( BuildMessage.Level.INFO );
                break;
        }

        m.setId( message.getId() );
        m.setArtifactID( artifactId );
        m.setLine( message.getLine() );
        if ( message.getPath() != null && !message.getPath().isEmpty() ) {
            m.setPath( handles.get( RESOURCE_PATH + "/" + message.getPath() ) );
        }
        m.setColumn( message.getColumn() );
        m.setText( message.getText() );
        return m;
    }

    private BuildMessage makeMessage( final String prefix,
                                      final Exception e ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( prefix + ": " + e.getMessage() );
        return buildMessage;
    }

    private void addJavaClass( final Path path ) {
        if ( !javaResourceFilter.accept( path ) ) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName( path );
        if ( fullyQualifiedClassName != null ) {
            javaResources.add( fullyQualifiedClassName );
        }
    }

    private void removeJavaClass( final Path path ) {
        if ( !javaResourceFilter.accept( path ) ) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName( path );
        if ( fullyQualifiedClassName != null ) {
            javaResources.remove( fullyQualifiedClassName );
        }
    }

    private String getFullyQualifiedClassName( final Path path ) {
        final Package pkg = projectService.resolvePackage( paths.convert( path,
                                                                          false ) );
        final String packageName = pkg.getPackageName();
        if ( packageName == null ) {
            return null;
        }
        final String className = path.getFileName().toString().replace( ".java",
                                                                        "" );
        return ( packageName.equals( "" ) ? className : packageName + "." + className );
    }

    public TypeSource getClassSource( final KieModuleMetaData metaData,
                                      final Class<?> clazz ) {
        //Was the Type declared in DRL
        if ( metaData.getTypeMetaInfo( clazz ).isDeclaredType() ) {
            return TypeSource.DECLARED;
        }

        //Was the Type defined inside the project or within a dependency
        String fullyQualifiedClassName = clazz.getName();
        int innerClassIdentifierIndex = fullyQualifiedClassName.indexOf( "$" );
        if ( innerClassIdentifierIndex > 0 ) {
            fullyQualifiedClassName = fullyQualifiedClassName.substring( 0,
                                                                         innerClassIdentifierIndex );
        }
        if ( javaResources.contains( fullyQualifiedClassName ) ) {
            return TypeSource.JAVA_PROJECT;
        }
        return TypeSource.JAVA_DEPENDENCY;
    }

    private boolean isResourceTypeSupportedByKie( final Path resource ) {
        final ResourceType resourceType = ResourceType.determineResourceType( resource.getFileName().toString() );
        return resourceType != null;
    }

    private ValidationService getValidationService( final Path resource ) {
        for ( ValidationService validator : validators ) {
            if ( validator.accepts( paths.convert( resource ) ) ) {
                return validator;
            }
        }
        return null;
    }

}
