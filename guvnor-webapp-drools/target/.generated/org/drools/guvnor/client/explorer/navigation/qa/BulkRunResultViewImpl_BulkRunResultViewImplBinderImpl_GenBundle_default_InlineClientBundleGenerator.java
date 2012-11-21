package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle {
  private static BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42LN{font-weight:" + ("bold")  + ";}.GEI-42KN{vertical-align:" + ("top")  + ";text-align:" + ("right")  + ";}")) : ((".GEI-42LN{font-weight:" + ("bold")  + ";}.GEI-42KN{vertical-align:" + ("top")  + ";text-align:" + ("left")  + ";}"));
      }
      public java.lang.String alignTop(){
        return "GEI-42KN";
      }
      public java.lang.String bold(){
        return "GEI-42LN";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
