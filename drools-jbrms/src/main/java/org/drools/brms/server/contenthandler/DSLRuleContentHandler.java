package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.DSLRuleData;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class DSLRuleContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        
        //TODO: make this read in the DSL files in the current package.
        DSLRuleData data = getDummyDSLSuggestions();
        data.text = text;

        asset.content = data;
        
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        DSLRuleData data = (DSLRuleData) asset.content;
        repoAsset.updateContent( data.text.content );

    }
    
    private DSLRuleData getDummyDSLSuggestions() {
        DSLRuleData data = new DSLRuleData();
        data.lhsSuggestions = new String[] {"The surboard cosmetic configuration", "- colour1 is {colour}", "- colour2 is {colour}", "- colour3 is {colour}", "- graphic is large", "- graphic is normal",
                                            "The surfboard shape", "- full malibu", "- mini mal", "- standard short", "- long short", "- stunt"};
        data.rhsSuggestions = new String[] {"Reject configuration", 
                                            "Send notification to manufacturing '{email}'",
                                            "Accept configuration",
                                            "Send notification to sales '{email}'"};                                                   
        return data;
    }    

}
