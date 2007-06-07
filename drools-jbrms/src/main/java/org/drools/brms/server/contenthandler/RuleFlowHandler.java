package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class RuleFlowHandler extends ContentHandler implements IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf) {
        //do nothing... as no change to source.
    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, ErrorLogger logger) throws DroolsParserException,
                                                                                        IOException {
        builder.addRuleFlow( new InputStreamReader(asset.getBinaryContentAttachment()) );
    }

}
