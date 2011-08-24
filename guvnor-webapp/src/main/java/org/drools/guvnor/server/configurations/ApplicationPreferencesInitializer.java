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

import java.util.Map;

public class ApplicationPreferencesInitializer {

    public static void setSystemProperties(Map<String, String> preferences) {
        setProperty(preferences, ApplicationPreferences.DATE_FORMAT);
        setProperty(preferences, ApplicationPreferences.DEFAULT_LANGUAGE);
        setProperty(preferences, ApplicationPreferences.DEFAULT_COUNTRY);
        
        setProperty(preferences, ApplicationPreferences.DESIGNER_URL);
        setProperty(preferences, ApplicationPreferences.DESIGNER_PROFILE);
        setProperty(preferences, ApplicationPreferences.DESIGNER_CONTEXT);

        setProperty(preferences, KeyStoreHelper.PROP_SIGN);
        setProperty(preferences, KeyStoreHelper.PROP_PVT_KS_URL);
        setProperty(preferences, KeyStoreHelper.PROP_PVT_KS_PWD);
        setProperty(preferences, KeyStoreHelper.PROP_PVT_ALIAS);
        setProperty(preferences, KeyStoreHelper.PROP_PVT_PWD);
        setProperty(preferences, KeyStoreHelper.PROP_PUB_KS_URL);
        setProperty(preferences, KeyStoreHelper.PROP_PUB_KS_PWD);
    }

    private static void setProperty(Map<String, String> preferences, String value) {
        if (preferences.containsKey(value)) {
            System.setProperty(value, preferences.get(value));
        }
    }
}
