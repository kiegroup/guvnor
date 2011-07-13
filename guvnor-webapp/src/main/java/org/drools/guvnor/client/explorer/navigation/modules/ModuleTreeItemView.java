package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;

public interface ModuleTreeItemView {

    void setRootItem( IsTreeItem treeItem );

    void add( ImageResource formatIcon, String formatText, ModuleFormatsGrid formats );

    void setRootUserObject( ModuleEditorPlace place );
}
