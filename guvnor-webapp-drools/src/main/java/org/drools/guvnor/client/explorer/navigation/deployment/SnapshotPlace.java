package org.drools.guvnor.client.explorer.navigation.deployment;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public class SnapshotPlace extends DefaultPlaceRequest {

    public SnapshotPlace(String moduleName,
                         String snapshotName) {
        super("snapshotScreen");
        addParameter("moduleName", moduleName);
        addParameter("snapshotName", snapshotName);
    }

}
