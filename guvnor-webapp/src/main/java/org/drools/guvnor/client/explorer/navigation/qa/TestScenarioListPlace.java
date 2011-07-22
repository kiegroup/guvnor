package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TestScenarioListPlace extends Place {

    private final String moduleUuid;

    public TestScenarioListPlace(String moduleUuid) {
        this.moduleUuid = moduleUuid;
    }

    public String getModuleUuid() {
        return moduleUuid;
    }

    public static class Tokenizer implements PlaceTokenizer<TestScenarioListPlace> {

        private final String PLACE_ID = "TEST_SCENARIO=";

        public String getToken(TestScenarioListPlace listPlace) {
            return PLACE_ID + listPlace.getModuleUuid();
        }

        public TestScenarioListPlace getPlace(String token) {
            return new TestScenarioListPlace( token.substring( PLACE_ID.length() ) );
        }
    }
}
