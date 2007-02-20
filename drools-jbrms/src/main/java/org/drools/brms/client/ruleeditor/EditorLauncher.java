package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.modeldriven.ui.RuleModeller;
import org.drools.brms.client.packages.ModelAttachmentFileWidget;
import org.drools.brms.client.rpc.DSLRuleData;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleModelData;

import com.google.gwt.user.client.ui.Widget;

/**
 * This launches the appropriate editor for the asset type.
 * This uses the format attribute to determine the appropriate editor, and
 * ALSO to unpackage the content payload from the generic asset RPC object.
 * 
 * NOTE: when adding new editors for asset types, this will also need to be enhanced to load 
 * it up/unpackage it correctly for the editor.
 * The editors will make changes to the rpc objects in place, and when checking in the whole RPC 
 * objects will be sent back to the server.
 * 
 * @author Michael Neale
 */
public class EditorLauncher {

    /**
     * This will return the appropriate viewer for the asset.
     */
    public static Widget getEditorViewer(RuleAsset asset, RuleViewer viewer) {
        //depending on the format, load the appropriate editor
        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {
            return new RuleModeller( getSuggestionCompletionEngine(asset), getRuleModel(asset) );
        } else if (asset.metaData.format.equals( AssetFormats.DSL_TEMPLATE_RULE )){
            DSLRuleData data = (DSLRuleData) asset.content;            
            return new DSLRuleEditor( data.text, data.lhsSuggestions, data.rhsSuggestions );
        } else if (asset.metaData.format.equals( AssetFormats.MODEL ) ) {
            return new ModelAttachmentFileWidget(asset, viewer);
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
