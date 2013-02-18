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

package org.kie.guvnor.globals.backend.server.util;

import java.util.ArrayList;
import java.util.List;

import org.kie.commons.data.Pair;
import org.kie.guvnor.datamodel.backend.server.builder.util.GlobalsParser;
import org.kie.guvnor.globals.model.Global;
import org.kie.guvnor.globals.model.GlobalsModel;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.kie.guvnor.services.config.model.imports.ImportsParser;

/**
 * This class persists the rule model to DRL and back
 */
public class GlobalsPersistence {

    private static final GlobalsPersistence INSTANCE = new GlobalsPersistence();

    protected GlobalsPersistence() {
    }

    public static GlobalsPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final GlobalsModel model ) {
        final StringBuilder sb = new StringBuilder();
        final Imports imports = model.getImports();
        sb.append( imports.toString() );
        if ( imports.getImports().size() > 0 ) {
            sb.append( "\n" );
        }
        for ( Global global : model.getGlobals() ) {
            sb.append( "global " ).append( global.getClassName() ).append( " " ).append( global.getAlias() ).append( ";\n" );
        }
        return sb.toString();
    }

    public GlobalsModel unmarshal( final String content ) {
        final Imports imports = ImportsParser.parseImports( content );
        final List<Pair<String, String>> parsedGlobalsContent = GlobalsParser.parseGlobals( content );
        final List<Global> globals = makeGlobals( parsedGlobalsContent );
        final GlobalsModel model = new GlobalsModel();
        model.setImports( imports );
        model.setGlobals( globals );
        return model;
    }

    private List<Global> makeGlobals( final List<Pair<String, String>> parsedGlobalsContent ) {
        final List<Global> globals = new ArrayList<Global>();
        for ( Pair<String, String> p : parsedGlobalsContent ) {
            globals.add( new Global( p.getK1(),
                                     p.getK2() ) );
        }
        return globals;
    }

}
