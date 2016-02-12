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

package org.guvnor.common.services.project.backend.server;

import java.util.Map;

import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GAVCheckPreferencesLoaderTest {

    private String oldPropertyValue;

    private GAVCheckPreferencesLoader loader;

    @Before
    public void setup() {
        oldPropertyValue = System.getProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED );
        loader = new GAVCheckPreferencesLoader();
    }

    @After
    public void reset() {
        if ( oldPropertyValue != null ) {
            System.setProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED,
                                oldPropertyValue );
        } else {
            System.clearProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED );
        }
    }

    @Test
    public void testWithoutSystemProperty() {
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 0,
                      results.size() );
    }

    @Test
    public void testWithSystemPropertyTrue() {
        System.setProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED,
                            "true" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertTrue( Boolean.parseBoolean( results.get( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED ) ) );
    }

    @Test
    public void testWithSystemPropertyFalse() {
        System.setProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED,
                            "false" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertFalse( Boolean.parseBoolean( results.get( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED ) ) );
    }

    @Test
    public void testWithSystemPropertyDuffValue() {
        System.setProperty( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED,
                            "cheese" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertFalse( Boolean.parseBoolean( results.get( ProjectRepositoryResolver.CONFLICTING_GAV_CHECK_DISABLED ) ) );
    }

}
