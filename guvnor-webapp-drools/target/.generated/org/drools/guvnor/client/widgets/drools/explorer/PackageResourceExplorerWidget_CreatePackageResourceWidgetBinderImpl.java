package org.drools.guvnor.client.widgets.drools.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PackageResourceExplorerWidget_CreatePackageResourceWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget>, org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget.CreatePackageResourceWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<table style='width:100%; border-collapse:collapse;'> <tr> <td style='text-align:right;'><b><span id='{0}'></span></b></td> <td><span id='{1}'></span></td> </tr> <tr> <td style='text-align:right;'><b><span id='{2}'></span></b></td> <td><span id='{3}'></span></td> </tr> <tr> <td colspan='2'><b><span id='{4}'></span></b></td> </tr> <tr height='100%' width='100%'> <td colspan='2' height='100%' width='100%'> <span id='{5}'></span> </td> </tr> </table>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget owner) {

    org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget_CreatePackageResourceWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget_CreatePackageResourceWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.explorer.PackageResourceExplorerWidget_CreatePackageResourceWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label labelName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.TextBox txtName = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label labelDescr = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.TextBox txtDescription = (com.google.gwt.user.client.ui.TextBox) GWT.create(com.google.gwt.user.client.ui.TextBox.class);
    java.lang.String domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label2 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId5 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Tree packageTree = (com.google.gwt.user.client.ui.Tree) GWT.create(com.google.gwt.user.client.ui.Tree.class);
    com.google.gwt.user.client.ui.ScrollPanel sclTreePanel = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1, domId2, domId3, domId4, domId5).asString());

    labelName.setText("" + i18n.Name() + ":");
    labelDescr.setText("" + i18n.Description() + ":");
    f_Label2.setText("" + i18n.Packages() + ":");
    sclTreePanel.add(packageTree);
    sclTreePanel.setHeight("150px");
    sclTreePanel.setWidth("100%");
    f_HTMLPanel1.setWidth("100%");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    com.google.gwt.user.client.Element domId4Element = com.google.gwt.dom.client.Document.get().getElementById(domId4).cast();
    com.google.gwt.user.client.Element domId5Element = com.google.gwt.dom.client.Document.get().getElementById(domId5).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(labelName, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(txtName, domId1Element);
    f_HTMLPanel1.addAndReplaceElement(labelDescr, domId2Element);
    f_HTMLPanel1.addAndReplaceElement(txtDescription, domId3Element);
    f_HTMLPanel1.addAndReplaceElement(f_Label2, domId4Element);
    f_HTMLPanel1.addAndReplaceElement(sclTreePanel, domId5Element);


    owner.labelDescr = labelDescr;
    owner.labelName = labelName;
    owner.packageTree = packageTree;
    owner.txtDescription = txtDescription;
    owner.txtName = txtName;

    return f_HTMLPanel1;
  }
}
