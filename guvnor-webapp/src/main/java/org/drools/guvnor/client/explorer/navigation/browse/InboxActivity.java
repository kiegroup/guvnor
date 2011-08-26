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
        if ( ExplorerNodeConfig.INCOMING_ID.equals( place.getInboxType() ) ) {
            openInboxIncomingPagedTable(
                    tabbedPanel,
                    clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxIncomingName(),
                    place.getInboxType() );
        } else if ( ExplorerNodeConfig.RECENT_EDITED_ID.equals( place.getInboxType() ) ) {
            openInboxPagedTable(
                    tabbedPanel,
                    clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxRecentEditedName(),
                    place.getInboxType() );
        } else if ( ExplorerNodeConfig.RECENT_VIEWED_ID.equals( place.getInboxType() ) ) {
            openInboxPagedTable(
                    tabbedPanel,
                    clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxRecentViewedName(),
                    place.getInboxType() );
        }
    }

    private void openInboxIncomingPagedTable(AcceptTabItem tabbedPanel,
                                             String title,
                                             String type) {
        tabbedPanel.addTab(
                title,
                new InboxIncomingPagedTable(
                        type,
                        clientFactory ) );
    }

    private void openInboxPagedTable(AcceptTabItem tabbedPanel,
                                     String title,
                                     String type) {
        tabbedPanel.addTab(
                title,
                new InboxPagedTable(
                        type,
                        clientFactory ) );
    }
}
