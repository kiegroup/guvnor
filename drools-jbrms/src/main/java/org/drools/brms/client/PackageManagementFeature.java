package org.drools.brms.client;

import org.drools.brms.client.packages.PackageManagerView;

/**
 * This is the package management feature. 
 * For managing packages (namespaces, imports etc) for rule assets.
 * 
 * This is also an alternative way of viewing packages.
 */
public class PackageManagementFeature extends JBRMSFeature {


    public static ComponentInfo init() {
        return new ComponentInfo( "Packages",
                                  "Configure and view packages of business rule assets." ) {
            public JBRMSFeature createInstance() {
                return new PackageManagementFeature();
            }
        };
    }


    public PackageManagementFeature() {
        initWidget( new PackageManagerView() );
    }




    public void onShow() {
    }
}
