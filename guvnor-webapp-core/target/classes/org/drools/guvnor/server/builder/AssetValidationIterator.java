package org.drools.guvnor.server.builder;

import org.drools.repository.AssetItem;

import java.util.Iterator;

public class AssetValidationIterator implements Iterator<AssetItem> {

    private final Iterator<AssetItem> assetItemIterator;
    private AssetItem assetItemUnderValidation;

    public AssetValidationIterator(Iterator<AssetItem> assetItemIterator) {
        this.assetItemIterator = assetItemIterator;
    }

    public void setAssetItemUnderValidation(AssetItem assetItemUnderValidation) {
        this.assetItemUnderValidation = assetItemUnderValidation;
    }

    public boolean hasNext() {
        return assetItemIterator.hasNext();
    }

    public AssetItem next() {
        AssetItem assetItem = assetItemIterator.next();


        if (assetItemUnderValidation != null && assetItem.getUUID().equals(assetItemUnderValidation.getUUID())) {
            return this.assetItemUnderValidation;
        }

        return assetItem;
    }

    public void remove() {
        assetItemIterator.remove();
    }
}
