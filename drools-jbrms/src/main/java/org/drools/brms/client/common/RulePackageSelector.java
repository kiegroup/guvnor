package org.drools.brms.client.common;

import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A rule package selector widget.
 * @author michael neale 
 */
public class RulePackageSelector extends Composite {

    private ListBox packageList;
    
    public RulePackageSelector() {
        packageList = new ListBox();
        
        RepositoryServiceFactory.getService().listPackages( new AsyncCallback() {

            public void onFailure(Throwable arg0) {
                ErrorPopup.showMessage( "Unable to load list of packages." );                
            }

            public void onSuccess(Object o) {
                PackageConfigData[] list = (PackageConfigData[]) o;

                for ( int i = 0; i < list.length; i++ ) {
                    packageList.addItem( list[i].name );
                }
                
            }
            
        });
        
        initWidget( packageList );
    }
    
    /**
     * Returns the selected package.
     */
    public String getSelectedPackage() {
        return packageList.getItemText( packageList.getSelectedIndex() );
    }
    
}
