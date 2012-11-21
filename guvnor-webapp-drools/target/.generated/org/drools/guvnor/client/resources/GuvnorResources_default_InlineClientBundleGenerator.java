package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class GuvnorResources_default_InlineClientBundleGenerator implements org.drools.guvnor.client.resources.GuvnorResources {
  private static GuvnorResources_default_InlineClientBundleGenerator _instance0 = new GuvnorResources_default_InlineClientBundleGenerator();
  private void guvnorCssInitializer() {
    guvnorCss = new org.drools.guvnor.client.resources.GuvnorCss() {
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
        return "guvnorCss";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42DJ{background-color:" + ("orange")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GEI-42EJ{margin-right:" + ("10px")  + ";margin-top:" + ("6px")  + ";}.GEI-42BJ{background-color:" + ("#f0f0f0")  + ";border-top-color:" + ("#d0d0d0")  + ";border-top-style:" + ("solid")  + ";border-top-width:") + (("1px")  + ";padding-top:" + ("12px")  + ";padding-right:" + ("8px")  + ";width:" + ("100%")  + ";height:" + ("48px")  + ";}.GEI-42CJ{text-align:" + ("center")  + ";font-weight:" + ("bold")  + ";margin:" + ("8px"+ " " +"8px"+ " " +"8px"+ " " +"8px")  + ";}")) : ((".GEI-42DJ{background-color:" + ("orange")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GEI-42EJ{margin-left:" + ("10px")  + ";margin-top:" + ("6px")  + ";}.GEI-42BJ{background-color:" + ("#f0f0f0")  + ";border-top-color:" + ("#d0d0d0")  + ";border-top-style:" + ("solid")  + ";border-top-width:") + (("1px")  + ";padding-top:" + ("12px")  + ";padding-left:" + ("8px")  + ";width:" + ("100%")  + ";height:" + ("48px")  + ";}.GEI-42CJ{text-align:" + ("center")  + ";font-weight:" + ("bold")  + ";margin:" + ("8px"+ " " +"8px"+ " " +"8px"+ " " +"8px")  + ";}"));
      }
      public java.lang.String popupListWidgetButtonBar(){
        return "GEI-42BJ";
      }
      public java.lang.String popupListWidgetCaption(){
        return "GEI-42CJ";
      }
      public java.lang.String warningContainer(){
        return "GEI-42DJ";
      }
      public java.lang.String warningMessage(){
        return "GEI-42EJ";
      }
    }
    ;
  }
  private static class guvnorCssInitializer {
    static {
      _instance0.guvnorCssInitializer();
    }
    static org.drools.guvnor.client.resources.GuvnorCss get() {
      return guvnorCss;
    }
  }
  public org.drools.guvnor.client.resources.GuvnorCss guvnorCss() {
    return guvnorCssInitializer.get();
  }
  private void headerCssInitializer() {
    headerCss = new org.drools.guvnor.client.resources.HeaderCss() {
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
        return "headerCss";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42HJ{height:" + ((GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getURL() + "\") -" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getLeft() + "px -" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getTop() + "px  repeat-x")  + ";height:" + ("50px")  + ";}.GEI-42GJ{float:" + ("right")  + ";}.GEI-42FJ{float:" + ("left")  + ";padding-left:" + ("10px")  + ";}.GEI-42JJ{color:" + ("#fff")  + ";font-size:" + ("0.9em")  + ";}.GEI-42JJ a{color:" + ("#fff")  + ";}.GEI-42IJ{text-align:") + (("left")  + ";}")) : ((".GEI-42HJ{height:" + ((GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getURL() + "\") -" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getLeft() + "px -" + (GuvnorResources_default_InlineClientBundleGenerator.this.jbossrulesBlue()).getTop() + "px  repeat-x")  + ";height:" + ("50px")  + ";}.GEI-42GJ{float:" + ("left")  + ";}.GEI-42FJ{float:" + ("right")  + ";padding-right:" + ("10px")  + ";}.GEI-42JJ{color:" + ("#fff")  + ";font-size:" + ("0.9em")  + ";}.GEI-42JJ a{color:" + ("#fff")  + ";}.GEI-42IJ{text-align:") + (("right")  + ";}"));
      }
      public java.lang.String controlsClass(){
        return "GEI-42FJ";
      }
      public java.lang.String logoClass(){
        return "GEI-42GJ";
      }
      public java.lang.String mainClass(){
        return "GEI-42HJ";
      }
      public java.lang.String perspectivesClass(){
        return "GEI-42IJ";
      }
      public java.lang.String userInfoClass(){
        return "GEI-42JJ";
      }
    }
    ;
  }
  private static class headerCssInitializer {
    static {
      _instance0.headerCssInitializer();
    }
    static org.drools.guvnor.client.resources.HeaderCss get() {
      return headerCss;
    }
  }
  public org.drools.guvnor.client.resources.HeaderCss headerCss() {
    return headerCssInitializer.get();
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
  private void jbossrulesBlueInitializer() {
    jbossrulesBlue = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "jbossrulesBlue",
      externalImage,
      0, 0, 1, 70, false, false
    );
  }
  private static class jbossrulesBlueInitializer {
    static {
      _instance0.jbossrulesBlueInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return jbossrulesBlue;
    }
  }
  public com.google.gwt.resources.client.ImageResource jbossrulesBlue() {
    return jbossrulesBlueInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.resources.GuvnorCss guvnorCss;
  private static org.drools.guvnor.client.resources.HeaderCss headerCss;
  private static org.drools.guvnor.client.resources.TitledTextCellCss titledTextCellCss;
  private static final java.lang.String externalImage = "data:image/gif;base64,R0lGODlhAQBGAMQAADtPZTNFWjdKXys8TzVIXS4+UjhLYTZJXjJEWThMYig5TCo6TTpOZCg4SzFDVzlNYis7TjRGXCw9UCw+US5AUy0+UjpPZSk6TTFDWDlOYzBBVTBCViQzRTFCVzRGWjlMYiH5BAAAAAAALAAAAAABAEYAAAUmYKNcCzQM0lQVFKVtnYMhQeBFRH4IgmF8iUeGwbAAjsikcslEckIAOw==";
  private static com.google.gwt.resources.client.ImageResource jbossrulesBlue;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      guvnorCss(), 
      headerCss(), 
      titledTextCellCss(), 
      jbossrulesBlue(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("guvnorCss", guvnorCss());
        resourceMap.put("headerCss", headerCss());
        resourceMap.put("titledTextCellCss", titledTextCellCss());
        resourceMap.put("jbossrulesBlue", jbossrulesBlue());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'guvnorCss': return this.@org.drools.guvnor.client.resources.GuvnorResources::guvnorCss()();
      case 'headerCss': return this.@org.drools.guvnor.client.resources.GuvnorResources::headerCss()();
      case 'titledTextCellCss': return this.@org.drools.guvnor.client.resources.GuvnorResources::titledTextCellCss()();
      case 'jbossrulesBlue': return this.@org.drools.guvnor.client.resources.GuvnorResources::jbossrulesBlue()();
    }
    return null;
  }-*/;
}
