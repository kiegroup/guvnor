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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        VerifierPlace that = (VerifierPlace) o;

        if ( moduleUuid != null ? !moduleUuid.equals( that.moduleUuid ) : that.moduleUuid != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return moduleUuid != null ? moduleUuid.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<VerifierPlace> {

        public String getToken(VerifierPlace place) {
            return place.getModuleUuid();
        }

        public VerifierPlace getPlace(String token) {
            return new VerifierPlace( token );
        }
    }
}
