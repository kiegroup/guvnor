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

package org.drools.guvnor.client.ruleeditor;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DefaultContentUploadEditor;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.decisiontable.DecisionTableXLSWidget;
import org.drools.guvnor.client.decisiontable.GuidedDecisionTableWidget;
import org.drools.guvnor.client.explorer.Preferences;
import org.drools.guvnor.client.factmodel.FactModelsWidget;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.modeldriven.ui.RuleModellerWidgetFactory;
import org.drools.guvnor.client.modeldriven.ui.RuleTemplateEditor;
import org.drools.guvnor.client.packages.ModelAttachmentFileWidget;
import org.drools.guvnor.client.processeditor.BusinessProcessEditor;
import org.drools.guvnor.client.qa.testscenarios.ScenarioWidget;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.core.client.GWT;
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

    private static Images                                    images      = GWT.create( Images.class );

    public static final Map<String, RuleFormatImageResource> TYPE_IMAGES = getTypeImages();
    private static RepositoryServiceAsync                    SERVICE     = RepositoryServiceFactory.getService();
    public static Boolean                                    HOSTED_MODE = Boolean.FALSE;

    static {
        SERVICE.isHostedMode( new GenericCallback<Boolean>() {
            public void onSuccess(Boolean result) {
                if ( result.booleanValue() ) {
                    HOSTED_MODE = Boolean.TRUE;
                }
            }
        } );
    }

    /**
     * This will return the appropriate viewer for the asset.
     */
    public static Widget getEditorViewer(RuleAsset asset,
                                         RuleViewer viewer) {
        RulePackageSelector.currentlySelectedPackage = asset.metaData.packageName;
        //depending on the format, load the appropriate editor
        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {
            return new RuleModeller( asset,
                                     new RuleModellerWidgetFactory() );
        } else if ( asset.metaData.format.equals( AssetFormats.DSL_TEMPLATE_RULE ) ) {
            return new RuleValidatorWrapper( new DSLRuleEditor( asset ) );
        } else if ( asset.metaData.format.equals( AssetFormats.BPEL_PACKAGE ) && Preferences.getBooleanPref( "flex-bpel-editor" ) ) {
            return new BPELWrapper( asset,
                                    viewer );
        } else if ( asset.metaData.format.equals( AssetFormats.MODEL ) ) {
            return new ModelAttachmentFileWidget( asset,
                                                  viewer );
        } else if ( asset.metaData.format.equals( AssetFormats.DECISION_SPREADSHEET_XLS ) ) {
            return new DecisionTableXLSWidget( asset,
                                               viewer );

        } else if ( asset.metaData.format.equals( AssetFormats.RULE_FLOW_RF ) ) {
            return new RuleFlowWrapper( asset,
                                        viewer );
        } else if ( asset.metaData.format.equals( AssetFormats.BPMN2_PROCESS ) && Preferences.getBooleanPref( "oryx-bpmn-editor" ) ) {
            return new BusinessProcessEditor( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.DRL ) ) {
            return new DrlEditor( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.ENUMERATION ) ) {
            return new DefaultRuleContentWidget( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.TEST_SCENARIO ) ) {
            return new ScenarioWidget( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.DECISION_TABLE_GUIDED ) ) {
            return new GuidedDecisionTableWidget( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.DRL_MODEL ) ) {
            return new FactModelsWidget( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.DSL ) ) {
            return new DefaultRuleContentWidget( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.PROPERTIES ) ) {
            return new PropertiesWidget( asset,
                                         viewer );
        } else if ( asset.metaData.format.equals( AssetFormats.XML ) ) {
            return new XmlFileWidget( asset,
                                      viewer );
        } else if ( asset.metaData.format.equals( AssetFormats.FUNCTION ) ) {
            return new FunctionEditor( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.WORKING_SET ) ) {
            return new WorkingSetEditor( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.RULE_TEMPLATE ) ) {
            return new RuleTemplateEditor( asset );
        } else {
            return new DefaultContentUploadEditor( asset,
                                                   viewer );
        }

    }

    private static Map<String, RuleFormatImageResource> getTypeImages() {
        Map<String, RuleFormatImageResource> result = new HashMap<String, RuleFormatImageResource>();

        result.put( AssetFormats.DRL,
                    new RuleFormatImageResource( AssetFormats.DRL,
                                                 images.technicalRuleAssets() ) );
        result.put( AssetFormats.DSL,
                    new RuleFormatImageResource( AssetFormats.DSL,
                                                 images.dsl() ) );
        result.put( AssetFormats.FUNCTION,
                    new RuleFormatImageResource( AssetFormats.FUNCTION,
                                                 images.functionAssets() ) );
        result.put( AssetFormats.MODEL,
                    new RuleFormatImageResource( AssetFormats.MODEL,
                                                 images.modelAsset() ) );
        result.put( AssetFormats.DECISION_SPREADSHEET_XLS,
                    new RuleFormatImageResource( AssetFormats.DECISION_SPREADSHEET_XLS,
                                                 images.spreadsheetSmall() ) );
        result.put( AssetFormats.BUSINESS_RULE,
                    new RuleFormatImageResource( AssetFormats.BUSINESS_RULE,
                                                 images.businessRule() ) );
        result.put( AssetFormats.DSL_TEMPLATE_RULE,
                    new RuleFormatImageResource( AssetFormats.DSL_TEMPLATE_RULE,
                                                 images.businessRule() ) );
        result.put( AssetFormats.RULE_FLOW_RF,
                    new RuleFormatImageResource( AssetFormats.RULE_FLOW_RF,
                                                 images.ruleflowSmall() ) );
        result.put( AssetFormats.BPMN2_PROCESS,
                    new RuleFormatImageResource( AssetFormats.BPMN2_PROCESS,
                                                 images.ruleflowSmall() ) );
        result.put( AssetFormats.TEST_SCENARIO,
                    new RuleFormatImageResource( AssetFormats.TEST_SCENARIO,
                                                 images.testManager() ) );
        result.put( AssetFormats.ENUMERATION,
                    new RuleFormatImageResource( AssetFormats.ENUMERATION,
                                                 images.enumeration() ) );
        result.put( AssetFormats.DECISION_TABLE_GUIDED,
                    new RuleFormatImageResource( AssetFormats.DECISION_TABLE_GUIDED,
                                                 images.gdst() ) );

        return result;
    }

    /**
     * Get the icon name (not the path), including the extension, for the appropriate
     * asset format.
     */
    public static RuleFormatImageResource getAssetFormatIcon(String format) {
        RuleFormatImageResource result = TYPE_IMAGES.get( format );
        if ( result == null ) {
            return new RuleFormatImageResource( format,
                                                images.ruleAsset() );
        } else {
            return result;
        }
    }

}