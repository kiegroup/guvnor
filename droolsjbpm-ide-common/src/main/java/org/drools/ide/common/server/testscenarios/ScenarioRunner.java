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

import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.*;
import org.drools.ide.common.server.testscenarios.populators.FactPopulator;
import org.drools.ide.common.server.testscenarios.populators.FactPopulatorFactory;
import org.mvel2.MVEL;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.drools.lang.descr.ImportDescr;



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
     *
     * @param typeResolver A populated type resolved to be used to resolve the types in
     *                     the scenario.
     *                     <p/>
     *                     For info on how to invoke this, see
     *                     ContentPackageAssemblerTest.testPackageWithRuleflow in
     *                     guvnor-webapp This requires that the classloader for the
     *                     thread context be set appropriately. The PackageBuilder can
     *                     provide a suitable TypeResolver for a given package header,
     *                     and the Package config can provide a classloader.
     * @param classLoader  This is used by MVEL to instantiate classes in expressions, in
     *                     particular enum field values. See EnumFieldPopulator and
     *                     FactFieldValueVerifier
     * @param workingMemory
     * @param imports
     */
    public ScenarioRunner(final TypeResolver typeResolver,
                          final ClassLoader classLoader,
                          final InternalWorkingMemory workingMemory,
                          List<ImportDescr> imports) throws ClassNotFoundException {
     

       this(typeResolver,classLoader,workingMemory);

        for ( ImportDescr importDescr : imports ) {
            String className = importDescr.getTarget();
            try {
                addImport( className ,typeResolver);

            } catch ( WildCardException e ) {
                //Was already catched in the suggestion engine
            }
        }
        
    }
        
        
    public ScenarioRunner(final TypeResolver typeResolver,
                          final ClassLoader classLoader,
                          final InternalWorkingMemory workingMemory) throws ClassNotFoundException {
        Map<String, Object> populatedData = new HashMap<String, Object>();
        Map<String, Object> globalData = new HashMap<String, Object>();
        this.workingMemoryWrapper = new TestScenarioWorkingMemoryWrapper(workingMemory,
                typeResolver,
                classLoader,
                populatedData,
                globalData);
        this.factPopulatorFactory = new FactPopulatorFactory(populatedData,
                globalData,
                typeResolver,
                classLoader);
        this.factPopulator = new FactPopulator(workingMemory,
                populatedData);
    }

    public void run(Scenario scenario)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        scenario.setLastRunResult(new Date());

        populateGlobals(scenario.getGlobals());

        applyFixtures(scenario.getFixtures(),
                createScenarioSettings(scenario));
    }

    private ScenarioSettings createScenarioSettings(Scenario scenario) {
        ScenarioSettings scenarioSettings = new ScenarioSettings();
        scenarioSettings.setRuleList(scenario.getRules());
        scenarioSettings.setInclusive(scenario.isInclusive());
        scenarioSettings.setMaxRuleFirings(getMaxRuleFirings(scenario));
        return scenarioSettings;
    }

    private int getMaxRuleFirings(Scenario scenario) {
        String property = System.getProperty("guvnor.testscenario.maxrulefirings");
        if (property == null) {
            return scenario.getMaxRuleFirings();
        } else {
            return Integer.parseInt(property);
        }
    }

    private void applyFixtures(List<Fixture> fixtures,
                               ScenarioSettings scenarioSettings)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

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

                workingMemoryWrapper.executeSubScenario((ExecutionTrace) fixture,
                        scenarioSettings);

            } else if (fixture instanceof Expectation) {

                factPopulator.populate();

                workingMemoryWrapper.verifyExpectation((Expectation) fixture);
            } else {
                throw new IllegalArgumentException("Not sure what to do with " + fixture);
            }

        }

        factPopulator.populate();
    }

    private void populateGlobals(List<FactData> globals)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        for (final FactData fact : globals) {
            factPopulator.add(
                    factPopulatorFactory.createGlobalFactPopulator(fact));
        }

        factPopulator.populate();
    }
    private void addImport(String className, TypeResolver resolver) throws ScenarioRunner.WildCardException {
        if (isWildCardImport(className)) {
            throw new ScenarioRunner.WildCardException();
        } else {
            resolver.addImport(className);
        }
    }

    private boolean isWildCardImport(String className) {
        return className.endsWith("*");
    }

    @SuppressWarnings("serial")
    private static class WildCardException extends Exception {
    }
}
