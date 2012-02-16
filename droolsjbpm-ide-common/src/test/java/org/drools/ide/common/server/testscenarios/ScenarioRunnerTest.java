/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.server.testscenarios;

import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.Expectation;
import org.drools.WorkingMemory;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ScenarioRunnerTest extends RuleUnit {

    @Test
    public void testPopulateFactsWithInterfaces() throws Exception {
        Scenario sc = new Scenario();
        List facts = ls(new FactData("List",
                "ls",
                new ArrayList(),
                false));

        List globals = ls(new FactData("List",
                "ls",
                new ArrayList(),
                false));
        sc.getFixtures().addAll(facts);
        sc.getGlobals().addAll(globals);
        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("java.util.List");

        ScenarioRunner runner = new ScenarioRunner(
                resolver,
                new MockWorkingMemory());
        runner.run(sc);

    }

    @Test
    public void testVerifyFacts() throws Exception {

        TypeResolver resolver = new ClassTypeResolver(
                new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.Person");

        Scenario scenario = new Scenario();

        ScenarioRunner runner = new ScenarioRunner(
                resolver,
                new MockWorkingMemory());

        scenario.getFixtures().add(
                new FactData(
                        "Cheese",
                        "f1",
                        asList(
                                new FieldData(
                                        "type",
                                        "cheddar"),
                                new FieldData(
                                        "price",
                                        "42")),
                        false
                ));

        scenario.getFixtures().add(
                new FactData(
                        "Person",
                        "f2",
                        asList(
                                new FieldData(
                                        "name",
                                        "michael"),
                                new FieldData(
                                        "age",
                                        "33")),
                        false
                ));

        // test all true
        VerifyFact verifyCheddar = new VerifyFact();
        verifyCheddar.setName("f1");
        verifyCheddar.setFieldValues(
                asList(
                        new VerifyField(
                                "type",
                                "cheddar",
                                "=="),
                        new VerifyField(
                                "price",
                                "42",
                                "==")));

        scenario.getFixtures().add(verifyCheddar);

        VerifyFact michaelVerifyFact = new VerifyFact();
        michaelVerifyFact.setName("f2");
        michaelVerifyFact.setFieldValues(
                asList(
                        new VerifyField(
                                "name",
                                "michael",
                                "=="),
                        new VerifyField(
                                "age",
                                "33",
                                "==")));

        scenario.getFixtures().add(michaelVerifyFact);

        // test one false
        VerifyFact markVerifyFact = new VerifyFact();
        markVerifyFact.setName("f2");
        markVerifyFact.setFieldValues(
                asList(
                        new VerifyField(
                                "name",
                                "mark",
                                "=="),
                        new VerifyField(
                                "age",
                                "33",
                                "==")));

        scenario.getFixtures().add(markVerifyFact);

        // test 2 false
        VerifyFact mark2VerifyFact = new VerifyFact();
        mark2VerifyFact.setName("f2");
        mark2VerifyFact.setFieldValues(
                asList(
                        new VerifyField(
                                "name",
                                "mark",
                                "=="),
                        new VerifyField(
                                "age",
                                "32",
                                "==")));

        scenario.getFixtures().add(mark2VerifyFact);

        runner.run(scenario);

        for (VerifyField verifyField : verifyCheddar.getFieldValues()) {
            assertTrue(verifyField.getSuccessResult());
        }

        for (VerifyField verifyField : michaelVerifyFact.getFieldValues()) {
            assertTrue(verifyField.getSuccessResult());
        }

        assertFalse((markVerifyFact.getFieldValues().get(0)).getSuccessResult());
        assertTrue((markVerifyFact.getFieldValues().get(1)).getSuccessResult());

        assertEquals("michael", markVerifyFact.getFieldValues().get(0).getActualResult());
        assertEquals("mark", markVerifyFact.getFieldValues().get(0).getExpected());

        assertFalse((mark2VerifyFact.getFieldValues().get(0)).getSuccessResult());
        assertFalse((mark2VerifyFact.getFieldValues().get(1)).getSuccessResult());

        assertEquals("michael", mark2VerifyFact.getFieldValues().get(0).getActualResult());
        assertEquals("mark", mark2VerifyFact.getFieldValues().get(0).getExpected());

        assertEquals("33", mark2VerifyFact.getFieldValues().get(1).getActualResult());
        assertEquals("32", mark2VerifyFact.getFieldValues().get(1).getExpected());
    }

    @Test
    public void testVerifyFactsWithEnum() throws Exception {
        FieldData fieldData = new FieldData(
                "cheeseType",
                "CheeseType.CHEDDAR");
        fieldData.setNature(FieldData.TYPE_ENUM,
                null);
        FactData cheeseFactData = new FactData(
                "Cheese",
                "c1",
                asList(fieldData),
                false);

        FieldData cheeseType = new FieldData(
                "cheeseType",
                "CheeseType.CHEDDAR"
        );
        cheeseType.setNature(FieldData.TYPE_ENUM, null);
        FactData f1 = new FactData(
                "Cheese",
                "f1",
                asList(cheeseType),
                false
        );

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.CheeseType");

        Scenario scenario = new Scenario();
        scenario.getFixtures().add(cheeseFactData);
        scenario.getFixtures().add(f1);

        ScenarioRunner runner = new ScenarioRunner(
                resolver,
                new MockWorkingMemory());

        VerifyFact vf = new VerifyFact();
        vf.setName("f1");
        VerifyField verifyField = new VerifyField(
                "cheeseType",
                "CheeseType.CHEDDAR",
                "==");
        verifyField.setNature(VerifyField.TYPE_ENUM);
        vf.setFieldValues(ls(verifyField));
        scenario.getFixtures().add(vf);

        runner.run(scenario);

        for (VerifyField field : vf.getFieldValues()) {
            assertTrue(field.getSuccessResult());
        }
    }


    @Test
    public void testTestingEventListener() throws Exception {
        Scenario sc = new Scenario();
        sc.getRules().add("foo");
        sc.getRules().add("bar");
        ExecutionTrace ext = new ExecutionTrace();

        sc.getFixtures().add(ext);

        MockWorkingMemory wm = new MockWorkingMemory();
        PseudoClockScheduler clock = new PseudoClockScheduler();
        long time = new Date().getTime();
        clock.setStartupTime(time);
        clock.setSession(wm);
        wm.setSessionClock(clock);

        ScenarioRunner run = new ScenarioRunner(
                null,
                wm);
        run.run(sc);
        assertNotNull(wm.agendaEventListener);
        assertTrue(wm.agendaEventListener instanceof TestingEventListener);
        assertEquals(2,
                sc.getRules().size());
        assertTrue(sc.getRules().contains("foo"));
        assertTrue(sc.getRules().contains("bar"));
    }


    /**
     * Check if global list is empty.
     */
    @Test
    public void testWithGlobalList() throws Exception {
        Scenario sc = new Scenario();
        sc.getGlobals().add(new FactData("List",
                "testList",
                new ArrayList(),
                false));

        Expectation[] assertions = new Expectation[2];

        assertions[0] = new VerifyFact("testList",
                ls(new VerifyField("empty",
                        "true",
                        "==")));
        assertions[1] = new VerifyFact("testList",
                ls(new VerifyField("size",
                        "0",
                        "==")));

        sc.getFixtures().addAll(Arrays.asList(assertions));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("java.util.List");

        MockWorkingMemory wm = new MockWorkingMemory();
        ScenarioRunner run = new ScenarioRunner(
                resolver,
                wm);
        run.run(sc);

        List testList = (List) wm.globals.get("testList");
        assertTrue(testList.isEmpty());
        assertEquals(0,
                testList.size());
    }

    @SuppressWarnings("deprecation")
    // F**** dates in java. What a mess. Someone should die.
    @Test
    public void testSimulatedDate() throws Exception {
        Scenario sc = new Scenario();
        MockWorkingMemory wm = new MockWorkingMemory();
        PseudoClockScheduler clock = new PseudoClockScheduler();
        long time = new Date().getTime();
        clock.setStartupTime(time);
        clock.setSession(wm);
        wm.setSessionClock(clock);
        ScenarioRunner run = new ScenarioRunner(
                null,
                wm);
        run.run(sc);

        assertEquals(time,
                wm.getSessionClock().getCurrentTime());

        ExecutionTrace ext = new ExecutionTrace();
        ext.setScenarioSimulatedDate(new Date("10-Jul-1974"));
        sc.getFixtures().add(ext);
        run = new ScenarioRunner(
                null,
                wm);
        run.run(sc);

        long expected = ext.getScenarioSimulatedDate().getTime();
        assertEquals(expected,
                wm.getSessionClock().getCurrentTime());
        //        Thread.sleep( 50 );
        //        assertEquals( expected,
        //                      tm.getNow().getTimeInMillis() );

    }


    /**
     * Do a kind of end to end test with some real rules.
     */
    @Test
    public void testIntegrationWithSuccess() throws Exception {

        Scenario sc = new Scenario();
        FactData[] facts = new FactData[]{new FactData("Cheese",
                "c1",
                ls(new FieldData("type",
                        "cheddar"),
                        new FieldData("price",
                                "42")),
                false)

        };
        sc.getGlobals().add(new FactData("Person",
                "p",
                new ArrayList(),
                false));
        sc.getFixtures().addAll(Arrays.asList(facts));

        ExecutionTrace executionTrace = new ExecutionTrace();

        sc.getRules().add("rule1");
        sc.getRules().add("rule2");
        sc.setInclusive(true);
        sc.getFixtures().add(executionTrace);

        Expectation[] assertions = new Expectation[5];

        assertions[0] = new VerifyFact("c1",
                ls(new VerifyField("type",
                        "cheddar",
                        "==")

                ));

        assertions[1] = new VerifyFact("p",
                ls(new VerifyField("name",
                        "rule1",
                        "=="),
                        new VerifyField("status",
                                "rule2",
                                "=="))

        );

        assertions[2] = new VerifyRuleFired("rule1",
                1,
                null);
        assertions[3] = new VerifyRuleFired("rule2",
                1,
                null);
        assertions[4] = new VerifyRuleFired("rule3",
                0,
                null);

        sc.getFixtures().addAll(Arrays.asList(assertions));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules2.drl");

        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);
        run.run(sc);

        assertEquals(2,
                executionTrace.getNumberOfRulesFired().intValue());


        assertTrue(sc.wasSuccessful());

        Thread.sleep(50);

        assertTrue((new Date()).after(sc.getLastRunResult()));
        assertTrue(executionTrace.getExecutionTimeResult() != null);

        assertTrue(executionTrace.getRulesFired().length > 0);

    }

    @Test
    public void testIntegrationInfiniteLoop() throws Exception {

        Scenario sc = new Scenario();
        FactData[] facts = new FactData[]{new FactData("Cheese",
                "c1",
                ls(new FieldData("type",
                        "cheddar"),
                        new FieldData("price",
                                "42")),
                false)

        };
        sc.getGlobals().add(new FactData("Person",
                "p",
                new ArrayList(),
                false));
        sc.getFixtures().addAll(Arrays.asList(facts));

        ExecutionTrace executionTrace = new ExecutionTrace();

        sc.getRules().add("rule1");
        sc.getRules().add("rule2");
        sc.setInclusive(true);
        sc.getFixtures().add(executionTrace);

        Expectation[] assertions = new Expectation[5];

        assertions[0] = new VerifyFact("c1",
                ls(new VerifyField("type",
                        "cheddar",
                        "==")

                ));

        assertions[1] = new VerifyFact("p",
                ls(new VerifyField("name",
                        "rule1",
                        "=="),
                        new VerifyField("status",
                                "rule2",
                                "=="))

        );

        assertions[2] = new VerifyRuleFired("rule1",
                1,
                null);
        assertions[3] = new VerifyRuleFired("rule2",
                1,
                null);
        assertions[4] = new VerifyRuleFired("rule3",
                0,
                null);

        sc.getFixtures().addAll(Arrays.asList(assertions));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules_infinite_loop.drl");

        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);
        run.run(sc);

        assertEquals(sc.getMaxRuleFirings(),
                executionTrace.getNumberOfRulesFired().intValue());

    }

    @Test
    public void testIntegrationWithDeclaredTypes() throws Exception {
        Scenario scenario = new Scenario();
        FactData[] facts = new FactData[]{new FactData("Coolness",
                "c",
                ls(new FieldData("num",
                        "42"),
                        new FieldData("name",
                                "mic")),
                false)

        };
        scenario.getFixtures().addAll(Arrays.asList(facts));

        ExecutionTrace executionTrace = new ExecutionTrace();

        scenario.getRules().add("rule1");
        scenario.setInclusive(true);
        scenario.getFixtures().add(executionTrace);

        Expectation[] assertions = new Expectation[2];

        assertions[0] = new VerifyFact("c",
                ls(new VerifyField("num",
                        "42",
                        "==")

                ));

        assertions[1] = new VerifyRuleFired("rule1",
                1,
                null);

        scenario.getFixtures().addAll(Arrays.asList(assertions));

        WorkingMemory workingMemory = getWorkingMemory("test_rules3.drl");
        ClassLoader cl = ((InternalRuleBase) workingMemory.getRuleBase()).getRootClassLoader();

        HashSet<String> imports = new HashSet<String>();
        imports.add("foo.bar.*");

        assertNotNull(cl.loadClass("foo.bar.Coolness"));

        ClassLoader cl_ = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);

        //resolver will need to have generated beans in it - possibly using a composite classloader from the package,
        //including whatever CL has the generated beans...
        ScenarioRunner run = new ScenarioRunner(
                new ClassTypeResolver(
                        imports,
                        cl),
                (InternalWorkingMemory) workingMemory);
        run.run(scenario);

        assertEquals(1,
                executionTrace.getNumberOfRulesFired().intValue());

        assertTrue(scenario.wasSuccessful());

        Thread.currentThread().setContextClassLoader(cl_);

    }

    @Test
    public void testRuleFlowGroupActivation() throws Exception {
        Scenario scenario = new Scenario();
        Fixture[] given = new Fixture[]{new FactData("Coolness",
                "c",
                ls(new FieldData("num",
                        "42"),
                        new FieldData("name",
                                "mic")),
                false)

        };
        scenario.getFixtures().addAll(Arrays.asList(given));

        ExecutionTrace executionTrace = new ExecutionTrace();

        scenario.getRules().add("rule1");
        scenario.setInclusive(true);
        scenario.getFixtures().add(executionTrace);

        Expectation[] assertions = new Expectation[2];

        assertions[0] = new VerifyFact("c",
                ls(new VerifyField("num",
                        "42",
                        "==")));

        assertions[1] = new VerifyRuleFired("rule1",
                1,
                null);

        scenario.getFixtures().addAll(Arrays.asList(assertions));

        WorkingMemory workingMemory = getWorkingMemory("rule_flow_actication.drl");
        ClassLoader classLoader = ((InternalRuleBase) workingMemory.getRuleBase()).getRootClassLoader();

        HashSet<String> imports = new HashSet<String>();
        imports.add("foo.bar.*");

        TypeResolver resolver = new ClassTypeResolver(imports,
                classLoader);

        Class<?> coolnessClass = classLoader.loadClass("foo.bar.Coolness");
        assertNotNull(coolnessClass);

        ClassLoader cl_ = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        //resolver will need to have generated beans in it - possibly using a composite classloader from the package,
        //including whatever CL has the generated beans...
        ScenarioRunner scenarioRunner = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) workingMemory);

        scenarioRunner.run(scenario);

        assertEquals(0,
                executionTrace.getNumberOfRulesFired().intValue());


        assertFalse(scenario.wasSuccessful());

        // Activate rule flow
        scenario.getFixtures().clear();
        given = new Fixture[]{new FactData("Coolness",
                "c",
                ls(new FieldData("num",
                        "42"),
                        new FieldData("name",
                                "mic")),
                false), new ActivateRuleFlowGroup("asdf")};
        workingMemory.clearAgenda();
        scenario.getFixtures().addAll(Arrays.asList(given));
        scenario.getFixtures().add(executionTrace);
        workingMemory.getAgenda().getRuleFlowGroup("asdf").setAutoDeactivate(false);
        scenarioRunner = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) workingMemory);

        scenarioRunner.run(scenario);

        assertEquals(1,
                executionTrace.getNumberOfRulesFired().intValue());


        assertTrue(scenario.wasSuccessful());

        Thread.currentThread().setContextClassLoader(cl_);
    }

    @Test
    public void testIntgerationStateful() throws Exception {
        Scenario sc = new Scenario();
        sc.getFixtures().add(new FactData("Cheese",
                "c1",
                ls(new FieldData("price",
                        "1")),
                false));
        ExecutionTrace ex = new ExecutionTrace();
        sc.getFixtures().add(ex);
        sc.getFixtures().add(new FactData("Cheese",
                "c2",
                ls(new FieldData("price",
                        "2")),
                false));
        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "rule1",
                        "=="))));
        ex = new ExecutionTrace();
        sc.getFixtures().add(ex);
        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "rule2",
                        "=="))));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");

        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);
        run.run(sc);

        assertTrue(sc.wasSuccessful());

    }

    @Test
    public void testIntegrationWithModify() throws Exception {
        Scenario sc = new Scenario();
        sc.getFixtures().add(new FactData("Cheese",
                "c1",
                ls(new FieldData("price",
                        "1")),
                false));

        sc.getFixtures().add(new ExecutionTrace());

        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "rule1",
                        "=="))));

        sc.getFixtures().add(new FactData("Cheese",
                "c1",
                ls(new FieldData("price",
                        "42")),
                true));
        sc.getFixtures().add(new ExecutionTrace());

        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "rule3",
                        "=="))));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");

        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);

        run.run(sc);

        assertTrue(sc.wasSuccessful());
    }

    @Test
    public void testIntegrationWithRetract() throws Exception {
        Scenario sc = new Scenario();
        sc.getFixtures().add(new FactData("Cheese",
                "c1",
                ls(new FieldData("price",
                        "46"),
                        new FieldData("type",
                                "XXX")),
                false));
        sc.getFixtures().add(new FactData("Cheese",
                "c2",
                ls(new FieldData("price",
                        "42")),
                false));
        sc.getFixtures().add(new ExecutionTrace());

        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "XXX",
                        "=="))));

        sc.getFixtures().add(new RetractFact("c2"));
        sc.getFixtures().add(new ExecutionTrace());

        sc.getFixtures().add(new VerifyFact("c1",
                ls(new VerifyField("type",
                        "rule4",
                        "=="))));

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");

        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);
        run.run(sc);

        assertTrue(sc.wasSuccessful());
    }

    @Test
    public void testIntegrationWithFailure() throws Exception {
        Scenario sc = new Scenario();
        Expectation[] assertions = populateScenarioForFailure(sc);

        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
                Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.Cheese");
        resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules2.drl");

        ScenarioRunner run = new ScenarioRunner(
                resolver,
                (InternalWorkingMemory) wm);

        run.run(sc);

        assertFalse(sc.wasSuccessful());

        VerifyFact vf = (VerifyFact) assertions[1];
        assertFalse((vf.getFieldValues().get(0)).getSuccessResult());
        assertEquals("XXX",
                vf.getFieldValues().get(0).getExpected());
        assertEquals("rule1",
                vf.getFieldValues().get(0).getActualResult());
        assertNotNull(vf.getFieldValues().get(0).getExplanation());

        VerifyRuleFired vr = (VerifyRuleFired) assertions[4];
        assertFalse(vr.getSuccessResult());

        assertEquals(2,
                vr.getExpectedCount().intValue());
        assertEquals(0,
                vr.getActualResult().intValue());

    }

    private Expectation[] populateScenarioForFailure(Scenario sc) {
        FactData[] facts = new FactData[]{new FactData("Cheese",
                "c1",
                ls(new FieldData("type",
                        "cheddar"),
                        new FieldData("price",
                                "42")),
                false)

        };
        sc.getFixtures().addAll(Arrays.asList(facts));
        sc.getGlobals().add(new FactData("Person",
                "p",
                new ArrayList(),
                false));

        ExecutionTrace executionTrace = new ExecutionTrace();
        sc.getRules().add("rule1");
        sc.getRules().add("rule2");
        sc.setInclusive(true);
        sc.getFixtures().add(executionTrace);

        Expectation[] assertions = new Expectation[5];

        assertions[0] = new VerifyFact("c1",
                ls(new VerifyField("type",
                        "cheddar",
                        "==")

                ));

        assertions[1] = new VerifyFact("p",
                ls(new VerifyField("name",
                        "XXX",
                        "=="),
                        new VerifyField("status",
                                "rule2",
                                "==")

                ));

        assertions[2] = new VerifyRuleFired("rule1",
                1,
                null);
        assertions[3] = new VerifyRuleFired("rule2",
                1,
                null);
        assertions[4] = new VerifyRuleFired("rule3",
                2,
                null);

        sc.getFixtures().addAll(Arrays.asList(assertions));
        return assertions;
    }

    private <T> List<T> ls
            (T... objects) {
        return Arrays.asList(objects);
    }

}
