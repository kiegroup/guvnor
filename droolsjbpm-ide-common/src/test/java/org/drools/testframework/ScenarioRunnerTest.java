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

package org.drools.testframework;

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

    //
//
//    @Test
//    public void testVerifyFactsWithOperator() throws Exception {
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheese f1 = new Cheese("cheddar",
//                42);
//        runner.getPopulatedData().put("f1",
//                f1);
//
//        // test all true
//        VerifyFact vf = new VerifyFact();
//        vf.setName("f1");
//        vf.setFieldValues(ls(new VerifyField("type",
//                "cheddar",
//                "=="),
//                new VerifyField("price",
//                        "4777",
//                        "!=")));
//        runner.verify(vf);
//        for (int i = 0; i < vf.getFieldValues().size(); i++) {
//            assertTrue(((VerifyField) vf.getFieldValues().get(i)).getSuccessResult());
//        }
//
//        vf = new VerifyFact();
//        vf.setName("f1");
//        vf.setFieldValues(ls(new VerifyField("type",
//                "cheddar",
//                "!=")));
//        runner.verify(vf);
//        assertFalse(((VerifyField) vf.getFieldValues().get(0)).getSuccessResult());
//
//    }
//
//    @Test
//    public void testVerifyFactsWithExpression() throws Exception {
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheese f1 = new Cheese("cheddar",
//                42);
//        runner.getPopulatedData().put("f1",
//                f1);
//        f1.setPrice(42);
//        // test all true
//        VerifyFact vf = new VerifyFact();
//        vf.setName("f1");
//        vf.setFieldValues(ls(new VerifyField("price",
//                "= 40 + 2",
//                "==")));
//        runner.verify(vf);
//
//        assertTrue(((VerifyField) vf.getFieldValues().get(0)).getSuccessResult());
//    }
//
//    @Test
//    public void testVerifyFactExplanation() throws Exception {
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheese f1 = new Cheese();
//        f1.setType(null);
//        runner.getPopulatedData().put("f1",
//                f1);
//
//        VerifyFact vf = new VerifyFact();
//        vf.setName("f1");
//        vf.getFieldValues().add(new VerifyField("type",
//                "boo",
//                "!="));
//
//        runner.verify(vf);
//        VerifyField vfl = (VerifyField) vf.getFieldValues().get(0);
//        assertEquals("[f1] field [type] was not [boo].",
//                vfl.getExplanation());
//
//    }
//
//    @Test
//    public void testVerifyFieldAndActualIsNull() throws Exception {
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheese f1 = new Cheese();
//        f1.setType(null);
//        runner.getPopulatedData().put("f1",
//                f1);
//
//        VerifyFact vf = new VerifyFact();
//        vf.setName("f1");
//        vf.getFieldValues().add(new VerifyField("type",
//                "boo",
//                "=="));
//
//        runner.verify(vf);
//        VerifyField vfl = (VerifyField) vf.getFieldValues().get(0);
//
//        assertEquals("[f1] field [type] was [] expected [boo].",
//                vfl.getExplanation());
//        assertEquals("boo",
//                vfl.getExpected());
//        assertEquals("",
//                vfl.getActualResult());
//
//    }
//
//    @Test
//    public void testDummyRunNoRules() throws Exception {
//        Scenario sc = new Scenario();
//        FactData[] facts = new FactData[]{new FactData("Cheese",
//                "c1",
//                ls(new FieldData("type",
//                        "cheddar"),
//                        new FieldData("price",
//                                "42")),
//                false)};
//
//        VerifyFact[] assertions = new VerifyFact[]{new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "cheddar",
//                        "=="),
//                        new VerifyField("price",
//                                "42",
//                                "==")))};
//
//        sc.getFixtures().addAll(Arrays.asList(facts));
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//
//        MockWorkingMemory wm = new MockWorkingMemory();
//        ScenarioRunner runner = new ScenarioRunner(sc,
//                resolver,
//                wm);
//        assertEquals(1,
//                wm.facts.size());
//        assertEquals(runner.getPopulatedData().get("c1"),
//                wm.facts.get(0));
//
//        assertTrue(runner.getPopulatedData().containsKey("c1"));
//        VerifyFact vf = (VerifyFact) assertions[0];
//        for (int i = 0; i < vf.getFieldValues().size(); i++) {
//            assertTrue(((VerifyField) vf.getFieldValues().get(i)).getSuccessResult());
//        }
//
//    }
//
//    @Test
//    public void testCountVerification() throws Exception {
//
//        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
//        firingCounts.put("foo",
//                2);
//        firingCounts.put("bar",
//                1);
//        // and baz, we leave out
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        VerifyRuleFired v = new VerifyRuleFired();
//        v.setRuleName("foo");
//        v.setExpectedFire(true);
//        runner.verify(v,
//                firingCounts);
//        assertTrue(v.getSuccessResult());
//        assertEquals(2,
//                v.getActualResult().intValue());
//
//        v = new VerifyRuleFired();
//        v.setRuleName("foo");
//        v.setExpectedFire(false);
//        runner.verify(v,
//                firingCounts);
//        assertFalse(v.getSuccessResult());
//        assertEquals(2,
//                v.getActualResult().intValue());
//        assertNotNull(v.getExplanation());
//
//        v = new VerifyRuleFired();
//        v.setRuleName("foo");
//        v.setExpectedCount(2);
//
//        runner.verify(v,
//                firingCounts);
//        assertTrue(v.getSuccessResult());
//        assertEquals(2,
//                v.getActualResult().intValue());
//
//    }
//
//    @Test
//    public void testRuleFiredWithEnum() throws Exception {
//        Map<String, Integer> firingCounts = new HashMap<String, Integer>();
//        firingCounts.put("foo",
//                2);
//        firingCounts.put("bar",
//                1);
//        // and baz, we leave out
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        VerifyRuleFired v = new VerifyRuleFired();
//        v.setRuleName("foo");
//        v.setExpectedFire(true);
//        runner.verify(v,
//                firingCounts);
//        assertTrue(v.getSuccessResult());
//        assertEquals(2,
//                v.getActualResult().intValue());
//    }
//
//    @Test
//    public void testTestingEventListener() throws Exception {
//        Scenario sc = new Scenario();
//        sc.getRules().add("foo");
//        sc.getRules().add("bar");
//        ExecutionTrace ext = new ExecutionTrace();
//
//        sc.getFixtures().add(ext);
//
//        MockWorkingMemory wm = new MockWorkingMemory();
//        PseudoClockScheduler clock = new PseudoClockScheduler();
//        long time = new Date().getTime();
//        clock.setStartupTime(time);
//        clock.setSession(wm);
//        wm.setSessionClock(clock);
//
//        ScenarioRunner run = new ScenarioRunner(sc,
//                null,
//                wm);
//        assertEquals(wm,
//                run.getWorkingMemory());
//        assertNotNull(wm.agendaEventListener);
//        assertTrue(wm.agendaEventListener instanceof TestingEventListener);
//        TestingEventListener lnr = (TestingEventListener) wm.agendaEventListener;
//        assertEquals(2,
//                sc.getRules().size());
//        assertTrue(sc.getRules().contains("foo"));
//        assertTrue(sc.getRules().contains("bar"));
//    }
//
//    @Test
//    public void testWithGlobals() throws Exception {
//        Scenario sc = new Scenario();
//        FactData[] facts = new FactData[]{new FactData("Cheese",
//                "c2",
//                ls(new FieldData("type",
//                        "stilton")),
//                false)};
//        sc.getGlobals().add(new FactData("Cheese",
//                "c",
//                ls(new FieldData("type",
//                        "cheddar")),
//                false));
//        sc.getFixtures().addAll(Arrays.asList(facts));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//
//        MockWorkingMemory wm = new MockWorkingMemory();
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                wm);
//        assertEquals(1,
//                wm.globals.size());
//        assertEquals(1,
//                run.getGlobalData().size());
//        assertEquals(1,
//                run.getPopulatedData().size());
//        assertEquals(1,
//                wm.facts.size());
//
//        Cheese c = (Cheese) wm.globals.get("c");
//        assertEquals("cheddar",
//                c.getType());
//        Cheese c2 = (Cheese) wm.facts.get(0);
//        assertEquals("stilton",
//                c2.getType());
//
//    }
//
//    /**
//     * Check if global list is empty.
//     */
//    @Test
//    public void testWithGlobalList() throws Exception {
//        Scenario sc = new Scenario();
//        sc.getGlobals().add(new FactData("List",
//                "testList",
//                new ArrayList(),
//                false));
//
//        Expectation[] assertions = new Expectation[2];
//
//        assertions[0] = new VerifyFact("testList",
//                ls(new VerifyField("empty",
//                        "true",
//                        "==")));
//        assertions[1] = new VerifyFact("testList",
//                ls(new VerifyField("size",
//                        "0",
//                        "==")));
//
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("java.util.List");
//
//        MockWorkingMemory wm = new MockWorkingMemory();
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                wm);
//
//        List testList = (List) wm.globals.get("testList");
//        assertTrue(testList.isEmpty());
//        assertEquals(0,
//                testList.size());
//    }
//
//    @SuppressWarnings("deprecation")
//    // F**** dates in java. What a mess. Someone should die.
//    @Test
//    public void testSimulatedDate() throws Exception {
//        Scenario sc = new Scenario();
//        MockWorkingMemory wm = new MockWorkingMemory();
//        PseudoClockScheduler clock = new PseudoClockScheduler();
//        long time = new Date().getTime();
//        clock.setStartupTime(time);
//        clock.setSession(wm);
//        wm.setSessionClock(clock);
//        ScenarioRunner run = new ScenarioRunner(sc,
//                null,
//                wm);
//
//        assertEquals(time,
//                wm.getSessionClock().getCurrentTime());
//
//        ExecutionTrace ext = new ExecutionTrace();
//        ext.setScenarioSimulatedDate(new Date("10-Jul-1974"));
//        sc.getFixtures().add(ext);
//        run = new ScenarioRunner(sc,
//                null,
//                wm);
//
//        long expected = ext.getScenarioSimulatedDate().getTime();
//        assertEquals(expected,
//                wm.getSessionClock().getCurrentTime());
//        //        Thread.sleep( 50 );
//        //        assertEquals( expected,
//        //                      tm.getNow().getTimeInMillis() );
//
//    }
//
//    @Test
//    public void testVerifyRuleFired() throws Exception {
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//
//        VerifyRuleFired vr = new VerifyRuleFired("qqq",
//                42,
//                null);
//        Map<String, Integer> f = new HashMap<String, Integer>();
//        f.put("qqq",
//                42);
//        f.put("qaz",
//                1);
//
//        runner.verify(vr,
//                f);
//        assertTrue(vr.wasSuccessful());
//        assertEquals(42,
//                vr.getActualResult().intValue());
//
//        vr = new VerifyRuleFired("qqq",
//                41,
//                null);
//        runner.verify(vr,
//                f);
//        assertFalse(vr.wasSuccessful());
//        assertEquals(42,
//                vr.getActualResult().intValue());
//
//        vr = new VerifyRuleFired("qaz",
//                1,
//                null);
//        runner.verify(vr,
//                f);
//        assertTrue(vr.wasSuccessful());
//        assertEquals(1,
//                vr.getActualResult().intValue());
//
//        vr = new VerifyRuleFired("XXX",
//                null,
//                false);
//        runner.verify(vr,
//                f);
//        assertTrue(vr.wasSuccessful());
//        assertEquals(0,
//                vr.getActualResult().intValue());
//
//        vr = new VerifyRuleFired("qqq",
//                null,
//                true);
//        runner.verify(vr,
//                f);
//        assertTrue(vr.wasSuccessful());
//        assertEquals(42,
//                vr.getActualResult().intValue());
//
//        vr = new VerifyRuleFired("qqq",
//                null,
//                false);
//        runner.verify(vr,
//                f);
//        assertFalse(vr.wasSuccessful());
//        assertEquals(42,
//                vr.getActualResult().intValue());
//
//    }
//
//    /**
//     * Do a kind of end to end test with some real rules.
//     */
//    @Test
//    public void testIntegrationWithSuccess() throws Exception {
//
//        Scenario sc = new Scenario();
//        FactData[] facts = new FactData[]{new FactData("Cheese",
//                "c1",
//                ls(new FieldData("type",
//                        "cheddar"),
//                        new FieldData("price",
//                                "42")),
//                false)
//
//        };
//        sc.getGlobals().add(new FactData("Person",
//                "p",
//                new ArrayList(),
//                false));
//        sc.getFixtures().addAll(Arrays.asList(facts));
//
//        ExecutionTrace executionTrace = new ExecutionTrace();
//
//        sc.getRules().add("rule1");
//        sc.getRules().add("rule2");
//        sc.setInclusive(true);
//        sc.getFixtures().add(executionTrace);
//
//        Expectation[] assertions = new Expectation[5];
//
//        assertions[0] = new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "cheddar",
//                        "==")
//
//                ));
//
//        assertions[1] = new VerifyFact("p",
//                ls(new VerifyField("name",
//                        "rule1",
//                        "=="),
//                        new VerifyField("status",
//                                "rule2",
//                                "=="))
//
//        );
//
//        assertions[2] = new VerifyRuleFired("rule1",
//                1,
//                null);
//        assertions[3] = new VerifyRuleFired("rule2",
//                1,
//                null);
//        assertions[4] = new VerifyRuleFired("rule3",
//                0,
//                null);
//
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//        resolver.addImport("org.drools.Person");
//
//        WorkingMemory wm = getWorkingMemory("test_rules2.drl");
//
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        assertEquals(2,
//                executionTrace.getNumberOfRulesFired().intValue());
//
//        assertSame(run.getScenario(),
//                sc);
//
//        assertTrue(sc.wasSuccessful());
//
//        Person p = (Person) run.getGlobalData().get("p");
//        assertEquals("rule1",
//                p.getName());
//        assertEquals("rule2",
//                p.getStatus());
//        assertEquals(0,
//                p.getAge());
//
//        Thread.sleep(50);
//
//        assertTrue((new Date()).after(sc.getLastRunResult()));
//        assertTrue(executionTrace.getExecutionTimeResult() != null);
//
//        assertTrue(executionTrace.getRulesFired().length > 0);
//
//    }
//
//    @Test
//    public void testIntegrationInfiniteLoop() throws Exception {
//
//        Scenario sc = new Scenario();
//        FactData[] facts = new FactData[]{new FactData("Cheese",
//                "c1",
//                ls(new FieldData("type",
//                        "cheddar"),
//                        new FieldData("price",
//                                "42")),
//                false)
//
//        };
//        sc.getGlobals().add(new FactData("Person",
//                "p",
//                new ArrayList(),
//                false));
//        sc.getFixtures().addAll(Arrays.asList(facts));
//
//        ExecutionTrace executionTrace = new ExecutionTrace();
//
//        sc.getRules().add("rule1");
//        sc.getRules().add("rule2");
//        sc.setInclusive(true);
//        sc.getFixtures().add(executionTrace);
//
//        Expectation[] assertions = new Expectation[5];
//
//        assertions[0] = new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "cheddar",
//                        "==")
//
//                ));
//
//        assertions[1] = new VerifyFact("p",
//                ls(new VerifyField("name",
//                        "rule1",
//                        "=="),
//                        new VerifyField("status",
//                                "rule2",
//                                "=="))
//
//        );
//
//        assertions[2] = new VerifyRuleFired("rule1",
//                1,
//                null);
//        assertions[3] = new VerifyRuleFired("rule2",
//                1,
//                null);
//        assertions[4] = new VerifyRuleFired("rule3",
//                0,
//                null);
//
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//        resolver.addImport("org.drools.Person");
//
//        WorkingMemory wm = getWorkingMemory("test_rules_infinite_loop.drl");
//
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        assertEquals(sc.getMaxRuleFirings(),
//                executionTrace.getNumberOfRulesFired().intValue());
//
//    }
//
//    @Test
//    public void testIntegrationWithDeclaredTypes() throws Exception {
//        Scenario sc = new Scenario();
//        FactData[] facts = new FactData[]{new FactData("Coolness",
//                "c",
//                ls(new FieldData("num",
//                        "42"),
//                        new FieldData("name",
//                                "mic")),
//                false)
//
//        };
//        sc.getFixtures().addAll(Arrays.asList(facts));
//
//        ExecutionTrace executionTrace = new ExecutionTrace();
//
//        sc.getRules().add("rule1");
//        sc.setInclusive(true);
//        sc.getFixtures().add(executionTrace);
//
//        Expectation[] assertions = new Expectation[2];
//
//        assertions[0] = new VerifyFact("c",
//                ls(new VerifyField("num",
//                        "42",
//                        "==")
//
//                ));
//
//        assertions[1] = new VerifyRuleFired("rule1",
//                1,
//                null);
//
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//
//        WorkingMemory wm = getWorkingMemory("test_rules3.drl");
//        ClassLoader cl = ((InternalRuleBase) wm.getRuleBase()).getRootClassLoader();
//
//        HashSet<String> imports = new HashSet<String>();
//        imports.add("foo.bar.*");
//
//        TypeResolver resolver = new ClassTypeResolver(imports,
//                cl);
//
//        Class cls = cl.loadClass("foo.bar.Coolness");
//        assertNotNull(cls);
//
//        ClassLoader cl_ = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(cl);
//
//        //resolver will need to have generated beans in it - possibly using a composite classloader from the package,
//        //including whatever CL has the generated beans...
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        assertEquals(1,
//                executionTrace.getNumberOfRulesFired().intValue());
//
//        assertSame(run.getScenario(),
//                sc);
//
//        assertTrue(sc.wasSuccessful());
//
//        Thread.currentThread().setContextClassLoader(cl_);
//
//    }
//
//    @Test
//    public void testRuleFlowGroupActivation() throws Exception {
//        Scenario scenario = new Scenario();
//        Fixture[] given = new Fixture[]{new FactData("Coolness",
//                "c",
//                ls(new FieldData("num",
//                        "42"),
//                        new FieldData("name",
//                                "mic")),
//                false)
//
//        };
//        scenario.getFixtures().addAll(Arrays.asList(given));
//
//        ExecutionTrace executionTrace = new ExecutionTrace();
//
//        scenario.getRules().add("rule1");
//        scenario.setInclusive(true);
//        scenario.getFixtures().add(executionTrace);
//
//        Expectation[] assertions = new Expectation[2];
//
//        assertions[0] = new VerifyFact("c",
//                ls(new VerifyField("num",
//                        "42",
//                        "==")));
//
//        assertions[1] = new VerifyRuleFired("rule1",
//                1,
//                null);
//
//        scenario.getFixtures().addAll(Arrays.asList(assertions));
//
//        WorkingMemory workingMemory = getWorkingMemory("rule_flow_actication.drl");
//        ClassLoader classLoader = ((InternalRuleBase) workingMemory.getRuleBase()).getRootClassLoader();
//
//        HashSet<String> imports = new HashSet<String>();
//        imports.add("foo.bar.*");
//
//        TypeResolver resolver = new ClassTypeResolver(imports,
//                classLoader);
//
//        Class<?> coolnessClass = classLoader.loadClass("foo.bar.Coolness");
//        assertNotNull(coolnessClass);
//
//        ClassLoader cl_ = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(classLoader);
//
//        //resolver will need to have generated beans in it - possibly using a composite classloader from the package,
//        //including whatever CL has the generated beans...
//        ScenarioRunner scenarioRunner = new ScenarioRunner(scenario,
//                resolver,
//                (InternalWorkingMemory) workingMemory);
//
//        assertEquals(0,
//                executionTrace.getNumberOfRulesFired().intValue());
//
//        assertSame(scenarioRunner.getScenario(),
//                scenario);
//
//        assertFalse(scenario.wasSuccessful());
//
//        // Activate rule flow
//        scenario.getFixtures().clear();
//        given = new Fixture[]{new FactData("Coolness",
//                "c",
//                ls(new FieldData("num",
//                        "42"),
//                        new FieldData("name",
//                                "mic")),
//                false), new ActivateRuleFlowGroup("asdf")};
//        workingMemory.clearAgenda();
//        scenario.getFixtures().addAll(Arrays.asList(given));
//        scenario.getFixtures().add(executionTrace);
//        workingMemory.getAgenda().getRuleFlowGroup("asdf").setAutoDeactivate(false);
//        scenarioRunner = new ScenarioRunner(scenario,
//                resolver,
//                (InternalWorkingMemory) workingMemory);
//
//        assertEquals(1,
//                executionTrace.getNumberOfRulesFired().intValue());
//
//        assertSame(scenarioRunner.getScenario(),
//                scenario);
//
//        assertTrue(scenario.wasSuccessful());
//
//        Thread.currentThread().setContextClassLoader(cl_);
//    }
//
//    @Test
//    public void testIntgerationStateful() throws Exception {
//        Scenario sc = new Scenario();
//        sc.getFixtures().add(new FactData("Cheese",
//                "c1",
//                ls(new FieldData("price",
//                        "1")),
//                false));
//        ExecutionTrace ex = new ExecutionTrace();
//        sc.getFixtures().add(ex);
//        sc.getFixtures().add(new FactData("Cheese",
//                "c2",
//                ls(new FieldData("price",
//                        "2")),
//                false));
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "rule1",
//                        "=="))));
//        ex = new ExecutionTrace();
//        sc.getFixtures().add(ex);
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "rule2",
//                        "=="))));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//
//        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        Cheese c1 = (Cheese) run.getPopulatedData().get("c1");
//        Cheese c2 = (Cheese) run.getPopulatedData().get("c2");
//
//        assertEquals("rule2",
//                c1.getType());
//        assertEquals("rule2",
//                c2.getType());
//
//        assertTrue(sc.wasSuccessful());
//
//    }
//
//    @Test
//    public void testIntegrationWithModify() throws Exception {
//        Scenario sc = new Scenario();
//        sc.getFixtures().add(new FactData("Cheese",
//                "c1",
//                ls(new FieldData("price",
//                        "1")),
//                false));
//
//        sc.getFixtures().add(new ExecutionTrace());
//
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "rule1",
//                        "=="))));
//
//        sc.getFixtures().add(new FactData("Cheese",
//                "c1",
//                ls(new FieldData("price",
//                        "42")),
//                true));
//        sc.getFixtures().add(new ExecutionTrace());
//
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "rule3",
//                        "=="))));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//
//        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        Cheese c1 = (Cheese) run.getPopulatedData().get("c1");
//
//        assertEquals("rule3",
//                c1.getType());
//
//        assertTrue(sc.wasSuccessful());
//    }
//
//    @Test
//    public void testIntegrationWithRetract() throws Exception {
//        Scenario sc = new Scenario();
//        sc.getFixtures().add(new FactData("Cheese",
//                "c1",
//                ls(new FieldData("price",
//                        "46"),
//                        new FieldData("type",
//                                "XXX")),
//                false));
//        sc.getFixtures().add(new FactData("Cheese",
//                "c2",
//                ls(new FieldData("price",
//                        "42")),
//                false));
//        sc.getFixtures().add(new ExecutionTrace());
//
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "XXX",
//                        "=="))));
//
//        sc.getFixtures().add(new RetractFact("c2"));
//        sc.getFixtures().add(new ExecutionTrace());
//
//        sc.getFixtures().add(new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "rule4",
//                        "=="))));
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//
//        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        Cheese c1 = (Cheese) run.getPopulatedData().get("c1");
//
//        assertEquals("rule4",
//                c1.getType());
//        assertFalse(run.getPopulatedData().containsKey("c2"));
//
//        assertTrue(sc.wasSuccessful());
//    }
//
//    @Test
//    public void testIntegrationWithFailure() throws Exception {
//        Scenario sc = new Scenario();
//        Expectation[] assertions = populateScenarioForFailure(sc);
//
//        TypeResolver resolver = new ClassTypeResolver(new HashSet<String>(),
//                Thread.currentThread().getContextClassLoader());
//        resolver.addImport("org.drools.Cheese");
//        resolver.addImport("org.drools.Person");
//
//        WorkingMemory wm = getWorkingMemory("test_rules2.drl");
//
//        ScenarioRunner run = new ScenarioRunner(sc,
//                resolver,
//                (InternalWorkingMemory) wm);
//
//        assertSame(run.getScenario(),
//                sc);
//
//        assertFalse(sc.wasSuccessful());
//
//        VerifyFact vf = (VerifyFact) assertions[1];
//        assertFalse(((VerifyField) vf.getFieldValues().get(0)).getSuccessResult());
//        assertEquals("XXX",
//                ((VerifyField) vf.getFieldValues().get(0)).getExpected());
//        assertEquals("rule1",
//                ((VerifyField) vf.getFieldValues().get(0)).getActualResult());
//        assertNotNull(((VerifyField) vf.getFieldValues().get(0)).getExplanation());
//
//        VerifyRuleFired vr = (VerifyRuleFired) assertions[4];
//        assertFalse(vr.getSuccessResult());
//
//        assertEquals(2,
//                vr.getExpectedCount().intValue());
//        assertEquals(0,
//                vr.getActualResult().intValue());
//
//    }
//
//    private Expectation[] populateScenarioForFailure(Scenario sc) {
//        FactData[] facts = new FactData[]{new FactData("Cheese",
//                "c1",
//                ls(new FieldData("type",
//                        "cheddar"),
//                        new FieldData("price",
//                                "42")),
//                false)
//
//        };
//        sc.getFixtures().addAll(Arrays.asList(facts));
//        sc.getGlobals().add(new FactData("Person",
//                "p",
//                new ArrayList(),
//                false));
//
//        ExecutionTrace executionTrace = new ExecutionTrace();
//        sc.getRules().add("rule1");
//        sc.getRules().add("rule2");
//        sc.setInclusive(true);
//        sc.getFixtures().add(executionTrace);
//
//        Expectation[] assertions = new Expectation[5];
//
//        assertions[0] = new VerifyFact("c1",
//                ls(new VerifyField("type",
//                        "cheddar",
//                        "==")
//
//                ));
//
//        assertions[1] = new VerifyFact("p",
//                ls(new VerifyField("name",
//                        "XXX",
//                        "=="),
//                        new VerifyField("status",
//                                "rule2",
//                                "==")
//
//                ));
//
//        assertions[2] = new VerifyRuleFired("rule1",
//                1,
//                null);
//        assertions[3] = new VerifyRuleFired("rule2",
//                1,
//                null);
//        assertions[4] = new VerifyRuleFired("rule3",
//                2,
//                null);
//
//        sc.getFixtures().addAll(Arrays.asList(assertions));
//        return assertions;
//    }
//
    private <T> List<T> ls
    (T... objects) {
        return Arrays.asList(objects);
    }
//
//    @Test
//    public void testCollectionFieldInFacts() throws Exception {
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheesery listChesse = new Cheesery();
//        Cheese f1 = new Cheese("cheddar",
//                42);
//        Cheese f2 = new Cheese("Camembert",
//                43);
//        Cheese f3 = new Cheese("Emmental",
//                45);
//        runner.getPopulatedData().put("f1",
//                f1);
//        runner.getPopulatedData().put("f2",
//                f2);
//        runner.getPopulatedData().put("f3",
//                f3);
//        FactData fd1 = new FactData("Cheese",
//                "f1",
//                ls(new FieldData("type",
//                        ""),
//                        new FieldData("price",
//                                "42")),
//                false);
//        FactData fd2 = new FactData("Cheese",
//                "f2",
//                ls(new FieldData("type",
//                        ""),
//                        new FieldData("price",
//                                "43")),
//                false);
//        FactData fd3 = new FactData("Cheese",
//                "f3",
//                ls(new FieldData("type",
//                        ""),
//                        new FieldData("price",
//                                "45")),
//                false);
//        runner.getPopulatedData().put("ACheesery",
//                listChesse);
//        FieldData field = new FieldData();
//        field.setName("cheeses");
//        field.collectionType = "Cheese";
//        field.setNature(FieldData.TYPE_COLLECTION);
//        field.setValue("=[f1,f2,f3]");
//        List<FieldData> lstField = new ArrayList<FieldData>();
//        lstField.add(field);
//        FactData lst = new FactData("Cheesery",
//                "listChesse",
//                lstField,
//                false);
//        runner.populateFields(lst,
//                listChesse);
//        assertTrue(listChesse.getCheeses().size() == 3);
//        assertTrue(listChesse.getCheeses().contains(f1));
//        assertTrue(listChesse.getCheeses().contains(f3));
//        assertTrue(listChesse.getCheeses().contains(f3));
//
//    }
//
//    @Test
//    public void testCallMethodNoArgumentOnFact() throws Exception {
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheesery listChesse = new Cheesery();
//        listChesse.setTotalAmount(1000);
//        runner.getPopulatedData().put("cheese",
//                listChesse);
//        CallMethod mCall = new CallMethod();
//        mCall.setVariable("cheese");
//        mCall.setMethodName("setTotalAmountToZero");
//        runner.executeMethodOnObject(mCall,
//                listChesse);
//        assertTrue(listChesse.getTotalAmount() == 0);
//    }
//
//    @Test
//    public void testCallMethodOnStandardArgumentOnFact() throws Exception {
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheesery listChesse = new Cheesery();
//        listChesse.setTotalAmount(1000);
//        runner.getPopulatedData().put("cheese",
//                listChesse);
//        CallMethod mCall = new CallMethod();
//        mCall.setVariable("cheese");
//        mCall.setMethodName("addToTotalAmount");
//        CallFieldValue field = new CallFieldValue();
//        field.value = "5";
//        mCall.addFieldValue(field);
//        runner.executeMethodOnObject(mCall,
//                listChesse);
//        assertTrue(listChesse.getTotalAmount() == 1005);
//    }
//
//    @Test
//    public void testCallMethodOnClassArgumentOnFact() throws Exception {
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheesery listChesse = new Cheesery();
//        listChesse.setTotalAmount(1000);
//        runner.getPopulatedData().put("cheese",
//                listChesse);
//        Maturity m = new Maturity();
//        runner.getPopulatedData().put("m",
//                m);
//        CallMethod mCall = new CallMethod();
//        mCall.setVariable("cheese");
//        mCall.setMethodName("setGoodMaturity");
//        CallFieldValue field = new CallFieldValue();
//        field.value = "=m";
//        mCall.addFieldValue(field);
//        runner.executeMethodOnObject(mCall,
//                listChesse);
//        assertTrue(listChesse.getMaturity().equals(m));
//        assertTrue(listChesse.getMaturity() == m);
//    }
//
//    @Test
//    public void testCallMethodOnClassArgumentAndOnArgumentStandardOnFact() throws Exception {
//
//        ScenarioRunner runner = new ScenarioRunner(new Scenario(),
//                null,
//                new MockWorkingMemory());
//        Cheesery listChesse = new Cheesery();
//        listChesse.setTotalAmount(1000);
//        runner.getPopulatedData().put("cheese",
//                listChesse);
//        Maturity m = new Maturity("veryYoung");
//        runner.getPopulatedData().put("m",
//                m);
//        CallMethod mCall = new CallMethod();
//        mCall.setVariable("cheese");
//        mCall.setMethodName("setAgeToMaturity");
//        CallFieldValue field = new CallFieldValue();
//        field.value = "=m";
//        mCall.addFieldValue(field);
//        CallFieldValue field2 = new CallFieldValue();
//        field2.value = "veryold";
//        mCall.addFieldValue(field2);
//        runner.executeMethodOnObject(mCall,
//                listChesse);
//        assertTrue(m.getAge().equals("veryold"));
//    }

}
