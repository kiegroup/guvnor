package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.NodeTraversalCallback;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.SplitButtonListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Anton Arhipov
 */
public class DeploymentPanel extends GenericPanel {

    private boolean deploymentPackagesLoaded = false;
    private static Constants constants = ((Constants) GWT.create(Constants.class));

    public DeploymentPanel(ExplorerViewCenterPanel tabbedPanel) {
        super(constants.PackageSnapshots(), tabbedPanel);
        setIconCls("nav-deployment"); //NON-NLS

        final VerticalPanel deploymentPanel = new VerticalPanel();
        Toolbar deployToolbar = new Toolbar();
        final ToolbarMenuButton menuButton = new ToolbarMenuButton(constants.Deploy(), deploymentMenu());
        deployToolbar.addButton( menuButton );
        deploymentPanel.add(deployToolbar);
        deploymentPanel.setWidth("100%");

        menuButton.addListener( new SplitButtonListenerAdapter() {
            public void onClick(Button button,
                                EventObject e) {
                menuButton.showMenu();
            }
        } );

        addListener(new PanelListenerAdapter() {
            public void onExpand(Panel panel) {
                if (!deploymentPackagesLoaded) {
                    deploymentPanel.add(deploymentExplorer());
                    deploymentPackagesLoaded = true;
                }
            }
        });

        add(deploymentPanel);

    }

    private Menu deploymentMenu() {
        Menu m = new Menu();

        Item nds = new Item(constants.NewDeploymentSnapshot(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                SnapshotView.showNewSnapshot();
            }
        });
        nds.setIcon("images/snapshot_small.gif"); //NON-NLS
        m.addItem(nds);

        Item rebuild = new Item(constants.RebuildAllSnapshotBinaries(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                SnapshotView.rebuildBinaries();
            }
        });
        rebuild.setIcon("images/refresh.gif");             //NON-NLS
        m.addItem(rebuild);

        return m;
    }

    private Panel deploymentExplorer() {

        final TreeNode root = new TreeNode(constants.PackageSnapshots());
        root.setIcon("images/silk/chart_organisation.gif"); //NON-NLS
        root.setId("snapshotRoot");                         //NON-NLS

        final TreePanel panel = genericExplorerWidget(root);


        deploymentListPackages(root);

        panel.addListener(new TreePanelListenerAdapter() {

            public void onCollapseNode(TreeNode node) {
                Node[] children = node.getChildNodes();

                for (Node child : children) {
                    node.removeChild(child);
                }

                if (node.getId().equals("snapshotRoot")) {
                    deploymentListPackages(root);
                } else {
                    node.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
                }
            }

            public void onExpandNode(final TreeNode node) {
                if (node.getId().equals("snapshotRoot")) { //NON-NLS
                    return;
                }
                final PackageConfigData conf = (PackageConfigData) node.getUserObject();
                if (conf != null) {
                    RepositoryServiceFactory.getService().listSnapshots(conf.name, new GenericCallback() {
                        public void onSuccess(Object data) {
                            final SnapshotInfo[] snaps = (SnapshotInfo[]) data;
                            for (final SnapshotInfo snapInfo : snaps) {
                                TreeNode snap = new TreeNode();
                                snap.setTooltip(snapInfo.comment);
                                snap.setText(snapInfo.name);
                                snap.setUserObject(new Object[]{snapInfo, conf});
                                node.appendChild(snap);
                            }
                            node.removeChild(node.getFirstChild());
                        }
                    });
                }
            }

            public void onClick(TreeNode node, EventObject e) {

                if (node.getUserObject() instanceof Object[]) {
//                	Node[] children = node.getParentNode().getChildNodes();
//                	for(Node n : children) {
//                		n.remove();
//                	}
                    Object[] o = (Object[]) node.getUserObject();
                    final String snapName = ((SnapshotInfo) o[0]).name;
                    PackageConfigData conf = (PackageConfigData) o[1];
                    RepositoryServiceFactory.getService().listSnapshots(conf.name, new GenericCallback<SnapshotInfo[]>() {
                        public void onSuccess(SnapshotInfo[] a) {
                            for(SnapshotInfo snap : a) {
                            	if (snap.name.equals(snapName)) {
                            		centertabbedPanel.openSnapshot(snap);
                            		return;
                            	}
                            }
                        }
                    });

                }
            }
        });

        return panel;
    }

    private void deploymentListPackages(final TreeNode root) {
        RepositoryServiceFactory.getService().listPackages(
                new GenericCallback() {
                    public void onSuccess(Object data) {
                        PackageConfigData[] value = (PackageConfigData[]) data;
                        PackageHierarchy ph = new PackageHierarchy();

                        for (PackageConfigData val : value) {
                            ph.addPackage(val);
                        }
                        for (PackageHierarchy.Folder hf : ph.root.children) {
                            buildDeploymentTree(root, hf);
                        }

                        root.expand();
                    }
                });
    }

    private void buildDeploymentTree(TreeNode root, PackageHierarchy.Folder fldr) {
        if (fldr.conf != null) {
            TreeNode pkg = new TreeNode(fldr.conf.name);
            pkg.setIcon("images/snapshot_small.gif");
            pkg.setUserObject(fldr.conf);
            pkg.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
            root.appendChild(pkg);
        } else {
            TreeNode tn = new TreeNode();
            tn.setText(fldr.name);
            tn.setIcon("images/empty_package.gif"); //NON-NLS
            root.appendChild(tn);
            for (PackageHierarchy.Folder c : fldr.children) {
                buildDeploymentTree(tn, c);
            }
        }
    }

}
