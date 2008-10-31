package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.LogViewer;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.StateManager;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Anton Arhipov
 */
public class AdministrationPanel extends GenericPanel {

    public AdministrationPanel(ExplorerViewCenterPanel tabbedPanel) {
        super("Administration", tabbedPanel);
        setIconCls("nav-admin");

        TreePanel adminTree = basicTreeStructure(ExplorerNodeConfig.getAdminStructure(), new TreePanelListenerAdapter() {
            public void onClick(TreeNode self, EventObject e) {

                int id = Integer.parseInt(self.getAttribute("id"));
                switch (id) {
                    case 0:
                        if (!centertabbedPanel.showIfOpen("catman"))
                            centertabbedPanel.addTab("Category Manager", true, new CategoryManager(), "catman");
                        break;
                    case 1:
                        if (!centertabbedPanel.showIfOpen("archman"))
                            centertabbedPanel.addTab("Archived Manager", true, new ArchivedAssetManager(centertabbedPanel), "archman");
                        break;

                    case 2:
                        if (!centertabbedPanel.showIfOpen("stateman"))
                            centertabbedPanel.addTab("State Manager", true, new StateManager(), "stateman");
                        break;
                    case 3:
                        if (!centertabbedPanel.showIfOpen("bakman"))
                            centertabbedPanel.addTab("Import Export", true, new BackupManager(), "bakman");
                        break;

                    case 4:
                        if (!centertabbedPanel.showIfOpen("errorLog"))
                            centertabbedPanel.addTab("Event Log", true, new LogViewer(), "errorLog");
                        break;
                    case 5:
                        if (!centertabbedPanel.showIfOpen("securityPermissions"))
                            centertabbedPanel.addTab("User Permission mappings", true, new PermissionViewer(), "securityPermissions");
                        break;
                }
            }
        });
        adminTree.setRootVisible(false);

        VerticalPanel adminPanel = new VerticalPanel();
        adminPanel.add(adminTree);
        adminPanel.setWidth("100%");
        add(adminPanel);
    }

}
