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

package org.kie.guvnor.commons.data.header;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HeaderConfigBuilder {

    private List<HeaderConfigBasic.Import> imports          = new ArrayList<HeaderConfigBasic.Import>();
    private List<HeaderConfigBasic.Global> globals          = new ArrayList<HeaderConfigBasic.Global>();
    private boolean                        hasRules         = false;
    private boolean                        hasDeclaredTypes = false;
    private boolean                        hasFunctions     = false;
    private String                         content          = null;

    public HeaderConfigBuilder( final String content ) {
        this.content = content;
    }

    public void addImport( final HeaderConfigBasic.Import i ) {
        imports.add( i );
    }

    public void addGlobal( final HeaderConfigBasic.Global global ) {
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

    public HeaderConfig build() {
        if ( hasRules || hasDeclaredTypes || hasFunctions ) {
            return new HeaderConfigAdvanced( content, hasRules, hasDeclaredTypes, hasFunctions );
        }
        return new HeaderConfigBasic( imports, globals );
    }
}
