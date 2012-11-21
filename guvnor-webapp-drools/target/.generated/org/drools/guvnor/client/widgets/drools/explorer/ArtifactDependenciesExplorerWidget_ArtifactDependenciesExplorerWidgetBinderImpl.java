package org.drools.guvnor.client.widgets.drools.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ArtifactDependenciesExplorerWidget_ArtifactDependenciesExplorerWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget>, org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget.ArtifactDependenciesExplorerWidgetBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget owner) {

    org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget_ArtifactDependenciesExplorerWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget_ArtifactDependenciesExplorerWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.explorer.ArtifactDependenciesExplorerWidget_ArtifactDependenciesExplorerWidgetBinderImpl_GenBundle.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Tree treeArtifacts = (com.google.gwt.user.client.ui.Tree) GWT.create(com.google.gwt.user.client.ui.Tree.class);
    com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel3 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.DecoratorPanel f_DecoratorPanel2 = (com.google.gwt.user.client.ui.DecoratorPanel) GWT.create(com.google.gwt.user.client.ui.DecoratorPanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0).asString());

    f_ScrollPanel3.add(treeArtifacts);
    f_ScrollPanel3.setHeight("400px");
    f_ScrollPanel3.setWidth("500px");
    f_DecoratorPanel2.add(f_ScrollPanel3);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_DecoratorPanel2, domId0Element);


    owner.treeArtifacts = treeArtifacts;

    return f_HTMLPanel1;
  }
}
