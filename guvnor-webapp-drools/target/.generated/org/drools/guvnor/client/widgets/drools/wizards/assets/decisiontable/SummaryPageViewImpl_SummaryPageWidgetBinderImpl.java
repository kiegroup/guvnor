package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class SummaryPageViewImpl_SummaryPageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl>, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl.SummaryPageWidgetBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl owner) {

    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl_SummaryPageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl_SummaryPageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.SummaryPageViewImpl_SummaryPageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image f_Image2 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel messages = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label6 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtAssetName = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Image f_Image7 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel assetNameContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label lblAssetDescription = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel8 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label11 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label lblPackageName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel10 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label13 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label lblTableFormat = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel12 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel5 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel1.add(f_Image2);
    f_Label3.setStyleName("" + res.style().wizardDTableMessage() + "");
    f_Label3.setText("" + i18n.DecisionTableWizardSummaryNameMissing() + "");
    f_HorizontalPanel1.add(f_Label3);
    messages.add(f_HorizontalPanel1);
    messages.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    messages.setVisible(false);
    container.add(messages);
    f_Label4.setStyleName("" + res.style().wizardDTableCaption() + "");
    f_Label4.setText("" + i18n.DecisionTableWizardDescriptionSummaryPage() + "");
    container.add(f_Label4);
    f_Label6.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label6.setText("" + i18n.NameColon() + "");
    assetNameContainer.add(f_Label6);
    txtAssetName.setStyleName("" + res.style().wizardDTableFields() + "");
    assetNameContainer.add(txtAssetName);
    f_Image7.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image7.setTitle("" + i18n.MandatoryField() + "");
    assetNameContainer.add(f_Image7);
    assetNameContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    f_VerticalPanel5.add(assetNameContainer);
    f_Label9.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label9.setText("" + i18n.InitialDescription() + "");
    f_HorizontalPanel8.add(f_Label9);
    lblAssetDescription.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel8.add(lblAssetDescription);
    f_HorizontalPanel8.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    f_VerticalPanel5.add(f_HorizontalPanel8);
    f_Label11.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label11.setText("" + i18n.CreateInPackage() + "");
    f_HorizontalPanel10.add(f_Label11);
    lblPackageName.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel10.add(lblPackageName);
    f_HorizontalPanel10.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    f_VerticalPanel5.add(f_HorizontalPanel10);
    f_Label13.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label13.setText("" + i18n.TableFormat() + "");
    f_HorizontalPanel12.add(f_Label13);
    lblTableFormat.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel12.add(lblTableFormat);
    f_HorizontalPanel12.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    f_VerticalPanel5.add(f_HorizontalPanel12);
    f_VerticalPanel5.setStyleName("" + res.style().wizardDTableSummaryContainer() + "");
    container.add(f_VerticalPanel5);
    container.setWidth("100%");



    owner.assetNameContainer = assetNameContainer;
    owner.lblAssetDescription = lblAssetDescription;
    owner.lblPackageName = lblPackageName;
    owner.lblTableFormat = lblTableFormat;
    owner.messages = messages;
    owner.txtAssetName = txtAssetName;

    return container;
  }
}
