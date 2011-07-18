package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;
import org.drools.guvnor.client.util.Activity;

import java.util.Arrays;

public class ModuleFormatsGridPlace extends Activity {

    private ModuleFormatsGrid moduleFormatsGrid;

    public ModuleFormatsGridPlace( ModuleFormatsGrid moduleFormatsGrid ) {
        this.moduleFormatsGrid = moduleFormatsGrid;
    }


    private String key() {
        StringBuilder keyBuilder = new StringBuilder( moduleFormatsGrid.getPackageConfigData().getUuid() );
        if ( moduleFormatsGrid.getFormats().length == 0 ) {
            keyBuilder.append( "[0]" );
        } else {
            for (String format : moduleFormatsGrid.getFormats()) {
                keyBuilder.append( format );
            }
        }
        return keyBuilder.toString();
    }

    @Override
    public void start( AcceptTabItem tabbedPanel, EventBus eventBus ) {
        TabManager tabManager = TabContainer.getInstance();
        RulePackageSelector.currentlySelectedPackage = moduleFormatsGrid.getPackageConfigData().getName();

        tabManager.openPackageViewAssets( moduleFormatsGrid.getPackageConfigData().getUuid(),
                moduleFormatsGrid.getPackageConfigData().getName(),
                key(),
                moduleFormatsGrid.getFormats().length == 0 ? null : Arrays.asList( moduleFormatsGrid.getFormats() ),
                moduleFormatsGrid.getFormats().length == 0 ? Boolean.TRUE : null,
                moduleFormatsGrid.getTitle() );
    }
}
