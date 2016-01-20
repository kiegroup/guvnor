/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.test.GuvnorTestAppSetup;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class POMServiceImplCreateTest {


    private POMService     service;
    private TestFileSystem testFileSystem;
    private IOService      ioService;

    @Before
    public void setUp() throws Exception {
        ioService = mock( IOService.class );

        GuvnorTestAppSetup.ioService = ioService;

        testFileSystem = new TestFileSystem();

        service = testFileSystem.getReference( POMService.class );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
        GuvnorTestAppSetup.reset();
    }

    @Test
    public void testCreate() throws Exception {

        final Path path = testFileSystem.createTempDirectory( "/MyTestProject" );

        service.create( path,
                        "baseurl?",
                        new POM() );

        ArgumentCaptor<org.uberfire.java.nio.file.Path> pathArgumentCaptor = ArgumentCaptor.forClass( org.uberfire.java.nio.file.Path.class );
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass( String.class );

        verify( ioService ).write( pathArgumentCaptor.capture(), stringArgumentCaptor.capture() );

        assertEquals( pathArgumentCaptor.getValue().toUri().toString(),
                      path.toURI() + "/pom.xml" );

        String pomXML = stringArgumentCaptor.getValue();

        assertTrue( pomXML.contains( "<id>guvnor-m2-repo</id>" ) );

    }

}