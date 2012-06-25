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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Expectation;
import org.drools.ide.common.client.modeldriven.testing.Fact;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.FieldPlaceHolder;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.RetractFact;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.drools.ide.common.server.util.FieldConverter;


/**
 * Persists the scenario model.
 */
public class SimulationTestXMLPersistence {

    private static final SimulationTestXMLPersistence INSTANCE = new SimulationTestXMLPersistence();

    public static SimulationTestXMLPersistence getInstance() {
        return INSTANCE;
    }

    private XStream xStream;

    private SimulationTestXMLPersistence() {
        xStream = new XStream(new DomDriver());
        xStream.processAnnotations(SimulationModel.class);
    }

    public String marshal(SimulationModel simulationModel) {
        return xStream.toXML(simulationModel);
    }

    public SimulationModel unmarshal(String xml) {
        if (StringUtils.isEmpty(xml)) {
            return new SimulationModel();
        }
        return (SimulationModel) xStream.fromXML(xml);
    }

}
