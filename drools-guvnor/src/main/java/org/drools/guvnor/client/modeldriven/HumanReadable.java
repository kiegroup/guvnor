package org.drools.guvnor.client.modeldriven;
/*
 * Copyright 2005 JBoss Inc
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



import java.util.HashMap;
import java.util.Map;

/**
 * This contains some simple mappings between operators, conditional elements and the human readable
 * equivalent.
 *
 * Yes, I am making the presumption that programmers are not human,
 * but I think they (we) are cool with that.
 *
 * @author Michael Neale
 */
public class HumanReadable {

    public static Map operatorDisplayMap = new HashMap();
    public static Map ceDisplayMap = new HashMap();
    public static Map actionDisplayMap = new HashMap();
    public static final String[] CONDITIONAL_ELEMENTS = new String[] {"not", "exists", "or"};

    static {
        operatorDisplayMap.put( "==", "is equal to" );
        operatorDisplayMap.put( "!=", "is not equal to" );
        operatorDisplayMap.put( "<", "is less than" );
        operatorDisplayMap.put( "<=", "less than or equal to" );
        operatorDisplayMap.put( ">", "greater than" );
        operatorDisplayMap.put( ">=", "greater than or equal to" );

        operatorDisplayMap.put( "|| ==", "or equal to" );
        operatorDisplayMap.put( "|| !=", "or not equal to" );
        operatorDisplayMap.put( "&& !=", "and not equal to" );
        operatorDisplayMap.put( "&& >", "and greater than" );
        operatorDisplayMap.put( "&& <", "and less than" );
        operatorDisplayMap.put( "|| >", "or greater than" );
        operatorDisplayMap.put( "|| <", "or less than" );
        operatorDisplayMap.put( "&& <", "and less than" );

        operatorDisplayMap.put( "|| >=", "or greater than (or equal to)" );
        operatorDisplayMap.put( "|| <=", "or less than (or equal to)" );
        operatorDisplayMap.put( "&& >=", "and greater than (or equal to)" );
        operatorDisplayMap.put( "&& <=", "and less than (or equal to)" );
        operatorDisplayMap.put( "&& contains", "and contains" );
        operatorDisplayMap.put( "|| contains", "or contains" );
        operatorDisplayMap.put( "&& matches", "and matches" );
        operatorDisplayMap.put( "|| matches", "or matches" );
        operatorDisplayMap.put( "|| excludes", "or excludes" );
        operatorDisplayMap.put( "&& excludes", "and excludes" );

        operatorDisplayMap.put( "soundslike", "sounds like" );

        ceDisplayMap.put( "not", "There is no" );
        ceDisplayMap.put( "exists", "There exists" );
        ceDisplayMap.put( "or", "Any of" );

        actionDisplayMap.put( "assert", "Insert" );
        actionDisplayMap.put( "assertLogical", "Logically insert" );
        actionDisplayMap.put( "retract", "Retract" );
        actionDisplayMap.put( "set", "Set" );
        actionDisplayMap.put( "modify", "Modify" );
        actionDisplayMap.put( "call", "Call");

    }




    public static String getOperatorDisplayName(String op) {
        return lookup(op, operatorDisplayMap);
    }

    public static String getCEDisplayName(String ce) {
        return lookup( ce, ceDisplayMap );
    }

    private static String lookup(String ce, Map map) {
        if (map.containsKey(ce)) {
            return (String) map.get(ce);
        } else {
            return ce;
        }
    }

    public static String getActionDisplayName(String action) {
        return lookup(action, actionDisplayMap);
    }

}