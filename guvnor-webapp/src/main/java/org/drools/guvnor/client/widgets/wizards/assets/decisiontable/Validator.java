/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Validator {

    public static boolean validateDecoratedPatterns(List<DecoratedPattern> patterns) {
        boolean duplicateBindings = false;

        //Store Patterns by their binding
        Map<String, List<DecoratedPattern>> bindings = new HashMap<String, List<DecoratedPattern>>();
        for ( DecoratedPattern pw : patterns ) {
            pw.setDuplicateBinding( false );
            String binding = pw.getPattern().getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                List<DecoratedPattern> pws = bindings.get( binding );
                if ( pws == null ) {
                    pws = new ArrayList<DecoratedPattern>();
                    bindings.put( binding,
                                  pws );
                }
                pws.add( pw );
            }
        }

        //Check if any bindings have multiple Patterns
        for ( List<DecoratedPattern> pws : bindings.values() ) {
            if ( pws.size() > 1 ) {
                duplicateBindings = true;
                for ( DecoratedPattern pw : pws ) {
                    pw.setDuplicateBinding( true );
                }
            }
        }
        return duplicateBindings;
    }

}
