package org.drools.guvnor.server.selector;

import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.repository.AssetItem;

public class TestSelector
    implements
    AssetSelector {

    public boolean isAssetAllowed(AssetItem asset) {

        return true;
    }

}
