package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.Util;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;

import java.util.Arrays;
import java.util.List;

public class ModuleFormatsGridPlace extends Activity {

    private org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace moduleFormatsGridPlace;
    private final ClientFactory clientFactory;

    public ModuleFormatsGridPlace(org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace moduleFormatsGridPlace,
                                  ClientFactory clientFactory) {
        this.moduleFormatsGridPlace = moduleFormatsGridPlace;
        this.clientFactory = clientFactory;
    }


    private String key() {
        StringBuilder keyBuilder = new StringBuilder( moduleFormatsGridPlace.getPackageConfigData().getUuid() );
        if ( moduleFormatsGridPlace.getFormats().length == 0 ) {
            keyBuilder.append( "[0]" );
        } else {
            for (String format : moduleFormatsGridPlace.getFormats()) {
                keyBuilder.append( format );
            }
        }
        return keyBuilder.toString();
    }

    @Override
    public void start(AcceptItem tabbedPanel,
                      EventBus eventBus) {
        RulePackageSelector.currentlySelectedPackage = moduleFormatsGridPlace.getPackageConfigData().getName();

        openPackageViewAssets(
                tabbedPanel,
                moduleFormatsGridPlace.getPackageConfigData().getUuid(),
                moduleFormatsGridPlace.getPackageConfigData().getName(),
                key(),
                moduleFormatsGridPlace.getFormats().length == 0 ? null : Arrays.asList( moduleFormatsGridPlace.getFormats() ),
                moduleFormatsGridPlace.getFormats().length == 0 ? Boolean.TRUE : null,
                moduleFormatsGridPlace.getTitle() );
    }

    private void openPackageViewAssets(AcceptItem tabbedPanel,
                                       final String packageUuid,
                                       final String packageName,
                                       String key,
                                       final List<String> formatInList,
                                       Boolean formatIsRegistered,
                                       final String itemName) {
        String feedUrl = GWT.getModuleBaseURL()
                + "feed/package?name="
                + packageName
                + "&viewUrl="
                + Util.getSelfURL()
                + "&status=*";
        final AssetPagedTable table = new AssetPagedTable(
                packageUuid,
                formatInList,
                formatIsRegistered,
                feedUrl,
                clientFactory );
        tabbedPanel.add( itemName
                + " ["
                + packageName
                + "]",
                table );

        final ServerPushNotification sub = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( response.messageType.equals( "packageChange" )
                        && response.message.equals( packageName ) ) {
                    table.refresh();
                }
            }
        };
        PushClient.instance().subscribe( sub );
        table.addUnloadListener( new Command() {
            public void execute() {
                PushClient.instance().unsubscribe( sub );
            }
        } );
    }

}
