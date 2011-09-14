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

import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

/**
 * Guided Decision Table validator
 */
public class Validator {

    private List<Pattern52>                                                 patternsConditions;
    private List<Pattern52>                                                 patternsActions;
    private Map<Pattern52, List<ActionSetFieldCol52>>                       patternToActionSetFieldsMap;
    private Map<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> patternToActionInsertFactFieldsMap;

    Validator(List<Pattern52> patterns) {
        this.patternsConditions = patterns;
        this.patternsActions = new ArrayList<Pattern52>();
    }

    void addActionPattern(Pattern52 pattern) {
        this.patternsActions.add( pattern );
    }

    void removeActionPattern(Pattern52 pattern) {
        this.patternsActions.remove( pattern );
    }

    public boolean arePatternBindingsUnique() {

        boolean hasUniqueBindings = true;

        //Store Patterns by their binding
        Map<String, List<Pattern52>> bindings = new HashMap<String, List<Pattern52>>();
        for ( Pattern52 p : patternsConditions ) {
            String binding = p.getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                List<Pattern52> ps = bindings.get( binding );
                if ( ps == null ) {
                    ps = new ArrayList<Pattern52>();
                    bindings.put( binding,
                                  ps );
                }
                ps.add( p );
            }
        }
        for ( Pattern52 p : patternsActions ) {
            String binding = p.getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                List<Pattern52> ps = bindings.get( binding );
                if ( ps == null ) {
                    ps = new ArrayList<Pattern52>();
                    bindings.put( binding,
                                  ps );
                }
                ps.add( p );
            }
        }

        //Check if any bindings have multiple Patterns
        for ( List<Pattern52> pws : bindings.values() ) {
            if ( pws.size() > 1 ) {
                hasUniqueBindings = false;
                break;
            }
        }
        return hasUniqueBindings;
    }

    public boolean isPatternBindingUnique(Pattern52 pattern) {
        String binding = pattern.getBoundName();
        if ( binding == null || binding.equals( "" ) ) {
            return true;
        }
        for ( Pattern52 p : patternsConditions ) {
            if ( p != pattern ) {
                if ( p.getBoundName() != null && p.getBoundName().equals( binding ) ) {
                    return false;
                }
            }
        }
        for ( Pattern52 p : patternsActions ) {
            if ( p != pattern ) {
                if ( p.getBoundName() != null && p.getBoundName().equals( binding ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPatternValid(Pattern52 p) {
        return !(p.getBoundName() == null || p.getBoundName().equals( "" ));
    }

    public boolean isConditionValid(ConditionCol52 c) {
        return isConditionHeaderValid( c ) && isConditionOperatorValid( c );
    }

    public boolean isConditionHeaderValid(ConditionCol52 c) {
        return !(c.getHeader() == null || c.getHeader().equals( "" ));
    }

    public boolean isConditionOperatorValid(ConditionCol52 c) {
        if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            return true;
        }
        return !(c.getOperator() == null || c.getOperator().equals( "" ));
    }

    public boolean isActionValid(ActionCol52 c) {
        return isActionHeaderValid( c );
    }

    public boolean isActionHeaderValid(ActionCol52 a) {
        return !(a.getHeader() == null || a.getHeader().equals( "" ));
    }

    public void setPatternToActionSetFieldsMap(Map<Pattern52, List<ActionSetFieldCol52>> patternToActionSetFieldsMap) {
        this.patternToActionSetFieldsMap = patternToActionSetFieldsMap;
    }

    public boolean arePatternActionSetFieldsValid(Pattern52 p) {
        if ( patternToActionSetFieldsMap == null ) {
            return true;
        }
        List<ActionSetFieldCol52> actions = patternToActionSetFieldsMap.get( p );
        if ( actions == null ) {
            return true;
        }
        for ( ActionSetFieldCol52 a : actions ) {
            if ( !isActionValid( a ) ) {
                return false;
            }
        }
        return true;
    }

    public void setPatternToActionInsertFactFieldsMap(Map<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> patternToActionInsertFactFieldsMap) {
        this.patternToActionInsertFactFieldsMap = patternToActionInsertFactFieldsMap;
    }

    public boolean arePatternActionInsertFactFieldsValid(Pattern52 p) {
        if ( patternToActionInsertFactFieldsMap == null ) {
            return true;
        }
        List<ActionInsertFactCol52> actions = patternToActionInsertFactFieldsMap.get( p );
        if ( actions == null ) {
            return true;
        }
        for ( ActionInsertFactCol52 a : actions ) {
            if ( !isActionValid( a ) ) {
                return false;
            }
        }
        return true;
    }

}
