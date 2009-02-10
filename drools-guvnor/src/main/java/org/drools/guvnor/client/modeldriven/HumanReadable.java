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



import org.drools.guvnor.client.messages.Constants;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

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

    private static Constants constants;

    static {
        constants = ((Constants) GWT.create(Constants.class));
        operatorDisplayMap.put( "==", constants.isEqualTo());
        operatorDisplayMap.put( "!=", constants.isNotEqualTo());
        operatorDisplayMap.put( "<", constants.isLessThan());
        operatorDisplayMap.put( "<=", constants.lessThanOrEqualTo());
        operatorDisplayMap.put( ">", constants.greaterThan());
        operatorDisplayMap.put( ">=", constants.greaterThanOrEqualTo());

        operatorDisplayMap.put( "|| ==", constants.orEqualTo());
        operatorDisplayMap.put( "|| !=", constants.orNotEqualTo());
        operatorDisplayMap.put( "&& !=", constants.andNotEqualTo());
        operatorDisplayMap.put( "&& >", constants.andGreaterThan());
        operatorDisplayMap.put( "&& <", constants.andLessThan());
        operatorDisplayMap.put( "|| >", constants.orGreaterThan());
        operatorDisplayMap.put( "|| <", constants.orLessThan());
        operatorDisplayMap.put( "&& <", constants.andLessThan());

        operatorDisplayMap.put( "|| >=", constants.orGreaterThanOrEqualTo());
        operatorDisplayMap.put( "|| <=", constants.orLessThanOrEqualTo());
        operatorDisplayMap.put( "&& >=", constants.andGreaterThanOrEqualTo());
        operatorDisplayMap.put( "&& <=", constants.andLessThanOrEqualTo());
        operatorDisplayMap.put( "&& contains", constants.andContains());
        operatorDisplayMap.put( "|| contains", constants.orContains());
        operatorDisplayMap.put( "&& matches", constants.andMatches());
        operatorDisplayMap.put( "|| matches", constants.orMatches());
        operatorDisplayMap.put( "|| excludes", constants.orExcludes());
        operatorDisplayMap.put( "&& excludes", constants.andExcludes());

        operatorDisplayMap.put( "soundslike", constants.soundsLike());

        ceDisplayMap.put( "not", constants.ThereIsNo());
        ceDisplayMap.put( "exists", constants.ThereExists());
        ceDisplayMap.put( "or", constants.AnyOf1());

        actionDisplayMap.put( "assert", constants.Insert());
        actionDisplayMap.put( "assertLogical", constants.LogicallyInsert());
        actionDisplayMap.put( "retract", constants.Retract());
        actionDisplayMap.put( "set", constants.Set());
        actionDisplayMap.put( "modify", constants.Modify() );
        actionDisplayMap.put( "call", constants.CallMethod());

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