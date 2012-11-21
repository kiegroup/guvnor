package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedKBaseConfigWidget_KBaseAdvancedConfigOptionsBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget>, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget.KBaseAdvancedConfigOptionsBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget owner) {

    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget_KBaseAdvancedConfigOptionsBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget_KBaseAdvancedConfigOptionsBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AdvancedKBaseConfigWidget_KBaseAdvancedConfigOptionsBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listMBeans = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listEventProcessingMode = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox listAssertBehavior = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Grid f_Grid2 = (com.google.gwt.user.client.ui.Grid) GWT.create(com.google.gwt.user.client.ui.Grid.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.CheckBox checkEnabledAuthentication = (com.google.gwt.user.client.ui.CheckBox) GWT.create(com.google.gwt.user.client.ui.CheckBox.class);
    com.google.gwt.user.client.ui.Label f_Label10 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox textAssetsUser = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Label f_Label11 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox textAssetsPassword = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Grid f_Grid8 = (com.google.gwt.user.client.ui.Grid) GWT.create(com.google.gwt.user.client.ui.Grid.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel7 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.DisclosurePanel f_DisclosurePanel6 = new com.google.gwt.user.client.ui.DisclosurePanel("Assets Security Information");
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_Label3.setText("" + i18n.MBeansColon() + "");
    f_Label4.setText("" + i18n.EventProcessingModeColon() + "");
    f_Label5.setText("" + i18n.AssertBehaviorColon() + "");
    f_Grid2.resize(3, 2);
    f_Grid2.setWidget(0, 0, f_Label3);
    f_Grid2.setWidget(0, 1, listMBeans);
    f_Grid2.setWidget(1, 0, f_Label4);
    f_Grid2.setWidget(1, 1, listEventProcessingMode);
    f_Grid2.setWidget(2, 0, f_Label5);
    f_Grid2.setWidget(2, 1, listAssertBehavior);
    f_Grid2.setCellSpacing(6);
    f_VerticalPanel1.add(f_Grid2);
    f_Label9.setText("" + i18n.EnableAuthentication() + "");
    f_Label10.setText("" + i18n.UserName() + "");
    f_Label11.setText("" + i18n.Password() + "");
    f_Grid8.resize(3, 2);
    f_Grid8.setWidget(0, 0, f_Label9);
    f_Grid8.setWidget(0, 1, checkEnabledAuthentication);
    f_Grid8.setWidget(1, 0, f_Label10);
    f_Grid8.setWidget(1, 1, textAssetsUser);
    f_Grid8.setWidget(2, 0, f_Label11);
    f_Grid8.setWidget(2, 1, textAssetsPassword);
    f_Grid8.setCellSpacing(6);
    f_VerticalPanel7.add(f_Grid8);
    f_DisclosurePanel6.add(f_VerticalPanel7);
    f_VerticalPanel1.add(f_DisclosurePanel6);
    f_VerticalPanel1.setHeight("100%");
    f_VerticalPanel1.setWidth("100%");
    f_VerticalPanel1.setSpacing(10);



    owner.checkEnabledAuthentication = checkEnabledAuthentication;
    owner.listAssertBehavior = listAssertBehavior;
    owner.listEventProcessingMode = listEventProcessingMode;
    owner.listMBeans = listMBeans;
    owner.textAssetsPassword = textAssetsPassword;
    owner.textAssetsUser = textAssetsUser;

    return f_VerticalPanel1;
  }
}
