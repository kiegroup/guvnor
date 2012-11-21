package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PermissionsPagedTableView_PermissionsPagedTableViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView>, org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView.PermissionsPagedTableViewBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView owner) {

    org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView_PermissionsPagedTableViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView_PermissionsPagedTableViewBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.tables.PermissionsPagedTableView_PermissionsPagedTableViewBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.ConstantsCore i18n = (org.drools.guvnor.client.messages.ConstantsCore) GWT.create(org.drools.guvnor.client.messages.ConstantsCore.class);
    com.google.gwt.user.client.ui.Button createNewUserButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button deleteSelectedUserButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button openSelectedUserButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button refreshButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.ToggleButton columnPickerButton = owner.columnPickerButton;
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.cellview.client.CellTable cellTable = owner.cellTable;
    org.drools.guvnor.client.widgets.tables.GuvnorSimplePager pager = (org.drools.guvnor.client.widgets.tables.GuvnorSimplePager) GWT.create(org.drools.guvnor.client.widgets.tables.GuvnorSimplePager.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    createNewUserButton.setText("" + i18n.CreateNewUserMapping() + "");
    f_HorizontalPanel2.add(createNewUserButton);
    deleteSelectedUserButton.setText("" + i18n.DeleteSelectedUser() + "");
    f_HorizontalPanel2.add(deleteSelectedUserButton);
    openSelectedUserButton.setText("" + i18n.openSelected() + "");
    f_HorizontalPanel2.add(openSelectedUserButton);
    refreshButton.setText("" + i18n.refreshList() + "");
    f_HorizontalPanel2.add(refreshButton);
    f_HorizontalPanel2.add(columnPickerButton);
    f_VerticalPanel1.add(f_HorizontalPanel2);
    f_VerticalPanel1.add(cellTable);
    f_VerticalPanel1.add(pager);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.createNewUser(event);
      }
    };
    createNewUserButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.deleteSelectedUser(event);
      }
    };
    deleteSelectedUserButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.openSelectedUser(event);
      }
    };
    openSelectedUserButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.refresh(event);
      }
    };
    refreshButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.createNewUserButton = createNewUserButton;
    owner.deleteSelectedUserButton = deleteSelectedUserButton;
    owner.openSelectedUserButton = openSelectedUserButton;
    owner.pager = pager;

    return f_VerticalPanel1;
  }
}
