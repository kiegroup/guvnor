/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.common.services.project.model.Project;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.impl.authz.RuntimeResourceManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectRuntimeResourceManagerTest {

    private RuntimeResourceManager manager = new RuntimeResourceManager();

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1193484
    public void testCachedRestrictionRefresh() {
        final Path root = mock( Path.class );
        when( root.toURI() ).thenReturn( "root" );
        final Project p1_1 = new Project( root,
                                          mock( Path.class ),
                                          "p1" );
        final boolean response1 = manager.requiresAuthentication( p1_1 );
        assertFalse( response1 );

        final Project p1_2 = new Project( root,
                                          mock( Path.class ),
                                          "p1" );
        p1_2.getGroups().add( "admin" );
        final boolean response2 = manager.requiresAuthentication( p1_2 );
        assertTrue( response2 );
    }

}
