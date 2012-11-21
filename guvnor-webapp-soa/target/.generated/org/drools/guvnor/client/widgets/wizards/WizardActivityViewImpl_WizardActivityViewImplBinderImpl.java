package org.drools.guvnor.client.widgets.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class WizardActivityViewImpl_WizardActivityViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl>, org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl.WizardActivityViewImplBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl owner) {

    org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl_WizardActivityViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl_WizardActivityViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl_WizardActivityViewImplBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.VerticalPanel sideBar = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.ScrollPanel sideBarContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel2 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.SimplePanel body = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel bodyContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Button btnPrevious = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnNext = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnCancel = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnFinish = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.HorizontalPanel buttonBar = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel3 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    sideBar.setWidth("100%");
    sideBarContainer.add(sideBar);
    sideBarContainer.setStyleName("" + res.style().wizardSidebar() + "");
    f_HorizontalPanel1.add(sideBarContainer);
    f_SimplePanel2.setStyleName("" + res.style().wizardSidebarSpacer() + "");
    f_HorizontalPanel1.add(f_SimplePanel2);
    bodyContainer.add(body);
    f_HorizontalPanel1.add(bodyContainer);
    container.add(f_HorizontalPanel1);
    btnPrevious.setText("" + i18n.Previous() + "");
    buttonBar.add(btnPrevious);
    btnNext.setText("" + i18n.Next() + "");
    buttonBar.add(btnNext);
    btnCancel.setText("" + i18n.Cancel() + "");
    buttonBar.add(btnCancel);
    btnFinish.setText("" + i18n.Finish() + "");
    buttonBar.add(btnFinish);
    buttonBar.setSpacing(5);
    f_SimplePanel3.add(buttonBar);
    f_SimplePanel3.setStyleName("" + res.style().wizardButtonbar() + "");
    container.add(f_SimplePanel3);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnCancelClick(event);
      }
    };
    btnCancel.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnFinishClick(event);
      }
    };
    btnFinish.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnNextClick(event);
      }
    };
    btnNext.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnPreviousClick(event);
      }
    };
    btnPrevious.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.body = body;
    owner.bodyContainer = bodyContainer;
    owner.btnFinish = btnFinish;
    owner.btnNext = btnNext;
    owner.btnPrevious = btnPrevious;
    owner.sideBar = sideBar;
    owner.sideBarContainer = sideBarContainer;

    return container;
  }
}
