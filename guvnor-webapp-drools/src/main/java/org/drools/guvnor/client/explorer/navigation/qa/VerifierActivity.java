package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.util.Activity;

public class VerifierActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final String moduleUuid;
    private final ClientFactory clientFactory;

    public VerifierActivity(String moduleUuid,
                            ClientFactory clientFactory) {
        this.moduleUuid = moduleUuid;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel, EventBus eventBus) {
        openVerifierView( tabbedPanel );
    }

    public void openVerifierView(final AcceptItem tabbedPanel) {

        clientFactory.getModuleService().loadModule(
                moduleUuid,
                new GenericCallback<Module>() {
                    public void onSuccess(Module packageConfigData) {
                        tabbedPanel.add(
                                constants.AnalysisForPackage( packageConfigData.getName() ),
                                new VerifierScreen(
                                        packageConfigData.getUuid(),
                                        packageConfigData.getName() ) );
                    }
                } );

    }

}
