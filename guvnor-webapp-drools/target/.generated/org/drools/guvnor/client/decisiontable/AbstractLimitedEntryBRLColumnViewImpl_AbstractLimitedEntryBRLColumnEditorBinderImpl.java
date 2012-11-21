package org.drools.guvnor.client.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AbstractLimitedEntryBRLColumnViewImpl_AbstractLimitedEntryBRLColumnEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl>, org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl.AbstractLimitedEntryBRLColumnEditorBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl owner) {

    org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl_AbstractLimitedEntryBRLColumnEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl_AbstractLimitedEntryBRLColumnEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.decisiontable.AbstractLimitedEntryBRLColumnViewImpl_AbstractLimitedEntryBRLColumnEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.WizardResources wizardResources = (org.drools.guvnor.client.resources.WizardResources) GWT.create(org.drools.guvnor.client.resources.WizardResources.class);
    com.google.gwt.user.client.ui.Label f_Label2 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.TextBox txtColumnHeader = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.CheckBox chkHideColumn = (com.google.gwt.user.client.ui.CheckBox) GWT.create(com.google.gwt.user.client.ui.CheckBox.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel3 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller ruleModeller = owner.ruleModeller;
    com.google.gwt.user.client.ui.ScrollPanel brlEditorContainer = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.Button cmdApplyChanges = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.VerticalPanel container = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_Label2.setStyleName("" + wizardResources.style().wizardDTableFields() + "");
    f_Label2.setText("" + i18n.ColumnHeaderDescription() + "");
    f_HorizontalPanel1.add(f_Label2);
    txtColumnHeader.setStyleName("" + wizardResources.style().wizardDTableFields() + "");
    f_HorizontalPanel1.add(txtColumnHeader);
    f_HorizontalPanel1.setStyleName("" + wizardResources.style().wizardDTableFieldContainerValid() + "");
    container.add(f_HorizontalPanel1);
    f_Label4.setStyleName("" + wizardResources.style().wizardDTableFields() + "");
    f_Label4.setText("" + i18n.HideThisColumn() + "");
    f_HorizontalPanel3.add(f_Label4);
    chkHideColumn.setStyleName("" + wizardResources.style().wizardDTableFields() + "");
    f_HorizontalPanel3.add(chkHideColumn);
    f_HorizontalPanel3.setStyleName("" + wizardResources.style().wizardDTableFieldContainerValid() + "");
    container.add(f_HorizontalPanel3);
    brlEditorContainer.add(ruleModeller);
    brlEditorContainer.setStyleName("" + wizardResources.style().scrollPanel() + "");
    container.add(brlEditorContainer);
    cmdApplyChanges.setText("" + i18n.ApplyChanges() + "");
    container.add(cmdApplyChanges);
    container.setWidth("100%");



    final com.google.gwt.event.dom.client.ChangeHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ChangeHandler() {
      public void onChange(com.google.gwt.event.dom.client.ChangeEvent event) {
        owner.columnHanderChangeHandler(event);
      }
    };
    txtColumnHeader.addChangeHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.hideColumnClickHandler(event);
      }
    };
    chkHideColumn.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.applyChangesClickHandler(event);
      }
    };
    cmdApplyChanges.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    owner.brlEditorContainer = brlEditorContainer;
    owner.chkHideColumn = chkHideColumn;
    owner.cmdApplyChanges = cmdApplyChanges;
    owner.txtColumnHeader = txtColumnHeader;

    return container;
  }
}
