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

package org.kie.guvnor.services.config.model.imports;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImportsConfigBuilder {

    private List<ImportsConfig.Import> imports          = new ArrayList<ImportsConfig.Import>();
    private List<ImportsConfig.Global> globals          = new ArrayList<ImportsConfig.Global>();
    private boolean                    hasRules         = false;
    private boolean                    hasDeclaredTypes = false;
    private boolean                    hasFunctions     = false;
    private String                     content          = null;

    public ImportsConfigBuilder( final String content ) {
        this.content = content;
    }

    public void addImport( final ImportsConfig.Import i ) {
        imports.add( i );
    }

    public void addGlobal( final ImportsConfig.Global global ) {
        globals.add( global );
    }

    public void setHasRules() {
        hasRules = true;
    }

    public void setHasDeclaredTypes() {
        hasDeclaredTypes = true;
    }

    public void setHasFunctions() {
        hasFunctions = true;
    }

    public boolean hasRules() {
        return hasRules;
    }

    public boolean hasDeclaredTypes() {
        return hasDeclaredTypes;
    }

    public boolean hasFunctions() {
        return hasFunctions;
    }

    public ImportsConfig build() {
        if ( hasRules || hasDeclaredTypes || hasFunctions ) {
            return null;
        }
        return new ImportsConfig( imports, globals );
    }
}
