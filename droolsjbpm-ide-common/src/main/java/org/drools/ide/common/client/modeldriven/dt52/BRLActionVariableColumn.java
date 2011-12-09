/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.client.modeldriven.dt52;

/**
 * A column representing a single BRL fragment variable
 */
public class BRLActionVariableColumn extends ActionCol52 {

    private static final long serialVersionUID = 540l;

    private final String      varName;
    private final String      dataType;
    private final String      factType;
    private final String      factField;

    public BRLActionVariableColumn(String varName,
                             String dataType,
                             String factType,
                             String factField) {
        this.varName = varName;
        this.dataType = dataType;
        this.factType = factType;
        this.factField = factField;
    }

    public String getVarName() {
        return varName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getFactType() {
        return factType;
    }

    public String getFactField() {
        return factField;
    }

}
