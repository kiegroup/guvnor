package org.drools.guvnor.client.perspective;

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
import org.drools.guvnor.client.rpc.Module;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.moduleeditor.AbstractModuleEditor;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.common.StackItemHeader;
import org.drools.guvnor.client.util.Util;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

public class PerspectiveFactoryImpl implements org.drools.guvnor.client.perspective.PerspectiveFactory {
    private static DroolsGuvnorImages images = GWT.create(DroolsGuvnorImages.class);
    private static Constants constants = GWT.create(Constants.class);
    public String[] getRegisteredAssetEditorFormats(String moduleType) {
      String formats = "";
      if("package".equals(moduleType)) {
        formats = "brl,dslr,xls,gdst,template,drl,function,dsl,jar,model.drl,rf,formdef,bpmn,bpmn2,rf,enumeration,scenario,properties,xml,,workingset,springContext,wid,changeset";
      }
      String[] results = formats.split(",");
      return results;
    }
    public String[] getRegisteredModuleEditorFormats(String perspectiveType) {
      if("author".equals(perspectiveType)) {
        String[] formats = new String[] {
        "package"};
        return formats;
      }
      return null;
    }
    public String[] getRegisteredPerspectiveTypes() {
      String[] formats = new String[] {"author", "runtime"};
      return formats;
    }
    public AbstractModuleEditor getModuleEditor(Module module, ClientFactory clientFactory, EventBus eventBus, boolean isHistoryReadOnly, Command refreshCommand) {
      if(module.getFormat().equals("package")) {
        return new org.drools.guvnor.client.moduleeditor.drools.PackageEditor(module, clientFactory, eventBus, isHistoryReadOnly, refreshCommand);
      }
      return null;
    }
    public Perspective getPerspective(String perspectiveType) {
      if("author".equals(perspectiveType)) {
        return new org.drools.guvnor.client.perspective.author.AuthorPerspective();
      }
      if("runtime".equals(perspectiveType)) {
        return new org.drools.guvnor.client.perspective.runtime.RunTimePerspective();
      }
      return null;
    }
    public IsWidget  getModulesHeaderView(String perspectiveType) {
      String title;
      ImageResource image;
      if("author".equals(perspectiveType)) {
        title = constants.KnowledgeBases();
        image = images.packages();
      }
      title = constants.KnowledgeBases();
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
      if("author".equals(perspectiveType)) {
        title = constants.Packages();
        image = images.packages();
      }
      title = constants.Packages();
      image = images.packages();
      return Util.getHeader( image, title );
    }
    public Widget getModulesNewAssetMenu(String perspectiveType, ClientFactory clientFactory, EventBus eventBus) {
      if("author".equals(perspectiveType)) {
        return (new org.drools.guvnor.client.asseteditor.drools.PackagesNewAssetMenu( clientFactory, eventBus )).asWidget();
      }
      return (new org.drools.guvnor.client.asseteditor.drools.PackagesNewAssetMenu( clientFactory, eventBus )).asWidget();
    }
    public Widget getModuleEditorActionToolbar(Module data,  ClientFactory clientFactory, EventBus eventBus, boolean readOnly, Command refreshCommand) {
      if("package".equals(data.getFormat())) {
        return new org.drools.guvnor.client.widgets.drools.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);
      }
      return new org.drools.guvnor.client.widgets.drools.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);
    }
    public Widget getAssetEditorActionToolbar(String perspectiveType, Asset asset, Widget editor, ClientFactory clientFactory, EventBus eventBus, boolean readOnly) {
      if("author".equals(perspectiveType)) {
        return new org.drools.guvnor.client.widgets.drools.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);
      }
      return new org.drools.guvnor.client.widgets.drools.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);
    }
  }
