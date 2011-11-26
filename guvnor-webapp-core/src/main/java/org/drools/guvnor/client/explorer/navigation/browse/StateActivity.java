package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.tables.StatePagedTable;

public class StateActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final String stateName;
    private final ClientFactory clientFactory;

    public StateActivity(String stateName,
                         ClientFactory clientFactory) {
        this.stateName = stateName;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel, EventBus eventBus) {
        final StatePagedTable table = new StatePagedTable(
                stateName,
                clientFactory );

        final ServerPushNotification push = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( response.messageType.equals( "statusChange" )
                        && (response.message).equals( stateName ) ) {
                    table.refresh();
                }
            }
        };
        PushClient.instance().subscribe( push );
        table.addUnloadListener( new Command() {
            public void execute() {
                PushClient.instance().unsubscribe( push );
            }
        } );

        tabbedPanel.add(
                constants.Status() + stateName,
                table );
    }
}
