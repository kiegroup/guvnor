package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.PackageConfigData;

public class GlobalAreaTreeItem extends ModulesTreeItemBase {

    public GlobalAreaTreeItem( ClientFactory clientFactory ) {
        super(
                clientFactory,
                clientFactory.getNavigationViewFactory().getGlobalAreaTreeItemView() );
    }

    @Override
    protected void fillModulesTree( final IsTreeItem treeItem ) {
        clientFactory.getPackageService().loadGlobalPackage( new GenericCallback<PackageConfigData>() {
            public void onSuccess( PackageConfigData packageConfigData ) {
                new ModuleTreeItem(
                        clientFactory,
                        treeItem,
                        packageConfigData
                );
            }
        } );
    }
}
