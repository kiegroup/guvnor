package org.drools.guvnor.server.util;
/*
 * Copyright 2005 JBoss Inc
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



import java.lang.reflect.Method;

import junit.framework.TestCase;

public class TableDisplayHandlerTest extends TestCase {

    public void testRowLoaders() throws Exception {

        RowLoader loader = new RowLoader("rulelist");

        assertEquals(5, loader.getHeaders().length);
        String[] headers = loader.getHeaders();

        assertEquals("Name", headers[0]);
        assertEquals("Description", headers[1]);
        assertEquals("LastModified", headers[2]);
        assertEquals("Status", headers[3]);

        assertEquals(5, loader.extractors.size());
        assertTrue(loader.extractors.get( 0 ) instanceof Method);
        assertEquals("getLastModified", ((Method)loader.extractors.get( 2 )).getName());
    }
}