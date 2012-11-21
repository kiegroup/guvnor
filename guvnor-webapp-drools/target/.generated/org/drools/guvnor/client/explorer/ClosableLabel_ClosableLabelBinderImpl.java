package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ClosableLabel_ClosableLabelBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.explorer.ClosableLabel>, org.drools.guvnor.client.explorer.ClosableLabel.ClosableLabelBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.explorer.ClosableLabel owner) {

    org.drools.guvnor.client.explorer.ClosableLabel_ClosableLabelBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.explorer.ClosableLabel_ClosableLabelBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.explorer.ClosableLabel_ClosableLabelBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    com.google.gwt.user.client.ui.Label text = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Image closeButton = new com.google.gwt.user.client.ui.Image(images.close());
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel2 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.FocusPanel basePanel = (com.google.gwt.user.client.ui.FocusPanel) GWT.create(com.google.gwt.user.client.ui.FocusPanel.class);

    f_HorizontalPanel1.add(text);
    closeButton.setVisible(false);
    f_SimplePanel2.add(closeButton);
    f_SimplePanel2.setWidth("10px");
    f_HorizontalPanel1.add(f_SimplePanel2);
    basePanel.add(f_HorizontalPanel1);



    final com.google.gwt.event.dom.client.MouseOverHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.MouseOverHandler() {
      public void onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent event) {
        owner.showCloseButton(event);
      }
    };
    basePanel.addMouseOverHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.MouseOutHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.MouseOutHandler() {
      public void onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent event) {
        owner.hideCloseButton(event);
      }
    };
    basePanel.addMouseOutHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.closeTab(event);
      }
    };
    closeButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    owner.closeButton = closeButton;
    owner.text = text;

    return basePanel;
  }
}
