package org.drools.guvnor.client.ruleeditor;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DefaultContentUploadEditor;
import org.drools.guvnor.client.decisiontable.DecisionTableXLSWidget;
import org.drools.guvnor.client.decisiontable.GuidedDecisionTableWidget;
import org.drools.guvnor.client.factmodel.FactModelWidget;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.packages.ModelAttachmentFileWidget;
import org.drools.guvnor.client.qa.ScenarioWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;

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


    public static final Map TYPE_IMAGES = getTypeImages();

    /**
     * This will return the appropriate viewer for the asset.
     */
    public static Widget getEditorViewer(RuleAsset asset,
                                         RuleViewer viewer) {
        //depending on the format, load the appropriate editor
        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {
            return new RuleValidatorWrapper( new RuleModeller( asset  ), asset);
        	//return new RuleModeller( asset  );
        } else if ( asset.metaData.format.equals( AssetFormats.DSL_TEMPLATE_RULE ) ) {
            return new RuleValidatorWrapper(new DSLRuleEditor( asset ), asset);
        } else if ( asset.metaData.format.equals( AssetFormats.MODEL ) ) {
            return new ModelAttachmentFileWidget( asset, viewer );
        } else if (asset.metaData.format.equals( AssetFormats.DECISION_SPREADSHEET_XLS )){
            return new RuleValidatorWrapper(new DecisionTableXLSWidget( asset, viewer ), asset);
        } else if (asset.metaData.format.equals( AssetFormats.RULE_FLOW_RF )) {
            return new RuleFlowUploadWidget(asset, viewer);
        } else if (asset.metaData.format.equals( AssetFormats.DRL )) {
            return new RuleValidatorWrapper(new DefaultRuleContentWidget( asset ), asset);
        } else if (asset.metaData.format.equals( AssetFormats.ENUMERATION )) {
            return new RuleValidatorWrapper(new DefaultRuleContentWidget( asset ), asset);
        } else if (asset.metaData.format.equals(AssetFormats.TEST_SCENARIO)) {
        	return new ScenarioWidget(asset);
        } else if (asset.metaData.format.equals(AssetFormats.DECISION_TABLE_GUIDED)) {
        	return new RuleValidatorWrapper(new GuidedDecisionTableWidget(asset), asset);
        } else if (asset.metaData.format.equals(AssetFormats.DRL_MODEL)) {
        	return new RuleValidatorWrapper(new FactModelWidget(asset), asset);
        } else {
            return new DefaultContentUploadEditor( asset, viewer );
        }

    }


    private static Map getTypeImages() {
        Map result = new HashMap();

        result.put( AssetFormats.DRL, "technical_rule_assets.gif" );
        result.put( AssetFormats.DSL, "dsl.gif" );
        result.put( AssetFormats.FUNCTION, "function_assets.gif" );
        result.put( AssetFormats.MODEL, "model_asset.gif" );
        result.put( AssetFormats.DECISION_SPREADSHEET_XLS, "spreadsheet_small.gif" );
        result.put( AssetFormats.BUSINESS_RULE, "business_rule.gif" );
        result.put( AssetFormats.DSL_TEMPLATE_RULE, "business_rule.gif" );
        result.put( AssetFormats.RULE_FLOW_RF, "ruleflow_small.gif" );
        result.put( AssetFormats.TEST_SCENARIO, "test_manager.gif");
        result.put( AssetFormats.ENUMERATION, "enumeration.gif");
        result.put( AssetFormats.DECISION_TABLE_GUIDED, "gdst.gif");

        return result;
    }

    /**
     * Get the icon name (not the path), including the extension, for the appropriate
     * asset format.
     */
    public static String getAssetFormatIcon(String format) {
        String result = (String) TYPE_IMAGES.get( format );
        if (result == null) {
            return "rule_asset.gif";
        } else {
            return result;
        }
    }

}