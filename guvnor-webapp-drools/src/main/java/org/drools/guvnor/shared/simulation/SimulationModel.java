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
import org.kie.simulation.SimulationPath;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a DTO.
 */
@XStreamAlias("SimulationModel")
public class SimulationModel implements PortableObject {

    public static SimulationModel createNew() {
        SimulationModel simulation = new SimulationModel();
        simulation.addPath(SimulationPathModel.createNew());
        todoCreateTestdata(simulation);
        return simulation;
    }

    // TODO remove me
    private static void todoCreateTestdata(SimulationModel simulation) {
        simulation.addPath(SimulationPathModel.createNew());
        simulation.addPath(SimulationPathModel.createNew());
    }

    private Map<String, SimulationPathModel> paths = new LinkedHashMap<String, SimulationPathModel>();

    public Map<String, SimulationPathModel> getPaths() {
        return paths;
    }

    public int getPathsSize() {
        return paths.size();
    }

    public void addPath(SimulationPathModel path) {
        if (path.getName() == null) {
            generatePathName(path);
        }
        if (paths.containsKey(path.getName())) {
            throw new IllegalArgumentException("The simulation path's name ("
                    + path.getName() + ") is not unique.");
        }
        paths.put(path.getName(), path);
    }
    public void removePath(SimulationPathModel path) {
//        if (path.getSimulation() != this) {
//            throw new IllegalArgumentException("The simulation path's simulation ("
//                    + path.getSimulation() + ") is not this simulation (" + this + ").");
//        }
        String name = path.getName();
        if (!paths.containsKey(name)) {
            throw new IllegalArgumentException("The simulation path's name ("
                    + name + ") is not known. It probably changed while it was in the Map.");
        }
        paths.remove(name);
    }

    private void generatePathName(SimulationPathModel path) {
        String pathName;
        int index = 0;
        do {
            pathName = "path_" + index;
            index++;
        } while (paths.containsKey(pathName));
        path.setName(pathName);
    }

    public long getMaximumDistanceMillis() {
        long maximumDistanceMillis = 0L;
        for (SimulationPathModel path : paths.values()) {
            long distanceMillis = path.getMaximumDistanceMillis();
            if (maximumDistanceMillis < distanceMillis) {
                maximumDistanceMillis = distanceMillis;
            }
        }
        return maximumDistanceMillis;
    }

}
