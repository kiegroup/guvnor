/*
 * Copyright 2010 JBoss Inc
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

import java.util.HashMap;
import java.util.Map;

/**
 * This is for a connective constraint that adds more options to a field
 * constraint.
 */
public class ConnectiveConstraint extends BaseSingleFieldConstraint
    implements
    HasOperatorParameters {

    private String              operator;
    private String              fieldName;
    private String              fieldType;

    private Map<String, String> parameters;

    public ConnectiveConstraint() {
    }

    public ConnectiveConstraint(final String fieldName,
                                final String fieldType,
                                final String opr,
                                final String val) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.operator = opr;
        this.setValue( val );
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void clearParameters() {
        this.parameters.clear();
    }

    public String getParameter(String key) {
        if ( parameters == null ) {
            parameters = new HashMap<String, String>();
        }
        String parameter = parameters.get( key );
        return parameter;
    }

    public void setParameter(String key,
                             String parameter) {
        if ( parameters == null ) {
            parameters = new HashMap<String, String>();
        }
        parameters.put( key,
                        parameter );
    }

    public void deleteParameter(String key) {
        parameters.remove( key );
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

}
