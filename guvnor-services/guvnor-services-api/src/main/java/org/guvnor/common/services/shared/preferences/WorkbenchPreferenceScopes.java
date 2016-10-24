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

public enum WorkbenchPreferenceScopes {

    GLOBAL("global"),

    PROJECT("project"),

    USER("user");

    private final String type;

    WorkbenchPreferenceScopes( final String type ) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static WorkbenchPreferenceScopes fromType( final String type ) {
        for ( WorkbenchPreferenceScopes scope : values() ) {
            if ( scope.type().equals( type ) ) {
                return scope;
            }
        }

        return null;
    }
}
