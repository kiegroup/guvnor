package org.drools.brms.client.explorer;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.LoggedInUserInfo;
import org.drools.brms.client.admin.ArchivedAssetManager;
import org.drools.brms.client.admin.BackupManager;
import org.drools.brms.client.admin.CategoryManager;
import org.drools.brms.client.admin.StateManager;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.packages.NewPackageWizard;
import org.drools.brms.client.packages.SnapshotView;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.rulelist.AssetItemGrid;
import org.drools.brms.client.rulelist.AssetItemGridDataLoader;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.QuickTips;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.TabPanelItem;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelItemListener;
import com.gwtext.client.widgets.event.TabPanelItemListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.ContentPanel;
import com.gwtext.client.widgets.layout.LayoutRegion;
import com.gwtext.client.widgets.layout.LayoutRegionConfig;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.ItemConfig;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.AsyncTreeNode;
import com.gwtext.client.widgets.tree.AsyncTreeNodeConfig;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreeNodeConfig;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreePanelConfig;
import com.gwtext.client.widgets.tree.XMLTreeLoader;
import com.gwtext.client.widgets.tree.XMLTreeLoaderConfig;
import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class ExplorerLayoutManager {

    private Map screens = new HashMap();

    private BorderLayout layout;
    private boolean packagesLoaded = false;
	private boolean deploymentPackagesLoaded = false;

    ExplorerViewCenterPanel centertabbedPanel;

	private VerticalPanel packagesPanel;

    public BorderLayout getBaseLayout() {
        return layout;
    }

    public ExplorerLayoutManager(LoggedInUserInfo uif) {
        Field.setMsgTarget("side");
        QuickTips.init();

        layout = createBorderLayout();
        centertabbedPanel = new ExplorerViewCenterPanel();

        ContentPanel ncp = new ContentPanel("north", "North Title");

        // setup the main / center panel
        ContentPanel centerPanel = new ContentPanel("center-panel");

        centerPanel.add(centertabbedPanel);

        layout.add(LayoutRegionConfig.CENTER, centerPanel);

        DockPanel dock = new DockPanel();
        dock.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        dock.add(new HTML("<div class='headerBarblue'><img src='images/hdrlogo_drools50px.gif' /></div>"),DockPanel.WEST);

        dock.add(uif, DockPanel.EAST);

        dock.setStyleName("headerBarblue");

        dock.setWidth("100%");
        ncp.add(dock);


        layout.add(LayoutRegionConfig.NORTH, ncp);


        // add a navigation tree menu

        final TabPanel tp = new TabPanel("tab-1");
        tp.setWidth("100%");
        tp.setHeight("100%");

        TabPanelItem tpCategory = tp.addTab("tpi1", "Rules", false);
        TabPanelItem tpPackageExplorer = tp.addTab("tpi2", "Packages", false);
        TabPanelItem tpDeployment = tp.addTab("tpi3", "Deployment",
                false);
        TabPanelItem tpAdmin = tp.addTab("tpi4", "Admin", false);

        VerticalPanel rulesPanel = new VerticalPanel();
        packagesPanel = new VerticalPanel();
        final VerticalPanel deploymentPanel = new VerticalPanel();
        VerticalPanel vp4 = new VerticalPanel();


        /** **************************** */
        ContentPanel baseCategory = new ContentPanel("eg-explorer", "BRMS Explorer");
        baseCategory.setWidth(" 100%");

        TreePanel categoryTree = basicTreeStructure(ExplorerNodeConfig
                .getRulesStructure(), new TreePanelListenerAdapter() {

            public void onClick(final TreeNode self, EventObject e) {

                //this refreshes the list.
                if (self.getAttribute("id").equals(
                        ExplorerNodeConfig.CATEGORY_ID)) {
                    self.getParentNode().replaceChild(
                            ExplorerNodeConfig.getCategoriesStructure(), self);
                } else if (self.getAttribute("id").equals(
                        ExplorerNodeConfig.STATES_ID)) {
                    self.getParentNode().replaceChild(
                            ExplorerNodeConfig.getStatesStructure(), self);
                } else if (self.getAttribute("id").equals("FIND")) {
                	centertabbedPanel.openFind();
                } else {

                    final String key = (String) self.getUserObject();
                    final boolean isState = key.startsWith("-");

                    if (!centertabbedPanel.showIfOpen(key)) {
                        AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
                            public void open(String uuid) {
                                centertabbedPanel.openAsset( uuid);
                            }
                        },
                        AssetItemGrid.RULE_LIST_TABLE_ID,
                        new AssetItemGridDataLoader() {
                            public void loadData(int skip, int numberOfRows, GenericCallback cb) {
                            	if (isState) {
                            		RepositoryServiceFactory.getService().loadRuleListForState(key.substring(1) , skip, numberOfRows, cb);
                            	} else {
                            		RepositoryServiceFactory.getService().loadRuleListForCategories(key, skip, numberOfRows, cb);
                            	}
                            }
                        }
                        );

                        centertabbedPanel.addTab(((isState) ?"State: " : "Category: ") + self.getText(), true, list, key);
                    }

                }

            }
        });
        centertabbedPanel.openFind();

        baseCategory.add(categoryTree);

        Toolbar rulesToolBar = new Toolbar(Ext.generateId());
        rulesPanel.add(rulesToolBar);


        rulesToolBar.addButton(new ToolbarMenuButton("Create New", rulesNewMenu()));


        rulesPanel.add(baseCategory);
        rulesPanel.setWidth("100%");



        Toolbar pkgToolbar = new Toolbar(Ext.generateId());
        pkgToolbar.addButton(new ToolbarMenuButton("Create New", packageNewMenu()));

        packagesPanel.add(pkgToolbar);
        packagesPanel.setWidth("100%");

        //deploymentPanel.add(deploymentToolbar());

        /** ****************** */
        ContentPanel cp = new ContentPanel("eg-explorer", "BRMS Explorer");
        cp.setWidth(" 100%");

        TreePanel adminTree = basicTreeStructure(ExplorerNodeConfig
                .getAdminStructure(), new TreePanelListenerAdapter() {
            public void onClick(TreeNode self, EventObject e) {


                int id = Integer.parseInt(self.getAttribute("id"));
                switch (id) {
                case 0:
                    centertabbedPanel.addTab("Category Manager", true, new CategoryManager(), "catman");
                    break;
                case 1:
                    centertabbedPanel.addTab("Archived Manager", true, new ArchivedAssetManager(), "archman");
                    break;

                case 2:
                    centertabbedPanel.addTab("State Manager", true, new StateManager(), "stateman");
                    break;
                case 3:
                    centertabbedPanel.addTab("Backup Manager", true, new BackupManager(), "bakman");
                    break;

                case 4:
                    break;
                }

            }
        });

        cp.add(adminTree);
        vp4.add(cp);

        /** ****************** */

        tpCategory.setContent(rulesPanel);


        tpPackageExplorer.setContent(packagesPanel);
        tpDeployment.setContent(deploymentPanel);


        tpAdmin.setContent(vp4);

        //these panels are lazy loaded to easy startup wait time.
        tpPackageExplorer.addTabPanelItemListener(new TabPanelItemListenerAdapter() {
			public void onActivate(TabPanelItem tab) {
        		if (!packagesLoaded) {
        			packagesPanel.add(packageExplorer(centertabbedPanel));
        			packagesLoaded = true;
        		}
        	}
        });

        tpDeployment.addTabPanelItemListener(new TabPanelItemListenerAdapter() {


			public void onActivate(TabPanelItem tab) {
        		if (!deploymentPackagesLoaded) {
        			deploymentPanel.add(deploymentExplorer(centertabbedPanel));
        			deploymentPackagesLoaded = true;
        		}
        	}
        });


        tp.activate(0);


        ContentPanel tree = new ContentPanel();
        tree.add(tp);
        layout.add(LayoutRegionConfig.WEST, tree);
    }

	private Widget deploymentToolbar() {
		Toolbar tb = new Toolbar(Ext.generateId());
		tb.addButton(new ToolbarButton(new ButtonConfig() {
			{
				setText("New deployment snapshot");
				setButtonListener(new ButtonListenerAdapter() {
					public void onClick(Button button, EventObject e) {

						super.onClick(button, e);
					}
				});
			}
		}));
		// TODO Auto-generated method stub
		return tb;
	}

	private Menu rulesNewMenu() {
		Menu m = new Menu(Ext.generateId());

        m.addItem(new Item("New Business Rule (Guided editor)", new ItemConfig() {
        	{
        		setIcon("images/business_rule.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.BUSINESS_RULE, "New Business Rule (Guided editor)", true);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New DSL Business Rule (text editor)", new ItemConfig() {
        	{
        		setIcon("images/business_rule.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DSL_TEMPLATE_RULE, "New Rule using DSL", true);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New DRL (Technical rule)", new ItemConfig() {
        	{
        		setIcon("images/rule_asset.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DRL, "New DRL", true);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New Decision Table (Spreadsheet)", new ItemConfig() {
        	{
        		setIcon("images/spreadsheet_small.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DECISION_SPREADSHEET_XLS, "New Decision Table (Spreadsheet)", true);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New Test Scenario", new ItemConfig() {
        	{
        		setIcon("images/test_manager.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.TEST_SCENARIO,
                                "Create a test scenario.", false);
        			}
        		});
        	}
        }));



		return m;
	}

	private Menu packageNewMenu() {
		Menu m = new Menu(Ext.generateId());
        m.addItem(new Item("New Package", new ItemConfig() {
        	{
        		setIcon("images/new_package.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				NewPackageWizard wiz = new NewPackageWizard(new Command() {
							public void execute() {
								refreshPackageTree();
							}
        				});
        				wiz.show();
        			}
        		});
        	}
        }));
        m.addItem(new Item("New Rule", new ItemConfig() {
        	{
        		setIcon("images/rule_asset.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(null, "New Rule", true);
        			}
        		});
        	}
        }));
        m.addItem(new Item("New Model (jar) of fact classes", new ItemConfig() {
        	{
        		setIcon("images/model_asset.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.MODEL, "New model archive (jar)", false);
        			}
        		});
        	}
        }));
        m.addItem(new Item("New Function", new ItemConfig() {
        	{
        		setIcon("images/function_assets.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.FUNCTION, "Create a new function", false);
        			}
        		});
        	}
        }));
        m.addItem(new Item("New DSL", new ItemConfig() {
        	{
        		setIcon("images/dsl.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DSL, "Create a new DSL configuration", false);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New RuleFlow", new ItemConfig() {
        	{
        		setIcon("images/ruleflow_small.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.RULE_FLOW_RF, "Create a new RuleFlow", false);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New Enumeration", new ItemConfig() {
        	{
        		setIcon("images/new_enumeration.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.ENUMERATION,
                                "Create a new enumeration (drop down mapping).", false);
        			}
        		});
        	}
        }));

        m.addItem(new Item("New Test Scenario", new ItemConfig() {
        	{
        		setIcon("images/test_manager.gif");
        		setBaseItemListener(new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.TEST_SCENARIO,
                                "Create a test scenario.", false);
        			}
        		});
        	}
        }));



		return m;
	}

    private void launchWizard(String format,
            String title, boolean showCats) {

    	NewAssetWizard pop = new NewAssetWizard( new EditItemEvent() {
                                   public void open(String key) {
                                       centertabbedPanel.openAsset(key);
                                   }
                               },
                               showCats,
                               format,
                               title,
                               null );

    	pop.show();
    }


    private TreePanel basicTreeStructure(TreeNode basenode, TreePanelListenerAdapter listener) {

        TreePanel adminTreePanel = genericExplorerWidget(basenode);

        adminTreePanel.addTreePanelListener(listener);
        return adminTreePanel;
    }

    private BorderLayout createBorderLayout() {

        LayoutRegionConfig north = new LayoutRegionConfig();
        north.setSplit(false);
        north.setInitialSize(50);
        north.setTitlebar(false);
        north.setAutoScroll(false);

        LayoutRegionConfig west = new LayoutRegionConfig();
        west.setSplit(true);
        west.setInitialSize(300);
        west.setMinSize(175);
        west.setMaxSize(400);
        west.setTitlebar(true);
        west.setCollapsible(true);
        west.setAnimate(true);
        west.setCollapsed(false);
        west.setAutoScroll(false);


        LayoutRegionConfig center = new LayoutRegionConfig();
        center.setTitlebar(false);
        center.setAutoScroll(true);
        center.setTabPosition("top");

        return new BorderLayout("100%", "100%", north, null, west, null, center );
    }

    private ContentPanel deploymentExplorer(final ExplorerViewCenterPanel tabPanel) {
        final ContentPanel cp = new ContentPanel(Ext.generateId(), "Deployment Explorer");
        cp.setWidth("100%");

        final TreeNode root = new TreeNode("Package snapshots", new TreeNodeConfig() {
        	{
        		setIcon("images/silk/chart_organisation.gif");
        		setId("snapshotRoot");
        	}
        });

		final TreePanel panel = genericExplorerWidget(root);
        cp.add(panel);

		deploymentListPackages(root);

		panel.addTreePanelListener(new TreePanelListenerAdapter() {

			public void onCollapse(TreeNode node) {
				Node[] children = node.getChildNodes();
				for (int i = 0; i < children.length; i++) {
					node.removeChild(children[i]);
				}
				if (node.getId().equals("snapshotRoot")) {
					deploymentListPackages(root);
				} else {
					node.appendChild(new TreeNode("Please wait..."));
				}
;			}

			public void onExpand(final TreeNode node) {
				if (node.getId().equals("snapshotRoot")) {
					return;
				}
				final PackageConfigData conf = (PackageConfigData) node.getUserObject();
				RepositoryServiceFactory.getService().listSnapshots(conf.name, new GenericCallback() {
					public void onSuccess(Object data) {
						final SnapshotInfo[] snaps = (SnapshotInfo[]) data;
						for (int i = 0; i < snaps.length; i++) {
							final SnapshotInfo snapInfo = snaps[i];
							TreeNode snap = new TreeNode(new TreeNodeConfig() {
								{
									setQtip(snapInfo.comment);
									setText(snapInfo.name);
								}
							});
							snap.setUserObject(new Object[] {snapInfo, conf});

							node.appendChild(snap);

						}
						node.removeChild(node.getFirstChild());


					}
				});

			}


			public void onClick(TreeNode node, EventObject e) {
				if (node.getUserObject() instanceof Object[]) {
					Object[] o = (Object[]) node.getUserObject();
					SnapshotInfo snap = (SnapshotInfo) o[0];
					centertabbedPanel.openSnapshot(snap);
				}
			}
		});

        return cp;
    }

	private void deploymentListPackages(final TreeNode root) {
		RepositoryServiceFactory.getService().listPackages(
                new GenericCallback() {
                    public void onSuccess(Object data) {
                        PackageConfigData value[] = (PackageConfigData[]) data;
                        for (int i = 0; i < value.length; i++) {
                        	TreeNode pkg = new TreeNode(value[i].name, new TreeNodeConfig() {
                        		{
                        			setIcon("images/package.gif");
                        		}
                        	});
                        	pkg.setUserObject(value[i]);
                        	pkg.appendChild(new TreeNode("Please wait..."));
                            root.appendChild(pkg);
                        }
                        root.expand();
                    }
                });
	}


    /**
     * Build the package explorer panel.
     */
    private ContentPanel packageExplorer(final ExplorerViewCenterPanel tabPanel) {

        final ContentPanel cp = new ContentPanel(Ext.generateId(), "Package Explorer");
        cp.setWidth("100%");

        TreeNode root = new TreeNode("Packages");
        root.setAttribute("icon", "images/silk/chart_organisation.gif");


		final TreePanel panel = genericExplorerWidget(root);
        cp.add(panel);
        loadPackages(root);


        TreePanelListener treePanelListener = new TreePanelListenerAdapter() {
            public void onClick(TreeNode node, EventObject e) {
        		if (node.getUserObject() instanceof PackageConfigData) {
        			String uuid = ((PackageConfigData) node.getUserObject()).uuid;
		        			centertabbedPanel.openPackageEditor(uuid, new Command() {
								public void execute() {
									//refresh the package tree.
									refreshPackageTree();
								}
		        			});
	    		} else if (node.getUserObject() instanceof Object[] ){
        			Object[] uo = (Object[]) node.getUserObject();
        			final String[] fmts = (String[]) uo[0];
        			final PackageConfigData pc = (PackageConfigData) node.getParentNode().getUserObject();
        			String key = key(fmts, pc);
        			if (!centertabbedPanel.showIfOpen(key)) {
                        AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
                            public void open(String uuid) {
                                centertabbedPanel.openAsset(uuid);
                            }
                        },
                        AssetItemGrid.RULE_LIST_TABLE_ID,
                        new AssetItemGridDataLoader() {
                            public void loadData(int skip, int numRows, GenericCallback cb) {
                            	RepositoryServiceFactory.getService().listAssets(pc.uuid, fmts, skip, numRows, cb);
                            }
                        }
                        );

        				tabPanel.addTab(uo[1] + " [" + pc.name + "]", true, list, key);
        			}
        		}
            }




            public void onCollapse(final TreeNode node) {
            	if (node.getText().equals("Packages")) {
            		Node[] children = node.getChildNodes();
	            	for (int i = 0; i < children.length; i++) {
						node.removeChild(children[i]);
					}
	            	loadPackages(node);
            	}
            }


        };
        // register listener
        panel.addTreePanelListener(treePanelListener);
        panel.render();

        return cp;
    }

	private void loadPackages(final TreeNode root) {
		RepositoryServiceFactory.getService().listPackages(
                new GenericCallback() {
                    public void onSuccess(Object data) {
                        PackageConfigData value[] = (PackageConfigData[]) data;
                        for (int i = 0; i < value.length; i++) {
                            root.appendChild(loadPackage(root, value[i]));
                        }
                        root.expand();
                    }
                });
	}

	private String key(String[] fmts,
			PackageConfigData userObject) {
		String key = userObject.uuid;
		for (int i = 0; i < fmts.length; i++) {
			key = key + fmts[i];
		}
		return key;
	}

	private TreeNode loadPackage(final TreeNode root,
			PackageConfigData packagedata) {
		TreeNode pn = ExplorerNodeConfig.getPackageItemStructure(packagedata.name, packagedata.uuid);
		pn.setUserObject(packagedata);
		return pn;
	}


    public static TreePanel genericExplorerWidget(final TreeNode childNode) {
        // create and configure the main tree
        final TreePanel menuTree = new TreePanel(Ext.generateId(),
                new TreePanelConfig() {
                    {
                        setAnimate(true);
                        setEnableDD(true);
                        setContainerScroll(true);
                        setRootVisible(true);
                    }
                });

        menuTree.setRootNode(childNode);
        menuTree.render();

        menuTree.expandAll();

        return menuTree;
    }

	private void refreshPackageTree() {
		packagesPanel.remove(1);
		packagesPanel.add(packageExplorer(centertabbedPanel));
	}



}
