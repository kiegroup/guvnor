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

package org.kie.guvnor.guided.rule.client.resources;

import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This contains some simple mappings between operators, conditional elements and the human readable
 * equivalent.
 * <p/>
 * Yes, I am making the presumption that programmers are not human,
 * but I think they (we) are cool with that.
 */
public class HumanReadable {

    public static       Map<String, String> operatorDisplayMap          = new HashMap<String, String>();
    public static       Map<String, String> operatorExtensionDisplayMap = new HashMap<String, String>();
    public static       Map<String, String> ceDisplayMap                = new HashMap<String, String>();
    public static       Map<String, String> actionDisplayMap            = new HashMap<String, String>();
    public static final String[]            CONDITIONAL_ELEMENTS        = new String[]{ "not", "exists", "or" };
    public static final String[]            FROM_CONDITIONAL_ELEMENTS   = new String[]{ "from", "from accumulate", "from collect", "from entry-point" };

    static {
        operatorDisplayMap.put( "==", Constants.INSTANCE.isEqualTo() );
        operatorDisplayMap.put( "!=", Constants.INSTANCE.isNotEqualTo() );
        operatorDisplayMap.put( "<", Constants.INSTANCE.isLessThan() );
        operatorDisplayMap.put( "<=", Constants.INSTANCE.lessThanOrEqualTo() );
        operatorDisplayMap.put( ">", Constants.INSTANCE.greaterThan() );
        operatorDisplayMap.put( ">=", Constants.INSTANCE.greaterThanOrEqualTo() );
        operatorDisplayMap.put( "|| ==", Constants.INSTANCE.orEqualTo() );
        operatorDisplayMap.put( "|| !=", Constants.INSTANCE.orNotEqualTo() );
        operatorDisplayMap.put( "&& !=", Constants.INSTANCE.andNotEqualTo() );
        operatorDisplayMap.put( "&& >", Constants.INSTANCE.andGreaterThan() );
        operatorDisplayMap.put( "&& <", Constants.INSTANCE.andLessThan() );
        operatorDisplayMap.put( "|| >", Constants.INSTANCE.orGreaterThan() );
        operatorDisplayMap.put( "|| <", Constants.INSTANCE.orLessThan() );
        operatorDisplayMap.put( "&& <", Constants.INSTANCE.andLessThan() );
        operatorDisplayMap.put( "|| >=", Constants.INSTANCE.orGreaterThanOrEqualTo() );
        operatorDisplayMap.put( "|| <=", Constants.INSTANCE.orLessThanOrEqualTo() );
        operatorDisplayMap.put( "&& >=", Constants.INSTANCE.andGreaterThanOrEqualTo() );
        operatorDisplayMap.put( "&& <=", Constants.INSTANCE.andLessThanOrEqualTo() );
        operatorDisplayMap.put( "&& contains", Constants.INSTANCE.andContains() );
        operatorDisplayMap.put( "|| contains", Constants.INSTANCE.orContains() );
        operatorDisplayMap.put( "&& matches", Constants.INSTANCE.andMatches() );
        operatorDisplayMap.put( "|| matches", Constants.INSTANCE.orMatches() );
        operatorDisplayMap.put( "|| excludes", Constants.INSTANCE.orExcludes() );
        operatorDisplayMap.put( "&& excludes", Constants.INSTANCE.andExcludes() );
        operatorDisplayMap.put( "soundslike", Constants.INSTANCE.soundsLike() );
        operatorDisplayMap.put( "in", Constants.INSTANCE.isContainedInTheFollowingList() );
        operatorDisplayMap.put( "not in", Constants.INSTANCE.isNotContainedInTheFollowingList() );
        operatorDisplayMap.put( "== null", Constants.INSTANCE.isEqualToNull() );
        operatorDisplayMap.put( "!= null", Constants.INSTANCE.isNotEqualToNull() );

        operatorDisplayMap.put( "|| after", Constants.INSTANCE.orAfter() );
        operatorDisplayMap.put( "|| before", Constants.INSTANCE.orBefore() );
        operatorDisplayMap.put( "|| coincides", Constants.INSTANCE.orCoincides() );
        operatorDisplayMap.put( "&& after", Constants.INSTANCE.andAfter() );
        operatorDisplayMap.put( "&& before", Constants.INSTANCE.andBefore() );
        operatorDisplayMap.put( "&& coincides", Constants.INSTANCE.andCoincides() );
        operatorDisplayMap.put( "|| during", Constants.INSTANCE.orDuring() );
        operatorDisplayMap.put( "|| finishes", Constants.INSTANCE.orFinishes() );
        operatorDisplayMap.put( "|| finishedby", Constants.INSTANCE.orFinishedBy() );
        operatorDisplayMap.put( "|| includes", Constants.INSTANCE.orIncludes() );
        operatorDisplayMap.put( "|| meets", Constants.INSTANCE.orMeets() );
        operatorDisplayMap.put( "|| metby", Constants.INSTANCE.orMetBy() );
        operatorDisplayMap.put( "|| overlaps", Constants.INSTANCE.orOverlaps() );
        operatorDisplayMap.put( "|| overlappedby", Constants.INSTANCE.orOverlappedBy() );
        operatorDisplayMap.put( "|| starts", Constants.INSTANCE.orStarts() );
        operatorDisplayMap.put( "|| startedby", Constants.INSTANCE.orStartedBy() );
        operatorDisplayMap.put( "&& during", Constants.INSTANCE.addDuring() );
        operatorDisplayMap.put( "&& finishes", Constants.INSTANCE.andFinishes() );
        operatorDisplayMap.put( "&& finishedby", Constants.INSTANCE.andFinishedBy() );
        operatorDisplayMap.put( "&& includes", Constants.INSTANCE.andIncluded() );
        operatorDisplayMap.put( "&& meets", Constants.INSTANCE.andMeets() );
        operatorDisplayMap.put( "&& metby", Constants.INSTANCE.andMetBy() );
        operatorDisplayMap.put( "&& overlaps", Constants.INSTANCE.andOverlaps() );
        operatorDisplayMap.put( "&& overlappedby", Constants.INSTANCE.andOverlappedBy() );
        operatorDisplayMap.put( "&& starts", Constants.INSTANCE.andStarts() );
        operatorDisplayMap.put( "&& startedby", Constants.INSTANCE.andStartedBy() );
        operatorDisplayMap.put( "over window:time", Constants.INSTANCE.OverCEPWindowTime() );
        operatorDisplayMap.put( "over window:length", Constants.INSTANCE.OverCEPWindowLength() );

        ceDisplayMap.put( "not", Constants.INSTANCE.ThereIsNo() );
        ceDisplayMap.put( "exists", Constants.INSTANCE.ThereExists() );
        ceDisplayMap.put( "or", Constants.INSTANCE.AnyOf1() );
        ceDisplayMap.put( "from", Constants.INSTANCE.From() );
        ceDisplayMap.put( "from accumulate", Constants.INSTANCE.FromAccumulate() );
        ceDisplayMap.put( "from collect", Constants.INSTANCE.FromCollect() );
        ceDisplayMap.put( "from entry-point", Constants.INSTANCE.FromEntryPoint() );
        ceDisplayMap.put( "from entry-point", Constants.INSTANCE.FromEntryPoint() );

        actionDisplayMap.put( "assert", Constants.INSTANCE.Insert() );
        actionDisplayMap.put( "assertLogical", Constants.INSTANCE.LogicallyInsert() );
        actionDisplayMap.put( "retract", Constants.INSTANCE.Retract() );
        actionDisplayMap.put( "set", Constants.INSTANCE.Set() );
        actionDisplayMap.put( "modify", Constants.INSTANCE.Modify() );
        actionDisplayMap.put( "call", Constants.INSTANCE.CallMethod() );

    }

    public static String getOperatorDisplayName( String op ) {
        return lookup( op, operatorDisplayMap );
    }

    public static String getCEDisplayName( String ce ) {
        return lookup( ce, ceDisplayMap );
    }

    private static String lookup( String ce,
                                  Map<String, String> map ) {
        String ret = map.get( ce );
        return ret == null ? ce : ret;
    }

    public static String getActionDisplayName( String action ) {
        return lookup( action, actionDisplayMap );
    }
}
