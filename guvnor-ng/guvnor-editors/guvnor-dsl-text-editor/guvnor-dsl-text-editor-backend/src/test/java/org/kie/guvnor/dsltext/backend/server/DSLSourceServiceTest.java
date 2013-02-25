/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.dsltext.backend.server;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DSLSourceServiceTest {

    private DSLSourceService dslSourceService;

    @Before
    public void setUp() throws Exception {
        dslSourceService = new DSLSourceService();
    }

    @Test
    public void testAlreadyHasPackage() throws Exception {
        Path path = mock( Path.class );
        when(
                path.toUri()
            ).thenReturn(
                new URI( "/src/main/resources/org/test/myfile.drl" )
                        );

        assertEquals( "package org.test\n", dslSourceService.getSource( path,
                                                                      "" ) );
    }

    @Test
    public void testAddPackage() throws Exception {
        Path path = mock( Path.class );
        when(
                path.toUri()
            ).thenReturn(
                new URI( "/src/main/resources/org/test/myfile.drl" )
                        );

        assertEquals( "package org.test\nsomething", dslSourceService.getSource( path,
                                                                                 "something" ) );
    }

}
