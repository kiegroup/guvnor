package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldEditor_FactFieldsEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor>, org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor.FactFieldsEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='float:left; padding-left:40px;'> <span id='{0}'></span> </div> <div style='float:right;'> <span id='{1}'></span> </div> <div style='clear:both;'></div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor_FactFieldsEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor_FactFieldsEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldEditor_FactFieldsEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label fieldName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label fieldType = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image editFieldIcon = new com.google.gwt.user.client.ui.Image(images.edit());
    com.google.gwt.user.client.ui.Image deleteFieldIcon = new com.google.gwt.user.client.ui.Image(images.deleteItemSmall());
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel4 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1).asString());

    f_HorizontalPanel2.add(fieldName);
    f_Label3.setText(":");
    f_HorizontalPanel2.add(f_Label3);
    f_HorizontalPanel2.add(fieldType);
    editFieldIcon.setStyleName("guvnor-cursor");
    f_HorizontalPanel4.add(editFieldIcon);
    deleteFieldIcon.setStyleName("guvnor-cursor");
    f_HorizontalPanel4.add(deleteFieldIcon);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel2, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel4, domId1Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.editFieldIconClick(event);
      }
    };
    editFieldIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.deleteFieldIconClick(event);
      }
    };
    deleteFieldIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.deleteFieldIcon = deleteFieldIcon;
    owner.editFieldIcon = editFieldIcon;
    owner.fieldName = fieldName;
    owner.fieldType = fieldType;

    return f_HTMLPanel1;
  }
}
