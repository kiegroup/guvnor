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

package org.drools.ide.common.server.util;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

public class ScenarioXMLPersistenceTest {

    @Test
    public void testToXML() {
        ScenarioXMLPersistence p = ScenarioXMLPersistence.getInstance();

        Scenario sc = new Scenario();

        String s = p.marshal(sc);
        assertNotNull(s);

        sc = getDemo();

        s = p.marshal(sc);

        assertTrue(s.indexOf("<ruleName>Life unverse and everything</ruleName>") > 0);

        Scenario sc_ = p.unmarshal(s);
        assertEquals(sc.getGlobals().size(), sc_.getGlobals().size());
        assertEquals(sc.getFixtures().size(), sc_.getFixtures().size());
        assertTrue(s.indexOf("org.drools") == -1); //check we have aliased all

    }

    @Test
    public void testTrimUneededSection() {
        Scenario sc = getDemo();
        Scenario orig = getDemo();
        sc.getFixtures().add(new ExecutionTrace());

        int origSize = orig.getFixtures().size();

        assertEquals(origSize + 1, sc.getFixtures().size());
        String xml = ScenarioXMLPersistence.getInstance().marshal(sc);
        Scenario sc_ = ScenarioXMLPersistence.getInstance().unmarshal(xml);

        assertEquals(origSize, sc_.getFixtures().size());






    }


    @Test
    public void testNewScenario() {
        FactData d1 = new FactData("Driver", "d1", ls(new FieldData[] {new FieldData("age", "42"), new FieldData("name", "david")}), false);
        Scenario sc = new Scenario();
        sc.getFixtures().add(d1);
        sc.getFixtures().add(new ExecutionTrace());

        int size = sc.getFixtures().size();

        String xml = ScenarioXMLPersistence.getInstance().marshal(sc);
        Scenario sc_ = ScenarioXMLPersistence.getInstance().unmarshal(xml);

        assertEquals(size, sc_.getFixtures().size());

        sc = new Scenario();
        sc.getFixtures().add(new ExecutionTrace());
        xml = ScenarioXMLPersistence.getInstance().marshal(sc);
        sc_ = ScenarioXMLPersistence.getInstance().unmarshal(xml);
        assertEquals(1, sc_.getFixtures().size());
    }

    private Scenario getDemo() {
        //Sample data
        FactData d1 = new FactData("Driver", "d1", ls(new FieldData[] {new FieldData("age", "42"), new FieldData("name", "david")}), false);
        FactData d2 = new FactData("Driver", "d2", ls(new FieldData[] {new FieldData("name", "michael")}), false);
        FactData d3 = new FactData("Driver", "d3", ls(new FieldData[] {new FieldData("name", "michael2")}), false);
        FactData d4 = new FactData("Accident", "a1", ls(new FieldData[] {new FieldData("name", "michael2")}), false);
        Scenario sc = new Scenario();
        sc.getFixtures().add(d1);
        sc.getFixtures().add(d2);
        sc.getGlobals().add(d3);
        sc.getGlobals().add(d4);
        sc.getRules().add("rule1");
        sc.getRules().add("rule2");

        sc.getFixtures().add(new ExecutionTrace());

        List fields = new ArrayList();
        VerifyField vfl = new VerifyField("age", "42", "==");
        vfl.setActualResult( "43" );
        vfl.setSuccessResult( new Boolean(false) );
        vfl.setExplanation( "Not cool jimmy." );

        fields.add(vfl);

        vfl = new VerifyField("name", "michael", "!=");
        vfl.setActualResult( "bob" );
        vfl.setSuccessResult( new Boolean(true) );
        vfl.setExplanation( "Yeah !" );
        fields.add(vfl);

        VerifyFact vf = new VerifyFact("d1", fields);

        sc.getFixtures().add(vf);

        VerifyRuleFired vf1 = new VerifyRuleFired("Life unverse and everything", new Integer(42), null);
        vf1.setActualResult( new Integer(42) );
        vf1.setSuccessResult( new Boolean(true) );
        vf1.setExplanation( "All good here." );

        VerifyRuleFired vf2 = new VerifyRuleFired("Everything else", null, new Boolean(true));
        vf2.setActualResult( new Integer(0) );
        vf2.setSuccessResult( new Boolean(false) );
        vf2.setExplanation( "Not so good here." );
        sc.getFixtures().add(vf1);
        sc.getFixtures().add(vf2);

        return sc;
    }

    private List ls(FieldData[] fieldDatas) {
        List ls = new ArrayList();
        for (int i = 0; i < fieldDatas.length; i++) {
            ls.add(fieldDatas[i]);
        }
        return ls;
    }


}
