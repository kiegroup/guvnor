package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsWidget;

public interface KnowledgeModulesTreeView extends IsWidget {

    void setNewAssetMenu( ModulesNewAssetMenu modulesNewAssetMenu );

    void setGlobalAreaTreeItem( GlobalAreaTreeItem globalAreaTreeItem );

    void setKnowledgeModulesTreeItem( KnowledgeModulesTreeItem knowledgeModulesTreeItem );
}
