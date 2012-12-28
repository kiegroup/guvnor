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
package org.kie.guvnor.commons.data.imports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ImportsConfig {

    private List<Import> imports = new ArrayList<Import>();
    private List<Global> globals = new ArrayList<Global>();

    public ImportsConfig() {

    }

    public ImportsConfig( final List<Import> imports,
                          final List<Global> globals ) {
        this.imports = new ArrayList<Import>( imports );
        this.globals = new ArrayList<Global>( globals );
    }

    public List<Import> getImports() {
        return imports;
    }

    public List<Global> getGlobals() {
        return globals;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for ( final Import i : imports ) {
            sb.append( "import " ).append( i.getType() ).append( '\n' );
        }

        for ( final Iterator<Global> it = globals.iterator(); it.hasNext(); ) {
            final ImportsConfig.Global g = it.next();
            sb.append( "global " ).append( g.getType() ).append( ' ' ).append( g.getName() );
            if ( it.hasNext() ) {
                sb.append( '\n' );
            }
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
    public static class Global {

        private String type;
        private String name;

        public Global() {

        }

        public Global( final String type,
                       final String name ) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

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
