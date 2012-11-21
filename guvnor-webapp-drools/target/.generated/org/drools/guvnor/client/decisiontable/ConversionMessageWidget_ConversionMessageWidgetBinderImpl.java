package org.drools.guvnor.client.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ConversionMessageWidget_ConversionMessageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.decisiontable.ConversionMessageWidget>, org.drools.guvnor.client.decisiontable.ConversionMessageWidget.ConversionMessageWidgetBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.decisiontable.ConversionMessageWidget owner) {

    org.drools.guvnor.client.decisiontable.ConversionMessageWidget_ConversionMessageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.decisiontable.ConversionMessageWidget_ConversionMessageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.decisiontable.ConversionMessageWidget_ConversionMessageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources wizardResources = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image image = (com.google.gwt.user.client.ui.Image) GWT.create(com.google.gwt.user.client.ui.Image.class);
    com.google.gwt.user.client.ui.Label label = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);

    f_HorizontalPanel1.add(image);
    f_HorizontalPanel1.add(label);
    f_HorizontalPanel1.setWidth("500px");



    owner.image = image;
    owner.label = label;

    return f_HorizontalPanel1;
  }
}
