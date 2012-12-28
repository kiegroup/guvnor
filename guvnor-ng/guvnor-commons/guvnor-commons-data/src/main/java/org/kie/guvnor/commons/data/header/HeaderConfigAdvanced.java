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

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.guvnor.commons.data.header.HeaderType.*;

@Portable
public class HeaderConfigAdvanced implements HeaderConfig {

    private String  content          = null;
    private boolean hasRules         = false;
    private boolean hasDeclaredTypes = false;
    private boolean hasFunctions     = false;

    public HeaderConfigAdvanced() {

    }

    public HeaderConfigAdvanced( final String content ) {
        this( content, false, false, false );
    }

    public HeaderConfigAdvanced( final String content,
                                 final boolean hasRules,
                                 final boolean hasDeclaredTypes,
                                 final boolean hasFunctions ) {
        this.content = content;
        this.hasRules = hasRules;
        this.hasDeclaredTypes = hasDeclaredTypes;
        this.hasFunctions = hasFunctions;
    }

    @Override
    public HeaderType getType() {
        return ADVANCED;
    }

    public String getContent() {
        return content;
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

    public void setContent( final String content ) {
        this.content = content;
    }
}
