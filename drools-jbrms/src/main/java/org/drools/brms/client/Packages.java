package org.drools.brms.client;

import org.drools.brms.client.packages.PackageExplorerWidget;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * This is the package management feature. 
 * For managing packages (namespaces, imports etc) for rule assets.
 * 
 */
public class Packages extends JBRMSFeature {


    public static ComponentInfo init() {
        return new ComponentInfo( "Packages",
                                  "Configure and view packages of business rule assets." ) {
            public JBRMSFeature createInstance() {
                return new Packages();
            }
        };
    }


    public Packages() {
        final FlexTable layout = new FlexTable();
        
        layout.setWidget( 0, 0, new PackageExplorerWidget() );
        
        initWidget( layout );
    }




    public void onShow() {
    }
}
