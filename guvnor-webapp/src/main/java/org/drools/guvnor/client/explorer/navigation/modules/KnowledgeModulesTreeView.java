package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsWidget;

public interface KnowledgeModulesTreeView
    extends
    IsWidget {

    interface Presenter {

        void setFlatView();
        
        void setHierarchyView();
        
        void collapseAll();
        
        void expandAll();

    }

    void setNewAssetMenu(ModulesNewAssetMenu modulesNewAssetMenu);

    void setGlobalAreaTreeItem(GlobalAreaTreeItem globalAreaTreeItem);

    void setKnowledgeModulesTreeItem(KnowledgeModulesTreeItem knowledgeModulesTreeItem);

    void setPresenter(Presenter presenter);
    
}
