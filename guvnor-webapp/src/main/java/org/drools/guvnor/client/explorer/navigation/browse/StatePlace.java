package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class StatePlace extends Place {

    private final String stateName;

    public StatePlace(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    public static class Tokenizer implements PlaceTokenizer<StatePlace> {

        private final String PLACE_ID = "STATE=";

        public StatePlace getPlace(String token) {
            return new StatePlace( token.substring( PLACE_ID.length() ) );
        }

        public String getToken(StatePlace place) {
            return PLACE_ID + place.getStateName();
        }
    }
}
