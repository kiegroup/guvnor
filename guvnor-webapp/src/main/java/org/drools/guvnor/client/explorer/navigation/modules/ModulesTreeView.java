package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ModulesTreeView
    extends
    IsWidget {

    interface Presenter {

        void setFlatView();
        
        void setHierarchyView();
        
        void collapseAll();
        
        void expandAll();

    }

    void setNewAssetMenu(Widget modulesNewAssetMenu);

    void setGlobalAreaTreeItem(GlobalAreaTreeItem globalAreaTreeItem);

    void setModulesTreeItem(ModulesTreeItem modulesTreeItem);

    void setPresenter(Presenter presenter);
    
}
