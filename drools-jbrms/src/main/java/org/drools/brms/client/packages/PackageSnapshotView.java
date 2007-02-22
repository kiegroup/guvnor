package org.drools.brms.client.packages;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
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
        service.listRulePackages( new GenericCallback() {
            public void onSuccess(Object data) {
                String[] list = (String[]) data;
                addPackages(list);
            }            
        });
    }

    private void addPackages(final String[] list) {
        VerticalPanel packages = new VerticalPanel();
        for ( int i = 0; i < list.length; i++ ) {
            final String pkgName = list[i];
            
            
            HTML pkg = new HTML("<img src = 'images/package.gif'/>&nbsp;" + pkgName);
            
            pkg.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    showPackage(pkgName);
                }                
            });
            packages.add( pkg );
        }
        
        Image refresh = new Image("images/refresh.gif");
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                refreshPackageList();
            }            
        });
        
        layout.setWidget( 0, 0, packages );
    }

    /**
     * This will load up the list of snapshots for a package.
     */
    private void showPackage(String pkgName) {
        service.listSnapshots( pkgName, new GenericCallback() {
            public void onSuccess(Object data) {
                String[] list = (String[]) data;
            }
        });
    }

}
