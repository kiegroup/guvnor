package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.packages.RefreshModuleListEvent;
import org.drools.guvnor.client.packages.RefreshModuleListEventHandler;
import org.drools.guvnor.client.rpc.PackageConfigData;


public class KnowledgeModulesTreeItem extends ModulesTreeItemBase {

    public KnowledgeModulesTreeItem( ClientFactory clientFactory ) {
        super(
                clientFactory,
                clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeItemView()
        );

        setRefreshHandler( clientFactory );
    }

    private void setRefreshHandler( ClientFactory clientFactory ) {
        clientFactory.getEventBus().addHandler(
                RefreshModuleListEvent.TYPE,
                new RefreshModuleListEventHandler() {
                    public void onRefreshList( RefreshModuleListEvent refreshModuleListEvent ) {
                        getView().clearModulesTreeItem();
                        setUpRootItem();
                    }
                } );
    }

    @Override
    protected void fillModulesTree( final IsTreeItem treeItem ) {
        clientFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
            public void onSuccess( PackageConfigData[] packageConfigDatas ) {
                addModules( packageConfigDatas, treeItem );
            }
        } );
    }

    private KnowledgeModulesTreeItemView getView() {
        return (KnowledgeModulesTreeItemView) view;
    }

}
