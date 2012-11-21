package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactModelEditor_FactModelEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor>, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor.FactModelEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='float:left;'> <span id='{0}'></span> </div> <div style='float:right;'> <table> <tr> <td width='17px'> <span id='{1}'></span> </td> <td width='17px'> <span id='{2}'></span> </td> <td width='17px'> <span id='{3}'></span> </td> <td width='17px'> <span id='{4}'></span> </td> </tr> </table> </div> <div style='clear:both;'></div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4);
     
    @Template("<table style='width:100%; border-collapse:collapse;'> <tr> <td style='width:4px;'> <div class='{0}'></div> </td> <td> <div class='{1}'></div> </td> <td style='width:4px;'> <div class='{2}'></div> </td> </tr> <tr> <td class='{3}'></td> <td class='{4}'> <span id='{5}'></span> </td> <td class='{6}'></td> </tr> <tr> <td style='width:4px;'> <div class='{7}'></div> </td> <td> <div class='{8}'></div> </td> <td style='width:4px;'> <div class='{9}'></div> </td> </tr> </table>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor_FactModelEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor_FactModelEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelEditor_FactModelEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    org.drools.guvnor.client.resources.RoundedCornersResource roundCornersResource = (org.drools.guvnor.client.resources.RoundedCornersResource) GWT.create(org.drools.guvnor.client.resources.RoundedCornersResource.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image icon = (com.google.gwt.user.client.ui.Image) GWT.create(com.google.gwt.user.client.ui.Image.class);
    com.google.gwt.user.client.ui.Label titleLabel = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel3 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image editIcon = new com.google.gwt.user.client.ui.Image(images.edit());
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image moveUpIcon = new com.google.gwt.user.client.ui.Image(images.shuffleUp());
    java.lang.String domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image moveDownIcon = new com.google.gwt.user.client.ui.Image(images.shuffleDown());
    java.lang.String domId5 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image deleteIcon = new com.google.gwt.user.client.ui.Image(images.deleteItemSmall());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel2 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId1, domId2, domId3, domId4, domId5).asString());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html2("" + roundCornersResource.roundCornersCss().greyTopLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().greyTopClass() + "", "" + roundCornersResource.roundCornersCss().greyTopRightCornerClass() + "", "" + roundCornersResource.roundCornersCss().greySideLeftClass() + "", "" + roundCornersResource.roundCornersCss().greyCenterClass() + "", domId0, "" + roundCornersResource.roundCornersCss().greySideRightClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomLeftCornerClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomClass() + "", "" + roundCornersResource.roundCornersCss().greyBottomRightCornerClass() + "").asString());

    icon.setStyleName("guvnor-LazyStackPanel-row-header-icon");
    f_HorizontalPanel3.add(icon);
    titleLabel.setStyleName("guvnor-cursor");
    f_HorizontalPanel3.add(titleLabel);
    editIcon.setStyleName("guvnor-cursor");
    moveUpIcon.setStyleName("guvnor-cursor");
    moveDownIcon.setStyleName("guvnor-cursor");
    deleteIcon.setStyleName("guvnor-cursor");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel2.getElement());
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    com.google.gwt.user.client.Element domId4Element = com.google.gwt.dom.client.Document.get().getElementById(domId4).cast();
    com.google.gwt.user.client.Element domId5Element = com.google.gwt.dom.client.Document.get().getElementById(domId5).cast();
    attachRecord0.detach();
    f_HTMLPanel2.addAndReplaceElement(f_HorizontalPanel3, domId1Element);
    f_HTMLPanel2.addAndReplaceElement(editIcon, domId2Element);
    f_HTMLPanel2.addAndReplaceElement(moveUpIcon, domId3Element);
    f_HTMLPanel2.addAndReplaceElement(moveDownIcon, domId4Element);
    f_HTMLPanel2.addAndReplaceElement(deleteIcon, domId5Element);
    UiBinderUtil.TempAttachment attachRecord1 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord1.detach();
    f_HTMLPanel1.addAndReplaceElement(f_HTMLPanel2, domId0Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.editIconClick(event);
      }
    };
    editIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.moveUpClick(event);
      }
    };
    moveUpIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.moveDownClick(event);
      }
    };
    moveDownIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.deleteClick(event);
      }
    };
    deleteIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.deleteIcon = deleteIcon;
    owner.editIcon = editIcon;
    owner.icon = icon;
    owner.moveDownIcon = moveDownIcon;
    owner.moveUpIcon = moveUpIcon;
    owner.titleLabel = titleLabel;

    return f_HTMLPanel1;
  }
}
