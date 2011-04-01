package org.drools.guvnor.client.explorer.navigation;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.util.TabOpenerImpl;

public abstract class NavigationItemBuilderOld extends Composite
        implements
        SelectionHandler<TreeItem>, NavigationItem {

    protected final Tree mainTree;

    protected Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    public NavigationItemBuilderOld() {

        mainTree = createTree();
        mainTree.setStyleName("guvnor-Tree");

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.EM);

        MenuBar menu = createMenu();
        if (menu != null) {
            dockLayoutPanel.addNorth(menu, 2);
        }

        dockLayoutPanel.add(new ScrollPanel(mainTree));

        /*

        TODO: wrap with DockLayoutPanel if has a menu, if not, just wrap with a ScrollPanel -Rikkola-
        TODO: check the permissions to show menus in the AbstractTree implementations -Rikkola-
        TODO: check the permissions to show abstract tree implementations in NavigationPanel, but keep the logic for this in the AT impl -Rikkola-
        TODO: This should be an interface -Rikkola-
        TODO: Deprecate TabOpener and create places -Rikkola-


         */


        initWidget(dockLayoutPanel);
    }

    public abstract Tree createTree();

    protected void launchWizard(String format,
                                String title,
                                boolean showCats) {
        final TabManager tabManager = TabContainer.getInstance();

        NewAssetWizard pop = new NewAssetWizard(new OpenItemCommand() {
            public void open(String key) {
                tabManager.openAsset(key);
            }

            public void open(MultiViewRow[] rows) {
                for (MultiViewRow row : rows) {
                    tabManager.openAsset(row.uuid);
                }
            }
        },
                showCats,
                format,
                title);

        pop.show();
    }

}
