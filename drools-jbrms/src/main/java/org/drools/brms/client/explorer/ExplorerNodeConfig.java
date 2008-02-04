package org.drools.brms.client.explorer;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreeNodeConfig;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

/*
 * This class contains static node config for BRMS' explorer widget
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

		                               new String[]{AssetFormats.MODEL} ) ) ;

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
		TreeNode tn = new TreeNode(new TreeNodeConfig() {
			{
				setIcon(img);
				setText(txt);

			}
		});
		tn.setUserObject(new Object[] {formats, txt});
		return tn;
	}

	public static TreeNode getAdminStructure() {

		TreeNode adminNode = new TreeNode("Admin");
		adminNode.setAttribute("icon", "images/managment.gif");

		String[][] adminStructure = new String[][] {
				{ "Categories", "images/category_small.gif" }, // ID 0
				{ "Archived Assets", "images/backup_small.gif" }, // ID 1
				{ "State", "images/tag.png" }, // ID 2
				{ "Import/Export", "images/save_edit.gif" }, // ID 3
				{ "Users", "images/icoUsers.gif" }, // ID 4
				{ "Security", "images/login.gif" } }; // ID 5

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

		//final TreeNode adminNode = new TreeNode("Rules");
		return new TreeNode(new TreeNodeConfig() {
			{
				setText("Rules");
				setExpanded(true);
			}
		}) {
			{
				appendChild(new TreeNode(new TreeNodeConfig() {
					{
						setIcon("images/find.gif");
						setId("FIND");
						setText("Find");

					}
				}));
				appendChild(getStatesStructure());
				appendChild(getCategoriesStructure());
			}

		};

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
								final TreeNode childNode = new TreeNode( new TreeNodeConfig() {
									{
										setIcon("images/category_small.gif");
										setText(current);
									}
								});

								childNode.setUserObject((path.equals("/")) ? current : path + "/" + current);
								childNode.appendChild(new TreeNode("Please wait..."));
								childNode.addTreeNodeListener(new TreeNodeListenerAdapter() {
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

}


