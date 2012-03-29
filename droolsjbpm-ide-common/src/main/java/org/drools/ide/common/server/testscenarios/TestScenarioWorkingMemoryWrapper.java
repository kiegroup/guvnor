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

package org.drools.ide.common.server.testscenarios;

import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.*;
import org.drools.ide.common.server.testscenarios.executors.MethodExecutor;
import org.drools.ide.common.server.testscenarios.verifiers.FactVerifier;
import org.drools.ide.common.server.testscenarios.verifiers.RuleFiredVerifier;
import org.drools.time.impl.PseudoClockScheduler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestScenarioWorkingMemoryWrapper {

    private final InternalWorkingMemory workingMemory;
    private final FactVerifier factVerifier;
    private final RuleFiredVerifier ruleFiredVerifier = new RuleFiredVerifier();

    private TestingEventListener eventListener = null;
    private final MethodExecutor methodExecutor;
    private final Map<String, Object> populatedData;

    private final ClassLoader classLoader;
    
    public TestScenarioWorkingMemoryWrapper(
            InternalWorkingMemory workingMemory,
            final TypeResolver resolver,
            final ClassLoader classLoader,
            Map<String, Object> populatedData,
            Map<String, Object> globalData) {
        this.workingMemory = workingMemory;
        this.populatedData = populatedData;
        this.methodExecutor = new MethodExecutor(populatedData);
        this.classLoader = classLoader;

        factVerifier = initFactVerifier(resolver, globalData);
    }

    private FactVerifier initFactVerifier(TypeResolver resolver, Map<String, Object> globalData) {
        return new FactVerifier(
                populatedData,
                resolver,
                classLoader,
                workingMemory,
                globalData);
    }

    public void activateRuleFlowGroup(String activateRuleFlowGroupName) {
        workingMemory.getAgenda().getRuleFlowGroup(activateRuleFlowGroupName).setAutoDeactivate(false);
        workingMemory.getAgenda().activateRuleFlowGroup(activateRuleFlowGroupName);
    }

    public void verifyExpectation(Expectation expectation) {
        if (expectation instanceof VerifyFact) {
            factVerifier.verify((VerifyFact) expectation);
        } else if (expectation instanceof VerifyRuleFired) {
            ruleFiredVerifier.verifyFiringCounts((VerifyRuleFired) expectation);
        }
    }

    public void executeMethod(CallMethod callMethod) {
        methodExecutor.executeMethod(callMethod);
    }

    private void fireAllRules(ScenarioSettings scenarioSettings) {
        this.workingMemory.fireAllRules(
                eventListener.getAgendaFilter(
                        scenarioSettings.getRuleList(),
                        scenarioSettings.isInclusive()),
                scenarioSettings.getMaxRuleFirings());
    }

    private void resetEventListener() {
        if (eventListener != null) {
            this.workingMemory.removeEventListener(eventListener); //remove the old
        }
        eventListener = new TestingEventListener();
        this.workingMemory.addEventListener(eventListener);
        this.ruleFiredVerifier.setFireCounter(eventListener.getFiringCounts());
    }

    public void executeSubScenario(ExecutionTrace executionTrace, ScenarioSettings scenarioSettings) {

        resetEventListener();

        //set up the time machine
        applyTimeMachine(
                executionTrace);

        long startTime = System.currentTimeMillis();

        fireAllRules(scenarioSettings);

        executionTrace.setExecutionTimeResult(System.currentTimeMillis() - startTime);
        executionTrace.setNumberOfRulesFired(eventListener.totalFires);
        executionTrace.setRulesFired(eventListener.getRulesFiredSummary());
    }

    private void applyTimeMachine(ExecutionTrace executionTrace) {
        ((PseudoClockScheduler) workingMemory.getSessionClock()).advanceTime(
                getTargetTime(executionTrace) - getCurrentTime(),
                TimeUnit.MILLISECONDS);
    }

    private long getTargetTime(ExecutionTrace executionTrace) {
        if (executionTrace.getScenarioSimulatedDate() != null) {
            return executionTrace.getScenarioSimulatedDate().getTime();
        } else {
            return new Date().getTime();
        }
    }

    private long getCurrentTime() {
        return workingMemory.getSessionClock().getCurrentTime();
    }
}
