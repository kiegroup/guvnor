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

package org.drools.ide.common.server.testscenarios.populators;

import org.drools.FactHandle;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.server.testscenarios.MockWorkingMemory;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GlobalFactPopulatorTest {

    @Test
    public void testWithGlobals() throws Exception {

        FactData global = new FactData("Cheese",
                "c",
                Arrays.<Field>asList(new FieldData("type",
                        "cheddar")),
                false);

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");

        MockWorkingMemory wm = new MockWorkingMemory();
        Map<String, Object> populatedData = new HashMap<String, Object>();
        Map<String, Object> globalData = new HashMap<String, Object>();
        GlobalFactPopulator globalFactPopulator = new GlobalFactPopulator(populatedData,
                resolver,
                Thread.currentThread().getContextClassLoader(),
                global,
                globalData);

        globalFactPopulator.populate(wm, new HashMap<String, FactHandle>());

        assertEquals(1,
                wm.globals.size());
        assertEquals(1,
                globalData.size());
        assertEquals(0,
                populatedData.size());

    }

}
