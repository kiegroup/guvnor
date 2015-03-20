package org.guvnor.structure.backend.backcompat;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigurationFactory;

@ApplicationScoped
public class BackwardCompatibleUtil {

    private ConfigurationFactory configurationFactory;

    public BackwardCompatibleUtil() {
    }

    @Inject
    public BackwardCompatibleUtil( ConfigurationFactory configurationFactory ) {
        this.configurationFactory = configurationFactory;
    }

    public ConfigGroup compat( final ConfigGroup configGroup ) {
        if ( configGroup != null ) {
            final ConfigItem<List<String>> roles = configGroup.getConfigItem( "security:roles" );
            if ( roles != null && !roles.getValue().isEmpty() ) {
                configGroup.addConfigItem( configurationFactory.newConfigItem( "security:groups", new ArrayList<String>( roles.getValue() ) ) );
            }
            configGroup.removeConfigItem( "security:roles" );
        }
        return configGroup;
    }
}
