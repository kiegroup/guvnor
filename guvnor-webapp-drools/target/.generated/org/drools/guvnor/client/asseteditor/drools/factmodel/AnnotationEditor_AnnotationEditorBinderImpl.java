package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationEditor_AnnotationEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor>, org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor.AnnotationEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='float:left; padding-left:40px;'> <span id='{0}'></span> </div> <div style='float:right;'> <span id='{1}'></span> </div> <div style='clear:both;'></div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor_AnnotationEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor_AnnotationEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationEditor_AnnotationEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.resources.DroolsGuvnorImages images = (org.drools.guvnor.client.resources.DroolsGuvnorImages) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorImages.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label annotationName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label annotationKey = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Label annotationValue = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image editAnnotationIcon = new com.google.gwt.user.client.ui.Image(images.edit());
    com.google.gwt.user.client.ui.Image deleteAnnotationIcon = new com.google.gwt.user.client.ui.Image(images.deleteItemSmall());
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel5 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1).asString());

    f_HorizontalPanel2.add(annotationName);
    f_Label3.setText(":");
    f_HorizontalPanel2.add(f_Label3);
    f_HorizontalPanel2.add(annotationKey);
    f_Label4.setText("=");
    f_HorizontalPanel2.add(f_Label4);
    f_HorizontalPanel2.add(annotationValue);
    editAnnotationIcon.setStyleName("guvnor-cursor");
    f_HorizontalPanel5.add(editAnnotationIcon);
    deleteAnnotationIcon.setStyleName("guvnor-cursor");
    f_HorizontalPanel5.add(deleteAnnotationIcon);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel2, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel5, domId1Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.editAnnotationIconClick(event);
      }
    };
    editAnnotationIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.deleteAnnotationIconClick(event);
      }
    };
    deleteAnnotationIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    owner.annotationKey = annotationKey;
    owner.annotationName = annotationName;
    owner.annotationValue = annotationValue;
    owner.deleteAnnotationIcon = deleteAnnotationIcon;
    owner.editAnnotationIcon = editAnnotationIcon;

    return f_HTMLPanel1;
  }
}
