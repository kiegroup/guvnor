/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.services.config;

import java.util.Map;

/**
 * Storage for global preferences. Preferences effect behaviour and display.
 */
public class ApplicationPreferences {

    public static final String DATE_FORMAT      = "drools.dateformat";
    public static final String DATE_TIME_FORMAT = "drools.datetimeformat";
    public static final String DEFAULT_LANGUAGE = "drools.defaultlanguage";
    public static final String DEFAULT_COUNTRY  = "drools.defaultcountry";

    public static final String DESIGNER_URL     = "designer.url";
    public static final String DESIGNER_CONTEXT = "designer.context";
    public static final String DESIGNER_PROFILE = "designer.profile";

    public static final String FORMBUILDER_URL     = "formbuilder.url";
    public static final String FORMBUILDER_CONTEXT = "formbuilder.context";
    public static final String FORMBUILDER_PROFILE = "formbuilder.profile";

    public static final String GUVNOR_URL       = "guvnor.url";
    public static final String SCORECARDS_ASSET = "asset.enabled.scorecards";

    public static ApplicationPreferences instance;
    private       Map<String, String>    preferences;

    private ApplicationPreferences( Map<String, String> preferences ) {
        this.preferences = preferences;
    }

    public static void setUp( Map<String, String> map ) {
        instance = new ApplicationPreferences( map );
    }

    public static boolean getBooleanPref( String name ) {
        return getBooleanPref( name,
                               false );
    }

    public static boolean getBooleanPref( String name,
                                          boolean defaultValue ) {
        if ( instance.preferences.containsKey( name ) ) {
            return Boolean.parseBoolean( instance.preferences.get( name ) );
        } else {
            return defaultValue;
        }
    }

    public static String getStringPref( String name ) {
        return instance.preferences.get( name );
    }

    public static boolean enabledFormat( String format ) {
        return getBooleanPref( "asset.format.enabled." + format,
                               true );
    }

    public static String getDroolsDateFormat() {
        return getStringPref( DATE_FORMAT );
    }

    public static String getDroolsDateTimeFormat() {
        return getStringPref( DATE_TIME_FORMAT );
    }

    public static boolean showVerifier() {
        return getBooleanPref( "verifier" );
    }

    public static boolean showVisualRuleFlow() {
        return getBooleanPref( "visual-ruleflow" );
    }

    public static String getDesignerURL() {
        return getStringPref( DESIGNER_URL );
    }

    public static String getDesignerContext() {
        return getStringPref( DESIGNER_CONTEXT );
    }

    public static String getDesignerProfile() {
        return getStringPref( DESIGNER_PROFILE );
    }

    public static String getFormBuilderURL() {
        return getStringPref( FORMBUILDER_URL );
    }

    public static String getFormBuilderContext() {
        return getStringPref( FORMBUILDER_CONTEXT );
    }

    public static String getFormBuilderProfile() {
        return getStringPref( FORMBUILDER_PROFILE );
    }

    public static String getGuvnorURL() {
        return getStringPref( GUVNOR_URL );
    }
}
