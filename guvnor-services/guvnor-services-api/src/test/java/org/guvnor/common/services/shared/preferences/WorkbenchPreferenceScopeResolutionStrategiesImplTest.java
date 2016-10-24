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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.UsernameProvider;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.mocks.SessionInfoMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WorkbenchPreferenceScopeResolutionStrategiesImplTest {

    private PreferenceScopeFactory scopesFactory;

    private PreferenceScopeTypes scopeTypes;

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    @Before
    public void setup() {
        final SessionInfoMock sessionInfo = new SessionInfoMock( "admin" );
        final UsernameProvider usernameProvider = mock( UsernameProvider.class );
        doReturn( sessionInfo.getIdentity().getIdentifier() ).when( usernameProvider ).get();
        scopeTypes = new WorkbenchPreferenceScopeTypes( usernameProvider );
        scopesFactory = new PreferenceScopeFactoryImpl( scopeTypes );

        scopeResolutionStrategies = new WorkbenchPreferenceScopeResolutionStrategiesImpl( scopesFactory );
    }

    @Test
    public void getInfoTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getInfo( null,
                                                                                                   null);

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals( 2, order.size() );

        final PreferenceScope firstScope = order.get( 0 );
        assertEquals( "user", firstScope.type() );
        assertEquals( "admin", firstScope.key() );

        final PreferenceScope secondScope = order.get( 1 );
        assertEquals( "global", secondScope.type() );
        assertEquals( "global", secondScope.key() );

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals( "user", defaultScope.type() );
        assertEquals( "admin", defaultScope.key() );
    }

    @Test
    public void getInfoWithScopeTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getInfo( WorkbenchPreferenceScopes.PROJECT,
                                                                                                   "my-project" );

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals( 3, order.size() );

        final PreferenceScope firstScope = order.get( 0 );
        assertEquals( "project", firstScope.type() );
        assertEquals( "my-project", firstScope.key() );

        final PreferenceScope secondScope = order.get( 1 );
        assertEquals( "user", secondScope.type() );
        assertEquals( "admin", secondScope.key() );

        final PreferenceScope threeScope = order.get( 2 );
        assertEquals( "global", threeScope.type() );
        assertEquals( "global", threeScope.key() );

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals( "project", defaultScope.type() );
        assertEquals( "my-project", defaultScope.key() );
    }
}
