package org.drools.guvnor.client.explorer;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.EditItemEvent;

/**
 * @author Anton Arhipov
 */
public abstract class GenericPanel extends Panel {

    protected ExplorerViewCenterPanel centertabbedPanel;

    protected GenericPanel(String title, ExplorerViewCenterPanel centertabbedPanel) {
        super(title);
        this.centertabbedPanel = centertabbedPanel;
    }

    protected TreePanel basicTreeStructure(TreeNode basenode, TreePanelListenerAdapter listener) {
        TreePanel adminTreePanel = genericExplorerWidget(basenode);
        adminTreePanel.addListener(listener);
        return adminTreePanel;
    }

    public static TreePanel genericExplorerWidget(final TreeNode childNode) {
        // create and configure the main tree
        final TreePanel menuTree = new TreePanel();
        menuTree.setAnimate(true);
        menuTree.setEnableDD(true);
        menuTree.setContainerScroll(true);
        menuTree.setRootVisible(true);
        menuTree.setBodyBorder(false);
        menuTree.setBorder(false);
        menuTree.setRootNode(childNode);
        return menuTree;
    }

    protected void launchWizard(String format, String title, boolean showCats, String currentlySelectedPackage) {

        NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
            public void open(String key) {
                centertabbedPanel.openAsset(key);
            }
        }, showCats, format, title, currentlySelectedPackage);

        pop.show();
    }


    public void launchWizard(String format, String title, boolean showCats) {
        launchWizard(format, title, showCats, null);
    }

}
