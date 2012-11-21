package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class PercentageBar_PercentageBarBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenBundle {
  private static PercentageBar_PercentageBarBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new PercentageBar_PercentageBarBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenCss_style() {
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
        return (".GEI-42AO{background-color:" + ("white")  + ";border:" + ("1px"+ " " +"solid"+ " " +"black")  + ";}.GEI-42PN{position:" + ("absolute")  + ";text-align:" + ("center")  + ";}");
      }
      public java.lang.String text(){
        return "GEI-42PN";
      }
      public java.lang.String wrapper(){
        return "GEI-42AO";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
