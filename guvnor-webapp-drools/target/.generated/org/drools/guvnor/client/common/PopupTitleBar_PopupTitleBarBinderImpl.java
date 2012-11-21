package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PopupTitleBar_PopupTitleBarBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.common.PopupTitleBar>, org.drools.guvnor.client.common.PopupTitleBar.PopupTitleBarBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='guvnor-PopupTitleBar-bar' id='titleBar'> <div class='guvnor-PopupTitleBar-title' style='float:left;'> <span id='{0}'></span> </div> <div class='guvnor-PopupTitleBar-close-button' style='float:right;'> <span id='{1}'></span> </div> <div style='clear:both;'></div> </div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.common.PopupTitleBar owner) {

    org.drools.guvnor.client.common.PopupTitleBar_PopupTitleBarBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.common.PopupTitleBar_PopupTitleBarBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.common.PopupTitleBar_PopupTitleBarBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label titleLabel = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.common.ImageButton closeButton = new org.drools.guvnor.client.common.ImageButton(images.close());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1).asString());


    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(titleLabel, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(closeButton, domId1Element);


    owner.closeButton = closeButton;
    owner.titleLabel = titleLabel;

    return f_HTMLPanel1;
  }
}
