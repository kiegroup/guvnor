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

import java.util.Map;

import org.drools.base.TypeResolver;

import static org.mvel2.MVEL.*;

public class EnumFieldPopulator extends FieldPopulator {

    private final String fieldValue;
    private final TypeResolver typeResolver;

    public EnumFieldPopulator(Object factObject, String fieldName, String fieldValue, TypeResolver typeResolver) {
        super(factObject, fieldName);
        this.typeResolver = typeResolver;
        this.fieldValue = fieldValue;
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        Object value;
        String valueOfEnum = "";
        if (fieldValue.indexOf(".") != -1) {
            String classNameOfEnum = fieldValue.substring(0,
                    fieldValue.lastIndexOf("."));
            valueOfEnum = fieldValue.substring(fieldValue.lastIndexOf(".") + 1);
            try {
                //This is a Java enum type if the type can be resolved by ClassTypeResolver
                //Revisit: Better way to determine java enum type or Guvnor enum type.
                String fullName = typeResolver.getFullTypeName(classNameOfEnum);
                if (fullName != null && !"".equals(fullName)) {
                    valueOfEnum = fullName + "." + valueOfEnum;
                }
                value = eval(valueOfEnum);
            } catch (ClassNotFoundException e) {
                // This is a Guvnor enum type
                String fullName = classNameOfEnum;
                if (fullName != null && !"".equals(fullName)) {
                    valueOfEnum = fullName + "." + valueOfEnum;
                }
                value = valueOfEnum;
            }
        } else {
            value = this.fieldValue;
        }

        populateField(value, populatedData);
    }
}
