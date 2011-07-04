/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.configurations;

import org.drools.core.util.KeyStoreHelper;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ApplicationPreferencesInitializerTest {

    private Map<String, String> oldValues;

    @Before
    public void setUp() throws Exception {
        oldValues = new HashMap<String, String>();

        storeOldValue( ApplicationPreferences.DATE_FORMAT );
        storeOldValue( ApplicationPreferences.DEFAULT_LANGUAGE );
        storeOldValue( ApplicationPreferences.DEFAULT_COUNTRY );

        storeOldValue( KeyStoreHelper.PROP_SIGN );
        storeOldValue( KeyStoreHelper.PROP_PVT_KS_URL );
        storeOldValue( KeyStoreHelper.PROP_PVT_KS_PWD );
        storeOldValue( KeyStoreHelper.PROP_PVT_ALIAS );
        storeOldValue( KeyStoreHelper.PROP_PVT_PWD );
        storeOldValue( KeyStoreHelper.PROP_PUB_KS_URL );
        storeOldValue( KeyStoreHelper.PROP_PUB_KS_PWD );
    }

    private void storeOldValue( String propertyName ) {
        oldValues.put( propertyName, System.getProperty( propertyName ) );
        System.clearProperty( propertyName );
    }

    @After
    public void tearDown() throws Exception {
        for (String key : oldValues.keySet()) {
            String value = oldValues.get( key );
            if ( value == null ) {
                System.clearProperty( key );
            } else {
                System.setProperty( key, value );
            }
        }
    }

    @Test
    public void testEmptySetOfProperties() throws Exception {
        HashMap<String, String> preferences = new HashMap<String, String>();

        ApplicationPreferencesInitializer.setSystemProperties( preferences );

        assertNull( System.getProperty( ApplicationPreferences.DATE_FORMAT ) );
        assertNull( System.getProperty( ApplicationPreferences.DEFAULT_LANGUAGE ) );
        assertNull( System.getProperty( ApplicationPreferences.DEFAULT_COUNTRY ) );
    }

    @Test
    public void testDroolsCoreProperties() throws Exception {
        HashMap<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "date" );
        preferences.put( ApplicationPreferences.DEFAULT_LANGUAGE, "language" );
        preferences.put( ApplicationPreferences.DEFAULT_COUNTRY, "country" );

        ApplicationPreferencesInitializer.setSystemProperties( preferences );

        assertEquals( System.getProperty( ApplicationPreferences.DATE_FORMAT ), "date" );
        assertEquals( System.getProperty( ApplicationPreferences.DEFAULT_LANGUAGE ), "language" );
        assertEquals( System.getProperty( ApplicationPreferences.DEFAULT_COUNTRY ), "country" );
    }
}
