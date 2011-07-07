package org.drools.guvnor.client.explorer.navigation;


import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;

public class KnowledgesModuleTree {

    private KnowledgeModulesTreeView view;
    private ClientFactory clientFactory;

    public KnowledgesModuleTree( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;
        this.view = clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeView();
        addRootPanels();
    }

    private void addRootPanels() {
        view.setGlobalAreaTreeItem(new GlobalAreaTreeItem(clientFactory.getNavigationViewFactory().getGlobalAreaTreeItem()));

        view.setKnowledgeModulesTreeItem( new KnowledgeModulesTreeItem( clientFactory ) );

        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_CREATE_NEW_ASSET ) ) {
            view.setNewAssetMenu(
                    new ModulesNewAssetMenu(
                            clientFactory.getNavigationViewFactory().getModulesNewAssetMenuView(),
                            clientFactory.getPackageService() ) );
        }
    }
}
