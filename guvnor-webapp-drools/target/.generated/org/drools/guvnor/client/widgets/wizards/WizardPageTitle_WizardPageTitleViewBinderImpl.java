package org.drools.guvnor.client.widgets.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class WizardPageTitle_WizardPageTitleViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.wizards.WizardPageTitle>, org.drools.guvnor.client.widgets.wizards.WizardPageTitle.WizardPageTitleViewBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.wizards.WizardPageTitle owner) {

    org.drools.guvnor.client.widgets.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image imgCompleted = new com.google.gwt.user.client.ui.Image(images.tick());
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel1 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.Label lblTitle = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel lblContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel container = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);

    f_SimplePanel1.add(imgCompleted);
    f_SimplePanel1.setStyleName("" + res.style().wizardPageTitleImageContainer() + "");
    container.add(f_SimplePanel1);
    lblContainer.add(lblTitle);
    lblContainer.setStyleName("" + res.style().wizardPageTitleLabelContainer() + "");
    container.add(lblContainer);
    container.setStyleName("" + res.style().wizardPageTitleContainer() + "");



    owner.container = container;
    owner.imgCompleted = imgCompleted;
    owner.lblTitle = lblTitle;

    return container;
  }
}
