package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.qa.AnalysisView;
import org.drools.guvnor.client.qa.ScenarioPackageView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.security.Capabilities;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

/*
 * This class contains static node config for BRMS' explorer widgets
 */
public class ExplorerNodeConfig {

	public static String CATEGORY_ID = "category";
	public static String STATES_ID = "states";

	public static TreeNode getPackageItemStructure(String packageName, String uuid) {

		TreeNode pkg = new TreeNode(packageName);
		pkg.setAttribute("uuid", uuid);
		pkg.setAttribute("icon", "images/package.gif");

        pkg.appendChild( makeItem( "Business rule assets",
                "images/rule_asset.gif", AssetFormats.BUSINESS_RULE_FORMATS ) );
        pkg.appendChild( makeItem( "Technical rule assets",
                "images/technical_rule_assets.gif", new String[]{AssetFormats.DRL} )  );
		pkg.appendChild( makeItem( "Functions",
		                "images/function_assets.gif", new String[]{AssetFormats.FUNCTION} ) );
		pkg.appendChild( makeItem( "DSL configurations",
		                "images/dsl.gif",

		                               new String[]{AssetFormats.DSL} ) );
		pkg.appendChild( makeItem( "Model",
		                "images/model_asset.gif",

		                               new String[]{AssetFormats.MODEL, AssetFormats.DRL_MODEL} ) ) ;

		pkg.appendChild( makeItem( "Rule Flows",
		 "images/ruleflow_small.gif",

		                new String[]{AssetFormats.RULE_FLOW_RF} ) ) ;

		pkg.appendChild( makeItem( "Enumerations",
		 "images/enumeration.gif",

		                new String[]{AssetFormats.ENUMERATION} ) ) ;


		pkg.appendChild(makeItem( "Test Scenarios",
		                "images/test_manager.gif",

		                               new String[]{AssetFormats.TEST_SCENARIO} ) ) ;


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

		TreeNode adminNode = new TreeNode("Admin");
		adminNode.setAttribute("icon", "images/managment.gif");

		String[][] adminStructure = new String[][] {
				{ "Categories", "images/category_small.gif" }, // ID 0
				{ "Archived Items", "images/backup_small.gif" }, // ID 1
				{ "Statuses", "images/tag.png" }, // ID 2
				{ "Import/Export", "images/save_edit.gif" }, //ID 3
				{ "Error log", "images/error.gif" }}; // ID 4

		for (int i = 0; i < adminStructure.length; i++) {

			String[] packageData = adminStructure[i];
			TreeNode localChildNode = new TreeNode(packageData[0]);
			localChildNode.setAttribute("icon", packageData[1]);
			localChildNode.setAttribute("id", String.valueOf(i));

			adminNode.appendChild(localChildNode);
		}
		return adminNode;
	}

	public static TreeNode getRulesStructure () {
		TreeNode tn = new TreeNode();
		tn.setText("Rules");
		tn.setExpanded(true);

		TreeNode tnc = new TreeNode();
		tnc.setIcon("images/find.gif");
		tnc.setId("FIND");
		tnc.setText("Find");

		tn.appendChild(tnc);
		if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_PACKAGE_VIEW)) {
			tn.appendChild(getStatesStructure());
		}
		tn.appendChild(getCategoriesStructure());
		return tn;

	}

	public static TreeNode getCategoriesStructure () {
		final TreeNode treeNode = new TreeNode("Categories");
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
							infanticide(treeNode);
						} else {
							for (int i = 0; i < value.length; i++) {

								final String current = value[i];
								System.err.println("VALUE: " + current + "(" + i + ")");
								final TreeNode childNode = new TreeNode();
								childNode.setIcon("images/category_small.gif");
								childNode.setText(current);

								childNode.setUserObject((path.equals("/")) ? current : path + "/" + current);
								childNode.appendChild(new TreeNode("Please wait..."));
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


				});
	}

	private static void infanticide(final TreeNode treeNode) {
		Node[] children = treeNode.getChildNodes();
		for (int i = 0; i < children.length; i++) {
			treeNode.removeChild(children[i]);
		}
	}

	public static TreeNode getStatesStructure () {

		final TreeNode treeNode = new TreeNode("States");
		treeNode.setAttribute("icon", "images/status_small.gif");
		treeNode.setAttribute("id",STATES_ID);


		RepositoryServiceFactory.getService().listStates(new GenericCallback() {
			public void onSuccess(Object data) {
				String value[] = (String[]) data;


				for (int i = 0; i < value.length; i++) {
					TreeNode childNode = new TreeNode(value[i]);
					childNode.setAttribute("icon", "images/category_small.gif");
					childNode.setUserObject("-" + value[i]);
					treeNode.appendChild(childNode);
				}
			}
		});

		return treeNode;
	}

	public static TreeNode getQAStructure(final ExplorerViewCenterPanel centerPanel) {

		final TreeNode treeNode = new TreeNode();
		treeNode.setText("QA");


		final TreeNode scenarios = new TreeNode();
		scenarios.setText("Test Scenarios in packages:");
		scenarios.setIcon("images/test_manager.gif");

		final EditItemEvent edit = new EditItemEvent() {
			public void open(String key) {centerPanel.openAsset(key);}
		};

		scenarios.appendChild(new TreeNode("Please wait..."));
		treeNode.appendChild(scenarios);


		final TreeNode analysis = new TreeNode();
		analysis.setText("Analysis");
		analysis.setIcon("images/analyze.gif");
		analysis.setExpanded(false);
		analysis.appendChild(new TreeNode("Please wait..."));

		treeNode.appendChild(analysis);


		scenarios.addListener(new TreeNodeListenerAdapter() {
			public void onExpand(Node node) {
				System.err.println("-->Loading packages 1");

				RepositoryServiceFactory.getService().listPackages(new GenericCallback() {
					public void onSuccess(Object data) {
						PackageConfigData[] conf = (PackageConfigData[]) data;

						for (int i = 0; i < conf.length; i++) {
							final PackageConfigData c = conf[i];
							TreeNode pkg = new TreeNode();
							pkg.setText(c.name);
							pkg.setIcon("images/package.gif");

							scenarios.appendChild(pkg);
							pkg.addListener(new TreeNodeListenerAdapter() {
								public void onClick(Node node, EventObject e) {
									if (!centerPanel.showIfOpen("scenarios" + c.uuid)) {
										centerPanel.addTab("Scenarios for " + c.name, true, new ScenarioPackageView(
												c.uuid, c.name, edit, centerPanel ), "scenarios" + c.uuid);
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
				node.appendChild(new TreeNode("Please wait..."));
			}
		});


		analysis.addListener(new TreeNodeListenerAdapter() {


			public void onExpand(Node node) {
				System.err.println("-->Loading packages 2");
				RepositoryServiceFactory.getService().listPackages(new GenericCallback() {
					public void onSuccess(Object data) {
						PackageConfigData[] conf = (PackageConfigData[]) data;

						for (int i = 0; i < conf.length; i++) {
							final PackageConfigData c = conf[i];
							TreeNode pkg = new TreeNode();
							pkg.setText(c.name);
							pkg.setIcon("images/package.gif");


							analysis.appendChild(pkg);
							pkg.addListener(new TreeNodeListenerAdapter() {
								public void onClick(Node node, EventObject e) {
									if (!centerPanel.showIfOpen("analysis" + c.uuid)) {
										centerPanel.addTab("Analysis for " + c.name, true, new AnalysisView(c.uuid, c.name), "analysis" + c.uuid);
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
				node.appendChild(new TreeNode("Please wait..."));
			}
		});

		return treeNode;
	}

}


