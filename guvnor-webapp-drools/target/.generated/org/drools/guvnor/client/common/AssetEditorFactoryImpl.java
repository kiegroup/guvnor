package org.drools.guvnor.client.common;

import java.util.Map;
import java.util.List;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.explorer.ClientFactory;
import com.google.gwt.event.shared.EventBus;

public class AssetEditorFactoryImpl implements org.drools.guvnor.client.common.AssetEditorFactory {
    private static DroolsGuvnorImages images = GWT.create(DroolsGuvnorImages.class);
    private static Constants constants = GWT.create(Constants.class);
    public String[] getRegisteredAssetEditorFormats() {
      String[] formats = new String[] {
      "brl", "dslr", "xls", "gdst", "template", "drl", "function", "dsl", "jar", "model.drl", "changeset", "rf", "bpmn", "bpmn2", "formdef", "rf", "enumeration", "scenario", "properties", "xml", "", "workingset", "springContext", "serviceConfig", "wid"};
      return formats;
    }
    public Widget getAssetEditor(Asset asset, RuleViewer viewer, ClientFactory clientFactory, EventBus eventBus) {
      if(asset.getFormat().equals("brl")) {
        return new org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("dslr")) {
        return new org.drools.guvnor.client.asseteditor.drools.RuleValidatorWrapper(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("xls")) {
        return new org.drools.guvnor.client.decisiontable.DecisionTableXLSWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("gdst")) {
        return new org.drools.guvnor.client.decisiontable.GuidedDecisionTableWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("template")) {
        return new org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates.RuleTemplateEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("drl")) {
        return new org.drools.guvnor.client.asseteditor.drools.DrlEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("function")) {
        return new org.drools.guvnor.client.asseteditor.drools.FunctionEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("dsl")) {
        return new org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("jar")) {
        return new org.drools.guvnor.client.asseteditor.POJOModelUploadWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("model.drl")) {
        return new org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("changeset")) {
        return new org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("rf")) {
        return new org.drools.guvnor.client.asseteditor.RuleFlowWrapper(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("bpmn")) {
        return new org.drools.guvnor.client.asseteditor.BusinessProcessEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("bpmn2")) {
        return new org.drools.guvnor.client.asseteditor.BusinessProcessEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("formdef")) {
        return new org.drools.guvnor.client.asseteditor.FormEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("rf")) {
        return new org.drools.guvnor.client.asseteditor.RuleFlowWrapper(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("enumeration")) {
        return new org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("scenario")) {
        return new org.drools.guvnor.client.explorer.navigation.qa.testscenarios.ScenarioWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("properties")) {
        return new org.drools.guvnor.client.asseteditor.drools.PropertiesWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("xml")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("")) {
        return new org.drools.guvnor.client.asseteditor.drools.PropertiesWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("workingset")) {
        return new org.drools.guvnor.client.asseteditor.drools.WorkingSetEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("springContext")) {
        return new org.drools.guvnor.client.asseteditor.drools.springcontext.SpringContextEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("serviceConfig")) {
        return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("wid")) {
        return new org.drools.guvnor.client.asseteditor.drools.workitem.WorkitemDefinitionEditor(asset, viewer, clientFactory, eventBus);
      }
      return new DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
    }
    public ImageResource getAssetEditorIcon(String format) {
      if(format.equals("brl")) {
        return images.ruleAsset();
      }
      if(format.equals("dslr")) {
        return images.ruleAsset();
      }
      if(format.equals("xls")) {
        return images.spreadsheetSmall();
      }
      if(format.equals("gdst")) {
        return images.gdst();
      }
      if(format.equals("template")) {
        return images.ruleAsset();
      }
      if(format.equals("drl")) {
        return images.technicalRuleAssets();
      }
      if(format.equals("function")) {
        return images.functionAssets();
      }
      if(format.equals("dsl")) {
        return images.dsl();
      }
      if(format.equals("jar")) {
        return images.modelAsset();
      }
      if(format.equals("model.drl")) {
        return images.modelAsset();
      }
      if(format.equals("changeset")) {
        return images.enumeration();
      }
      if(format.equals("rf")) {
        return images.ruleflowSmall();
      }
      if(format.equals("bpmn")) {
        return images.ruleflowSmall();
      }
      if(format.equals("bpmn2")) {
        return images.ruleflowSmall();
      }
      if(format.equals("formdef")) {
        return images.formDefIcon();
      }
      if(format.equals("rf")) {
        return images.ruleflowSmall();
      }
      if(format.equals("enumeration")) {
        return images.enumeration();
      }
      if(format.equals("scenario")) {
        return images.testManager();
      }
      if(format.equals("properties")) {
        return images.newFile();
      }
      if(format.equals("xml")) {
        return images.newFile();
      }
      if(format.equals("")) {
        return images.newFile();
      }
      if(format.equals("workingset")) {
        return images.workingset();
      }
      if(format.equals("springContext")) {
        return images.enumeration();
      }
      if(format.equals("serviceConfig")) {
        return images.enumeration();
      }
      if(format.equals("wid")) {
        return images.enumeration();
      }
      return images.ruleAsset();
    }
    public String getAssetEditorTitle(String format) {
      if(format.equals("brl")) {
        return constants.BusinessRuleAssets();
      }
      if(format.equals("dslr")) {
        return constants.BusinessRuleAssets();
      }
      if(format.equals("xls")) {
        return constants.BusinessRuleAssets();
      }
      if(format.equals("gdst")) {
        return constants.BusinessRuleAssets();
      }
      if(format.equals("template")) {
        return constants.BusinessRuleAssets();
      }
      if(format.equals("drl")) {
        return constants.TechnicalRuleAssets();
      }
      if(format.equals("function")) {
        return constants.Functions();
      }
      if(format.equals("dsl")) {
        return constants.DSLConfigurations();
      }
      if(format.equals("jar")) {
        return constants.Model();
      }
      if(format.equals("model.drl")) {
        return constants.Model();
      }
      if(format.equals("changeset")) {
        return constants.ChangeSets();
      }
      if(format.equals("rf")) {
        return constants.RuleFlows();
      }
      if(format.equals("bpmn")) {
        return constants.RuleFlows();
      }
      if(format.equals("bpmn2")) {
        return constants.RuleFlows();
      }
      if(format.equals("formdef")) {
        return constants.FormDefinition();
      }
      if(format.equals("rf")) {
        return constants.RuleFlows();
      }
      if(format.equals("enumeration")) {
        return constants.Enumerations();
      }
      if(format.equals("scenario")) {
        return constants.TestScenarios();
      }
      if(format.equals("properties")) {
        return constants.XMLProperties();
      }
      if(format.equals("xml")) {
        return constants.XMLProperties();
      }
      if(format.equals("")) {
        return constants.OtherAssetsDocumentation();
      }
      if(format.equals("workingset")) {
        return constants.WorkingSets();
      }
      if(format.equals("springContext")) {
        return constants.SpringContext();
      }
      if(format.equals("serviceConfig")) {
        return constants.ServiceConfig();
      }
      if(format.equals("wid")) {
        return constants.WorkItemDefinition();
      }
      return "";
    }
  }
