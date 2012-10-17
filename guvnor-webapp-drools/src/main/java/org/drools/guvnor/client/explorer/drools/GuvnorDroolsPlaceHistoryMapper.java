package org.drools.guvnor.client.explorer.drools;

import com.google.gwt.place.shared.WithTokenizers;
import org.drools.guvnor.client.explorer.GuvnorPlaceHistoryMapper;
import org.drools.guvnor.client.explorer.navigation.browse.StatePlace;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotAssetListPlace;

@WithTokenizers(
        {
                StatePlace.Tokenizer.class,
                SnapshotAssetListPlace.Tokenizer.class
        }
)
public interface GuvnorDroolsPlaceHistoryMapper extends GuvnorPlaceHistoryMapper {

}
