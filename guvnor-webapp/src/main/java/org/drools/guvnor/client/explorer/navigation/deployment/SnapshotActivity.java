package org.drools.guvnor.client.explorer.navigation.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.AcceptTabItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.util.Activity;

public class SnapshotActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final ClientFactory clientFactory;
    private final String moduleName;
    private final String snapshotName;

    public SnapshotActivity(String moduleName,
                            String snapshotName,
                            ClientFactory clientFactory) {
        this.moduleName = moduleName;
        this.snapshotName = snapshotName;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(final AcceptTabItem tabbedPanel, EventBus eventBus) {
        clientFactory.getPackageService().loadSnapshotInfo(
                moduleName,
                snapshotName,
                new GenericCallback<SnapshotInfo>() {
                    public void onSuccess(SnapshotInfo snapshotInfo) {
                        showTab( tabbedPanel, snapshotInfo );
                    }
                } );
    }

    private void showTab(final AcceptTabItem tabbedPanel, final SnapshotInfo snapshotInfo) {

        LoadingPopup.showMessage( constants.LoadingSnapshot() );

        RepositoryServiceFactory.getPackageService().loadPackageConfig( snapshotInfo.getUuid(),
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess(PackageConfigData conf) {
                        tabbedPanel.addTab( constants.SnapshotLabel( snapshotInfo.getName() ),
                                new SnapshotView(
                                        clientFactory,
                                        snapshotInfo,
                                        conf ) );
                        LoadingPopup.close();
                    }
                } );

    }

}
