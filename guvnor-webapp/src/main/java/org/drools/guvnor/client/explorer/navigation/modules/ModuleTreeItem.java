package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;
import org.drools.guvnor.client.rpc.PackageConfigData;

import java.util.ArrayList;
import java.util.List;

public class ModuleTreeItem {

    private final IsTreeItem treeItem;
    private final ModuleTreeItemView view;
    private final ClientFactory clientFactory;
    private final PackageConfigData packageConfigData;

    public ModuleTreeItem( ClientFactory clientFactory,
                           IsTreeItem treeItem,
                           PackageConfigData packageConfigData ) {
        this.clientFactory = clientFactory;
        this.treeItem = treeItem;
        this.packageConfigData = packageConfigData;
        this.view = clientFactory.getNavigationViewFactory().getModuleTreeItemView();
        view.setRootItem( treeItem );
        view.setRootUserObject( new ModuleEditorPlace( packageConfigData.getUuid() ) );
        fillModuleItemStructure();
    }

    public IsTreeItem getRootItem() {
        return treeItem;
    }

    private String getTitle( String format ) {
        return clientFactory.getAssetEditorFactory().getAssetEditorTitle( format );
    }


    private void fillModuleItemStructure() {
        //If two or more asset editors (that are associated with different formats) have same titles,
        //we group them together and display them as one node on the package tree.
        String[] registeredFormats = clientFactory.getAssetEditorFactory().getRegisteredAssetEditorFormats();

        //Use list to preserve the order of asset editors defined in configuration.
        List<FormatList> formatListGroupedByTitles = new ArrayList<FormatList>();
        for (String format : registeredFormats) {
            boolean found = false;
            for (FormatList formatListWithSameTitle : formatListGroupedByTitles) {
                found = formatListWithSameTitle.titleAlreadyExists( format );
                if ( found ) {
                    formatListWithSameTitle.add( format );
                    break;
                }
            }
            if ( !found ) {
                FormatList formatListWithSameTile = new FormatList();
                formatListWithSameTile.add( format );
                formatListGroupedByTitles.add( formatListWithSameTile );
            }
        }

        addTitleItems( formatListGroupedByTitles );
    }

    private void addTitleItems( List<FormatList> formatListGroupedByTitles ) {
        for (FormatList formatList : formatListGroupedByTitles) {
            view.add(
                    clientFactory.getAssetEditorFactory().getAssetEditorIcon( formatList.getFirst() ),
                    getTitle( formatList.getFirst() ),
                    new ModuleFormatsGrid(
                            packageConfigData,
                            getTitle( formatList.getFirst() ),
                            formatList.getFormats() ) );
        }
    }

    class FormatList {

        private List<String> formatList = new ArrayList<String>();

        private boolean titleAlreadyExists( String format ) {
            for (String addedFormat : formatList) {
                //If two formats has same tile, group them together
                if ( hasSameTitle( format, addedFormat ) ) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasSameTitle( String format, String addedFormat ) {
            return getTitle( addedFormat ).equals( getTitle( format ) );
        }


        String[] getFormats() {
            if ( formatList.size() == 1 && "".equals( formatList.get( 0 ) ) ) {
                return new String[0];
            } else {
                return formatList.toArray( new String[formatList.size()] );
            }
        }

        public void add( String format ) {
            formatList.add( format );
        }

        public int size() {
            return formatList.size();
        }

        public String[] toArray( String[] strings ) {
            return formatList.toArray( strings );
        }

        public String getFirst() {
            return formatList.get( 0 );
        }
    }
}
