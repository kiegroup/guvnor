package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ColumnExpansionPageViewImpl_ColumnExpansionPageWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl>, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl.ColumnExpansionPageWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("&gt;&gt;")
    SafeHtml html1();
     
    @Template("&lt;&lt;")
    SafeHtml html2();
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl owner) {

    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl_ColumnExpansionPageWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl_ColumnExpansionPageWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ColumnExpansionPageViewImpl_ColumnExpansionPageWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources res = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Image f_Image2 = new com.google.gwt.user.client.ui.Image(images.warningLarge());
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel msgIncompleteConditions = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.CheckBox chkExpandInFull = (com.google.gwt.user.client.ui.CheckBox) GWT.create(com.google.gwt.user.client.ui.CheckBox.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel6 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel8 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel availableColumnsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel7 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.PushButton btnAdd = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton btnRemove = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.VerticalPanel buttonBar = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label12 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.SimplePanel f_SimplePanel11 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.ScrollPanel chosenColumnsContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel10 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel columnSelectorContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel4 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_HorizontalPanel1.add(f_Image2);
    f_Label3.setStyleName("" + res.style().wizardDTableMessage() + "");
    f_Label3.setText("" + i18n.DecisionTableWizardIncompleteConditions() + "");
    f_HorizontalPanel1.add(f_Label3);
    msgIncompleteConditions.add(f_HorizontalPanel1);
    msgIncompleteConditions.setStyleName("" + res.style().wizardDTableMessageContainer() + "");
    msgIncompleteConditions.setVisible(false);
    container.add(msgIncompleteConditions);
    f_Label5.setStyleName("" + res.style().wizardDTableCaption() + "");
    f_Label5.setText("" + i18n.DecisionTableWizardDescriptionExpandColumnsPage() + "");
    f_VerticalPanel4.add(f_Label5);
    chkExpandInFull.setStyleName("" + res.style().wizardDTableFields() + "");
    chkExpandInFull.setText("" + i18n.DecisionTableWizardExpandInFull() + "");
    chkExpandInFull.setValue(true);
    f_HorizontalPanel6.add(chkExpandInFull);
    f_HorizontalPanel6.setStyleName("" + res.style().wizardDTableFieldContainerValid() + "");
    f_VerticalPanel4.add(f_HorizontalPanel6);
    f_Label9.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label9.setText("" + i18n.DecisionTableWizardAvailableColumns() + "");
    f_SimplePanel8.add(f_Label9);
    f_VerticalPanel7.add(f_SimplePanel8);
    availableColumnsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    availableColumnsContainer.setHeight("300px");
    availableColumnsContainer.setWidth("280px");
    f_VerticalPanel7.add(availableColumnsContainer);
    columnSelectorContainer.add(f_VerticalPanel7);
    btnAdd.setHTML(template.html1().asString());
    btnAdd.setEnabled(false);
    buttonBar.add(btnAdd);
    btnRemove.setHTML(template.html2().asString());
    btnRemove.setEnabled(false);
    buttonBar.add(btnRemove);
    buttonBar.setStyleName("" + res.style().wizardDTableButtons() + "");
    columnSelectorContainer.add(buttonBar);
    f_Label12.setStyleName("" + res.style().wizardDTableHeader() + "");
    f_Label12.setText("" + i18n.DecisionTableWizardChosenColumns() + "");
    f_SimplePanel11.add(f_Label12);
    f_VerticalPanel10.add(f_SimplePanel11);
    chosenColumnsContainer.setStyleName("" + res.style().wizardDTableList() + "");
    chosenColumnsContainer.setHeight("300px");
    chosenColumnsContainer.setWidth("280px");
    f_VerticalPanel10.add(chosenColumnsContainer);
    columnSelectorContainer.add(f_VerticalPanel10);
    columnSelectorContainer.setVisible(false);
    f_VerticalPanel4.add(columnSelectorContainer);
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

    owner.availableColumnsContainer = availableColumnsContainer;
    owner.btnAdd = btnAdd;
    owner.btnRemove = btnRemove;
    owner.chkExpandInFull = chkExpandInFull;
    owner.chosenColumnsContainer = chosenColumnsContainer;
    owner.columnSelectorContainer = columnSelectorContainer;
    owner.msgIncompleteConditions = msgIncompleteConditions;

    return container;
  }
}
