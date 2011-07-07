package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.KnowledgeModulesTreeItemView.Presenter;
import org.drools.guvnor.client.rpc.PackageConfigData;

import java.util.HashMap;
import java.util.Map;


public class KnowledgeModulesTreeItem implements IsWidget, Presenter {

    private KnowledgeModulesTreeItemView view;
    private ClientFactory clientFactory;

    private Map<IsTreeItem, String> uuidsByTreeItems = new HashMap<IsTreeItem, String>();

    public KnowledgeModulesTreeItem( ClientFactory clientFactory ) {
        this.view = clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeItemView();
        view.setPresenter( this );
        this.clientFactory = clientFactory;
        fillModulesTree( view.addModulesTreeItem() );
    }

    private void fillModulesTree( final IsTreeItem treeItem ) {
        clientFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
            public void onSuccess( PackageConfigData[] packageConfigDatas ) {
                addModules( packageConfigDatas, treeItem );
            }
        } );
    }

    public void onModuleSelected( IsTreeItem treeItem ) {
        String uuid = uuidsByTreeItems.get( treeItem );
        if ( uuid != null ) {
            clientFactory.getPlaceController().goTo(
                    new ModuleEditorPlace( uuidsByTreeItems.get( treeItem ) )
            );
        }
    }

    private void addModules( PackageConfigData[] packageConfigDatas, IsTreeItem treeItem ) {
        for (PackageConfigData packageConfigData : packageConfigDatas) {
            IsTreeItem isTreeItem = view.addModulesTreeItem( treeItem, packageConfigData.getName() );

            uuidsByTreeItems.put( isTreeItem, packageConfigData.getUuid() );

            if ( packageConfigData.getSubPackages() != null ) {
                addModules(
                        packageConfigData.getSubPackages(),
                        isTreeItem );
            }
        }
    }

    public Widget asWidget() {
        return view.asWidget();
    }
}
