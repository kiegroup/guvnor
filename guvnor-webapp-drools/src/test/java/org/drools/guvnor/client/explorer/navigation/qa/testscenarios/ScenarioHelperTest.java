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

package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.CallFixtureMap;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.junit.Test;

public class ScenarioHelperTest {


    //need to get out chunks, so we get data (insert, update, retract)
    //then callMethodData
    //then execute
    //then expectations
    //then data
    //then execute

    //want it in chunks
    @SuppressWarnings("unchecked")
    @Test
    public void testChunks() {
        List l = new ArrayList();
        l.add(new FactData("Q", "x", new ArrayList(), false));
        l.add(new FactData("Q", "y", new ArrayList(), false));
        l.add(new FactData("X", "a", new ArrayList(), false));
        l.add(new FactData("X", "b", new ArrayList(), false));
        l.add(new CallMethod("x","hello"));
        l.add(new CallMethod("x","helloItherWay"));
        l.add(new CallMethod("v","helloAgain"));
        ExecutionTrace ex1 = new ExecutionTrace();
        l.add(ex1);

        l.add(new FactData("Z", "z", new ArrayList(), false));
        l.add(new FactData("Q", "x", new ArrayList(), true));
        l.add(new FactData("Q", "y", new ArrayList(), true));
        l.add(new RetractFact("y"));
        l.add(new CallMethod("x","hello"));


        VerifyFact vf1 = new VerifyFact();
        VerifyFact vf2 = new VerifyFact();
        VerifyRuleFired vr1 = new VerifyRuleFired();
        VerifyRuleFired vr2 = new VerifyRuleFired();
        l.add(vf1);
        l.add(vf2);
        l.add(vr1);
        l.add(vr2);

        ExecutionTrace ex2 = new ExecutionTrace();
        l.add(ex2);

        VerifyFact vf3 = new VerifyFact();
        l.add(vf3);

        ScenarioHelper hlp = new ScenarioHelper();

        List fx = hlp.lumpyMap(l);
        assertEquals(9, fx.size());

        Map first  = (Map) fx.get(0);
        assertEquals(2, first.size());
        List fdl = (List) first.get("Q");
        assertEquals(2, fdl.size());
        FactData fd = (FactData) fdl.get(0);
        assertEquals("x", fd.getName());
        fd = (FactData) fdl.get(1);
        assertEquals("y", fd.getName());

        fdl = (List) first.get("X");
        assertEquals(2, fdl.size());
        fd = (FactData) fdl.get(0);
        assertEquals("a", fd.getName());

        CallFixtureMap callMap = (CallFixtureMap)fx.get(1);
        assertEquals(2, callMap.size());
        assertTrue(callMap.containsKey("x"));
        FixtureList lcall1 = callMap.get("x");
        CallMethod c1 = (CallMethod)lcall1.get(0);
        assertTrue(c1.getVariable().equals("x"));
        assertTrue(c1.getMethodName().equals("hello"));
        CallMethod c2 = (CallMethod)lcall1.get(1);
        assertTrue(c2.getVariable().equals("x"));
        assertTrue(c2.getMethodName().equals("helloItherWay"));
        assertTrue(callMap.containsKey("v"));
        FixtureList lcall2 = callMap.get("v");
        CallMethod c3= (CallMethod)lcall2.get(0);
        assertTrue(c3.getVariable().equals("v"));
        assertTrue(c3.getMethodName().equals("helloAgain"));


        assertEquals(ex1, fx.get(2));

        List ruleFired = (List) fx.get(3);
        assertEquals(2, ruleFired.size());
        assertEquals(vr1, ruleFired.get(0));
        assertEquals(vr2, ruleFired.get(1));

        List verifyFact = (List) fx.get(4);
        assertEquals(2, verifyFact.size());
        assertEquals(vf1, verifyFact.get(0));
        assertEquals(vf2, verifyFact.get(1));



        Map second = (Map) fx.get(5);
        assertEquals(3, second.size());
        assertTrue(second.containsKey("Z"));
        assertTrue(second.containsKey("Q"));
        fdl = (List) second.get("Q");
        assertEquals(2, fdl.size());

        assertTrue(second.containsKey("retract"));
        List retracts = (List) second.get("retract");
        assertEquals(1, retracts.size());
        RetractFact ret = (RetractFact) retracts.get(0);
        assertEquals("y", ret.getName());


        CallFixtureMap third = (CallFixtureMap)fx.get(6);
        assertEquals(1, third.size());
        assertTrue(third.containsKey("x"));
        FixtureList lcall3 = third.get("x");
        CallMethod c4= (CallMethod)lcall3.get(0);
        assertTrue(c4.getVariable().equals("x"));
        assertTrue(c4.getMethodName().equals("hello"));
        assertEquals(ex2, fx.get(7));



        List last = (List) fx.get(8);
        assertEquals(1, last.size());
        assertEquals(vf3, last.get(0));



    }
    @SuppressWarnings("unchecked")
    @Test
    public void testGlobals() {
        List l = new ArrayList();
        l.add(new FactData("X", "d", new ArrayList(), true));
        l.add(new FactData("X", "c", new ArrayList(), true));
        l.add(new FactData("Q", "a", new ArrayList(), true));
        l.add(new FactData("Q", "b", new ArrayList(), true));

        ScenarioHelper hlp = new ScenarioHelper();
        Map m = hlp.lumpyMapGlobals(l);
        assertEquals(2, m.size());
        List fd = (List) m.get("X");
        assertEquals(2, fd.size());
        assertEquals("d", ((FactData)fd.get(0)).getName());
        assertEquals("c", ((FactData)fd.get(1)).getName());

        fd = (List) m.get("Q");
        assertEquals(2, fd.size());
        assertEquals("a", ((FactData)fd.get(0)).getName());
        assertEquals("b", ((FactData)fd.get(1)).getName());

    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveField() {
        List fieldData = new ArrayList();
        fieldData.add(new FieldData("q", "1"));
        fieldData.add(new FieldData("w", "2"));
        FactData fd = new FactData("X", "x", fieldData, false);

        List fieldData2 = new ArrayList();
        fieldData2.add(new FieldData("q", "3"));
        fieldData2.add(new FieldData("w", "4"));
        fieldData2.add(new FieldData("x", "5"));
        FactData fd2 = new FactData("X", "x", fieldData2, false);

        List factData = new ArrayList();
        factData.add(fd);
        factData.add(fd2);

        ScenarioHelper.removeFields(factData, "q");

        assertEquals(2, factData.size());

        assertEquals(1, fieldData.size());
        assertEquals("w", ((FieldData)fieldData.get(0)).getName());
        assertEquals(2, fieldData2.size());
        assertEquals("w", ((FieldData)fieldData2.get(0)).getName());

    }
    @SuppressWarnings("unchecked")
    @Test
    public void testEmptyMap() {
        //this should check that there is always a map present to force the GUI to show a "GIVEN" section.
        List<Fixture> fl = new ArrayList<Fixture>();
        fl.add(new FactData());
        fl.add(new ExecutionTrace());
        fl.add(new ExecutionTrace());

        ScenarioHelper hlp = new ScenarioHelper();
        List r = hlp.lumpyMap(fl);
        assertEquals(6, r.size());
        assertTrue(r.get(0) instanceof Map);
        assertTrue(r.get(1) instanceof Map);
        assertTrue(r.get(2) instanceof ExecutionTrace);
        assertTrue(r.get(3) instanceof Map);
        assertTrue(r.get(4) instanceof Map);
        assertTrue(r.get(5) instanceof ExecutionTrace);

        Map r_ = (Map) r.get(3);
        assertEquals(0, r_.size());
        r_ = (Map) r.get(4);
        assertEquals(0, r_.size());


        r_ = (Map) r.get(0);
        assertEquals(1, r_.size());
        r_ = (Map) r.get(1);
        assertEquals(0, r_.size());

    }






}
