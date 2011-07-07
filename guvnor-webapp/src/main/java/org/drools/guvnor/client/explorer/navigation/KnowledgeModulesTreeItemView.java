package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.IsWidget;

interface KnowledgeModulesTreeItemView extends IsWidget {

    interface Presenter {
        void onModuleSelected( IsTreeItem treeItem );
    }

    void setPresenter( Presenter presenter );

    IsTreeItem addModulesTreeItem();

    IsTreeItem addModulesTreeItem( IsTreeItem parentTreeItem, String moduleName );
}
