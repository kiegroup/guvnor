package org.drools.guvnor.client.asseteditor.drools.factmodel;

public class FactFieldEditor_FactFieldsEditorBinderImpl_TemplateImpl implements org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor_FactFieldsEditorBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div style='float:left; padding-left:40px;'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> </div> <div style='float:right;'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> </div> <div style='clear:both;'></div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
