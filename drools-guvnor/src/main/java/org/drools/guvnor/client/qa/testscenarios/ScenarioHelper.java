/**
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

package org.drools.guvnor.client.qa.testscenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

/**
 * Some utility methods as the display logic is a bit hairy.
 */
public class ScenarioHelper {

    static final String RETRACT_KEY              = "retract";
    static final String ACTIVATE_RULE_FLOW_GROUP = "activate_rule_flow_group";

    /**
     * Called lumpy map - as this takes a flat list of fixtures, and groups things together.
     * It will return a list - of which each element will either be a list - or a map.
     * If its a map - then its a map of FactData to the fact type. If its a list, then it will be
     * expectations or retractions.
     *
     * Man, this will be so much nicer with generics.
     * @return List<List<VeryifyRuleFired or VerifyFact or RetractFact> OR Map<String, List<FactData>> OR ExecutionTrace>
     */
    public List<Fixture> lumpyMap(List<Fixture> fixtures) {
        List<Fixture> output = new ArrayList<Fixture>();

        FixturesMap dataInput = new FixturesMap();
        FixtureList verifyFact = new FixtureList();
        FixtureList verifyRule = new FixtureList();
        FixtureList retractFacts = new FixtureList();

        for ( Iterator<Fixture> iterator = fixtures.iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();
            if ( fixture instanceof FactData ) {
                accumulateData( dataInput,
                                fixture );
            } else if ( fixture instanceof ActivateRuleFlowGroup ) {
                accumulateData( dataInput,
                                fixture );
            } else if ( fixture instanceof RetractFact ) {
                retractFacts.add( (RetractFact) fixture );
            } else if ( fixture instanceof VerifyRuleFired ) {
                verifyRule.add( (VerifyRuleFired) fixture );
            } else if ( fixture instanceof VerifyFact ) {
                verifyFact.add( (VerifyFact) fixture );
            } else if ( fixture instanceof ExecutionTrace ) {
                gatherFixtures( output,
                                dataInput,
                                verifyFact,
                                verifyRule,
                                retractFacts,
                                false );

                output.add( fixture );

                verifyRule = new FixtureList();
                verifyFact = new FixtureList();
                retractFacts = new FixtureList();
                dataInput = new FixturesMap();
            }
        }
        gatherFixtures( output,
                        dataInput,
                        verifyFact,
                        verifyRule,
                        retractFacts,
                        true );

        return output;
    }

    private void gatherFixtures(List<Fixture> output,
                                FixturesMap dataInput,
                                FixtureList verifyFact,
                                FixtureList verifyRule,
                                FixtureList retractFacts,
                                boolean end) {
        if ( verifyRule.size() > 0 ) output.add( verifyRule );
        if ( verifyFact.size() > 0 ) output.add( verifyFact );
        if ( retractFacts.size() > 0 ) dataInput.put( RETRACT_KEY,
                                                      retractFacts );
        if ( dataInput.size() > 0 || !end ) output.add( dataInput ); //want to have a place holder for the GUI
    }

    /**
     * Group the globals together by fact type.
     */
    public Map<String, FixtureList> lumpyMapGlobals(List<FactData> globals) {
        Map<String, FixtureList> map = new HashMap<String, FixtureList>();
        for ( FactData factData : globals ) {
            accumulateData( map,
                            factData );
        }
        return map;
    }

    private void accumulateData(Map<String, FixtureList> dataInput,
                                Fixture f) {
        if ( f instanceof FactData ) {
            FactData fd = (FactData) f;
            if ( !dataInput.containsKey( fd.type ) ) {
                dataInput.put( fd.type,
                               new FixtureList() );
            }
            ((FixtureList) dataInput.get( fd.type )).add( fd );
        } else if ( f instanceof ActivateRuleFlowGroup ) {
            if ( !dataInput.containsKey( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP ) ) {
                dataInput.put( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP,
                               new FixtureList() );
            }
            ((FixtureList) dataInput.get( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP )).add( f );
        }
    }

    static void removeFields(List<Fixture> factDatas,
                             String field) {
        for ( Fixture fixture : factDatas ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                for ( Iterator<FieldData> fieldDataIterator = factData.fieldData.iterator(); fieldDataIterator.hasNext(); ) {
                    FieldData fieldData = fieldDataIterator.next();
                    if ( fieldData.name.equals( field ) ) {
                        fieldDataIterator.remove();
                    }
                }
            }
        }
    }

}
