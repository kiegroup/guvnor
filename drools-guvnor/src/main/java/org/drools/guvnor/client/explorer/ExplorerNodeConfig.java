package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.qa.AnalysisView;
import org.drools.guvnor.client.qa.ScenarioPackageView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;
import com.gwtext.client.util.Format;

/*
 * This class contains static node config for BRMS' explorer widgets
 */
public class ExplorerNodeConfig {

	public static String CATEGORY_ID = "category";      //NON-NLS
	public static String STATES_ID = "states";          //NON-NLS
    private static Constants constants = ((Constants) GWT.create(Constants.class));

    public static TreeNode getPackageItemStructure(String packageName, String uuid) {

		TreeNode pkg = new TreeNode(packageName);
		pkg.setAttribute("uuid", uuid);
		pkg.setAttribute("icon", "images/package.gif");



        pkg.appendChild( makeItem(constants.BusinessRuleAssets(),
                "images/rule_asset.gif", AssetFormats.BUSINESS_RULE_FORMATS ) );
        pkg.appendChild( makeItem(constants.TechnicalRuleAssets(),
                "images/technical_rule_assets.gif", new String[]{AssetFormats.DRL} )  );
		pkg.appendChild( makeItem(constants.Functions(),
		                "images/function_assets.gif", new String[]{AssetFormats.FUNCTION} ) );
		pkg.appendChild( makeItem(constants.DSLConfigurations(),
		                "images/dsl.gif",

		                               new String[]{AssetFormats.DSL} ) );
		pkg.appendChild( makeItem(constants.Model(),
		                "images/model_asset.gif",

		                               new String[]{AssetFormats.MODEL, AssetFormats.DRL_MODEL} ) ) ;

		if (Preferences.getBooleanPref("flex-bpel-editor")) {
			pkg.appendChild(makeItem(constants.BPELPackages(),
					"images/model_asset.gif",

					new String[] { AssetFormats.BPEL_PACKAGE }));
		}
		
		pkg.appendChild( makeItem(constants.RuleFlows(),
		 "images/ruleflow_small.gif",

		                new String[]{AssetFormats.RULE_FLOW_RF} ) ) ;

		pkg.appendChild( makeItem(constants.Enumerations(),
		 "images/enumeration.gif",

		                new String[]{AssetFormats.ENUMERATION} ) ) ;


		pkg.appendChild(makeItem(constants.TestScenarios(),
		                "images/test_manager.gif",

		                               new String[]{AssetFormats.TEST_SCENARIO} ) ) ;

        pkg.appendChild(makeItem(constants.XMLProperties(),
                "images/new_file.gif",
                               new String[]{AssetFormats.XML, AssetFormats.PROPERTIES} ) ) ;

        pkg.appendChild(makeItem(constants.OtherAssetsDocumentation(),
                "images/new_file.gif",
                               new String[0] ) ) ;


        return pkg;
	}

	private static TreeNode makeItem(final String txt, final String img,
			final String[] formats) {
		TreeNode tn = new TreeNode();
		tn.setIcon(img);
		tn.setText(txt);
		tn.setUserObject(new Object[] {formats, txt});
		return tn;
	}

	public static TreeNode getAdminStructure() {

		TreeNode adminNode = new TreeNode(constants.Admin());
		//adminNode.setAttribute("icon", "images/managment.gif");

		String[][] adminStructure = new String[][] {
				{ constants.Category(), "images/category_small.gif", "0"},
                { constants.Status(), "images/tag.png", "2" },
				{ constants.Archive(), "images/backup_small.gif", "1" }, 
                { constants.EventLog(), "images/error.gif", "4" },
                { constants.UserPermission(), "images/icoUsers.gif", "5" },
				{ constants.ImportExport(), "images/save_edit.gif", "3" },
				{ constants.About(), "images/information.gif", "6" }};

		for (int i = 0; i < adminStructure.length; i++) {

			String[] packageData = adminStructure[i];
			TreeNode localChildNode = new TreeNode(packageData[0]);
			localChildNode.setAttribute("icon", packageData[1]);   //NON-NLS
			localChildNode.setAttribute("id", packageData[2]);

			adminNode.appendChild(localChildNode);
		}
		return adminNode;
	}

	public static TreeNode getRulesStructure () {
		TreeNode tn = new TreeNode();
		tn.setText(constants.AssetsTreeView());
		tn.setExpanded(true);

		TreeNode tnc = new TreeNode();
		tnc.setIcon("images/find.gif"); //NON-NLS
		tnc.setId("FIND");
		tnc.setText(constants.Find());

		tn.appendChild(tnc);
		if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
			tn.appendChild(getStatesStructure());
		}
		tn.appendChild(getCategoriesStructure());
		return tn;

	}

	public static TreeNode getCategoriesStructure () {
		final TreeNode treeNode = new TreeNode(constants.ByCategory());
		treeNode.setAttribute("icon", "images/silk/chart_organisation.gif");
		treeNode.setAttribute("id",CATEGORY_ID);
		doCategoryNode(treeNode, "/");
		return treeNode;
	}

	private static void doCategoryNode(final TreeNode treeNode, final String path) {
		infanticide(treeNode);
		RepositoryServiceFactory.getService().loadChildCategories(path,
				new GenericCallback() {
					public void onSuccess(Object data) {
						final String value[] = (String[]) data;
						if (value.length == 0) {
							if (path.equals("/") && ExplorerLayoutManager.shouldShow(Capabilities.SHOW_ADMIN)) {
                                RepositoryServiceFactory.getService().listPackages(new GenericCallback<PackageConfigData[]>() {
                                    public void onSuccess(PackageConfigData[] result) {
                                        if (result.length == 1) {
                                            doNewRepoDialog();
                                        }
                                    }
                                });
                                
							}
							infanticide(treeNode);
						} else {
							for (int i = 0; i < value.length; i++) {

								final String current = value[i];
								System.err.println("VALUE: " + current + "(" + i + ")");
								final TreeNode childNode = new TreeNode();
								childNode.setIcon("images/category_small.gif");
								childNode.setText(current);

								childNode.setUserObject((path.equals("/")) ? current : path + "/" + current);
								childNode.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
								childNode.addListener(new TreeNodeListenerAdapter() {
									boolean expanding = false;

									public void onExpand(Node node) {

										if (!expanding) {
											expanding = true;
											infanticide(childNode);
											doCategoryNode(childNode, (String) childNode.getUserObject());
											childNode.expand();
											expanding = false;
										}
									}
								});

								treeNode.appendChild(childNode);
							}
						}
					}

					private void doNewRepoDialog() {
						NewRepoDialog diag = new NewRepoDialog();
						diag.show();
					}


				});
	}

	private static void infanticide(final TreeNode treeNode) {
		Node[] children = treeNode.getChildNodes();
		for (int i = 0; i < children.length; i++) {
			treeNode.removeChild(children[i]);
		}
	}

	public static TreeNode getStatesStructure () {

		final TreeNode treeNode = new TreeNode(constants.ByStatus());
		treeNode.setAttribute("icon", "images/status_small.gif"); //NON-NLS
		treeNode.setAttribute("id",STATES_ID);


		RepositoryServiceFactory.getService().listStates(new GenericCallback<String[]>() {
			public void onSuccess(String[] value) {
				for (int i = 0; i < value.length; i++) {
					TreeNode childNode = new TreeNode(value[i]);
					childNode.setAttribute("icon", "images/category_small.gif");  //NON-NLS
					childNode.setUserObject("-" + value[i]);
					treeNode.appendChild(childNode);
				}
			}
		});

		return treeNode;
	}

	public static TreeNode getQAStructure(final ExplorerViewCenterPanel centerPanel) {

		final TreeNode treeNode = new TreeNode();
		treeNode.setText(constants.QA());


		final TreeNode scenarios = new TreeNode();
		scenarios.setText(constants.TestScenariosInPackages());
		scenarios.setIcon("images/test_manager.gif"); //NON-NLS

		final EditItemEvent edit = new EditItemEvent() {
			public void open(String key) {centerPanel.openAsset(key);}
		};

		scenarios.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
		treeNode.appendChild(scenarios);


		final TreeNode analysis = new TreeNode();
		analysis.setText(constants.Analysis());
		analysis.setIcon("images/analyze.gif"); //NON-NLS
		analysis.setExpanded(false);
		analysis.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));

        if (Preferences.getBooleanPref("verifier")) {
		    treeNode.appendChild(analysis);
        }


		scenarios.addListener(new TreeNodeListenerAdapter() {
			public void onExpand(Node node) {

				RepositoryServiceFactory.getService().listPackages(new GenericCallback<PackageConfigData[]>() {
					public void onSuccess(PackageConfigData[] conf) {
						for (int i = 0; i < conf.length; i++) {
							final PackageConfigData c = conf[i];
							TreeNode pkg = new TreeNode();
							pkg.setText(c.name);
							pkg.setIcon("images/package.gif");  //NON-NLS

							scenarios.appendChild(pkg);
							pkg.addListener(new TreeNodeListenerAdapter() {
								public void onClick(Node node, EventObject e) {
									if (!centerPanel.showIfOpen("scenarios" + c.uuid)) { //NON-NLS
                                        String m = Format.format(constants.ScenariosForPackage(), c.name);
										centerPanel.addTab(m, true, new ScenarioPackageView(
												c.uuid, c.name, edit, centerPanel ), "scenarios" + c.uuid); //NON-NLS
									}
								}
							});
						}
						scenarios.removeChild(scenarios.getFirstChild());

					}
				});
			}


			public void onCollapse(Node node) {
				Node[] cs = node.getChildNodes();
				for (int i = 0; i < cs.length; i++) {
					node.removeChild(cs[i]);
				}
				node.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
			}
		});


		analysis.addListener(new TreeNodeListenerAdapter() {


			public void onExpand(Node node) {
				RepositoryServiceFactory.getService().listPackages(new GenericCallback<PackageConfigData[]>() {
					public void onSuccess(PackageConfigData[] conf) {

						for (int i = 0; i < conf.length; i++) {
							final PackageConfigData c = conf[i];
							TreeNode pkg = new TreeNode();
							pkg.setText(c.name);
							pkg.setIcon("images/package.gif");    //NON-NLS


							analysis.appendChild(pkg);
							pkg.addListener(new TreeNodeListenerAdapter() {
								public void onClick(Node node, EventObject e) {
									if (!centerPanel.showIfOpen("analysis" + c.uuid)) { //NON-NLS
                                        String m = Format.format(constants.AnalysisForPackage(), c.name);
										centerPanel.addTab(m, true, new AnalysisView(c.uuid, c.name), "analysis" + c.uuid); //NON-NLS
									}
								}
							});
						}
						analysis.removeChild(analysis.getFirstChild());

					}
				});
			}

			public void onCollapse(Node node) {
				Node[] cs = node.getChildNodes();
				for (int i = 0; i < cs.length; i++) {
					node.removeChild(cs[i]);
				}
				node.appendChild(new TreeNode(constants.PleaseWaitDotDotDot()));
			}
		});


		return treeNode;
	}

}


