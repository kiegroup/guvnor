package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraint.server.DefaultConstraintImpl;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.ValidationResult;

/**
 * 
 * @author esteban.aliverti@gmail.com
 */
public class NotNullConstraint extends DefaultConstraintImpl {
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
