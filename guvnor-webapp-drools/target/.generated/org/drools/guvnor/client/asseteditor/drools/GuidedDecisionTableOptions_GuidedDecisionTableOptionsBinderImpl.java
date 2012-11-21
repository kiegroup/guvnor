package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class GuidedDecisionTableOptions_GuidedDecisionTableOptionsBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions>, org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions.GuidedDecisionTableOptionsBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions owner) {

    org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions_GuidedDecisionTableOptionsBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions_GuidedDecisionTableOptionsBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.GuidedDecisionTableOptions_GuidedDecisionTableOptionsBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.CheckBox chkUseWizard = (com.google.gwt.user.client.ui.CheckBox) GWT.create(com.google.gwt.user.client.ui.CheckBox.class);
    com.google.gwt.user.client.ui.RadioButton optExtendedEntry = new com.google.gwt.user.client.ui.RadioButton("tableFormat");
    com.google.gwt.user.client.ui.RadioButton optLimitedEntry = new com.google.gwt.user.client.ui.RadioButton("tableFormat");
    com.google.gwt.user.client.ui.VerticalPanel tableFormat = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    chkUseWizard.setText("" + i18n.UseWizardToBuildAsset() + "");
    f_VerticalPanel1.add(chkUseWizard);
    optExtendedEntry.setText("" + i18n.TableFormatExtendedEntry() + "");
    optExtendedEntry.setValue(true);
    tableFormat.add(optExtendedEntry);
    optLimitedEntry.setText("" + i18n.TableFormatLimitedEntry() + "");
    tableFormat.add(optLimitedEntry);
    f_VerticalPanel1.add(tableFormat);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.chkUseWizardClick(event);
      }
    };
    chkUseWizard.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.optExtendedEntryClick(event);
      }
    };
    optExtendedEntry.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.optoptLimitedEntryClick(event);
      }
    };
    optLimitedEntry.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    owner.chkUseWizard = chkUseWizard;

    return f_VerticalPanel1;
  }
}
