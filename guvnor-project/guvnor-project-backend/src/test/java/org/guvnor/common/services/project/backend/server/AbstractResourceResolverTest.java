/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AbstractResourceResolverTest {

    protected static final int PROJECT_RESOURCE_PATH_RESOLVERS_SIZE = 5;

    @Mock
    protected IOService ioService;

    @Mock
    protected POMService pomService;

    @Mock
    protected ConfigurationService configurationService;

    @Mock
    protected CommentedOptionFactory commentedOptionFactory;

    @Mock
    protected BackwardCompatibleUtil backward;

    @Mock
    protected Instance< ProjectResourcePathResolver > resourcePathResolversInstance;

    protected ResourceResolver resourceResolver;

    protected List< ProjectResourcePathResolver > projectResourcePathResolvers = new ArrayList<>( );

    @Rule
    public ExpectedException expectedException = ExpectedException.none( );

    @Before
    public void setUp( ) {
        initProjectResourcePathResolvers( PROJECT_RESOURCE_PATH_RESOLVERS_SIZE );
        when( resourcePathResolversInstance.iterator( ) ).thenReturn( projectResourcePathResolvers.iterator( ) );

        resourceResolver = spy( new ResourceResolver( ioService,
                pomService,
                configurationService,
                commentedOptionFactory,
                backward,
                resourcePathResolversInstance ) {
            @Override
            public Project resolveProject( Path resource ) {
                return null;
            }

            @Override
            public Project simpleProjectInstance( org.uberfire.java.nio.file.Path nioProjectRootPath ) {
                return null;
            }
        } );
    }

    @Test
    public void resolveDefaultPathSuccessful( ) {
        Package pkg = mock( Package.class );
        String resourceType = "any";
        Path defaultPath = mock( Path.class );

        // pick one of the configured resolvers as the one that accepts the given resource type.
        ProjectResourcePathResolver blessedProjectResourcePathResolver = projectResourcePathResolvers.get( 3 );
        when( blessedProjectResourcePathResolver.accept( resourceType ) ).thenReturn( true );
        when( blessedProjectResourcePathResolver.resolveDefaultPath( pkg ) ).thenReturn( defaultPath );

        assertEquals( defaultPath, resourceResolver.resolveDefaultPath( pkg, resourceType ) );

        verify( blessedProjectResourcePathResolver, times( 1 ) ).resolveDefaultPath( pkg );
        projectResourcePathResolvers.forEach( projectResourcePathResolver -> {
            if ( projectResourcePathResolver != blessedProjectResourcePathResolver ) {
                verify( projectResourcePathResolver, never( ) ).resolveDefaultPath( any( Package.class ) );
            }
        } );
    }

    @Test
    public void resolveDefaultPathWithErrors( ) {
        Package pkg = mock( Package.class );
        String resourceType = "any";
        // if none of the configured ProjectResourcePathResolvers accepts the resourceType, and exception must have been
        // thrown.
        expectedException.expectMessage( "No ProjectResourcePathResolver has been defined for resourceType: " + resourceType );
        resourceResolver.resolveDefaultPath( pkg, resourceType );
    }

    private void initProjectResourcePathResolvers( int size ) {
        for ( int i = 0; i < size; i++ ) {
            projectResourcePathResolvers.add( mock( ProjectResourcePathResolver.class ) );
        }
    }
}