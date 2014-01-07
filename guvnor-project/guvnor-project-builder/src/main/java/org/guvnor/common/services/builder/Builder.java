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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.events.RuleNameUpdateEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Builder {

    private final static String RESOURCE_PATH = "src/main/resources";

    private final static String ERROR_CLASS_NOT_FOUND = "Class not found";

    //TODO internationalize error messages?.
    private final static String ERROR_EXTERNAL_CLASS_VERIFICATON = "An error was found during external classes check.\n" +
            "The external class {0} did not pass the verification. \n" +
            "Please check the external .jar files configured as dependencies for this project.\n" +
            "The low level error is: ";

    private final static String DEFAULTPKG = "defaultpkg";

    private KieBuilder kieBuilder;
    private final Project project;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;
    private final Path moduleDirectory;
    private final GAV gav;
    private final IOService ioService;
    private final ProjectService projectService;

    private final String projectPrefix;

    private Map<String, org.uberfire.backend.vfs.Path> handles = new HashMap<String, org.uberfire.backend.vfs.Path>();

    private final Event<RuleNameUpdateEvent> ruleNameUpdateEvent;
    private final List<BuildValidationHelper> buildValidationHelpers;
    private final Map<Path, List<ValidationMessage>> nonKieResourceValidationHelperMessages = new HashMap<Path, List<ValidationMessage>>();

    private final DirectoryStream.Filter<Path> javaResourceFilter = new JavaFileFilter();
    private final DirectoryStream.Filter<Path> dotFileFilter = new DotFileFilter();

    private Set<String> javaResources = new HashSet<String>();

    private KieContainer kieContainer;

    public Builder( final Project project,
                    final Path moduleDirectory,
                    final GAV gav,
                    final IOService ioService,
                    final ProjectService projectService,
                    final Event<RuleNameUpdateEvent> ruleNameUpdateEvent,
                    final List<BuildValidationHelper> buildValidationHelpers ) {
        this.project = project;
        this.moduleDirectory = moduleDirectory;
        this.gav = gav;
        this.ioService = ioService;
        this.projectService = projectService;
        this.ruleNameUpdateEvent = ruleNameUpdateEvent;
        this.buildValidationHelpers = buildValidationHelpers;

        projectPrefix = moduleDirectory.toUri().toString();
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = Files.newDirectoryStream( moduleDirectory );
        visitPaths( directoryStream );
    }

    public BuildResults build() {
        //KieBuilder is not re-usable for successive "full" builds
        kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final Results kieResults = kieBuilder.buildAll().getResults();
        final BuildResults results = convertMessages( kieResults );

        //Add validate messages from external helpers
        for ( Map.Entry<Path, List<ValidationMessage>> e : nonKieResourceValidationHelperMessages.entrySet() ) {
            final List<ValidationMessage> validationMessages = e.getValue();
            if ( !( validationMessages == null || validationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : validationMessages ) {
                    results.addBuildMessage( convertValidationMessage( validationMessage ) );
                }
            }
        }

        //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
        final org.uberfire.java.nio.file.Path nioExternalImportsPath = moduleDirectory.resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final org.uberfire.backend.vfs.Path externalImportsPath = Paths.convert( nioExternalImportsPath );
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

        //At the end we are interested to ensure that external .jar files referenced as dependencies don't have
        // referential inconsistencies. We will at least provide a basic algorithm to ensure that if an external class
        // X references another external class Y, Y is also accessible by the class loader.
        final KieModuleMetaData kieModuleMetaData = getKieModuleMetaData();
        for ( final String packageName : kieModuleMetaData.getPackages() ) {
            for ( final String className : kieModuleMetaData.getClasses( packageName ) ) {
                final Class clazz = kieModuleMetaData.getClass( packageName, className );
                final TypeSource typeSource = getClassSource( kieModuleMetaData, clazz );
                if ( TypeSource.JAVA_DEPENDENCY == typeSource ) {
                    try {
                        verifyExternalClass( clazz );
                    } catch ( Throwable e ) {
                        results.addBuildMessage( makeMessage(
                                MessageFormat.format( ERROR_EXTERNAL_CLASS_VERIFICATON, clazz.getName() ), e ) );
                    }
                }
            }
        }

        //It's impossible to retrieve a KieContainer if the KieModule contains errors
        if ( results.getMessages().isEmpty() ) {
            kieContainer = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() );
        }

        fireRuleNameUpdateEvent();

        return results;
    }

    private KieModuleMetaData getKieModuleMetaData() {
        return KieModuleMetaData.Factory.newKieModuleMetaData( ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors() );
    }

    private void verifyExternalClass( Class clazz ) {
        //don't recommended to instantiate the class doing clazz.newInstance().
        clazz.getDeclaredConstructors();
        clazz.getDeclaredFields();
        clazz.getDeclaredMethods();
        clazz.getDeclaredClasses();
        clazz.getDeclaredAnnotations();
    }

    public IncrementalBuildResults addResource( final Path resource ) {
        checkNotNull( "resource",
                      resource );

        //Only files can be processed
        if ( !Files.isRegularFile( resource ) ) {
            return new IncrementalBuildResults( gav );
        }

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Resource Type might require "external" validation (i.e. it's not covered by Kie)
        final IncrementalBuildResults results = new IncrementalBuildResults( gav );
        final BuildValidationHelper validator = getBuildValidationHelper( resource );
        if ( validator != null ) {
            final List<ValidationMessage> addedValidationMessages = validator.validate( Paths.convert( resource ) );

            if ( !( addedValidationMessages == null || addedValidationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : addedValidationMessages ) {
                    results.addAddedMessage( convertValidationMessage( validationMessage ) );
                }
            }

            final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
            if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : removedValidationMessages ) {
                    results.addRemovedMessage( convertValidationMessage( validationMessage ) );
                }
            }
            nonKieResourceValidationHelperMessages.put( resource,
                                                        addedValidationMessages );
        }

        //Add new resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        final InputStream is = ioService.newInputStream( resource );
        final BufferedInputStream bis = new BufferedInputStream( is );
        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
        addJavaClass( resource );
        handles.put( destinationPath,
                     Paths.convert( resource ) );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();
        for ( final Message message : incrementalResults.getAddedMessages() ) {
            results.addAddedMessage( convertMessage( message ) );
        }
        for ( final Message message : incrementalResults.getRemovedMessages() ) {
            results.addRemovedMessage( convertMessage( message ) );
        }

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        fireRuleNameUpdateEvent();

        return results;
    }

    private void fireRuleNameUpdateEvent() {
        KieModuleMetaData kieModuleMetaData = getKieModuleMetaData();
        HashMap<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();
        for ( String packageName : kieModuleMetaData.getPackages() ) {
            if ( packageName.isEmpty() ) {
                packageName = DEFAULTPKG;
            }
            ruleNames.put( packageName, kieModuleMetaData.getRuleNamesInPackage( packageName ) );
        }

        ruleNames.put( DEFAULTPKG, kieModuleMetaData.getRuleNamesInPackage( DEFAULTPKG ) );

        ruleNameUpdateEvent.fire( new RuleNameUpdateEvent( project, ruleNames ) );
    }

    public IncrementalBuildResults deleteResource( final Path resource ) {
        checkNotNull( "resource",
                      resource );
        //The file has already been deleted so we can't check if the Path is a file or folder :(

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Delete resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        kieFileSystem.delete( destinationPath );
        removeJavaClass( resource );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
        final BuildValidationHelper validator = getBuildValidationHelper( resource );
        if ( validator != null ) {
            final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
            if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
                for ( ValidationMessage validationMessage : removedValidationMessages ) {
                    results.addRemovedMessage( convertValidationMessage( validationMessage ) );
                }
            }
        }

        fireRuleNameUpdateEvent();

        return results;
    }

    public IncrementalBuildResults updateResource( final Path resource ) {
        return addResource( resource );
    }

    public IncrementalBuildResults applyBatchResourceChanges( final Map<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> changes ) {
        checkNotNull( "changes", changes );

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Add all changes to KieFileSystem before executing the build
        final List<String> changedFilesKieBuilderPaths = new ArrayList<String>();
        final List<ValidationMessage> nonKieResourceValidatorAddedMessages = new ArrayList<ValidationMessage>();
        final List<ValidationMessage> nonKieResourceValidatorRemovedMessages = new ArrayList<ValidationMessage>();

        for ( final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> pathCollectionEntry : changes.entrySet() ) {
            for ( final ResourceChange change : pathCollectionEntry.getValue() ) {
                final ResourceChangeType type = change.getType();
                final Path resource = Paths.convert( pathCollectionEntry.getKey() );

                checkNotNull( "type", type );
                checkNotNull( "resource", resource );

                final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
                changedFilesKieBuilderPaths.add( destinationPath );
                switch ( type ) {
                    case ADD:
                    case UPDATE:
                        //Only files can be processed
                        if ( !Files.isRegularFile( resource ) ) {
                            continue;
                        }

                        //Resource Type might require "external" validation (i.e. it's not covered by Kie)
                        final BuildValidationHelper validator = getBuildValidationHelper( resource );
                        if ( validator != null ) {
                            final List<ValidationMessage> addedValidationMessages = validator.validate( Paths.convert( resource ) );

                            if ( !( addedValidationMessages == null || addedValidationMessages.isEmpty() ) ) {
                                for ( ValidationMessage validationMessage : addedValidationMessages ) {
                                    nonKieResourceValidatorAddedMessages.add( validationMessage );
                                }
                            }

                            final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
                            if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
                                for ( ValidationMessage validationMessage : removedValidationMessages ) {
                                    nonKieResourceValidatorRemovedMessages.add( validationMessage );
                                }
                            }
                            nonKieResourceValidationHelperMessages.put( resource,
                                                                        addedValidationMessages );
                        }

                        //Add new resource
                        final InputStream is = ioService.newInputStream( resource );
                        final BufferedInputStream bis = new BufferedInputStream( is );
                        kieFileSystem.write( destinationPath,
                                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                        addJavaClass( resource );
                        handles.put( destinationPath,
                                     Paths.convert( resource ) );

                        break;
                    case DELETE:
                        //The file has already been deleted so we can't check if the Path is a file or folder :(
                        kieFileSystem.delete( destinationPath );
                        removeJavaClass( resource );

                        //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
                        final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove( resource );
                        if ( !( removedValidationMessages == null || removedValidationMessages.isEmpty() ) ) {
                            for ( ValidationMessage validationMessage : removedValidationMessages ) {
                                nonKieResourceValidatorRemovedMessages.add( validationMessage );
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
        for ( ValidationMessage addedValidationMessage : nonKieResourceValidatorAddedMessages ) {
            results.addAddedMessage( convertValidationMessage( addedValidationMessage ) );
        }
        for ( ValidationMessage removedValidationMessage : nonKieResourceValidatorRemovedMessages ) {
            results.addRemovedMessage( convertValidationMessage( removedValidationMessage ) );
        }

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public KieModule getKieModule() {
        //Kie classes are only available once built
        if ( !isBuilt() ) {
            build();
        }
        return kieBuilder.getKieModule();
    }

    public KieModule getKieModuleIgnoringErrors() {
        //Kie classes are only available once built
        if ( !isBuilt() ) {
            build();
        }
        return ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors();
    }

    public KieContainer getKieContainer() {
        //Kie classes are only available once built
        if ( !isBuilt() ) {
            build();
        }
        return kieContainer;
    }

    public boolean isBuilt() {
        return kieBuilder != null;
    }

    private void visitPaths( final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream ) {
        for ( final org.uberfire.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( Files.newDirectoryStream( path ) );

            } else {
                //Don't process dotFiles
                if ( !dotFileFilter.accept( path ) ) {

                    //Resource Type might require "external" validation (i.e. it's not covered by Kie)
                    final BuildValidationHelper validator = getBuildValidationHelper( path );
                    if ( validator != null ) {
                        final org.uberfire.backend.vfs.Path vfsPath = Paths.convert( path );
                        final List<ValidationMessage> addedValidationMessages = validator.validate( vfsPath );
                        nonKieResourceValidationHelperMessages.put( path,
                                                                    addedValidationMessages );
                    }

                    //Add new resource
                    final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                    final InputStream is = ioService.newInputStream( path );
                    final BufferedInputStream bis = new BufferedInputStream( is );
                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                    handles.put( destinationPath,
                                 Paths.convert( path ) );

                    //Java classes are handled by KIE so we can safely post-process them here
                    addJavaClass( path );
                }
            }
        }
    }

    private BuildResults convertMessages( final Results kieBuildResults ) {
        final BuildResults results = new BuildResults( gav );
        for ( final Message message : kieBuildResults.getMessages() ) {
            results.addBuildMessage( convertMessage( message ) );
        }
        return results;
    }

    private IncrementalBuildResults convertMessages( final IncrementalResults kieIncrementalResults ) {
        final IncrementalBuildResults results = new IncrementalBuildResults( gav );
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
        m.setLine( message.getLine() );
        if ( message.getPath() != null && !message.getPath().isEmpty() ) {
            m.setPath( handles.get( RESOURCE_PATH + "/" + message.getPath() ) );
        }
        m.setColumn( message.getColumn() );
        m.setText( message.getText() );
        return m;
    }

    private BuildMessage convertValidationMessage( final ValidationMessage message ) {
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
        m.setLine( message.getLine() );
        m.setColumn( message.getColumn() );
        m.setText( message.getText() );
        m.setPath( message.getPath() );
        return m;
    }

    private BuildMessage makeMessage( final String prefix,
                                      final Throwable e ) {
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
        final Package pkg = projectService.resolvePackage( Paths.convert( path ) );
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

    private BuildValidationHelper getBuildValidationHelper( final Path nioResource ) {
        for ( BuildValidationHelper validator : buildValidationHelpers ) {
            final org.uberfire.backend.vfs.Path resource = Paths.convert( nioResource );
            if ( validator.accepts( resource ) ) {
                return validator;
            }
        }
        return null;
    }

}
