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

package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;
import org.drools.factconstraints.server.DefaultFieldConstraintImpl;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class IntegerConstraint extends DefaultFieldConstraintImpl {
	private static final long serialVersionUID = 501l;
	public static final String NAME = "IntegerConstraint";
	
    public IntegerConstraint(){}

    @Override
    protected String internalVerifierRule(ConstraintConfiguration config,
    		Map<String, Object> context) {
        List<String> constraints = new ArrayList<String>();
        constraints.add("valueType != Field.INT");

        return this.createVerifierRuleTemplate(config, context, "Integer_Field_Constraint", constraints, "The value must be an integer"); //I18N
    }

    @Override
    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        ValidationResult result = new ValidationResult();

        if (value == null){
            result.setSuccess(false);
            result.setMessage("The value is null"); //TODO: I18N
        }else if (value instanceof Integer){
            result.setSuccess(true);
        }else if (value instanceof String){
            try{
                Integer.parseInt((String)value);
                result.setSuccess(true);
            } catch(NumberFormatException ex){
                result.setSuccess(false);
                result.setMessage(ex.getMessage()); //TODO: I18N
            }
        }else{
            result.setSuccess(false);
            result.setMessage("Invalid value type "+value.getClass().getName()); //TODO: I18N
        }

        return result;
    }
}
