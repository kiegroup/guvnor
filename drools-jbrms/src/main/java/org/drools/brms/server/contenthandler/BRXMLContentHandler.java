package org.drools.brms.server.contenthandler;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.DSLSentenceFragment;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleModelData;
import org.drools.brms.server.util.BRLPersistence;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class BRXMLContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializableException {
        RuleModel model = BRLPersistence.getInstance().toModel( item.getContent() );

        RuleModelData data = new RuleModelData();
        data.model = model;
        //TODO: replace with the code that loads it from a cache server side.
        //otherwise it will look at the current package, and then work out the model from that.
        data.completionEngine = getDummySuggestionEngine();
        asset.content = data;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModelData data = (RuleModelData) asset.content;
        repoAsset.updateContent( BRLPersistence.getInstance().toXML( data.model ) );
    }
    
    private SuggestionCompletionEngine getDummySuggestionEngine() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        
        com.factTypes = new String[] {"Board", "Order", "Clothing"};
        
        Map fieldTypes = new HashMap();
        fieldTypes.put("Board.size", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Board.cost", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Board.type", SuggestionCompletionEngine.TYPE_STRING);
        fieldTypes.put("Board.name", SuggestionCompletionEngine.TYPE_STRING);        
        fieldTypes.put("Order.value", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Order.quantity", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Clothing.value", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Clothing.type", SuggestionCompletionEngine.TYPE_STRING);
        com.fieldTypes = fieldTypes;

        Map fieldsForType = new HashMap();
        fieldsForType.put("Board", new String[] {"size", "cost", "type", "name"});
        fieldsForType.put("Order", new String[] {"value", "quantity"});
        fieldsForType.put("Clothing", new String[] {"value", "type"});
        com.fieldsForType = fieldsForType;
        
        
        DSLSentence sen = new DSLSentence();
//        sen.elements = new DSLSentenceFragment[2];
//        sen.elements[0] = new DSLSentenceFragment("Notify manufacturing", false);
//        sen.elements[1] = new DSLSentenceFragment("(something)", true);
//        com.conditionDSLSentences = new DSLSentence[] {sen};
        
        sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[3];
        sen.elements[0] = new DSLSentenceFragment("Notify manufacturing with warning [", false);
        sen.elements[1] = new DSLSentenceFragment("(quantity of items description)", true);
        sen.elements[2] = new DSLSentenceFragment("]", false);
                
        DSLSentence sen2 = new DSLSentence();
        sen2.elements = new DSLSentenceFragment[1];
        sen2.elements[0] = new DSLSentenceFragment("Reject order (too many items)", false);
        
        com.actionDSLSentences = new DSLSentence[] {sen, sen2};        
        
        return com;
    }    

}
