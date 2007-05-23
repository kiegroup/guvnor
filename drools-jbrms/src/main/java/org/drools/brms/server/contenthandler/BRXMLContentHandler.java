package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;

import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentPackageAssembler;
import org.drools.brms.server.util.BRDRLPersistence;
import org.drools.brms.server.util.BRXMLPersistence;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class BRXMLContentHandler extends ContentHandler implements IRuleAsset {
    

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( item.getContent() );


        
        asset.content = model;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModel data = (RuleModel) asset.content;
        if (data.name == null) {
            data.name = repoAsset.getName();
        }
        repoAsset.updateContent( BRXMLPersistence.getInstance().marshal( data ) );
    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                    IOException {
        builder.addPackageFromDrl( new StringReader(getSourceDRL( asset, builder ) ) );
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf) {
        String drl = getSourceDRL( asset, builder );
        buf.append( drl );
    }

    private String getSourceDRL(AssetItem asset, BRMSPackageBuilder builder) {
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( asset.getContent() );
        String drl = BRDRLPersistence.getInstance().marshal( model );
        if (builder.hasDSL()) {
            drl = builder.getDSLExpander().expand( drl );
        }
        return drl;
    }
}
