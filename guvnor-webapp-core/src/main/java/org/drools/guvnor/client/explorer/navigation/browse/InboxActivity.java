package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.widgets.tables.InboxPagedTable;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "inbox")
public class InboxActivity {

    private final ClientFactory clientFactory;
    private final PlaceManager placeManager;
    private String inboxType;

    @Inject
    public InboxActivity(PlaceManager placeManager, ClientFactory clientFactory) {
        this.placeManager = placeManager;
        this.clientFactory = clientFactory;
    }

    @OnStart
    public void init() {
        inboxType = placeManager.getCurrentPlaceRequest().getParameters().get("inboxType");
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return new InboxPagedTable(
                inboxType,
                clientFactory);
    }


    @WorkbenchPartTitle
    public String getTitle() {
        if (ExplorerNodeConfig.INCOMING_ID.equals(inboxType)) {
            return clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxIncomingName();
        } else if (ExplorerNodeConfig.RECENT_EDITED_ID.equals(inboxType)) {
            return clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxRecentEditedName();
        } else if (ExplorerNodeConfig.RECENT_VIEWED_ID.equals(inboxType)) {
            return clientFactory.getNavigationViewFactory().getBrowseTreeView().getInboxRecentViewedName();
        } else {
            return "";
        }
    }
}
