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
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.decisiontable.DecisionTableXLSWidget;
import org.drools.guvnor.client.decisiontable.GuidedDecisionTableWidget;
import org.drools.guvnor.client.explorer.Preferences;
import org.drools.guvnor.client.factmodel.FactModelWidget;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.modeldriven.ui.RuleModellerWidgetFactory;
import org.drools.guvnor.client.modeldriven.ui.RuleTemplateEditor;
import org.drools.guvnor.client.packages.ModelAttachmentFileWidget;
import org.drools.guvnor.client.qa.ScenarioWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

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

    public static final Map<String, String> TYPE_IMAGES = getTypeImages();

    /**
     * This will return the appropriate viewer for the asset.
     */
	public static Widget getEditorViewer(RuleAsset asset, RuleViewer viewer) {
		RulePackageSelector.currentlySelectedPackage = asset.metaData.packageName;
		// depending on the format, load the appropriate editor
		if (asset.metaData.format.equals(AssetFormats.BUSINESS_RULE)) {
			return new RuleModeller(asset, new RuleModellerWidgetFactory());
		} else if (asset.metaData.format.equals(AssetFormats.DSL_TEMPLATE_RULE)) {
			return new RuleValidatorWrapper(new DSLRuleEditor(asset), asset);
		} else if (asset.metaData.format.equals(AssetFormats.BPEL_PACKAGE)
				&& Preferences.getBooleanPref("flex-bpel-editor")) {
			return new BPELWrapper(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.MODEL)) {
			return new ModelAttachmentFileWidget(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.DECISION_SPREADSHEET_XLS)) {
			return new DecisionTableXLSWidget(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.RULE_FLOW_RF)) {
			return new RuleFlowWrapper(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.BPMN2_PROCESS)) {
			return new RuleFlowWrapper(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.DRL)) {
			return new DrlEditor(asset);
		} else if (asset.metaData.format.equals(AssetFormats.ENUMERATION)) {
			return new DefaultRuleContentWidget(asset);
		} else if (asset.metaData.format.equals(AssetFormats.TEST_SCENARIO)) {
			return new ScenarioWidget(asset);
		} else if (asset.metaData.format.equals(AssetFormats.DECISION_TABLE_GUIDED)) {
			return new GuidedDecisionTableWidget(asset);
		} else if (asset.metaData.format.equals(AssetFormats.DRL_MODEL)) {
			return new FactModelWidget(asset);
		} else if (asset.metaData.format.equals(AssetFormats.DSL)) {
			return new DefaultRuleContentWidget(asset);
		} else if (asset.metaData.format.equals(AssetFormats.PROPERTIES)) {
			return new PropertiesWidget(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.XML)) {
			return new XmlFileWidget(asset, viewer);
		} else if (asset.metaData.format.equals(AssetFormats.FUNCTION)) {
			return new FunctionEditor(asset);
		} else if (asset.metaData.format.equals(AssetFormats.WORKING_SET)) {
			return new WorkingSetEditor(asset);
        } else if (asset.metaData.format.equals(AssetFormats.RULE_TEMPLATE)) {
            return new RuleTemplateEditor(asset);
		} else {
			return new DefaultContentUploadEditor(asset, viewer);
		}

	}

    private static Map<String, String> getTypeImages() {
        Map<String, String> result = new HashMap<String, String>();

        result.put( AssetFormats.DRL,
                    "technical_rule_assets.gif" );
        result.put( AssetFormats.DSL,
                    "dsl.gif" );
        result.put( AssetFormats.FUNCTION,
                    "function_assets.gif" );
        result.put( AssetFormats.MODEL,
                    "model_asset.gif" );
        result.put( AssetFormats.DECISION_SPREADSHEET_XLS,
                    "spreadsheet_small.gif" );
        result.put( AssetFormats.BUSINESS_RULE,
                    "business_rule.gif" );
        result.put( AssetFormats.DSL_TEMPLATE_RULE,
                    "business_rule.gif" );
        result.put( AssetFormats.RULE_FLOW_RF,
                    "ruleflow_small.gif" );
        result.put( AssetFormats.BPMN2_PROCESS,
                    "ruleflow_small.gif" );
        result.put( AssetFormats.TEST_SCENARIO,
                    "test_manager.gif" );
        result.put( AssetFormats.ENUMERATION,
                    "enumeration.gif" );
        result.put( AssetFormats.DECISION_TABLE_GUIDED,
                    "gdst.gif" );

        return result;
    }

    /**
     * Returns a css style for background with the icon that belongs for the format.
     * @param format
     * @return
     */
    public static String getAssetFormatBGStyle(String format) {
        String style = getTypeStyles().get( format );

        if ( style == null ) {
            return "bg_rule_asset";
        } else {
            return style;
        }
    }

    private static Map<String, String> getTypeStyles() {
        Map<String, String> result = new HashMap<String, String>();

        result.put( AssetFormats.DRL,
                    "bg_technical_rule_assets" );
        result.put( AssetFormats.DSL,
                    "bg_dsl" );
        result.put( AssetFormats.FUNCTION,
                    "bg_function_assets" );
        result.put( AssetFormats.MODEL,
                    "bg_model_asset" );
        result.put( AssetFormats.DECISION_SPREADSHEET_XLS,
                    "bg_spreadsheet_small" );
        result.put( AssetFormats.BUSINESS_RULE,
                    "bg_business_rule" );
        result.put( AssetFormats.DSL_TEMPLATE_RULE,
                    "bg_business_rule" );
        result.put( AssetFormats.RULE_FLOW_RF,
                    "bg_ruleflow_small" );
        result.put( AssetFormats.BPMN2_PROCESS,
                    "bg_ruleflow_small" );
        result.put( AssetFormats.TEST_SCENARIO,
                    "bg_test_manager" );
        result.put( AssetFormats.ENUMERATION,
                    "bg_enumeration" );
        result.put( AssetFormats.DECISION_TABLE_GUIDED,
                    "bg_gdst" );

        return result;
    }

    /**
     * Get the icon name (not the path), including the extension, for the appropriate
     * asset format.
     */
    public static String getAssetFormatIcon(String format) {
        String result = (String) TYPE_IMAGES.get( format );
        if ( result == null ) {
            return "rule_asset.gif";
        } else {
            return result;
        }
    }

}