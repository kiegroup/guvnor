package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl>, org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl.BulkRunResultViewImplBinder {

  interface Template extends SafeHtmlTemplates {
    @Template(" ")
    SafeHtml html1();
     
    @Template(" ")
    SafeHtml html2();
     
    @Template("<table class='guvnor-FormPanel'> <tr> <td align='right' style='vertical-align: top;'> <span id='{0}'></span> </td> <td> <span id='{1}'></span> </td> </tr> <tr> <td align='right' style='vertical-align: top;'> <span id='{2}'></span> </td> <td> <span id='{3}'></span> </td> </tr> <tr> <td align='right' style='vertical-align: top;'> <span id='{4}'></span> </td> <td> <span id='{5}'></span> </td> </tr> <tr> <td align='right' style='vertical-align: top;'> <span id='{6}'></span> </td> <td> <span id='{7}'></span> </td> </tr> </table>")
    SafeHtml html3(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl owner) {

    org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.explorer.navigation.qa.BulkRunResultViewImpl_BulkRunResultViewImplBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.ToggleLabel overAll = (org.drools.guvnor.client.util.ToggleLabel) GWT.create(org.drools.guvnor.client.util.ToggleLabel.class);
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.PercentageBar resultsBar = (org.drools.guvnor.client.util.PercentageBar) GWT.create(org.drools.guvnor.client.util.PercentageBar.class);
    com.google.gwt.user.client.ui.HTML f_HTML6 = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
    org.drools.guvnor.client.common.SmallLabel failuresOutOfExpectations = (org.drools.guvnor.client.common.SmallLabel) GWT.create(org.drools.guvnor.client.common.SmallLabel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel5 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label7 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId5 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.PercentageBar coveredPercentBar = (org.drools.guvnor.client.util.PercentageBar) GWT.create(org.drools.guvnor.client.util.PercentageBar.class);
    com.google.gwt.user.client.ui.HTML f_HTML9 = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
    org.drools.guvnor.client.common.SmallLabel ruleCoveragePercent = (org.drools.guvnor.client.common.SmallLabel) GWT.create(org.drools.guvnor.client.common.SmallLabel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel8 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId6 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label10 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId7 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.ValueList uncoveredRules = (org.drools.guvnor.client.util.ValueList) GWT.create(org.drools.guvnor.client.util.ValueList.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel2 = new com.google.gwt.user.client.ui.HTMLPanel(template.html3(domId0, domId1, domId2, domId3, domId4, domId5, domId6, domId7).asString());
    org.drools.guvnor.client.explorer.navigation.qa.SummaryTableView summaryTableView = owner.summaryTableView;
    com.google.gwt.user.client.ui.Button closeButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel11 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);

    f_Label3.setText("" + i18n.OverallResult() + "");
    overAll.setStyleName("" + style.bold() + "");
    overAll.setFailureText("" + i18n.FailureOverall() + "");
    overAll.setSuccessText("" + i18n.SuccessOverall() + "");
    f_Label4.setText("" + i18n.Results() + "");
    resultsBar.setWidth("300");
    f_HorizontalPanel5.add(resultsBar);
    f_HTML6.setHTML(template.html1().asString());
    f_HorizontalPanel5.add(f_HTML6);
    f_HorizontalPanel5.add(failuresOutOfExpectations);
    f_Label7.setText("" + i18n.RulesCovered() + "");
    coveredPercentBar.setWidth("300");
    coveredPercentBar.setInCompleteBarColor("YELLOW");
    f_HorizontalPanel8.add(coveredPercentBar);
    f_HTML9.setHTML(template.html2().asString());
    f_HorizontalPanel8.add(f_HTML9);
    f_HorizontalPanel8.add(ruleCoveragePercent);
    f_Label10.setText("" + i18n.UncoveredRules() + "");
    uncoveredRules.setMaxVisibleItemCount(20);
    f_VerticalPanel1.add(f_HTMLPanel2);
    f_VerticalPanel11.add(summaryTableView);
    closeButton.setText("" + i18n.Close() + "");
    f_VerticalPanel11.add(closeButton);
    f_VerticalPanel11.setStyleName("guvnor-FormPanel");
    f_VerticalPanel11.setWidth("100%");
    f_VerticalPanel1.add(f_VerticalPanel11);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel2.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    com.google.gwt.user.client.Element domId4Element = com.google.gwt.dom.client.Document.get().getElementById(domId4).cast();
    com.google.gwt.user.client.Element domId5Element = com.google.gwt.dom.client.Document.get().getElementById(domId5).cast();
    com.google.gwt.user.client.Element domId6Element = com.google.gwt.dom.client.Document.get().getElementById(domId6).cast();
    com.google.gwt.user.client.Element domId7Element = com.google.gwt.dom.client.Document.get().getElementById(domId7).cast();
    attachRecord0.detach();
    f_HTMLPanel2.addAndReplaceElement(f_Label3, domId0Element);
    f_HTMLPanel2.addAndReplaceElement(overAll, domId1Element);
    f_HTMLPanel2.addAndReplaceElement(f_Label4, domId2Element);
    f_HTMLPanel2.addAndReplaceElement(f_HorizontalPanel5, domId3Element);
    f_HTMLPanel2.addAndReplaceElement(f_Label7, domId4Element);
    f_HTMLPanel2.addAndReplaceElement(f_HorizontalPanel8, domId5Element);
    f_HTMLPanel2.addAndReplaceElement(f_Label10, domId6Element);
    f_HTMLPanel2.addAndReplaceElement(uncoveredRules, domId7Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.close(event);
      }
    };
    closeButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.coveredPercentBar = coveredPercentBar;
    owner.failuresOutOfExpectations = failuresOutOfExpectations;
    owner.overAll = overAll;
    owner.resultsBar = resultsBar;
    owner.ruleCoveragePercent = ruleCoveragePercent;
    owner.uncoveredRules = uncoveredRules;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_VerticalPanel1;
  }
}
