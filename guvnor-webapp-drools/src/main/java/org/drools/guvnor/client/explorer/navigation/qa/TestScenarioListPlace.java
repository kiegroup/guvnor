package org.drools.guvnor.client.explorer.navigation.qa;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public class TestScenarioListPlace extends DefaultPlaceRequest {

    public TestScenarioListPlace(String moduleUuid) {
        super("testScenarioList");
        addParameter("moduleUuid", moduleUuid);
    }

}
