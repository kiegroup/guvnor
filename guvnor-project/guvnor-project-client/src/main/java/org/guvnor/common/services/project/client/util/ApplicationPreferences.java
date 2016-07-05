/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.client.util;

import java.util.Collections;
import java.util.Map;

import org.guvnor.common.services.project.service.ProjectRepositoryResolver;

/**
 * Storage for global preferences. Preferences effect behaviour and display.
 */
public class ApplicationPreferences {

    private static ApplicationPreferences instance = new ApplicationPreferences( Collections.<String, String>emptyMap() );

    private Map<String, String> preferences = Collections.<String, String>emptyMap();

    private ApplicationPreferences( Map<String, String> preferences ) {
        this.preferences = preferences;
    }

    public static void setUp( Map<String, String> map ) {
        instance = new ApplicationPreferences( map );
    }

    public static boolean isChildGAVEditEnabled() {
        return Boolean.parseBoolean( getPreference( ProjectRepositoryResolver.CHILD_GAV_EDIT_ENABLED ) );
    }

    private static String getPreference( final String key ) {
        return instance.preferences.get( key );
    }
}
