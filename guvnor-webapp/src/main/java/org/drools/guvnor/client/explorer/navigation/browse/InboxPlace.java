package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class InboxPlace extends Place {

    private final String inboxName;

    public InboxPlace(String inboxName) {
        this.inboxName = inboxName;
    }

    public String getInboxName() {
        return inboxName;
    }

    public static class Tokenizer implements PlaceTokenizer<InboxPlace> {

        public InboxPlace getPlace(String token) {
            return new InboxPlace( token );
        }

        public String getToken(InboxPlace place) {
            return place.getInboxName();
        }
    }
}
