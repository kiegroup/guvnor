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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class FieldPopulatorFactory {

    private final Object factObject;
    private final TypeResolver typeResolver;
    private final ClassLoader classLoader;

    public FieldPopulatorFactory(Object factObject,
                                 TypeResolver typeResolver,
                                 ClassLoader classLoader) {
        this.factObject = factObject;
        this.typeResolver = typeResolver;
        this.classLoader = classLoader;
    }

    public FieldPopulator getFieldPopulator(Field field) throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (field instanceof FieldData) {
            FieldData fieldData = (FieldData) field;
            if (fieldData.getValue() == null) {
                throw new IllegalArgumentException("Field value can not be null");
            } else {
                return getFieldDataPopulator(factObject,
                        fieldData);
            }
        } else if (field instanceof FactAssignmentField) {
            return new FactAssignmentFieldPopulator(factObject,
                    (FactAssignmentField) field,
                    typeResolver,
                    classLoader);
        }

        throw new IllegalArgumentException("Unknown field type " + field.getClass());
    }

    private FieldPopulator getFieldDataPopulator(Object factObject,
                                                 FieldData fieldData) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (fieldData.getNature() == FieldData.TYPE_COLLECTION) {
            return new CollectionFieldPopulator(factObject,
                    fieldData.getName(),
                    fieldData.getValue().substring(1));
        }else if (fieldData.getValue().startsWith("=")) {
            return new ExpressionFieldPopulator(factObject,
                    fieldData.getName(),
                    fieldData.getValue().substring(1));

        } else if (fieldData.getNature() == FieldData.TYPE_ENUM) {
            return new EnumFieldPopulator(factObject,
                    fieldData.getName(),
                    fieldData.getValue(),
                    typeResolver,
                    classLoader);
        } else if (isDate(fieldData.getName())) {
            return new DateFieldPopulator(
                    factObject,
                    getFieldType(fieldData.getName()),
                    fieldData.getName(),
                    fieldData.getValue());
        } else {
            return new SimpleFieldPopulator(factObject,
                    fieldData.getName(),
                    fieldData.getValue());
        }
    }

    private boolean isDate(String fieldName) {
        for (Method method : factObject.getClass().getDeclaredMethods()) {
            if (hasMutator(fieldName, method)) {
                if (java.util.Date.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    private Class<?> getFieldType(String fieldName) {
        for (Method method : factObject.getClass().getDeclaredMethods()) {
            if (hasMutator(fieldName, method)) {
                return method.getParameterTypes()[0];
            }
        }
        throw new IllegalArgumentException("No field named: " + fieldName);
    }

    private boolean hasMutator(String fieldName, Method method) {
        if (method.getName().equals(fieldName) || method.getName().equals("set" + capitalize(fieldName))) {
            if (method.getParameterTypes().length == 1) {
                return true;
            }
        }
        return false;
    }

    private String capitalize(String fieldName) {
        if (fieldName.length() == 0) {
            return "";
        } else if (fieldName.length() == 1) {
            return fieldName.toUpperCase();
        } else {
            String firstLetter = fieldName.substring(0, 1);

            String tail = fieldName.substring(1);
            return firstLetter.toUpperCase() + tail;
        }
    }

}
