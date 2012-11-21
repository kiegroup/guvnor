package org.drools.guvnor.client.widgets.drools.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertiesEditorSimpleTable_PropertiesEditorSimpleTableBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable>, org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable.PropertiesEditorSimpleTableBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable owner) {

    org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable_PropertiesEditorSimpleTableBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable_PropertiesEditorSimpleTableBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.tables.PropertiesEditorSimpleTable_PropertiesEditorSimpleTableBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.Button addPropertyButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button deleteSelectedPropertiesButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.ToggleButton columnPickerButton = owner.columnPickerButton;
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.cellview.client.CellTable cellTable = owner.cellTable;
    com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel3 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    addPropertyButton.setText("" + i18n.Add() + "");
    f_HorizontalPanel2.add(addPropertyButton);
    deleteSelectedPropertiesButton.setText("" + i18n.Delete() + "");
    f_HorizontalPanel2.add(deleteSelectedPropertiesButton);
    f_HorizontalPanel2.add(columnPickerButton);
    f_VerticalPanel1.add(f_HorizontalPanel2);
    f_ScrollPanel3.add(cellTable);
    f_ScrollPanel3.setHeight("300px");
    f_ScrollPanel3.setWidth("600px");
    f_VerticalPanel1.add(f_ScrollPanel3);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addProperty(event);
      }
    };
    addPropertyButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.deleteSelectedProperties(event);
      }
    };
    deleteSelectedPropertiesButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.addPropertyButton = addPropertyButton;
    owner.deleteSelectedPropertiesButton = deleteSelectedPropertiesButton;

    return f_VerticalPanel1;
  }
}
