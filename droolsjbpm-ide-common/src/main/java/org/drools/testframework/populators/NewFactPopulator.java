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

import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.ide.common.client.modeldriven.testing.FactData;

import static org.mvel2.MVEL.*;

class NewFactPopulator extends FactPopulatorBase {

    public NewFactPopulator(
            Map<String, Object> populatedData,
            TypeResolver typeResolver,
            FactData fact) throws ClassNotFoundException {
        super(populatedData,
                typeResolver,
                fact);
    }

    protected Object resolveFactObject() throws ClassNotFoundException {
        Object factObject = eval("new " + getTypeName(typeResolver, fact) + "()");
        populatedData.put(
                fact.getName(),
                factObject);
        return factObject;
    }

    public List<FieldPopulator> populate(InternalWorkingMemory workingMemory, Map<String, FactHandle> factHandles) throws ClassNotFoundException {
        Object factObject = resolveFactObject();

        factHandles.put(
                fact.getName(),
                workingMemory.insert(factObject));

        return getFieldPopulators(factObject);
    }

}
