package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.packages.NewPackageWizard;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
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
import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Anton Arhipov
 */
public class PackagesPanel extends GenericPanel {

    private VerticalPanel packagesPanel;
    private boolean packagesLoaded = false;
    private static Constants constants = ((Constants) GWT.create(Constants.class));

    public PackagesPanel(ExplorerViewCenterPanel tabbedPanel) {
        super(constants.KnowledgeBases(), tabbedPanel);
        setIconCls("nav-packages"); //NON-NLS

        Toolbar pkgToolbar = new Toolbar();
        final ToolbarMenuButton menuButton = new ToolbarMenuButton(constants.CreateNew(), packageNewMenu());
        pkgToolbar.addButton( menuButton );

        menuButton.addListener( new SplitButtonListenerAdapter() {

            public void onClick(Button button,
                                EventObject e) {
                menuButton.showMenu();
            }
        } );
        
        packagesPanel = new VerticalPanel();
        packagesPanel.setWidth("100%");
        packagesPanel.add(pkgToolbar);

        //these panels are lazy loaded to easy startup wait time.
        addListener(new PanelListenerAdapter() {
        	public void onExpand(Panel panel) {
                loadPackageList();
            }
        });

        add(packagesPanel);

    }

    public void loadPackageList() {
        if (!packagesLoaded) {
            packagesPanel.add(packageExplorer(centertabbedPanel));
            packagesLoaded = true;
        }
    }

    //TODO: move it to separate class
    private Menu packageNewMenu() {
        Menu m = new Menu();
        m.addItem(new Item(constants.NewPackage1(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                NewPackageWizard wiz = new NewPackageWizard(new Command() {
                    public void execute() {
                        refreshPackageTree();
                    }
                });
                wiz.show();
            }
        }, "images/new_package.gif")); //NON-NLS

        m.addItem(new Item(constants.NewRule(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(null, constants.NewRule(), true);
            }
        }, "images/rule_asset.gif"));          //NON-NLS

        m.addItem(new Item(constants.UploadPOJOModelJar(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.MODEL, constants.NewModelArchiveJar(), false);
            }
        }, "images/model_asset.gif"));              //NON-NLS

        m.addItem(new Item(constants.NewDeclarativeModel(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.DRL_MODEL, constants.NewDeclarativeModelUsingGuidedEditor(), false);
            }
        }, "images/model_asset.gif")); //NON-NLS

        if (Preferences.getBooleanPref("flex-bpel-editor")) {
			m.addItem(new Item(constants.NewBPELPackage(),
					new BaseItemListenerAdapter() {
						public void onClick(BaseItem item, EventObject e) {
							launchWizard(AssetFormats.BPEL_PACKAGE, constants
									.CreateANewBPELPackage(), false);
						}
					}, "images/model_asset.gif")); // NON-NLS
		}

        m.addItem(new Item(constants.NewFunction(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.FUNCTION, constants.CreateANewFunction(), false);
            }
        }, "images/function_assets.gif")); //NON-NLS


        m.addItem(new Item(constants.NewDSL(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.DSL, constants.CreateANewDSLConfiguration(), false);
            }
        }, "images/dsl.gif"));   //NON-NLS


        m.addItem(new Item(constants.NewRuleFlow(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.RULE_FLOW_RF, constants.CreateANewRuleFlow(), false);
            }
        }, "images/ruleflow_small.gif")); //NON-NLS
        
        m.addItem(new Item(constants.NewEnumeration(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.ENUMERATION, constants.CreateANewEnumerationDropDownMapping(), false);
            }
        }, "images/new_enumeration.gif")); //NON-NLS

        m.addItem(new Item(constants.NewTestScenario(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard(AssetFormats.TEST_SCENARIO, constants.CreateATestScenario(), false);
            }
        }, "images/test_manager.gif")); //NON-NLS

        m.addItem(new Item(constants.NewFile(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                launchWizard("*", constants.CreateAFile(), false);
            }
        }, "images/new_file.gif")); //NON-NLS


        m.addItem(new Item(constants.RebuildAllPackageBinariesQ(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                if (Window.confirm(constants.RebuildConfirmWarning())) {
                    LoadingPopup.showMessage(constants.RebuildingPackageBinaries());
                    RepositoryServiceFactory.getService().rebuildPackages(new GenericCallback() {
                        public void onSuccess(Object data) {
                            LoadingPopup.close();
                        }
                    });
                }

            }
        }, "images/refresh.gif")); //NON-NLS

        return m;
    }

    private void refreshPackageTree() {
        packagesPanel.remove(1);
        packagesPanel.add(packageExplorer(centertabbedPanel));
    }

    private Widget packageExplorer(final ExplorerViewCenterPanel tabPanel) {
		TreeNode rootNode = new TreeNode(constants.Admin());

		
        TreeNode packageRootNode = new TreeNode(constants.Packages());
        packageRootNode.setAttribute("icon", "images/silk/chart_organisation.gif"); //NON-NLS
        loadPackages(packageRootNode);
        
/*		TreeNode globalRootNode = new TreeNode("Global area");
		globalRootNode.setAttribute("icon", "images/silk/chart_organisation.gif");   //NON-NLS
		globalRootNode.setAttribute("id", "globalarea");*/
		loadGlobal(rootNode);
		
		rootNode.appendChild(packageRootNode);
		//rootNode.appendChild(globalRootNode);
		
        final TreePanel panel = genericExplorerWidget(rootNode);
        panel.setRootVisible(false);

        
        TreePanelListener treePanelListener = new TreePanelListenerAdapter() {
            public void onClick(TreeNode node, EventObject e) {
                if (node.getUserObject() instanceof PackageConfigData && !"global".equals(((PackageConfigData)node.getUserObject()).name)) {
                    PackageConfigData pc = (PackageConfigData) node.getUserObject();
                    RulePackageSelector.currentlySelectedPackage = pc.name;

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
                    RulePackageSelector.currentlySelectedPackage = pc.name;
                    String key = key(fmts, pc);
                    if (!centertabbedPanel.showIfOpen(key)) {

                        final AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
                            public void open(String uuid) {
                                centertabbedPanel.openAsset(uuid);
                            }
                            public void open(String[] uuids) {
                                centertabbedPanel.openAssets( uuids );
                            }
                        },
                                AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID,
                                new AssetItemGridDataLoader() {
                                    public void loadData(int skip, int numRows, GenericCallback cb) {
                                        RepositoryServiceFactory.getService().listAssets(pc.uuid, fmts, skip, numRows, AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID, cb);
                                    }
                                }
                        , GWT.getModuleBaseURL() + "feed/package?name=" + pc.name + "&viewUrl=" + CategoriesPanel.getSelfURL() + "&status=*");
                        tabPanel.addTab(uo[1] + " [" + pc.name + "]", true, list, key);
                        
                        final ServerPushNotification sub = new ServerPushNotification() {
                            public void messageReceived(PushResponse response) {
                                if (response.messageType.equals("packageChange") && response.message.equals(pc.name)) {
                                    list.refreshGrid();
                                }
                            }
                        };
                        PushClient.instance().subscribe(sub);
                        list.addUnloadListener(new Command() {
                            public void execute() {
                                PushClient.instance().unsubscribe(sub);
                            }
                        });
                    }
                }
            }


            @Override
            public void onCollapseNode(final TreeNode node) {
                if (node.getText().equals(constants.Packages())) {
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

        final ScrollPanel scp = wrapScroll(panel);


        return scp;
    }

    public static ScrollPanel wrapScroll(TreePanel panel) {
        final ScrollPanel scp = new ScrollPanel(panel);


        Window.addWindowResizeListener(new WindowResizeListener() {
            public void onWindowResized(int width, int height) {
                scp.setHeight((int) (Window.getClientHeight() / 1.8) + "px"); //NON-NLS
            }
        });
        scp.setHeight((int) (Window.getClientHeight() / 1.8) + "px"); //NON-NLS
        return scp;
    }

    private void loadPackages(final TreeNode root) {
        RepositoryServiceFactory.getService().listPackages(
                new GenericCallback<PackageConfigData[]>() {
                    public void onSuccess(PackageConfigData[] value) {
                        PackageHierarchy ph = new PackageHierarchy();

                        for (PackageConfigData val : value) {
                            ph.addPackage(val);
                        }

                        for (PackageHierarchy.Folder hf : ph.root.children) {
                            buildPkgTree(root, hf);
                        }

                        //root.expand();
                    }
                });
    }

    private void loadGlobal(final TreeNode root) {
        RepositoryServiceFactory.getService().loadGlobalPackage(
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess(PackageConfigData value) {

                                TreeNode globalRootNode = ExplorerNodeConfig.getPackageItemStructure("Global Area", value.uuid);
                                globalRootNode.setUserObject(value);
                                
                                globalRootNode.setAttribute("icon", "images/silk/chart_organisation.gif");   //NON-NLS
                                globalRootNode.setAttribute("id", "globalarea");
                        		
                                root.appendChild(globalRootNode);

                    }
                });
    }
    
    private void buildPkgTree(TreeNode root, PackageHierarchy.Folder fldr) {
        if (fldr.conf != null) {
            root.appendChild(loadPackage(fldr.name, fldr.conf));
        } else {
            TreeNode tn = new TreeNode();
            tn.setText(fldr.name);
            tn.setIcon("images/empty_package.gif"); //NON-NLS
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
        if (fmts.length == 0) {
            key = key + "[0]";
        }
        return key;
    }
}
