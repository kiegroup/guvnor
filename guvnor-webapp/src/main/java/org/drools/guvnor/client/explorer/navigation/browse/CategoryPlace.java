package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class CategoryPlace extends Place {

    private final String categoryPath;

    public CategoryPlace(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public static class Tokenizer implements PlaceTokenizer<CategoryPlace> {

        public CategoryPlace getPlace(String token) {
            return new CategoryPlace( token );
        }

        public String getToken(CategoryPlace place) {
            return place.getCategoryPath();
        }
    }
}
