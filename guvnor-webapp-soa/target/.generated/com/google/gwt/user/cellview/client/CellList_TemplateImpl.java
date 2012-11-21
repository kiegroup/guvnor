package com.google.gwt.user.cellview.client;

public class CellList_TemplateImpl implements com.google.gwt.user.cellview.client.CellList.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml divFocusable(int arg0,java.lang.String arg1,int arg2,com.google.gwt.safehtml.shared.SafeHtml arg3) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div onclick=\"\" __idx=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg0)));
    sb.append("\" class=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("\" style=\"outline:none;\" tabindex=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg2)));
    sb.append("\">");
    sb.append(arg3.asString());
    sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml divFocusableWithKey(int arg0,java.lang.String arg1,int arg2,char arg3,com.google.gwt.safehtml.shared.SafeHtml arg4) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div onclick=\"\" __idx=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg0)));
sb.append("\" class=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("\" style=\"outline:none;\" tabindex=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg2)));
sb.append("\" accesskey=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg3)));
sb.append("\">");
sb.append(arg4.asString());
sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml div(int arg0,java.lang.String arg1,com.google.gwt.safehtml.shared.SafeHtml arg2) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div onclick=\"\" __idx=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(String.valueOf(arg0)));
sb.append("\" class=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("\" style=\"outline:none;\" >");
sb.append(arg2.asString());
sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
