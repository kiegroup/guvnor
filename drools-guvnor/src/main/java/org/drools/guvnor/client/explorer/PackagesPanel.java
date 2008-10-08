package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.explorer.PackageHierarchy;
import org.drools.guvnor.client.packages.NewPackageWizard;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;

/**
 * @author Anton Arhipov
 */
public class PackagesPanel extends GenericPanel {

    private VerticalPanel packagesPanel;
    protected String currentPackage;
    private boolean packagesLoaded = false;

    public PackagesPanel(ExplorerViewCenterPanel tabbedPanel) {
        super("Packages", tabbedPanel);
        setIconCls("nav-packages");

        Toolbar pkgToolbar = new Toolbar();
        pkgToolbar.addButton(new ToolbarMenuButton("Create New", packageNewMenu()));

        packagesPanel = new VerticalPanel();
        packagesPanel.setWidth("100%");
        packagesPanel.add(pkgToolbar);

        //these panels are lazy loaded to easy startup wait time.
        addListener(new PanelListenerAdapter() {
        	public void onExpand(Panel panel) {
        		if (!packagesLoaded) {
        			packagesPanel.add(packageExplorer(centertabbedPanel));
        			packagesLoaded = true;
        		}
        	}
        });

        add(packagesPanel);
    }

    //TODO: move it to separate class
    private Menu packageNewMenu() {
        Menu m = new Menu();
        m.addItem(new Item("New Package", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                NewPackageWizard wiz = new NewPackageWizard(new Command() {
                    public void execute() {
                        refreshPackageTree();
                    }
                });
                wiz.show();
            }
        }, "images/new_package.gif"));

        m.addItem(new Item("New Rule", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(null, "New Rule", true, currentPackage);
            }
        }, "images/rule_asset.gif"));

        m.addItem(new Item("Upload new Model jar (fact classes)", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.MODEL, "New model archive (jar)", false, currentPackage);
            }
        }, "images/model_asset.gif"));

        m.addItem(new Item("New Model (in rules)", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.DRL_MODEL, "New declarative model (using guided editor).", false, currentPackage);
            }
        }, "images/model_asset.gif"));

        m.addItem(new Item("New Function", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.FUNCTION, "Create a new function", false, currentPackage);
            }
        }, "images/function_assets.gif"));


        m.addItem(new Item("New DSL", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.DSL, "Create a new DSL configuration", false, currentPackage);
            }
        }, "images/dsl.gif"));


        m.addItem(new Item("New RuleFlow", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.RULE_FLOW_RF, "Create a new RuleFlow", false, currentPackage);
            }
        }, "images/ruleflow_small.gif"));

        m.addItem(new Item("New Enumeration", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.ENUMERATION, "Create a new enumeration (drop down mapping).", false, currentPackage);
            }
        }, "images/new_enumeration.gif"));

        m.addItem(new Item("New Test Scenario", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.TEST_SCENARIO, "Create a test scenario.", false, currentPackage);
            }
        }, "images/test_manager.gif"));

        m.addItem(new Item("New File", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard("*", "Create a file.", false, currentPackage);
            }
        }, "images/new_file.gif"));


        m.addItem(new Item("Rebuild all package binaries", new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                if (Window.confirm("You should only run this if Drools has been upgraded recently " +
                        "(and you have been experiencing errors)." +
                        "This may take some time - are you sure you want to do this? ")) {
                    LoadingPopup.showMessage("Rebuilding package binaries...");
                    RepositoryServiceFactory.getService().rebuildPackages(new GenericCallback() {
                        public void onSuccess(Object data) {
                            LoadingPopup.close();
                        }
                    });
                }

            }
        }, "images/refresh.gif"));

        return m;
    }

    private void refreshPackageTree() {
        packagesPanel.remove(1);
        packagesPanel.add(packageExplorer(centertabbedPanel));
    }

    private Widget packageExplorer(final ExplorerViewCenterPanel tabPanel) {
        TreeNode root = new TreeNode("Packages");
        root.setAttribute("icon", "images/silk/chart_organisation.gif");

        final TreePanel panel = genericExplorerWidget(root);


        loadPackages(root);

        TreePanelListener treePanelListener = new TreePanelListenerAdapter() {
            public void onClick(TreeNode node, EventObject e) {
                if (node.getUserObject() instanceof PackageConfigData) {
                    PackageConfigData pc = (PackageConfigData) node.getUserObject();
                    currentPackage = pc.name;
                    String uuid = pc.uuid;
                    centertabbedPanel.openPackageEditor(uuid, new Command() {
                        public void execute() {
                            //refresh the package tree.
                            refreshPackageTree();
                        }
                    });
                } else if (node.getUserObject() instanceof Object[]) {
                    Object[] uo = (Object[]) node.getUserObject();
                    final String[] fmts = (String[]) uo[0];
                    final PackageConfigData pc = (PackageConfigData) node.getParentNode().getUserObject();
                    currentPackage = pc.name;
                    String key = key(fmts, pc);
                    if (!centertabbedPanel.showIfOpen(key)) {
                        AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
                            public void open(String uuid) {
                                centertabbedPanel.openAsset(uuid);
                            }
                        },
                                AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID,
                                new AssetItemGridDataLoader() {
                                    public void loadData(int skip, int numRows, GenericCallback cb) {
                                        RepositoryServiceFactory.getService().listAssets(pc.uuid, fmts, skip, numRows, AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID, cb);
                                    }
                                }
                        );

                        tabPanel.addTab(uo[1] + " [" + pc.name + "]", true, list, key);
                    }
                }
            }


            @Override
            public void onCollapseNode(final TreeNode node) {
                if (node.getText().equals("Packages")) {
                    Node[] children = node.getChildNodes();
                    for (Node child : children) {
                        node.removeChild(child);
                    }
                    loadPackages(node);
                }
            }


        };
        // register listener
        panel.addListener(treePanelListener);

        ScrollPanel scp = new ScrollPanel(panel);
        scp.setHeight("300px");


        return scp;
    }

    private void loadPackages(final TreeNode root) {
        RepositoryServiceFactory.getService().listPackages(
                new GenericCallback() {
                    public void onSuccess(Object data) {
                        PackageConfigData[] value = (PackageConfigData[]) data;
                        PackageHierarchy ph = new PackageHierarchy();

                        for (PackageConfigData val : value) {
                            ph.addPackage(val);
                        }

                        for (PackageHierarchy.Folder hf : ph.root.children) {
                            buildPkgTree(root, hf);
                        }

                        root.expand();
                    }
                });
    }

    private void buildPkgTree(TreeNode root, PackageHierarchy.Folder fldr) {
        if (fldr.conf != null) {
            root.appendChild(loadPackage(fldr.name, fldr.conf));
        } else {
            TreeNode tn = new TreeNode();
            tn.setText(fldr.name);
            tn.setIcon("images/empty_package.gif");
            root.appendChild(tn);
            for (PackageHierarchy.Folder c : fldr.children) {
                buildPkgTree(tn, c);
            }
        }
    }

    private TreeNode loadPackage(String name, PackageConfigData conf) {
        TreeNode pn = ExplorerNodeConfig.getPackageItemStructure(name, conf.uuid);
        pn.setUserObject(conf);
        return pn;
    }

    public static String key(String[] fmts, PackageConfigData userObject) {
        String key = userObject.uuid;
        for (String fmt : fmts) {
            key = key + fmt;
        }
        return key;
    }
}
