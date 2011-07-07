package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ModuleEditorPlace extends Place {

    private String uuid;

    public ModuleEditorPlace( String uuid ) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public static class Tokenizer implements PlaceTokenizer<ModuleEditorPlace> {

        private final String PLACE_ID = "MODULE=";

        public String getToken( ModuleEditorPlace place ) {
            return PLACE_ID + place.getUuid();
        }

        public ModuleEditorPlace getPlace( String token ) {
            return new ModuleEditorPlace( token.substring( PLACE_ID.length() ) );
        }
    }
}
