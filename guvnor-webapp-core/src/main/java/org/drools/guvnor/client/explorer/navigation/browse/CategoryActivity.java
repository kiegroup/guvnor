package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.Util;
import org.drools.guvnor.client.widgets.tables.CategoryPagedTable;


public class CategoryActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );

    private final String categoryPath;
    private final ClientFactory clientFactory;

    public CategoryActivity(String categoryPath,
                            ClientFactory clientFactory) {
        this.categoryPath = categoryPath;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptItem tabbedPanel,
                      EventBus eventBus) {
        final CategoryPagedTable table = new CategoryPagedTable( categoryPath,
                GWT.getModuleBaseURL()
                        + "feed/category?name="
                        + categoryPath
                        + "&viewUrl="
                        + Util.getSelfURL(),
                clientFactory );
        final ServerPushNotification push = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( response.messageType.equals( "categoryChange" )
                        && response.message.equals( categoryPath ) ) {
                    table.refresh();
                }
            }
        };
        PushClient.instance().subscribe( push );
        table.addUnloadListener( new Command() {
            public void execute() {
                PushClient.instance().unsubscribe( push );
            }
        } );


        tabbedPanel.add(
                constants.CategoryColon() + subStringCategoryName( categoryPath ),
                table );
    }

    private String subStringCategoryName(String categoryPath) {
        return categoryPath.substring( categoryPath.lastIndexOf( "/" ) + 1 );
    }

}