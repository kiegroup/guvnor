package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PercentageBar_PercentageBarBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.util.PercentageBar>, org.drools.guvnor.client.util.PercentageBar.PercentageBarBinder {

  interface Template extends SafeHtmlTemplates {
    @Template(" ")
    SafeHtml html1();
     
    @Template("<div class='{0}' id='{1}'> <div class='{2}' id='{3}'> <span id='{4}'></span> </div> <div id='{5}'> <span id='{6}'></span> </div> </div>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.util.PercentageBar owner) {

    org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenBundle.class);
    org.drools.guvnor.client.util.PercentageBar_PercentageBarBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.google.gwt.dom.client.DivElement wrapper = null;
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.dom.client.DivElement text = null;
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label percentage = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.dom.client.DivElement bar = null;
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.HTML f_HTML2 = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html2("" + style.wrapper() + "", domId0, "" + style.text() + "", domId1, domId2, domId3, domId4).asString());

    f_HTML2.setHTML(template.html1().asString());

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    wrapper = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    wrapper.removeAttribute("id");
    text = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    text.removeAttribute("id");
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    bar = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    bar.removeAttribute("id");
    com.google.gwt.user.client.Element domId4Element = com.google.gwt.dom.client.Document.get().getElementById(domId4).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(percentage, domId2Element);
    f_HTMLPanel1.addAndReplaceElement(f_HTML2, domId4Element);


    owner.bar = bar;
    owner.percentage = percentage;
    owner.text = text;
    owner.wrapper = wrapper;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
