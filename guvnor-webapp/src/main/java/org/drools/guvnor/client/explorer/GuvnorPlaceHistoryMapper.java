package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({AuthorPerspectivePlace.Tokenizer.class, IFramePerspectivePlace.Tokenizer.class})
public interface GuvnorPlaceHistoryMapper extends PlaceHistoryMapper {
}
