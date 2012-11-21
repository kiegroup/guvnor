package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactPatternConstraintsPageViewImpl_FactPatternConstraintsPageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl>, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl.FactPatternConstraintsPageWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("&gt;&gt;")
    SafeHtml html1();
     
    @Template("&lt;&lt;")
    SafeHtml html2();
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl owner) {

    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl_FactPatternConstraintsPageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl_FactPatternConstraintsPageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.FactPatternConstraintsPageViewImpl_FactPatternConstraintsPageWidgetBinderImpl_GenBundle.class);
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
    com.google.gwt.user.client.ui.HorizontalPanel msgIncompleteConditions = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label8 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label12 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel11 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availablePatternsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel10 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label15 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel14 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availableFieldsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel13 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnAdd = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton btnRemove = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.VerticalPanel buttonBar = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label18 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel17 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel chosenConditionsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel16 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnMoveUp = owner.btnMoveUp;
    com.google.gwt.user.client.ui.PushButton btnMoveDown = owner.btnMoveDown;
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel19 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel9 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label20 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.RadioButton optLiteral = new com.google.gwt.user.client.ui.RadioButton("calculationType");
    com.google.gwt.user.client.ui.RadioButton optFormula = new com.google.gwt.user.client.ui.RadioButton("calculationType");
    com.google.gwt.user.client.ui.RadioButton optPredicate = new com.google.gwt.user.client.ui.RadioButton("calculationType");
    com.google.gwt.user.client.ui.HorizontalPanel calculationType = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label21 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtColumnHeader = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.Image f_Image22 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel columnHeaderContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label23 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtPredicateExpression = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup24 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.Predicates() + "", "" + i18n.PredicatesInfo() + "");
    com.google.gwt.user.client.ui.HorizontalPanel predicateExpressionContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label25 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel ddOperatorContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.Image f_Image26 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel operatorContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label28 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtValueList = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    org.drools.guvnor.client.common.InfoPopup f_InfoPopup29 = new org.drools.guvnor.client.common.InfoPopup("" + i18n.ValueList() + "", "" + i18n.ValueListsExplanation() + "");
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel27 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label30 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel defaultValueWidgetContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel defaultValueContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel criteriaExtendedEntry = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label31 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel limitedEntryValueWidgetContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.Image f_Image32 = new com.google.gwt.user.client.ui.Image(images.mandatory());
    com.google.gwt.user.client.ui.HorizontalPanel limitedEntryValueContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel criteriaLimitedEntry = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel conditionDefinition = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
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
    f_Label6.setText("" + i18n.DecisionTableWizardIncompleteConditions() + "");
    f_HorizontalPanel4.add(f_Label6);
    msgIncompleteConditions.add(f_HorizontalPanel4);
    msgIncompleteConditions.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    msgIncompleteConditions.setVisible(false);
    container.add(msgIncompleteConditions);
    f_Label8.setStyleName("" + res.style().wizardDTableCaption() + "");
    f_Label8.setText("" + i18n.DecisionTableWizardDescriptionFactPatternConstraintsPage() + "");
    f_VerticalPanel7.add(f_Label8);
    f_Label12.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label12.setText("" + i18n.DecisionTableWizardAvailableTypes() + "");
    f_SimplePanel11.add(f_Label12);
    f_VerticalPanel10.add(f_SimplePanel11);
    availablePatternsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availablePatternsContainer.setHeight("220px");
    availablePatternsContainer.setWidth("180px");
    f_VerticalPanel10.add(availablePatternsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel10);
    f_Label15.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label15.setText("" + i18n.DecisionTableWizardAvailableFields() + "");
    f_SimplePanel14.add(f_Label15);
    f_VerticalPanel13.add(f_SimplePanel14);
    availableFieldsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availableFieldsContainer.setHeight("220px");
    availableFieldsContainer.setWidth("180px");
    f_VerticalPanel13.add(availableFieldsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel13);
    btnAdd.setHTML(template.html1().asString());
    btnAdd.setEnabled(false);
    buttonBar.add(btnAdd);
    btnRemove.setHTML(template.html2().asString());
    btnRemove.setEnabled(false);
    buttonBar.add(btnRemove);
    buttonBar.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel9.add(buttonBar);
    f_Label18.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label18.setText("" + i18n.DecisionTableWizardChosenConditions() + "");
    f_SimplePanel17.add(f_Label18);
    f_VerticalPanel16.add(f_SimplePanel17);
    chosenConditionsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    chosenConditionsContainer.setHeight("220px");
    chosenConditionsContainer.setWidth("180px");
    f_VerticalPanel16.add(chosenConditionsContainer);
    f_HorizontalPanel9.add(f_VerticalPanel16);
    btnMoveUp.setEnabled(false);
    f_VerticalPanel19.add(btnMoveUp);
    btnMoveDown.setEnabled(false);
    f_VerticalPanel19.add(btnMoveDown);
    f_VerticalPanel19.setStyleName("" + res.style().wizardDTableButtons() + "");
    f_HorizontalPanel9.add(f_VerticalPanel19);
    f_VerticalPanel7.add(f_HorizontalPanel9);
    f_Label20.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label20.setText("" + i18n.CalculationType() + "");
    calculationType.add(f_Label20);
    optLiteral.setStyleName("" + res.style().wizardDTableFields() + "");
    optLiteral.setEnabled(false);
    optLiteral.setText("" + i18n.LiteralValue() + "");
    calculationType.add(optLiteral);
    optFormula.setStyleName("" + res.style().wizardDTableFields() + "");
    optFormula.setEnabled(false);
    optFormula.setText("" + i18n.Formula() + "");
    calculationType.add(optFormula);
    optPredicate.setStyleName("" + res.style().wizardDTableFields() + "");
    optPredicate.setEnabled(false);
    optPredicate.setText("" + i18n.Predicate() + "");
    calculationType.add(optPredicate);
    calculationType.setStyleName("{res.style.wizardDTableFieldContainerValid");
    conditionDefinition.add(calculationType);
    f_Label21.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label21.setText("" + i18n.ColumnHeaderDescription() + "");
    columnHeaderContainer.add(f_Label21);
    txtColumnHeader.setStyleName("" + res.style().wizardDTableFields() + "");
    txtColumnHeader.setEnabled(false);
    columnHeaderContainer.add(txtColumnHeader);
    f_Image22.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image22.setTitle("" + i18n.MandatoryField() + "");
    columnHeaderContainer.add(f_Image22);
    columnHeaderContainer.setStyleName("{res.style.wizardDTableFieldContainerValid");
    conditionDefinition.add(columnHeaderContainer);
    f_Label23.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label23.setText("" + i18n.DecisionTableWizardPredicateExpression() + "");
    predicateExpressionContainer.add(f_Label23);
    txtPredicateExpression.setStyleName("" + res.style().wizardDTableFields() + "");
    txtPredicateExpression.setEnabled(false);
    predicateExpressionContainer.add(txtPredicateExpression);
    f_InfoPopup24.setStyleName("" + res.style().wizardDTableFields() + "");
    predicateExpressionContainer.add(f_InfoPopup24);
    predicateExpressionContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    predicateExpressionContainer.setVisible(false);
    conditionDefinition.add(predicateExpressionContainer);
    f_Label25.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label25.setText("" + i18n.Operator() + "");
    operatorContainer.add(f_Label25);
    operatorContainer.add(ddOperatorContainer);
    f_Image26.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image26.setTitle("" + i18n.MandatoryField() + "");
    operatorContainer.add(f_Image26);
    operatorContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    conditionDefinition.add(operatorContainer);
    f_Label28.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label28.setText("" + i18n.optionalValueList() + "");
    f_HorizontalPanel27.add(f_Label28);
    txtValueList.setStyleName("" + res.style().wizardDTableFields() + "");
    txtValueList.setEnabled(false);
    f_HorizontalPanel27.add(txtValueList);
    f_InfoPopup29.setStyleName("" + res.style().wizardDTableFields() + "");
    f_HorizontalPanel27.add(f_InfoPopup29);
    f_HorizontalPanel27.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaExtendedEntry.add(f_HorizontalPanel27);
    f_Label30.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label30.setText("" + i18n.DefaultValue() + "");
    defaultValueContainer.add(f_Label30);
    defaultValueWidgetContainer.setStyleName("" + res.style().wizardDTableFields() + "");
    defaultValueContainer.add(defaultValueWidgetContainer);
    defaultValueContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaExtendedEntry.add(defaultValueContainer);
    conditionDefinition.add(criteriaExtendedEntry);
    f_Label31.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Label31.setText("" + i18n.LimitedEntryValue() + "");
    limitedEntryValueContainer.add(f_Label31);
    limitedEntryValueWidgetContainer.setStyleName("" + res.style().wizardDTableFields() + "");
    limitedEntryValueContainer.add(limitedEntryValueWidgetContainer);
    f_Image32.setStyleName("" + res.style().wizardDTableFields() + "");
    f_Image32.setTitle("" + i18n.MandatoryField() + "");
    limitedEntryValueContainer.add(f_Image32);
    limitedEntryValueContainer.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    criteriaLimitedEntry.add(limitedEntryValueContainer);
    conditionDefinition.add(criteriaLimitedEntry);
    conditionDefinition.setVisible(false);
    f_VerticalPanel7.add(conditionDefinition);
    container.add(f_VerticalPanel7);
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

    owner.availableFieldsContainer = availableFieldsContainer;
    owner.availablePatternsContainer = availablePatternsContainer;
    owner.btnAdd = btnAdd;
    owner.btnRemove = btnRemove;
    owner.calculationType = calculationType;
    owner.chosenConditionsContainer = chosenConditionsContainer;
    owner.columnHeaderContainer = columnHeaderContainer;
    owner.conditionDefinition = conditionDefinition;
    owner.criteriaExtendedEntry = criteriaExtendedEntry;
    owner.criteriaLimitedEntry = criteriaLimitedEntry;
    owner.ddOperatorContainer = ddOperatorContainer;
    owner.defaultValueContainer = defaultValueContainer;
    owner.defaultValueWidgetContainer = defaultValueWidgetContainer;
    owner.limitedEntryValueContainer = limitedEntryValueContainer;
    owner.limitedEntryValueWidgetContainer = limitedEntryValueWidgetContainer;
    owner.msgDuplicateBindings = msgDuplicateBindings;
    owner.msgIncompleteConditions = msgIncompleteConditions;
    owner.operatorContainer = operatorContainer;
    owner.optFormula = optFormula;
    owner.optLiteral = optLiteral;
    owner.optPredicate = optPredicate;
    owner.predicateExpressionContainer = predicateExpressionContainer;
    owner.txtColumnHeader = txtColumnHeader;
    owner.txtPredicateExpression = txtPredicateExpression;
    owner.txtValueList = txtValueList;

    return container;
  }
}
