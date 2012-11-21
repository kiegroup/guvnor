package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class LogPagedTable_LogPagedTableBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.tables.LogPagedTable>, org.drools.guvnor.client.widgets.tables.LogPagedTable.LogPagedTableBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.tables.LogPagedTable owner) {

    org.drools.guvnor.client.widgets.tables.LogPagedTable_LogPagedTableBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.tables.LogPagedTable_LogPagedTableBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.tables.LogPagedTable_LogPagedTableBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.ConstantsCore i18n = (org.drools.guvnor.client.messages.ConstantsCore) GWT.create(org.drools.guvnor.client.messages.ConstantsCore.class);
    com.google.gwt.user.client.ui.Button cleanButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button refreshButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.ToggleButton columnPickerButton = owner.columnPickerButton;
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.cellview.client.CellTable cellTable = owner.cellTable;
    org.drools.guvnor.client.widgets.tables.GuvnorSimplePager pager = (org.drools.guvnor.client.widgets.tables.GuvnorSimplePager) GWT.create(org.drools.guvnor.client.widgets.tables.GuvnorSimplePager.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    cleanButton.setText("" + i18n.Clean() + "");
    f_HorizontalPanel2.add(cleanButton);
    refreshButton.setText("" + i18n.refreshList() + "");
    f_HorizontalPanel2.add(refreshButton);
    f_HorizontalPanel2.add(columnPickerButton);
    f_VerticalPanel1.add(f_HorizontalPanel2);
    f_VerticalPanel1.add(cellTable);
    f_VerticalPanel1.add(pager);



    owner.cleanButton = cleanButton;
    owner.pager = pager;
    owner.refreshButton = refreshButton;

    return f_VerticalPanel1;
  }
}
