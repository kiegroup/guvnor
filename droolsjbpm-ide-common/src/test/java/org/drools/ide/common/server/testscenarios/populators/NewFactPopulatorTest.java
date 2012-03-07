/*
 * Copyright 2011 JBoss Inc
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class NewFactPopulatorTest {

    @Test
    public void testDummyRunNoRules() throws Exception {
        TypeResolver typeResolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        typeResolver.addImport("org.drools.Cheese");

        HashMap<String, Object> populatedData = new HashMap<String, Object>();

        List<Field> fieldData = new ArrayList<Field>();
        fieldData.add(new FieldData("type",
                "cheddar"));
        fieldData.add(new FieldData("price",
                "42"));
        FactData fact = new FactData("Cheese",
                "c1",
                fieldData,
                false);

        NewFactPopulator newFactPopulator = new NewFactPopulator(
                populatedData,
                typeResolver,
                fact);

        MockWorkingMemory workingMemory = new MockWorkingMemory();
        newFactPopulator.populate(workingMemory, new HashMap<String, FactHandle>());

        assertTrue(populatedData.containsKey("c1"));
        assertNotNull(populatedData.get("c1"));
        assertEquals(populatedData.get("c1"),
                workingMemory.facts.get(0));

    }


}
