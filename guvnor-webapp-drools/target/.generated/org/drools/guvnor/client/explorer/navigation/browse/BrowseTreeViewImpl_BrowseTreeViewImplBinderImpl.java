package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class BrowseTreeViewImpl_BrowseTreeViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl>, org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl.BrowseTreeViewImplBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl owner) {

    org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl_BrowseTreeViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl_BrowseTreeViewImplBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl_BrowseTreeViewImplBinderImpl_GenBundle.class);
    com.google.gwt.user.client.ui.Tree tree = (com.google.gwt.user.client.ui.Tree) GWT.create(com.google.gwt.user.client.ui.Tree.class);
    com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel1 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.DockLayoutPanel layout = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PX);

    tree.setStyleName("guvnor-Tree");
    tree.setAnimationEnabled(true);
    f_ScrollPanel1.add(tree);
    layout.add(f_ScrollPanel1);



    owner.layout = layout;
    owner.tree = tree;

    return layout;
  }
}
