package org.kie.guvnor.testscenario.model;


import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

@Portable
public class TestScenarioModelContent {

    private Scenario scenario;

    private DataModelOracle oracle;


    public TestScenarioModelContent() {

    }

    public TestScenarioModelContent(Scenario scenario, DataModelOracle oracle) {
        this.scenario = scenario;
        this.oracle = oracle;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public DataModelOracle getOracle() {
        return oracle;
    }
}
