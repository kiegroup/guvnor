package org.drools.brms.client.packages;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.ruleeditor.EditorLauncher;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * This view is a tabbed browser for package management.
 * The first tab always shows the list of packages in tree form, 
 * with a list/explorer like motif.
 * 
 * This can also be specified to only show one package (ie when viewing a snapshot).
 * 
 * Each editor that is opened is opened in a new tab.
 * 
 * @author Michael Neale
 */
public class PackageManagerView extends Composite {

    private final TabPanel tab;
    private Map openedViewers = new HashMap();

    /**
     * This will provide a explorer for all the packages in the system,
     * not including snapshots.
     */
    public PackageManagerView() {
        this(null, null);
    }

    /**
     * This is used when you only want to view one package.
     * @param packageUUID The UUID of the package.
     */
    public PackageManagerView(String packageUUID, final String snapshotName) {
        tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("100%");
        
        EditItemEvent editEvent = new EditItemEvent() {
            public void open(String key) {
                EditorLauncher.showLoadEditor( openedViewers, tab, key, (snapshotName != null) );
            }
        };
        PackageExplorerWidget explorer = null;
        
        if (packageUUID == null) {
            explorer = new PackageExplorerWidget(editEvent);            
        } else {
            explorer = new PackageExplorerWidget(editEvent, packageUUID, snapshotName);
        }
        
        tab.add( explorer,  "Explore");        
        tab.selectTab( 0 );
        
        initWidget( tab );
    }
    
}
