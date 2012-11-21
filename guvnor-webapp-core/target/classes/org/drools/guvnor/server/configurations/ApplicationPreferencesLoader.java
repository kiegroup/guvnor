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

import org.drools.core.util.DateUtils;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.repository.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationPreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger(ApplicationPreferencesLoader.class);

    public static Map<String, String> load() {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = ApplicationPreferencesLoader.class.getResourceAsStream("/preferences.properties");
            properties.load(in);

            return readPreferences(properties);
        } catch (IOException e) {
            log.info("Couldn't find preferences.properties - using defaults");
            return new HashMap<String, String>();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private static Map<String, String> readPreferences(Properties properties) {
        Map<String, String> preferences = getDefaultPreferences();

        addPreferencesFromProperties(properties, preferences);

        return preferences;
    }

    private static void addPreferencesFromProperties(Properties properties, Map<String, String> preferences) {
        for (Object key : properties.keySet()) {
            String feature = (String) key;

            preferences.put(feature, properties.getProperty(feature));
        }
    }

    private static Map<String, String> getDefaultPreferences() {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put(ApplicationPreferences.DATE_FORMAT, DateUtils.getDateFormatMask());
        preferences.put(ApplicationPreferences.DEFAULT_LANGUAGE, System.getProperty(ApplicationPreferences.DEFAULT_LANGUAGE));
        preferences.put(ApplicationPreferences.DEFAULT_COUNTRY, System.getProperty(ApplicationPreferences.DEFAULT_COUNTRY));

        // For security Serialization we DO NOT want to set any default
        // as those can be set through other means and we don't want
        // to override or mess with that

        return preferences;
    }
}
