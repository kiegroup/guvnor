package org.drools.guvnor.client.perspective;

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
import org.drools.guvnor.client.rpc.Module;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.moduleeditor.AbstractModuleEditor;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.common.StackItemHeader;
import org.drools.guvnor.client.util.Util;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

public class PerspectiveFactoryImpl implements org.drools.guvnor.client.perspective.PerspectiveFactory {
    private static Images images = GWT.create(Images.class);
    private static Constants constants = GWT.create(Constants.class);
    public String[] getRegisteredAssetEditorFormats(String moduleType) {
      String formats = "";
      if("soaservice".equals(moduleType)) {
        formats = "xmlschema,samplemessages,wsdl,jar,bpmn2,esbconfig,smooks,scenariodiagram,choreographymodel,deploymentarchive,jmsdestination,doc,other";
      }
      String[] results = formats.split(",");
      return results;
    }
    public String[] getRegisteredModuleEditorFormats(String perspectiveType) {
      if("soaservice".equals(perspectiveType)) {
        String[] formats = new String[] {
        "soaservice"};
        return formats;
      }
      return null;
    }
    public String[] getRegisteredPerspectiveTypes() {
      String[] formats = new String[] {"soaservice"};
      return formats;
    }
    public AbstractModuleEditor getModuleEditor(Module module, ClientFactory clientFactory, EventBus eventBus, boolean isHistoryReadOnly, Command refreshCommand) {
      if(module.getFormat().equals("soaservice")) {
        return new org.drools.guvnor.client.moduleeditor.soa.SOAServiceEditor(module, clientFactory, eventBus, isHistoryReadOnly, refreshCommand);
      }
      return null;
    }
    public Perspective getPerspective(String perspectiveType) {
      if("soaservice".equals(perspectiveType)) {
        return new org.drools.guvnor.client.perspective.soa.SOAPerspective();
      }
      return null;
    }
    public IsWidget  getModulesHeaderView(String perspectiveType) {
      String title;
      ImageResource image;
      if("soaservice".equals(perspectiveType)) {
        title = "Services";
        image = images.packages();
      }
      title = "Services";
      image = images.packages();
      StackItemHeaderViewImpl view = new StackItemHeaderViewImpl();
      StackItemHeader header = new StackItemHeader( view );
      header.setName( title );
      header.setImageResource( image );
      return view;
    }
    public SafeHtml getModulesTreeRootNodeHeader(String perspectiveType) {
      String title;
      ImageResource image;
      if("soaservice".equals(perspectiveType)) {
        title = "Services";
        image = images.chartOrganisation();
      }
      title = "Services";
      image = images.packages();
      return Util.getHeader( image, title );
    }
    public Widget getModulesNewAssetMenu(String perspectiveType, ClientFactory clientFactory, EventBus eventBus) {
      if("soaservice".equals(perspectiveType)) {
        return (new org.drools.guvnor.client.asseteditor.soa.SOAServicesNewAssetMenu( clientFactory, eventBus )).asWidget();
      }
      return (new org.drools.guvnor.client.asseteditor.soa.SOAServicesNewAssetMenu( clientFactory, eventBus )).asWidget();
    }
    public Widget getModuleEditorActionToolbar(Module data,  ClientFactory clientFactory, EventBus eventBus, boolean readOnly, Command refreshCommand) {
      if("soaservice".equals(data.getFormat())) {
        return new org.drools.guvnor.client.widgets.soa.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);
      }
      return new org.drools.guvnor.client.widgets.soa.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);
    }
    public Widget getAssetEditorActionToolbar(String perspectiveType, Asset asset, Widget editor, ClientFactory clientFactory, EventBus eventBus, boolean readOnly) {
      if("soaservice".equals(perspectiveType)) {
        return new org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);
      }
      return new org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);
    }
  }
