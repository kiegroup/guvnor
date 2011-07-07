package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsWidget;

interface KnowledgeModulesTreeView extends IsWidget {

    void setNewAssetMenu( ModulesNewAssetMenu modulesNewAssetMenu );

    void setGlobalAreaTreeItem( GlobalAreaTreeItem globalAreaTreeItem );

    void setKnowledgeModulesTreeItem( KnowledgeModulesTreeItem knowledgeModulesTreeItem );
}
