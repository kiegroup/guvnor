package org.drools.guvnor.client.moduleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ArtifactEditor_ArtifactEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.moduleeditor.ArtifactEditor>, org.drools.guvnor.client.moduleeditor.ArtifactEditor.ArtifactEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.moduleeditor.ArtifactEditor owner) {

    org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.moduleeditor.ArtifactEditor_ArtifactEditorBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.widgets.MessageWidget messageWidget = (org.drools.guvnor.client.widgets.MessageWidget) GWT.create(org.drools.guvnor.client.widgets.MessageWidget.class);
    org.drools.guvnor.client.widgets.MetaDataWidget metaWidget = owner.metaWidget;
    org.drools.guvnor.client.widgets.RuleDocumentWidget ruleDocumentWidget = owner.ruleDocumentWidget;
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel2 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0).asString());

    f_VerticalPanel2.add(messageWidget);
    metaWidget.setStyleName("" + style.metadataWidget() + "");
    f_VerticalPanel2.add(metaWidget);
    f_VerticalPanel2.add(ruleDocumentWidget);
    f_VerticalPanel2.setWidth("100%");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_VerticalPanel2, domId0Element);


    owner.messageWidget = messageWidget;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
