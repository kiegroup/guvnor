package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers(
        {
                AuthorPerspectivePlace.Tokenizer.class,
                IFramePerspectivePlace.Tokenizer.class,
                FindPlace.Tokenizer.class,
                AssetEditorPlace.Tokenizer.class,
                ModuleEditorPlace.Tokenizer.class
        }
)
public interface GuvnorPlaceHistoryMapper extends PlaceHistoryMapper {
}
