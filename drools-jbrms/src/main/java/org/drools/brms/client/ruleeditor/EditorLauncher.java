package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.modeldriven.ui.RuleModeller;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleModelData;

import com.google.gwt.user.client.ui.Widget;

/**
 * This launches the appropriate editor for the asset type.
 * 
 * @author Michael Neale
 */
public class EditorLauncher {

    /**
     * This will return the appropriate viewer for the asset.
     */
    public static Widget getEditorViewer(RuleAsset asset) {
        //depending on the format, load the appropriate editor
        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {
            return new RuleModeller( getSuggestionCompletionEngine(asset), getRuleModel(asset) );
        } else {
            return new DefaultRuleContentWidget( asset );
        }

    }

    private static SuggestionCompletionEngine getSuggestionCompletionEngine(RuleAsset asset) {
        RuleModelData data = (RuleModelData) asset.content;
        return data.completionEngine;
    }

    private static RuleModel getRuleModel(RuleAsset asset) {
        RuleModelData data = (RuleModelData) asset.content;        
        return (RuleModel) data.model;
    }


}
