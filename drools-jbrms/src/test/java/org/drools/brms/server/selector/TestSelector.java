package org.drools.brms.server.selector;

import org.drools.repository.AssetItem;

public class TestSelector
    implements
    AssetSelector {

    public boolean isAssetAllowed(AssetItem asset) {

        return true;
    }

}
