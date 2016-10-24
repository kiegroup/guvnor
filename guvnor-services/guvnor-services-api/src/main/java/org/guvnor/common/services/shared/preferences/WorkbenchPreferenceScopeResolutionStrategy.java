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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolver;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * The Workbench scope resolution strategy can be composed by a sequence of two (user and global) or three
 * ("component", user, global). The "component" scope represents some workbench component (e.g. project).
 */
@Dependent
public class WorkbenchPreferenceScopeResolutionStrategy implements PreferenceScopeResolutionStrategy {

    private PreferenceScopeResolutionStrategyInfo info;

    private PreferenceScopeResolver resolver;

    public WorkbenchPreferenceScopeResolutionStrategy() {
    }

    @Inject
    public WorkbenchPreferenceScopeResolutionStrategy( final PreferenceScopeFactory scopeFactory ) {
        this( scopeFactory, null, null );
    }

    public WorkbenchPreferenceScopeResolutionStrategy( final PreferenceScopeFactory scopeFactory,
                                                       final WorkbenchPreferenceScopes scope,
                                                       final String key ) {
        final List<PreferenceScope> order = getScopeOrder( scopeFactory, scope, key );
        final PreferenceScope defaultScope = getDefaultScope( order );

        info = new PreferenceScopeResolutionStrategyInfo( order, defaultScope );
        resolver = new WorkbenchPreferenceScopeResolver( order );
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getInfo() {
        return info;
    }

    @Override
    public PreferenceScopeResolver getScopeResolver() {
        return resolver;
    }

    public PreferenceScope getDefaultScope( final List<PreferenceScope> order ) {
        return order.get( 0 );
    }

    private List<PreferenceScope> getScopeOrder( final PreferenceScopeFactory scopeFactory,
                                                 final WorkbenchPreferenceScopes scope,
                                                 final String key ) {
        List<PreferenceScope> order = new ArrayList<>();

        if ( scope != null ) {
            if ( key != null ) {
                order.add( scopeFactory.createScope( scope.type(), key ) );
            } else {
                order.add( scopeFactory.createScope( scope.type() ) );
            }
        }

        order.add( scopeFactory.createScope( WorkbenchPreferenceScopes.USER.type() ) );
        order.add( scopeFactory.createScope( WorkbenchPreferenceScopes.GLOBAL.type() ) );

        return order;
    }
}