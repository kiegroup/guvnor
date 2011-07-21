package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.util.Activity;

import java.util.Arrays;

public class ModuleFormatsGridPlace extends Activity {

    private org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace moduleFormatsGridPlace;

    public ModuleFormatsGridPlace( org.drools.guvnor.client.explorer.navigation.ModuleFormatsGridPlace moduleFormatsGridPlace ) {
        this.moduleFormatsGridPlace = moduleFormatsGridPlace;
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
    public void start( AcceptTabItem tabbedPanel, EventBus eventBus ) {
        TabManager tabManager = TabContainer.getInstance();
        RulePackageSelector.currentlySelectedPackage = moduleFormatsGridPlace.getPackageConfigData().getName();

        tabManager.openPackageViewAssets( moduleFormatsGridPlace.getPackageConfigData().getUuid(),
                moduleFormatsGridPlace.getPackageConfigData().getName(),
                key(),
                moduleFormatsGridPlace.getFormats().length == 0 ? null : Arrays.asList( moduleFormatsGridPlace.getFormats() ),
                moduleFormatsGridPlace.getFormats().length == 0 ? Boolean.TRUE : null,
                moduleFormatsGridPlace.getTitle() );
    }
}
