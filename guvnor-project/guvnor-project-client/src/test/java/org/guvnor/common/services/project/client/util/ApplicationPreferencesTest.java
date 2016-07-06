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

import java.util.HashMap;

import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationPreferencesTest {

    @Test
    public void testIsChildGAVEditEnabledDefaultValue() {
        setChildGAVEdit( "" );
        assertFalse( ApplicationPreferences.isChildGAVEditEnabled() );
    }

    @Test
    public void testIsChildGAVEditEnabledSetUp() {
        setChildGAVEdit( "true" );
        assertTrue( ApplicationPreferences.isChildGAVEditEnabled() );
    }

    private void setChildGAVEdit( final String value ) {
        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ProjectRepositoryResolver.CHILD_GAV_EDIT_ENABLED, value );
        }} );
    }
}
