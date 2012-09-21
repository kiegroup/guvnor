package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.widgets.tables.StatePagedTable;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

//@Dependent
//@WorkbenchScreen(identifier = "stateScreen")
public class StateActivity {

    private final String stateName;
    private final ClientFactory clientFactory;

    @Inject
    PlaceManager placeManager;

    @Inject
    public StateActivity(ClientFactory clientFactory) {
        this.stateName = placeManager.getCurrentPlaceRequest().getParameters().get("state");
        this.clientFactory = clientFactory;
    }

    @WorkbenchPartView
    public Widget asWidget() {
        final StatePagedTable table = new StatePagedTable(
                stateName,
                clientFactory);

        final ServerPushNotification push = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if (response.messageType.equals("statusChange")
                        && (response.message).equals(stateName)) {
                    table.refresh();
                }
            }
        };
        PushClient.instance().subscribe(push);
        table.addUnloadListener(new Command() {
            public void execute() {
                PushClient.instance().unsubscribe(push);
            }
        });

        return table;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.Status() + stateName;
    }

}
