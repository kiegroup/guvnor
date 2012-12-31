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

package org.kie.guvnor.services.config.model.imports;

/**
 * Utility methods to parse an Imports Config
 */
public final class ImportsConfigHelper {

    private ImportsConfigHelper() {

    }

    public static ImportsConfigBuilder parseImports( final String content ) {
        if ( content == null || content.trim().equals( "" ) ) {
            return new ImportsConfigBuilder( "" );
        } else {
            final ImportsConfigBuilder builder = new ImportsConfigBuilder( content );

            final String[] lines = content.split( "\\n" );

            for ( int i = 0; i < lines.length; i++ ) {
                String tk = lines[ i ].trim();
                if ( !tk.equals( "" ) && !tk.startsWith( "#" ) ) {
                    if ( tk.startsWith( "import" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0, tk.length() - 1 );
                        }
                        builder.addImport( new ImportsConfig.Import( tk ) );
                    } else if ( tk.startsWith( "global" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0, tk.length() - 1 );
                        }
                        final String[] gt = tk.split( "\\s+" );
                        builder.addGlobal( new ImportsConfig.Global( gt[ 0 ], gt[ 1 ] ) );
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

            return builder;
        }

    }

}
