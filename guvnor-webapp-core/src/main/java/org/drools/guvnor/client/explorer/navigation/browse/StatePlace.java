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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        StatePlace that = (StatePlace) o;

        if ( stateName != null ? !stateName.equals( that.stateName ) : that.stateName != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return stateName != null ? stateName.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<StatePlace> {


        public StatePlace getPlace(String token) {
            return new StatePlace( token );
        }

        public String getToken(StatePlace place) {
            return place.getStateName();
        }
    }
}
