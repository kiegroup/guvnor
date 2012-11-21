package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FreeFormLinePopup_FreeFormLinePopupBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup>, org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup.FreeFormLinePopupBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup owner) {

    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup_FreeFormLinePopupBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup_FreeFormLinePopupBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.FreeFormLinePopup_FreeFormLinePopupBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.DynamicTextArea textArea = (org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.DynamicTextArea) GWT.create(org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.DynamicTextArea.class);
    com.google.gwt.user.client.ui.Button btnOK = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnCancel = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel content = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    content.add(textArea);
    btnOK.setText("" + i18n.OK() + "");
    f_HorizontalPanel1.add(btnOK);
    btnCancel.setText("" + i18n.Cancel() + "");
    f_HorizontalPanel1.add(btnCancel);
    content.add(f_HorizontalPanel1);



    owner.btnCancel = btnCancel;
    owner.btnOK = btnOK;
    owner.content = content;
    owner.textArea = textArea;

    return content;
  }
}
