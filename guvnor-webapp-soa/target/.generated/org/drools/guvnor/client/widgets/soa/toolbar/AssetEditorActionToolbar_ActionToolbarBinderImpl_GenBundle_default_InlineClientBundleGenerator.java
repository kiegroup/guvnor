package org.drools.guvnor.client.widgets.soa.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle {
  private static AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenCss_style() {
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
        return (".GFVQBORBIM{cursor:" + ("default")  + ";}.GFVQBORBIM .GFVQBORBJM{cursor:" + ("pointer")  + ";color:" + ("#666")  + ";font-weight:" + ("bold")  + ";padding:" + ("0"+ " " +"10px")  + ";vertical-align:" + ("bottom")  + ";}.GFVQBORBKM{color:" + ("#666")  + ";font-weight:" + ("bold")  + ";font-style:" + ("italic")  + ";padding:" + ("0"+ " " +"10px")  + ";vertical-align:") + (("bottom")  + ";}");
      }
      public java.lang.String menuBar(){
        return "GFVQBORBIM";
      }
      public java.lang.String menuItem(){
        return "GFVQBORBJM";
      }
      public java.lang.String statusLabel(){
        return "GFVQBORBKM";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
