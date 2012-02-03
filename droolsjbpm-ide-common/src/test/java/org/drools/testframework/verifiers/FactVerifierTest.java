package org.drools.testframework.verifiers;

import org.drools.Cheese;
import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.testframework.MockWorkingMemory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/*
* Copyright 2011 JBoss Inc
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
public class FactVerifierTest {

    @Test
    public void testVerifyAnonymousFacts() throws Exception {

        MockWorkingMemory workingMemory = new MockWorkingMemory();

        Cheese c = new Cheese();
        c.setPrice(42);
        c.setType("stilton");

        workingMemory.facts.add(c);

        VerifyFact vf = new VerifyFact("Cheese",
                new ArrayList(),
                true);
        vf.getFieldValues().add(new VerifyField("price",
                "42",
                "=="));
        vf.getFieldValues().add(new VerifyField("type",
                "stilton",
                "=="));

        TypeResolver typeResolver = mock(TypeResolver.class);

        FactVerifier factVerifier = new FactVerifier(new HashMap<String, Object>(), typeResolver, workingMemory, new HashMap<String, Object>());

        factVerifier.verify(vf);
        assertTrue(vf.wasSuccessful());

        vf = new VerifyFact("Person",
                new ArrayList(),
                true);
        vf.getFieldValues().add(new VerifyField("age",
                "42",
                "=="));

        factVerifier.verify(vf);
        assertFalse(vf.wasSuccessful());

        vf = new VerifyFact("Cheese",
                new ArrayList(),
                true);
        vf.getFieldValues().add(new VerifyField("price",
                "43",
                "=="));
        vf.getFieldValues().add(new VerifyField("type",
                "stilton",
                "=="));

        factVerifier.verify(vf);
        assertFalse(vf.wasSuccessful());
        assertEquals(Boolean.FALSE,
                vf.getFieldValues().get(0).getSuccessResult());

        vf = new VerifyFact("Cell",
                new ArrayList(),
                true);
        vf.getFieldValues().add(new VerifyField("value",
                "43",
                "=="));

        factVerifier.verify(vf);
        assertFalse(vf.wasSuccessful());
        assertEquals(Boolean.FALSE,
                vf.getFieldValues().get(0).getSuccessResult());

    }
}
