package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ActionInsertFactFieldsPageViewImpl_ActionInsertFactFieldsPageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl>, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl.ActionInsertFactFieldsPageWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("&gt;&gt;")
    SafeHtml html1();
     
    @Template("&lt;&lt;")
    SafeHtml html2();
     
    @Template("&gt;&gt;")
    SafeHtml html3();
     
    @Template("&lt;&lt;")
    SafeHtml html4();
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl owner) {

    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl_ActionInsertFactFieldsPageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl_ActionInsertFactFieldsPageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPageViewImpl_ActionInsertFactFieldsPageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image f_Image2 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel msgDuplicateBindings = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Image f_Image5 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label6 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel4 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel msgIncompleteActions = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label8 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label12 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel11 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availableFactTypesContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel10 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnAddFactTypes = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton btnRemoveFactTypes = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.VerticalPanel buttonBarFactTypes = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label15 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel14 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel chosenPatternsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel13 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label18 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel17 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availableFieldsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel16 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnAdd = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton btnRemove = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.VerticalPanel buttonBar = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label21 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel20 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel chosenFieldsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel19 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel9 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label22 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox txtBinding = (org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox) GWT.create(org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup23 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.BindingFact() + "", "" + i18n.BindingDescription() + "");
    com.google.gwt.user.client.ui.HorizontalPanel bindingContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.CheckBox chkLogicalInsert = (com.google.gwt.user.client.ui.CheckBox) GWT.create(com.google.gwt.user.client.ui.CheckBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup25 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.UpdateFact() + "", "" + i18n.UpdateDescription() + "");
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel24 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel patternDefinition = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label26 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtColumnHeader = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Image f_Image27 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel columnHeaderContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label29 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtValueList = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup30 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.ValueList() + "", "" + i18n.ValueListsExplanation() + "");
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel28 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label31 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel defaultValueWidgetContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel defaultValueContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel criteriaExtendedEntry = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label32 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel limitedEntryValueWidgetContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.Image f_Image33 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel limitedEntryValueContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel criteriaLimitedEntry = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel fieldDefinition = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel7 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel1.add(f_Image2);
    f_Label3.setStyleName("" + res.style().wizardDTableMessage() + "");
    f_Label3.setText("" + i18n.DecisionTableWizardDuplicateBindings() + "");
    f_HorizontalPanel1.add(f_Label3);
    msgDuplicateBindings.add(f_HorizontalPanel1);
    msgDuplicateBindings.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    msgDuplicateBindings.setVisible(false);
    container.add(msgDuplicateBindings);
    f_HorizontalPanel4.add(f_Image5);
    f_Label6.setStyleName("" + res.style().wizardDTableMessage() + "");
    f_Label6.setText("" + i18n.DecisionTableWizardIncompleteActions() + "");
    f_HorizontalPanel4.add(f_Label6);
    msgIncompleteActions.add(f_HorizontalPanel4);
    msgIncompleteActions.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    msgIncompleteActions.setVisible(false);
    container.add(msgIncompleteActions);
    f_Label8.setStyleName("" + res.style().wizardDTableCaption() + "");
    f_Label8.setText("" + i18n.DecisionTableWizardDescriptionActionInsertFactFieldsPage() + "");
    f_VerticalPanel7.add(f_Label8);
    f_Label12.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label12.setText("" + i18n.DecisionTableWizardAvailableTypes() + "");
    f_SimplePanel11.add(f_Label12);
    f_VerticalPanel10.add(f_SimplePanel11);
    availableFactTypesContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availableFactTypesContainer.setHeight("200px");
    availableFactTypesContainer.setWidth("140px");
    f_VerticalPanel10.add(availableFactTypesContainer);
    f_HorizontalPanel9.add(f_VerticalPanel10);
    btnAddFactTypes.setHTML(template.html1().asString());
    btnAddFactTypes.setEnabled(false);
    buttonBarFactTypes.add(btnAddFactTypes);
    btnRemoveFactTypes.setHTML(template.html2().asString());
    btnRemoveFactTypes.setEnabled(false);
    buttonBarFactTypes.add(btnRemoveFactTypes);
    buttonBarFactTypes.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel9.add(buttonBarFactTypes);
    f_Label15.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label15.setText("" + i18n.DecisionTableWizardChosenTypes() + "");
    f_SimplePanel14.add(f_Label15);
    f_VerticalPanel13.add(f_SimplePanel14);
    chosenPatternsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    chosenPatternsContainer.setHeight("200px");
    chosenPatternsContainer.setWidth("140px");
    f_VerticalPanel13.add(chosenPatternsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel13);
    f_Label18.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label18.setText("" + i18n.DecisionTableWizardAvailableFields() + "");
    f_SimplePanel17.add(f_Label18);
    f_VerticalPanel16.add(f_SimplePanel17);
    availableFieldsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availableFieldsContainer.setHeight("200px");
    availableFieldsContainer.setWidth("140px");
    f_VerticalPanel16.add(availableFieldsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel16);
    btnAdd.setHTML(template.html3().asString());
    btnAdd.setEnabled(false);
    buttonBar.add(btnAdd);
    btnRemove.setHTML(template.html4().asString());
    btnRemove.setEnabled(false);
    buttonBar.add(btnRemove);
    buttonBar.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel9.add(buttonBar);
    f_Label21.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label21.setText("" + i18n.DecisionTableWizardChosenFields() + "");
    f_SimplePanel20.add(f_Label21);
    f_VerticalPanel19.add(f_SimplePanel20);
    chosenFieldsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    chosenFieldsContainer.setHeight("200px");
    chosenFieldsContainer.setWidth("140px");
    f_VerticalPanel19.add(chosenFieldsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel19);
    f_VerticalPanel7.add(f_HorizontalPanel9);
    f_Label22.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label22.setText("" + i18n.Binding() + "");
    bindingContainer.add(f_Label22);
    txtBinding.setStyleName("" + res.style().wizardDTableFields() + "");
    txtBinding.setEnabled(false);
    bindingContainer.add(txtBinding);
    f_InfoPopup23.setStyleName("" + res.style().wizardDTableFields() + "");
    bindingContainer.add(f_InfoPopup23);
    bindingContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    patternDefinition.add(bindingContainer);
    chkLogicalInsert.setStyleName("" + res.style().wizardDTableFields() + "");
    chkLogicalInsert.setText("" + i18n.LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved() + "");
    f_HorizontalPanel24.add(chkLogicalInsert);
    f_InfoPopup25.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel24.add(f_InfoPopup25);
    f_HorizontalPanel24.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    patternDefinition.add(f_HorizontalPanel24);
    patternDefinition.setVisible(false);
    f_VerticalPanel7.add(patternDefinition);
    f_Label26.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label26.setText("" + i18n.ColumnHeaderDescription() + "");
    columnHeaderContainer.add(f_Label26);
    txtColumnHeader.setStyleName("" + res.style().wizardDTableFields() + "");
    txtColumnHeader.setEnabled(false);
    columnHeaderContainer.add(txtColumnHeader);
    f_Image27.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image27.setTitle("" + i18n.MandatoryField() + "");
    columnHeaderContainer.add(f_Image27);
    columnHeaderContainer.setStyleName("{res.style.wizardDTableFieldContainerValid");
    fieldDefinition.add(columnHeaderContainer);
    f_Label29.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label29.setText("" + i18n.optionalValueList() + "");
    f_HorizontalPanel28.add(f_Label29);
    txtValueList.setStyleName("" + res.style().wizardDTableFields() + "");
    txtValueList.setEnabled(false);
    f_HorizontalPanel28.add(txtValueList);
    f_InfoPopup30.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel28.add(f_InfoPopup30);
    f_HorizontalPanel28.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaExtendedEntry.add(f_HorizontalPanel28);
    f_Label31.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label31.setText("" + i18n.DefaultValue() + "");
    defaultValueContainer.add(f_Label31);
    defaultValueWidgetContainer.setStyleName("" + res.style().wizardDTableFields() + "");
    defaultValueContainer.add(defaultValueWidgetContainer);
    defaultValueContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaExtendedEntry.add(defaultValueContainer);
    fieldDefinition.add(criteriaExtendedEntry);
    f_Label32.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label32.setText("" + i18n.LimitedEntryValue() + "");
    limitedEntryValueContainer.add(f_Label32);
    limitedEntryValueWidgetContainer.setStyleName("" + res.style().wizardDTableFields() + "");
    limitedEntryValueContainer.add(limitedEntryValueWidgetContainer);
    f_Image33.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image33.setTitle("" + i18n.MandatoryField() + "");
    limitedEntryValueContainer.add(f_Image33);
    limitedEntryValueContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaLimitedEntry.add(limitedEntryValueContainer);
    fieldDefinition.add(criteriaLimitedEntry);
    fieldDefinition.setVisible(false);
    f_VerticalPanel7.add(fieldDefinition);
    container.add(f_VerticalPanel7);
    container.setWidth("100%");



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnAddFactTypesClick(event);
      }
    };
    btnAddFactTypes.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnRemoveFactTypesClick(event);
      }
    };
    btnRemoveFactTypes.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnAddClick(event);
      }
    };
    btnAdd.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.btnRemoveClick(event);
      }
    };
    btnRemove.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.availableFactTypesContainer = availableFactTypesContainer;
    owner.availableFieldsContainer = availableFieldsContainer;
    owner.bindingContainer = bindingContainer;
    owner.btnAdd = btnAdd;
    owner.btnAddFactTypes = btnAddFactTypes;
    owner.btnRemove = btnRemove;
    owner.btnRemoveFactTypes = btnRemoveFactTypes;
    owner.chkLogicalInsert = chkLogicalInsert;
    owner.chosenFieldsContainer = chosenFieldsContainer;
    owner.chosenPatternsContainer = chosenPatternsContainer;
    owner.columnHeaderContainer = columnHeaderContainer;
    owner.criteriaExtendedEntry = criteriaExtendedEntry;
    owner.criteriaLimitedEntry = criteriaLimitedEntry;
    owner.defaultValueContainer = defaultValueContainer;
    owner.defaultValueWidgetContainer = defaultValueWidgetContainer;
    owner.fieldDefinition = fieldDefinition;
    owner.limitedEntryValueContainer = limitedEntryValueContainer;
    owner.limitedEntryValueWidgetContainer = limitedEntryValueWidgetContainer;
    owner.msgDuplicateBindings = msgDuplicateBindings;
    owner.msgIncompleteActions = msgIncompleteActions;
    owner.patternDefinition = patternDefinition;
    owner.txtBinding = txtBinding;
    owner.txtColumnHeader = txtColumnHeader;
    owner.txtValueList = txtValueList;

    return container;
  }
}
