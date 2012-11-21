package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class OperatorsResource_default_InlineClientBundleGenerator implements org.drools.guvnor.client.resources.OperatorsResource {
  private static OperatorsResource_default_InlineClientBundleGenerator _instance0 = new OperatorsResource_default_InlineClientBundleGenerator();
  private void clockInitializer() {
    clock = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "clock",
      externalImage,
      0, 0, 16, 16, false, false
    );
  }
  private static class clockInitializer {
    static {
      _instance0.clockInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return clock;
    }
  }
  public com.google.gwt.resources.client.ImageResource clock() {
    return clockInitializer.get();
  }
  private void operatorsCssInitializer() {
    operatorsCss = new org.drools.guvnor.client.resources.OperatorsCss() {
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
        return "operatorsCss";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVQBORBCJ{padding:" + ("0")  + ";padding:" + ("0")  + ";}.GFVQBORBDJ{width:" + ("25px")  + ";margin-left:" + ("1px")  + ";font-size:" + ("smaller")  + ";}")) : ((".GFVQBORBCJ{padding:" + ("0")  + ";padding:" + ("0")  + ";}.GFVQBORBDJ{width:" + ("25px")  + ";margin-right:" + ("1px")  + ";font-size:" + ("smaller")  + ";}"));
      }
      public java.lang.String container(){
        return "GFVQBORBCJ";
      }
      public java.lang.String parameter(){
        return "GFVQBORBDJ";
      }
    }
    ;
  }
  private static class operatorsCssInitializer {
    static {
      _instance0.operatorsCssInitializer();
    }
    static org.drools.guvnor.client.resources.OperatorsCss get() {
      return operatorsCss;
    }
  }
  public org.drools.guvnor.client.resources.OperatorsCss operatorsCss() {
    return operatorsCssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADIElEQVR42nVTa0iTURg+BNGPon9Rf4KgHxFFQXQjKSiiIuhH0A+jCwRRSIsw1l1rgzQzK7sNV+ac1qyvXF7m7KZbn9typs61NL8mW8xM1/a5y+dn+nl5OmfFoKAXHng47/M8L+flHEJo4RKZITUQzUgD8VK0wUpmkX9qpJbM/9N3JxvJknQDejJz2EwKR9r3fVOiNiTa9odb63Y51E2KXf1WEVOgvLV2N5/0ZEVHe3LEmJm4xRqyMBUw9IRcSLQdHBgfakCk9VCsndsgFTiiuPshjIddgykwfoMPoItbJcd9WlnqUv8IV5EONpyEDCQ36c0Rh1oOxVyPNkzcfh/CU+93mPiv0Jm7cftZN8qbAjB5vqHsnQ2f67ZPjfY/mxysWSoMPKRXCTwgG3vvzxacpSvkQlsAVe4QzLYg+voTiCYkRBIjEEIJVL7uQ1lLEE9ri+A1rR/r0c/tDOj+7EJdZrl2ptqH+44gODqN1XA8ji8Dg/hJOYM0DZRY+qCz+3GR45FlcOalF7lf32E/9dyHkkYBPXSyPDENSZZxr8yAMA1Kjo2h+lUz+C9xXOE+gmmZJx2wp5gXs02duPrYi/CoguGxKYzTqec0Wjwy1+LMJS1eu9zwxRWc1LnBtMyTDtiV3ygeKXVDY/AgJCk0ZAr98SQuF11PmXnPR0QmgPaoguPF78G0zJMO2HGx2p550wZtZQfe+eP4Ti/dn1RgtTsREBMYmgSCMvDiUwwn9a1gWuZJB2w5bdTs1NTgxAMXCjgBEbqwYQVITlFQHqMBEcrPVwpQ6XkwLfOkAzJUJaszjuu6t+XWI5sKrnK98ARjkOg+JFmBl/K8qs9Q3W0G0zAt8/z11tceuX40q6BS3nr2BQ4WvUH2HR7qWw5k32yBqtiGA4UvsTffgsN5Rplp//0rxOFwbPb5fGiy8ZMZx0qxRmXCuhPmFBjfedqIFodz2ulyTTudzk1/ma1W6zyLxSL5/X7U1deLK/flapdn5tiXZ14Qf4PxHA3tJQVBANVOUM/idADHcXMqKip6KcbLy8uXkf+UwWBYZDQa4xQDlC9gZ78AS41uPqDxatkAAAAASUVORK5CYII=";
  private static com.google.gwt.resources.client.ImageResource clock;
  private static org.drools.guvnor.client.resources.OperatorsCss operatorsCss;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      clock(), 
      operatorsCss(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("clock", clock());
        resourceMap.put("operatorsCss", operatorsCss());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'clock': return this.@org.drools.guvnor.client.resources.OperatorsResource::clock()();
      case 'operatorsCss': return this.@org.drools.guvnor.client.resources.OperatorsResource::operatorsCss()();
    }
    return null;
  }-*/;
}
