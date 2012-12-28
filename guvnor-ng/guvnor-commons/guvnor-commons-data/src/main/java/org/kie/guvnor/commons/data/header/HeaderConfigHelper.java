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

package org.kie.guvnor.commons.data.header;

/**
 * Utility methods to parse a Module Header
 */
public final class HeaderConfigHelper {

    private HeaderConfigHelper() {

    }

    /**
     * Attempt to parse out a model, if it can't, it will return null in which
     * case an "advanced" editor should be used.
     */
    public static HeaderConfig parseHeaderConfig( final String header ) {
        if ( header == null || header.trim().equals( "" ) ) {
            return new HeaderConfigBasic();
        } else {
            final HeaderConfigBuilder builder = new HeaderConfigBuilder( header );

            final String[] lines = header.split( "\\n" );

            for ( int i = 0; i < lines.length; i++ ) {
                String tk = lines[ i ].trim();
                if ( !tk.equals( "" ) && !tk.startsWith( "#" ) ) {
                    if ( tk.startsWith( "import" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0, tk.length() - 1 );
                        }
                        builder.addImport( new HeaderConfigBasic.Import( tk ) );
                    } else if ( tk.startsWith( "global" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0, tk.length() - 1 );
                        }
                        final String[] gt = tk.split( "\\s+" );
                        builder.addGlobal( new HeaderConfigBasic.Global( gt[ 0 ], gt[ 1 ] ) );
                    } else if ( tk.startsWith( "rule" ) ) {
                        builder.setHasRules();
                        break;
                    } else if ( tk.startsWith( "declare" ) ) {
                        builder.setHasDeclaredTypes();
                        break;
                    } else if ( tk.startsWith( "function" ) ) {
                        builder.setHasFunctions();
                        break;
                    } else {
                        return null;
                    }
                }
            }

            return builder.build();
        }

    }

}
