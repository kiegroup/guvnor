package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ModulesTreeViewImpl_ModulesTreeViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl>, org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl.ModulesTreeViewImplBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html2(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html3(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html4(String arg0);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl owner) {

    org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl_ModulesTreeViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl_ModulesTreeViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeViewImpl_ModulesTreeViewImplBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.ConstantsCore i18n = (org.drools.guvnor.client.messages.ConstantsCore) GWT.create(org.drools.guvnor.client.messages.ConstantsCore.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    com.google.gwt.user.client.ui.SimplePanel menuContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.SimplePanel modulesTreeContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.SimplePanel globalModulesTreeContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel3 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel2 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image imgExpandAll = new com.google.gwt.user.client.ui.Image(images.expandAll());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel6 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0).asString());
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image imgCollapseAll = new com.google.gwt.user.client.ui.Image(images.collapseAll());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel7 = new com.google.gwt.user.client.ui.HTMLPanel(template.html2(domId1).asString());
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image imgFlatView = new com.google.gwt.user.client.ui.Image(images.flatView());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel8 = new com.google.gwt.user.client.ui.HTMLPanel(template.html3(domId2).asString());
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Image imgHierarchicalView = new com.google.gwt.user.client.ui.Image(images.hierarchicalView());
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel9 = new com.google.gwt.user.client.ui.HTMLPanel(template.html4(domId3).asString());
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel5 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel4 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.DockLayoutPanel f_DockLayoutPanel1 = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PX);

    f_DockLayoutPanel1.addNorth(menuContainer, 25);
    f_VerticalPanel3.add(modulesTreeContainer);
    f_VerticalPanel3.add(globalModulesTreeContainer);
    f_ScrollPanel2.add(f_VerticalPanel3);
    imgExpandAll.setTitle("" + i18n.ExpandAll() + "");
    f_HTMLPanel6.setStyleName("icon-bar-item");
    f_HorizontalPanel5.add(f_HTMLPanel6);
    imgCollapseAll.setTitle("" + i18n.CollapseAll() + "");
    f_HTMLPanel7.setStyleName("icon-bar-item");
    f_HorizontalPanel5.add(f_HTMLPanel7);
    imgFlatView.setTitle("" + i18n.FlatView() + "");
    f_HTMLPanel8.setStyleName("icon-bar-item");
    f_HorizontalPanel5.add(f_HTMLPanel8);
    imgHierarchicalView.setTitle("" + i18n.HierarchicalView() + "");
    f_HTMLPanel9.setStyleName("icon-bar-item");
    f_HorizontalPanel5.add(f_HTMLPanel9);
    f_HorizontalPanel4.add(f_HorizontalPanel5);
    f_HorizontalPanel4.setStyleName("icon-bar");
    f_DockLayoutPanel1.addSouth(f_HorizontalPanel4, 28);
    f_DockLayoutPanel1.add(f_ScrollPanel2);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel6.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord0.detach();
    f_HTMLPanel6.addAndReplaceElement(imgExpandAll, domId0Element);
    UiBinderUtil.TempAttachment attachRecord1 = UiBinderUtil.attachToDom(f_HTMLPanel7.getElement());
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord1.detach();
    f_HTMLPanel7.addAndReplaceElement(imgCollapseAll, domId1Element);
    UiBinderUtil.TempAttachment attachRecord2 = UiBinderUtil.attachToDom(f_HTMLPanel8.getElement());
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    attachRecord2.detach();
    f_HTMLPanel8.addAndReplaceElement(imgFlatView, domId2Element);
    UiBinderUtil.TempAttachment attachRecord3 = UiBinderUtil.attachToDom(f_HTMLPanel9.getElement());
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    attachRecord3.detach();
    f_HTMLPanel9.addAndReplaceElement(imgHierarchicalView, domId3Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.doOnClickHierarchyView(event);
      }
    };
    imgHierarchicalView.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.doOnClickFlatView(event);
      }
    };
    imgFlatView.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.doOnClickCollapseAll(event);
      }
    };
    imgCollapseAll.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.doOnClickExpandAll(event);
      }
    };
    imgExpandAll.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.globalModulesTreeContainer = globalModulesTreeContainer;
    owner.imgCollapseAll = imgCollapseAll;
    owner.imgExpandAll = imgExpandAll;
    owner.imgFlatView = imgFlatView;
    owner.imgHierarchicalView = imgHierarchicalView;
    owner.menuContainer = menuContainer;
    owner.modulesTreeContainer = modulesTreeContainer;

    return f_DockLayoutPanel1;
  }
}
