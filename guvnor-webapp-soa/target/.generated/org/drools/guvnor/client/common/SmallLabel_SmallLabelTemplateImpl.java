package org.drools.guvnor.client.common;

public class SmallLabel_SmallLabelTemplateImpl implements org.drools.guvnor.client.common.SmallLabel.SmallLabelTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml message(com.google.gwt.safehtml.shared.SafeHtml arg0) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div class='form-field'>");
    sb.append(arg0.asString());
    sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
