package org.drools.brms.client.packages;

import java.util.ArrayList;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This contains a list of packages and their deployment snapshots.
 * 
 * @author Michael Neale
 */
public class PackageSnapshotView extends Composite {
    
    private RepositoryServiceAsync service;
    private FlexTable layout;

    public PackageSnapshotView() {
        
        layout = new FlexTable();
        layout.getCellFormatter().setWidth( 0, 0, "40%" );
        
        
        service = RepositoryServiceFactory.getService();
        
        refreshPackageList();
        
        
        initWidget( layout );
        
        
    }

    private void refreshPackageList() {
        LoadingPopup.showMessage( "Loading package list..." );
        service.listPackages( new GenericCallback() {
            public void onSuccess(Object data) {
                PackageConfigData[] list = (PackageConfigData[]) data;
                addPackages(list);
                LoadingPopup.close();
            }            
        });
    }

    private void addPackages(final PackageConfigData[] list) {
        
        Tree snapTree = new Tree();
        
        VerticalPanel packages = new VerticalPanel();
        for ( int i = 0; i < list.length; i++ ) {
            final String pkgName = list[i].name;
            TreeItem item  = makeItem( pkgName, "images/package_snapshot.gif", new Command() {
                public void execute() {
                    showPackage(pkgName);
                }
            } );


            snapTree.addItem( item );

        }
        
        packages.add( snapTree );
        
        HTML refresh = new HTML("Refresh list:&nbsp;<img src='images/refresh.gif'/>");
        
        //Image refresh = new Image("images/refresh.gif");
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                refreshPackageList();
            }            
        });
        
        snapTree.addTreeListener( new TreeListener() {
            public void onTreeItemSelected(TreeItem item) {
                DeferredCommand.add( (Command) item.getUserObject() );
            }
            public void onTreeItemStateChanged(TreeItem a) {}
        });
        
        packages.add( refresh );
        
        layout.setWidget( 0, 0, packages );
    }

    /**
     * This will load up the list of snapshots for a package.
     */
    private void showPackage(String pkgName) {
        LoadingPopup.showMessage( "Loading snapshots..." );
        service.listSnapshots( pkgName, new GenericCallback() {
            public void onSuccess(Object data) {
                SnapshotInfo[] list = (SnapshotInfo[]) data;
                
                renderListOfSnapshots(list);
                LoadingPopup.close();
            }
        });
    }
    
    /**
     * This will render the snapshot list.
     */
    protected void renderListOfSnapshots(SnapshotInfo[] list) {
        FlexTable table = new FlexTable();
        for ( int i = 0; i < list.length; i++ ) {
            Label name = new Label( list[i].name );
            table.setWidget( i, 0,  new Image("images/package_snapshot_item.gif"));
            table.setWidget( i, 1, name );
            table.setWidget( i, 2, new Label(list[i].comment) );
            
        }
        layout.setWidget( 0, 1, table );
    }

    private TreeItem makeItem(String name, String icon, Object command) {
        TreeItem item = new TreeItem();
        item.setHTML( "<img src=\""+ icon + "\">" + name + "</a>" );
        item.setUserObject( command );
        return item;
    }

}
