package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.Util;
import org.drools.guvnor.client.widgets.tables.CategoryPagedTable;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "categoryScreen")
public class CategoryActivity {

    private String categoryPath;
    private final ClientFactory clientFactory;
    private final PlaceManager placeManager;

    @Inject
    public CategoryActivity(PlaceManager placeManager, ClientFactory clientFactory) {
        this.placeManager = placeManager;
        this.clientFactory = clientFactory;
    }

    @OnStart
    public void init() {
        categoryPath = placeManager.getCurrentPlaceRequest().getParameters().get("category");
    }


    @WorkbenchPartView
    public Widget asWidget() {
        final CategoryPagedTable table = new CategoryPagedTable(categoryPath,
                GWT.getModuleBaseURL()
                        + "feed/category?name="
                        + categoryPath
                        + "&viewUrl="
                        + Util.getSelfURL(),
                clientFactory);
        final ServerPushNotification push = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if (response.messageType.equals("categoryChange")
                        && response.message.equals(categoryPath)) {
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

    private String subStringCategoryName(String categoryPath) {
        return categoryPath.substring(categoryPath.lastIndexOf("/") + 1);
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.CategoryColon() + subStringCategoryName(categoryPath);
    }

}