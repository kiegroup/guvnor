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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TestScenarioListPlace that = (TestScenarioListPlace) o;

        if ( moduleUuid != null ? !moduleUuid.equals( that.moduleUuid ) : that.moduleUuid != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return moduleUuid != null ? moduleUuid.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<TestScenarioListPlace> {

        public String getToken(TestScenarioListPlace listPlace) {
            return listPlace.getModuleUuid();
        }

        public TestScenarioListPlace getPlace(String token) {
            return new TestScenarioListPlace( token );
        }
    }
}
