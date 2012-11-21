package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells;

public class ActionSetFieldCell_ActionSetFieldCellTemplateImpl implements org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ActionSetFieldCell.ActionSetFieldCellTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml text(java.lang.String arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div class=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("\" >");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("</div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
