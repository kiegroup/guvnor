package org.drools.factconstraints.server.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.server.DefaultConstraintImpl;

public class NotMatchesConstraint extends DefaultConstraintImpl {

	public static final String NOT_MATCHES_ARGUMENT = "matches";
	private static final long serialVersionUID = 501L;
	public static final String NAME = "NotMatches";

	@Override
	protected String internalVerifierRule(ConstraintConfiguration config, Map<String, Object> context) {
		List<String> constraints = new ArrayList<String>();
		constraints.add("valueAsString not matches \"" + config.getArgumentValue(NOT_MATCHES_ARGUMENT) + "\"");

		return this.createVerifierRuleTemplate(config, context, 
				"Matches_Field_Constraint", constraints,
				"The value must not match: " + config.getArgumentValue(NOT_MATCHES_ARGUMENT)); // I18N
	}
	
//	@Override
//	public ValidationResult validate(Object value, ConstraintConfiguration config) {
//		Pattern p = Pattern.compile((String) config.getArgumentValue(NOT_MATCHES_ARGUMENT));
//		
//		return p.matcher(value.toString()).matches();
//	}

}
