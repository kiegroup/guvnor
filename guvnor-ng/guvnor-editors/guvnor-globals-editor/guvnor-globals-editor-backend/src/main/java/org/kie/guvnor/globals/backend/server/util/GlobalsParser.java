/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.guvnor.globals.backend.server.util;

import java.util.ArrayList;
import java.util.List;

import org.kie.guvnor.globals.model.Global;

/**
 * Utility methods to parse Globals
 */
public final class GlobalsParser {

    private static String KEYWORD = "global";

    private GlobalsParser() {
    }

    public static List<Global> parseGlobals( final String content ) {
        final List<Global> globals = new ArrayList<Global>();

        if ( content == null || content.isEmpty() ) {
            return globals;

        } else {
            final String[] lines = content.split( "\\n" );
            for ( int i = 0; i < lines.length; i++ ) {
                final String line = lines[ i ].trim();
                final Global g = parseGlobal( line );
                if ( g != null ) {
                    globals.add( g );
                }
            }
        }
        return globals;
    }

    private static Global parseGlobal( final String line ) {
        if ( line.equals( "" ) || line.startsWith( "#" ) ) {
            return null;
        }
        //Replace multiple spaces with single spaces
        final String compactedLine = line.replaceAll( "^ +| +$|( )+", "$1" );
        final String[] fragments = compactedLine.split( " " );
        if ( fragments.length != 3 ) {
            return null;
        }
        if ( !fragments[ 0 ].equalsIgnoreCase( KEYWORD ) ) {
            return null;
        }
        final Global g = new Global();
        g.setClassName( fragments[ 1 ] );
        g.setAlias( stripSemiColon( fragments[ 2 ] ) );
        return g;
    }

    private static String stripSemiColon( final String className ) {
        if ( className.endsWith( ";" ) ) {
            return className.substring( 0, className.indexOf( ";" ) );
        }
        return className;
    }

}
