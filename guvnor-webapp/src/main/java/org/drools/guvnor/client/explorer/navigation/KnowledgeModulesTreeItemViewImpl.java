package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

public class KnowledgeModulesTreeItemViewImpl implements KnowledgeModulesTreeItemView {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private final Tree tree = new Tree();
    private Presenter presenter;

    public KnowledgeModulesTreeItemViewImpl() {
        tree.setStyleName( "guvnor-Tree" );
        tree.setAnimationEnabled( true );

        addSelectionHandler();
    }

    private void addSelectionHandler() {
        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection( SelectionEvent<TreeItem> treeItemSelectionEvent ) {
                presenter.onModuleSelected( treeItemSelectionEvent.getSelectedItem() );
            }
        } );
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    public IsTreeItem addModulesTreeItem() {
        return tree.addItem(
                Util.getHeader(
                        images.chartOrganisation(),
                        constants.Packages() ) );
    }

    public IsTreeItem addModulesTreeItem( IsTreeItem parentTreeItem, String moduleName ) {
        return parentTreeItem.asTreeItem().addItem(
                Util.getHeader(
                        images.packages(),
                        moduleName ) );
    }

    public Widget asWidget() {
        return tree;
    }
}
