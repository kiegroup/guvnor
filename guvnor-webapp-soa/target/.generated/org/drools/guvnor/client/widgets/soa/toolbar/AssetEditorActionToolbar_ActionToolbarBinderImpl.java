package org.drools.guvnor.client.widgets.soa.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AssetEditorActionToolbar_ActionToolbarBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar>, org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar.ActionToolbarBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("" + "{0}" + "")
    SafeHtml html1(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html2(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html3(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html4(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html5(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html6(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html7(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html8(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html9(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html10(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html11(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html12(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html13(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html14(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html15(String arg0);
     
    @Template("<table style='width:100%; border-collapse:collapse;'> <tr> <td style='width:4px;'> <div class='{0}'></div> </td> <td> <div class='{1}'></div> </td> <td style='width:4px;'> <div class='{2}'></div> </td> </tr> <tr> <td class='{3}'></td> <td class='{4}'> <div style='float:left;'> <span id='{5}'></span> </div> <div style='float:right;'> <span id='{6}'></span> </div> <div style='clear:both;'></div> </td> <td class='{7}'></td> </tr> <tr> <td style='width:4px;'> <div class='{8}'></div> </td> <td> <div class='{9}'></div> </td> <td style='width:4px;'> <div class='{10}'></div> </td> </tr> </table>")
    SafeHtml html16(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar owner) {

    org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.RoundedCornersResource roundCornersResource = (org.drools.guvnor.client.resources.RoundedCornersResource) GWT.create(org.drools.guvnor.client.resources.RoundedCornersResource.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.widgets.soa.toolbar.AssetEditorActionToolbar_ActionToolbarBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.MenuItem saveChanges = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem saveChangesAndClose = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem archive = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem delete = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuBar f_MenuBar4 = new com.google.gwt.user.client.ui.MenuBar(true);
    com.google.gwt.user.client.ui.MenuItem f_MenuItem3 = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem copy = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem rename = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem promoteToGlobal = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem selectWorkingSets = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem changeStatus = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuBar f_MenuBar6 = new com.google.gwt.user.client.ui.MenuBar(true);
    com.google.gwt.user.client.ui.MenuItem f_MenuItem5 = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem validate = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem verify = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuItem viewSource = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuBar f_MenuBar7 = new com.google.gwt.user.client.ui.MenuBar(true);
    com.google.gwt.user.client.ui.MenuItem sourceMenu = new com.google.gwt.user.client.ui.MenuItem("", (com.google.gwt.user.client.Command) null);
    com.google.gwt.user.client.ui.MenuBar f_MenuBar2 = (com.google.gwt.user.client.ui.MenuBar) GWT.create(com.google.gwt.user.client.ui.MenuBar.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label status = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html16("" + roundCornersResource.roundCornersCss().greyTopLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().greyTopClass() + "", "" + roundCornersResource.roundCornersCss().greyTopRightCornerClass() + "", "" + roundCornersResource.roundCornersCss().greySideLeftClass() + "", "" + roundCornersResource.roundCornersCss().greyCenterClass() + "", domId0, domId1, "" + roundCornersResource.roundCornersCss().greySideRightClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomRightCornerClass() + "").asString());

    saveChanges.setHTML(template.html1("" + i18n.SaveChanges() + "").asString());
    saveChanges.setTitle("" + i18n.CommitAnyChangesForThisAsset() + "");
    f_MenuBar4.addItem(saveChanges);
    saveChangesAndClose.setHTML(template.html2("" + i18n.SaveAndClose() + "").asString());
    saveChangesAndClose.setTitle("" + i18n.CommitAnyChangesForThisAsset() + "");
    f_MenuBar4.addItem(saveChangesAndClose);
    archive.setHTML(template.html3("" + i18n.Archive() + "").asString());
    f_MenuBar4.addItem(archive);
    delete.setHTML(template.html4("" + i18n.Delete() + "").asString());
    delete.setTitle("" + i18n.DeleteAssetTooltip() + "");
    f_MenuBar4.addItem(delete);
    f_MenuItem3.setSubMenu(f_MenuBar4);
    f_MenuItem3.setHTML(template.html5("" + i18n.File() + "").asString());
    f_MenuItem3.setStyleName("" + style.menuItem() + "");
    f_MenuBar2.addItem(f_MenuItem3);
    copy.setHTML(template.html6("" + i18n.Copy() + "").asString());
    f_MenuBar6.addItem(copy);
    rename.setHTML(template.html7("" + i18n.Rename() + "").asString());
    f_MenuBar6.addItem(rename);
    promoteToGlobal.setHTML(template.html8("" + i18n.PromoteToGlobal() + "").asString());
    f_MenuBar6.addItem(promoteToGlobal);
    selectWorkingSets.setHTML(template.html9("" + i18n.SelectWorkingSets() + "").asString());
    f_MenuBar6.addItem(selectWorkingSets);
    changeStatus.setHTML(template.html10("" + i18n.ChangeStatus() + "").asString());
    f_MenuBar6.addItem(changeStatus);
    f_MenuItem5.setSubMenu(f_MenuBar6);
    f_MenuItem5.setHTML(template.html11("" + i18n.Edit() + "").asString());
    f_MenuItem5.setStyleName("" + style.menuItem() + "");
    f_MenuBar2.addItem(f_MenuItem5);
    validate.setHTML(template.html12("" + i18n.Validate() + "").asString());
    f_MenuBar7.addItem(validate);
    verify.setHTML(template.html13("" + i18n.Verify() + "").asString());
    f_MenuBar7.addItem(verify);
    viewSource.setHTML(template.html14("" + i18n.ViewSource() + "").asString());
    f_MenuBar7.addItem(viewSource);
    sourceMenu.setSubMenu(f_MenuBar7);
    sourceMenu.setHTML(template.html15("" + i18n.Source() + "").asString());
    sourceMenu.setStyleName("" + style.menuItem() + "");
    f_MenuBar2.addItem(sourceMenu);
    f_MenuBar2.setStyleName("" + style.menuBar() + "");
    status.setText("" + "" + i18n.Status() + "" + "");
    status.setStyleName("" + style.statusLabel() + "");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_MenuBar2, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(status, domId1Element);


    owner.archive = archive;
    owner.changeStatus = changeStatus;
    owner.copy = copy;
    owner.delete = delete;
    owner.promoteToGlobal = promoteToGlobal;
    owner.rename = rename;
    owner.saveChanges = saveChanges;
    owner.saveChangesAndClose = saveChangesAndClose;
    owner.selectWorkingSets = selectWorkingSets;
    owner.sourceMenu = sourceMenu;
    owner.status = status;
    owner.validate = validate;
    owner.verify = verify;
    owner.viewSource = viewSource;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
