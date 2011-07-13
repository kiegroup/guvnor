package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.IsWidget;

public interface ModulesTreeItemBaseView extends IsWidget {

    interface Presenter {
        void onModuleSelected( Object userObject );
    }

    void setPresenter( Presenter presenter );

    IsTreeItem addModulesTreeItem();

    IsTreeItem addModuleTreeItem( IsTreeItem parentTreeItem, String moduleName );
}
