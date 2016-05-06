/*
 * Copyright 2016 JBoss by Red Hat.
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

package org.guvnor.inbox.backend.server;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Paths;

@RunWith(MockitoJUnitRunner.class)
public class InboxPageRowBuilderTest {

    @Mock
    private IOService ioService;
    @Mock
    private Path convertedPath;
    @Mock
    private org.uberfire.java.nio.file.Path nioPath;
    @InjectMocks
    private InboxPageRowBuilder rowBuilder;

    private static final String TEST_PATH = "fakepath";
    private static URI TEST_URI;

    @BeforeClass
    public static void setup() throws URISyntaxException {
        TEST_URI = new URI(TEST_PATH);
    }

    @Test
    public void makePath_null() {
        when(ioService.get(null)).then(new Answer<org.uberfire.java.nio.file.Path>() {
            @Override
            public org.uberfire.java.nio.file.Path answer(InvocationOnMock invocation) throws Throwable {
                return Paths.get(null);
            }
        });

        assertNull(rowBuilder.makePath(null));
    }

    @Test
    public void makePath_nonexistentPath() {
        when(ioService.get(TEST_URI)).thenThrow(FileSystemNotFoundException.class);

        assertNull(rowBuilder.makePath(TEST_PATH));
    }

    @Test
    public void makePath_correctPath() {
        when(ioService.get(TEST_URI)).thenReturn(nioPath);
        when(nioPath.getFileName()).thenReturn(nioPath);
        when(nioPath.toUri()).thenReturn(TEST_URI);
        when(nioPath.getFileSystem()).thenReturn(mock(FileSystem.class));

        assertNotNull(rowBuilder.makePath(TEST_PATH));
    }
}
