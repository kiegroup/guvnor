package org.drools.guvnor.client.explorer.navigation.modules;


import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;

public class KnowledgeModulesTree {

    private KnowledgeModulesTreeView view;
    private ClientFactory clientFactory;

    public KnowledgeModulesTree( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;
        this.view = clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeView();
        addRootPanels();
    }

    private void addRootPanels() {
        view.setGlobalAreaTreeItem( new GlobalAreaTreeItem( clientFactory ) );

        view.setKnowledgeModulesTreeItem( new KnowledgeModulesTreeItem( clientFactory ) );

        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_CREATE_NEW_ASSET ) ) {
            view.setNewAssetMenu(
                    new ModulesNewAssetMenu( clientFactory ) );
        }
    }
}
