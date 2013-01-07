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

package org.kie.guvnor.builder;

import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.source.SourceService;

import javax.enterprise.inject.Instance;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SourceServicesImplTest {

    private Instance instance;
    private ArrayList<SourceService> list;

    @Before
    public void setUp() throws Exception {
        instance = mock(Instance.class);

        list = new ArrayList<SourceService>();

    }

    @Test
    public void testSomethingSimple() throws Exception {

        addToList(getSourceService(".drl"));

        assertTrue(new SourceServicesImpl(instance).hasServiceFor("myFile.drl"));
    }

    @Test
    public void testMissing() throws Exception {

        addToList(getSourceService(".notHere"));

        assertFalse(new SourceServicesImpl(instance).hasServiceFor("myFile.drl"));
    }

    @Test
    public void testShorter() throws Exception {
        SourceService DRL = getSourceService(".drl");
        SourceService modelDRL = getSourceService(".model.drl");
        addToList(DRL, modelDRL);

        assertEquals(DRL, new SourceServicesImpl(instance).getServiceFor("myFile.drl"));

        list.clear();

        addToList(modelDRL, DRL);

        assertEquals(DRL, new SourceServicesImpl(instance).getServiceFor("myFile.drl"));

    }


    @Test
    public void testLonger() throws Exception {
        SourceService DRL = getSourceService(".drl");
        SourceService modelDRL = getSourceService(".model.drl");
        addToList(DRL, modelDRL);

        assertEquals(modelDRL, new SourceServicesImpl(instance).getServiceFor("myFile.model.drl"));

        list.clear();

        addToList(modelDRL, DRL);

        assertEquals(modelDRL, new SourceServicesImpl(instance).getServiceFor("myFile.model.drl"));

    }

    private SourceService getSourceService(final String extension) {
        return new SourceService() {
            @Override
            public String getSupportedFileExtension() {
                return extension;
            }

            @Override
            public String getSource(Path path) {
                return null;
            }
        };
    }

    private void addToList(SourceService... services) {

        for (SourceService service : services) {
            list.add(service);
        }

        when(
                instance.iterator()
        ).thenReturn(
                list.iterator()
        );
    }
}
