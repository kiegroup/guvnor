package org.drools.guvnor.client.moduleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AssetViewerActivityViewImpl_AssetViewerActivityViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl>, org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl.AssetViewerActivityViewImplBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl owner) {

    org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl_AssetViewerActivityViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl_AssetViewerActivityViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl_AssetViewerActivityViewImplBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.Images images = (org.drools.guvnor.client.resources.Images) GWT.create(org.drools.guvnor.client.resources.Images.class);
    org.drools.guvnor.client.resources.GuvnorResources res = (org.drools.guvnor.client.resources.GuvnorResources) GWT.create(org.drools.guvnor.client.resources.GuvnorResources.class);
    com.google.gwt.user.client.ui.Image f_Image3 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel msgNoAssetsDefinedInPackage = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel assetGroupsContainer = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel2.add(f_Image3);
    f_Label4.setStyleName("" + res.guvnorCss().warningMessage() + "");
    f_Label4.setText("" + i18n.NoAssetsDefinedInPackage() + "");
    f_HorizontalPanel2.add(f_Label4);
    msgNoAssetsDefinedInPackage.add(f_HorizontalPanel2);
    msgNoAssetsDefinedInPackage.setStyleName("" + res.guvnorCss().warningContainer() + "");
    msgNoAssetsDefinedInPackage.setVisible(false);
    f_VerticalPanel1.add(msgNoAssetsDefinedInPackage);
    assetGroupsContainer.setWidth("100%");
    f_VerticalPanel1.add(assetGroupsContainer);
    f_VerticalPanel1.setWidth("100%");



    owner.assetGroupsContainer = assetGroupsContainer;
    owner.msgNoAssetsDefinedInPackage = msgNoAssetsDefinedInPackage;

    return f_VerticalPanel1;
  }
}
