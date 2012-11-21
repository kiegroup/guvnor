package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldsEditor_FactFieldsEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor>, org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor.FactFieldsEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='guvnor-modelview-button-bar'> <div style='float:right;padding-right:20px;'> <span id='{0}'></span> </div> <div style='clear:both;'> </div> </div> <div style='padding-left:40px;'> <span id='{1}'></span> </div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor_FactFieldsEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor_FactFieldsEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.factmodel.FactFieldsEditor_FactFieldsEditorBinderImpl_GenBundle.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.AddButton addFieldIcon = (org.drools.guvnor.client.util.AddButton) GWT.create(org.drools.guvnor.client.util.AddButton.class);
    org.drools.guvnor.client.util.AddButton addAnnotationIcon = (org.drools.guvnor.client.util.AddButton) GWT.create(org.drools.guvnor.client.util.AddButton.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.VerticalPanel fieldsPanel = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1).asString());

    f_HorizontalPanel2.add(addFieldIcon);
    f_HorizontalPanel2.add(addAnnotationIcon);
    f_HorizontalPanel2.setSpacing(5);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel2, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(fieldsPanel, domId1Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addNewFieldClick(event);
      }
    };
    addFieldIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addNewAnnotationClick(event);
      }
    };
    addAnnotationIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.addAnnotationIcon = addAnnotationIcon;
    owner.addFieldIcon = addFieldIcon;
    owner.fieldsPanel = fieldsPanel;

    return f_HTMLPanel1;
  }
}
