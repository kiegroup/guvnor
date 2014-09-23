package org.guvnor.asset.management.client.log;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.inject.Inject;

@WorkbenchScreen(identifier = "org.guvnor.asset.management.LogScreen")
public class AssetManagementLogScreen {


    private AssetManagementLogScreenView view;

    @Inject
    public AssetManagementLogScreen(AssetManagementLogScreenView view) {
        this.view = view;
//        new SocialActivitiesEvent();
//        view.add();
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
