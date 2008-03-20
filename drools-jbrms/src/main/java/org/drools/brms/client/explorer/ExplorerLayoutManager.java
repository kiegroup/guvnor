package org.drools.brms.client.explorer;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.LoggedInUserInfo;
import org.drools.brms.client.admin.ArchivedAssetManager;
import org.drools.brms.client.admin.BackupManager;
import org.drools.brms.client.admin.CategoryManager;
import org.drools.brms.client.admin.LogViewer;
import org.drools.brms.client.admin.StateManager;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.packages.NewPackageWizard;
import org.drools.brms.client.packages.SnapshotView;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.rulelist.AssetItemGrid;
import org.drools.brms.client.rulelist.AssetItemGridDataLoader;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.QuickTips;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.PanelListener;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class ExplorerLayoutManager {

    private Map screens = new HashMap();

    private boolean packagesLoaded = false;
	private boolean deploymentPackagesLoaded = false;

    ExplorerViewCenterPanel centertabbedPanel;

	private VerticalPanel packagesPanel;

	protected String currentPackage;

	private Panel northPanel;

	private Panel accordion;

    public Panel getBaseLayout() {
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMargins(0, 0, 0, 0);

        BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
        northLayoutData.setMargins(0, 0, 0, 0);

        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        centerLayoutData.setMargins(new Margins(5, 0, 5, 5));

        Panel centerPanelWrappper = new Panel();
        centerPanelWrappper.setLayout(new FitLayout());
        centerPanelWrappper.setBorder(false);
        centerPanelWrappper.setBodyBorder(false);



        //setup the west regions layout properties
        BorderLayoutData westLayoutData = new BorderLayoutData(RegionPosition.WEST);
        westLayoutData.setMargins(new Margins(5, 5, 0, 5));
        westLayoutData.setCMargins(new Margins(5, 5, 5, 5));
        westLayoutData.setMinSize(155);
        westLayoutData.setMaxSize(350);
        westLayoutData.setSplit(true);

        //create the west panel and add it to the main panel applying the west region layout properties
        Panel westPanel = new Panel();
        westPanel.setId("side-nav");
        westPanel.setTitle("Navigate BRMS");
        //westPanel.setAutoScroll(true);
        westPanel.setLayout(new FitLayout());
        westPanel.setWidth(210);
        westPanel.setCollapsible(true);;//MN createWestPanel();
        westPanel.add(accordion);
        mainPanel.add(westPanel, westLayoutData);



        centerPanelWrappper.add(centertabbedPanel.getPanel());

        mainPanel.add(centerPanelWrappper, centerLayoutData);



        mainPanel.add(northPanel, northLayoutData);


        return mainPanel;

    }

    public ExplorerLayoutManager(LoggedInUserInfo uif) {
        Field.setMsgTarget("side");
        QuickTips.init();

        centertabbedPanel = new ExplorerViewCenterPanel();

        //north
        northPanel = new Panel();
        DockPanel dock = new DockPanel();
        dock.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        dock.add(new HTML("<div class='headerBarBlue'><img src='images/hdrlogo_drools50px.gif' /></div>"),DockPanel.WEST);
        dock.add(uif, DockPanel.EAST);
        dock.setStyleName("headerBarblue");
        dock.setWidth("100%");




        northPanel.add(dock);
        northPanel.setHeight(50);


        // add a navigation for the west area
        accordion = new Panel();
        accordion.setLayout(new AccordionLayout(true));
        //accordion.setId("side-nav");
        //accordion.setTitle("Showcase Explorer");
        //accordion.setLayout(new FitLayout());
        //accordion.setWidth(210);
        //accordion.setCollapsible(true);



        Panel tpCategory = new Panel("Rules");
        tpCategory.setIconCls("nav-categories");
        accordion.add(tpCategory);


        Panel tpPackageExplorer = new Panel("Packages");
        tpPackageExplorer.setIconCls("nav-packages");
        accordion.add(tpPackageExplorer);



        Panel tpDeployment = new Panel("Deployment");
        tpDeployment.setIconCls("nav-deployment");
        accordion.add(tpDeployment);

        Panel tpAdmin = new Panel("Administration");
        tpAdmin.setIconCls("nav-admin");
        accordion.add(tpAdmin);

        Panel tpQA = new Panel("QA");
        tpQA.setIconCls("nav-qa");
        accordion.add(tpQA);


        packagesPanel = new VerticalPanel();
        final VerticalPanel deploymentPanel = new VerticalPanel();
        VerticalPanel adminPanel = new VerticalPanel();


        /** **************************** */

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




        Toolbar rulesToolBar = new Toolbar();
        rulesToolBar.addButton(new ToolbarMenuButton("Create New", rulesNewMenu()));

        VerticalPanel rulesPanel = new VerticalPanel();
        rulesPanel.add(rulesToolBar);
        rulesPanel.add(categoryTree);

        rulesPanel.setWidth("100%");
        tpCategory.add(rulesPanel);




        Toolbar pkgToolbar = new Toolbar();
        pkgToolbar.addButton(new ToolbarMenuButton("Create New", packageNewMenu()));
        packagesPanel.setWidth("100%");
        packagesPanel.add(pkgToolbar);


        Toolbar deployToolbar = new Toolbar();
        deployToolbar.addButton(new ToolbarMenuButton("Deploy...", deploymentMenu()));
        deploymentPanel.add(deployToolbar);
        deploymentPanel.setWidth("100%");

        /** ****************** */

        TreePanel adminTree = basicTreeStructure(ExplorerNodeConfig
                .getAdminStructure(), new TreePanelListenerAdapter() {
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
                		centertabbedPanel.addTab("Backup Manager", true, new BackupManager(), "bakman");
                    break;

                case 4:
                	if (!centertabbedPanel.showIfOpen("errorLog"))
                		centertabbedPanel.addTab("Error Log", true, new LogViewer(), "errorLog");
                    break;
                }

            }
        });

        adminPanel.add(adminTree);
        adminPanel.setWidth("100%");


        tpCategory.add(rulesPanel);


        tpPackageExplorer.add(packagesPanel);
        tpDeployment.add(deploymentPanel);

        tpAdmin.add(adminPanel);


        //these panels are lazy loaded to easy startup wait time.
        tpPackageExplorer.addListener(new PanelListenerAdapter() {
        	public void onExpand(Panel panel) {
        		if (!packagesLoaded) {
        			packagesPanel.add(packageExplorer(centertabbedPanel));
        			packagesLoaded = true;
        		}
        	}
        });

        tpDeployment.addListener(new PanelListenerAdapter() {
        	public void onExpand(Panel panel) {
        		if (!deploymentPackagesLoaded) {
        			deploymentPanel.add(deploymentExplorer(centertabbedPanel));
        			deploymentPackagesLoaded = true;
        		}
        	}
        });






        final VerticalPanel qaPanel = new VerticalPanel();
        qaPanel.setWidth("100%");


//        tpQA.addListener(new PanelListenerAdapter() {
//        	private boolean qaPackagesLoaded;
//        	public void onActivate(Panel panel) {
//        		if (!qaPackagesLoaded) {
        	        TreePanel qaTree = genericExplorerWidget(ExplorerNodeConfig.getQAStructure(centertabbedPanel));
        	        qaPanel.add(qaTree);
//        			qaPackagesLoaded = true;
//        		}
//        	}
//        });

        tpQA.add(qaPanel);

    }

	private Menu deploymentMenu() {
		Menu m = new Menu();

        Item nds = new Item("New Deployment snapshot", new BaseItemListenerAdapter() {
			public void onClick(BaseItem item, EventObject e) {
				SnapshotView.showNewSnapshot();
			}
		});
        nds.setIcon("images/snapshot_small.gif");
        m.addItem(nds);

        Item rebuild = new Item("Rebuild all snapshot binaries", new BaseItemListenerAdapter() {
			public void onClick(BaseItem item, EventObject e) {
				SnapshotView.rebuildBinaries();
			}
		});
        rebuild.setIcon("images/refresh.gif");
        m.addItem(rebuild);

        return m;
	}

	private Menu rulesNewMenu() {
		Menu m = new Menu();

		m.addItem( new Item("New Business Rule (Guided editor)", new BaseItemListenerAdapter() {
				public void onClick(BaseItem item, EventObject e) {
					launchWizard(AssetFormats.BUSINESS_RULE, "New Business Rule (Guided editor)", true);
				}
			}, "images/business_rule.gif"));


		m.addItem( new Item("New DSL Business Rule (text editor)",new BaseItemListenerAdapter() {
				public void onClick(BaseItem item, EventObject e) {
					launchWizard(AssetFormats.DSL_TEMPLATE_RULE, "New Rule using DSL", true);
				}
			}, "images/business_rule.gif"));


        m.addItem(new Item("New DRL (Technical rule)", new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DRL, "New DRL", true);
        			}
        	    }, "images/rule_asset.gif"));

        m.addItem(new Item("New Decision Table (Spreadsheet)", new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.DECISION_SPREADSHEET_XLS, "New Decision Table (Spreadsheet)", true);
        			}
        		}, "images/spreadsheet_small.gif"));

        m.addItem(new Item("New Decision Table (Web - guided editor)", new BaseItemListenerAdapter() {
			public void onClick(BaseItem item, EventObject e) {
				launchWizard(AssetFormats.DECISION_TABLE_GUIDED, "New Decision Table (Guided editor)", true);
			}
		}, "images/gdst.gif"));

        m.addItem(new Item("New Test Scenario", new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.TEST_SCENARIO,
                                "Create a test scenario.", false);
        			}
        		}, "images/test_manager.gif"));

		return m;
	}

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

        m.addItem(new Item("New Model (jar) of fact classes", new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.MODEL, "New model archive (jar)", false, currentPackage);
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
        				launchWizard(AssetFormats.ENUMERATION,
                                "Create a new enumeration (drop down mapping).", false, currentPackage);
        			}
        		}, "images/new_enumeration.gif"));

        m.addItem(new Item("New Test Scenario", new BaseItemListenerAdapter() {
        			public void onClick(BaseItem item, EventObject e) {
        				launchWizard(AssetFormats.TEST_SCENARIO,
                                "Create a test scenario.", false, currentPackage);
        			}
        		}, "images/test_manager.gif"));



		return m;
	}

    private void launchWizard(String format,
            String title, boolean showCats, String currentlySelectedPackage) {

    	NewAssetWizard pop = new NewAssetWizard( new EditItemEvent() {
                                   public void open(String key) {
                                       centertabbedPanel.openAsset(key);
                                   }
                               },
                               showCats,
                               format,
                               title,
                               currentlySelectedPackage );

    	pop.show();
    }


    private void launchWizard(String format,
            String title, boolean showCats) {
    	launchWizard(format, title, showCats, null);
    }



    private TreePanel basicTreeStructure(TreeNode basenode, TreePanelListenerAdapter listener) {
        TreePanel adminTreePanel = genericExplorerWidget(basenode);
        adminTreePanel.addListener(listener);
        return adminTreePanel;
    }


    private Panel deploymentExplorer(final ExplorerViewCenterPanel tabPanel) {
//        final ContentPanel cp = new ContentPanel(Ext.generateId(), "Deployment Explorer");
//        cp.setWidth("100%");

        final TreeNode root = new TreeNode("Package snapshots");
        root.setIcon("images/silk/chart_organisation.gif");
        root.setId("snapshotRoot");

		final TreePanel panel = genericExplorerWidget(root);


		deploymentListPackages(root);

		panel.addListener(new TreePanelListenerAdapter() {

			public void onCollapseNode(TreeNode node) {
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

			public void onExpandNode(final TreeNode node) {
				if (node.getId().equals("snapshotRoot")) {
					return;
				}
				final PackageConfigData conf = (PackageConfigData) node.getUserObject();
				RepositoryServiceFactory.getService().listSnapshots(conf.name, new GenericCallback() {
					public void onSuccess(Object data) {
						final SnapshotInfo[] snaps = (SnapshotInfo[]) data;
						for (int i = 0; i < snaps.length; i++) {
							final SnapshotInfo snapInfo = snaps[i];
							TreeNode snap = new TreeNode();
							snap.setTooltip(snapInfo.comment);
							snap.setText(snapInfo.name);

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

        return panel;
    }

	private void deploymentListPackages(final TreeNode root) {
		System.err.println("-->Loading packages");
		RepositoryServiceFactory.getService().listPackages(
                new GenericCallback() {
                    public void onSuccess(Object data) {
                        PackageConfigData value[] = (PackageConfigData[]) data;
                        for (int i = 0; i < value.length; i++) {
                        	TreeNode pkg = new TreeNode(value[i].name);
                        	pkg.setIcon("images/snapshot_small.gif");
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
    private Panel packageExplorer(final ExplorerViewCenterPanel tabPanel) {

//        final Panel cp = new Panel("Package Explorer");
//        cp.setWidth("100%");

        TreeNode root = new TreeNode("Packages");
        root.setAttribute("icon", "images/silk/chart_organisation.gif");



		final TreePanel panel = genericExplorerWidget(root);

//        cp.add(panel);
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
	    		} else if (node.getUserObject() instanceof Object[] ){
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
        panel.addListener(treePanelListener);


        return panel;
    }

	private void loadPackages(final TreeNode root) {
		System.err.println("-->Loading packages");
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

	private void refreshPackageTree() {
		packagesPanel.remove(1);
		packagesPanel.add(packageExplorer(centertabbedPanel));
	}



}
