package org.drools.guvnor.client.explorer.navigation.deployment;


import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import java.util.Arrays;

public class SnapshotAssetListPlace extends Place {

    private final String snapshotName;
    private final String moduleUuid;
    private final String[] assetTypes;

    public SnapshotAssetListPlace(final String snapshotName,
                                  final String moduleUuid,
                                  final String[] assetTypes) {
        this.snapshotName = snapshotName;
        this.moduleUuid = moduleUuid;
        this.assetTypes = assetTypes;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public String getModuleUuid() {
        return moduleUuid;
    }

    public String[] getAssetTypes() {
        return assetTypes;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SnapshotAssetListPlace that = (SnapshotAssetListPlace) o;

        if ( !Arrays.equals( assetTypes, that.assetTypes ) ) return false;
        if ( moduleUuid != null ? !moduleUuid.equals( that.moduleUuid ) : that.moduleUuid != null ) return false;
        if ( snapshotName != null ? !snapshotName.equals( that.snapshotName ) : that.snapshotName != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = snapshotName != null ? snapshotName.hashCode() : 0;
        result = 31 * result + (moduleUuid != null ? moduleUuid.hashCode() : 0);
        result = 31 * result + (assetTypes != null ? Arrays.hashCode( assetTypes ) : 0);
        return result;
    }

    public static class Tokenizer implements PlaceTokenizer<SnapshotAssetListPlace> {

        private final String PLACE_ID = "SNAPSHOT_ASSET_LIST=";
        private final String MODULE_PARAMETER = "?MODULE_UUID=";
        private final String FORMATS = "&FORMATS=";

        public String getToken(SnapshotAssetListPlace place) {
            return PLACE_ID + place.getSnapshotName() + MODULE_PARAMETER + place.getModuleUuid() + FORMATS + formatsToString( place.getAssetTypes() );
        }

        public SnapshotAssetListPlace getPlace(String token) {
            return new SnapshotAssetListPlace(
                    subStringSnapshotName( token ),
                    subStringModuleUuid( token ),
                    subStringFormats( token )
            );
        }

        private String subStringSnapshotName(String token) {
            return token.substring( PLACE_ID.length(), token.indexOf( MODULE_PARAMETER ) );
        }

        private String subStringModuleUuid(String token) {
            return token.substring( token.indexOf( MODULE_PARAMETER ) + MODULE_PARAMETER.length(), token.indexOf( FORMATS ) );
        }

        private String[] subStringFormats(String token) {
            return token.substring( token.indexOf( FORMATS ) + FORMATS.length() ).split( "," );
        }

        private String formatsToString(String[] assetTypes) {
            String result = assetTypes[0];
            for (int i = 1; i < assetTypes.length; i++) {
                result += ",";
                result += assetTypes[i];
            }
            return result;
        }
    }
}
