package org.drools.brms.client.packages;

import java.util.ArrayList;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
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
        service.listRulePackages( new GenericCallback() {
            public void onSuccess(Object data) {
                String[] list = (String[]) data;
                addPackages(list);
                LoadingPopup.close();
            }            
        });
    }

    private void addPackages(final String[] list) {
        
        Tree snapTree = new Tree();
        
        VerticalPanel packages = new VerticalPanel();
        for ( int i = 0; i < list.length; i++ ) {
            final String pkgName = list[i];
            
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
                String[] list = (String[]) data;
                renderListOfSnapshots(list);
                LoadingPopup.close();
            }
        });
    }
    
    /**
     * This will render the snapshot list.
     */
    protected void renderListOfSnapshots(String[] list) {
        FlexTable table = new FlexTable();
        for ( int i = 0; i < list.length; i++ ) {
            table.setWidget( i, 0, new Label( list[i] ) );
            
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
