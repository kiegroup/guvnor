package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.AcceptTabItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.tables.InboxIncomingPagedTable;
import org.drools.guvnor.client.widgets.tables.InboxPagedTable;

public class InboxActivity extends Activity {

    private final InboxPlace place;
    private final ClientFactory clientFactory;

    public InboxActivity(InboxPlace place,
                         ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptTabItem tabbedPanel, EventBus eventBus) {
        if ( ExplorerNodeConfig.INCOMING_ID.equals( place.getInboxName() ) ) {
            openInboxIncomingPagedTable(
                    tabbedPanel,
                    place.getInboxName() );
        } else {
            openInboxPagedTable(
                    tabbedPanel,
                    place.getInboxName() );
        }
    }

    private void openInboxIncomingPagedTable(AcceptTabItem tabbedPanel,
                                             String title) {
        tabbedPanel.addTab(
                title,
                new InboxIncomingPagedTable(
                        title,
                        clientFactory ) );
    }

    private void openInboxPagedTable(AcceptTabItem tabbedPanel,
                                     String title) {
        tabbedPanel.addTab(
                title,
                new InboxPagedTable(
                        title,
                        clientFactory ) );
    }
}
