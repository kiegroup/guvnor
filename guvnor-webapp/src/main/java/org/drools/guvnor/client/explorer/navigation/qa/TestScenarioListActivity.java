package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AcceptTabItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.util.Activity;

public class TestScenarioListActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final ClientFactory clientFactory;
    private final String moduleUuid;

    public TestScenarioListActivity(String moduleUuid,
                                    ClientFactory clientFactory) {
        this.moduleUuid = moduleUuid;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptTabItem tabbedPanel, EventBus eventBus) {
        openTestScenario( tabbedPanel );
    }

    public void openTestScenario(final AcceptTabItem tabbedPanel) {

        clientFactory.getPackageService().loadPackageConfig(
                moduleUuid,
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess(PackageConfigData packageConfigData) {

                        tabbedPanel.addTab(
                                constants.ScenariosForPackage( packageConfigData.getName() ),
                                new ScenarioPackageScreen(
                                        packageConfigData.getUuid(),
                                        packageConfigData.getName(),
                                        clientFactory ) );
                    }
                } );

    }
}
