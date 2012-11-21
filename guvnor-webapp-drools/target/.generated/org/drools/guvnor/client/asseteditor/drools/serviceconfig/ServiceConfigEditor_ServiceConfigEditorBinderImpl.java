package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ServiceConfigEditor_ServiceConfigEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor>, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor.ServiceConfigEditorBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor_ServiceConfigEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor_ServiceConfigEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor_ServiceConfigEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfigEditor_ServiceConfigEditorBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.google.gwt.user.client.ui.TabLayoutPanel tabPanel = new com.google.gwt.user.client.ui.TabLayoutPanel(30, com.google.gwt.dom.client.Style.Unit.PX);
    com.google.gwt.user.client.ui.Button btnArtifacts = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnDownloadWar = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel1 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);

    tabPanel.setHeight("400px");
    tabPanel.setWidth("100%");
    f_FlowPanel1.add(tabPanel);
    btnArtifacts.setText("" + i18n.ManageDependenciesEllipsis() + "");
    f_HorizontalPanel2.add(btnArtifacts);
    btnDownloadWar.setText("" + i18n.DownloadWar() + "");
    f_HorizontalPanel2.add(btnDownloadWar);
    f_HorizontalPanel2.setStyleName("" + style.externalButtons() + "");
    f_HorizontalPanel2.setSpacing(10);
    f_FlowPanel1.add(f_HorizontalPanel2);
    f_FlowPanel1.setWidth("100%");



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.setupMavenArtifacts(event);
      }
    };
    btnArtifacts.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.downloadWarFile(event);
      }
    };
    btnDownloadWar.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.btnArtifacts = btnArtifacts;
    owner.btnDownloadWar = btnDownloadWar;
    owner.tabPanel = tabPanel;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_FlowPanel1;
  }
}
