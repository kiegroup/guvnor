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
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectEditorServiceImplTest {

    private VFSService vfsService;
    private ProjectEditorServiceImpl service;

    @Before
    public void setUp() throws Exception {
        vfsService = mock(VFSService.class);
        service = new ProjectEditorServiceImpl(vfsService);
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

        assertEquals("file//myproject/src/resources/META-INF/kproject.xml", filePathArgumentCaptor.getValue().toURI());
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
}
