/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.guvnor.services.config.model.imports;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ImportsConfig {

    private List<Import> imports = new ArrayList<Import>();

    public ImportsConfig() {

    }

    public ImportsConfig( final List<Import> imports ) {
        this.imports = new ArrayList<Import>( imports );
    }

    public List<Import> getImports() {
        return imports;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for ( final Import i : imports ) {
            sb.append( "import " ).append( i.getType() ).append( '\n' );
        }

        return sb.toString();
    }

    public void removeImport( final int i ) {
        imports.remove( i );
    }

    public void addImport( final Import i ) {
        imports.add( i );
    }

    @Portable
    public static class Import {

        private String type;

        public Import() {

        }

        public Import( String t ) {
            this.type = t;
        }

        public String getType() {
            return this.type;
        }

    }

}
