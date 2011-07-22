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

    public static class Tokenizer implements PlaceTokenizer<SnapshotPlace> {

        private final String PLACE_ID = "TEST_SCENARIO=";
        private final String MODULE_PARAMETER = "?MODULE_NAME=";

        public String getToken(SnapshotPlace place) {
            return PLACE_ID + place.getSnapshotName() + MODULE_PARAMETER + place.getModuleName();
        }

        public SnapshotPlace getPlace(String token) {
            return new SnapshotPlace(
                    subStringModuleName( token ),
                    subStringSnapshotName( token )
            );
        }

        private String subStringSnapshotName(String token) {
            return token.substring( PLACE_ID.length(), token.indexOf( MODULE_PARAMETER ) );
        }

        private String subStringModuleName(String token) {
            return token.substring( token.indexOf( MODULE_PARAMETER ) + MODULE_PARAMETER.length() );
        }
    }
}
