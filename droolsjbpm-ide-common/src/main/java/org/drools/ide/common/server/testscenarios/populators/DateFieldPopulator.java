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

import org.drools.core.util.DateUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

public class DateFieldPopulator extends FieldPopulator {

    private final Date value;

    public DateFieldPopulator(Object factObject,
                              Class<?> fieldClass,
                              String fieldName,
                              String value) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        super(factObject, fieldName);
        this.value = createTimeObject(fieldClass, value);
    }

    private Date createTimeObject(Class<?> fieldClass,
                                  String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class parameterTypes[] = new Class[1];
        parameterTypes[0] = Long.TYPE;
        Constructor constructor
                = fieldClass.getConstructor(parameterTypes);
        Object args[] = new Object[1];
        args[0] = getTimeAsLong(value);
        return (Date) constructor.newInstance(args);
    }

    private long getTimeAsLong(String value) {
        return DateUtils.parseDate(value, null).getTime();
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        populateField(value,
                populatedData);
    }
}
