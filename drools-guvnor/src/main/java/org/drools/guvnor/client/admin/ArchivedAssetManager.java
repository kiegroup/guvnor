package org.drools.guvnor.client.admin;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.messages.Messages;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

/**
 * @author Fernando Meyer
 */

public class ArchivedAssetManager extends Composite {


	private AssetItemGrid grid;
	private ListBox packages = new ListBox(true);
    private Messages constants = GWT.create(Messages.class);

    public ArchivedAssetManager(final ExplorerViewCenterPanel tab) {


        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        header.add(new HTML(constants.ArchivedItems()));

        pf.addHeader("images/backup_large.png", header); //NON-NLS




        EditItemEvent edit = new EditItemEvent () {
            public void open(String key) {
            	tab.openAsset(key);
            }
        };
        grid = new AssetItemGrid(edit, AssetItemGrid.ARCHIVED_RULE_LIST_TABLE_ID, new AssetItemGridDataLoader() {
			public void loadData(int startRow, int numberOfRows,
					GenericCallback cb) {
				RepositoryServiceFactory.getService().loadArchivedAssets(startRow, numberOfRows, cb);
			}
        });


        loadPackages();
        Toolbar tb = new Toolbar();
        final ToolbarButton restorePackage = new ToolbarButton();
        restorePackage.addListener(new ButtonListenerAdapter() {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
        				restorePackage(packages.getValue(packages.getSelectedIndex()));
        			}

        		});
        restorePackage.setText(constants.RestoreSelectedPackage());
        tb.addButton(restorePackage);




        final ToolbarButton delPackage = new ToolbarButton();
        delPackage.setText(constants.PermanentlyDeletePackage());
        delPackage.addListener(new ButtonListenerAdapter() {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
        				if (Window.confirm(constants.AreYouSurePackageDelete())) {
        					deletePackage(packages.getValue(packages.getSelectedIndex()));
        				}
        			}
        });
        tb.addButton(delPackage);



        pf.startSection(constants.ArchivedPackagesList());

        pf.addRow(tb);
        pf.addRow(packages);


        pf.endSection();

        tb = new Toolbar();
        final ToolbarButton restoreAsset = new ToolbarButton();
        restoreAsset.setText(constants.RestoreSelectedAsset());
        tb.addButton(restoreAsset);
        restoreAsset.addListener(new ButtonListenerAdapter() {
        			public void onClick(com.gwtext.client.widgets.Button button, EventObject e) {
                    	if (grid.getSelectedRowUUID() == null) {
                    		Window.alert(constants.PleaseSelectAnItemToRestore());
                    		return;
                    	}
                        RepositoryServiceFactory.getService().archiveAsset( grid.getSelectedRowUUID(), false, new GenericCallback() {
                            public void onSuccess(Object arg0) {
                                Window.alert(constants.ItemRestored());
                                grid.refreshGrid();
                            }
                        });
        			};
        });


        final ToolbarButton deleteAsset = new ToolbarButton();
        deleteAsset.setText(constants.DeleteSelectedAsset());
        tb.addButton(deleteAsset);

        deleteAsset.addListener(
        		new ButtonListenerAdapter() {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
                    	if (grid.getSelectedRowUUID() == null) {
                    		Window.alert(constants.PleaseSelectAnItemToPermanentlyDelete());
                    		return;
                    	}
                    	if (!Window.confirm(constants.AreYouSureDeletingAsset())) {
                    		return;
                    	}
                        RepositoryServiceFactory.getService().removeAsset( grid.getSelectedRowUUID(), new GenericCallback() {

                            public void onSuccess(Object arg0) {
                                Window.alert(constants.ItemDeleted());
                                grid.refreshGrid();
                            }
                        });
        			}
        		});

        pf.startSection(constants.ArchivedAssets());
        pf.addRow(tb);

        pf.addRow(grid);

        pf.endSection();


        initWidget( pf );
    }


	private void deletePackage(final String uuid) {
		RepositoryServiceFactory.getService().removePackage(uuid, new GenericCallback( ) {
			public void onSuccess(Object data) {
				Window.alert(constants.PackageDeleted());
				packages.clear();
				loadPackages();
			}
		});
	}


	private void restorePackage(String uuid) {
		RepositoryServiceFactory.getService().loadPackageConfig(uuid, new GenericCallback<PackageConfigData>() {
			public void onSuccess(PackageConfigData cf) {
				cf.archived = false;
				RepositoryServiceFactory.getService().savePackage(cf, new GenericCallback<ValidatedResponse>() {
					public void onSuccess(ValidatedResponse data) {
						Window.alert(constants.PackageRestored());
						packages.clear();
						loadPackages();
					}
				});
			}
		});
	}



    private ListBox loadPackages() {

    	RepositoryServiceFactory.getService().listArchivedPackages(new GenericCallback<PackageConfigData[]>() {
			public void onSuccess(PackageConfigData[] configs) {
				for (int i = 0; i < configs.length; i++) {
						packages.addItem(configs[i].name, configs[i].uuid);
				}
				if (configs.length == 0) {
					packages.addItem(constants.noArchivedPackages());
				}
			}
    	});


		return packages;
	}



}