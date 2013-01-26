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

package org.kie.guvnor.explorer.backend.server;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.explorer.backend.server.loaders.ItemsLoader;
import org.kie.guvnor.explorer.model.BreadCrumb;
import org.kie.guvnor.explorer.model.ExplorerContent;
import org.kie.guvnor.explorer.model.Item;
import org.kie.guvnor.explorer.service.ExplorerService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class ExplorerServiceImpl
        implements ExplorerService {

    private static final String JAVA_PATH = "src/main/java";
    private static final String RESOURCES_PATH = "src/main/resources";

    private IOService ioService;
    private ProjectService projectService;
    private BreadCrumbFactory breadCrumbFactory;
    private Paths paths;

    @Inject
    @Named("outsideProjectList")
    private ItemsLoader outsideProjectListLoader;

    @Inject
    @Named("projectRootList")
    private ItemsLoader projectRootListLoader;

    @Inject
    @Named("projectPackageList")
    private ItemsLoader projectPackageListLoader;

    @Inject
    @Named("projectNonPackageList")
    private ItemsLoader projectNonPackageListLoader;

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ExplorerServiceImpl( final @Named("ioStrategy") IOService ioService,
                                final ProjectService projectService,
                                final BreadCrumbFactory breadCrumbFactory,
                                final Paths paths ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.breadCrumbFactory = breadCrumbFactory;
        this.paths = paths;
    }

    @Override
    public ExplorerContent getContentInScope( final Path resource ) {
        //Null Path cannot be in a Project scope
        if ( resource == null ) {
            return makeOutsideProjectList( resource );
        }

        //Check if Path is within a Project scope
        final Path projectRootPath = projectService.resolveProject( resource );
        if ( projectRootPath == null ) {
            return makeOutsideProjectList( resource );
        }

        //Check if Path is Project root
        final boolean isProjectRootPath = projectRootPath.toURI().equals( resource.toURI() );
        if ( isProjectRootPath ) {
            return makeProjectRootList( resource );
        }

        //Check if Path is within Projects Java folder
        final org.kie.commons.java.nio.file.Path pRoot = paths.convert( projectRootPath );
        final org.kie.commons.java.nio.file.Path pJavaSources = pRoot.resolve( JAVA_PATH );
        final org.kie.commons.java.nio.file.Path pResources = pRoot.resolve( RESOURCES_PATH );
        final org.kie.commons.java.nio.file.Path pResource = paths.convert( resource );
        if ( Files.isSameFile( pResource, pJavaSources ) ) {
            return makeProjectRootList( resource );
        }
        if ( pResource.startsWith( pJavaSources ) ) {
            return makeProjectPackageList( resource );
        }

        //Check if Path is within Projects resources
        if ( Files.isSameFile( pResource, pResources ) ) {
            return makeProjectRootList( resource );
        }
        if ( pResource.startsWith( pResources ) ) {
            return makeProjectPackageList( resource );
        }

        //Otherwise Path must be between Project root and Project resources
        return makeProjectNonPackageList( resource );
    }

    private ExplorerContent makeOutsideProjectList( final Path path ) {
        final List<Item> items = outsideProjectListLoader.load( path );
        final List<BreadCrumb> breadCrumbs = breadCrumbFactory.makeBreadCrumbs( path );
        return new ExplorerContent( items,
                                    breadCrumbs );
    }

    private ExplorerContent makeProjectRootList( final Path path ) {
        final List<Item> items = projectRootListLoader.load( path );
        final List<BreadCrumb> breadCrumbs = breadCrumbFactory.makeBreadCrumbs( path,
                                                                                breadCrumbFactory.makeBreadCrumbExclusions( path ) );
        return new ExplorerContent( items,
                                    breadCrumbs );
    }

    private ExplorerContent makeProjectPackageList( final Path path ) {
        final List<Item> items = projectPackageListLoader.load( path );
        final List<BreadCrumb> breadCrumbs = breadCrumbFactory.makeBreadCrumbs( path,
                                                                                breadCrumbFactory.makeBreadCrumbExclusions( path ) );
        return new ExplorerContent( items,
                                    breadCrumbs );
    }

    private ExplorerContent makeProjectNonPackageList( final Path path ) {
        final List<Item> items = projectNonPackageListLoader.load( path );
        final List<BreadCrumb> breadCrumbs = breadCrumbFactory.makeBreadCrumbs( path,
                                                                                breadCrumbFactory.makeBreadCrumbExclusions( path ) );
        return new ExplorerContent( items,
                                    breadCrumbs );
    }

}
