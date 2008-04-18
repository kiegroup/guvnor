package org.drools.brms.client.packages;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.PrettyFormLayout;
import org.drools.brms.client.common.RulePackageSelector;
import org.drools.brms.client.explorer.ExplorerLayoutManager;
import org.drools.brms.client.explorer.ExplorerNodeConfig;
import org.drools.brms.client.explorer.ExplorerViewCenterPanel;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.rulelist.AssetItemGrid;
import org.drools.brms.client.rulelist.AssetItemGridDataLoader;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * This is the new snapshot view.
 * @author Michael Neale
 *
 */
public class SnapshotView extends Composite {


	public static final String LATEST_SNAPSHOT = "LATEST";

	private ExplorerViewCenterPanel centerPanel;
	private PackageConfigData parentConf;
	private SnapshotInfo snapInfo;
	private SimplePanel gridPanel = new SimplePanel();

	private Command close;

	public SnapshotView(SnapshotInfo snapInfo, PackageConfigData parentPackage, Command closeSnap) {

		VerticalPanel vert = new VerticalPanel();
		this.snapInfo = snapInfo;
		this.parentConf = parentPackage;
		this.close = closeSnap;
		PrettyFormLayout head = new PrettyFormLayout();

		head.addHeader("images/snapshot.png", header());



		vert.add(head);

		centerPanel = new ExplorerViewCenterPanel();


		//MN
		centerPanel.addTab("Info", false, infoPanel(), "INFO");
		//centerPanel.addTab("Info", false, new Label("ho"), "INFO");

		vert.add(centerPanel.getPanel());


		vert.setWidth("100%");
		initWidget(vert);

	}

	private Widget header() {
		FlexTable ft = new FlexTable();




		ft.setWidget(0, 0, new Label("Viewing snapshot:"));
		ft.setWidget(0, 1, new HTML("<b>" + this.snapInfo.name + "</b>"));
		ft.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		ft.setWidget(1, 0, new Label("For package:"));
		ft.setWidget(1, 1, new Label(this.parentConf.name));
		ft.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		HTML dLink = new HTML("<a href='" + PackageBuilderWidget.getDownloadLink(this.parentConf)
				+ "' target='_blank'>click here to download binary (or copy URL for Rule Agent)</a>");
		ft.setWidget(2, 0, new Label("Deployment URL:"));
		ft.setWidget(2, 1, dLink);
		ft.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		ft.setWidget(3, 0, new Label("Snapshot created on:"));
		ft.setWidget(3, 1, new Label( parentConf.lastModified.toLocaleString() ));
		ft.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		ft.setWidget(4,0, new Label("Comment:"));
		ft.setWidget(4,1, new Label( parentConf.checkinComment ));
		ft.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel actions = new HorizontalPanel();

		actions.add(getDeleteButton(this.snapInfo.name, this.parentConf.name));
		actions.add(getCopyButton(this.snapInfo.name, this.parentConf.name));


		ft.setWidget(5, 0, actions);
		ft.getFlexCellFormatter().setColSpan(5, 0, 2);



		return ft;
	}

    private Button getDeleteButton(final String snapshotName, final String pkgName) {
        Button btn = new Button("Delete");
        btn.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if (Window.confirm( "Are you sure you want to delete the snapshot labelled [" + snapshotName +
                        "] from the package [" + pkgName + "] ?")) {
                    RepositoryServiceFactory.getService().copyOrRemoveSnapshot( pkgName, snapshotName, true, null, new GenericCallback() {
                        public void onSuccess(Object data) {
                            close.execute();
                            Window.alert("Snapshot was deleted.");
                        }
                    });
                }
            }

        });
        return btn;
    }

    private Button getCopyButton(final String snapshotName, final String packageName) {
        final FormStylePopup copy = new FormStylePopup("images/snapshot.png", "Copy snapshot " + snapshotName);
        final TextBox box = new TextBox();
        copy.addAttribute( "New label:", box );
        Button ok = new Button("OK");
        copy.addAttribute( "", ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                RepositoryServiceFactory.getService().copyOrRemoveSnapshot( packageName, snapshotName, false, box.getText(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        copy.hide();
                        Window.alert("Created snapshot [" + snapshotName + "] for package [" + packageName + "]");
                    }
                });
            }
        } );


        Button btn = new Button("Copy");
        btn.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

    		  copy.show();
            }
        });

        return btn;
    }



	private Widget infoPanel() {
		HorizontalPanel h = new HorizontalPanel();
		h.add(packageTree());
		h.add(gridPanel);
		h.setHeight("100%");
		return h;
	}


	protected Widget packageTree() {
		TreeNode pkg = ExplorerNodeConfig.getPackageItemStructure(parentConf.name, snapInfo.uuid);
		pkg.setUserObject(snapInfo);
		TreeNode root = new TreeNode(snapInfo.name);
		root.appendChild(pkg);
		TreePanel tp = ExplorerLayoutManager.genericExplorerWidget(root);
		tp.addListener(new TreePanelListenerAdapter() {

			public void onClick(TreeNode node, EventObject e) {
				Object uo = node.getUserObject();
				if (uo instanceof Object[]) {
					Object o = ((Object[]) uo)[0];
					showAssetList((String[]) o);
				} else if (uo instanceof SnapshotInfo) {
					SnapshotInfo s = (SnapshotInfo) uo;
					centerPanel.openPackageEditor(s.uuid, null);
				}

			}
		});
		return tp;

	}

	protected void showAssetList(final String[] assetTypes) {
		this.gridPanel.clear();
		AssetItemGrid grid = new AssetItemGrid(new EditItemEvent() {
			public void open(String key) {
				centerPanel.openAsset(key);
			}
		}, AssetItemGrid.RULE_LIST_TABLE_ID, new AssetItemGridDataLoader() {
			public void loadData(int startRow, int numberOfRows,
					GenericCallback cb) {
				RepositoryServiceFactory.getService().listAssets(snapInfo.uuid, assetTypes, startRow, numberOfRows, AssetItemGrid.RULE_LIST_TABLE_ID , cb);
			}
		});
		this.gridPanel.add(grid);

	}

	public static void showNewSnapshot() {
		final FormStylePopup pop = new FormStylePopup("images/snapshot.png", "New snapshot");
		final RulePackageSelector sel = new RulePackageSelector();

		pop.addAttribute("For package:", sel);
		Button ok = new Button("OK");
		pop.addAttribute("", ok);
		pop.show();

		ok.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				pop.hide();
				String pkg = sel.getSelectedPackage();
				PackageBuilderWidget.showSnapshotDialog(pkg);
			}
		});


	}

	public static void rebuildBinaries() {
        if (Window.confirm( "Rebuilding the snapshot binaries will take some time, and only needs to be done if" +
                " the BRMS itself has been updated recently. This will also cause the rule agents to load the rules anew." +
                " Are you sure you want to do this?" )) {
            LoadingPopup.showMessage( "Rebuilding snapshots. Please wait, this may take some time..." );
            RepositoryServiceFactory.getService().rebuildSnapshots( new GenericCallback() {
                public void onSuccess(Object data) {
                    LoadingPopup.close();
                    Window.alert( "Snapshots were rebuilt successfully." );
                }
            });
        }
	}

}
