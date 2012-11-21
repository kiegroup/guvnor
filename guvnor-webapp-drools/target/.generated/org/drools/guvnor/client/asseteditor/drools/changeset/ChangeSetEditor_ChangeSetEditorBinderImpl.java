package org.drools.guvnor.client.asseteditor.drools.changeset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ChangeSetEditor_ChangeSetEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor>, org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor.ChangeSetEditorBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor_ChangeSetEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor_ChangeSetEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.changeset.ChangeSetEditor_ChangeSetEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Button btnPackageResource = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnAssetResource = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel2 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel pnlURL = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.TextArea editorArea = (com.google.gwt.user.client.ui.TextArea) GWT.create(com.google.gwt.user.client.ui.TextArea.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel4 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);

    f_Label3.setText("" + i18n.AddNewResourceElement() + "");
    f_VerticalPanel2.add(f_Label3);
    btnPackageResource.setText("" + i18n.Package() + "");
    f_VerticalPanel2.add(btnPackageResource);
    btnAssetResource.setText("" + i18n.Asset() + "");
    f_VerticalPanel2.add(btnAssetResource);
    f_HorizontalPanel1.add(f_VerticalPanel2);
    f_VerticalPanel4.add(pnlURL);
    editorArea.setWidth("100%");
    f_VerticalPanel4.add(editorArea);
    f_VerticalPanel4.setWidth("100%");
    f_HorizontalPanel1.add(f_VerticalPanel4);
    f_HorizontalPanel1.setWidth("100%");



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addNewPackageResource(event);
      }
    };
    btnPackageResource.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addNewAssetResource(event);
      }
    };
    btnAssetResource.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.btnAssetResource = btnAssetResource;
    owner.btnPackageResource = btnPackageResource;
    owner.editorArea = editorArea;
    owner.pnlURL = pnlURL;

    return f_HorizontalPanel1;
  }
}
