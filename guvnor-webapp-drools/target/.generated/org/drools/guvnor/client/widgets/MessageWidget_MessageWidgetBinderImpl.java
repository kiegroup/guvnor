package org.drools.guvnor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class MessageWidget_MessageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.MessageWidget>, org.drools.guvnor.client.widgets.MessageWidget.MessageWidgetBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.MessageWidget owner) {

    org.drools.guvnor.client.widgets.MessageWidget_MessageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.MessageWidget_MessageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.MessageWidget_MessageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    org.drools.guvnor.client.resources.GuvnorResources res = (org.drools.guvnor.client.resources.GuvnorResources) GWT.create(org.drools.guvnor.client.resources.GuvnorResources.class);
    com.google.gwt.user.client.ui.Image f_Image3 = new com.google.gwt.user.client.ui.Image(images.infoLarge());
    com.google.gwt.user.client.ui.Label lblMessage = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.SimplePanel messageContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel2.add(f_Image3);
    lblMessage.setStyleName("" + res.guvnorCss().warningMessage() + "");
    f_HorizontalPanel2.add(lblMessage);
    messageContainer.add(f_HorizontalPanel2);
    messageContainer.setStyleName("" + res.guvnorCss().warningContainer() + "");
    messageContainer.setVisible(false);
    f_VerticalPanel1.add(messageContainer);
    f_VerticalPanel1.setWidth("100%");



    owner.lblMessage = lblMessage;
    owner.messageContainer = messageContainer;

    return f_VerticalPanel1;
  }
}
