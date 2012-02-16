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

import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Expectation;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Persists the scenario model.
 */
public class ScenarioXMLPersistence {

    private XStream                     xt;
    private static final ScenarioXMLPersistence INSTANCE = new ScenarioXMLPersistence();

    private ScenarioXMLPersistence() {
        xt = new XStream(new DomDriver());
        xt.alias("scenario", Scenario.class);
        xt.alias("execution-trace", ExecutionTrace.class);
        xt.alias("expectation", Expectation.class);
        xt.alias("fact-data", FactData.class);
        xt.alias("field-data", FieldData.class);
        xt.alias("fixture", Fixture.class);
        xt.alias("retract-fact", RetractFact.class);
        xt.alias("expect-fact", VerifyFact.class);
        xt.alias("expect-field", VerifyField.class);
        xt.alias("expect-rule", VerifyRuleFired.class);
        xt.omitField(ExecutionTrace.class, "rulesFired");

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage( "org.drools.guvnor.client", "org.drools.ide.common.client" );
}

    public static ScenarioXMLPersistence getInstance() {
        return INSTANCE;
    }



    public String marshal(Scenario sc) {
        if (sc.getFixtures().size() > 1  && sc.getFixtures().get(sc.getFixtures().size() - 1) instanceof ExecutionTrace) {
            Object f = sc.getFixtures().get(sc.getFixtures().size() - 2);

            if (f instanceof Expectation) {
                sc.getFixtures().remove(sc.getFixtures().size() - 1);
            }

        }
        return xt.toXML(sc);
    }

    public Scenario unmarshal(String xml) {
        if (xml == null) return new Scenario();
        if (xml.trim().equals("")) return new Scenario();
        return (Scenario) xt.fromXML(xml);
    }

}
