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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        CategoryPlace that = (CategoryPlace) o;

        if ( categoryPath != null ? !categoryPath.equals( that.categoryPath ) : that.categoryPath != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return categoryPath != null ? categoryPath.hashCode() : 0;
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
