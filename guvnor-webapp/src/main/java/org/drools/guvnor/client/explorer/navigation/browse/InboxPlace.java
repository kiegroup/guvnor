package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class InboxPlace extends Place {

    private final String inboxType;

    public InboxPlace(String inboxType) {
        this.inboxType = inboxType;
    }

    public String getInboxType() {
        return inboxType;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        InboxPlace that = (InboxPlace) o;

        if ( inboxType != null ? !inboxType.equals( that.inboxType ) : that.inboxType != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inboxType != null ? inboxType.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<InboxPlace> {

        public InboxPlace getPlace(String token) {
            return new InboxPlace( token );
        }

        public String getToken(InboxPlace place) {
            return place.getInboxType();
        }
    }
}
