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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        InboxPlace that = (InboxPlace) o;

        if ( inboxName != null ? !inboxName.equals( that.inboxName ) : that.inboxName != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inboxName != null ? inboxName.hashCode() : 0;
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
