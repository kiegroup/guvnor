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

package org.drools.ide.common.server.testscenarios.populators;

import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class FactPopulatorBase implements Populator {

    protected final Map<String, Object> populatedData;
    protected final TypeResolver typeResolver;
    protected final FactData fact;

    public FactPopulatorBase(
            Map<String, Object> populatedData,
            TypeResolver typeResolver,
            FactData fact) throws ClassNotFoundException {
        this.populatedData = populatedData;
        this.typeResolver = typeResolver;
        this.fact = fact;
    }

    public String getName() {
        return fact.getName();
    }

    protected List<FieldPopulator> getFieldPopulators(Object factObject) {

        List<FieldPopulator> fieldPopulators = new ArrayList<FieldPopulator>();

        for (int i = 0; i < fact.getFieldData().size(); i++) {
            Field field = fact.getFieldData().get(i);

            if (field instanceof FieldData) {
                FieldData fieldData=(FieldData)field;
                if (fieldData.getValue() != null) {
                    if (fieldData.getValue().startsWith("=")) {
                        fieldPopulators.add(
                                new ExpressionFieldPopulator(
                                        factObject,
                                        fieldData.getName(),
                                        fieldData.getValue().substring(1)));

                    } else if (fieldData.getNature() == FieldData.TYPE_ENUM) {
                        fieldPopulators.add(
                                new EnumFieldPopulator(
                                        factObject,
                                        fieldData.getName(),
                                        fieldData.getValue(),
                                        typeResolver));
                    } else {
                        fieldPopulators.add(
                                new SimpleFieldPopulator(
                                        factObject,
                                        fieldData.getName(),
                                        fieldData.getValue()));
                    }
                }
            }
        }
        return fieldPopulators;
    }

    protected String getTypeName(
            TypeResolver resolver,
            FactData fact) throws ClassNotFoundException {

        String fullName = resolver.getFullTypeName(fact.getType());
        if (fullName.equals("java.util.List") || fullName.equals("java.util.Collection")) {
            return "java.util.ArrayList";
        } else {
            return fullName;
        }
    }
}
