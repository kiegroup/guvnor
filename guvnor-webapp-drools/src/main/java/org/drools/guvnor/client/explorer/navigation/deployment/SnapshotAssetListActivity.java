package org.drools.guvnor.client.explorer.navigation.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;

import java.util.Arrays;

public class SnapshotAssetListActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );
    private final ClientFactory clientFactory;
    private final SnapshotAssetListPlace place;

    public SnapshotAssetListActivity(SnapshotAssetListPlace place,
                                     ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel, EventBus eventBus) {
        tabbedPanel.add(
                constants.SnapshotItems(),
                getPanel() );
    }

    public VerticalPanel getPanel() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add( new HTML( "<i><small>"
                + constants.SnapshotListingFor()
                + place.getSnapshotName()
                + "</small></i>" ) );
        verticalPanel.add(
                new AssetPagedTable(
                        place.getModuleUuid(),
                        Arrays.asList( place.getAssetTypes() ),
                        null,
                        clientFactory ) );

        return verticalPanel;
    }
}
