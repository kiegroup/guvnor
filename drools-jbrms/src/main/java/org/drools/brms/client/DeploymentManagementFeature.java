package org.drools.brms.client;

import org.drools.brms.client.packages.PackageSnapshotView;

/**
 * This is the package management feature. 
 * For managing packages (namespaces, imports etc) for rule assets.
 * 
 * This is also an alternative way of viewing packages.
 */
public class DeploymentManagementFeature extends JBRMSFeature {


    public static ComponentInfo init() {
        return new ComponentInfo( "Deployment",
                                  "Configure and view frozen snapshots of packages." ) {
            public JBRMSFeature createInstance() {
                return new DeploymentManagementFeature();
            }
        };
    }


    public DeploymentManagementFeature() {
        initWidget( new PackageSnapshotView() );
    }




    public void onShow() {
    }
}
