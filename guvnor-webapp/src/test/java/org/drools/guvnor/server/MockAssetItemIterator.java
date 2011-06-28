package org.drools.guvnor.server;

import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.VersionedAssetItemIterator;

import javax.jcr.NodeIterator;
import java.util.Arrays;
import java.util.Iterator;

import static org.mockito.Mockito.mock;

public class MockAssetItemIterator extends VersionedAssetItemIterator {

    private Iterator<AssetItem> assetItems;

    public MockAssetItemIterator() {
        super(mock(NodeIterator.class), mock(RulesRepository.class), new String[0]);
    }

    public boolean hasNext() {
        return assetItems.hasNext();
    }

    public AssetItem next() {
        return assetItems.next();
    }

    public void setAssets(AssetItem... assetItems) {
        this.assetItems = Arrays.asList(assetItems).iterator();
    }
}
