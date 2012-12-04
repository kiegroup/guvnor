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
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectEditorServiceImplTest {

    private VFSService vfsService;
    private ProjectEditorServiceImpl service;
    private KProjectEditorContentHandler kProjectEditorContentHandler;
    private GroupArtifactVersionModelContentHandler groupArtifactVersionModelContentHandler;

    @Before
    public void setUp() throws Exception {
        vfsService = mock(VFSService.class);
        kProjectEditorContentHandler = mock(KProjectEditorContentHandler.class);
        groupArtifactVersionModelContentHandler = mock(GroupArtifactVersionModelContentHandler.class);
        service = new ProjectEditorServiceImpl(vfsService, kProjectEditorContentHandler, groupArtifactVersionModelContentHandler);
    }

    @Test
    public void testSetUpProjectStructure() throws Exception {
        Path pathToPom = new PathImpl("file://myproject/pom.xml");

        service.setUpProjectStructure(pathToPom);

        ArgumentCaptor<Path> folderPathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        verify(vfsService, times(4)).createDirectory(folderPathArgumentCaptor.capture());

        assertContains("file://myproject/src/kbases", folderPathArgumentCaptor.getAllValues());
        assertContains("file://myproject/src/main/java", folderPathArgumentCaptor.getAllValues());
        assertContains("file://myproject/src/test/java", folderPathArgumentCaptor.getAllValues());
        assertContains("file://myproject/src/test/resources", folderPathArgumentCaptor.getAllValues());

        ArgumentCaptor<Path> filePathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        verify(vfsService).write(filePathArgumentCaptor.capture(), anyString());

        assertEquals("file://myproject/src/main/resources/META-INF/kproject.xml", filePathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void testLoadKProject() throws Exception {

        Path path = mock(Path.class);
        when(
                vfsService.readAllString(path)
        ).thenReturn(
                "blaaXML"
        );

        KProjectModel original = new KProjectModel();
        when(
                kProjectEditorContentHandler.toModel("blaaXML")
        ).thenReturn(
                original
        );

        KProjectModel loaded = service.loadKProject(path);

        assertEquals(original, loaded);
    }

    @Test
    public void testSaveKPRoject() throws Exception {
        Path path = mock(Path.class);
        KProjectModel kProjectModel = new KProjectModel();

        when(
                kProjectEditorContentHandler.toString(kProjectModel)
        ).thenReturn(
                "Here I am, tadaa!"
        );

        service.saveKProject(path, kProjectModel);

        verify(vfsService).write(path, "Here I am, tadaa!");
    }

    @Test
    public void testLoadGav() throws Exception {

        Path path = mock(Path.class);
        when(
                vfsService.readAllString(path)
        ).thenReturn(
                "someXML"
        );

        GroupArtifactVersionModel original = new GroupArtifactVersionModel();
        when(
                groupArtifactVersionModelContentHandler.toModel("someXML")
        ).thenReturn(
                original
        );

        GroupArtifactVersionModel loaded = service.loadGav(path);

        assertEquals(original, loaded);
    }

    @Test
    public void testSaveGAV() throws Exception {
        Path path = mock(Path.class);
        GroupArtifactVersionModel gavModel = new GroupArtifactVersionModel();

        when(
                groupArtifactVersionModelContentHandler.toString(gavModel)
        ).thenReturn(
                "Howdy!"
        );

        service.saveGav(path, gavModel);

        verify(vfsService).write(path, "Howdy!");
    }

    @Test
    public void testCheckIfKProjectExists() throws Exception {
        Path path = new PathImpl("file://project/pom.xml");

        when(
                vfsService.exists(argThat(new PathMatcher("file://project/src/main/resources/META-INF/kproject.xml")))
        ).thenReturn(
                true
        );

        Path result = service.pathToRelatedKProjectFileIfAny(path);
        assertNotNull(result);
        assertEquals("file://project/src/main/resources/META-INF/kproject.xml", result.toURI());
    }

    @Test
    public void testCheckIfKProjectExistsWhenItDoesNot() throws Exception {
        Path path = new PathImpl("file://secondproject/pom.xml");

        when(
                vfsService.exists(argThat(new PathMatcher("file://secondproject/src/main/resources/META-INF/kproject.xml")))
        ).thenReturn(
                false
        );

        assertNull(service.pathToRelatedKProjectFileIfAny(path));
    }


    private void assertContains(String uri, List<Path> allValues) {
        boolean contains = false;
        for (Path path : allValues) {
            if (uri.equals(path.toURI())) {
                contains = true;
                break;
            }
        }

        assertTrue("Values should contain " + uri, contains);
    }

    class PathMatcher extends ArgumentMatcher<Path> {

        private final String uri;

        PathMatcher(String uri) {
            this.uri = uri;
        }

        public boolean matches(Object path) {
            return ((Path) path).toURI().matches(uri);
        }
    }
}
