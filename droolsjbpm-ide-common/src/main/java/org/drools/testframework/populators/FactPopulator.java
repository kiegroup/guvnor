/*
 * Copyright 2011 JBoss Inc
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

package org.drools.testframework.populators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;

public class FactPopulator {

    private Map<String, Populator> toBePopulatedData = new HashMap<String, Populator>();

    private final Map<String, Object> populatedData;
    private final Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();

    private final InternalWorkingMemory workingMemory;

    public FactPopulator(InternalWorkingMemory workingMemory, Map<String, Object> populatedData) {
        this.workingMemory = workingMemory;
        this.populatedData = populatedData;
    }

    public void populate() throws ClassNotFoundException {
        List<FieldPopulator> fieldPopulators = new ArrayList<FieldPopulator>();

        for (Populator populator : toBePopulatedData.values()) {
            fieldPopulators.addAll(populator.populate(workingMemory, factHandles));
        }

        for (FieldPopulator fieldPopulator : fieldPopulators) {
            fieldPopulator.populate(populatedData);
        }

        toBePopulatedData.clear();
    }

    public void retractFact(String retractFactName) {
        this.workingMemory.retract(this.factHandles.get(retractFactName));
        this.populatedData.remove(retractFactName);
    }

    public void add(Populator factPopulator) {
        toBePopulatedData.put(factPopulator.getName(), factPopulator);
    }
}
