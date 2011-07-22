package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerPlace;

@WithTokenizers(
        {
                FindPlace.Tokenizer.class,
                AssetEditorPlace.Tokenizer.class,
                ModuleEditorPlace.Tokenizer.class,
                AssetViewerPlace.Tokenizer.class,
                ManagerPlace.Tokenizer.class
        }
)
public interface GuvnorPlaceHistoryMapper extends PlaceHistoryMapper {
}
