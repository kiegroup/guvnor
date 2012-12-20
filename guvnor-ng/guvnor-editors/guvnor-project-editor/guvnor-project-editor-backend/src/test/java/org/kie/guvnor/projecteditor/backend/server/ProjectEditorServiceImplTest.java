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

package org.kie.guvnor.projecteditor.backend.server;

import org.junit.Before;
import org.junit.Test;
import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.event.Event;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectEditorServiceImplTest {

    private IOService ioService;
    private Paths paths;
    private ProjectEditorServiceImpl service;
    private KModuleEditorContentHandler kProjectEditorContentHandler;
    private Event messagesEvent;
    private ProjectService projectService;

    @Before
    public void setUp() throws Exception {
        ioService = mock(IOService.class);
        paths = mock(Paths.class);
        kProjectEditorContentHandler = mock(KModuleEditorContentHandler.class);
        messagesEvent = mock(Event.class);
        BuildService buildService = mock(BuildService.class);
        projectService = mock(ProjectService.class);
        service = new ProjectEditorServiceImpl(ioService, paths, buildService, messagesEvent, kProjectEditorContentHandler, projectService);
    }

    @Test
    public void testSetUpProjectStructure() throws Exception {

        Path pathToPom = mock(Path.class);
        org.kie.commons.java.nio.file.Path directory = setUpPathToPomDirectory(pathToPom);

        org.kie.commons.java.nio.file.Path mainJava = mock(org.kie.commons.java.nio.file.Path.class);
        setUpDirectory(directory, "src/main/java", mainJava);
        org.kie.commons.java.nio.file.Path mainResources = mock(org.kie.commons.java.nio.file.Path.class);
        setUpDirectory(directory, "src/main/resources", mainResources);
        org.kie.commons.java.nio.file.Path testJava = mock(org.kie.commons.java.nio.file.Path.class);
        setUpDirectory(directory, "src/test/java", testJava);
        org.kie.commons.java.nio.file.Path testResources = mock(org.kie.commons.java.nio.file.Path.class);
        setUpDirectory(directory, "src/test/resources", testResources);

        org.kie.commons.java.nio.file.Path kmodule = mock(org.kie.commons.java.nio.file.Path.class);
        setUpDirectory(directory, "src/main/resources/META-INF/kmodule.xml", kmodule);

        service.setUpKModuleStructure(pathToPom);

        verify(ioService).createDirectory(mainJava);
        verify(ioService).createDirectory(mainResources);
        verify(ioService).createDirectory(testJava);
        verify(ioService).createDirectory(testResources);

        verify(ioService).write(eq(kmodule), anyString());
    }

    private org.kie.commons.java.nio.file.Path setUpPathToPomDirectory(Path pathToPom) {
        org.kie.commons.java.nio.file.Path child = mock(org.kie.commons.java.nio.file.Path.class);
        when(
                paths.convert(pathToPom)
        ).thenReturn(
                child
        );
        org.kie.commons.java.nio.file.Path directory = mock(org.kie.commons.java.nio.file.Path.class);
        when(
                child.getParent()
        ).thenReturn(
                directory
        );
        return directory;
    }

    private void setUpDirectory(org.kie.commons.java.nio.file.Path directory, String pathAsText, org.kie.commons.java.nio.file.Path path) {
        when(
                directory.resolve(pathAsText)
        ).thenReturn(
                path
        );
    }


    //
//    @Test
//    public void testLoadKProject() throws Exception {
//
//        Path path = messagesEvent( Path.class );
//        when(
//                ioService.readAllString( path )
//            ).thenReturn(
//                "blaaXML"
//                        );
//
//        KModuleModel original = new KModuleModel();
//        when(
//                kProjectEditorContentHandler.toModel( "blaaXML" )
//            ).thenReturn(
//                original
//                        );
//
//        KModuleModel loaded = service.loadKProject( path );
//
//        assertEquals( original, loaded );
//    }
//
//    @Test
//    public void testSaveKPRoject() throws Exception {
//        Path path = messagesEvent( Path.class );
//        KModuleModel kProjectModel = new KModuleModel();
//
//        when(
//                kProjectEditorContentHandler.toString( kProjectModel )
//            ).thenReturn(
//                "Here I am, tadaa!"
//                        );
//
//        service.saveKProject( path, kProjectModel );
//
//        verify( ioService ).write( path, "Here I am, tadaa!" );
//    }
//
//    @Test
//    public void testLoadGav() throws Exception {
//
//        Path path = messagesEvent( Path.class );
//        when(
//                ioService.readAllString( path )
//            ).thenReturn(
//                "someXML"
//                        );
//
//        GroupArtifactVersionModel original = new GroupArtifactVersionModel();
//        when(
//                groupArtifactVersionModelContentHandler.toModel( "someXML" )
//            ).thenReturn(
//                original
//                        );
//
//        GroupArtifactVersionModel loaded = service.loadGav( path );
//
//        assertEquals( original, loaded );
//    }
//
//    @Test
//    public void testSaveGAV() throws Exception {
//        Path path = messagesEvent( Path.class );
//        GroupArtifactVersionModel gavModel = new GroupArtifactVersionModel();
//
//        when(
//                groupArtifactVersionModelContentHandler.toString( gavModel )
//            ).thenReturn(
//                "Howdy!"
//                        );
//
//        service.saveGav( path, gavModel );
//
//        verify( ioService ).write( path, "Howdy!" );
//    }
//
//    @Test
//    public void testCheckIfKProjectExists() throws Exception {
//        Path path = PathFactory.newPath( "file://project/pom.xml" );
//
//        when(
//                ioService.exists( argThat( new PathMatcher( "file://project/src/main/resources/META-INF/kproject.xml" ) ) )
//            ).thenReturn(
//                true
//                        );
//
//        Path result = service.pathToRelatedKProjectFileIfAny( path );
//        assertNotNull( result );
//        assertEquals( "file://project/src/main/resources/META-INF/kproject.xml", result.toURI() );
//    }
//
//    @Test
//    public void testCheckIfKProjectExistsWhenItDoesNot() throws Exception {
//        Path path = PathFactory.newPath( "file://secondproject/pom.xml" );
//
//        when(
//                ioService.exists( argThat( new PathMatcher( "file://secondproject/src/main/resources/META-INF/kproject.xml" ) ) )
//            ).thenReturn(
//                false
//                        );
//
//        assertNull( service.pathToRelatedKProjectFileIfAny( path ) );
//    }
//
    private void assertContains(String uri,
                                List<org.kie.commons.java.nio.file.Path> allValues) {
        boolean contains = false;
        for (org.kie.commons.java.nio.file.Path path : allValues) {
            if (uri.equals(path.toUri())) {
                contains = true;
                break;
            }
        }

        assertTrue("Values should contain " + uri, contains);
    }
//
//    class PathMatcher extends ArgumentMatcher<Path> {
//
//        private final String uri;
//
//        PathMatcher( String uri ) {
//            this.uri = uri;
//        }
//
//        public boolean matches( Object path ) {
//            return ( (Path) path ).toURI().matches( uri );
//        }
//    }
}
