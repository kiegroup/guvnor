package org.drools.guvnor.client.explorer.navigation.deployment;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class SnapshotPlace extends Place {

    private final String moduleName;
    private final String snapshotName;

    public SnapshotPlace(String moduleName,
                         String snapshotName) {
        this.moduleName = moduleName;
        this.snapshotName = snapshotName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SnapshotPlace that = (SnapshotPlace) o;

        if ( moduleName != null ? !moduleName.equals( that.moduleName ) : that.moduleName != null ) return false;
        if ( snapshotName != null ? !snapshotName.equals( that.snapshotName ) : that.snapshotName != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleName != null ? moduleName.hashCode() : 0;
        result = 31 * result + (snapshotName != null ? snapshotName.hashCode() : 0);
        return result;
    }

    public static class Tokenizer implements PlaceTokenizer<SnapshotPlace> {

        private final String PLACE_ID = "SNAPSHOT=";
        private final String MODULE_PARAMETER = "?MODULE_NAME=";

        public SnapshotPlace getPlace(String token) {
            return new SnapshotPlace(
                    subStringModuleName( token ),
                    subStringSnapshotName( token ) );
        }

        public String getToken(SnapshotPlace place) {
            return PLACE_ID + place.getSnapshotName() + MODULE_PARAMETER + place.getModuleName();
        }

        private String subStringSnapshotName(String token) {
            return token.substring( PLACE_ID.length(), token.indexOf( MODULE_PARAMETER ) );
        }

        private String subStringModuleName(String token) {
            return token.substring( token.indexOf( MODULE_PARAMETER ) + MODULE_PARAMETER.length() );
        }
    }
}
