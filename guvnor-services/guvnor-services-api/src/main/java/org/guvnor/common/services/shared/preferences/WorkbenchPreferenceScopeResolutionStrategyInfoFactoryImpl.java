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

import java.util.Map;
import javax.inject.Inject;

import org.uberfire.ext.preferences.shared.CustomPreferenceScopeResolutionStrategyInfoFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public class WorkbenchPreferenceScopeResolutionStrategyInfoFactoryImpl implements CustomPreferenceScopeResolutionStrategyInfoFactory {

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    public WorkbenchPreferenceScopeResolutionStrategyInfoFactoryImpl() {
    }

    @Inject
    public WorkbenchPreferenceScopeResolutionStrategyInfoFactoryImpl( final WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies ) {
        this.scopeResolutionStrategies = scopeResolutionStrategies;
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo create( final Map<String, String> params ) {
        if ( params == null ) {
            return scopeResolutionStrategies.getInfo( null, null );
        }

        final String scopeName = params.getOrDefault( "scope", null );
        final String key = params.getOrDefault( "key", null );
        final WorkbenchPreferenceScopes scope = WorkbenchPreferenceScopes.fromType( scopeName );

        return scopeResolutionStrategies.getInfo( scope, key );
    }
}
