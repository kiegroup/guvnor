package org.drools.brms.client.packages;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * This view is a tabbed browser for package management.
 * The first tab always shows the list of packages in tree form, 
 * with a list/explorer like motif.
 * 
 * Each editor that is opened is opened in a new tab.
 * 
 * @author Michael Neale
 *
 */
public class PackageManagerView extends Composite {

    private final TabPanel tab;
    
    public PackageManagerView() {
        tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("100%");   
        
        PackageExplorerWidget explorer = new PackageExplorerWidget();
        tab.add( explorer,  "Explore");
        
        tab.selectTab( 0 );
        
        initWidget( tab );
    }
    
}
