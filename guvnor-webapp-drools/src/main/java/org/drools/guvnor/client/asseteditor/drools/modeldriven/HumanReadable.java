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

package org.drools.guvnor.client.asseteditor.drools.modeldriven;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.messages.Constants;

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

    public static Map<String, String> operatorDisplayMap = new HashMap<String, String>();
    public static Map<String, String> operatorExtensionDisplayMap = new HashMap<String, String>();
    public static Map<String, String> ceDisplayMap = new HashMap<String, String>();
    public static Map<String, String> actionDisplayMap = new HashMap<String, String>();
    public static final String[] CONDITIONAL_ELEMENTS = new String[]{"not", "exists", "or"};
    public static final String[] FROM_CONDITIONAL_ELEMENTS = new String[]{"from", "from accumulate", "from collect", "from entry-point"};

    static {
        Constants constants = ((Constants) GWT.create(Constants.class));
        operatorDisplayMap.put("==", constants.isEqualTo());
        operatorDisplayMap.put("!=", constants.isNotEqualTo());
        operatorDisplayMap.put("<", constants.isLessThan());
        operatorDisplayMap.put("<=", constants.lessThanOrEqualTo());
        operatorDisplayMap.put(">", constants.greaterThan());
        operatorDisplayMap.put(">=", constants.greaterThanOrEqualTo());
        operatorDisplayMap.put("|| ==", constants.orEqualTo());
        operatorDisplayMap.put("|| !=", constants.orNotEqualTo());
        operatorDisplayMap.put("&& !=", constants.andNotEqualTo());
        operatorDisplayMap.put("&& >", constants.andGreaterThan());
        operatorDisplayMap.put("&& <", constants.andLessThan());
        operatorDisplayMap.put("|| >", constants.orGreaterThan());
        operatorDisplayMap.put("|| <", constants.orLessThan());
        operatorDisplayMap.put("&& <", constants.andLessThan());
        operatorDisplayMap.put("|| >=", constants.orGreaterThanOrEqualTo());
        operatorDisplayMap.put("|| <=", constants.orLessThanOrEqualTo());
        operatorDisplayMap.put("&& >=", constants.andGreaterThanOrEqualTo());
        operatorDisplayMap.put("&& <=", constants.andLessThanOrEqualTo());
        operatorDisplayMap.put("&& contains", constants.andContains());
        operatorDisplayMap.put("|| contains", constants.orContains());
        operatorDisplayMap.put("&& matches", constants.andMatches());
        operatorDisplayMap.put("|| matches", constants.orMatches());
        operatorDisplayMap.put("|| excludes", constants.orExcludes());
        operatorDisplayMap.put("&& excludes", constants.andExcludes());
        operatorDisplayMap.put("soundslike", constants.soundsLike());
        operatorDisplayMap.put("in", constants.isContainedInTheFollowingList());
        operatorDisplayMap.put("== null", constants.isEqualToNull());
        operatorDisplayMap.put("!= null", constants.isNotEqualToNull());

        operatorDisplayMap.put("|| after", constants.orAfter());
        operatorDisplayMap.put("|| before", constants.orBefore());
        operatorDisplayMap.put("|| coincides", constants.orCoincides());
        operatorDisplayMap.put("&& after", constants.andAfter());
        operatorDisplayMap.put("&& before", constants.andBefore());
        operatorDisplayMap.put("&& coincides", constants.andCoincides());
        operatorDisplayMap.put("|| during", constants.orDuring());
        operatorDisplayMap.put("|| finishes", constants.orFinishes());
        operatorDisplayMap.put("|| finishedby", constants.orFinishedBy());
        operatorDisplayMap.put("|| includes", constants.orIncludes());
        operatorDisplayMap.put("|| meets", constants.orMeets());
        operatorDisplayMap.put("|| metby", constants.orMetBy());
        operatorDisplayMap.put("|| overlaps", constants.orOverlaps());
        operatorDisplayMap.put("|| overlappedby", constants.orOverlappedBy());
        operatorDisplayMap.put("|| starts", constants.orStarts());
        operatorDisplayMap.put("|| startedby", constants.orStartedBy());
        operatorDisplayMap.put("&& during", constants.addDuring());
        operatorDisplayMap.put("&& finishes", constants.andFinishes());
        operatorDisplayMap.put("&& finishedby", constants.andFinishedBy());
        operatorDisplayMap.put("&& includes", constants.andIncluded());
        operatorDisplayMap.put("&& meets", constants.andMeets());
        operatorDisplayMap.put("&& metby", constants.andMetBy());
        operatorDisplayMap.put("&& overlaps", constants.andOverlaps());
        operatorDisplayMap.put("&& overlappedby", constants.andOverlappedBy());
        operatorDisplayMap.put("&& starts", constants.andStarts());
        operatorDisplayMap.put("&& startedby", constants.andStartedBy());
        operatorDisplayMap.put("over window:time", constants.OverCEPWindowTime());
        operatorDisplayMap.put("over window:length", constants.OverCEPWindowLength());

        ceDisplayMap.put("not", constants.ThereIsNo());
        ceDisplayMap.put("exists", constants.ThereExists());
        ceDisplayMap.put("or", constants.AnyOf1());
        ceDisplayMap.put("from", constants.From());
        ceDisplayMap.put("from accumulate", constants.FromAccumulate());
        ceDisplayMap.put("from collect", constants.FromCollect());
        ceDisplayMap.put("from entry-point", constants.FromEntryPoint());
        ceDisplayMap.put("from entry-point", constants.FromEntryPoint());

        actionDisplayMap.put("assert", constants.Insert());
        actionDisplayMap.put("assertLogical", constants.LogicallyInsert());
        actionDisplayMap.put("retract", constants.Retract());
        actionDisplayMap.put("set", constants.Set());
        actionDisplayMap.put("modify", constants.Modify());
        actionDisplayMap.put("call", constants.CallMethod());

    }

    public static String getOperatorDisplayName(String op) {
        return lookup(op, operatorDisplayMap);
    }

    public static String getCEDisplayName(String ce) {
        return lookup(ce, ceDisplayMap);
    }

    private static String lookup(String ce, Map<String, String> map) {
        String ret = map.get(ce);
        return ret == null ? ce : ret;
    }

    public static String getActionDisplayName(String action) {
        return lookup(action, actionDisplayMap);
    }
}
