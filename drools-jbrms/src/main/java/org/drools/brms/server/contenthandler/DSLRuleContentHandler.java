package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.DSLRuleData;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class DSLRuleContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        
        //TODO: make this read in the DSL files in the current package.
        DSLRuleData data = getDummyDSLSuggestions();
        data.text = text;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        DSLRuleData data = (DSLRuleData) asset.content;
        repoAsset.updateContent( data.text.content );

    }
    
    private DSLRuleData getDummyDSLSuggestions() {
        DSLRuleData data = new DSLRuleData();
        data.lhsSuggestions = new String[] {"The persons name is {name}", "- age is less than {age}"};
        data.rhsSuggestions = new String[] {"Reject claim", "Send notification to [{email}]"};                                                   
        return data;
    }    

}
