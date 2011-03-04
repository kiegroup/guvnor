package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.PlaceTokenizer;

public class AuthorPerspectivePlace extends Perspective {

    public String getName() {
        return "Author";
    }

    public static class Tokenizer implements PlaceTokenizer<AuthorPerspectivePlace> {

        public String getToken(AuthorPerspectivePlace place) {
            return place.getName();
        }

        public AuthorPerspectivePlace getPlace(String token) {
            return new AuthorPerspectivePlace();
        }
    }

}
