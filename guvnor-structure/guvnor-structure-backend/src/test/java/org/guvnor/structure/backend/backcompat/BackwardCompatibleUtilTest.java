package org.guvnor.structure.backend.backcompat;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackwardCompatibleUtilTest {

    @Test
    public void backwardCompatTest() {
        final ConfigurationFactory factory = new ConfigurationFactoryImpl();
        final BackwardCompatibleUtil backwardUtil = new BackwardCompatibleUtil( factory );

        assertNull( backwardUtil.compat( null ) );

        final ConfigGroup group1 = factory.newConfigGroup( ConfigType.PROJECT, "cool", "test" );
        assertNotNull( backwardUtil.compat( group1 ) );
        assertNull( backwardUtil.compat( group1 ).getConfigItem( "security:groups" ) );

        group1.addConfigItem( factory.newConfigItem( "security:groups", new ArrayList() {{
            add( "group1" );
        }} ) );
        assertNotNull( backwardUtil.compat( group1 ).getConfigItem( "security:groups" ) );
        assertTrue( ( (List<String>) ( backwardUtil.compat( group1 ).getConfigItem( "security:groups" ) ).getValue() ).size() == 1 );

        final ConfigGroup group2 = factory.newConfigGroup( ConfigType.PROJECT, "cool2", "test2" );
        assertNotNull( backwardUtil.compat( group2 ) );
        assertNull( backwardUtil.compat( group2 ).getConfigItem( "security:groups" ) );

        group2.addConfigItem( factory.newConfigItem( "security:roles", new ArrayList() {{
            add( "group1" );
        }} ) );
        assertNotNull( backwardUtil.compat( group2 ).getConfigItem( "security:groups" ) );
        assertTrue( ( (List<String>) ( backwardUtil.compat( group2 ).getConfigItem( "security:groups" ) ).getValue() ).size() == 1 );
        assertNull( backwardUtil.compat( group2 ).getConfigItem( "security:roles" ) );
    }

}
