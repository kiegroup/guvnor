package org.drools.guvnor.client.util;

public class Util_HeaderTemplateImpl implements org.drools.guvnor.client.util.Util.HeaderTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml message(com.google.gwt.safehtml.shared.SafeHtml arg0,com.google.gwt.safehtml.shared.SafeHtml arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append(arg0.asString());
    sb.append(" ");
    sb.append(arg1.asString());
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
