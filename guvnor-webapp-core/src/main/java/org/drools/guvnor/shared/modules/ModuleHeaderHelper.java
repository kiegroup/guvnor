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

package org.drools.guvnor.shared.modules;

import java.util.Iterator;

import org.drools.guvnor.shared.modules.ModuleHeader.Global;
import org.drools.guvnor.shared.modules.ModuleHeader.Import;

/**
 * Utility methods to parse a Module Header
 */
public class ModuleHeaderHelper {

    /**
     * Attempt to parse out a model, if it can't, it will return null in which
     * case an "advanced" editor should be used.
     */
    public static ModuleHeader parseHeader(String header) {
        if ( header == null || header.equals( "" ) ) {
            ModuleHeader mh = new ModuleHeader();
            return mh;
        } else {
            ModuleHeader mh = new ModuleHeader();

            String[] lines = header.split( "\\n" );

            for ( int i = 0; i < lines.length; i++ ) {
                String tk = lines[i].trim();
                if ( !tk.equals( "" ) && !tk.startsWith( "#" ) ) {
                    if ( tk.startsWith( "import" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0,
                                               tk.length() - 1 );
                        }
                        mh.getImports().add( new Import( tk ) );
                    } else if ( tk.startsWith( "global" ) ) {
                        tk = tk.substring( 6 ).trim();
                        if ( tk.endsWith( ";" ) ) {
                            tk = tk.substring( 0,
                                               tk.length() - 1 );
                        }
                        String[] gt = tk.split( "\\s+" );
                        mh.getGlobals().add( new Global( gt[0],
                                                         gt[1] ) );
                    } else if ( tk.startsWith( "rule" ) ) {
                        mh.setHasRules( true );
                        return mh;
                    } else if ( tk.startsWith( "declare" ) ) {
                        mh.setHasDeclaredTypes( true );
                        return mh;
                    } else if ( tk.startsWith( "function" ) ) {
                        mh.setHasFunctions( true );
                        return mh;
                    } else {
                        return null;
                    }
                }
            }

            return mh;

        }

    }

    /**
     * Render the ModuleHeader as a String
     * 
     * @param mh
     * @return
     */
    public static String renderModuleHeader(ModuleHeader mh) {
        StringBuilder sb = new StringBuilder();
        for ( Iterator<Import> iterator = mh.getImports().iterator(); iterator.hasNext(); ) {
            Import i = iterator.next();
            sb.append( "import " + i.getType() + "\n" );
        }

        for ( Iterator<Global> it = mh.getGlobals().iterator(); it.hasNext(); ) {
            Global g = (Global) it.next();
            sb.append( "global " + g.getType() + " " + g.getName() );
            if ( it.hasNext() ) {
                sb.append( '\n' );
            }
        }
        return sb.toString();
    }

}
