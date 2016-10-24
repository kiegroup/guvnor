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

import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Service used to obtain information regarding the workbench preference scopes resolution strategies.
 */
public interface WorkbenchPreferenceScopeResolutionStrategies {

    /**
     * Returns a scope resolution strategy with the scopes (in that order): scope (parameter), user and global.
     * @param scope The first scope in the resolution order (e.g. project).
     * @param key The key for the first scope in the resolution order (e.g. my-project-name).
     * @return The scope resolution strategy info based on the passed parameters.
     */
    PreferenceScopeResolutionStrategyInfo getInfo( WorkbenchPreferenceScopes scope,
                                                   String key );
}
