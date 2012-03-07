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

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.server.testscenarios.MatryoshkaDoll;
import org.drools.ide.common.server.testscenarios.Mouse;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FactAssignmentFieldPopulatorTest {


    @Test
    public void testFactAssignmentField() throws Exception {
        TypeResolver typeResolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        typeResolver.addImport("org.drools.ide.common.server.testscenarios.Cheese");

        Mouse mouse = new Mouse();

        FactAssignmentField factAssignmentField = new FactAssignmentField("cheese", "Cheese");

        FactAssignmentFieldPopulator factAssignmentFieldPopulator = new FactAssignmentFieldPopulator(mouse, factAssignmentField, typeResolver);

        factAssignmentFieldPopulator.populate(new HashMap<String, Object>());

        assertNotNull(mouse.getCheese());
    }

    @Test
    public void testSimpleFields() throws Exception {
        TypeResolver typeResolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        typeResolver.addImport("org.drools.ide.common.server.testscenarios.Cheese");

        Mouse mouse = new Mouse();

        FactAssignmentField factAssignmentField = new FactAssignmentField("cheese", "Cheese");
        factAssignmentField.getFact().getFieldData().add(new FieldData("name", "Best cheddar EVER! (tm)"));

        FactAssignmentFieldPopulator factAssignmentFieldPopulator = new FactAssignmentFieldPopulator(mouse, factAssignmentField, typeResolver);

        factAssignmentFieldPopulator.populate(new HashMap<String, Object>());

        assertEquals("Best cheddar EVER! (tm)", mouse.getCheese().getName());
    }

    @Test
    public void testMatryoshkaDollSituation() throws Exception {
        TypeResolver typeResolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        typeResolver.addImport("org.drools.ide.common.server.testscenarios.MatryoshkaDoll");

        MatryoshkaDoll matryoshkaDoll = new MatryoshkaDoll();

        FactAssignmentField factAssignmentField = createFactAssignmentField();
        addFactAssignmentFieldIntoFactAssignmentField(factAssignmentField, 5);

        FactAssignmentFieldPopulator factAssignmentFieldPopulator = new FactAssignmentFieldPopulator(matryoshkaDoll, factAssignmentField, typeResolver);

        factAssignmentFieldPopulator.populate(new HashMap<String, Object>());

        assertNotNull(matryoshkaDoll.getMatryoshkaDoll());
        assertNotNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll());
        assertNotNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll());
        assertNotNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll());
        assertNotNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll());
        assertNotNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll());
        assertNull(matryoshkaDoll.getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll().getMatryoshkaDoll());
    }

    private void addFactAssignmentFieldIntoFactAssignmentField(FactAssignmentField factAssignmentField, int times) {
        if (times > 0) {
            FactAssignmentField innerFactAssignmentField = createFactAssignmentField();

            factAssignmentField.getFact().getFieldData().add(innerFactAssignmentField);

            addFactAssignmentFieldIntoFactAssignmentField(innerFactAssignmentField, --times);
        }
    }

    private FactAssignmentField createFactAssignmentField() {
        return new FactAssignmentField("matryoshkaDoll", "MatryoshkaDoll");
    }
}
