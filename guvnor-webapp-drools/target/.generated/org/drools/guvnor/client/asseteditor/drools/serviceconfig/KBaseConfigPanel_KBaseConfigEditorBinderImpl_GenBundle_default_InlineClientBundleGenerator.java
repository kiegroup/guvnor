package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle {
  private static KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenCss_style() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "style";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42JN{font-weight:" + ("bold")  + ";text-align:" + ("center")  + ";}.GEI-42IN{float:" + ("right")  + ";}.GEI-42HN{border-bottom:" + ("1px"+ " " +"solid"+ " " +"#ccc")  + ";text-align:" + ("right")  + ";margin-bottom:" + ("4px")  + ";}")) : ((".GEI-42JN{font-weight:" + ("bold")  + ";text-align:" + ("center")  + ";}.GEI-42IN{float:" + ("left")  + ";}.GEI-42HN{border-bottom:" + ("1px"+ " " +"solid"+ " " +"#ccc")  + ";text-align:" + ("left")  + ";margin-bottom:" + ("4px")  + ";}"));
      }
      public java.lang.String cellTable(){
        return "GEI-42HN";
      }
      public java.lang.String horizontalChild(){
        return "GEI-42IN";
      }
      public java.lang.String palleteLabel(){
        return "GEI-42JN";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenCss_style style;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      style(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("style", style());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'style': return this.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
