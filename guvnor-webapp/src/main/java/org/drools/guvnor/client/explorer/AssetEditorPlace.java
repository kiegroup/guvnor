package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AssetEditorPlace extends Place {

    private final String uuid;

    public AssetEditorPlace( String uuid ) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public static class Tokenizer implements PlaceTokenizer<AssetEditorPlace> {

        private final String PLACE_ID = "ASSET=";

        public String getToken( AssetEditorPlace place ) {
            return PLACE_ID + place.getUuid();
        }

        public AssetEditorPlace getPlace( String token ) {
            return new AssetEditorPlace( token.substring( PLACE_ID.length() ) );
        }
    }
}
