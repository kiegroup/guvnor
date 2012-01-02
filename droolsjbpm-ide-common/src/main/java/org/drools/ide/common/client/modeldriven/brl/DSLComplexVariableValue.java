/*
 * Copyright 2011 JBoss by Red Hat.
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
package org.drools.ide.common.client.modeldriven.brl;

/**
 * This class represents the value of a complex variable inside a DSLSentence.
 * "complex variable" means that the variable has 2 values: id and the real value.
 */
public class DSLComplexVariableValue extends DSLVariableValue {
    private String id;

    public DSLComplexVariableValue() {
    }

    public DSLComplexVariableValue(String id, String value) {
        super(value);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
