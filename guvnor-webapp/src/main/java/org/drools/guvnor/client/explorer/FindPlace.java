package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class FindPlace extends Place {

    public static class Tokenizer implements PlaceTokenizer<FindPlace> {

        public String getToken( FindPlace place ) {
            return "FIND";
        }

        public FindPlace getPlace( String token ) {
            return new FindPlace();
        }
    }
}
