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

package org.drools.ide.common.server.testscenarios.populators;

import org.drools.base.TypeResolver;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

class FieldPopulatorFactory {

    private final Object factObject;
    private final TypeResolver typeResolver;

    public FieldPopulatorFactory(Object factObject, TypeResolver typeResolver) {
        this.factObject = factObject;
        this.typeResolver = typeResolver;
    }

    public FieldPopulator getFieldPopulator(Field field) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (field instanceof FieldData) {
            FieldData fieldData = (FieldData) field;
            if (fieldData.getValue() != null) {

                return getFieldDataPopulator(factObject, fieldData);
            }
        } else if (field instanceof FactAssignmentField) {
            return new FactAssignmentFieldPopulator(
                    factObject,
                    (FactAssignmentField) field,
                    typeResolver);
        }

        throw new IllegalArgumentException("Unknown field type " + field.getClass());
    }

    private FieldPopulator getFieldDataPopulator(Object factObject, FieldData fieldData) {
        if (fieldData.getValue().startsWith("=")) {
            return new ExpressionFieldPopulator(
                    factObject,
                    fieldData.getName(),
                    fieldData.getValue().substring(1));

        } else if (fieldData.getNature() == FieldData.TYPE_ENUM) {
            return new EnumFieldPopulator(
                    factObject,
                    fieldData.getName(),
                    fieldData.getValue(),
                    typeResolver);
        } else {
            return new SimpleFieldPopulator(
                    factObject,
                    fieldData.getName(),
                    fieldData.getValue());
        }
    }

}
