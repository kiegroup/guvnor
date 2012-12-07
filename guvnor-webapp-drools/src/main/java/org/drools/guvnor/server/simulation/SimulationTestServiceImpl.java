/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.simulation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.fluent.session.StatefulKnowledgeSessionSimFluent;
import org.drools.fluent.simulation.SimulationFluent;
import org.drools.fluent.simulation.impl.DefaultSimulationFluent;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.server.RepositoryModuleOperations;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationTestService;
import org.drools.guvnor.shared.simulation.command.AbstractCommandModel;
import org.drools.guvnor.shared.simulation.command.AssertRuleFiredCommandModel;
import org.drools.guvnor.shared.simulation.command.FireAllRulesCommandModel;
import org.drools.guvnor.shared.simulation.command.InsertBulkDataCommandModel;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;

@ApplicationScoped
public class SimulationTestServiceImpl implements SimulationTestService {

    @Inject
    private RulesRepository rulesRepository;

    @Inject
    private ModuleService repositoryModuleService;
    
    @Inject
    private RepositoryModuleOperations repositoryModuleOperations;

    public void runSimulation(String moduleName, SimulationModel simulation) throws DetailedSerializationException {
        ModuleItem moduleItem = rulesRepository.loadModule(moduleName);
        if (!moduleItem.isBinaryUpToDate()) {
            repositoryModuleOperations.buildModuleWithoutErrors( moduleItem, false );
        }
//        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, classLoader));

        SimulationFluent simulationFluent = new DefaultSimulationFluent();
//        for (SimulationPathModel path : simulation.getPaths().values()) {
//            simulationFluent.newPath(path.getName());
//            simulationFluent.newKnowledgeBuilder() // TODO only do once, for the root
//                .add(ResourceFactory.newByteArrayResource(moduleItem.getCompiledBinaryBytes()), ResourceType.PKG)
//                .end();
//            simulationFluent.newKnowledgeBase()
//                    .addKnowledgePackages()
//                    .end(World.ROOT, KnowledgeBase.class.getName());
//            simulationFluent.newStatefulKnowledgeSession().end();
//            for (SimulationStepModel step : path.getSteps().values()) {
//                simulationFluent.newStep(step.getDistanceMillis());
//                StatefulKnowledgeSessionSimFluent session = simulationFluent.getStatefulKnowledgeSession();
//                for (AbstractCommandModel command : step.getCommands()) {
//                    addCommand(session, command);
//                }
//            }
//        }
        simulationFluent.runSimulation();
    }

    private void addCommand(StatefulKnowledgeSessionSimFluent session, AbstractCommandModel abstractCommand) {
        if (abstractCommand instanceof InsertBulkDataCommandModel) {
            InsertBulkDataCommandModel command = (InsertBulkDataCommandModel) abstractCommand;
            session.insert("Hello world"); // TODO
        } if (abstractCommand instanceof FireAllRulesCommandModel) {
            FireAllRulesCommandModel command = (FireAllRulesCommandModel) abstractCommand;
            session.fireAllRules();
            for (AssertRuleFiredCommandModel assertRuleFiredCommand
                    : command.getAssertRuleFiredCommands()) {
                session.assertRuleFired(assertRuleFiredCommand.getRuleName(),
                        assertRuleFiredCommand.getFireCount());
            }
        } else {
            throw new IllegalStateException("The AbstractCommandModel class ("
                    + abstractCommand.getClass() + ") is not implemented on the server side.");
        }
    }

}
