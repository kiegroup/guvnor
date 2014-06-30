package org.guvnor.structure.backend.config;

import com.thoughtworks.xstream.XStream;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.SecureConfigItem;

/**
 * Marshall a ConfigGroup to and from XML
 */
public class ConfigGroupMarshaller {

    private final XStream backwardCompatibleXstream = new XStream();
    private final XStream xstream = new XStream();

    public ConfigGroupMarshaller() {
        backwardCompatibleXstream.alias( "group",
                       ConfigGroup.class );
        backwardCompatibleXstream.alias( "item",
                       ConfigItem.class );
        backwardCompatibleXstream.alias( "type",
                       ConfigType.class );
        backwardCompatibleXstream.alias("secureitem",
                       SecureConfigItem.class);
        // for backward compatibility only
        backwardCompatibleXstream.alias("org.uberfire.backend.server.config.SecureConfigItem",
                       SecureConfigItem.class);

        xstream.alias( "group",
                ConfigGroup.class );
        xstream.alias( "item",
                ConfigItem.class );
        xstream.alias( "type",
                ConfigType.class );
        xstream.alias("secureitem",
                SecureConfigItem.class);
    }

    public String marshall( final ConfigGroup configGroup ) {
        return xstream.toXML( configGroup );
    }

    public ConfigGroup unmarshall( final String xml ) {
        return (ConfigGroup) backwardCompatibleXstream.fromXML( xml );
    }

}
