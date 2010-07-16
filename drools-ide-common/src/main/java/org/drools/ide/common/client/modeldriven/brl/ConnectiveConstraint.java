/**
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

/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint extends BaseSingleFieldConstraint {

    public ConnectiveConstraint() {
    }

    public ConnectiveConstraint(final String fieldName,
    		                    final String fieldType,
    							final String opr,
                                final String val) {
    	this.fieldName = fieldName;
    	this.fieldType = fieldType;
        this.operator = opr;
        this.setValue(val);
    }

    public String operator;
    public String fieldName;
    public String fieldType;

}
