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

package org.guvnor.common.services.shared.preferences;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.CustomPreferenceScopeResolutionStrategyInfoFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.UsernameProvider;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.mocks.SessionInfoMock;

import static org.mockito.Mockito.*;

public class WorkbenchPreferenceScopeResolutionStrategyInfoFactoryImplTest {

    private PreferenceScopeFactory scopesFactory;

    private PreferenceScopeTypes scopeTypes;

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    private CustomPreferenceScopeResolutionStrategyInfoFactory customScopeResolutionStrategyInfoFactory;

    @Before
    public void setup() {
        final SessionInfoMock sessionInfo = new SessionInfoMock( "admin" );
        final UsernameProvider usernameProvider = mock( UsernameProvider.class );
        doReturn( sessionInfo.getIdentity().getIdentifier() ).when( usernameProvider ).get();
        scopeTypes = new WorkbenchPreferenceScopeTypes( usernameProvider );
        scopesFactory = new PreferenceScopeFactoryImpl( scopeTypes );
        scopeResolutionStrategies = spy( new WorkbenchPreferenceScopeResolutionStrategiesImpl( scopesFactory ) );
        customScopeResolutionStrategyInfoFactory = new WorkbenchPreferenceScopeResolutionStrategyInfoFactoryImpl( scopeResolutionStrategies );
    }

    @Test
    public void createWithNullParametersMapTest() {
        customScopeResolutionStrategyInfoFactory.create( null );
        verify( scopeResolutionStrategies ).getInfo( null, null );
    }

    @Test
    public void createWithNullParametersValuesTest() {
        customScopeResolutionStrategyInfoFactory.create( new HashMap<>() );
        verify( scopeResolutionStrategies ).getInfo( null, null );
    }

    @Test
    public void createWithParametersValuesTest() {
        final HashMap<String, String> params = new HashMap<>();
        params.put( "scope", "project" );
        params.put( "key", "my-project" );
        customScopeResolutionStrategyInfoFactory.create( params );
        verify( scopeResolutionStrategies ).getInfo( WorkbenchPreferenceScopes.PROJECT, "my-project" );
    }
}
