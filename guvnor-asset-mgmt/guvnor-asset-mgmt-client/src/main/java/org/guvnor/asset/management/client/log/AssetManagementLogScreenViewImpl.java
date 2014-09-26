package org.guvnor.asset.management.client.log;

import com.google.gwt.user.client.ui.SimplePanel;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;

public class AssetManagementLogScreenViewImpl
        extends SimplePanel
        implements AssetManagementLogScreenView {


    @Override
    public void init(SimpleSocialTimelineWidgetModel model) {
        add(new SimpleSocialTimelineWidget(model));
    }
}
