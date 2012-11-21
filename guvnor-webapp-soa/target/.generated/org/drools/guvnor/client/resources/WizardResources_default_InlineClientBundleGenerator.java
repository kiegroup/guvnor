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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVQBORBHL{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("none")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("0"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GFVQBORBGL{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GFVQBORBBL{margin-top:" + ("100px")  + ";margin-right:" + ("10px")  + ";margin-left:" + ("10px") ) + (";}.GFVQBORBBL td{margin-bottom:" + ("10px")  + ";width:" + ("48px")  + ";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GFVQBORBFL{margin-right:" + ("3px")  + ";margin-left:" + ("3px")  + ";}.GFVQBORBJL{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GFVQBORBIL{margin-right:") + (("10px")  + ";margin-top:" + ("6px")  + ";}.GFVQBORBLL{font-weight:" + ("bolder")  + ";color:" + ("red")  + ";}.GFVQBORBPL{width:" + ("200px")  + ";height:" + ("100%")  + ";border-left:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-left:" + ("5px")  + ";margin-right:" + ("2px")  + ";overflow:" + ("auto")  + ";}.GFVQBORBAM{width:" + ("5px") ) + (";}.GFVQBORBAL{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GFVQBORBML{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px")  + ";padding-right:") + (("2px")  + ";}.GFVQBORBML:HOVER{background-color:" + ("#cdcdcd")  + ";}.GFVQBORBNL{width:" + ("16px")  + ";margin-left:" + ("5px")  + ";}.GFVQBORBOL{width:" + ("180px")  + ";}.GFVQBORBEL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GFVQBORBDL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:" + ("#900")  + ";}.GFVQBORBKL{margin:" + ("10px") ) + (";}.GFVQBORBCL{margin-right:" + ("10px")  + ";margin-left:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GFVQBORBPK{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px")  + ";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:") + (("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}")) : ((".GFVQBORBHL{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("none")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("0"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GFVQBORBGL{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GFVQBORBBL{margin-top:" + ("100px")  + ";margin-left:" + ("10px")  + ";margin-right:" + ("10px") ) + (";}.GFVQBORBBL td{margin-bottom:" + ("10px")  + ";width:" + ("48px")  + ";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GFVQBORBFL{margin-left:" + ("3px")  + ";margin-right:" + ("3px")  + ";}.GFVQBORBJL{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GFVQBORBIL{margin-left:") + (("10px")  + ";margin-top:" + ("6px")  + ";}.GFVQBORBLL{font-weight:" + ("bolder")  + ";color:" + ("red")  + ";}.GFVQBORBPL{width:" + ("200px")  + ";height:" + ("100%")  + ";border-right:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-right:" + ("5px")  + ";margin-left:" + ("2px")  + ";overflow:" + ("auto")  + ";}.GFVQBORBAM{width:" + ("5px") ) + (";}.GFVQBORBAL{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GFVQBORBML{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px")  + ";padding-left:") + (("2px")  + ";}.GFVQBORBML:HOVER{background-color:" + ("#cdcdcd")  + ";}.GFVQBORBNL{width:" + ("16px")  + ";margin-right:" + ("5px")  + ";}.GFVQBORBOL{width:" + ("180px")  + ";}.GFVQBORBEL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GFVQBORBDL{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:" + ("#900")  + ";}.GFVQBORBKL{margin:" + ("10px") ) + (";}.GFVQBORBCL{margin-left:" + ("10px")  + ";margin-right:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GFVQBORBPK{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px")  + ";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:") + (("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}"));
      }
      public java.lang.String scrollPanel(){
        return "GFVQBORBPK";
      }
      public java.lang.String wizardButtonbar(){
        return "GFVQBORBAL";
      }
      public java.lang.String wizardDTableButtons(){
        return "GFVQBORBBL";
      }
      public java.lang.String wizardDTableCaption(){
        return "GFVQBORBCL";
      }
      public java.lang.String wizardDTableFieldContainerInvalid(){
        return "GFVQBORBDL";
      }
      public java.lang.String wizardDTableFieldContainerValid(){
        return "GFVQBORBEL";
      }
      public java.lang.String wizardDTableFields(){
        return "GFVQBORBFL";
      }
      public java.lang.String wizardDTableHeader(){
        return "GFVQBORBGL";
      }
      public java.lang.String wizardDTableList(){
        return "GFVQBORBHL";
      }
      public java.lang.String wizardDTableMessage(){
        return "GFVQBORBIL";
      }
      public java.lang.String wizardDTableMessageContainer(){
        return "GFVQBORBJL";
      }
      public java.lang.String wizardDTableSummaryContainer(){
        return "GFVQBORBKL";
      }
      public java.lang.String wizardDTableValidationError(){
        return "GFVQBORBLL";
      }
      public java.lang.String wizardPageTitleContainer(){
        return "GFVQBORBML";
      }
      public java.lang.String wizardPageTitleImageContainer(){
        return "GFVQBORBNL";
      }
      public java.lang.String wizardPageTitleLabelContainer(){
        return "GFVQBORBOL";
      }
      public java.lang.String wizardSidebar(){
        return "GFVQBORBPL";
      }
      public java.lang.String wizardSidebarSpacer(){
        return "GFVQBORBAM";
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
