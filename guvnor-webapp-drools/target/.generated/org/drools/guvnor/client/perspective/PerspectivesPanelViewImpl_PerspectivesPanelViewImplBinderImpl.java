package org.drools.guvnor.client.perspective;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PerspectivesPanelViewImpl_PerspectivesPanelViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl>, org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl.PerspectivesPanelViewImplBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("[" + "{0}" + "]")
    SafeHtml html1(String arg0);
     
    @Template("<div class='{0}'> <img src='images/header_logo.gif'> </div> <div class='{1}'> <div class='{2}'> <small> " + "{3}" + " <span id='{4}'></span> <span id='{5}'></span> </small>  </div>    </div> <div style='clear:both;'></div>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl owner) {

    org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl_PerspectivesPanelViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl_PerspectivesPanelViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl_PerspectivesPanelViewImplBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.ConstantsCore i18n = (org.drools.guvnor.client.messages.ConstantsCore) GWT.create(org.drools.guvnor.client.messages.ConstantsCore.class);
    org.drools.guvnor.client.resources.GuvnorResources guvnorResources = (org.drools.guvnor.client.resources.GuvnorResources) GWT.create(org.drools.guvnor.client.resources.GuvnorResources.class);
    org.drools.guvnor.client.resources.ImagesCore images = (org.drools.guvnor.client.resources.ImagesCore) GWT.create(org.drools.guvnor.client.resources.ImagesCore.class);
    org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl.TitlePanelHeight titlePanelHeight = (org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl.TitlePanelHeight) GWT.create(org.drools.guvnor.client.perspective.PerspectivesPanelViewImpl.TitlePanelHeight.class);
    com.google.gwt.dom.client.SpanElement userName = null;
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Anchor logoutAnchor = (com.google.gwt.user.client.ui.Anchor) GWT.create(com.google.gwt.user.client.ui.Anchor.class);
    com.google.gwt.user.client.ui.HTMLPanel titlePanel = new com.google.gwt.user.client.ui.HTMLPanel(template.html2("" + guvnorResources.headerCss().logoClass() + "", "" + guvnorResources.headerCss().controlsClass() + "", "" + guvnorResources.headerCss().userInfoClass() + "", "" + i18n.WelcomeUser() + "", domId0, domId1).asString());
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel2 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    org.drools.guvnor.client.explorer.navigation.NavigationPanel navigationPanel = owner.navigationPanel;
    org.drools.guvnor.client.explorer.ExplorerViewCenterPanel explorerCenterPanel = owner.explorerCenterPanel;
    com.google.gwt.user.client.ui.SplitLayoutPanel f_SplitLayoutPanel3 = (com.google.gwt.user.client.ui.SplitLayoutPanel) GWT.create(com.google.gwt.user.client.ui.SplitLayoutPanel.class);
    com.google.gwt.user.client.ui.DockLayoutPanel f_DockLayoutPanel1 = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.EM);

    logoutAnchor.setHTML(template.html1("" + i18n.SignOut() + "").asString());
    logoutAnchor.setHref("javascript:;");
    titlePanel.setStyleName("" + guvnorResources.headerCss().mainClass() + "");
    f_VerticalPanel2.add(titlePanel);
    f_VerticalPanel2.setWidth("100%");
    f_DockLayoutPanel1.addNorth(f_VerticalPanel2, (double)titlePanelHeight.getHeight());
    f_SplitLayoutPanel3.addWest(navigationPanel, 250);
    f_SplitLayoutPanel3.add(explorerCenterPanel);
    f_DockLayoutPanel1.add(f_SplitLayoutPanel3);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(titlePanel.getElement());
    userName = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    userName.removeAttribute("id");
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    attachRecord0.detach();
    titlePanel.addAndReplaceElement(logoutAnchor, domId1Element);


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.logout(event);
      }
    };
    logoutAnchor.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.logoutAnchor = logoutAnchor;
    owner.titlePanel = titlePanel;
    owner.userName = userName;

    return f_DockLayoutPanel1;
  }
}
