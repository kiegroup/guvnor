/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.LinkedDirectoryFilter;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.project.ProjectFactory;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.PackageAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

public abstract class AbstractProjectService<T extends Project>
        implements ProjectService<T>,
                   ProjectFactory<T> {

    protected static final String SOURCE_FILENAME = "src";

    protected static final String POM_PATH = "pom.xml";

    private static final String MAIN_SRC_PATH = "src/main/java";
    private static final String TEST_SRC_PATH = "src/test/java";
    protected static final String MAIN_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private static String[] sourcePaths = { MAIN_SRC_PATH, MAIN_RESOURCES_PATH, TEST_SRC_PATH, TEST_RESOURCES_PATH };

    protected IOService ioService;

    protected POMService pomService;
    protected ProjectConfigurationContentHandler projectConfigurationContentHandler;

    private ConfigurationService configurationService;
    private ConfigurationFactory configurationFactory;

    protected Event<NewProjectEvent> newProjectEvent;
    protected Event<NewPackageEvent> newPackageEvent;

    private Event<RenameProjectEvent> renameProjectEvent;
    private Event<DeleteProjectEvent> deleteProjectEvent;

    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache;

    @Inject
    private CommentedOptionFactory optionsFactory;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private BackwardCompatibleUtil backward;

    @Inject
    private User identity;

    protected SessionInfo sessionInfo;

    protected AbstractProjectService() {
    }

    public AbstractProjectService( final IOService ioService,
                                   final POMService pomService,
                                   final ProjectConfigurationContentHandler projectConfigurationContentHandler,
                                   final ConfigurationService configurationService,
                                   final ConfigurationFactory configurationFactory,
                                   final Event<NewProjectEvent> newProjectEvent,
                                   final Event<NewPackageEvent> newPackageEvent,
                                   final Event<RenameProjectEvent> renameProjectEvent,
                                   final Event<DeleteProjectEvent> deleteProjectEvent,
                                   final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                                   final User identity,
                                   final SessionInfo sessionInfo ) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.projectConfigurationContentHandler = projectConfigurationContentHandler;
        this.configurationService = configurationService;
        this.configurationFactory = configurationFactory;
        this.newProjectEvent = newProjectEvent;
        this.newPackageEvent = newPackageEvent;
        this.renameProjectEvent = renameProjectEvent;
        this.deleteProjectEvent = deleteProjectEvent;
        this.invalidateDMOCache = invalidateDMOCache;
        this.identity = identity;
        this.sessionInfo = new SafeSessionInfo( sessionInfo );
    }

    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    protected T makeProject( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {
        final T project = simpleProjectInstance( nioProjectRootPath );

        addSecurityGroups( project );

        return project;
    }

    protected void addSecurityGroups( final T project ) {
        //Copy in Security Roles required to access this resource
        final ConfigGroup projectConfiguration = findProjectConfig( project.getRootPath() );
        if ( projectConfiguration != null ) {
            ConfigItem<List<String>> groups = backward.compat( projectConfiguration ).getConfigItem( "security:groups" );
            if ( groups != null ) {
                for ( String group : groups.getValue() ) {
                    project.getGroups().add( group );
                }
            }
        }
    }

    public abstract T simpleProjectInstance( final org.uberfire.java.nio.file.Path nioProjectRootPath );

    @Override
    public org.guvnor.common.services.project.model.Package resolvePackage( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return null;
            }

            //If Path is not within a Project we cannot resolve a package
            final Project project = resolveProject( resource );
            if ( project == null ) {
                return null;
            }

            //pom.xml is not inside a package
            if ( isPom( resource ) ) {
                return null;
            }

            return makePackage( project,
                                resource );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    public abstract T resolveProject( final Path resource );

    @Override
    public Set<Package> resolvePackages( final Project project ) {
        final Set<Package> packages = new HashSet<Package>();
        final Set<String> packageNames = new HashSet<String>();
        if ( project == null ) {
            return packages;
        }
        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert( projectRoot );
        for ( String src : sourcePaths ) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src );
            packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                  nioPackageRootSrcPath,
                                                  true,
                                                  true,
                                                  true ) );
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for ( String packagePathSuffix : packageNames ) {
            for ( String src : sourcePaths ) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve( src ).resolve( packagePathSuffix );
                if ( Files.exists( nioPackagePath ) && !resolvedPackages.contains( packagePathSuffix ) ) {
                    packages.add( resolvePackage( Paths.convert( nioPackagePath ) ) );
                    resolvedPackages.add( packagePathSuffix );
                }
            }
        }

        return packages;
    }

    @Override
    public Set<Package> resolvePackages( final Package pkg ) {
        final Set<Package> packages = new HashSet<Package>();
        final Set<String> packageNames = new HashSet<String>();
        if ( pkg == null ) {
            return packages;
        }

        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)

        final Path projectRoot = pkg.getProjectRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert( projectRoot );

        for ( String src : sourcePaths ) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src ).resolve( resolvePkgName( pkg.getCaption() ) );
            packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                  nioPackageRootSrcPath,
                                                  false,
                                                  true,
                                                  false ) );
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for ( String packagePathSuffix : packageNames ) {
            for ( String src : sourcePaths ) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve( src ).resolve( packagePathSuffix );
                if ( Files.exists( nioPackagePath ) && !resolvedPackages.contains( packagePathSuffix ) ) {
                    packages.add( resolvePackage( Paths.convert( nioPackagePath ) ) );
                    resolvedPackages.add( packagePathSuffix );
                }
            }
        }

        return packages;
    }

    @Override
    public Package resolveDefaultPackage( final Project project ) {
        final Set<String> packageNames = new HashSet<String>();
        if ( project == null ) {
            return null;
        }
        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert( projectRoot );
        for ( String src : sourcePaths ) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src );
            packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                  nioPackageRootSrcPath,
                                                  true,
                                                  true,
                                                  false ) );
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for ( String packagePathSuffix : packageNames ) {
            for ( String src : sourcePaths ) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve( src ).resolve( packagePathSuffix );
                if ( Files.exists( nioPackagePath ) && !resolvedPackages.contains( packagePathSuffix ) ) {
                    return resolvePackage( Paths.convert( nioPackagePath ) );
                }
            }
        }

        return null;
    }

    @Override
    public Package resolveParentPackage( final Package pkg ) {
        final Set<String> packageNames = new HashSet<String>();

        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert( pkg.getProjectRootPath() );
        packageNames.addAll( getPackageNames( nioProjectRootPath,
                                              Paths.convert( pkg.getPackageMainSrcPath() ).getParent(),
                                              true,
                                              false,
                                              false ) );
        packageNames.addAll( getPackageNames( nioProjectRootPath,
                                              Paths.convert( pkg.getPackageMainResourcesPath() ).getParent(),
                                              true,
                                              false,
                                              false ) );
        packageNames.addAll( getPackageNames( nioProjectRootPath,
                                              Paths.convert( pkg.getPackageTestSrcPath() ).getParent(),
                                              true,
                                              false,
                                              false ) );
        packageNames.addAll( getPackageNames( nioProjectRootPath,
                                              Paths.convert( pkg.getPackageTestResourcesPath() ).getParent(),
                                              true,
                                              false,
                                              false ) );

        //Construct Package objects for each package name
        for ( String packagePathSuffix : packageNames ) {
            for ( String src : sourcePaths ) {
                if ( packagePathSuffix == null ) {
                    return null;
                }
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve( src ).resolve( packagePathSuffix );
                if ( Files.exists( nioPackagePath ) ) {
                    return resolvePackage( Paths.convert( nioPackagePath ) );
                }
            }
        }

        return null;
    }

    private String resolvePkgName( final String caption ) {
        if ( caption.equals( "<default>" ) ) {
            return "";
        }
        return caption.replaceAll( "\\.", "/" );
    }

    private Set<String> getPackageNames( final org.uberfire.java.nio.file.Path nioProjectRootPath,
                                         final org.uberfire.java.nio.file.Path nioPackageSrcPath,
                                         final boolean includeDefault,
                                         final boolean includeChild,
                                         final boolean recursive ) {
        final Set<String> packageNames = new HashSet<String>();
        if ( !Files.exists( nioPackageSrcPath ) ) {
            return packageNames;
        }
        if ( includeDefault || recursive ) {
            packageNames.add( getPackagePathSuffix( nioProjectRootPath,
                                                    nioPackageSrcPath ) );

        }

        if ( !includeChild ) {
            return packageNames;
        }

        //We're only interested in Directories (and not META-INF) so set-up appropriate filters
        final LinkedMetaInfFolderFilter metaDataFileFilter = new LinkedMetaInfFolderFilter();
        final LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter( metaDataFileFilter );
        final LinkedDirectoryFilter directoryFilter = new LinkedDirectoryFilter( dotFileFilter );

        final DirectoryStream<org.uberfire.java.nio.file.Path> nioChildPackageSrcPaths = ioService.newDirectoryStream( nioPackageSrcPath,
                                                                                                                       directoryFilter );
        for ( org.uberfire.java.nio.file.Path nioChildPackageSrcPath : nioChildPackageSrcPaths ) {
            if ( recursive ) {
                packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                      nioChildPackageSrcPath,
                                                      includeDefault,
                                                      includeChild,
                                                      recursive ) );
            } else {
                packageNames.add( getPackagePathSuffix( nioProjectRootPath,
                                                        nioChildPackageSrcPath ) );
            }
        }
        return packageNames;
    }

    private String getPackagePathSuffix( final org.uberfire.java.nio.file.Path nioProjectRootPath,
                                         final org.uberfire.java.nio.file.Path nioPackagePath ) {
        final org.uberfire.java.nio.file.Path nioMainSrcPath = nioProjectRootPath.resolve( MAIN_SRC_PATH );
        final org.uberfire.java.nio.file.Path nioTestSrcPath = nioProjectRootPath.resolve( TEST_SRC_PATH );
        final org.uberfire.java.nio.file.Path nioMainResourcesPath = nioProjectRootPath.resolve( MAIN_RESOURCES_PATH );
        final org.uberfire.java.nio.file.Path nioTestResourcesPath = nioProjectRootPath.resolve( TEST_RESOURCES_PATH );

        String packageName = null;
        org.uberfire.java.nio.file.Path packagePath = null;
        if ( nioPackagePath.startsWith( nioMainSrcPath ) ) {
            packagePath = nioMainSrcPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioTestSrcPath ) ) {
            packagePath = nioTestSrcPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioMainResourcesPath ) ) {
            packagePath = nioMainResourcesPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioTestResourcesPath ) ) {
            packagePath = nioTestResourcesPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        }

        return packageName;
    }

    protected Package makePackage( final Project project,
                                   final Path resource ) {
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRoot = Paths.convert( projectRoot );
        final org.uberfire.java.nio.file.Path nioMainSrcPath = nioProjectRoot.resolve( MAIN_SRC_PATH );
        final org.uberfire.java.nio.file.Path nioTestSrcPath = nioProjectRoot.resolve( TEST_SRC_PATH );
        final org.uberfire.java.nio.file.Path nioMainResourcesPath = nioProjectRoot.resolve( MAIN_RESOURCES_PATH );
        final org.uberfire.java.nio.file.Path nioTestResourcesPath = nioProjectRoot.resolve( TEST_RESOURCES_PATH );

        org.uberfire.java.nio.file.Path nioResource = Paths.convert( resource );

        if ( Files.isRegularFile( nioResource ) ) {
            nioResource = nioResource.getParent();
        }

        String packageName = null;
        org.uberfire.java.nio.file.Path packagePath = null;
        if ( nioResource.startsWith( nioMainSrcPath ) ) {
            packagePath = nioMainSrcPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioTestSrcPath ) ) {
            packagePath = nioTestSrcPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioMainResourcesPath ) ) {
            packagePath = nioMainResourcesPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioTestResourcesPath ) ) {
            packagePath = nioTestResourcesPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        }

        //Resource was not inside a package
        if ( packageName == null ) {
            return null;
        }

        final Path mainSrcPath = Paths.convert( nioMainSrcPath.resolve( packagePath ) );
        final Path testSrcPath = Paths.convert( nioTestSrcPath.resolve( packagePath ) );
        final Path mainResourcesPath = Paths.convert( nioMainResourcesPath.resolve( packagePath ) );
        final Path testResourcesPath = Paths.convert( nioTestResourcesPath.resolve( packagePath ) );

        final String displayName = getPackageDisplayName( packageName );

        final Package pkg = new Package( project.getRootPath(),
                                         mainSrcPath,
                                         testSrcPath,
                                         mainResourcesPath,
                                         testResourcesPath,
                                         packageName,
                                         displayName,
                                         getPackageRelativeCaption( displayName, resource.getFileName() ) );
        return pkg;
    }

    private String getPackageDisplayName( final String packageName ) {
        return packageName.isEmpty() ? "<default>" : packageName;
    }

    private String getPackageRelativeCaption( final String displayName,
                                              final String relativeName ) {
        return displayName.equals( "<default>" ) ? "<default>" : relativeName;
    }

    @Override
    public boolean isPom( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return false;
            }

            //Check if path equals pom.xml
            final Project project = resolveProject( resource );

            //It's possible that the Incremental Build attempts to act on a Project file before the project has been fully created.
            //This should be a short-term issue that will be resolved when saving a project batches pom.xml, kmodule.xml and project.imports
            //etc into a single git-batch. At present they are saved individually leading to multiple Incremental Build requests.
            if ( project == null ) {
                return false;
            }

            final org.uberfire.java.nio.file.Path path = Paths.convert( resource ).normalize();
            final org.uberfire.java.nio.file.Path pomFilePath = Paths.convert( project.getPomXMLPath() );
            return path.startsWith( pomFilePath );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public abstract T newProject(
            final org.guvnor.structure.repositories.Repository repository,
            final POM pom,
            final String baseUrl );

    @Override
    public Package newPackage( final Package parentPackage,
                               final String packageName ) {
        try {
            //Make new Package
            final Package newPackage = doNewPackage( parentPackage,
                                                     packageName,
                                                     true );

            //Raise an event for the new package
            newPackageEvent.fire( new NewPackageEvent( newPackage ) );

            //Return the new package
            return newPackage;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    protected Package doNewPackage( final Package parentPackage,
                                    final String packageName,
                                    final boolean startBatch ) {
        //If the package name contains separators, create sub-folders
        String newPackageName = packageName.toLowerCase();
        if ( newPackageName.contains( "." ) ) {
            newPackageName = newPackageName.replace( ".",
                                                     "/" );
        }

        //Return new package
        final Path mainSrcPath = parentPackage.getPackageMainSrcPath();
        final Path testSrcPath = parentPackage.getPackageTestSrcPath();
        final Path mainResourcesPath = parentPackage.getPackageMainResourcesPath();
        final Path testResourcesPath = parentPackage.getPackageTestResourcesPath();

        Path pkgPath = null;
        final FileSystem fs = Paths.convert( parentPackage.getPackageMainSrcPath() ).getFileSystem();

        try {

            if ( startBatch ) {
                ioService.startBatch( fs, makeCommentedOption( "New package [" + packageName + "]" ) );
            }

            final org.uberfire.java.nio.file.Path nioMainSrcPackagePath = Paths.convert( mainSrcPath ).resolve( newPackageName );
            if ( !Files.exists( nioMainSrcPackagePath ) ) {
                pkgPath = Paths.convert( ioService.createDirectory( nioMainSrcPackagePath ) );
            }
            final org.uberfire.java.nio.file.Path nioTestSrcPackagePath = Paths.convert( testSrcPath ).resolve( newPackageName );
            if ( !Files.exists( nioTestSrcPackagePath ) ) {
                pkgPath = Paths.convert( ioService.createDirectory( nioTestSrcPackagePath ) );
            }
            final org.uberfire.java.nio.file.Path nioMainResourcesPackagePath = Paths.convert( mainResourcesPath ).resolve( newPackageName );
            if ( !Files.exists( nioMainResourcesPackagePath ) ) {
                pkgPath = Paths.convert( ioService.createDirectory( nioMainResourcesPackagePath ) );
            }
            final org.uberfire.java.nio.file.Path nioTestResourcesPackagePath = Paths.convert( testResourcesPath ).resolve( newPackageName );
            if ( !Files.exists( nioTestResourcesPackagePath ) ) {
                pkgPath = Paths.convert( ioService.createDirectory( nioTestResourcesPackagePath ) );
            }

            //If pkgPath is null the package already existed in src/main/java, scr/main/resources, src/test/java and src/test/resources
            if ( pkgPath == null ) {
                throw new PackageAlreadyExistsException( packageName );
            }

            //Return new package
            final Package newPackage = resolvePackage( pkgPath );
            return newPackage;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            if ( startBatch ) {
                ioService.endBatch();
            }
        }
    }

    protected boolean hasPom( final org.uberfire.java.nio.file.Path path ) {
        final org.uberfire.java.nio.file.Path pomPath = path.resolve( POM_PATH );
        return Files.exists( pomPath );
    }

    protected CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = getIdentityName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( getSessionId(),
                                                        name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

    protected String getIdentityName() {
        try {
            return identity.getIdentifier();
        } catch ( ContextNotActiveException e ) {
            return "unknown";
        }
    }

    protected String getSessionId() {
        try {
            return sessionInfo.getId();
        } catch ( Exception e ) {
            return "--";
        }
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public void addGroup( final Project project,
                          final String group ) {
        ConfigGroup thisProjectConfig = findProjectConfig( project.getRootPath() );

        if ( thisProjectConfig == null ) {
            thisProjectConfig = configurationFactory.newConfigGroup( ConfigType.PROJECT,
                                                                     project.getRootPath().toURI(),
                                                                     "Project '" + project.getProjectName() + "' configuration" );
            thisProjectConfig.addConfigItem( configurationFactory.newConfigItem( "security:groups",
                                                                                 new ArrayList<String>() ) );
            configurationService.addConfiguration( thisProjectConfig );
        }

        if ( thisProjectConfig != null ) {
            final ConfigItem<List> groups = backward.compat( thisProjectConfig ).getConfigItem( "security:groups" );
            groups.getValue().add( group );

            configurationService.updateConfiguration( thisProjectConfig );

        } else {
            throw new IllegalArgumentException( "Project " + project.getProjectName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeGroup( final Project project,
                             final String group ) {
        final ConfigGroup thisProjectConfig = findProjectConfig( project.getRootPath() );

        if ( thisProjectConfig != null ) {
            final ConfigItem<List> groups = backward.compat( thisProjectConfig ).getConfigItem( "security:groups" );
            groups.getValue().remove( group );

            configurationService.updateConfiguration( thisProjectConfig );

        } else {
            throw new IllegalArgumentException( "Project " + project.getProjectName() + " not found" );
        }
    }

    @Override
    public Path rename( final Path pathToPomXML,
                        final String newName,
                        final String comment ) {

        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert( pathToPomXML ).getParent();
            final org.uberfire.java.nio.file.Path newProjectPath = projectDirectory.resolveSibling( newName );

            final POM content = pomService.load( pathToPomXML );

            if ( newProjectPath.equals( projectDirectory ) ) {
                return pathToPomXML;
            }

            if ( ioService.exists( newProjectPath ) ) {
                throw new FileAlreadyExistsException( newProjectPath.toString() );
            }

            final Path oldProjectDir = Paths.convert( projectDirectory );
            final Project oldProject = resolveProject( oldProjectDir );

            content.setName( newName );
            final Path newPathToPomXML = Paths.convert( newProjectPath.resolve( "pom.xml" ) );
            try {
                ioService.startBatch( newProjectPath.getFileSystem() );
                ioService.move( projectDirectory, newProjectPath, makeCommentedOption( comment ) );
                pomService.save( newPathToPomXML, content, null, comment );
            } catch ( final Exception e ) {
                throw e;
            } finally {
                ioService.endBatch();
            }
            final Project newProject = resolveProject( Paths.convert( newProjectPath ) );
            invalidateDMOCache.fire( new InvalidateDMOProjectCacheEvent( sessionInfo, oldProject, oldProjectDir ) );
            renameProjectEvent.fire( new RenameProjectEvent( oldProject, newProject ) );

            return newPathToPomXML;
        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void delete( final Path pathToPomXML,
                        final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert( pathToPomXML ).getParent();
            final Project project2Delete = resolveProject( Paths.convert( projectDirectory ) );

            final org.uberfire.java.nio.file.Path parentPom = projectDirectory.getParent().resolve( "pom.xml" );
            POM parent = null;
            if ( ioService.exists( parentPom ) ) {
                parent = pomService.load( Paths.convert( parentPom ) );
            }

            ioService.delete( projectDirectory,
                              StandardDeleteOption.NON_EMPTY_DIRECTORIES,
                              optionsFactory.makeCommentedOption( comment,
                                                                  identity,
                                                                  sessionInfo ) );
            //Note we do *not* raise a DeleteProjectEvent here, as that is handled by DeleteProjectObserverBridge
            
            if ( parent != null ) {
                parent.setPackaging( "pom" );
                parent.getModules().remove( project2Delete.getProjectName() );
                pomService.save( Paths.convert( parentPom ),
                                 parent,
                                 null,
                                 "Removing child module " + project2Delete.getProjectName() );
            }

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void copy( final Path pathToPomXML,
                      final String newName,
                      final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert( pathToPomXML ).getParent();
            final org.uberfire.java.nio.file.Path newProjectPath = projectDirectory.resolveSibling( newName );

            final POM content = pomService.load( pathToPomXML );

            if ( newProjectPath.equals( projectDirectory ) ) {
                return;
            }

            if ( ioService.exists( newProjectPath ) ) {
                throw new FileAlreadyExistsException( newProjectPath.toString() );
            }

            content.setName( newName );
            final Path newPathToPomXML = Paths.convert( newProjectPath.resolve( "pom.xml" ) );
            try {
                ioService.startBatch( newProjectPath.getFileSystem() );
                ioService.copy( projectDirectory, newProjectPath, makeCommentedOption( comment ) );
                pomService.save( newPathToPomXML, content, null, comment );
            } catch ( final Exception e ) {
                throw e;
            } finally {
                ioService.endBatch();
            }
            final Project newProject = resolveProject( Paths.convert( newProjectPath ) );
            newProjectEvent.fire( new NewProjectEvent( newProject, getSessionId(), getIdentityName() ) );

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    protected ConfigGroup findProjectConfig( final Path projectRoot ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.PROJECT );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( projectRoot.toURI() ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public Set<Project> getProjects( final Repository repository,
                                     String branch ) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if ( repository == null ) {
            return authorizedProjects;
        }
        final Path repositoryRoot = repository.getBranchRoot( branch );
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( Paths.convert( repositoryRoot ) );
        try {
            for ( org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
                if ( Files.isDirectory( nioRepositoryPath ) ) {
                    final org.uberfire.backend.vfs.Path projectPath = Paths.convert( nioRepositoryPath );
                    final Project project = resolveProject( projectPath );

                    if ( project != null ) {
                        if ( authorizationManager.authorize( project, identity ) ) {
                            POM projectPom = pomService.load( project.getPomXMLPath() );
                            project.setPom( projectPom );
                            authorizedProjects.add( project );
                        }
                    }
                }
            }
        } finally {
            nioRepositoryPaths.close();
        }
        return authorizedProjects;
    }

}
