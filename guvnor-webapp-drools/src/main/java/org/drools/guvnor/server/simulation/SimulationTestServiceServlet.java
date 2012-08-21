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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationTestService;
import org.jboss.solder.core.Veto;

import javax.inject.Inject;

/**
 * Boilerplate Servlet needed for GWT.
 */
@Veto
public class SimulationTestServiceServlet extends RemoteServiceServlet implements SimulationTestService {

    @Inject
    private SimulationTestService simulationTestService;

    public void runSimulation(String moduleName, SimulationModel simulation) throws DetailedSerializationException {
        simulationTestService.runSimulation(moduleName, simulation);
    }

}
