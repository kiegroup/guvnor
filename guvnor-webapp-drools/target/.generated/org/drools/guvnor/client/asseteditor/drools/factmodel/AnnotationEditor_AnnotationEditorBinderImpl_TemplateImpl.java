package org.drools.guvnor.client.asseteditor.drools.factmodel;

public class AnnotationEditor_AnnotationEditorBinderImpl_TemplateImpl implements org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor_AnnotationEditorBinderImpl.Template {
  
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
