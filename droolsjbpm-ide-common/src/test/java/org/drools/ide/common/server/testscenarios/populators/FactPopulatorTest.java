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

import org.drools.*;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.server.testscenarios.MockWorkingMemory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class FactPopulatorTest {

    static {
        try {
            Class.forName("org.drools.base.mvel.MVELCompilationUnit");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private MockWorkingMemory workingMemory;
    private Map<String, Object> populatedData;
    private FactPopulator factPopulator;

    @Before
    public void setUp() throws Exception {
        workingMemory = new MockWorkingMemory();
        populatedData = new HashMap<String, Object>();
        factPopulator = new FactPopulator(workingMemory, populatedData);
    }

    @Test
    public void testPopulateFacts() throws Exception {

        FactData factData = new FactData(
                "Person",
                "p1",
                Arrays.<Field>asList(
                        new FieldData(
                                "name",
                                "mic"),
                        new FieldData(
                                "age",
                                "=30 + 3")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, getTypeResolver(), factData));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("p1"));

        Person person = (Person) populatedData.get("p1");
        assertEquals("mic",
                person.getName());
        assertEquals(33,
                person.getAge());
    }

    @Test
    public void testPopulateEnum() throws Exception {

        FieldData fieldData = new FieldData(
                "cheeseType",
                "CheeseType.CHEDDAR");
        fieldData.setNature(FieldData.TYPE_ENUM,
                null);
        FactData factData = new FactData("Cheese",
                "c1",
                asList((Field) fieldData),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, getTypeResolver(), factData));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));

        Cheese cheese = (Cheese) populatedData.get("c1");
        assertEquals(CheeseType.CHEDDAR, cheese.getCheeseType());
    }

    @Test
    public void testPopulateNested() throws Exception {

        TypeResolver typeResolver = getTypeResolver();

        FactData cheeseFactData = new FactData(
                "Cheese",
                "c1",
                Arrays.<Field>asList(
                        new FieldData(
                                "type",
                                "cheddar"),
                        new FieldData(
                                "price",
                                "42")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, cheeseFactData));

        FactData outerFactData = new FactData(
                "OuterFact",
                "p1",
                Arrays.<Field>asList(
                        new FieldData(
                                "name",
                                "mic"),
                        new FieldData(
                                "innerFact",
                                "=c1")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, outerFactData));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));
        assertTrue(populatedData.containsKey("p1"));

        OuterFact o = (OuterFact) populatedData.get("p1");
        assertEquals(populatedData.get("c1"), o.getInnerFact());
    }

    @Test
    public void testPopulateNestedWrongOrder() throws Exception {

        TypeResolver typeResolver = getTypeResolver();

        FactData outerFactData = new FactData(
                "OuterFact",
                "p1",
                Arrays.<Field>asList(
                        new FieldData(
                                "name",
                                "mic"),
                        new FieldData(
                                "innerFact",
                                "=c1")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, outerFactData));

        FactData cheeseFactData = new FactData(
                "Cheese",
                "c1",
                Arrays.<Field>asList(
                        new FieldData(
                                "type",
                                "cheddar"),
                        new FieldData(
                                "price",
                                "42")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, cheeseFactData));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));
        assertTrue(populatedData.containsKey("p1"));

        OuterFact o = (OuterFact) populatedData.get("p1");
        assertEquals(populatedData.get("c1"), o.getInnerFact());
    }

    @Test
    public void testPopulateEmpty() throws Exception {

        factPopulator.add(
                new NewFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData("Cheese",
                                "c1",
                                new ArrayList(),
                                false)));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));
        assertTrue(populatedData.get("c1") instanceof Cheese);
    }

    @Test
    public void testPopulatingExistingFact() throws Exception {
        Cheese cheese = new Cheese();
        cheese.setType("whee");
        cheese.setPrice(1);

        Map<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put(
                "x",
                cheese);

        factPopulator.add(
                new ExistingFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData(
                                "Cheese",
                                "x",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "type",
                                                null),
                                        new FieldData(
                                                "price",
                                                "42")),
                                false)));

        factPopulator.populate();

        assertEquals("whee", cheese.getType());
        assertEquals(42, cheese.getPrice());
    }

    @Test
    public void testDateField() throws Exception {

        factPopulator.add(
                new NewFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData(
                                "Cheese",
                                "c1",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "type",
                                                "cheddar"),
                                        new FieldData(
                                                "usedBy",
                                                "10-Jul-2008")),
                                false)));
        factPopulator.add(
                new NewFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData(
                                "OuterFact",
                                "p1",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "name",
                                                "mic"),
                                        new FieldData(
                                                "innerFact",
                                                "=c1")),
                                false)));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));
        assertTrue(populatedData.containsKey("p1"));

        Cheese c = (Cheese) populatedData.get("c1");
        assertNotNull(c.getUsedBy());

    }

    @Test
    public void testPopulateFactsWithExpressions() throws Exception {

        factPopulator.add(
                new NewFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData("Cheese",
                                "c1",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "type",
                                                "cheddar"),
                                        new FieldData(
                                                "price",
                                                "42")),
                                false)));
        factPopulator.add(
                new NewFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData(
                                "Cheese",
                                "c2",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "type",
                                                "= c1.type")),
                                false)));

        factPopulator.populate();

        assertTrue(populatedData.containsKey("c1"));
        assertTrue(populatedData.containsKey("c2"));

        Cheese c = (Cheese) populatedData.get("c1");
        assertEquals("cheddar", c.getType());
        assertEquals(42, c.getPrice());

        Cheese c2 = (Cheese) populatedData.get("c2");
        assertEquals(c.getType(), c2.getType());
    }

    @Test
    public void testPopulateEmptyString() throws Exception {
        Cheese cheese = new Cheese();
        cheese.setType("whee");
        cheese.setPrice(1);
        populatedData.put("x", cheese);

        assertEquals(1, cheese.getPrice());

        //An empty String is a 'value' as opposed to null
        factPopulator.add(
                new ExistingFactPopulator(
                        populatedData,
                        getTypeResolver(),
                        new FactData(
                                "Cheese",
                                "x",
                                Arrays.<Field>asList(
                                        new FieldData(
                                                "type",
                                                ""),
                                        new FieldData(
                                                "price",
                                                "42")),
                                false)));

        factPopulator.populate();

        assertEquals("", cheese.getType());
        assertEquals(42, cheese.getPrice());
    }

    @Test
    public void testCollectionFieldInFacts() throws Exception {

        TypeResolver typeResolver = getTypeResolver();

        FactData fd1 = new FactData("Cheese",
                "f1",
                Arrays.<Field>asList(
                        new FieldData("type",
                                ""),
                        new FieldData("price",
                                "42")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, fd1));

        FactData fd2 = new FactData("Cheese",
                "f2",
                Arrays.<Field>asList(
                        new FieldData("type",
                                ""),
                        new FieldData("price",
                                "43")),
                false);
        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, fd2));

        FactData fd3 = new FactData("Cheese",
                "f3",
                Arrays.<Field>asList(
                        new FieldData("type",
                                ""),
                        new FieldData("price",
                                "45")),
                false);

        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, fd3));

        FieldData field = new FieldData();
        field.setName("cheeses");
        field.collectionType = "Cheese";
        field.setNature(FieldData.TYPE_COLLECTION);
        field.setValue("=[f1,f2,f3]");
        List<Field> lstField = new ArrayList<Field>();
        lstField.add(field);
        FactData lst = new FactData("Cheesery",
                "listChesse",
                lstField,
                false);
        factPopulator.add(new NewFactPopulator(populatedData, typeResolver, lst));

        factPopulator.populate();


        Cheesery listChesse = (Cheesery) populatedData.get("listChesse");
        Cheese f1 = (Cheese) populatedData.get("f1");
        Cheese f2 = (Cheese) populatedData.get("f2");
        Cheese f3 = (Cheese) populatedData.get("f3");

        assertEquals(3, listChesse.getCheeses().size());
        assertTrue(listChesse.getCheeses().contains(f1));
        assertTrue(listChesse.getCheeses().contains(f2));
        assertTrue(listChesse.getCheeses().contains(f3));

    }


    private TypeResolver getTypeResolver() {
        TypeResolver resolver = new ClassTypeResolver(
                new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());

        resolver.addImport("org.drools.Cheesery");
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.CheeseType");
        resolver.addImport("org.drools.Person");
        resolver.addImport("org.drools.OuterFact");

        return resolver;
    }
}
