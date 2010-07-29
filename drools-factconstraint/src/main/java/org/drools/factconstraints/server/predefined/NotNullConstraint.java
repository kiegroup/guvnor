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
public class NotNullConstraint extends DefaultFieldConstraintImpl {
	private static final long serialVersionUID = 501L;
	public static final String NAME = "NotNull";

	public NotNullConstraint() {
	}

	@Override
	protected String internalVerifierRule(ConstraintConfiguration config,
			Map<String, Object> context) {
		List<String> constraints = new ArrayList<String>();
		constraints.add("valueType == Field.UNKNOWN");

		return this.createVerifierRuleTemplate(config, context, 
				"Not_null_Field_Constraint", constraints,
				"The value could not be null"); // I18N
	}

	@Override
	public ValidationResult validate(Object value,
			ConstraintConfiguration config) {
		ValidationResult result = new ValidationResult();

		if (value == null) {
			result.setSuccess(false);
			result.setMessage("The value could not be null"); // TODO: I18N
		} else {
			result.setSuccess(true);
		}

		return result;
	}

}
