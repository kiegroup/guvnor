package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactPatternsPageViewImpl_FactPatternsPageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl>, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl.FactPatternsPageWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("&gt;&gt;")
    SafeHtml html1();
     
    @Template("&lt;&lt;")
    SafeHtml html2();
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl owner) {

    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl_FactPatternsPageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl_FactPatternsPageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternsPageViewImpl_FactPatternsPageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image f_Image2 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel msgDuplicateBindings = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel8 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availableTypesContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel7 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnAdd = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton btnRemove = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.VerticalPanel buttonBar = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label12 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel11 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel chosenPatternsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel10 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnMoveUp = owner.btnMoveUp;
    com.google.gwt.user.client.ui.PushButton btnMoveDown = owner.btnMoveDown;
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel13 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel6 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label14 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox txtBinding = (org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox) GWT.create(org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup15 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.BindingFact() + "", "" + i18n.BindingDescription() + "");
    com.google.gwt.user.client.ui.HorizontalPanel bindingContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label17 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtEntryPoint = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel16 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label18 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPWindowOperatorsDropdown ddCEPWindow = (org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPWindowOperatorsDropdown) GWT.create(org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPWindowOperatorsDropdown.class);
    com.google.gwt.user.client.ui.HorizontalPanel cepWindowContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel patternDefinition = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel4 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel1.add(f_Image2);
    f_Label3.setStyleName("" + res.style().wizardDTableMessage() + "");
    f_Label3.setText("" + i18n.DecisionTableWizardDuplicateBindings() + "");
    f_HorizontalPanel1.add(f_Label3);
    msgDuplicateBindings.add(f_HorizontalPanel1);
    msgDuplicateBindings.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    msgDuplicateBindings.setVisible(false);
    container.add(msgDuplicateBindings);
    f_Label5.setStyleName("" + res.style().wizardDTableCaption() + "");
    f_Label5.setText("" + i18n.DecisionTableWizardDescriptionFactPatternsPage() + "");
    f_VerticalPanel4.add(f_Label5);
    f_Label9.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label9.setText("" + i18n.DecisionTableWizardAvailableTypes() + "");
    f_SimplePanel8.add(f_Label9);
    f_VerticalPanel7.add(f_SimplePanel8);
    availableTypesContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availableTypesContainer.setHeight("260px");
    availableTypesContainer.setWidth("280px");
    f_VerticalPanel7.add(availableTypesContainer);
    f_HorizontalPanel6.add(f_VerticalPanel7);
    btnAdd.setHTML(template.html1().asString());
    btnAdd.setEnabled(false);
    buttonBar.add(btnAdd);
    btnRemove.setHTML(template.html2().asString());
    btnRemove.setEnabled(false);
    buttonBar.add(btnRemove);
    buttonBar.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel6.add(buttonBar);
    f_Label12.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label12.setText("" + i18n.DecisionTableWizardChosenTypes() + "");
    f_SimplePanel11.add(f_Label12);
    f_VerticalPanel10.add(f_SimplePanel11);
    chosenPatternsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    chosenPatternsContainer.setHeight("260px");
    chosenPatternsContainer.setWidth("280px");
    f_VerticalPanel10.add(chosenPatternsContainer);
    f_HorizontalPanel6.add(f_VerticalPanel10);
    btnMoveUp.setEnabled(false);
    f_VerticalPanel13.add(btnMoveUp);
    btnMoveDown.setEnabled(false);
    f_VerticalPanel13.add(btnMoveDown);
    f_VerticalPanel13.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel6.add(f_VerticalPanel13);
    f_VerticalPanel4.add(f_HorizontalPanel6);
    f_Label14.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label14.setText("" + i18n.Binding() + "");
    bindingContainer.add(f_Label14);
    txtBinding.setStyleName("" + res.style().wizardDTableFields() + "");
    txtBinding.setEnabled(false);
    bindingContainer.add(txtBinding);
    f_InfoPopup15.setStyleName("" + res.style().wizardDTableFields() + "");
    bindingContainer.add(f_InfoPopup15);
    bindingContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    patternDefinition.add(bindingContainer);
    f_Label17.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label17.setText("" + i18n.DTLabelFromEntryPoint() + "");
    f_HorizontalPanel16.add(f_Label17);
    txtEntryPoint.setStyleName("" + res.style().wizardDTableFields() + "");
    txtEntryPoint.setEnabled(false);
    f_HorizontalPanel16.add(txtEntryPoint);
    f_HorizontalPanel16.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    patternDefinition.add(f_HorizontalPanel16);
    f_Label18.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label18.setText("" + i18n.DTLabelOverCEPWindow() + "");
    cepWindowContainer.add(f_Label18);
    ddCEPWindow.setStyleName("" + res.style().wizardDTableFields() + "");
    cepWindowContainer.add(ddCEPWindow);
    cepWindowContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    cepWindowContainer.setVisible(false);
    patternDefinition.add(cepWindowContainer);
    patternDefinition.setVisible(false);
    f_VerticalPanel4.add(patternDefinition);
    container.add(f_VerticalPanel4);
    container.setWidth("100%");



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnAddClick(event);
      }
    };
    btnAdd.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnRemoveClick(event);
      }
    };
    btnRemove.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.availableTypesContainer = availableTypesContainer;
    owner.bindingContainer = bindingContainer;
    owner.btnAdd = btnAdd;
    owner.btnRemove = btnRemove;
    owner.cepWindowContainer = cepWindowContainer;
    owner.chosenPatternsContainer = chosenPatternsContainer;
    owner.ddCEPWindow = ddCEPWindow;
    owner.msgDuplicateBindings = msgDuplicateBindings;
    owner.patternDefinition = patternDefinition;
    owner.txtBinding = txtBinding;
    owner.txtEntryPoint = txtEntryPoint;

    return container;
  }
}
