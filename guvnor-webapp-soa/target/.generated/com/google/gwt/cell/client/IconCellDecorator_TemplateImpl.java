package com.google.gwt.cell.client;

public class IconCellDecorator_TemplateImpl implements com.google.gwt.cell.client.IconCellDecorator.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml imageWrapperMiddle(com.google.gwt.safecss.shared.SafeStyles arg0,com.google.gwt.safehtml.shared.SafeHtml arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div style=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0.asString()));
    sb.append("position:absolute;top:50%;line-height:0px;\">");
    sb.append(arg1.asString());
    sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml imageWrapperBottom(com.google.gwt.safecss.shared.SafeStyles arg0,com.google.gwt.safehtml.shared.SafeHtml arg1) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div style=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0.asString()));
sb.append("position:absolute;bottom:0px;line-height:0px;\">");
sb.append(arg1.asString());
sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml imageWrapperTop(com.google.gwt.safecss.shared.SafeStyles arg0,com.google.gwt.safehtml.shared.SafeHtml arg1) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div style=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0.asString()));
sb.append("position:absolute;top:0px;line-height:0px;\">");
sb.append(arg1.asString());
sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml outerDiv(com.google.gwt.safecss.shared.SafeStyles arg0,com.google.gwt.safehtml.shared.SafeHtml arg1,com.google.gwt.safehtml.shared.SafeHtml arg2) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div style=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0.asString()));
sb.append("position:relative;zoom:1;\">");
sb.append(arg1.asString());
sb.append("<div>");
sb.append(arg2.asString());
sb.append("</div></div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
