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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DSLSourceServiceTest {

    private DSLSourceService dslSourceService;
    private IOService ioService;

    @Before
    public void setUp() throws Exception {
        ioService = mock(IOService.class);
        dslSourceService = new DSLSourceService(ioService);
    }


    @Test
    public void testAlreadyHasPackage() throws Exception {
        Path path = mock(Path.class);
        when(
                path.toUri()
        ).thenReturn(
                new URI("/src/main/resources/org/test/myfile.drl")
        );


        when(
                ioService.readAllString(path)
        ).thenReturn(
                "package org.test"
        );

        assertEquals("package org.test", toString(dslSourceService.getSource(path).getInputSteam()));
    }

    @Test
    public void testAddPackage() throws Exception {
        Path path = mock(Path.class);
        when(
                path.toUri()
        ).thenReturn(
                new URI("/src/main/resources/org/test/myfile.drl")
        );


        when(
                ioService.readAllString(path)
        ).thenReturn(
                "something"
        );

        assertEquals("package org.test\nsomething", toString(dslSourceService.getSource(path).getInputSteam()));
    }

    private String toString(InputStream inputSteam) throws IOException {
        return CharStreams.toString(new InputStreamReader(inputSteam, Charsets.UTF_8));
    }
}
