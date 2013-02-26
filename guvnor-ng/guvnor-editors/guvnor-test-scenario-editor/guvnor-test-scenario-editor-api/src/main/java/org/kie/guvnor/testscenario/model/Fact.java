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

package org.kie.guvnor.testscenario.model;

import java.util.ArrayList;
import java.util.List;

public class Fact {

    private static final long serialVersionUID = 510l;

    /**
     * The type (class)
     */
    private String type;

    private List<Field> fieldData = new ArrayList<Field>();

    public Fact() {
    }

    public Fact(String type,
                List<Field> fieldData) {
        this(type);
        this.setFieldData(fieldData);
    }

    public Fact(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setFieldData(List<Field> fieldData) {
        this.fieldData = fieldData;
    }

    public List<Field> getFieldData() {
        return fieldData;
    }

    public void removeField(String fieldName) {
        for (Field field : fieldData) {
            if (field.getName().equals(fieldName)) {
                fieldData.remove(field);
                break;
            }
        }
    }

    public boolean isFieldNameInUse(String fieldName) {
        for (Field field : fieldData) {
            if (fieldName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }
}
