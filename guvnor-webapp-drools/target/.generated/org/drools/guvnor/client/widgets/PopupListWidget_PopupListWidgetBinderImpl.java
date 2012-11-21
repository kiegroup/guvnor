package org.drools.guvnor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PopupListWidget_PopupListWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.PopupListWidget>, org.drools.guvnor.client.widgets.PopupListWidget.PopupListWidgetBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.PopupListWidget owner) {

    org.drools.guvnor.client.widgets.PopupListWidget_PopupListWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.PopupListWidget_PopupListWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.PopupListWidget_PopupListWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.ConstantsCore i18n = (org.drools.guvnor.client.messages.ConstantsCore) GWT.create(org.drools.guvnor.client.messages.ConstantsCore.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    org.drools.guvnor.client.resources.GuvnorResources resources = (org.drools.guvnor.client.resources.GuvnorResources) GWT.create(org.drools.guvnor.client.resources.GuvnorResources.class);
    com.google.gwt.user.client.ui.Label f_Label2 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.VerticalPanel list = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.ScrollPanel listContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.Button cmdOk = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel3 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_Label2.setStyleName("" + resources.guvnorCss().popupListWidgetCaption() + "");
    f_Label2.setText("" + i18n.ConversionResults() + "");
    f_VerticalPanel1.add(f_Label2);
    list.setWidth("100%");
    listContainer.add(list);
    f_VerticalPanel1.add(listContainer);
    cmdOk.setText("" + i18n.OK() + "");
    cmdOk.setWidth("64px");
    f_HorizontalPanel3.add(cmdOk);
    f_HorizontalPanel3.setStyleName("" + resources.guvnorCss().popupListWidgetButtonBar() + "");
    f_VerticalPanel1.add(f_HorizontalPanel3);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.cmdOkOnClickEvent(event);
      }
    };
    cmdOk.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.cmdOk = cmdOk;
    owner.list = list;
    owner.listContainer = listContainer;

    return f_VerticalPanel1;
  }
}
