package org.drools.guvnor.client.common;

import java.util.Map;
import java.util.List;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.explorer.ClientFactory;
import com.google.gwt.event.shared.EventBus;

public class AssetEditorFactoryImpl implements org.drools.guvnor.client.common.AssetEditorFactory {
    private static Images images = GWT.create(Images.class);
    private static Constants constants = GWT.create(Constants.class);
    public String[] getRegisteredAssetEditorFormats() {
      String[] formats = new String[] {
      "rf", "bpmn", "bpmn2", "rf", "enumeration", "xml", "xmlschema", "samplemessages", "wsdl", "jar", "esbconfig", "smooks", "scenariodiagram", "choreographymodel", "deploymentarchive", "jmsdestination", "doc", "other"};
      return formats;
    }
    public Widget getAssetEditor(Asset asset, RuleViewer viewer, ClientFactory clientFactory, EventBus eventBus) {
      if(asset.getFormat().equals("rf")) {
        return new org.drools.guvnor.client.asseteditor.RuleFlowWrapper(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("bpmn")) {
        return new org.drools.guvnor.client.asseteditor.BusinessProcessEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("bpmn2")) {
        return new org.drools.guvnor.client.asseteditor.BusinessProcessEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("rf")) {
        return new org.drools.guvnor.client.asseteditor.RuleFlowWrapper(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("enumeration")) {
        return new org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("xml")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("xmlschema")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("samplemessages")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("wsdl")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("jar")) {
        return new org.drools.guvnor.client.asseteditor.soa.JarFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("esbconfig")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("smooks")) {
        return new org.drools.guvnor.client.asseteditor.XmlFileWidget(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("scenariodiagram")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("choreographymodel")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("deploymentarchive")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("jmsdestination")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("doc")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      if(asset.getFormat().equals("other")) {
        return new org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
      }
      return new DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);
    }
    public ImageResource getAssetEditorIcon(String format) {
      if(format.equals("rf")) {
        return images.ruleflowSmall();
      }
      if(format.equals("bpmn")) {
        return images.ruleflowSmall();
      }
      if(format.equals("bpmn2")) {
        return images.ruleflowSmall();
      }
      if(format.equals("rf")) {
        return images.ruleflowSmall();
      }
      if(format.equals("enumeration")) {
        return images.enumeration();
      }
      if(format.equals("xml")) {
        return images.newFile();
      }
      if(format.equals("xmlschema")) {
        return images.ruleAsset();
      }
      if(format.equals("samplemessages")) {
        return images.gdst();
      }
      if(format.equals("wsdl")) {
        return images.technicalRuleAssets();
      }
      if(format.equals("jar")) {
        return images.modelAsset();
      }
      if(format.equals("esbconfig")) {
        return images.dsl();
      }
      if(format.equals("smooks")) {
        return images.functionAssets();
      }
      if(format.equals("scenariodiagram")) {
        return images.enumeration();
      }
      if(format.equals("choreographymodel")) {
        return images.testManager();
      }
      if(format.equals("deploymentarchive")) {
        return images.modelAsset();
      }
      if(format.equals("jmsdestination")) {
        return images.workingset();
      }
      if(format.equals("doc")) {
        return images.eventLogSmall();
      }
      if(format.equals("other")) {
        return images.newFile();
      }
      return images.ruleAsset();
    }
    public String getAssetEditorTitle(String format) {
      if(format.equals("rf")) {
        return constants.RuleFlows();
      }
      if(format.equals("bpmn")) {
        return constants.RuleFlows();
      }
      if(format.equals("bpmn2")) {
        return constants.RuleFlows();
      }
      if(format.equals("rf")) {
        return constants.RuleFlows();
      }
      if(format.equals("enumeration")) {
        return constants.Enumerations();
      }
      if(format.equals("xml")) {
        return constants.XMLProperties();
      }
      if(format.equals("xmlschema")) {
        return constants.XMLSchemas();
      }
      if(format.equals("samplemessages")) {
        return constants.SampleMessages();
      }
      if(format.equals("wsdl")) {
        return constants.WSDLs();
      }
      if(format.equals("jar")) {
        return constants.Jar();
      }
      if(format.equals("esbconfig")) {
        return constants.JBOSSESBConfig();
      }
      if(format.equals("smooks")) {
        return constants.Smooks();
      }
      if(format.equals("scenariodiagram")) {
        return constants.ScenarioDiagrams();
      }
      if(format.equals("choreographymodel")) {
        return constants.ChoreographyModels();
      }
      if(format.equals("deploymentarchive")) {
        return constants.DeploymentArchives();
      }
      if(format.equals("jmsdestination")) {
        return constants.JMSDestinations();
      }
      if(format.equals("doc")) {
        return constants.Documentation();
      }
      if(format.equals("other")) {
        return constants.Other();
      }
      return "";
    }
  }
