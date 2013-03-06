package org.kie.guvnor.testscenario.backend.server;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.kie.guvnor.testscenario.model.CollectionFieldData;
import org.kie.guvnor.testscenario.model.ExecutionTrace;
import org.kie.guvnor.testscenario.model.Expectation;
import org.kie.guvnor.testscenario.model.Fact;
import org.kie.guvnor.testscenario.model.FactAssignmentField;
import org.kie.guvnor.testscenario.model.FactData;
import org.kie.guvnor.testscenario.model.Field;
import org.kie.guvnor.testscenario.model.FieldData;
import org.kie.guvnor.testscenario.model.FieldPlaceHolder;
import org.kie.guvnor.testscenario.model.Fixture;
import org.kie.guvnor.testscenario.model.RetractFact;
import org.kie.guvnor.testscenario.model.Scenario;
import org.kie.guvnor.testscenario.model.VerifyFact;
import org.kie.guvnor.testscenario.model.VerifyField;
import org.kie.guvnor.testscenario.model.VerifyRuleFired;

public class TestScenarioContentHandler {

    private XStream getXStream() {
        XStream xt = new XStream(new DomDriver());
        xt.alias("scenario", Scenario.class);
        xt.alias("execution-trace", ExecutionTrace.class);
        xt.alias("expectation", Expectation.class);
        xt.alias("fact-data", FactData.class);
        xt.alias("fact", Fact.class);
        xt.alias("field-data", Field.class);
        xt.alias("field-data", FieldPlaceHolder.class);
        xt.alias("field-data", FieldData.class);
        xt.alias("field-data", FactAssignmentField.class);
        xt.alias("field-data", CollectionFieldData.class);
        xt.alias("fixture", Fixture.class);
        xt.alias("retract-fact", RetractFact.class);
        xt.alias("expect-fact", VerifyFact.class);
        xt.alias("expect-field", VerifyField.class);
        xt.alias("expect-rule", VerifyRuleFired.class);


        xt.omitField(ExecutionTrace.class, "rulesFired");

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage("org.drools.guvnor.client", "org.drools.ide.common.client");

        xt.registerConverter(new FieldConverter(xt));

        return xt;
    }


    public String marshal(Scenario sc) {
        if (sc.getFixtures().size() > 1 && sc.getFixtures().get(sc.getFixtures().size() - 1) instanceof ExecutionTrace) {
            Object f = sc.getFixtures().get(sc.getFixtures().size() - 2);

            if (f instanceof Expectation) {
                sc.getFixtures().remove(sc.getFixtures().size() - 1);
            }

        }
        String s = getXStream().toXML(sc);
        return s;
    }

    public Scenario unmarshal(String xml) {
        if (xml == null) return new Scenario();
        if (xml.trim().equals("")) return new Scenario();
        Object o = getXStream().fromXML(xml);
        return (Scenario) o;
    }
}
