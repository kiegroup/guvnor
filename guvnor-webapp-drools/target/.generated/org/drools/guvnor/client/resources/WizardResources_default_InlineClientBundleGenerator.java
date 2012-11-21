package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class WizardResources_default_InlineClientBundleGenerator implements org.drools.guvnor.client.resources.WizardResources {
  private static WizardResources_default_InlineClientBundleGenerator _instance0 = new WizardResources_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.drools.guvnor.client.resources.WizardResources.WizardStyle() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEI-42PL{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("none")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("0"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GEI-42OL{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GEI-42JL{margin-top:" + ("100px")  + ";margin-right:" + ("10px")  + ";margin-left:" + ("10px") ) + (";}.GEI-42JL td{margin-bottom:" + ("10px")  + ";width:" + ("48px")  + ";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GEI-42NL{margin-right:" + ("3px")  + ";margin-left:" + ("3px")  + ";}.GEI-42BM{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GEI-42AM{margin-right:") + (("10px")  + ";margin-top:" + ("6px")  + ";}.GEI-42DM{font-weight:" + ("bolder")  + ";color:" + ("red")  + ";}.GEI-42HM{width:" + ("200px")  + ";height:" + ("100%")  + ";border-left:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-left:" + ("5px")  + ";margin-right:" + ("2px")  + ";overflow:" + ("auto")  + ";}.GEI-42IM{width:" + ("5px") ) + (";}.GEI-42IL{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GEI-42EM{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px")  + ";padding-right:") + (("2px")  + ";}.GEI-42EM:HOVER{background-color:" + ("#cdcdcd")  + ";}.GEI-42FM{width:" + ("16px")  + ";margin-left:" + ("5px")  + ";}.GEI-42GM{width:" + ("180px")  + ";}.GEI-42ML{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GEI-42LL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:" + ("#900")  + ";}.GEI-42CM{margin:" + ("10px") ) + (";}.GEI-42KL{margin-right:" + ("10px")  + ";margin-left:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GEI-42HL{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px")  + ";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:") + (("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}")) : ((".GEI-42PL{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("none")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("0"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GEI-42OL{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GEI-42JL{margin-top:" + ("100px")  + ";margin-left:" + ("10px")  + ";margin-right:" + ("10px") ) + (";}.GEI-42JL td{margin-bottom:" + ("10px")  + ";width:" + ("48px")  + ";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GEI-42NL{margin-left:" + ("3px")  + ";margin-right:" + ("3px")  + ";}.GEI-42BM{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GEI-42AM{margin-left:") + (("10px")  + ";margin-top:" + ("6px")  + ";}.GEI-42DM{font-weight:" + ("bolder")  + ";color:" + ("red")  + ";}.GEI-42HM{width:" + ("200px")  + ";height:" + ("100%")  + ";border-right:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-right:" + ("5px")  + ";margin-left:" + ("2px")  + ";overflow:" + ("auto")  + ";}.GEI-42IM{width:" + ("5px") ) + (";}.GEI-42IL{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GEI-42EM{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px")  + ";padding-left:") + (("2px")  + ";}.GEI-42EM:HOVER{background-color:" + ("#cdcdcd")  + ";}.GEI-42FM{width:" + ("16px")  + ";margin-right:" + ("5px")  + ";}.GEI-42GM{width:" + ("180px")  + ";}.GEI-42ML{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GEI-42LL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:" + ("#900")  + ";}.GEI-42CM{margin:" + ("10px") ) + (";}.GEI-42KL{margin-left:" + ("10px")  + ";margin-right:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GEI-42HL{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px")  + ";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:") + (("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}"));
      }
      public java.lang.String scrollPanel(){
        return "GEI-42HL";
      }
      public java.lang.String wizardButtonbar(){
        return "GEI-42IL";
      }
      public java.lang.String wizardDTableButtons(){
        return "GEI-42JL";
      }
      public java.lang.String wizardDTableCaption(){
        return "GEI-42KL";
      }
      public java.lang.String wizardDTableFieldContainerInvalid(){
        return "GEI-42LL";
      }
      public java.lang.String wizardDTableFieldContainerValid(){
        return "GEI-42ML";
      }
      public java.lang.String wizardDTableFields(){
        return "GEI-42NL";
      }
      public java.lang.String wizardDTableHeader(){
        return "GEI-42OL";
      }
      public java.lang.String wizardDTableList(){
        return "GEI-42PL";
      }
      public java.lang.String wizardDTableMessage(){
        return "GEI-42AM";
      }
      public java.lang.String wizardDTableMessageContainer(){
        return "GEI-42BM";
      }
      public java.lang.String wizardDTableSummaryContainer(){
        return "GEI-42CM";
      }
      public java.lang.String wizardDTableValidationError(){
        return "GEI-42DM";
      }
      public java.lang.String wizardPageTitleContainer(){
        return "GEI-42EM";
      }
      public java.lang.String wizardPageTitleImageContainer(){
        return "GEI-42FM";
      }
      public java.lang.String wizardPageTitleLabelContainer(){
        return "GEI-42GM";
      }
      public java.lang.String wizardSidebar(){
        return "GEI-42HM";
      }
      public java.lang.String wizardSidebarSpacer(){
        return "GEI-42IM";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.drools.guvnor.client.resources.WizardResources.WizardStyle get() {
      return style;
    }
  }
  public org.drools.guvnor.client.resources.WizardResources.WizardStyle style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.drools.guvnor.client.resources.WizardResources.WizardStyle style;
  
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
      case 'style': return this.@org.drools.guvnor.client.resources.WizardResources::style()();
    }
    return null;
  }-*/;
}
