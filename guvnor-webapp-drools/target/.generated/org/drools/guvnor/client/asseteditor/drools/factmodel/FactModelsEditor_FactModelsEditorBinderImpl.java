package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactModelsEditor_FactModelsEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor>, org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor.FactModelsEditorBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='margin-bottom:20px;'> <span id='{0}'></span> <span id='{1}'></span> </div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor owner) {

    org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor_FactModelsEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor_FactModelsEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.factmodel.FactModelsEditor_FactModelsEditorBinderImpl_GenBundle.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.AddButton addFactIcon = (org.drools.guvnor.client.util.AddButton) GWT.create(org.drools.guvnor.client.util.AddButton.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    org.drools.guvnor.client.util.LazyStackPanel factModelsPanel = (org.drools.guvnor.client.util.LazyStackPanel) GWT.create(org.drools.guvnor.client.util.LazyStackPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0, domId1).asString());


    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(addFactIcon, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(factModelsPanel, domId1Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addFactClick(event);
      }
    };
    addFactIcon.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.addFactIcon = addFactIcon;
    owner.factModelsPanel = factModelsPanel;

    return f_HTMLPanel1;
  }
}
