package com.google.gwt.user.cellview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CellList_Resources_default_InlineClientBundleGenerator implements com.google.gwt.user.cellview.client.CellList.Resources {
  private static CellList_Resources_default_InlineClientBundleGenerator _instance0 = new CellList_Resources_default_InlineClientBundleGenerator();
  private void cellListSelectedBackgroundInitializer() {
    cellListSelectedBackground = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellListSelectedBackground",
      com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl : externalImage,
      0, 0, 82, 26, false, false
    );
  }
  private static class cellListSelectedBackgroundInitializer {
    static {
      _instance0.cellListSelectedBackgroundInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellListSelectedBackground;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellListSelectedBackground() {
    return cellListSelectedBackgroundInitializer.get();
  }
  private void cellListStyleInitializer() {
    cellListStyle = new com.google.gwt.user.cellview.client.CellList.Style() {
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
        return "cellListStyle";
      }
      public String getText() {
        return (".GFVQBORBEB,.GFVQBORBGB{cursor:" + ("pointer")  + ";zoom:" + ("1")  + ";}.GFVQBORBFB{background:" + ("#ffc")  + ";}.GFVQBORBHB{height:" + ((CellList_Resources_default_InlineClientBundleGenerator.this.cellListSelectedBackground()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CellList_Resources_default_InlineClientBundleGenerator.this.cellListSelectedBackground()).getURL() + "\") -" + (CellList_Resources_default_InlineClientBundleGenerator.this.cellListSelectedBackground()).getLeft() + "px -" + (CellList_Resources_default_InlineClientBundleGenerator.this.cellListSelectedBackground()).getTop() + "px  repeat-x")  + ";background-color:" + ("#628cd5")  + ";color:" + ("white")  + ";height:" + ("auto")  + ";overflow:" + ("visible")  + ";}");
      }
      public java.lang.String cellListEvenItem(){
        return "GFVQBORBEB";
      }
      public java.lang.String cellListKeyboardSelectedItem(){
        return "GFVQBORBFB";
      }
      public java.lang.String cellListOddItem(){
        return "GFVQBORBGB";
      }
      public java.lang.String cellListSelectedItem(){
        return "GFVQBORBHB";
      }
      public java.lang.String cellListWidget(){
        return "GFVQBORBIB";
      }
    }
    ;
  }
  private static class cellListStyleInitializer {
    static {
      _instance0.cellListStyleInitializer();
    }
    static com.google.gwt.user.cellview.client.CellList.Style get() {
      return cellListStyle;
    }
  }
  public com.google.gwt.user.cellview.client.CellList.Style cellListStyle() {
    return cellListStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/png;base64,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";
  private static final java.lang.String externalImage_rtl = "data:image/png;base64,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";
  private static com.google.gwt.resources.client.ImageResource cellListSelectedBackground;
  private static com.google.gwt.user.cellview.client.CellList.Style cellListStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      cellListSelectedBackground(), 
      cellListStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("cellListSelectedBackground", cellListSelectedBackground());
        resourceMap.put("cellListStyle", cellListStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'cellListSelectedBackground': return this.@com.google.gwt.user.cellview.client.CellList.Resources::cellListSelectedBackground()();
      case 'cellListStyle': return this.@com.google.gwt.user.cellview.client.CellList.Resources::cellListStyle()();
    }
    return null;
  }-*/;
}
