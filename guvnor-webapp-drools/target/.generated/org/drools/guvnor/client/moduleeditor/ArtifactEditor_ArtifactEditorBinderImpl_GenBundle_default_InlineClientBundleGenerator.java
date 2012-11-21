package org.drools.guvnor.client.moduleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ArtifactEditor_ArtifactEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenBundle {
  private static ArtifactEditor_ArtifactEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ArtifactEditor_ArtifactEditorBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenCss_style() {
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
        return (".GEI-42MN{min-width:" + ("640px")  + ";vertical-align:" + ("top")  + ";}.GEI-42NN{vertical-align:" + ("top")  + ";}.GEI-42ON{width:" + ("150px")  + ";}");
      }
      public java.lang.String editor(){
        return "GEI-42MN";
      }
      public java.lang.String metadata(){
        return "GEI-42NN";
      }
      public java.lang.String metadataWidget(){
        return "GEI-42ON";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
