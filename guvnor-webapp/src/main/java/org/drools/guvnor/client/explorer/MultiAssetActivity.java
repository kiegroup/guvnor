package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.util.Activity;

public class MultiAssetActivity extends Activity {

    private final MultiAssetPlace place;
    private final ClientFactory clientFactory;

    public MultiAssetActivity(MultiAssetPlace place,
                              ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptTabItem tabbedPanel, EventBus eventBus) {
        MultiAssetView view = clientFactory.getNavigationViewFactory().getMultiAssetView();


        view.init(
                place.getMultiViewRows().toArray( new MultiViewRow[place.getMultiViewRows().size()] ),
                clientFactory );
//        addRows( view );

        tabbedPanel.addTab( getTitle(), view );
    }

    private String getTitle() {
        boolean first = true;
        String title = "[ ";
        for (MultiViewRow multiViewRow : place.getMultiViewRows()) {
            if ( first ) {
                title += multiViewRow.getName();
                first = false;
            } else {
                title += ", " + multiViewRow.getName();
            }
        }
        title += " ]";
        return title;
    }

}
