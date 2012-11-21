package org.drools.guvnor.client.common;

public class PopupTitleBar_PopupTitleBarBinderImpl_TemplateImpl implements org.drools.guvnor.client.common.PopupTitleBar_PopupTitleBarBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div class='guvnor-PopupTitleBar-bar' id='titleBar'> <div class='guvnor-PopupTitleBar-title' style='float:left;'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> </div> <div class='guvnor-PopupTitleBar-close-button' style='float:right;'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> </div> <div style='clear:both;'></div> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
