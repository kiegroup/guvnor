package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ManagerPlace extends Place {

    private final int id;

    public ManagerPlace(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ManagerPlace that = (ManagerPlace) o;

        if ( id != that.id ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class Tokenizer implements PlaceTokenizer<ManagerPlace> {

        private final String PLACE_ID = "MANAGER=";

        public String getToken(ManagerPlace place) {
            return PLACE_ID + place.getId();
        }

        public ManagerPlace getPlace(String token) {
            return new ManagerPlace( new Integer( token.substring( PLACE_ID.length() ) ) );
        }
    }
}
