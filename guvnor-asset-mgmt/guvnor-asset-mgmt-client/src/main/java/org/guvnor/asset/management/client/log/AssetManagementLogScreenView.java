package org.guvnor.asset.management.client.log;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;

public interface AssetManagementLogScreenView
        extends IsWidget {

    public void init(SimpleSocialTimelineWidgetModel model);

}

