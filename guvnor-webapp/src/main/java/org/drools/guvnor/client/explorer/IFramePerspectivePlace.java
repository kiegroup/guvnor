package org.drools.guvnor.client.explorer;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceTokenizer;

public class IFramePerspectivePlace extends Perspective {

    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Tokenizer implements PlaceTokenizer<IFramePerspectivePlace> {

        public String getToken(IFramePerspectivePlace place) {
            return URL.encode(place.getName());
        }

        public IFramePerspectivePlace getPlace(String token) {
            return new IFramePerspectivePlace();
        }
    }

}
