package org.drools.brms.client.admin;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.PrettyFormLayout;
import org.drools.brms.client.explorer.ExplorerViewCenterPanel;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rulelist.AssetItemGrid;
import org.drools.brms.client.rulelist.AssetItemGridDataLoader;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarSeparator;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

/**
 * @author Fernando Meyer
 */

public class ArchivedAssetManager extends Composite {


	private AssetItemGrid grid;
	private ListBox packages = new ListBox(true);

    public ArchivedAssetManager(final ExplorerViewCenterPanel tab) {


        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        header.add(new HTML("<b>Archived items</b>"));

        pf.addHeader("images/backup_large.png", header);




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
        Toolbar tb = new Toolbar(Ext.generateId());
        tb.addButton(new ToolbarButton(new ButtonConfig() {
        	{
        		setButtonListener(new ButtonListenerAdapter() {

        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
        				restorePackage(packages.getValue(packages.getSelectedIndex()));


        			}

        		});
        		setText("Restore selected package");

        	}
        }));

        tb.addButton(new ToolbarButton(new ButtonConfig() {
        	{
        		setButtonListener(new ButtonListenerAdapter() {

        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
        				if (Window.confirm("Are you sure you want to permanently delete this package? This can not be undone.")) {
        					deletePackage(packages.getValue(packages.getSelectedIndex()));
        				}


        			}


        		});
        		setText("Permanently delete package");

        	}
        }));


        pf.startSection("Archived packages");

        pf.addRow(tb);
        pf.addRow(packages);


        pf.endSection();

        tb = new Toolbar(Ext.generateId());
        tb.addButton(new ToolbarButton(new ButtonConfig() {
        	{
        		setText("Restore selected asset");
        		setButtonListener(new ButtonListenerAdapter() {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
                    	if (grid.getSelectedRowUUID() == null) {
                    		Window.alert("Please select an item to restore.");
                    		return;
                    	}
                        RepositoryServiceFactory.getService().archiveAsset( grid.getSelectedRowUUID(), false, new GenericCallback() {
                            public void onSuccess(Object arg0) {
                                Window.alert( "Item restored." );
                                grid.refreshGrid();
                            }
                        });
        			}
        		});
        	}
        }));

        tb.addButton(new ToolbarButton(new ButtonConfig() {
        	{
        		setText("Delete selected asset");
        		setButtonListener(new ButtonListenerAdapter() {
        			public void onClick(
        					com.gwtext.client.widgets.Button button,
        					EventObject e) {
                    	if (grid.getSelectedRowUUID() == null) {
                    		Window.alert("Please select an item to permanently delete.");
                    		return;
                    	}
                    	if (!Window.confirm("Are you sure you want to permanently delete this asset ? This can not be undone.")) {
                    		return;
                    	}
                        RepositoryServiceFactory.getService().removeAsset( grid.getSelectedRowUUID(), new GenericCallback() {

                            public void onSuccess(Object arg0) {
                                Window.alert( "Item deleted." );
                                grid.refreshGrid();
                            }
                        });
        			}
        		});
        	}
        }));
        pf.startSection("Archived assets");
        pf.addRow(tb);

        pf.addRow(grid);

        pf.endSection();


        initWidget( pf );
    }


	private void deletePackage(final String uuid) {
		RepositoryServiceFactory.getService().removePackage(uuid, new GenericCallback( ) {
			public void onSuccess(Object data) {
				Window.alert("Package deleted");
				packages.clear();
				loadPackages();
			}
		});
	}


	private void restorePackage(String uuid) {
		RepositoryServiceFactory.getService().loadPackageConfig(uuid, new GenericCallback() {
			public void onSuccess(Object data) {
				PackageConfigData cf = (PackageConfigData) data;
				cf.archived = false;
				RepositoryServiceFactory.getService().savePackage(cf, new GenericCallback() {
					public void onSuccess(Object data) {
						Window.alert("Package restored.");
						packages.clear();
						loadPackages();
					}
				});
			}
		});
	}



    private ListBox loadPackages() {

    	RepositoryServiceFactory.getService().listArchivedPackages(new GenericCallback() {
			public void onSuccess(Object data) {
				PackageConfigData[] configs = (PackageConfigData[]) data;
				for (int i = 0; i < configs.length; i++) {
						packages.addItem(configs[i].name, configs[i].uuid);
				}
				if (configs.length == 0) {
					packages.addItem("-- no archived packages --");
				}
			}
    	});


		return packages;
	}



}