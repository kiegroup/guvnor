package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DecoratedTextArea_DecoratedTextAreaBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.util.DecoratedTextArea>, org.drools.guvnor.client.util.DecoratedTextArea.DecoratedTextAreaBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<table style='width:100%; border-collapse:collapse;'> <tr> <td style='width:4px;'><div class='{0}'></div></td> <td><div class='{1}'></div></td> <td style='width:4px;'><div class='{2}'></div></td> </tr> <tr> <td class='{3}'></td> <td class='{4}'> <span id='{5}'></span> </td> <td class='{6}'></td> </tr> <tr> <td style='width:4px;'><div class='{7}'></div></td> <td><div class='{8}'></div></td> <td style='width:4px;'><div class='{9}'></div></td> </tr> </table>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.util.DecoratedTextArea owner) {

    org.drools.guvnor.client.util.DecoratedTextArea_DecoratedTextAreaBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.util.DecoratedTextArea_DecoratedTextAreaBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.util.DecoratedTextArea_DecoratedTextAreaBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.RoundedCornersResource roundCornersResource = (org.drools.guvnor.client.resources.RoundedCornersResource) GWT.create(org.drools.guvnor.client.resources.RoundedCornersResource.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.TextArea textArea = (com.google.gwt.user.client.ui.TextArea) GWT.create(com.google.gwt.user.client.ui.TextArea.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1("" + roundCornersResource.roundCornersCss().whiteTopLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().whiteTopClass() + "", "" + roundCornersResource.roundCornersCss().whiteTopRightCornerClass() + "", "" + roundCornersResource.roundCornersCss().whiteSideLeftClass() + "", "" + roundCornersResource.roundCornersCss().whiteCenterClass() + "", domId0, "" + roundCornersResource.roundCornersCss().whiteSideRightClass() + "", "" + roundCornersResource.roundCornersCss().whiteBottomLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().whiteBottomClass() + "", "" + roundCornersResource.roundCornersCss().whiteBottomRightCornerClass() + "").asString());

    textArea.setStyleName("" + roundCornersResource.roundCornersCss().cleanTextArea() + "");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(textArea, domId0Element);


    owner.textArea = textArea;

    return f_HTMLPanel1;
  }
}
