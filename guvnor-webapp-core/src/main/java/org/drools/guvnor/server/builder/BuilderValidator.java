package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.utils.Validator;

import java.util.Iterator;

public class BuilderValidator extends PackageAssemblerBase implements Validator {
    public static final String DEFAULT = "default";
    private AssetItem assetItemUnderValidation;

    public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration) {
        this.moduleItem = moduleItem;
        createBuilder();
    }

    public BuilderResult validateAsset(AssetItem item) {
        assetItemUnderValidation = item;
        this.moduleItem = item.getModule();
        createBuilder();
        if (setUpPackage()) {
            buildAsset(item);
        }
        return getResult();
    }

    public boolean validate(AssetItem item) {
        try {
            return !validateAsset(item).hasLines();
        } catch (RuntimeException re) {
            return false;
        }
    }

    public String getFormat() {
        return DEFAULT;
    }

    public BuilderResult getResult() {
        BuilderResult result = new BuilderResult();
        result.addLines(new BuilderResultHelper().generateBuilderResults(getErrors()));
        return result;
    }

    protected Iterator<AssetItem> getAssetItemIterator(String... formats) {
        AssetValidationIterator assetValidationIterator = new AssetValidationIterator(super.getAssetItemIterator(formats));
        assetValidationIterator.setAssetItemUnderValidation(assetItemUnderValidation);
        return assetValidationIterator;
    }
}
