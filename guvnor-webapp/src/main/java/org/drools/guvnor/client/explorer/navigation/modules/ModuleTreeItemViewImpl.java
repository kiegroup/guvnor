package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;
import org.drools.guvnor.client.util.Util;

public class ModuleTreeItemViewImpl implements ModuleTreeItemView {

    private TreeItem root;

    public void setRootItem( IsTreeItem treeItem ) {
        root = treeItem.asTreeItem();
    }

    public void add( ImageResource formatIcon, String formatText, ModuleFormatsGrid formats ) {
        TreeItem treeItem = new TreeItem( Util.getHeader(
                formatIcon,
                formatText ) );
        treeItem.setUserObject( formats );
        root.addItem( treeItem );
    }

    public void setRootUserObject( ModuleEditorPlace place ) {
        root.setUserObject( place );
    }
}
