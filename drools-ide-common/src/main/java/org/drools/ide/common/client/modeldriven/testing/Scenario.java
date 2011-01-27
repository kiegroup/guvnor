/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * This represents a test scenario.
 * It also encapsulates the result of a scenario run.
 *
 * @author Michael Neale
 */
public class Scenario
    implements
    PortableObject {

    private static final long serialVersionUID = 510l;

    /**
     * The maximum number of rules to fire so we don't recurse for ever.
     */
    private int               maxRuleFirings   = 100000;

    /**
     * global data which must be setup before hand.
     */
    private List<FactData>    globals          = new ArrayList<FactData>();

    /**
     * Fixtures are parts of the test. They may be assertions, globals, data, execution runs etc.
     * Anything really.
     *
     */
    private List<Fixture>     fixtures         = new ArrayList<Fixture>();

    /**
     * This is the date the last time the scenario was run (and what the results apply to).
     */
    private Date              lastRunResult;

    /**
     * the rules to include or exclude
     */
    private List<String>      rules            = new ArrayList<String>();

    /**
     * true if only the rules in the list should be allowed to fire. Otherwise
     * it is exclusive (ie all rules can fire BUT the ones in the list).
     */
    private boolean           inclusive        = false;

    /**
     * Returns true if this was a totally successful scenario, based on the results contained.
     */
    public boolean wasSuccessful() {
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof Expectation ) {
                if ( !((Expectation) fixture).wasSuccessful() ) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Will slip in a fixture after the specified one, but before the next execution trace.
     */
    public void insertBetween(Fixture fixtureBeforeTheNewOne,
                              Fixture newFixture) {

        boolean inserted = false;
        int start = (fixtureBeforeTheNewOne == null) ? 0 : fixtures.indexOf( fixtureBeforeTheNewOne ) + 1;

        for ( int j = start; j < fixtures.size(); j++ ) {
            if ( fixtures.get( j ) instanceof ExecutionTrace ) {
                getFixtures().add( j,
                                   newFixture );
                return;
            }
        }

        if ( !inserted ) {
            fixtures.add( newFixture );
        }
    }

    /**
     * Remove the specified fixture.
     */
    public void removeFixture(Fixture f) {
        this.fixtures.remove( f );
        this.globals.remove( f );
    }

    /**
     * Remove fixtures between this ExecutionTrace and the previous one.
     */
    public void removeExecutionTrace(ExecutionTrace et) {

        boolean remove = false;
        for ( Iterator<Fixture> iterator = getFixtures().iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            if ( fixture.equals( et ) ) {
                remove = true;
                continue;
            } else if ( remove && (fixture instanceof ExecutionTrace || (fixture instanceof FactData)) ) {
                break;
            }

            if ( remove ) {
                iterator.remove();
                globals.remove( fixture );
            }
        }

        Collections.reverse( getFixtures() );

        remove = false;
        for ( Iterator<Fixture> iterator = getFixtures().iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            // Catch the first or next ExecutionTrace.
            if ( fixture.equals( et ) ) {
                remove = true;
            } else if ( remove && (fixture instanceof ExecutionTrace || (fixture instanceof VerifyFact)) ) {
                break;
            }

            if ( remove ) {
                iterator.remove();
                globals.remove( fixture );
            }
        }

        Collections.reverse( fixtures );
    }

    /**
    *
    * @return A mapping of variable names to their fact type.
    */
    public Map<String, FactData> getFactTypes() {
        Map<String, FactData> factTypesByName = new HashMap<String, FactData>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                factTypesByName.put( factData.getName(),
                                     factData );
            }
        }
        return factTypesByName;
    }

    /**
     *
     * @return A mapping of variable names to their fact type.
     */
    public Map<String, String> getVariableTypes() {
        Map<String, String> map = new HashMap<String, String>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                map.put( factData.getName(),
                         factData.getType() );
            }
        }
        for ( FactData factData : globals ) {
            map.put( factData.getName(),
                     factData.getType() );
        }
        return map;
    }

    /**
     * This will return a list of fact names that are in scope (including globals).
     * @return List<String>
     */
    public List<String> getFactNamesInScope(ExecutionTrace executionTrace,
                                            boolean includeGlobals) {
        if ( executionTrace == null ) {
            return Collections.emptyList();
        }

        List<String> factDataNames = new ArrayList<String>();
        int p = this.getFixtures().indexOf( executionTrace );
        for ( int i = 0; i < p; i++ ) {
            Fixture fixture = (Fixture) getFixtures().get( i );
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                factDataNames.add( factData.getName() );
            } else if ( fixture instanceof RetractFact ) {
                RetractFact retractFact = (RetractFact) fixture;
                factDataNames.remove( retractFact.getName() );
            }
        }

        if ( includeGlobals ) {
            for ( FactData factData : getGlobals() ) {
                factDataNames.add( factData.getName() );
            }
        }
        return factDataNames;
    }

    /**
     * @return true if a fact name is already in use.
     */
    public boolean isFactNameReserved(String factName) {
        if ( isFactNameUsedInGlobals( factName ) ) {
            return true;
        } else if ( isFactNameUsedInFactDataFixtures( factName ) ) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isFactNameUsedInFactDataFixtures(String factName) {
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                if ( factData.getName().equals( factName ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isFactNameUsedInGlobals(String factName) {
        for ( FactData factData : globals ) {
            if ( factData.getName().equals( factName ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if a fact is actually used (ie if its not, its safe to remove it).
     */
    public boolean isFactDataReferenced(FactData factData) {
        int start = fixtures.indexOf( factData ) + 1;
        String factName = factData.getName();

        for ( Fixture fixture : fixtures.subList( start,
                                                  fixtures.size() ) ) {
            if ( isFactNameUsedInThisFixture( fixture,
                                              factName ) ) {
                return true;
            }
        }

        return false;
    }

    private boolean isFactNameUsedInThisFixture(Fixture fixture,
                                                String factName) {
        if ( fixture instanceof FactData ) {
            return ((FactData) fixture).getName().equals( factName );
        } else if ( fixture instanceof VerifyFact ) {
            return ((VerifyFact) fixture).getName().equals( factName );
        } else if ( fixture instanceof RetractFact ) {
            return ((RetractFact) fixture).getName().equals( factName );
        } else {
            return false;
        }
    }

    /**
     *
     * @return int[0] = failures, int[1] = total;
     */
    public int[] countFailuresTotal() {
        int total = 0;
        int failures = 0;
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof VerifyRuleFired ) {
                total++;
                VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
                if ( ruleFailedToFire( verifyRuleFired ) ) {
                    failures++;
                }
            } else if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;
                for ( VerifyField verifyField : verifyFact.getFieldValues() ) {
                    if ( fieldExpectationFailed( verifyField ) ) {
                        failures++;
                    }
                    total++;
                }
            }
        }
        return new int[]{failures, total};
    }

    protected boolean fieldExpectationFailed(VerifyField verifyField) {
        return verifyField.getSuccessResult() != null && !verifyField.getSuccessResult();
    }

    protected boolean ruleFailedToFire(VerifyRuleFired verifyRuleFired) {
        return verifyRuleFired.getSuccessResult() != null && !verifyRuleFired.getSuccessResult();
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public int getMaxRuleFirings() {
        return maxRuleFirings;
    }

    public List<FactData> getGlobals() {
        return globals;
    }

    public void setLastRunResult(Date lastRunResult) {
        this.lastRunResult = lastRunResult;
    }

    public Date getLastRunResult() {
        return lastRunResult;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    public boolean isInclusive() {
        return inclusive;
    }

}
