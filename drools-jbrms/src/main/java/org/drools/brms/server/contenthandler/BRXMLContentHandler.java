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

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModelData data = (RuleModelData) asset.content;
        repoAsset.updateContent( BRLPersistence.getInstance().toXML( data.model ) );
    }
    
    private SuggestionCompletionEngine getDummySuggestionEngine() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        
        com.factTypes = new String[] {"Person", "Vehicle"};
        
        Map fieldTypes = new HashMap();
        fieldTypes.put("Person.age", SuggestionCompletionEngine.TYPE_NUMERIC);
        fieldTypes.put("Person.name", "String");
        fieldTypes.put("Vehicle.type", "String");
        fieldTypes.put("Vehcile.make", "String");
        com.fieldTypes = fieldTypes;

        Map fieldsForType = new HashMap();
        fieldsForType.put("Person", new String[] {"age", "name"});
        fieldsForType.put("Vehicle", new String[] {"type", "make"});
        com.fieldsForType = fieldsForType;
        
        
        DSLSentence sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[2];
        sen.elements[0] = new DSLSentenceFragment("This is a dsl expression", false);
        sen.elements[1] = new DSLSentenceFragment("(something)", true);
        com.conditionDSLSentences = new DSLSentence[] {sen};
        
        sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[3];
        sen.elements[0] = new DSLSentenceFragment("Send an email to [", false);
        sen.elements[1] = new DSLSentenceFragment("(someone)", true);
        sen.elements[2] = new DSLSentenceFragment("]", false);
                
        DSLSentence sen2 = new DSLSentence();
        sen2.elements = new DSLSentenceFragment[1];
        sen2.elements[0] = new DSLSentenceFragment("do nothing", false);        
        
        com.actionDSLSentences = new DSLSentence[] {sen, sen2};        
        
        return com;
    }    

}
