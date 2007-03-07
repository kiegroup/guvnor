package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleModelData;
import org.drools.brms.server.rules.SuggestionCompletionLoader;
import org.drools.brms.server.util.BRLPersistence;
import org.drools.brms.server.util.SuggestionCompletionEngineBuilder;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.util.asm.ClassFieldInspector;

import com.google.gwt.user.client.rpc.SerializableException;

public class BRXMLContentHandler extends ContentHandler {
    

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleModel model = BRLPersistence.getInstance().toModel( item.getContent() );

        RuleModelData data = new RuleModelData();
        data.model = model;
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        data.completionEngine = loader.getSuggestionEngine( pkg );
        asset.content = data;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModelData data = (RuleModelData) asset.content;
        repoAsset.updateContent( BRLPersistence.getInstance().toXML( data.model ) );
    }
    

}
