package org.guvnor.asset.management.client.log;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.service.SocialPredicate;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

import javax.inject.Inject;

@WorkbenchScreen(identifier = "org.guvnor.asset.management.LogScreen")
public class AssetManagementLogScreen {


    private AssetManagementLogScreenView view;

    @Inject
    public AssetManagementLogScreen(AssetManagementLogScreenView view, PlaceManager placeManager) {
        this.view = view;


        view.init(
                new SimpleSocialTimelineWidgetModel(
                        AssetManagementEventTypes.BRANCH_CREATED,
                        new SocialPredicate<SocialActivitiesEvent>() {
                            @Override
                            public boolean test(SocialActivitiesEvent socialActivitiesEvent) {
                                return false;
                            }
                        },
                        placeManager,
                        new SocialPaged(10)));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.AssetManagementLog();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }


}
