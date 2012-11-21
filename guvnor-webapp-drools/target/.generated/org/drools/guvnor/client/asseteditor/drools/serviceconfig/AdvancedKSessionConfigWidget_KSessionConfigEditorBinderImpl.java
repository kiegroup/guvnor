package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedKSessionConfigWidget_KSessionConfigEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget>, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget.KSessionConfigEditorBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget owner) {

    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget_KSessionConfigEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget_KSessionConfigEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKSessionConfigWidget_KSessionConfigEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox textUrl = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listProtocol = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listMarshalling = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Grid f_Grid2 = (com.google.gwt.user.client.ui.Grid) GWT.create(com.google.gwt.user.client.ui.Grid.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listClockType = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Label f_Label10 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listKeepReference = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Grid f_Grid8 = (com.google.gwt.user.client.ui.Grid) GWT.create(com.google.gwt.user.client.ui.Grid.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel7 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.DisclosurePanel f_DisclosurePanel6 = new com.google.gwt.user.client.ui.DisclosurePanel("Other Options");
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_Label3.setText("" + i18n.UrlCollon() + "");
    f_Label4.setText("" + i18n.ProtocolColon() + "");
    f_Label5.setText("" + i18n.MarshallingColon() + "");
    f_Grid2.resize(3, 2);
    f_Grid2.setWidget(0, 0, f_Label3);
    f_Grid2.setWidget(0, 1, textUrl);
    f_Grid2.setWidget(1, 0, f_Label4);
    f_Grid2.setWidget(1, 1, listProtocol);
    f_Grid2.setWidget(2, 0, f_Label5);
    f_Grid2.setWidget(2, 1, listMarshalling);
    f_Grid2.setCellSpacing(6);
    f_VerticalPanel1.add(f_Grid2);
    f_Label9.setText("" + i18n.ClockTypeColon() + "");
    f_Label10.setText("" + i18n.KeepReferenceColon() + "");
    f_Grid8.resize(2, 2);
    f_Grid8.setWidget(0, 0, f_Label9);
    f_Grid8.setWidget(0, 1, listClockType);
    f_Grid8.setWidget(1, 0, f_Label10);
    f_Grid8.setWidget(1, 1, listKeepReference);
    f_Grid8.setCellSpacing(6);
    f_VerticalPanel7.add(f_Grid8);
    f_DisclosurePanel6.add(f_VerticalPanel7);
    f_VerticalPanel1.add(f_DisclosurePanel6);
    f_VerticalPanel1.setHeight("100%");
    f_VerticalPanel1.setWidth("100%");
    f_VerticalPanel1.setSpacing(10);



    owner.listClockType = listClockType;
    owner.listKeepReference = listKeepReference;
    owner.listMarshalling = listMarshalling;
    owner.listProtocol = listProtocol;
    owner.textUrl = textUrl;

    return f_VerticalPanel1;
  }
}
