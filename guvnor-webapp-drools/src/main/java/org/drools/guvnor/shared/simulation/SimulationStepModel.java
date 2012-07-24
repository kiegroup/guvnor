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

package org.drools.guvnor.shared.simulation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.guvnor.shared.api.PortableObject;
import org.drools.guvnor.shared.simulation.command.AbstractCommandModel;
import org.drools.guvnor.shared.simulation.command.AssertRuleFiredCommandModel;
import org.drools.guvnor.shared.simulation.command.FireAllRulesCommandModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a DTO.
 */
@XStreamAlias("SimulationStepModel")
public class SimulationStepModel implements PortableObject {

    public static SimulationStepModel createNew() {
        SimulationStepModel step = new SimulationStepModel();
        FireAllRulesCommandModel fireAllRulesCommand = new FireAllRulesCommandModel();
        AssertRuleFiredCommandModel assertRuleFiredCommand = new AssertRuleFiredCommandModel();
        assertRuleFiredCommand.setRuleName("myFirstRule");
        assertRuleFiredCommand.setFireCount(1);
        fireAllRulesCommand.getAssertRuleFiredCommands().add(assertRuleFiredCommand);
        step.getCommands().add(fireAllRulesCommand);
        return step;
    }

    private Long distanceMillis; // Distance to start of simulation
    private List<AbstractCommandModel> commands = new ArrayList<AbstractCommandModel>();

    public Long getDistanceMillis() {
        return distanceMillis;
    }

    public void setDistanceMillis(Long distanceMillis) {
        this.distanceMillis = distanceMillis;
    }

    public List<AbstractCommandModel> getCommands() {
        return commands;
    }

}
