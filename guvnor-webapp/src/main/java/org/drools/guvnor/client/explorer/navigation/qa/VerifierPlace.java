package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class VerifierPlace extends Place {

    private final String moduleUuid;

    public VerifierPlace(String moduleUuid) {
        this.moduleUuid = moduleUuid;
    }

    public String getModuleUuid() {
        return moduleUuid;
    }

    public static class Tokenizer implements PlaceTokenizer<VerifierPlace> {

        private final String PLACE_ID = "VERIFIER=";

        public String getToken(VerifierPlace place) {
            return PLACE_ID + place.getModuleUuid();
        }

        public VerifierPlace getPlace(String token) {
            return new VerifierPlace( token.substring( PLACE_ID.length() ) );
        }
    }
}
