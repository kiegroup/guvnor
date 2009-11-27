package org.drools.guvnor.client.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.guvnor.client.modeldriven.testing.ExecutionTrace;
import org.drools.guvnor.client.modeldriven.testing.FactData;
import org.drools.guvnor.client.modeldriven.testing.FieldData;
import org.drools.guvnor.client.modeldriven.testing.Fixture;
import org.drools.guvnor.client.modeldriven.testing.RetractFact;
import org.drools.guvnor.client.modeldriven.testing.VerifyFact;
import org.drools.guvnor.client.modeldriven.testing.VerifyRuleFired;

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
    public List lumpyMap(List<Fixture> fixtures) {
        List output = new ArrayList();

        Map<String, List< ? extends Fixture>> dataInput = new HashMap<String, List< ? extends Fixture>>();
        List<VerifyFact> verifyFact = new ArrayList<VerifyFact>();
        List<VerifyRuleFired> verifyRule = new ArrayList<VerifyRuleFired>();
        List<RetractFact> retractFacts = new ArrayList<RetractFact>();

        for ( Iterator<Fixture> iterator = fixtures.iterator(); iterator.hasNext(); ) {
            Fixture f = iterator.next();
            if ( f instanceof FactData ) {
                accumulateData( dataInput,
                                f );
            } else if ( f instanceof ActivateRuleFlowGroup ) {
                accumulateData( dataInput,
                                f );
            } else if ( f instanceof RetractFact ) {
                retractFacts.add( (RetractFact) f );
            } else if ( f instanceof VerifyRuleFired ) {
                verifyRule.add( (VerifyRuleFired) f );
            } else if ( f instanceof VerifyFact ) {
                verifyFact.add( (VerifyFact) f );
            } else if ( f instanceof ExecutionTrace ) {
                gatherFixtures( output,
                                dataInput,
                                verifyFact,
                                verifyRule,
                                retractFacts,
                                false );

                output.add( f );

                verifyRule = new ArrayList<VerifyRuleFired>();
                verifyFact = new ArrayList<VerifyFact>();
                retractFacts = new ArrayList<RetractFact>();
                dataInput = new HashMap<String, List<? extends Fixture>>();
//                verifyRule.clear();
//                verifyFact.clear();
//                retractFacts.clear();
//                dataInput.clear();
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

    private void gatherFixtures(List output,
                                Map<String, List< ? extends Fixture>> dataInput,
                                List<VerifyFact> verifyFact,
                                List<VerifyRuleFired> verifyRule,
                                List<RetractFact> retractFacts,
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
    public Map lumpyMapGlobals(List globals) {
        Map g = new HashMap();
        for ( Iterator iterator = globals.iterator(); iterator.hasNext(); ) {
            FactData f = (FactData) iterator.next();
            accumulateData( g,
                            f );
        }
        return g;
    }

    private void accumulateData(Map dataInput,
                                Fixture f) {
        if ( f instanceof FactData ) {
            FactData fd = (FactData) f;
            if ( !dataInput.containsKey( fd.type ) ) {
                dataInput.put( fd.type,
                               new ArrayList() );
            }
            ((List) dataInput.get( fd.type )).add( fd );
        } else if ( f instanceof ActivateRuleFlowGroup ) {
            if ( !dataInput.containsKey( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP ) ) {
                dataInput.put( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP,
                               new ArrayList() );
            }
            ((List) dataInput.get( ScenarioHelper.ACTIVATE_RULE_FLOW_GROUP )).add( f );
        }
    }

    static void removeFields(List factData,
                             String field) {
        for ( Iterator iterator = factData.iterator(); iterator.hasNext(); ) {
            FactData fa = (FactData) iterator.next();
            for ( Iterator iterator2 = fa.fieldData.iterator(); iterator2.hasNext(); ) {
                FieldData fi = (FieldData) iterator2.next();
                if ( fi.name.equals( field ) ) {
                    iterator2.remove();
                }
            }
        }
    }

}
