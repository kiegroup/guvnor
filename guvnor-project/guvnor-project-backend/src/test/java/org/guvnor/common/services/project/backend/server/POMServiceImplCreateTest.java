/*
 * Copyright 2015 JBoss Inc
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

import java.net.URL;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class POMServiceImplCreateTest
        extends CDITestBase {

    @Mock IOService ioService;

    private POMService service;

    @Before
    public void setUp() throws Exception {

        TestAppSetup.ioService = ioService;

        super.setUp();

        service = getReference( POMService.class );

    }

    @Test
    public void testCreate() throws Exception {

        final URL url = this.getClass().getResource( "/TestProject" );
        service.create( Paths.convert( fs.getPath( url.toURI() ) ),
                        "baseurl?",
                        new POM() );

        ArgumentCaptor<org.uberfire.java.nio.file.Path> pathArgumentCaptor = ArgumentCaptor.forClass( org.uberfire.java.nio.file.Path.class );
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass( String.class );
        verify( ioService ).write( pathArgumentCaptor.capture(), stringArgumentCaptor.capture() );
        assertTrue( pathArgumentCaptor.getValue().toUri().toString().endsWith( "TestProject/pom.xml" ) );

        String pomXML = stringArgumentCaptor.getValue();

        assertTrue( pomXML.contains( "<id>guvnor-m2-repo</id>" ) );

    }

}