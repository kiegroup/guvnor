package org.guvnor.structure.server.config;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGroup {

    private String name;
    private String description;
    private ConfigType type;
    private boolean enabled;

    private Map<String, ConfigItem> items = new ConcurrentHashMap<String, ConfigItem>();

    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( final String description ) {
        this.description = description;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType( final ConfigType type ) {
        this.type = type;
    }

    public Collection<ConfigItem> getItems() {
        return items.values();
    }

    public void addConfigItem( final ConfigItem configItem ) {
        if ( !this.items.containsKey( configItem.getName() ) ) {
            setConfigItem( configItem );
        }
    }

    public void setConfigItem( final ConfigItem configItem ) {
        this.items.put( configItem.getName(), configItem );
    }

    public ConfigItem getConfigItem( final String name ) {
        return this.items.get( name );
    }

    public void removeConfigItem( final String name ) {
        this.items.remove( name );
    }

    public String getConfigItemValue( final String name ) {
        ConfigItem<String> configItem = this.items.get( name );
        if ( configItem == null ) {
            return null;
        } else {
            return configItem.getValue();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( final boolean enabled ) {
        this.enabled = enabled;
    }

}
