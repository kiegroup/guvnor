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

package org.guvnor.common.services.project.preferences;

import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "GAVPreferences",
        bundleKey = "GAVPreferences.Label",
        parents = "GeneralPreferences")
public class GAVPreferences implements BasePreference<GAVPreferences> {

    static final String CONFLICTING_GAV_CHECK_DISABLED = "org.guvnor.project.gav.check.disabled";
    static final String CHILD_GAV_EDIT_ENABLED = "org.guvnor.project.gav.child.edit.enabled";

    @Property(bundleKey = "GAVPreferences.ConflictingGAVCheckDisabled.Label",
            formType = PropertyFormType.BOOLEAN)
    private boolean conflictingGAVCheckDisabled;

    @Property(bundleKey = "GAVPreferences.ChildGAVEditEnabled.Label",
            formType = PropertyFormType.BOOLEAN)
    private boolean childGAVEditEnabled;

    @Override
    public GAVPreferences defaultValue( final GAVPreferences defaultValue ) {
        final String conflictingGAVCheckDisabledSystemProperty = System.getProperty( CONFLICTING_GAV_CHECK_DISABLED, "false" );
        final boolean conflictingGAVCheckDisabled = Boolean.parseBoolean( conflictingGAVCheckDisabledSystemProperty );

        defaultValue.setConflictingGAVCheckDisabled( conflictingGAVCheckDisabled );

        final String childGAVEditEnabledSystemProperty = System.getProperty( CHILD_GAV_EDIT_ENABLED, "false" );
        final boolean childGAVEditEnabled = Boolean.parseBoolean( childGAVEditEnabledSystemProperty );

        defaultValue.setChildGAVEditEnabled( childGAVEditEnabled );

        return defaultValue;
    }

    public boolean isConflictingGAVCheckDisabled() {
        return conflictingGAVCheckDisabled;
    }

    public void setConflictingGAVCheckDisabled( final boolean conflictingGAVCheckDisabled ) {
        this.conflictingGAVCheckDisabled = conflictingGAVCheckDisabled;
    }

    public boolean isChildGAVEditEnabled() {
        return childGAVEditEnabled;
    }

    public void setChildGAVEditEnabled( final boolean childGAVEditEnabled ) {
        this.childGAVEditEnabled = childGAVEditEnabled;
    }
}
