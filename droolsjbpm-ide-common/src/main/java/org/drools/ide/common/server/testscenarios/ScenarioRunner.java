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

package org.drools.ide.common.server.testscenarios;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ActivateRuleFlowGroup;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Expectation;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.server.testscenarios.populators.FactPopulator;
import org.drools.ide.common.server.testscenarios.populators.FactPopulatorFactory;
import org.mvel2.MVEL;

/**
 * This actually runs the test scenarios.
 */
public class ScenarioRunner {

    private final TestScenarioWorkingMemoryWrapper workingMemoryWrapper;
    private final FactPopulatorFactory factPopulatorFactory;
    private final FactPopulator factPopulator;

    /**
     * This constructor is normally used by Guvnor for running tests on a users
     * request.
     * @param typeResolver A populated type resolved to be used to resolve the types in
     * the scenario.
     * <p/>
     * For info on how to invoke this, see
     * ContentPackageAssemblerTest.testPackageWithRuleflow in
     * guvnor-webapp This requires that the classloader for the
     * thread context be set appropriately. The PackageBuilder can
     * provide a suitable TypeResolver for a given package header,
     * and the Package config can provide a classloader.
     */
    public ScenarioRunner(
            final TypeResolver typeResolver,
            final InternalWorkingMemory workingMemory) throws ClassNotFoundException {

        Map<String, Object> populatedData = new HashMap<String, Object>();
        Map<String, Object> globalData = new HashMap<String, Object>();

        this.workingMemoryWrapper = new TestScenarioWorkingMemoryWrapper(workingMemory, typeResolver, populatedData, globalData);
        this.factPopulatorFactory = new FactPopulatorFactory(populatedData, globalData, typeResolver);
        factPopulator = new FactPopulator(workingMemory, populatedData);
    }

    public void run(Scenario scenario) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        scenario.setLastRunResult(new Date());

        populateGlobals(scenario.getGlobals());

        applyFixtures(scenario.getFixtures(), createScenarioSettings(scenario));
    }

    private ScenarioSettings createScenarioSettings(Scenario scenario) {
        ScenarioSettings scenarioSettings = new ScenarioSettings();
        scenarioSettings.setRuleList(scenario.getRules());
        scenarioSettings.setInclusive(scenario.isInclusive());
        scenarioSettings.setMaxRuleFirings(scenario.getMaxRuleFirings());
        return scenarioSettings;
    }

    private void applyFixtures(List<Fixture> fixtures, ScenarioSettings scenarioSettings) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        for (Iterator<Fixture> iterator = fixtures.iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            if (fixture instanceof FactData) {

                factPopulator.add(factPopulatorFactory.createFactPopulator((FactData) fixture));

            } else if (fixture instanceof RetractFact) {

                factPopulator.retractFact(((RetractFact) fixture).getName());

            } else if (fixture instanceof CallMethod) {

                workingMemoryWrapper.executeMethod((CallMethod) fixture);

            } else if (fixture instanceof ActivateRuleFlowGroup) {

                workingMemoryWrapper.activateRuleFlowGroup(((ActivateRuleFlowGroup) fixture).getName());

            } else if (fixture instanceof ExecutionTrace) {

                factPopulator.populate();

                workingMemoryWrapper.executeSubScenario((ExecutionTrace) fixture, scenarioSettings);

            } else if (fixture instanceof Expectation) {

                factPopulator.populate();

                workingMemoryWrapper.verifyExpectation((Expectation) fixture);
            } else {
                throw new IllegalArgumentException("Not sure what to do with " + fixture);
            }

        }

        factPopulator.populate();
    }

    private void populateGlobals(List<FactData> globals) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        for (final FactData fact : globals) {
            factPopulator.add(
                    factPopulatorFactory.createGlobalFactPopulator(fact));
        }

        factPopulator.populate();
    }
}
