package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenBundle {
  private static StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42AN{float:" + ("right")  + ";font-family:" + ("Arial"+ " " +"Unicode"+ " " +"MS"+ ","+ " " +"Arial"+ ","+ " " +"sans-serif")  + ";font-size:" + ("small")  + ";}")) : ((".GEI-42AN{float:" + ("left")  + ";font-family:" + ("Arial"+ " " +"Unicode"+ " " +"MS"+ ","+ " " +"Arial"+ ","+ " " +"sans-serif")  + ";font-size:" + ("small")  + ";}"));
      }
      public java.lang.String floatLeft(){
        return "GEI-42AN";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.drools.guvnor.client.common.StackItemHeaderViewImpl_HeaderHTMLBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
