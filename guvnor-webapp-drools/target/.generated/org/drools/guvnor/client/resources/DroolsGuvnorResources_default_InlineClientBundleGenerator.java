package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class DroolsGuvnorResources_default_InlineClientBundleGenerator implements org.drools.guvnor.client.resources.DroolsGuvnorResources {
  private static DroolsGuvnorResources_default_InlineClientBundleGenerator _instance0 = new DroolsGuvnorResources_default_InlineClientBundleGenerator();
  private void droolsGuvnorCssInitializer() {
    droolsGuvnorCss = new org.drools.guvnor.client.resources.DroolsGuvnorCss() {
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
        return "droolsGuvnorCss";
      }
      public String getText() {
        return (".GEI-42PI{border-radius:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#ccc")  + ";}");
      }
      public java.lang.String greyBorderWithRoundCorners(){
        return "GEI-42PI";
      }
      public java.lang.String workItemParameter(){
        return "GEI-42AJ";
      }
    }
    ;
  }
  private static class droolsGuvnorCssInitializer {
    static {
      _instance0.droolsGuvnorCssInitializer();
    }
    static org.drools.guvnor.client.resources.DroolsGuvnorCss get() {
      return droolsGuvnorCss;
    }
  }
  public org.drools.guvnor.client.resources.DroolsGuvnorCss droolsGuvnorCss() {
    return droolsGuvnorCssInitializer.get();
  }
  private void titledTextCellCssInitializer() {
    titledTextCellCss = new org.drools.guvnor.client.resources.TitledTextCellCss() {
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
        return "titledTextCellCss";
      }
      public String getText() {
        return (".GEI-42PK{height:" + ("32px")  + ";vertical-align:" + ("middle")  + ";display:" + ("table-cell")  + ";}.GEI-42AL{font-size:" + ("smaller")  + ";font-style:" + ("italic")  + ";}");
      }
      public java.lang.String container(){
        return "GEI-42PK";
      }
      public java.lang.String description(){
        return "GEI-42AL";
      }
    }
    ;
  }
  private static class titledTextCellCssInitializer {
    static {
      _instance0.titledTextCellCssInitializer();
    }
    static org.drools.guvnor.client.resources.TitledTextCellCss get() {
      return titledTextCellCss;
    }
  }
  public org.drools.guvnor.client.resources.TitledTextCellCss titledTextCellCss() {
    return titledTextCellCssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.resources.DroolsGuvnorCss droolsGuvnorCss;
  private static org.drools.guvnor.client.resources.TitledTextCellCss titledTextCellCss;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      droolsGuvnorCss(), 
      titledTextCellCss(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("droolsGuvnorCss", droolsGuvnorCss());
        resourceMap.put("titledTextCellCss", titledTextCellCss());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'droolsGuvnorCss': return this.@org.drools.guvnor.client.resources.DroolsGuvnorResources::droolsGuvnorCss()();
      case 'titledTextCellCss': return this.@org.drools.guvnor.client.resources.DroolsGuvnorResources::titledTextCellCss()();
    }
    return null;
  }-*/;
}
